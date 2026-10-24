const test = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const ts = require("typescript");

const FIXTURE_DATE = "2001-01-02";
const FIXTURE_OTHER_DATE = "2001-01-03";
const FIXTURE_DAY = { id: 1, journalDate: FIXTURE_DATE, journalChapterList: [] };

/** 기존 TypeScript 모듈을 격리하여 네트워크·인증 의존성만 대체한다. */
function loadModule(relativePath, dependencies) {
  const source = fs.readFileSync(path.join(__dirname, "../src", relativePath), "utf8");
  const compiled = ts.transpileModule(source, {
    compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022, jsx: ts.JsxEmit.ReactJSX }
  }).outputText;
  const module = { exports: {} };
  const quietConsole = { info() {}, warn() {}, error() {} };
  vm.runInNewContext(compiled, {
    module, exports: module.exports, FormData, Error, console: quietConsole,
    require(name) {
      if (!(name in dependencies)) throw new Error("Unexpected dependency: " + name);
      return dependencies[name];
    }
  });
  return module.exports;
}

/** 제어된 응답 순서로 훅의 상태를 렌더하며 실제 API나 개인정보를 사용하지 않는다. */
function hookHarness(request) {
  const slots = [];
  let cursor = 0;
  const react = {
    useState(initial) {
      const index = cursor++;
      if (!(index in slots)) slots[index] = initial;
      return [slots[index], (next) => { slots[index] = typeof next === "function" ? next(slots[index]) : next; }];
    },
    useRef(initial) {
      const index = cursor++;
      if (!(index in slots)) slots[index] = { current: initial };
      return slots[index];
    },
    useCallback(callback) { return callback; },
    useEffect() {}
  };
  const { useJournalDay } = loadModule("hooks/useJournalDay.ts", {
    react,
    "@react-navigation/native": { useFocusEffect() {} },
    "../api/dreamDiaryApi": { getDailyJournalDay: request },
    "../api/client": { isUnauthorizedError: (e) => e.status === 401 },
    "../types/journalDay": {
      dayHasEntries: () => false,
      dreamEntriesFromDay: () => []
    }
  });
  return (date = FIXTURE_DATE) => {
    cursor = 0;
    return useJournalDay(date, { refreshOnFocus: false });
  };
}

function deferred() {
  let resolve, reject;
  const promise = new Promise((yes, no) => { resolve = yes; reject = no; });
  return { promise, resolve, reject };
}

test("초기 상태와 조회 실패는 등록 가능한 부재로 처리하지 않는다", async () => {
  const render = hookHarness(async () => ({ rslt: false, message: "가상 조회 실패" }));
  assert.equal(render().canRegister, false);
  await render().load();
  const result = render();
  assert.equal(result.loading, false);
  assert.equal(result.error, "가상 조회 실패");
  assert.equal(result.canRegister, false);
});

test("조회 성공의 빈 일자만 등록 가능하며 엔트리 없는 기존 일자는 제외한다", async () => {
  for (const [rsltList, expected] of [[[], true], [[FIXTURE_DAY], false]]) {
    const render = hookHarness(async () => ({ rslt: true, rsltList }));
    await render().load();
    assert.equal(render().canRegister, expected);
  }
});

test("날짜 전환 직후 이전 날짜의 빈 결과로 등록 버튼을 표시하지 않는다", async () => {
  const render = hookHarness(async () => ({ rslt: true, rsltList: [] }));
  await render().load();
  assert.equal(render().canRegister, true);
  const changed = render(FIXTURE_OTHER_DATE);
  assert.equal(changed.canRegister, false);
  assert.equal(changed.loading, true);
  assert.equal(changed.day, null);
});

test("이전 날짜의 늦은 응답과 오류가 현재 날짜의 일자를 덮어쓰지 않는다", async () => {
  for (const fail of [false, true]) {
    const older = deferred();
    const render = hookHarness((date) => date === FIXTURE_DATE
      ? older.promise : Promise.resolve({ rslt: true, rsltList: [{ ...FIXTURE_DAY, journalDate: FIXTURE_OTHER_DATE }] }));
    const pending = render().load();
    await render(FIXTURE_OTHER_DATE).load();
    if (fail) older.reject(new Error("가상 이전 날짜 오류"));
    else older.resolve({ rslt: true, rsltList: [] });
    await pending;
    const current = render(FIXTURE_OTHER_DATE);
    assert.equal(current.day.journalDate, FIXTURE_OTHER_DATE);
    assert.equal(current.canRegister, false);
    assert.equal(current.error, null);
  }
});

