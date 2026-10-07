const test = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const ts = require("typescript");

const FIXTURE_ID = 101;
const FIXTURE_ERROR = "샘플 상태 오류";
const FIXTURE_KEEP = { id: FIXTURE_ID, lifecycle: { lifecycleKey: "OPEN" } };

/** 타입만 제거한 실제 모듈을 가상 네이티브 표면에서 실행한다. */
function componentHarness(file, props) {
  const slots = [];
  let cursor = 0;
  const jsx = (type, props) => ({ type, props });
  class Value {
    constructor(value) { this.value = value; }
    setValue(value) { this.value = value; }
    stopAnimation() {}
  }
  const mocks = {
    react: {
      useRef(initial) { const index = cursor++; return slots[index] ?? (slots[index] = { current: initial }); },
      useState(initial) {
        const index = cursor++;
        if (!(index in slots)) slots[index] = initial;
        return [slots[index], (value) => { slots[index] = value; }];
      },
      useEffect(callback) { callback(); }, useMemo(factory) { return factory(); }
    },
    "react/jsx-runtime": { jsx, jsxs: jsx },
    "react-native": {
      View: "View", Text: "Text", Switch: "Switch", ScrollView: "ScrollView", Pressable: "Pressable", Modal: "Modal", ActivityIndicator: "ActivityIndicator",
      StyleSheet: { create: (styles) => styles }, PanResponder: { create: (handlers) => ({ panHandlers: handlers }) },
      Animated: { Value, View: "AnimatedView", timing: (value, options) => ({ start() { value.setValue(options.toValue); } }) }
    },
    "react-native-safe-area-context": { useSafeAreaInsets: () => ({ bottom: 0 }) }
  };
  const cache = new Map();
  function load(file) {
    if (cache.has(file)) return cache.get(file).exports;
    const module = { exports: {} };
    cache.set(file, module);
    const source = ts.transpileModule(fs.readFileSync(file, "utf8"), { compilerOptions: {
      module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022, jsx: ts.JsxEmit.ReactJSX
    } }).outputText;
    vm.runInNewContext(source, {
      module, exports: module.exports, Error, console: { debug() {} },
      require(name) {
        if (name in mocks) return mocks[name];
        if (!name.startsWith(".")) throw new Error("Unexpected module: " + name);
        return load(path.resolve(path.dirname(file), name + ".ts"));
      }
    });
    return module.exports;
  }
  const component = Object.values(load(path.resolve(__dirname, "../src/components/keep", file)))[0];
  return { render(next = props) { cursor = 0; return component(next); } };
}

/** 자식 요소를 화면 순서로 탐색한다. */
function* walk(node) {
  if (Array.isArray(node)) { for (const child of node) yield* walk(child); }
  else if (node && typeof node === "object") { yield node; yield* walk(node.props?.children); }
}

 test("드래그는 가로 왼쪽만 포착하고 완료 요청 없이 버튼만 노출", () => {
  let writes = 0;
  const harness = componentHarness("KeepSwipeRow.tsx", { enabled: true, disabled: false, children: "fixture", onComplete: () => writes++ });
  let tree = harness.render();
  assert.equal(tree.props.onMoveShouldSetPanResponder(null, { dx: -8, dy: 0 }), false);
  assert.equal(tree.props.onMoveShouldSetPanResponder(null, { dx: -20, dy: 40 }), false);
  assert.equal(tree.props.onMoveShouldSetPanResponder(null, { dx: 20, dy: 0 }), false);
  assert.equal(tree.props.onMoveShouldSetPanResponder(null, { dx: -60, dy: 5 }), true);
  tree.props.onPanResponderGrant();
  tree.props.onPanResponderMove(null, { dx: -60, dy: 5 });
  tree.props.onPanResponderRelease(null, { dx: -60, dy: 5 });
  assert.equal(writes, 0);
  tree = harness.render();
  const button = [...walk(tree)].find((node) => node.type === "Pressable");
  assert.equal(button.props.disabled, false);
  button.props.onPress();
  assert.equal(writes, 1);
  assert.equal(tree.props.onMoveShouldSetPanResponder(null, { dx: 60, dy: 0 }), true);
  tree.props.onPanResponderGrant();
  tree.props.onPanResponderRelease(null, { dx: 60, dy: 0 });
  assert.equal([...walk(harness.render())].find((node) => node.type === "Pressable").props.disabled, true);
});

test("종결 행·저장 중에는 드래그를 포착하지 않고 접근성 완료도 잠금", () => {
  let writes = 0;
  for (const [enabled, disabled] of [[false, false], [true, true]]) {
    const harness = componentHarness("KeepSwipeRow.tsx", { enabled, disabled, children: "fixture", onComplete: () => writes++ });
    const tree = harness.render();
    assert.equal(tree.props.onMoveShouldSetPanResponder(null, { dx: -60, dy: 5 }), false);
    [...walk(tree)].find((node) => node.type === "AnimatedView").props.onAccessibilityAction({ nativeEvent: { actionName: "complete" } });
  }
  assert.equal(writes, 0);
});

