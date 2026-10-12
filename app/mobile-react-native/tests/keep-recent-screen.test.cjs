const test = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const ts = require("typescript");

const FIXTURE_NOW = Date.parse("2001-01-02T03:04:05.000Z");
const FIXTURE_TITLE = "샘플 메모";
const FIXTURE_LIST = [
  { id: 201, title: `${FIXTURE_TITLE} 201`, lifecycle: { lifecycleKey: "OPEN" } },
  ...Array.from({ length: 6 }, (_, index) => ({ id: 101 + index, title: `${FIXTURE_TITLE} ${101 + index}`,
    lifecycle: { lifecycleKey: "RESOLVED", resolvedAt: new Date(FIXTURE_NOW - index * 1000).toISOString() } })),
  { id: 301, title: `${FIXTURE_TITLE} 301`, lifecycle: { lifecycleKey: "RESOLVED", resolvedAt: null } }
];

/** 실제 화면 함수를 가상 읽기 응답과 가벼운 React 훅·네이티브 표면으로 렌더한다. */
function harness(apiOverrides = {}) {
  const slots = [];
  let cursor = 0;
  let focused = [];
  const jsx = (type, props, key) => ({ type, props, key });
  const react = {
    useState(initial) {
      const index = cursor++;
      if (!(index in slots)) slots[index] = typeof initial === "function" ? initial() : initial;
      return [slots[index], (next) => { slots[index] = typeof next === "function" ? next(slots[index]) : next; }];
    },
    useRef(initial) { const index = cursor++; return slots[index] ?? (slots[index] = { current: initial }); },
    useCallback(callback) { return callback; },
    useMemo(factory) { return factory(); },
    useEffect() {}
  };
  const mocks = {
    react, "react/jsx-runtime": { jsx, jsxs: jsx },
    "react-native": { ActivityIndicator: "ActivityIndicator", FlatList: "FlatList", Pressable: "Pressable", RefreshControl: "RefreshControl",
      ScrollView: "ScrollView", Text: "Text", TextInput: "TextInput", View: "View", StyleSheet: { create: (styles) => styles } },
    "react-native-safe-area-context": { SafeAreaView: "SafeAreaView" },
    "@react-navigation/native": { useNavigation: () => ({ navigate() {} }), useFocusEffect: (callback) => { focused.push(callback); } },
    "../api/dreamDiaryApi": { getKeepList: async () => FIXTURE_LIST, getKeepProjects: async () => [], getKeepTags: async () => [], getKeepPrefixOptions: async () => [], getKeepTagCategoryMap: async () => ({}), setKeepLifecycle: async () => {}, toggleKeepState: async () => {}, ...apiOverrides },
    "../api/client": { isUnauthorizedError: (error) => error?.status === 401 },
    "../navigation/appMenu": { MENU_URL: { KEEP: "/keep" } },
    "../components/navigation/AppMenu": { MenuScreenHeader: "MenuHeader" },
    "../components/journal/JournalCopyShareBar": { JournalCopyShareBar: "CopyBar", notifyBrief() {} },
    "../components/keep/KeepBadges": { KeepLifecycleBadge: "LifecycleBadge", KeepPrefixText: "Prefix", KeepStateBadge: "StateBadge", KeepTagChips: "Tags" },
    "../components/keep/KeepFilterSheet": { KeepFilterSheet: "FilterSheet" },
    "../components/keep/KeepSwipeRow": { KeepSwipeRow: "SwipeRow" },
    "../components/keep/KeepLifecycleMenu": { KeepLifecycleMenu: "LifecycleMenu" },
    "../components/keep/KeepProjectMoveSheet": { KeepProjectMoveSheet: "MoveSheet" },
    "../components/keep/useKeepEditSheet": { useKeepEditSheet: () => ({ openEdit: async () => {}, loading: false, savingRef: { current: false }, sheetProps: { visible: false, projects: [], prefixes: [], defaultProjectId: null, initial: null, tagMap: {}, onSubmit: async () => ({ ok: false, message: null }), onClose() {} } }) },
    "../components/keep/KeepQuickAddSheet": { KeepQuickAddSheet: "QuickAddSheet" }
  };
  const cache = new Map();
  class FixedDate extends Date {
    constructor(value) { super(value === undefined ? FIXTURE_NOW : value); }
    static now() { return FIXTURE_NOW; }
  }
  function load(file) {
    if (cache.has(file)) return cache.get(file).exports;
    const module = { exports: {} };
    cache.set(file, module);
    const source = fs.readFileSync(file, "utf8");
    const compiled = ts.transpileModule(source, { compilerOptions: {
      module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022, jsx: ts.JsxEmit.ReactJSX
    } }).outputText;
    vm.runInNewContext(compiled, {
      module, exports: module.exports, Date: FixedDate, Error, console: { info() {}, warn() {}, error() {} },
      require(name) {
        if (name in mocks) return mocks[name];
        if (!name.startsWith(".")) throw new Error("Unexpected module: " + name);
        return load(path.resolve(path.dirname(file), name + ".ts"));
      }
    });
    return module.exports;
  }
  const { KeepHomeScreen } = load(path.resolve(__dirname, "../src/screens/KeepHomeScreen.tsx"));
  function render() { cursor = 0; focused = []; return KeepHomeScreen(); }
  return { render, focus() { for (const callback of focused) callback(); } };
}