test("같은 날짜에서도 최신 요청을 우선하고 진행 중에는 등록을 잠근다", async () => {
  const older = deferred();
  let calls = 0;
  const render = hookHarness(() => ++calls === 1 ? older.promise : Promise.resolve({ rslt: true, rsltList: [FIXTURE_DAY] }));
  const pending = render().load();
  assert.equal(render().canRegister, false);
  await render().load(true);
  older.resolve({ rslt: true, rsltList: [] });
  await pending;
  assert.equal(render().day.id, FIXTURE_DAY.id);
  assert.equal(render().canRegister, false);
});

test("조용한 새로고침은 현재 데이터를 유지하고 등록 버튼만 잠근다", async () => {
  const next = deferred();
  let calls = 0;
  const render = hookHarness(() => ++calls === 1 ? Promise.resolve({ rslt: true, rsltList: [] }) : next.promise);
  await render().load();
  const pending = render().load(true);
  assert.equal(render().loading, false);
  assert.equal(render().canRegister, false);
  next.resolve({ rslt: true, rsltList: [] });
  await pending;
  assert.equal(render().canRegister, true);
});

function registrationApi(request) {
  class ApiError extends Error { constructor(message, status) { super(message); this.status = status; } }
  const dates = loadModule("utils/date.ts", {});
  return loadModule("api/dreamDiaryApi.ts", {
    "./client": { request, ApiError },
    "../utils/date": dates,
    // Keep 등록 FormData 변환은 저널 일자 등록 경로에서 쓰지 않는다 (dreamDiaryApi 가 import 만 한다)
    "../utils/keepFormData": { toKeepFormData() { throw new Error("unused in journal day registration"); }, toKeepEditFormData() { throw new Error("unused in journal day registration"); } },
    "../utils/keepTags": { normalizeKeepTagCategoryMap: (raw) => raw ?? {} }
  }).registerJournalDay;
}

test("등록은 날짜·정밀도·날씨만 multipart로 전송한다", async () => {
  let captured;
  const register = registrationApi(async (url, options) => {
    captured = { url, options };
    return { rslt: true };
  });
  await register(FIXTURE_DATE, "맑음");
  assert.equal(captured.url, "/api/journal/days");
  assert.equal(captured.options.method, "POST");
  assert.ok(captured.options.body instanceof FormData);
  assert.deepEqual(Object.fromEntries(captured.options.body.entries()), {
    journalDate: FIXTURE_DATE, journalDatePrecision: "EXACT", weather: "맑음"
  });
});

test("중복 업무 실패·HTTP 오류·401을 성공으로 처리하지 않는다", async () => {
  const duplicate = registrationApi(async () => ({ rslt: false, message: "가상 중복 일자" }));
  await assert.rejects(duplicate(FIXTURE_DATE, ""), /가상 중복 일자/);
  for (const status of [401, 500]) {
    const error = Object.assign(new Error("가상 HTTP 오류"), { status });
    const register = registrationApi(async () => { throw error; });
    await assert.rejects(register(FIXTURE_DATE, ""), (caught) => caught === error);
  }
});

test("유효하지 않은 날짜와 긴 날씨 입력은 전송 전에 거부한다", async () => {
  let calls = 0;
  const register = registrationApi(async () => { calls++; return { rslt: true }; });
  await assert.rejects(register("2001-02-30", ""), /날짜/);
  await assert.rejects(register(FIXTURE_DATE, "a".repeat(101)), /100자/);
  assert.equal(calls, 0);
});

