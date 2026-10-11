const { test } = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const ts = require("typescript");
const plain = (v) => JSON.parse(JSON.stringify(v));
const deq = (a, b) => assert.deepEqual(plain(a), plain(b));

const FIXTURE_ID = 301;
const FIXTURE_CONTENT = "샘플 본문\n- 항목";
const FIXTURE_LOCATION = "샘플 장소";
const FIXTURE_TAGS_RAW = '[{"value":"샘플태그","data":{"ctgr":"분류"}},{"value":"둘째","data":{"ctgr":null}}]';
const EDIT = { title: " 고친 제목 ", dateText: "10/24", expireDt: "2001-10-24", projectId: 5, prefixId: 7 };

/** 실제 TS 모듈을 읽는다(외부 의존 없음, 색 표는 실제 journalTagStyle). */
function load(relative) {
  const cache = new Map();
  function req(file) {
    if (cache.has(file)) return cache.get(file).exports;
    const module = { exports: {} };
    cache.set(file, module);
    const source = ts.transpileModule(fs.readFileSync(file, "utf8"), {
      compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022, jsx: ts.JsxEmit.ReactJSX }
    }).outputText;
    vm.runInNewContext(source, {
      module, exports: module.exports, Error, Date, FormData, JSON, Number, Object, Array, Set, String,
      console: { info() {}, warn() {}, error() {} },
      require(name) {
        if (!name.startsWith(".")) throw new Error("Unexpected dependency: " + name);
        const base = path.resolve(path.dirname(file), name);
        return req(fs.existsSync(base + ".ts") ? base + ".ts" : base + ".tsx");
      }
    });
    return module.exports;
  }
  return req(path.resolve(__dirname, "../src", relative));
}

function detail(overrides = {}) {
  return {
    id: FIXTURE_ID, title: "원래 제목", content: FIXTURE_CONTENT, markdownContent: "<p>샘플 본문</p>", location: FIXTURE_LOCATION,
    dateText: "10/17", expireDt: "2001-10-17", prefixId: 3, sortOrder: 12, projectId: null, updatedAt: "2001-10-01 10:00:00",
    state: { list: [{ stateKey: "IMPRTC" }] }, lifecycle: { lifecycleKey: "PENDING" },
    tag: { list: [{ tagId: 1 }, { tagId: 2 }], tagListStrWithCtgr: FIXTURE_TAGS_RAW },
    ...overrides
  };
}

const { toKeepEditFormData, toKeepFormData } = load("utils/keepFormData.ts");

test("수정 FormData는 웹 수정 모달과 같은 필드·순서로 보내고 고칠 수 없는 필드를 상세에서 옮긴다", () => {
  const form = toKeepEditFormData(detail(), EDIT);
  deq([...form.keys()], ["id", "title", "sortOrder", "prefixId", "projectId", "location", "dateText", "expireDt", "content", "tag.tagListStr"]);
  const v = Object.fromEntries(form.entries());
  assert.equal(v.id, String(FIXTURE_ID));
  assert.equal(v.title, "고친 제목");
  assert.equal(v.sortOrder, "12");
  assert.equal(v.prefixId, "7");
  assert.equal(v.projectId, "5");
  assert.equal(v.location, FIXTURE_LOCATION);
  assert.equal(v.content, FIXTURE_CONTENT);
  assert.equal(v.dateText, "10/24");
  assert.equal(v.expireDt, "2001-10-24");
  // 태그를 고치지 않으면(undefined) 상세 태그를 그대로 — null 카테고리는 data 를 뺀다(서버 getString(ctgr) 예외 방지)
  deq(JSON.parse(v["tag.tagListStr"]), [{ value: "샘플태그", data: { ctgr: "분류" } }, { value: "둘째" }]);
  // 상태·라이프사이클은 웹도 싣지 않는다
  assert.ok(!form.has("state") && !form.has("lifecycle") && !form.has("lifecycleKey"));
});

test("수정에서 고른 태그는 웹 Tagify 형식으로 바꿔 보낸다", () => {
  const form = toKeepEditFormData(detail(), { ...EDIT, tags: [{ name: "새것", ctgr: "분류" }, { name: "맨", ctgr: null }] });
  assert.equal(form.get("tag.tagListStr"), '[{"value":"새것","data":{"ctgr":"분류"}},{"value":"맨"}]');
  assert.equal(toKeepEditFormData(detail(), { ...EDIT, tags: [] }).get("tag.tagListStr"), "");
  assert.equal(toKeepFormData({ title: "x", tags: [{ name: "a", ctgr: null }] }).get("tag.tagListStr"), '[{"value":"a"}]');
});

test("빈 본문·위치·태그·순번은 빈 값으로 보존되고 sortOrder 는 없으면 생략", () => {
  const form = toKeepEditFormData(detail({ content: null, markdownContent: "-", location: null, sortOrder: null, tag: { list: [], tagListStrWithCtgr: null } }), EDIT);
  assert.equal(form.get("content"), "");
  assert.equal(form.get("location"), "");
  assert.equal(form.get("tag.tagListStr"), "");
  assert.equal(form.has("sortOrder"), false);
});

