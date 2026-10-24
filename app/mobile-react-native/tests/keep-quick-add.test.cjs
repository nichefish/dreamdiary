const { test, describe } = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const ts = require("typescript");

/** 기준 시각 — 로컬(KST) 2001-10-11 정오 */
const FIXTURE_NOW = Date.parse("2001-10-11T03:00:00.000Z");
const FIXTURE_TITLE = "샘플 메모";
const FIXTURE_ERROR = "샘플 등록 오류";
const FIXTURE_NEW_ID = 901;
const FIXTURE_PROJECT = { id: 51, title: "샘플 Project", lifecycle: { lifecycleKey: "OPEN" } };
const FIXTURE_PREFIX = { id: 61, name: "샘플말머리", sortOrder: 1, textClass: "text-primary" };
const FIXTURE_LIST = [{ id: 201, title: `${FIXTURE_TITLE} 201`, lifecycle: { lifecycleKey: "OPEN" } }];
const FIXTURE_CREATED_LIST = [...FIXTURE_LIST, { id: FIXTURE_NEW_ID, title: `${FIXTURE_TITLE} 새`, lifecycle: { lifecycleKey: "OPEN" } }];

/** 인자 없는 생성만 기준 시각으로 고정하고 년·월·일 생성은 그대로 둔다. */
class FixedDate extends Date {
  constructor(...args) { if (args.length === 0) super(FIXTURE_NOW); else super(...args); }
  static now() { return FIXTURE_NOW; }
}

/** 가벼운 React 훅·네이티브 표면으로 실제 TS/TSX 모듈을 읽는다. */
function loader(mocks) {
  const cache = new Map();
  function load(file) {
    if (cache.has(file)) return cache.get(file).exports;
    const module = { exports: {} };
    cache.set(file, module);
    const compiled = ts.transpileModule(fs.readFileSync(file, "utf8"), { compilerOptions: {
      module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022, jsx: ts.JsxEmit.ReactJSX
    } }).outputText;
    vm.runInNewContext(compiled, {
      module, exports: module.exports, Date: FixedDate, Error, console: { info() {}, warn() {}, error() {} },
      require(name) {
        if (name in mocks) return mocks[name];
        if (!name.startsWith(".")) throw new Error("Unexpected module: " + name);
        const base = path.resolve(path.dirname(file), name);
        return load(fs.existsSync(base + ".ts") ? base + ".ts" : base + ".tsx");
      }
    });
    return module.exports;
  }
  return load;
}

function hooks() {
  const slots = [];
  let cursor = 0;
  const focused = [];
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
  return { react, focused, reset() { cursor = 0; focused.length = 0; } };
}

const jsx = (type, props, key) => ({ type, props, key });
/** vm 영역에서 만든 객체는 프로토타입이 달라 값만 비교한다. */
function sameValue(actual, expected) { assert.deepEqual(JSON.parse(JSON.stringify(actual)), expected); }
const nativeSurface = {
  ActivityIndicator: "ActivityIndicator", FlatList: "FlatList", KeyboardAvoidingView: "KeyboardAvoidingView", Modal: "Modal",
  Platform: { OS: "android" }, Pressable: "Pressable", RefreshControl: "RefreshControl", ScrollView: "ScrollView", Switch: "Switch",
  Text: "Text", TextInput: "TextInput", View: "View", AppState: { addEventListener: () => ({ remove() {} }) },
  StyleSheet: { create: (styles) => styles, absoluteFillObject: {} }
};