test("상태 메뉴는 여섯 단계를 표시하고 현재 상태·기한 없는 만료를 선택 불가로 표시", () => {
  const selected = [];
  const harness = componentHarness("KeepLifecycleMenu.tsx", { item: FIXTURE_KEEP, saving: false, error: null,
    onClose() {}, onSelect: (key) => selected.push(key) });
  const options = [...walk(harness.render())].filter((node) => node.props?.accessibilityRole === "radio");
  assert.equal(options.length, 6);
  assert.equal(options[0].props.accessibilityState.checked, true);
  assert.equal(options[0].props.disabled, true);
  assert.equal(options[4].props.disabled, true);
  assert.equal(options[1].props.disabled, false);
  options[1].props.onPress();
  assert.deepEqual(selected, ["PENDING"]);
});

test("저장 중 메뉴 선택·Android 뒤로·배경 닫기를 잠금", () => {
  let closed = 0;
  const harness = componentHarness("KeepLifecycleMenu.tsx", { item: { ...FIXTURE_KEEP, expireDt: "2001-01-02" }, saving: true,
    error: null, onClose: () => closed++, onSelect() {} });
  const tree = harness.render();
  tree.props.onRequestClose();
  const nodes = [...walk(tree)];
  assert.equal(nodes.filter((node) => node.props?.accessibilityRole === "radio").every((node) => node.props.disabled), true);
  nodes.find((node) => node.props?.accessibilityLabel === "상태 메뉴 닫기").props.onPress();
  assert.equal(closed, 0);
});

/** 실제 API 함수의 요청·업무 실패 계약을 네트워크 없이 검증한다. */
function api(request) {
  class ApiError extends Error { constructor(message, status, body) { super(message); this.status = status; this.body = body; } }
  const module = { exports: {} };
  const source = ts.transpileModule(fs.readFileSync(path.resolve(__dirname, "../src/api/dreamDiaryApi.ts"), "utf8"), {
    compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022 }
  }).outputText;
  vm.runInNewContext(source, {
    module, exports: module.exports, Error,
    require(name) {
      if (name === "./client") return { ApiError, request };
      if (name === "../utils/date") return { normalizeDateStr: (value) => value };
      throw new Error("Unexpected API dependency: " + name);
    }
  });
  return module.exports;
}

test("라이프사이클 PUT은 ID·콘텐츠 유형·키만 보내고 완료 시각은 서버에 맡김", async () => {
  const calls = [];
  await api(async (url, options) => { calls.push({ url, options }); return { rslt: true }; }).setKeepLifecycle(FIXTURE_ID, "RESOLVED");
  assert.equal(calls[0].url, "/api/lifecycles");
  assert.equal(calls[0].options.method, "PUT");
  assert.deepEqual(JSON.parse(calls[0].options.body), { id: FIXTURE_ID, contentType: "PROSPECTIVE_KEEP", lifecycleKey: "RESOLVED" });
});

test("HTTP 200 rslt=false는 서버 메시지로 실패 처리하고 전송 오류는 그대로 전파", async () => {
  await assert.rejects(api(async () => ({ rslt: false, message: FIXTURE_ERROR })).setKeepLifecycle(FIXTURE_ID, "PENDING"),
    (error) => error.message === FIXTURE_ERROR && error.status === 200);
  const network = new Error(FIXTURE_ERROR);
  await assert.rejects(api(async () => { throw network; }).setKeepLifecycle(FIXTURE_ID, "PENDING"), (error) => error === network);
});

test("속성 스위치는 서버 현재 값과 독립적인 중요·요구 키를 표시", () => {
  const selected = [];
  const props = { item: { ...FIXTURE_KEEP, state: { list: [{ stateKey: "IMPRTC" }] } },
    saving: false, error: null, onClose() {}, onSelect() {}, onToggleState: (key) => selected.push(key) };
  const harness = componentHarness("KeepLifecycleMenu.tsx", props);
  const switches = [...walk(harness.render())].filter((node) => node.type === "Switch");
  assert.equal(switches.length, 2);
  assert.equal(switches[0].props.value, true);
  assert.equal(switches[1].props.value, false);
  switches[1].props.onValueChange(true);
  assert.deepEqual(selected, ["DEMANDED"]);
  const locked = [...walk(harness.render({ ...props, saving: true }))].filter((node) => node.type === "Switch");
  assert.equal(locked.every((node) => node.props.disabled), true);
  locked[0].props.onValueChange(false);
  assert.deepEqual(selected, ["DEMANDED"]);
});

test("속성 API는 POST 한 번으로 키를 보내며 업무 실패·전송 오류를 전파", async () => {
  const calls = [];
  await api(async (url, options) => { calls.push({ url, options }); return { rslt: true, rsltSts: "ON" }; })
    .toggleKeepState(FIXTURE_ID, "IMPRTC");
  assert.equal(calls.length, 1);
  assert.equal(calls[0].url, "/api/states");
  assert.equal(calls[0].options.method, "POST");
  assert.deepEqual(JSON.parse(calls[0].options.body), { id: FIXTURE_ID, contentType: "PROSPECTIVE_KEEP", stateKey: "IMPRTC" });
  await assert.rejects(api(async () => ({ rslt: false, message: FIXTURE_ERROR })).toggleKeepState(FIXTURE_ID, "DEMANDED"),
    (error) => error.message === FIXTURE_ERROR);
  const network = new Error(FIXTURE_ERROR);
  await assert.rejects(api(async () => { throw network; }).toggleKeepState(FIXTURE_ID, "IMPRTC"), (error) => error === network);
});