/** 네이티브 요소의 children을 화면 순서로 탐색한다. */
function* walk(node) {
  if (Array.isArray(node)) { for (const child of node) yield* walk(child); }
  else if (node && typeof node === "object") { yield node; yield* walk(node.props?.children); }
}
function flatList(tree) { return [...walk(tree)].find((node) => node.type === "FlatList").props; }
function copyBar(list) { return [...walk(list.ListHeaderComponent)].find((node) => node.type === "CopyBar").props; }
function footerRows(list) { return [...walk(list.ListFooterComponent)].filter((node) => node.type?.name === "KeepRow"); }

/** 가상 응답의 Promise와 상태 반영을 마친다. */
async function loaded(apiOverrides) {
  const screen = harness(apiOverrides);
  screen.render();
  screen.focus();
  for (let index = 0; index < 5; index++) await Promise.resolve();
  return screen;
}

test("앱 진행: 최근 완료 기본 접힘·5건·전체 펼침과 복사 범위 일치", async () => {
  const screen = await loaded();
  let list = flatList(screen.render());
  assert.equal(footerRows(list).length, 0);
  assert.equal(copyBar(list).buildText().includes(`${FIXTURE_TITLE} 101`), false);
  [...walk(list.ListFooterComponent)].find((node) => node.type === "Pressable").props.onPress();
  list = flatList(screen.render());
  assert.equal(footerRows(list).map((node) => node.props.item.id).join(","), "101,102,103,104,105");
  assert.equal(copyBar(list).buildText().includes(`${FIXTURE_TITLE} 105`), true);
  assert.equal(copyBar(list).buildText().includes(`${FIXTURE_TITLE} 106`), false);
  [...walk(list.ListFooterComponent)].find((node) => node.type === "Pressable" && node.props.accessibilityState?.expanded === false).props.onPress();
  list = flatList(screen.render());
  assert.equal(footerRows(list).length, 6);
  assert.equal(copyBar(list).buildText().includes(`${FIXTURE_TITLE} 106`), true);
});