/** 실제 KeepHomeScreen을 가상 API로 렌더한다. 빠른 추가 시트는 표지 요소로 두고 props를 검사한다. */
function screenHarness(apiOverrides = {}, screenProps = {}) {
  const h = hooks();
  const briefs = [];
  const load = loader({
    react: h.react, "react/jsx-runtime": { jsx, jsxs: jsx, Fragment: "Fragment" },
    "react-native": nativeSurface,
    "react-native-safe-area-context": { SafeAreaView: "SafeAreaView" },
    "@react-navigation/native": { useNavigation: () => ({ navigate() {} }), useFocusEffect: (callback) => { h.focused.push(callback); } },
    "../api/dreamDiaryApi": { getKeepList: async () => FIXTURE_LIST, getKeepProjects: async () => [FIXTURE_PROJECT], getKeepTags: async () => [],
      getKeepPrefixOptions: async () => [FIXTURE_PREFIX], setKeepLifecycle: async () => {}, toggleKeepState: async () => {},
      createKeep: async () => ({ id: FIXTURE_NEW_ID }), getKeepTagCategoryMap: async () => ({}), ...apiOverrides },
    "../api/client": { isUnauthorizedError: (error) => error?.status === 401 },
    "../navigation/appMenu": { MENU_URL: { KEEP: "/keep" } },
    "../components/navigation/AppMenu": { MenuScreenHeader: "MenuHeader" },
    "../components/journal/JournalCopyShareBar": { JournalCopyShareBar: "CopyBar", notifyBrief(message) { briefs.push(message); } },
    "../components/keep/KeepBadges": { KeepLifecycleBadge: "LifecycleBadge", KeepPrefixText: "Prefix", KeepStateBadge: "StateBadge", KeepTagChips: "Tags" },
    "../components/keep/KeepFilterSheet": { KeepFilterSheet: "FilterSheet" },
    "../components/keep/KeepSwipeRow": { KeepSwipeRow: "SwipeRow" },
    "../components/keep/KeepLifecycleMenu": { KeepLifecycleMenu: "LifecycleMenu" },
    "../components/keep/KeepProjectMoveSheet": { KeepProjectMoveSheet: "MoveSheet" },

    "../components/keep/useKeepEditSheet": { useKeepEditSheet: () => ({ openEdit: async () => {}, loading: false, savingRef: { current: false }, sheetProps: { visible: false, projects: [], prefixes: [], defaultProjectId: null, initial: null, tagMap: {}, onSubmit: async () => ({ ok: false, message: null }), onClose() {} } }) },
    "../components/keep/KeepQuickAddSheet": { KeepQuickAddSheet: "QuickAddSheet" }
  });
  const { KeepHomeScreen } = load(path.resolve(__dirname, "../src/screens/KeepHomeScreen.tsx"));
  return {
    briefs,
    render() { h.reset(); return KeepHomeScreen(screenProps); },
    focus() { for (const callback of [...h.focused]) callback(); }
  };
}

function* walk(node) {
  if (Array.isArray(node)) { for (const child of node) yield* walk(child); }
  else if (node && typeof node === "object") { yield node; yield* walk(node.props?.children); }
}
async function settle() { for (let index = 0; index < 15; index++) await Promise.resolve(); }
async function loadedScreen(apiOverrides) {
  const screen = screenHarness(apiOverrides);
  screen.render();
  screen.focus();
  await settle();
  return screen;
}
/** + 버튼으로 시트를 연 화면 */
async function openedScreen(apiOverrides) {
  const screen = await loadedScreen(apiOverrides);
  fabOf(screen.render()).props.onPress();
  assert.equal(sheetOf(screen.render()).visible, true);
  return screen;
}
/** 빠른 추가 시트 (수정 시트는 initial 을 넘기므로 제외) */
function sheetOf(tree) { return [...walk(tree)].find((node) => node.type === "QuickAddSheet" && node.props.initial === undefined).props; }
function fabOf(tree) { return [...walk(tree)].find((node) => node.props?.accessibilityLabel === "Keep 빠른 추가"); }
const INPUT = { title: FIXTURE_TITLE, dateText: "10/24", expireDt: "2001-10-24", projectId: null, important: false };

