const { test } = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const ts = require("typescript");

const plain = (v) => JSON.parse(JSON.stringify(v));
const transpile = (file) => ts.transpileModule(fs.readFileSync(file, "utf8"), {
  compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022 }
}).outputText;

class ApiError extends Error { constructor(message, status, body) { super(message); this.status = status; this.body = body; } }

/** 실제 API 모듈에 가짜 request 만 넣는다. */
function api(request) {
  const module = { exports: {} };
  vm.runInNewContext(transpile(path.resolve(__dirname, "../src/api/dreamDiaryApi.ts")), {
    module, exports: module.exports, Error, Number, JSON, console: { info() {}, warn() {}, error() {} },
    require(name) {
      if (name === "./client") return { ApiError, request };
      if (name === "../utils/date") return { normalizeDateStr: (v) => v };
      if (name === "../utils/keepFormData") return { toKeepFormData() { throw new Error("unused"); }, toKeepEditFormData() { throw new Error("unused"); } };
      if (name === "../utils/keepTags") return { normalizeKeepTagCategoryMap: (r) => r ?? {} };
      throw new Error("Unexpected API dependency: " + name);
    }
  });
  return module.exports;
}

function manage() {
  const module = { exports: {} };
  vm.runInNewContext(transpile(path.resolve(__dirname, "../src/utils/keepManage.ts")), {
    module, exports: module.exports, Promise, Error,
    require(name) {
      if (name === "../types/prospectiveKeep") return {
        keepProjectLifecycleKeyOf: (p) => p.lifecycle?.lifecycleKey ?? "OPEN",
        keepProjectTitle: (p) => (p.prefix?.name ? "[" + p.prefix.name + "]" : "") + (p.title ?? "")
      };
      throw new Error("Unexpected module: " + name);
    }
  });
  return module.exports;
}

test("삭제는 웹과 같은 DELETE /api/prospective/keep/{id} 를 본문 없이 한 번 보낸다", async () => {
  const calls = [];
  await api(async (url, init) => { calls.push([url, plain(init)]); return { rslt: true }; }).deleteKeep(42);
  assert.deepEqual(calls, [["/api/prospective/keep/42", { method: "DELETE" }]]);
});

test("삭제 rslt=false 는 서버 메시지로 실패, 잘못된 ID 는 요청하지 않는다", async () => {
  let calls = 0;
  const a = api(async () => { calls++; return { rslt: false, message: " 권한이 없습니다. " }; });
  await assert.rejects(a.deleteKeep(42), (e) => e.message === "권한이 없습니다." && e.status === 200);
  await assert.rejects(a.deleteKeep(0));
  assert.equal(calls, 1);
});

test("이동은 웹과 같은 PATCH /api/prospective/keep/{id}/project + JSON { projectId } (미지정은 null)", async () => {
  const calls = [];
  const a = api(async (url, init) => { calls.push([url, init.method, JSON.parse(init.body)]); return { rslt: true }; });
  await a.moveKeepProject(42, 7);
  await a.moveKeepProject(42, null);
  assert.deepEqual(plain(calls), [
    ["/api/prospective/keep/42/project", "PATCH", { projectId: 7 }],
    ["/api/prospective/keep/42/project", "PATCH", { projectId: null }]
  ]);
  const failing = api(async () => ({ rslt: false, message: "없는 Project" }));
  await assert.rejects(failing.moveKeepProject(42, 7), (e) => e.message === "없는 Project");
  await assert.rejects(a.moveKeepProject(42, -1));
  assert.equal(calls.length, 2);
});

test("삭제 확인: 제목을 밝힌 파괴적 대화상자, 삭제만 true 이고 한 번만 결정한다", async () => {
  const m = manage();
  let shown;
  const answer = m.confirmKeepDelete({ title: " 샘플 " }, (title, message, buttons, options) => { shown = { title, message, buttons, options }; });
  assert.equal(shown.title, "Keep 삭제");
  assert.match(shown.message, /'샘플'을\(를\) 삭제할까요\?/);
  assert.match(shown.message, /되돌릴 수 없습니다/);
  assert.deepEqual(plain(shown.buttons.map((b) => [b.text, b.style])), [["취소", "cancel"], ["삭제", "destructive"]]);
  shown.buttons[1].onPress();
  shown.buttons[0].onPress();
  shown.options.onDismiss();
  assert.equal(await answer, true);
  const cancel = m.confirmKeepDelete({ title: null }, (t, msg, buttons, options) => { shown = { msg, buttons, options }; });
  assert.match(shown.msg, /\(제목 없음\)/);
  shown.options.onDismiss();
  assert.equal(await cancel, false);
});