test("앱 종결: 최근 완료가 먼저이며 미확인 완료는 나머지 종결·모든 보기 건수 유지", async () => {
  const screen = await loaded();
  let list = flatList(screen.render());
  const terminalTab = [...walk(list.ListHeaderComponent)].find((node) => node.props?.accessibilityRole === "tab" && JSON.stringify(node.props.children).includes("종결"));
  terminalTab.props.onPress();
  list = flatList(screen.render());
  // 7일보다 오래된 종결(301)은 기본으로 접혀 맨 아래 `이전 종결 1개 더 보기` 뒤에 있다
  assert.equal(list.data.filter((row) => row.kind === "section").map((row) => row.label).join(","), "최근 완료");
  assert.equal(list.data.filter((row) => row.kind === "item").map((row) => row.item.id).join(","), "101,102,103,104,105");
  assert.deepEqual(JSON.parse(JSON.stringify(list.data.at(-1))), { kind: "olderMore", key: "older-more", count: 1 });
  assert.equal(copyBar(list).buildText().includes(`${FIXTURE_TITLE} 301`), false);
  const more = [...walk(list.renderItem({ item: list.data.at(-1) }))].find((node) => node.props?.accessibilityLabel === "이전 종결 1개 더 보기");
  more.props.onPress();
  list = flatList(screen.render());
  assert.equal(list.data.filter((row) => row.kind === "section").map((row) => row.label).join(","), "최근 완료,이전 종결");
  assert.equal(list.data.filter((row) => row.kind === "item").map((row) => row.item.id).join(","), "101,102,103,104,105,301");
  assert.equal(list.data.at(-1).kind, "olderMore");
  assert.equal(copyBar(list).buildText().includes(`${FIXTURE_TITLE} 301`), true);
  assert.equal(copyBar(list).buildText().includes(`${FIXTURE_TITLE} 201`), false);
});

const FIXTURE_ERROR = "샘플 상태 오류";
const FIXTURE_COMPLETED_LIST = FIXTURE_LIST.map((item) => item.id === 201
  ? { ...item, lifecycle: { lifecycleKey: "RESOLVED", resolvedAt: new Date(FIXTURE_NOW).toISOString() } } : item);

/** 실제 응답 순서를 제어하는 가상 요청이다. */
function deferred() {
  let resolve;
  const promise = new Promise((done) => { resolve = done; });
  return { promise, resolve };
}

/** 화면 이벤트 뒤 Promise와 상태 반영을 마친다. */
async function settleAction() { for (let index = 0; index < 12; index++) await Promise.resolve(); }
function progressSwipe(list) {
  const row = list.data.find((row) => row.kind === "item" && row.item.id === 201);
  return list.renderItem({ item: row });
}

test("완료 연타는 PUT 한 번·Keep 재조회 한 번이며 서버 완료 시각을 반영", async () => {
  const write = deferred();
  let writes = 0;
  let reads = 0;
  let projectReads = 0;
  const screen = await loaded({
    setKeepLifecycle: async (id, key) => { assert.equal(id, 201); assert.equal(key, "RESOLVED"); writes++; await write.promise; },
    getKeepList: async () => ++reads === 1 ? FIXTURE_LIST : FIXTURE_COMPLETED_LIST,
    getKeepProjects: async () => { projectReads++; return []; }
  });
  const swipe = progressSwipe(flatList(screen.render()));
  swipe.props.onComplete();
  swipe.props.onComplete();
  screen.focus();
  assert.equal(writes, 1);
  assert.equal(reads, 1);
  assert.equal(progressSwipe(flatList(screen.render())).props.disabled, true);
  write.resolve();
  await settleAction();
  const list = flatList(screen.render());
  assert.equal(reads, 2);
  assert.equal(projectReads, 1);
  assert.equal(list.data.some((row) => row.item?.id === 201), false);
  [...walk(list.ListFooterComponent)].find((node) => node.type === "Pressable").props.onPress();
  const complete = footerRows(flatList(screen.render())).find((row) => row.props.item.id === 201);
  assert.equal(complete.props.item.lifecycle.resolvedAt, new Date(FIXTURE_NOW).toISOString());
});