for (const [name, overrides, field] of [
  ["본문 필드가 없으면", { content: undefined }, "content"],
  ["본문 원문 없이 렌더 본문만 있으면", { content: null }, "content"],
  ["위치 필드가 없으면", { location: undefined }, "location"],
  ["태그 문자열이 없으면", { tag: { list: [{ tagId: 1 }] } }, "tag"],
  ["태그 개수가 다르면", { tag: { list: [{ tagId: 1 }], tagListStrWithCtgr: FIXTURE_TAGS_RAW } }, "tag"],
  ["태그 JSON 이 깨졌으면", { tag: { list: [{ tagId: 1 }], tagListStrWithCtgr: "[{" } }, "tag"],
  ["ID 가 이상하면", { id: 0 }, "id"]
]) {
  test(name + " 보존할 수 없어 저장을 막는다 (" + field + ")", () => {
    const d = detail(overrides);
    for (const k of Object.keys(overrides)) if (overrides[k] === undefined) delete d[k];
    assert.throws(() => toKeepEditFormData(d, EDIT), (e) => e.name === "KeepEditPreserveError" && e.field === field);
  });
}

test("태그 필드 자체가 없으면 태그를 고치지 않은 수정은 막고, 고른 태그가 있으면 그대로 보낸다", () => {
  const d = detail(); delete d.tag;
  assert.throws(() => toKeepEditFormData(d, EDIT), (e) => e.field === "tag");
  assert.equal(toKeepEditFormData(d, { ...EDIT, tags: [] }).get("tag.tagListStr"), "");
});

test("수정 시트 초기값·선택지: 중요·말머리·비활성 말머리·종결 Project를 유지한다", () => {
  const { keepEditInitial, keepEditPrefixOptions, keepEditProjectOptions, keepChangedSince, keepEditFailureMessage } = load("utils/keepEdit.ts");
  const d = detail({ prefix: { id: 3, name: "옛것", textClass: "text-danger" }, projectId: 9 });
  deq(JSON.parse(JSON.stringify(keepEditInitial(d))), { title: "원래 제목", dateText: "10/17", expireDt: "2001-10-17", projectId: 9, prefixId: 3, important: true });
  const prefixes = keepEditPrefixOptions([{ id: 1, name: "활성", sortOrder: 1 }], d);
  deq(prefixes.map((p) => [p.id, p.name, p.textClass ?? null]), [[1, "활성", null], [3, "옛것", "text-danger"]]);
  const projects = keepEditProjectOptions([{ id: 1, lifecycle: null }, { id: 9, title: "끝남", lifecycle: { lifecycleKey: "RESOLVED" } }], d);
  deq(projects.map((p) => p.id), [1, 9]);
  assert.equal(keepChangedSince(d, { ...d }), false);
  assert.equal(keepChangedSince(d, { ...d, updatedAt: "2001-10-02 10:00:00" }), true);
  assert.equal(keepEditFailureMessage(Object.assign(new Error("서버 오류"), { status: 400 })), "서버 오류");
  assert.match(keepEditFailureMessage(new Error("Network request failed")), /수정 여부를 상세에서 확인/);
});

test("태그 후보: 웹 맵을 (이름, 카테고리)로 펼치고 검색·중복 제외·토글", () => {
  const t = load("utils/keepTags.ts");
  const map = t.normalizeKeepTagCategoryMap({ 독서: ["취미", "공부"], 운동: [], " ": ["x"], 잘못: "x" });
  deq(JSON.parse(JSON.stringify(map)), { 독서: ["취미", "공부"], 운동: [] });
  const all = t.keepTagCandidates(map);
  deq(all.map((c) => [c.name, c.ctgr]), [["독서", "공부"], ["독서", "취미"], ["운동", null]]);
  const picked = t.toggleKeepTag([], all[0]);
  deq(t.filterKeepTagCandidates(all, "독", picked).map((c) => c.ctgr), ["취미"]);
  deq(t.filterKeepTagCandidates(all, "취미", []).map((c) => c.name), ["독서"]);
  deq(t.toggleKeepTag(picked, all[0]), []);
  deq(t.keepDetailTags({ tag: null }), []);
  assert.equal(t.keepDetailTags({}), null);
});

test("말머리 색은 목록·상세·시트·필터가 같은 keepPrefixColor 를 쓴다 (textClass 우선, 없으면 #287D94)", () => {
  const { keepPrefixColor, KEEP_PREFIX_COLOR } = load("types/prospectiveKeep.ts");
  assert.equal(keepPrefixColor("text-primary"), "#1B84FF");
  assert.equal(keepPrefixColor("text-emotion"), "#EC4899");
  assert.equal(keepPrefixColor(""), KEEP_PREFIX_COLOR);
  assert.equal(keepPrefixColor(null), "#287D94");
  assert.equal(keepPrefixColor("text-unknown"), KEEP_PREFIX_COLOR);
  const src = (f) => fs.readFileSync(path.resolve(__dirname, "../src", f), "utf8");
  assert.match(src("components/keep/KeepBadges.tsx"), /keepPrefixColor\(textClass\)/);
  assert.match(src("screens/KeepHomeScreen.tsx"), /KeepPrefixText name=\{item\.prefix\.name\} textClass=\{item\.prefix\.textClass\}/);
  assert.equal((src("screens/KeepDetailScreen.tsx").match(/textClass=\{item\.prefix\.textClass\}/g) ?? []).length, 2);
  assert.match(src("components/keep/KeepFilterSheet.tsx"), /keepPrefixColor\(p\.textClass\)/);
  assert.match(src("components/keep/KeepQuickAddSheet.tsx"), /keepPrefixColor\(option\.textClass\)/);
});
