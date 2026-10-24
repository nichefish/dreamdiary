const test = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const ts = require("typescript");

const FIXTURE_ID = 101;
const FIXTURE_TITLE = "샘플 메모";
const FIXTURE_BODY = ["# 가상 본문", "- 항목"].join(String.fromCharCode(10));
const FIXTURE_DETAIL = { id: FIXTURE_ID, title: FIXTURE_TITLE, location: "가상 장소", content: FIXTURE_BODY,
  markdownContent: "<p>가상 본문</p>", dateText: "2001/10/24", expireDt: "2001-10-24", projectId: 201, prefixId: 301,
  prefix: { id: 301, name: "가상 말머리", textClass: "text-danger" }, sortOrder: 1,
  updatedAt: "2001-01-01 00:00:00", lifecycle: { lifecycleKey: "OPEN" },
  state: { list: [] }, tag: { list: [{ tagId: 401 }], tagListStrWithCtgr: '[{"value":"가상 태그","data":{"ctgr":"분류"}}]' } };
const FIXTURE_PROJECTS = [{ id: 201, title: "가상 Project", lifecycle: { lifecycleKey: "OPEN" } }, { id: 202, title: "둘째 Project", lifecycle: { lifecycleKey: "OPEN" } }];
const FIXTURE_PREFIXES = [{ id: 301, name: "가상 말머리", textClass: "text-danger" }];

function deferred() { let resolve; const promise = new Promise((done) => { resolve = done; }); return { promise, resolve }; }