test("상태 메뉴 저장 실패는 서버 오류를 표시하고 목록과 메뉴 현재 상태 유지", async () => {
  let reads = 0;
  const screen = await loaded({
    setKeepLifecycle: async () => { throw new Error(FIXTURE_ERROR); },
    getKeepList: async () => { reads++; return FIXTURE_LIST; }
  });
  progressSwipe(flatList(screen.render())).props.children.props.onStatus();
  let menu = [...walk(screen.render())].find((node) => node.type === "LifecycleMenu");
  menu.props.onSelect("PENDING");
  await settleAction();
  menu = [...walk(screen.render())].find((node) => node.type === "LifecycleMenu");
  assert.equal(menu.props.error, FIXTURE_ERROR);
  assert.equal(menu.props.item.lifecycle.lifecycleKey, "OPEN");
  assert.equal(menu.props.saving, false);
  assert.equal(reads, 1);
});

test("저장 후 조회 실패는 쓰기를 재전송하지 않고 조회만 재시도", async () => {
  let reads = 0;
  let writes = 0;
  const screen = await loaded({
    setKeepLifecycle: async () => { writes++; },
    getKeepList: async () => {
      reads++;
      if (reads === 2) throw new Error(FIXTURE_ERROR);
      return reads === 1 ? FIXTURE_LIST : FIXTURE_COMPLETED_LIST;
    }
  });
  progressSwipe(flatList(screen.render())).props.onComplete();
  await settleAction();
  let list = flatList(screen.render());
  assert.equal(progressSwipe(list).props.disabled, true);
  progressSwipe(list).props.onComplete();
  await settleAction();
  assert.equal(writes, 1);
  const retry = [...walk(list.ListHeaderComponent)].find((node) => node.type === "Pressable" && JSON.stringify(node.props.children).includes("목록 다시 조회"));
  assert.ok(retry);
  retry.props.onPress();
  await settleAction();
  list = flatList(screen.render());
  assert.equal(reads, 3);
  assert.equal(writes, 1);
  assert.equal(list.data.some((row) => row.item?.id === 201), false);
});

test("저장 전 시작한 느린 조회 응답은 완료 후 목록을 되돌리지 않음", async () => {
  const oldRead = deferred();
  let reads = 0;
  const screen = await loaded({
    setKeepLifecycle: async () => {},
    getKeepList: async () => {
      reads++;
      return reads === 1 ? FIXTURE_LIST : reads === 2 ? oldRead.promise : FIXTURE_COMPLETED_LIST;
    }
  });
  let list = flatList(screen.render());
  list.refreshControl.props.onRefresh();
  progressSwipe(list).props.onComplete();
  await settleAction();
  assert.equal(flatList(screen.render()).data.some((row) => row.item?.id === 201), false);
  oldRead.resolve(FIXTURE_LIST);
  await settleAction();
  assert.equal(flatList(screen.render()).data.some((row) => row.item?.id === 201), false);
});

test("동일 상태·기한 없는 만료 요청은 API 호출 없이 막음", async () => {
  let writes = 0;
  const screen = await loaded({ setKeepLifecycle: async () => { writes++; } });
  progressSwipe(flatList(screen.render())).props.children.props.onStatus();
  const menu = [...walk(screen.render())].find((node) => node.type === "LifecycleMenu");
  menu.props.onSelect("OPEN");
  menu.props.onSelect("EXPIRED");
  await settleAction();
  assert.equal(writes, 0);
});

test("401은 화면 오류를 추가하지 않고 요청 잠금을 해제", async () => {
  const error = new Error(FIXTURE_ERROR);
  error.status = 401;
  const screen = await loaded({ setKeepLifecycle: async () => { throw error; } });
  progressSwipe(flatList(screen.render())).props.onComplete();
  await settleAction();
  const list = flatList(screen.render());
  assert.equal(progressSwipe(list).props.disabled, false);
  assert.equal(JSON.stringify(list.ListHeaderComponent).includes(FIXTURE_ERROR), false);
});

/** 가상 진행 항목의 속성 메뉴를 연다. */
function stateMenu(screen) {
  progressSwipe(flatList(screen.render())).props.children.props.onStatus();
  return [...walk(screen.render())].find((node) => node.type === "LifecycleMenu");
}
const FIXTURE_IMPORTANT_LIST = FIXTURE_LIST.map((item) => item.id === 201
  ? { ...item, state: { list: [{ stateKey: "IMPRTC" }] } } : item);

