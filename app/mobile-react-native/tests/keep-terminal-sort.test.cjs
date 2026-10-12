const test = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const ts = require("typescript");

/** 의존성 없는 실제 TypeScript 정렬 모듈을 로드한다. */
function load(relativePath) {
  const source = fs.readFileSync(path.join(__dirname, relativePath), "utf8");
  const compiled = ts.transpileModule(source, {
    compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022 }
  }).outputText;
  const module = { exports: {} };
  vm.runInNewContext(compiled, { module, exports: module.exports, Date });
  return module.exports;
}

const { byTerminalRecentDesc, keepTerminalSortTime, splitTerminalByWindow, KEEP_TERMINAL_WINDOW_DAYS } = load("../src/utils/keepTerminalSort.ts");
const ids = (items) => items.slice().sort(byTerminalRecentDesc).map((it) => it.id);

test("종결 Keep 은 최근 종결이 위로 온다 (2025.03 처럼 오래된 항목이 맨 위가 아니다)", () => {
  const items = [
    { id: 1, createdAt: "2025-03-01 10:00:00", updatedAt: "2025-03-02 10:00:00" },
    { id: 2, createdAt: "2026-09-01 10:00:00", updatedAt: "2026-10-05 09:00:00" },
    { id: 3, createdAt: "2026-01-01 10:00:00", updatedAt: "2026-02-01 10:00:00" }
  ];
  assert.deepEqual(ids(items), [2, 3, 1]);
});

test("완료 시각(resolvedAt)이 있으면 수정일시보다 우선한다", () => {
  const items = [
    { id: 1, updatedAt: "2026-10-09 10:00:00" },
    { id: 2, updatedAt: "2025-01-01 10:00:00", lifecycle: { resolvedAt: new Date(2026, 9, 10, 12).toISOString() } }
  ];
  assert.deepEqual(ids(items), [2, 1]);
});

test("상태 변경 시각(changedAt)이 가장 우선한다 — 오늘 내려놓은 항목이 오래전 완료보다 위", () => {
  const items = [
    { id: 1, updatedAt: "2026-10-10 10:00:00", lifecycle: { resolvedAt: "2026-10-10T01:00:00.000000Z" } },
    { id: 2, updatedAt: "2026-09-20 19:30:05", lifecycle: { changedAt: "2026-10-11T03:00:00.000000Z" } },
    { id: 3, updatedAt: "2026-10-09 10:00:00", lifecycle: { changedAt: "2026-10-10T00:00:00.000000Z", resolvedAt: "2026-10-11T05:00:00.000000Z" } }
  ];
  assert.deepEqual(ids(items), [2, 1, 3]);
});

test("시간대 없는 changedAt 은 무시하고 다음 기준으로 내려간다", () => {
  const item = { id: 1, lifecycle: { changedAt: "2026-10-10T00:00:00" }, updatedAt: "2026-10-01 08:30:00" };
  assert.equal(keepTerminalSortTime(item), new Date(2026, 9, 1, 8, 30).getTime());
});

test("시간대 없는 resolvedAt 은 무시하고 수정일시 → 등록일시로 내려간다", () => {
  const item = { id: 1, lifecycle: { resolvedAt: "2026-10-10T00:00:00" }, createdAt: "2026-10-01 08:30:00" };
  assert.equal(keepTerminalSortTime(item), new Date(2026, 9, 1, 8, 30).getTime());
});

test("같은 시각은 id 내림차순, 시각이 없는 항목은 맨 아래", () => {
  const items = [
    { id: 5 },
    { id: 7, updatedAt: "2026-10-01 10:00:00" },
    { id: 9, updatedAt: "2026-10-01 10:00:00" },
    { id: 8 }
  ];
  assert.deepEqual(ids(items), [9, 7, 8, 5]);
});

test("종결 7일 창: 종결 시각(changedAt → resolvedAt → updatedAt → createdAt)이 7×24시간 이내면 최근, 아니면 이전 — 순서 유지", () => {
  assert.equal(KEEP_TERMINAL_WINDOW_DAYS, 7);
  const now = Date.parse("2026-10-12T12:00:00Z");
  const day = 24 * 60 * 60 * 1000;
  const iso = (ms) => new Date(ms).toISOString();
  const items = [
    { id: 1, lifecycle: { changedAt: iso(now - day) } },
    { id: 2, lifecycle: { changedAt: iso(now - 8 * day), resolvedAt: iso(now - day) } }, // changedAt 우선 → 이전
    { id: 3, lifecycle: { resolvedAt: iso(now - 6 * day) } },
    { id: 4, lifecycle: { changedAt: iso(now - 7 * day) } }, // 경계 포함
    { id: 5, lifecycle: { changedAt: iso(now - 7 * day - 1) } },
    { id: 6 }, // 시각 없음 → 이전
    { id: 7, lifecycle: { changedAt: iso(now + 60000) } }, // 미래(시계 차이) → 최근
    { id: 8, updatedAt: "2001-01-01 00:00:00" }
  ];
  const { recent, older } = splitTerminalByWindow(items, now);
  assert.deepEqual(recent.map((i) => i.id).join(), [1, 3, 4, 7].join());
  assert.deepEqual(older.map((i) => i.id).join(), [2, 5, 6, 8].join());
  // 서버 로컬 형식(updatedAt)은 기기 로컬 시각으로 해석한다
  const local = new Date(2026, 9, 10, 9, 0, 0).getTime();
  const fmt = "2026-10-10 09:00:00";
  assert.deepEqual(splitTerminalByWindow([{ id: 9, updatedAt: fmt }], local + 7 * day).recent.map((i) => i.id).join(), [9].join());
  assert.deepEqual(splitTerminalByWindow([{ id: 9, updatedAt: fmt }], local + 7 * day + 1).older.map((i) => i.id).join(), [9].join());
  assert.deepEqual(JSON.parse(JSON.stringify(splitTerminalByWindow([], now))), { recent: [], older: [] });
});