/** 실제 UI·저장 훅을 실행한다. API만 가상 응답이며 각 컴포넌트의 훅 상태·effect는 독립적이다. */
function harness(overrides = {}) {
  let current = null;
  let cursor = 0;
  const fibers = new Map();
  const effects = [];
  const cleanups = [];
  const alerts = [];
  const calls = [];
  const cache = new Map();
  let prevent = null;
  const jsx = (type, props) => ({ type, props });
  const same = (a, b) => a && b && a.length === b.length && a.every((v, i) => Object.is(v, b[i]));
  const slots = () => fibers.get(current);
  const react = {
    useState(initial) { const i = cursor++; const state = slots(); if (!(i in state)) state[i] = typeof initial === "function" ? initial() : initial;
      return [state[i], (next) => { state[i] = typeof next === "function" ? next(state[i]) : next; }]; },
    useRef(initial) { const i = cursor++; const state = slots(); return state[i] ?? (state[i] = { current: initial }); },
    useMemo(factory, deps) { const i = cursor++; const state = slots(); if (!state[i] || !same(state[i].deps, deps)) state[i] = { deps, value: factory() }; return state[i].value; },
    useCallback(callback, deps) { return react.useMemo(() => callback, deps); },
    useEffect(callback, deps) { const i = cursor++; const state = slots(); if (!state[i] || !same(state[i].deps, deps)) { state[i] = { deps }; effects.push(callback); } }
  };
  class ApiError extends Error { constructor(message, status) { super(message); this.status = status; } }
  const apis = {
    getKeepDetail: async () => FIXTURE_DETAIL,
    getKeepProjects: async () => FIXTURE_PROJECTS,
    getKeepPrefixOptions: async () => FIXTURE_PREFIXES,
    getKeepTagCategoryMap: async () => ({ "가상 태그": ["분류"] }),
    modifyKeep: async () => {}, toggleKeepState: async () => {}, deleteKeep: async () => {},
    ...overrides
  };
  const api = Object.fromEntries(Object.entries(apis).map(([name, fn]) => [name, (...args) => { calls.push({ name, args }); return fn(...args); }]));
  const native = Object.fromEntries(["View", "Text", "TextInput", "Pressable", "ScrollView", "RefreshControl", "ActivityIndicator",
    "KeyboardAvoidingView", "Switch"].map((name) => [name, name]));
  Object.assign(native, { Keyboard: { dismiss() {} }, StyleSheet: { create: (styles) => styles },
    Alert: { alert: (...args) => alerts.push(args) } });
  function load(file) {
    if (cache.has(file)) return cache.get(file).exports;
    const module = { exports: {} }; cache.set(file, module);
    const compiled = ts.transpileModule(fs.readFileSync(file, "utf8"), {
      compilerOptions: { target: ts.ScriptTarget.ES2022, module: ts.ModuleKind.CommonJS, jsx: ts.JsxEmit.ReactJSX }
    }).outputText;
    vm.runInNewContext(compiled, { module, exports: module.exports, Error, Date, FormData, JSON, Math, console: { warn() {}, info() {}, error() {} },
      require(name) {
        if (name === "react") return react;
        if (name === "react/jsx-runtime") return { jsx, jsxs: jsx };
        if (name === "react-native") return native;
        if (name === "react-native-pell-rich-editor") return {
          RichEditor: "RichEditor", actions: { setBold: "bold", setItalic: "italic", insertBulletsList: "bullets",
            insertOrderedList: "ordered", undo: "undo", redo: "redo" },
          createHTML: () => "<html><head></head><body><script></script></body></html>"
        };
        if (name.endsWith("/config/env")) return { API_BASE_URL: "https://example.invalid/" };
        if (name === "react-native-safe-area-context") return { SafeAreaView: "SafeAreaView" };
        if (name === "@react-navigation/native") return { usePreventRemove: (enabled, callback) => { prevent = { enabled, callback }; } };
        if (name.endsWith("/dreamDiaryApi")) return api;
        if (name.endsWith("/client")) return { ApiError, isUnauthorizedError: (e) => e.status === 401 };
        if (name.endsWith("/JournalCopyShareBar")) return { notifyBrief() {}, copyTextToClipboard() {}, shareText() {} };
        if (!name.startsWith(".")) throw new Error("Unexpected dependency: " + name);
        const base = path.resolve(path.dirname(file), name);
        return load(fs.existsSync(base + ".ts") ? base + ".ts" : base + ".tsx");
      }
    });
    return module.exports;
  }
  function render(fn, props) { current = fn; cursor = 0; if (!fibers.has(fn)) fibers.set(fn, []); return fn(props); }
  return {
    load: (file) => load(path.resolve(__dirname, "../src", file)), render, api, calls, alerts,
    effect() { for (const callback of effects.splice(0)) { const cleanup = callback(); if (typeof cleanup === "function") cleanups.push(cleanup); } },
    unmount() { for (const cleanup of cleanups) cleanup(); },
    prevent: () => prevent
  };
}
function* walk(node) {
  if (Array.isArray(node)) { for (const child of node) yield* walk(child); }
  else if (node && typeof node === "object") { yield node; yield* walk(node.props?.children); }
}
async function flush() { for (let i = 0; i < 25; i++) await Promise.resolve(); }
async function controller(overrides) {
  const h = harness(overrides);
  const fn = h.load("components/keep/useKeepEdit.ts").useKeepEdit;
  const saved = [];
  const props = { onSaved: (id) => saved.push(id), logTag: "fixture" };
  const view = () => h.render(fn, props);
  view(); h.effect();
  await view().initialize(FIXTURE_DETAIL);
  return { h, view, saved };
}