describe("KeepHomeScreen 빠른 추가", () => {
  test("+ 버튼이 시트를 열고 기본 Project는 현재 칩(전체는 미지정)", async () => {
    const screen = await loadedScreen();
    let tree = screen.render();
    assert.equal(sheetOf(tree).visible, false);
    assert.equal(sheetOf(tree).defaultProjectId, null);
    sameValue(sheetOf(tree).projects.map((p) => p.id), [FIXTURE_PROJECT.id]);
    sameValue(sheetOf(tree).prefixes.map((p) => p.id), [FIXTURE_PREFIX.id]);
    fabOf(tree).props.onPress();
    tree = screen.render();
    assert.equal(sheetOf(tree).visible, true);
    const header = [...walk(tree)].find((node) => node.type === "FlatList").props.ListHeaderComponent;
    const projectChip = [...walk(header)].find((node) => node.type === "Pressable" && JSON.stringify(node.props.children).includes(FIXTURE_PROJECT.title));
    projectChip.props.onPress();
    assert.equal(sheetOf(screen.render()).defaultProjectId, FIXTURE_PROJECT.id);
    sheetOf(screen.render()).onClose();
    assert.equal(sheetOf(screen.render()).visible, false);
  });

  test("저장 성공은 등록 POST 한 번·Keep 목록 재조회 한 번 뒤 시트를 닫고 등록 안내(연속 입력 없음)", async () => {
    const calls = [];
    let reads = 0;
    const screen = await openedScreen({
      createKeep: async (input) => { calls.push(["create", input]); return { id: FIXTURE_NEW_ID }; },
      toggleKeepState: async () => { calls.push(["toggle"]); },
      getKeepList: async () => ++reads === 1 ? FIXTURE_LIST : FIXTURE_CREATED_LIST
    });
    const result = await sheetOf(screen.render()).onSubmit(INPUT);
    await settle();
    sameValue(result, { ok: true, id: FIXTURE_NEW_ID, notice: null });
    sameValue(calls.map((c) => c[0]), ["create"]);
    assert.equal(calls[0][1].title, FIXTURE_TITLE);
    assert.equal(calls[0][1].expireDt, "2001-10-24");
    assert.equal(reads, 2);
    const list = [...walk(screen.render())].find((node) => node.type === "FlatList").props;
    assert.equal(list.data.some((row) => row.item?.id === FIXTURE_NEW_ID), true);
    assert.equal(sheetOf(screen.render()).visible, false);
    sameValue(screen.briefs, ["Keep을 등록했습니다."]);
  });

  test("중요를 켜면 등록 뒤 새 ID로 IMPRTC 토글을 한 번 보내고 목록을 조회", async () => {
    const calls = [];
    const screen = await loadedScreen({
      createKeep: async () => { calls.push("create"); return { id: FIXTURE_NEW_ID }; },
      toggleKeepState: async (id, key) => { calls.push(`toggle:${id}:${key}`); },
      getKeepList: async () => { calls.push("list"); return FIXTURE_CREATED_LIST; }
    });
    calls.length = 0;
    const result = await sheetOf(screen.render()).onSubmit({ ...INPUT, important: true });
    assert.equal(result.ok, true);
    sameValue(calls, ["create", `toggle:${FIXTURE_NEW_ID}:IMPRTC`, "list"]);
  });

  test("중요 토글 실패는 재전송 없이 등록 성공 — 시트를 닫고 목록에서 안내", async () => {
    let toggles = 0;
    let reads = 0;
    const screen = await openedScreen({
      toggleKeepState: async () => { toggles++; throw Object.assign(new Error(FIXTURE_ERROR), { status: 500 }); },
      getKeepList: async () => { reads++; return FIXTURE_CREATED_LIST; }
    });
    const result = await sheetOf(screen.render()).onSubmit({ ...INPUT, important: true });
    assert.equal(result.ok, true);
    assert.match(result.notice, /중요 표시를 하지 못했습니다/);
    assert.equal(toggles, 1);
    assert.equal(reads, 2);
    assert.equal(sheetOf(screen.render()).visible, false);
    assert.equal(screen.briefs.length, 1);
    assert.match(screen.briefs[0], /^Keep을 등록했습니다\. .*중요 표시를 하지 못했습니다/);
  });

  test("저장 중 연타·다른 쓰기는 잠그고 등록 POST는 한 번", async () => {
    let creates = 0;
    let release;
    const gate = new Promise((done) => { release = done; });
    const screen = await loadedScreen({ createKeep: async () => { creates++; await gate; return { id: FIXTURE_NEW_ID }; } });
    const sheet = sheetOf(screen.render());
    const first = sheet.onSubmit(INPUT);
    const second = await sheet.onSubmit(INPUT);
    assert.equal(second.ok, false);
    assert.match(second.message, /다른 요청/);
    release();
    assert.equal((await first).ok, true);
    assert.equal(creates, 1);
  });

  test("서버 오류는 서버 메시지를 돌려주고 시트를 열어 둠, 중요 토글·목록 재조회 없음", async () => {
    let toggles = 0;
    let reads = 0;
    const screen = await openedScreen({
      createKeep: async () => { throw Object.assign(new Error(FIXTURE_ERROR), { status: 400 }); },
      toggleKeepState: async () => { toggles++; },
      getKeepList: async () => { reads++; return FIXTURE_LIST; }
    });
    const result = await sheetOf(screen.render()).onSubmit({ ...INPUT, important: true });
    sameValue(result, { ok: false, message: FIXTURE_ERROR });
    assert.equal(toggles, 0);
    assert.equal(reads, 1);
    assert.equal(sheetOf(screen.render()).visible, true);
    sameValue(screen.briefs, []);
  });

  test("응답 없는 전송 오류는 재전송하지 않고 목록을 다시 받은 뒤 시트를 닫고 확인 안내", async () => {
    let creates = 0;
    let reads = 0;
    const screen = await openedScreen({
      createKeep: async () => { creates++; throw new Error(FIXTURE_ERROR); },
      getKeepList: async () => { reads++; return FIXTURE_LIST; }
    });
    const result = await sheetOf(screen.render()).onSubmit(INPUT);
    sameValue(result, { ok: false, message: null });
    assert.equal(creates, 1);
    assert.equal(reads, 2);
    assert.equal(sheetOf(screen.render()).visible, false);
    assert.equal(screen.briefs.length, 1);
    assert.equal(screen.briefs[0].startsWith(FIXTURE_ERROR), true);
    assert.match(screen.briefs[0], /목록에서 확인/);
  });

  test("401은 시트 메시지 없이 공통 로그아웃 처리에 맡김", async () => {
    const screen = await loadedScreen({ createKeep: async () => { throw Object.assign(new Error(FIXTURE_ERROR), { status: 401 }); } });
    sameValue(await sheetOf(screen.render()).onSubmit(INPUT), { ok: false, message: null });
  });

  test("등록 후 목록 조회 실패는 등록 성공 — 시트를 닫고 목록 다시 조회 안내(쓰기 잠금)", async () => {
    let reads = 0;
    const screen = await openedScreen({
      getKeepList: async () => { reads++; if (reads === 2) throw new Error(FIXTURE_ERROR); return FIXTURE_CREATED_LIST; }
    });
    const result = await sheetOf(screen.render()).onSubmit(INPUT);
    assert.equal(result.ok, true);
    assert.match(result.notice, /목록을 새로 받지 못했습니다/);
    const header = [...walk(screen.render())].find((node) => node.type === "FlatList").props.ListHeaderComponent;
    assert.equal(JSON.stringify(header).includes("목록 다시 조회"), true);
    assert.equal(sheetOf(screen.render()).visible, false);
    assert.match(screen.briefs[0], /^Keep을 등록했습니다\. .*목록을 새로 받지 못했습니다/);
  });
});