test("속성 연타·라이프사이클 동시 요청은 POST 한 번으로 잠그고 서버 값 반영", async () => {
  const write = deferred();
  let writes = 0;
  let lifecycleWrites = 0;
  let reads = 0;
  const screen = await loaded({
    toggleKeepState: async (id, key) => { assert.equal(id, 201); assert.equal(key, "IMPRTC"); writes++; await write.promise; },
    setKeepLifecycle: async () => { lifecycleWrites++; },
    getKeepList: async () => ++reads === 1 ? FIXTURE_LIST : FIXTURE_IMPORTANT_LIST
  });
  const menu = stateMenu(screen);
  menu.props.onToggleState("IMPRTC");
  menu.props.onToggleState("IMPRTC");
  menu.props.onSelect("RESOLVED");
  screen.focus();
  assert.equal(writes, 1);
  assert.equal(lifecycleWrites, 0);
  assert.equal(reads, 1);
  write.resolve();
  await settleAction();
  assert.equal(reads, 2);
  assert.equal(progressSwipe(flatList(screen.render())).props.children.props.item.state.list[0].stateKey, "IMPRTC");
});

test("응답 유실 뒤에는 POST를 반복하지 않고 GET으로 실제 속성을 확인", async () => {
  let writes = 0;
  let reads = 0;
  const screen = await loaded({
    toggleKeepState: async () => { writes++; throw new Error(FIXTURE_ERROR); },
    getKeepList: async () => ++reads === 1 ? FIXTURE_LIST : FIXTURE_IMPORTANT_LIST
  });
  stateMenu(screen).props.onToggleState("IMPRTC");
  await settleAction();
  const list = flatList(screen.render());
  assert.equal(writes, 1);
  assert.equal(reads, 2);
  assert.equal(progressSwipe(list).props.disabled, false);
  assert.equal(progressSwipe(list).props.children.props.item.state.list[0].stateKey, "IMPRTC");
  assert.equal(JSON.stringify(list.ListHeaderComponent).includes(FIXTURE_ERROR), true);
});

test("속성 POST·조회 모두 실패하면 쓰기를 잠그고 재시도는 GET만 실행", async () => {
  let writes = 0;
  let reads = 0;
  const screen = await loaded({
    toggleKeepState: async () => { writes++; throw new Error(FIXTURE_ERROR); },
    getKeepList: async () => { reads++; if (reads === 2) throw new Error(FIXTURE_ERROR);
      return reads === 1 ? FIXTURE_LIST : FIXTURE_IMPORTANT_LIST; }
  });
  stateMenu(screen).props.onToggleState("IMPRTC");
  await settleAction();
  let list = flatList(screen.render());
  assert.equal(progressSwipe(list).props.disabled, true);
  progressSwipe(list).props.onComplete();
  const retry = [...walk(list.ListHeaderComponent)].find((node) => node.type === "Pressable"
    && JSON.stringify(node.props.children).includes("목록 다시 조회"));
  assert.ok(retry);
  retry.props.onPress();
  await settleAction();
  assert.equal(writes, 1);
  assert.equal(reads, 3);
  assert.equal(progressSwipe(flatList(screen.render())).props.disabled, false);
});

test("중요 필터를 유지한 채 중요 해제 결과를 서버 목록에서 투영", async () => {
  let reads = 0;
  const screen = await loaded({
    toggleKeepState: async () => {},
    getKeepList: async () => ++reads === 1 ? FIXTURE_IMPORTANT_LIST : FIXTURE_LIST
  });
  const sheet = [...walk(screen.render())].find((node) => node.type === "FilterSheet");
  sheet.props.onChange({ ...sheet.props.filters, important: true });
  stateMenu(screen).props.onToggleState("IMPRTC");
  await settleAction();
  assert.equal(flatList(screen.render()).data.some((row) => row.item?.id === 201), false);
});