test("읽기 항목을 누르면 해당 항목 아래만 열리고 입력은 API 없이 임시 값 변경", () => {
  const h = harness();
  const fn = h.load("components/keep/KeepInlineEditor.tsx").KeepInlineEditor;
  const { keepEditDraft } = h.load("utils/keepEdit.ts");
  let active = null;
  let draft = keepEditDraft(FIXTURE_DETAIL);
  const props = () => ({ item: FIXTURE_DETAIL, draft, projects: FIXTURE_PROJECTS, prefixes: FIXTURE_PREFIXES, tagMap: {},
    active, disabled: false, onOpen: (field) => { active = field; }, onChange: (patch) => { draft = { ...draft, ...patch }; } });
  function rows() { return [...walk(h.render(fn, props()))].filter((n) => typeof n.type === "function" && n.props.field); }
  const title = rows().find((n) => n.props.field === "title");
  [...walk(h.render(title.type, title.props))].find((n) => n.props.accessibilityLabel === "제목 편집").props.onPress();
  assert.equal(active, "title");
  assert.equal(rows().filter((n) => [...walk(h.render(n.type, n.props))].some((child) => child.type === "TextInput")).length, 1);
  const expanded = rows().find((n) => n.props.field === "title");
  [...walk(h.render(expanded.type, expanded.props))].find((n) => n.props.accessibilityLabel === "제목 입력").props.onChangeText("가상 수정");
  const location = rows().find((n) => n.props.field === "location");
  [...walk(h.render(location.type, location.props))].find((n) => n.props.accessibilityLabel === "위치 편집").props.onPress();
  assert.equal(active, "location");
  assert.equal(draft.title, "가상 수정");
  assert.equal(h.calls.length, 0);
});

test("빈 제목·기한·말머리는 옛 값으로 되돌려 표시하지 않고 Project도 임시 값으로 변경", () => {
  const h = harness();
  const fn = h.load("components/keep/KeepInlineEditor.tsx").KeepInlineEditor;
  const { keepEditDraft } = h.load("utils/keepEdit.ts");
  let draft = { ...keepEditDraft(FIXTURE_DETAIL), title: "", expireDt: "", prefixId: null };
  const props = { item: FIXTURE_DETAIL, draft, projects: FIXTURE_PROJECTS, prefixes: FIXTURE_PREFIXES, tagMap: {},
    active: "projectId", disabled: false, onOpen() {}, onChange: (patch) => { draft = { ...draft, ...patch }; } };
  const tree = h.render(fn, props);
  const rows = [...walk(tree)].filter((n) => typeof n.type === "function" && n.props.field);
  assert.equal(JSON.stringify(rows.find((n) => n.props.field === "title").props.value).includes(FIXTURE_TITLE), false);
  assert.equal(JSON.stringify(rows.find((n) => n.props.field === "expireDt").props.value).includes("2001-10-24"), false);
  assert.equal(JSON.stringify(rows.find((n) => n.props.field === "prefixId").props.value).includes("가상 말머리"), false);
  const project = rows.find((n) => n.props.field === "projectId");
  const choice = [...walk(h.render(project.type, project.props))].find((n) => n.props.accessibilityLabel === "Project 둘째 Project");
  choice.props.onPress();
  assert.equal(draft.projectId, 202);
  assert.equal(h.calls.length, 0);
});

test("기한 달력 선택은 API 없이 절대 날짜를 임시 값으로 전달", () => {
  const h = harness();
  const fn = h.load("components/keep/KeepInlineEditor.tsx").KeepInlineEditor;
  const { keepEditDraft } = h.load("utils/keepEdit.ts");
  let patch;
  const tree = h.render(fn, { item: FIXTURE_DETAIL, draft: keepEditDraft(FIXTURE_DETAIL), projects: [], prefixes: [],
    tagMap: {}, active: "expireDt", disabled: false, onOpen() {}, onChange: (value) => { patch = value; } });
  const row = [...walk(tree)].find((n) => n.props.field === "expireDt");
  const panel = h.render(row.type, row.props);
  const calendar = [...walk(panel)].find((n) => typeof n.type === "function" && n.type.name === "DueCalendar");
  const day = [...walk(h.render(calendar.type, calendar.props))].find((n) => n.props.accessibilityLabel === "기한 2001-10-25");
  day.props.onPress();
  assert.equal(patch.expireDt, "2001-10-25");
  assert.equal(h.calls.length, 0);
});