/** 실제 KeepQuickAddSheet를 렌더한다. */
function sheetHarness(props = {}) {
  const h = hooks();
  const load = loader({
    react: h.react, "react/jsx-runtime": { jsx, jsxs: jsx, Fragment: "Fragment" },
    "react-native": nativeSurface,
    "react-native-safe-area-context": { useSafeAreaInsets: () => ({ top: 0, bottom: 0, left: 0, right: 0 }) }
  });
  const { KeepQuickAddSheet } = load(path.resolve(__dirname, "../src/components/keep/KeepQuickAddSheet.tsx"));
  const submitted = [];
  const full = {
    visible: true, projects: [FIXTURE_PROJECT], prefixes: [FIXTURE_PREFIX], defaultProjectId: null, onClose() {},
    onSubmit: async (input) => { submitted.push(input); return { ok: true, id: FIXTURE_NEW_ID, notice: null }; },
    ...props
  };
  return { render() { h.reset(); return KeepQuickAddSheet(full); }, submitted };
}
function input(tree, label) { return [...walk(tree)].find((node) => node.type === "TextInput" && node.props.accessibilityLabel === label).props; }
function button(tree, label) { return [...walk(tree)].find((node) => node.type === "Pressable" && node.props.accessibilityLabel === label)?.props; }
function texts(tree) { return JSON.stringify(tree); }

