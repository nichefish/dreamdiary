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
function harness() {
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
    "../api/dreamDiaryApi": { getKeepList: async () => FIXTURE_LIST, getKeepProjects: async () => [], getKeepTags: async () => [], getKeepPrefixOptions: async () => [] },
    "../api/client": { isUnauthorizedError: () => false },
    "../navigation/appMenu": { MENU_URL: { KEEP: "/keep" } },
    "../components/navigation/AppMenu": { MenuScreenHeader: "MenuHeader" },
    "../components/journal/JournalCopyShareBar": { JournalCopyShareBar: "CopyBar", notifyBrief() {} },
    "../components/keep/KeepBadges": { KeepLifecycleBadge: "LifecycleBadge", KeepPrefixText: "Prefix", KeepStateBadge: "StateBadge", KeepTagChips: "Tags" },
    "../components/keep/KeepFilterSheet": { KeepFilterSheet: "FilterSheet" }
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
      module, exports: module.exports, Date: FixedDate, console: { info() {}, warn() {}, error() {} },
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
async function loaded() {
  const screen = harness();
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
  assert.equal(list.data.filter((row) => row.kind === "section").map((row) => row.label).join(","), "최근 완료,종결");
  assert.equal(list.data.filter((row) => row.kind === "item").map((row) => row.item.id).join(","), "101,102,103,104,105,301");
  assert.equal(copyBar(list).buildText().includes(`${FIXTURE_TITLE} 301`), true);
  assert.equal(copyBar(list).buildText().includes(`${FIXTURE_TITLE} 201`), false);
});