test("임시 값·취소는 서버 쓰기가 없고 저장 연타는 원문·Project POST 한 번", async () => {
  const pending = deferred();
  const { h, view, saved } = await controller({ modifyKeep: async () => pending.promise });
  view().patch({ content: FIXTURE_BODY + " 가상 변경", projectId: 202 });
  assert.equal(view().dirty, true);
  view().reset();
  assert.equal(view().dirty, false);
  assert.equal(h.calls.some((c) => c.name === "modifyKeep"), false);
  view().patch({ content: FIXTURE_BODY + " 가상 변경", projectId: 202 });
  const first = view().save(view().draft);
  await flush();
  assert.equal(view().saving, true);
  await view().save(view().draft);
  assert.equal(h.calls.filter((c) => c.name === "modifyKeep").length, 1);
  const sent = h.calls.find((c) => c.name === "modifyKeep").args[1];
  assert.equal(sent.projectId, 202);
  assert.equal(sent.content, FIXTURE_BODY + " 가상 변경");
  pending.resolve();
  await first;
  assert.equal(saved.length, 1);
  assert.equal(view().saving, false);
});

test("동시 수정·서버 오류는 임시 값을 유지하고 시간 초과는 재조회로 확인", async () => {
  const conflict = await controller({ getKeepDetail: async () => ({ ...FIXTURE_DETAIL, updatedAt: "2001-01-02 00:00:00" }) });
  conflict.view().patch({ title: "가상 수정" });
  await conflict.view().save(conflict.view().draft);
  assert.equal(conflict.h.calls.some((c) => c.name === "modifyKeep"), false);
  assert.equal(conflict.view().draft.title, "가상 수정");
  assert.equal(conflict.view().error.includes("다른 곳"), true);
  const rejected = await controller({ modifyKeep: async () => { throw Object.assign(new Error("샘플 서버 오류"), { status: 400 }); } });
  rejected.view().patch({ title: "가상 수정" });
  await rejected.view().save(rejected.view().draft);
  assert.equal(rejected.view().draft.title, "가상 수정");
  assert.equal(rejected.view().error, "샘플 서버 오류");
  const unknown = await controller({ modifyKeep: async () => { throw Object.assign(new Error("샘플 연결 오류"), { status: 0 }); } });
  unknown.view().patch({ title: "가상 수정" });
  const staleInput = unknown.view().draft;
  await unknown.view().save(staleInput);
  assert.equal(unknown.view().draft, null);
  assert.equal(unknown.saved.length, 1);
  await unknown.view().save(staleInput);
  assert.equal(unknown.h.calls.filter((c) => c.name === "modifyKeep").length, 1);
});

test("초기화 중 입력과 원문 누락 편집을 차단", async () => {
  const pending = deferred();
  const c = await controller();
  const h = c.h;
  // 진행 중인 선택지 요청에서는 이전 편집값을 재사용하지 않는다.
  h.api.getKeepProjects = () => pending.promise;
  const start = c.view().initialize(FIXTURE_DETAIL);
  c.view().patch({ title: "가상 수정" });
  assert.equal(c.view().draft, null);
  pending.resolve(FIXTURE_PROJECTS);
  await start;
  assert.equal(c.view().draft.title, FIXTURE_TITLE);
  const invalid = { ...FIXTURE_DETAIL }; delete invalid.content;
  await c.view().initialize(invalid);
  assert.equal(c.view().draft, null);
  assert.ok(c.view().error);
});

test("통합 화면은 변경 중 나가기 확인·저장/취소·새로고침 보호를 제공", async () => {
  const h = harness();
  const fn = h.load("screens/KeepDetailScreen.tsx").KeepDetailScreen;
  const dispatched = [];
  const props = { route: { params: { id: FIXTURE_ID, item: FIXTURE_DETAIL } },
    navigation: { goBack() {}, dispatch: (action) => dispatched.push(action) } };
  function screen() { return h.render(fn, props); }
  screen(); h.effect(); await flush();
  let tree = screen(); h.effect();
  const inline = [...walk(tree)].find((n) => n.type?.name === "KeepInlineEditor");
  inline.props.onChange({ title: "가상 수정" });
  tree = screen();
  assert.equal(h.prevent().enabled, true);
  assert.ok([...walk(tree)].some((n) => n.props.accessibilityLabel === "Keep 변경 저장"));
  const scroll = [...walk(tree)].find((n) => n.type === "ScrollView");
  const reads = h.calls.filter((c) => c.name === "getKeepDetail").length;
  scroll.props.refreshControl.props.onRefresh();
  assert.equal(h.calls.filter((c) => c.name === "getKeepDetail").length, reads);
  const action = { type: "GO_BACK" };
  h.prevent().callback({ data: { action } });
  const alert = h.alerts.at(-1);
  assert.equal(alert[0], "변경 내용 버리기");
  alert[2][1].onPress();
  assert.equal(dispatched[0], action);
});