describe("KeepQuickAddSheet", () => {
  test("일시 입력은 가장 늦은 날짜를 기한으로 채우고 남은 일수를 안내", () => {
    const sheet = sheetHarness();
    input(sheet.render(), "일시").onChangeText("10/17,18 · 10/24 19:30");
    const tree = sheet.render();
    assert.equal(input(tree, "기한").value, "2001-10-24");
    assert.match(texts(tree), /기한 2001-10-24 · 13일 남음/);
  });

  test("기한을 직접 고친 뒤에는 일시를 고쳐도 덮어쓰지 않고 기한 추출로만 다시 뽑음", () => {
    const sheet = sheetHarness();
    input(sheet.render(), "일시").onChangeText("10/24");
    input(sheet.render(), "기한").onChangeText("10/20");
    input(sheet.render(), "일시").onChangeText("10/24 · 10/30");
    let tree = sheet.render();
    assert.equal(input(tree, "기한").value, "10/20");
    assert.match(texts(tree), /기한 2001-10-20/);
    button(tree, "기한 추출").onPress();
    assert.equal(input(sheet.render(), "기한").value, "2001-10-30");
    button(sheet.render(), "기한 비우기").onPress();
    tree = sheet.render();
    assert.equal(input(tree, "기한").value, "");
    assert.match(texts(tree), /일시에서 찾은 날짜: 2001-10-24, 2001-10-30/);
  });

  test("빈 제목·해석 불가 기한은 저장 요청 없이 안내", async () => {
    const sheet = sheetHarness();
    await button(sheet.render(), "Keep 저장").onPress();
    await settle();
    assert.match(texts(sheet.render()), /제목을 입력해 주세요/);
    input(sheet.render(), "제목").onChangeText(FIXTURE_TITLE);
    input(sheet.render(), "기한").onChangeText("2/30");
    await button(sheet.render(), "Keep 저장").onPress();
    await settle();
    assert.match(texts(sheet.render()), /기한을 해석할 수 없습니다/);
    assert.equal(sheet.submitted.length, 0);
  });

  test("저장은 절대 기한·말머리·Project·중요를 보내고, 성공 뒤 연속 입력 안내를 띄우지 않음(닫기는 화면 몫)", async () => {
    const sheet = sheetHarness({ defaultProjectId: FIXTURE_PROJECT.id });
    input(sheet.render(), "제목").onChangeText(`  ${FIXTURE_TITLE}  `);
    input(sheet.render(), "일시").onChangeText("10/24 19:30");
    button(sheet.render(), `말머리 ${FIXTURE_PREFIX.name}`).onPress();
    [...walk(sheet.render())].find((node) => node.type === "Switch").props.onValueChange(true);
    input(sheet.render(), "제목").onSubmitEditing();
    await settle();
    sameValue(sheet.submitted, [{ title: FIXTURE_TITLE, dateText: "10/24 19:30", expireDt: "2001-10-24", projectId: FIXTURE_PROJECT.id,
      prefixId: FIXTURE_PREFIX.id, tags: [], important: true }]);
    const tree = sheet.render();
    assert.doesNotMatch(texts(tree), /이어서 입력/);
    assert.equal(button(tree, "Keep 저장").disabled, false);
  });

  test("말머리 칩: 기본 없음, 활성 말머리는 [이름]·textClass 색, 목록이 비면 줄을 숨김", async () => {
    const sheet = sheetHarness();
    let tree = sheet.render();
    assert.equal(button(tree, "말머리 없음").accessibilityState.selected, true);
    const chip = button(tree, `말머리 ${FIXTURE_PREFIX.name}`);
    assert.equal(chip.accessibilityState.selected, false);
    const label = [...walk(chip.children)].find((node) => node.type === "Text");
    assert.equal(label.props.children, `[${FIXTURE_PREFIX.name}]`);
    assert.equal(JSON.stringify(label.props.style).includes('"color":"'), true);
    input(tree, "제목").onChangeText(FIXTURE_TITLE);
    await button(sheet.render(), "Keep 저장").onPress();
    await settle();
    assert.equal(sheet.submitted[0].prefixId, null);

    const empty = sheetHarness({ prefixes: [] });
    tree = empty.render();
    assert.equal(button(tree, "말머리 없음"), undefined);
    assert.doesNotMatch(texts(tree), /"말머리"/);
  });

  test("저장 중 연타는 한 번만 보내고 버튼·닫기를 잠금, 실패는 입력 보존 + 메시지", async () => {
    let release;
    const gate = new Promise((done) => { release = done; });
    let closes = 0;
    const sheet = sheetHarness({
      onClose() { closes++; },
      onSubmit: async (value) => { sheet.submitted.push(value); await gate; return { ok: false, message: FIXTURE_ERROR }; }
    });
    input(sheet.render(), "제목").onChangeText(FIXTURE_TITLE);
    const save = button(sheet.render(), "Keep 저장");
    save.onPress();
    save.onPress();
    let tree = sheet.render();
    assert.equal(button(tree, "Keep 저장").disabled, true);
    button(tree, "빠른 추가 닫기").onPress();
    assert.equal(closes, 0);
    release();
    await settle();
    tree = sheet.render();
    assert.equal(sheet.submitted.length, 1);
    assert.equal(input(tree, "제목").value, FIXTURE_TITLE);
    assert.match(texts(tree), new RegExp(FIXTURE_ERROR));
    button(tree, "빠른 추가 닫기").onPress();
    assert.equal(closes, 1);
  });
});