test("이동 선택지: 미지정 + Project, 현재 표시, OPEN 아닌 Project 는 고를 수 없고 현재·비활성은 보내지 않는다", () => {
  const m = manage();
  const projects = [{ id: 1, title: "가", prefix: { name: "게임" } }, { id: 2, title: "나", lifecycle: { lifecycleKey: "RESOLVED" } }];
  const options = plain(m.keepMoveOptions(projects, 1));
  assert.deepEqual(options, [
    { id: null, label: "미지정", current: false, disabled: false },
    { id: 1, label: "[게임]가", current: true, disabled: false },
    { id: 2, label: "나", current: false, disabled: true }
  ]);
  assert.equal(m.keepMoveTarget(options[0]), null);
  assert.equal(m.keepMoveTarget(options[1]), undefined);
  assert.equal(m.keepMoveTarget(options[2]), undefined);
  assert.equal(plain(m.keepMoveOptions([], null))[0].current, true);
  assert.deepEqual(plain(m.keepMoveOptions([], 9)).map((o) => [o.id, o.current, o.disabled]), [[null, false, false], [9, true, true]]);
  assert.equal(m.keepMoveNotice({ id: null, label: "미지정" }), "Project 미지정으로 옮겼습니다.");
  assert.equal(m.keepMoveNotice({ id: 1, label: "[게임]가" }), "'[게임]가'(으)로 옮겼습니다.");
});

test("실패 메시지: 서버 응답은 그대로, 응답 없음은 목록 확인 안내", () => {
  const m = manage();
  assert.equal(m.keepManageFailureMessage(new ApiError("서버 거절", 400), "x"), "서버 거절");
  assert.match(m.keepManageFailureMessage(new Error("Network request failed"), "x"), /결과를 목록에서 확인하세요/);
});

test("수정은 목록·상세, Project 이동·삭제는 상세에서 제공", () => {
  const src = (f) => fs.readFileSync(path.resolve(__dirname, "../src", f), "utf8");
  const home = src("screens/KeepHomeScreen.tsx");
  assert.doesNotMatch(home, /deleteKeep|onDelete|confirmKeepDelete/);
  assert.match(home, /onEdit=\{/);
  assert.doesNotMatch(home, /onMove=\{|moveToProject|moveKeepProject/);
  assert.match(home, /navigation.navigate\("KeepDetail", \{ id, editField: "title"/);
  const menu = src("components/keep/KeepLifecycleMenu.tsx");
  assert.doesNotMatch(menu, /onDelete|Keep 삭제/);
  assert.match(menu, /accessibilityLabel="Keep 수정"/);
  assert.doesNotMatch(menu, /onMove|accessibilityLabel="Project 이동"/);
  assert.doesNotMatch(home, /<KeepProjectMoveSheet/);
  const detail = src("screens/KeepDetailScreen.tsx");
  assert.match(detail, /accessibilityLabel="Keep 삭제"/);
  assert.match(detail, /KeepInlineEditor/);
  assert.doesNotMatch(detail, /moveKeepProject|KeepProjectMoveSheet/);
  assert.match(detail, /navigation\.goBack\(\)/);
  assert.match(detail, /manageBusyRef\.current/);
  assert.match(detail, /useKeepEdit/);
});

test("종결·최근 완료 행도 진행 행과 같은 중요 별·요구 배지를 표시한다", () => {
  const home = fs.readFileSync(path.join(__dirname, "../src/screens/KeepHomeScreen.tsx"), "utf8");
  assert.match(home, /const showBadges = lifecycleKey === "PENDING" \|\| terminal \|\| demanded;/);
  assert.match(home, /\{important \? <Text style=\{styles\.star\}>★ <\/Text> : null\}/);
  assert.match(home, /important && styles\.titleImportant, terminal && styles\.titleTerminal/);
});

test("Project 칩 순서는 미지정 → 전체 → Project, 기본 선택은 웹과 같은 미지정", () => {
  const home = fs.readFileSync(path.resolve(__dirname, "../src/screens/KeepHomeScreen.tsx"), "utf8");
  const none = home.indexOf('{ key: "none", label: "미지정", scope: "UNASSIGNED" }');
  const all = home.indexOf('{ key: "all", label: "전체", scope: "ALL" }');
  assert.ok(none > 0 && all > none, "미지정 칩이 전체 칩보다 앞");
  const projection = fs.readFileSync(path.resolve(__dirname, "../src/utils/keepProjection.ts"), "utf8");
  assert.match(projection, /EMPTY_KEEP_FILTERS[^;]*scope: "UNASSIGNED"/s);
  // 고른 Project 가 OPEN 이 아니게 되면 웹처럼 미지정으로 돌아간다
  assert.match(home, /setFilters\(\(prev\) => \(\{ \.\.\.prev, scope: "UNASSIGNED" \}\)\)/);
  // 이동 선택지도 미지정이 첫 줄
  assert.equal(manage().keepMoveOptions([{ id: 1, title: "가" }], 1)[0].label, "미지정");
});