test("목록 수정은 같은 통합 화면의 제목으로 들어가고 빠른 추가는 등록만 제공", () => {
  const source = (file) => fs.readFileSync(path.resolve(__dirname, "../src", file), "utf8");
  assert.match(source("screens/KeepHomeScreen.tsx"), /editField: "title"/);
  assert.doesNotMatch(source("screens/KeepHomeScreen.tsx"), /useKeepEditSheet|edit\.sheetProps/);
  assert.doesNotMatch(source("components/keep/KeepQuickAddSheet.tsx"), /KeepSheetInitial|const editing/);
});

test("삭제 진행 중 나가기는 차단하고 성공 응답 뒤에는 목록으로 복귀", async () => {
  const pending = deferred();
  const h = harness({ deleteKeep: async () => pending.promise });
  const fn = h.load("screens/KeepDetailScreen.tsx").KeepDetailScreen;
  const dispatched = [];
  const action = { type: "GO_BACK" };
  const props = { route: { params: { id: FIXTURE_ID, item: FIXTURE_DETAIL } },
    navigation: { goBack() { if (h.prevent().enabled) h.prevent().callback({ data: { action } }); else dispatched.push(action); },
      dispatch: (value) => dispatched.push(value) } };
  const screen = () => h.render(fn, props);
  screen(); h.effect(); await flush();
  const button = [...walk(screen())].find((n) => n.props.accessibilityLabel === "Keep 삭제");
  button.props.onPress();
  screen();
  const confirmation = h.alerts.at(-1);
  confirmation[2].find((b) => b.style === "destructive").onPress();
  await flush();
  assert.equal(h.calls.filter((c) => c.name === "deleteKeep").length, 1);
  h.prevent().callback({ data: { action } });
  assert.equal(dispatched.length, 0);
  assert.equal(h.alerts.length, 1);
  pending.resolve();
  await flush();
  assert.equal(dispatched.length, 1);
  assert.equal(dispatched[0], action);
  assert.equal(h.calls.filter((c) => c.name === "deleteKeep").length, 1);
});
test("일시의 유효 날짜는 기한을 갱신하고 미완성 입력은 기존 날짜를 보존", async () => {
  const c = await controller();
  c.view().patch({ dateText: "2001/10/25,26" });
  assert.equal(c.view().draft.expireDt, "2001-10-26");
  c.view().patch({ dateText: "날짜 입력 중" });
  assert.equal(c.view().draft.expireDt, "2001-10-26");
  assert.equal(c.h.calls.some((call) => call.name === "modifyKeep"), false);
});