describe("keepQuickAdd 규칙", () => {
  const load = loader({});
  const quick = load(path.resolve(__dirname, "../src/utils/keepQuickAdd.ts"));
  test("기본 Project는 Project 칩일 때만 그 ID", () => {
    assert.equal(quick.quickAddDefaultProjectId("ALL"), null);
    assert.equal(quick.quickAddDefaultProjectId("UNASSIGNED"), null);
    assert.equal(quick.quickAddDefaultProjectId(7), 7);
  });
  test("기한 안내: 오늘·지남·해석 불가·빈 일시", () => {
    const today = new FixedDate();
    sameValue(quick.quickAddExpireHint("10/11", "", today), { text: "기한 2001-10-11 · 오늘", tone: "danger" });
    assert.equal(quick.quickAddExpireHint("2001-10-01", "", today).tone, "warning");
    assert.equal(quick.quickAddExpireHint("13/45", "", today).tone, "error");
    assert.equal(quick.quickAddExpireHint("", "", today).tone, "muted");
  });
  test("검사: 100자 경계·200자 일시·공백 제목", () => {
    const today = new FixedDate();
    const base = { title: "가".repeat(100), dateText: "", expireInput: "", projectId: null, important: false };
    assert.equal(quick.validateQuickAdd(base, today).ok, true);
    assert.equal(quick.validateQuickAdd({ ...base, title: "가".repeat(101) }, today).ok, false);
    assert.equal(quick.validateQuickAdd({ ...base, title: "   " }, today).ok, false);
    assert.equal(quick.validateQuickAdd({ ...base, dateText: "x".repeat(201) }, today).ok, false);
  });
  test("실패 메시지: 상태 있는 서버 오류는 그대로, 전송 오류는 확인 안내", () => {
    assert.equal(quick.quickAddFailureMessage(Object.assign(new Error(FIXTURE_ERROR), { status: 400 })), FIXTURE_ERROR);
    assert.match(quick.quickAddFailureMessage(new Error(FIXTURE_ERROR)), /목록에서 확인/);
  });
});

/** 가상 알림 진입은 전 Project 최신 대상 조회를 사용하고 완료 후 같은 범위를 재평가한다. */
test("알림 대상 목록은 서버 판정 결과를 조회하고 완료 후 사라진 대상을 반영", async () => {
  const calls = [];
  let ended = false;
  const target = { ...FIXTURE_LIST[0], projectId: FIXTURE_PROJECT.id };
  const screen = screenHarness({
    getKeepList: async () => { throw new Error("Unexpected whole-list read"); },
    getKeepReminderList: async (date, slot) => { calls.push([date, slot]); return ended ? [] : [target]; },
    setKeepLifecycle: async () => { ended = true; }
  }, { route: { params: { reminder: { localDate: "2001-10-24", slot: "MORNING" } } } });
  screen.render();
  screen.focus();
  await settle();
  let tree = screen.render();
  const listOf = (value) => [...walk(value)].find((node) => node.type === "FlatList").props;
  const row = listOf(tree).data.find((entry) => entry.kind === "item");
  assert.equal(row.item.id, target.id);
  const rendered = listOf(tree).renderItem({ item: row });
  const swipe = rendered.type === "SwipeRow" ? rendered : [...walk(rendered)].find((node) => node.type === "SwipeRow");
  swipe.props.onComplete();
  await settle();
  tree = screen.render();
  assert.equal(listOf(tree).data.filter((entry) => entry.kind === "item").length, 0);
  assert.match(JSON.stringify(listOf(tree).ListEmptyComponent), /현재 남아있는 알림 대상/);
  sameValue(calls, [["2001-10-24", "MORNING"], ["2001-10-24", "MORNING"]]);
});