/** 실제 모달의 이벤트를 실행하되 네이티브 렌더링과 API 쓰기는 격리한다. */
function modalHarness(register) {
  const slots = [];
  let cursor = 0;
  const counts = { closed: 0, refreshed: 0 };
  const react = {
    useState(initial) {
      const index = cursor++;
      if (!(index in slots)) slots[index] = initial;
      return [slots[index], (next) => { slots[index] = next; }];
    },
    useRef(initial) {
      const index = cursor++;
      if (!(index in slots)) slots[index] = { current: initial };
      return slots[index];
    }
  };
  const jsx = (type, props) => ({ type, props });
  const native = Object.fromEntries(["ActivityIndicator", "KeyboardAvoidingView", "Modal", "Pressable", "ScrollView", "Text", "TextInput", "View"].map((name) => [name, name]));
  native.Platform = { OS: "android" };
  native.StyleSheet = { create: (styles) => styles };
  const { JournalDayRegistModal } = loadModule("components/journal/JournalDayRegistModal.tsx", {
    react,
    "react/jsx-runtime": { jsx, jsxs: jsx },
    "react-native": native,
    "react-native-safe-area-context": { SafeAreaView: "SafeAreaView" },
    "../../api/dreamDiaryApi": { registerJournalDay: register },
    "../../api/client": { isUnauthorizedError: (e) => e.status === 401 },
    "../../theme/colors": { colors: {} },
    "../../utils/date": { formatDateDots: (date) => date.replaceAll("-", ".") }
  });
  return {
    counts,
    render() {
      cursor = 0;
      return JournalDayRegistModal({
        date: FIXTURE_DATE,
        onClose: () => { counts.closed++; },
        onRegistered: async () => { counts.refreshed++; }
      });
    }
  };
}

function elements(tree, predicate) {
  if (Array.isArray(tree)) return tree.flatMap((child) => elements(child, predicate));
  if (!tree || typeof tree !== "object") return [];
  return [
    ...(predicate(tree) ? [tree] : []),
    ...elements(tree.props?.children, predicate)
  ];
}

const nextTurn = () => new Promise((resolve) => setImmediate(resolve));

test("모달은 저장 중 연타·취소·Android 뒤로 닫기를 막고 성공 후만 재조회한다", async () => {
  const response = deferred();
  let calls = 0;
  const modal = modalHarness(() => { calls++; return response.promise; });
  const tree = modal.render();
  const buttons = elements(tree, (element) => element.type === "Pressable");
  buttons[1].props.onPress();
  buttons[1].props.onPress();
  buttons[0].props.onPress();
  tree.props.onRequestClose();
  const pending = modal.render();
  assert.equal(calls, 1);
  assert.equal(modal.counts.closed, 0);
  assert.equal(elements(pending, (element) => element.type === "TextInput")[0].props.editable, false);
  assert.equal(modal.counts.refreshed, 0);
  response.resolve({ rslt: true });
  await nextTurn();
  assert.equal(modal.counts.refreshed, 1);
});

test("모달의 저장 실패는 날씨 입력을 보존하고 서버 오류를 표시한다", async () => {
  const modal = modalHarness(async () => { throw new Error("가상 중복 일자"); });
  elements(modal.render(), (element) => element.type === "TextInput")[0].props.onChangeText("맑음");
  elements(modal.render(), (element) => element.type === "Pressable")[1].props.onPress();
  await nextTurn();
  const failed = modal.render();
  assert.equal(elements(failed, (element) => element.type === "TextInput")[0].props.value, "맑음");
  assert.equal(elements(failed, (element) => element.props?.accessibilityRole === "alert")[0].props.children, "가상 중복 일자");
  assert.equal(modal.counts.refreshed, 0);
});

test("모달 취소는 등록 요청을 보내지 않으며 401은 일반 오류 안내를 띄우지 않는다", async () => {
  let calls = 0;
  const modal = modalHarness(async () => { calls++; throw Object.assign(new Error("가상 인증 만료"), { status: 401 }); });
  const buttons = elements(modal.render(), (element) => element.type === "Pressable");
  buttons[0].props.onPress();
  assert.equal(calls, 0);
  assert.equal(modal.counts.closed, 1);
  buttons[1].props.onPress();
  await nextTurn();
  assert.equal(elements(modal.render(), (element) => element.props?.accessibilityRole === "alert").length, 0);
  assert.equal(modal.counts.refreshed, 0);
});