test("직접 지정·빈 기한은 일시 변경에서 보존하고 취소·초기화는 자동 연동 재개", async () => {
  const c = await controller();
  c.view().patch({ expireDt: "2001-11-01" });
  c.view().patch({ dateText: "2001/10/27" });
  assert.equal(c.view().draft.expireDt, "2001-11-01");
  c.view().patch({ expireDt: "" });
  c.view().patch({ dateText: "2001/10/28" });
  assert.equal(c.view().draft.expireDt, "");
  c.view().reset();
  c.view().patch({ dateText: "2001/10/29" });
  assert.equal(c.view().draft.expireDt, "2001-10-29");
  c.view().patch({ expireDt: "" });
  await c.view().initialize(FIXTURE_DETAIL);
  c.view().patch({ dateText: "2001/10/30" });
  assert.equal(c.view().draft.expireDt, "2001-10-30");
});
const FIXTURE_HTML = '<p>가상 <strong>강조</strong></p><ul><li>샘플 항목</li></ul><p><a href="https://example.invalid">샘플 링크</a><img src="/sample.png"></p><table><tr><td>가상 표</td></tr></table>';
test("본문 편집은 HTML을 리치 에디터로 전달하고 일반 입력창에 태그를 노출하지 않음", () => {
  const h = harness();
  const fn = h.load("components/keep/KeepInlineEditor.tsx").KeepInlineEditor;
  const { keepEditDraft } = h.load("utils/keepEdit.ts");
  const item = { ...FIXTURE_DETAIL, content: FIXTURE_HTML };
  const tree = h.render(fn, { item, draft: keepEditDraft(item), projects: [], prefixes: [], tagMap: {},
    active: "content", disabled: false, onOpen() {}, onChange() {} });
  const row = [...walk(tree)].find((n) => n.props.field === "content");
  const panel = h.render(row.type, row.props);
  const rich = [...walk(panel)].find((n) => n.type?.name === "KeepRichEditor");
  assert.equal(rich.props.value, FIXTURE_HTML);
  assert.equal([...walk(panel)].some((n) => n.type === "TextInput"), false);
});

test("본문 DOM 정규화는 미편집 원문을 보존하고 최신 편집 HTML을 읽어 저장", async () => {
  const h = harness();
  const fn = h.load("components/keep/KeepRichEditor.tsx").KeepRichEditor;
  const reader = { current: null };
  const changed = [];
  let html = FIXTURE_HTML.replace("<strong>", "<b>").replace("</strong>", "</b>");
  const initialDOM = html;
  const props = { value: FIXTURE_HTML, reader, onChange: (value) => changed.push(value), onPending() {}, onError() {} };
  const tree = h.render(fn, props);
  const editor = [...walk(tree)].find((n) => n.type === "RichEditor");
  editor.props.ref.current = { getContentHtml: async () => html, sendAction() {} };
  h.effect();
  await editor.props.editorInitializedCallback();
  await flush();
  assert.equal(await reader.current(), FIXTURE_HTML);
  assert.equal(changed.at(-1), FIXTURE_HTML);
  html = initialDOM.replace("가상 ", "가상 수정 ");
  assert.equal(await reader.current(), html);
  assert.ok(changed.at(-1).includes("<img"));
  assert.ok(changed.at(-1).includes("<table"));
  assert.match(editor.props.html, /Content-Security-Policy/);
  assert.match(editor.props.html, /<script nonce=/);
  h.unmount();
  assert.equal(reader.current, null);
});

test("에디터 마지막 입력은 접기·저장 전에 확인하고 확인 실패는 저장하지 않음", async () => {
  const h = harness();
  const fn = h.load("screens/KeepDetailScreen.tsx").KeepDetailScreen;
  const props = { route: { params: { id: FIXTURE_ID, item: { ...FIXTURE_DETAIL, content: FIXTURE_HTML } } },
    navigation: { goBack() {}, dispatch() {} } };
  const screen = () => h.render(fn, props);
  screen(); h.effect(); await flush();
  let tree = screen();
  let inline = [...walk(tree)].find((n) => n.type?.name === "KeepInlineEditor");
  inline.props.onOpen("content"); await flush();
  tree = screen(); inline = [...walk(tree)].find((n) => n.type?.name === "KeepInlineEditor");
  inline.props.onBodyPending(true);
  inline.props.bodyReader.current = async () => { throw new Error("가상 에디터 오류"); };
  tree = screen();
  const save = [...walk(tree)].find((n) => n.props.accessibilityLabel === "Keep 변경 저장");
  save.props.onPress(); await flush();
  assert.equal(h.calls.some((c) => c.name === "modifyKeep"), false);
  const latest = FIXTURE_HTML.replace("가상 ", "가상 마지막 입력 ");
  inline.props.bodyReader.current = async () => latest;
  save.props.onPress(); await flush();
  assert.equal(h.calls.find((c) => c.name === "modifyKeep").args[1].content, latest);
});