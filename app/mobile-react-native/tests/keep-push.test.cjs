const { test } = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const ts = require("typescript");

const FIXTURE_OWNER = "Alice";
const FIXTURE_OTHER = "Bob";
const FIXTURE_ID = "11111111-1111-4111-8111-111111111111";
const FIXTURE_PROJECT = "22222222-2222-4222-8222-222222222222";
const FIXTURE_TOKEN = "ExpoPushToken[sample_token]";
const FIXTURE_NATIVE = { type: "android", data: "sample-native-token" };
const FIXTURE_WINDOW = { localDate: "2001-10-24", slot: "MORNING" };

/** 실제 TypeScript 모듈을 같은 realm에서 실행하고 네이티브·API 경계를 주입한다. */
function load(relative, dependencies = {}, environment = {}) {
  const compiled = ts.transpileModule(fs.readFileSync(path.resolve(__dirname, relative), "utf8"), {
    compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022 }
  }).outputText;
  const module = { exports: {} };
  const logs = [];
  new Function("module", "exports", "require", "console", "process", compiled)(module, module.exports,
    (name) => { if (name in dependencies) return dependencies[name]; throw new Error("Unexpected dependency: " + name); },
    { info: (...args) => logs.push(args), warn: (...args) => logs.push(args) }, { env: environment });
  return { api: module.exports, logs };
}
const registrationModule = load("../src/notifications/keepPushRegistration.ts");
const { KeepPushRegistration, KeepPushError } = registrationModule.api;
const flush = async () => { for (let index = 0; index < 20; index++) await Promise.resolve(); };
function deferred() { let resolve; const promise = new Promise((done) => { resolve = done; }); return { promise, resolve }; }

function registration(overrides = {}) {
  const calls = [];
  let latest;
  const ports = {
    cleanup: async () => {}, prepareChannel: async () => {}, permission: async () => true,
    projectId: () => FIXTURE_PROJECT, token: async () => FIXTURE_TOKEN,
    deviceId: async () => FIXTURE_ID, timeZone: () => "Asia/Seoul",
    register: async (...args) => { calls.push(["register", ...args]); },
    unregister: async (...args) => { calls.push(["unregister", ...args]); }, ...overrides
  };
  const api = new KeepPushRegistration(ports);
  api.subscribe((next) => { latest = next; });
  api.setOwner(FIXTURE_OWNER);
  return { api, calls, ports, status: () => latest };
}

test("현재 시간대와 Expo 토큰을 등록하고 전경 확인에서 시간대 변경을 반영", async () => {
  const h = registration();
  await h.api.sync();
  assert.equal(h.status().state, "ready");
  h.ports.timeZone = () => "America/New_York";
  await h.api.sync();
  assert.deepEqual(h.calls.map((call) => call.slice(0, 2).concat(call[3])), [
    ["register", FIXTURE_ID, "Asia/Seoul"], ["register", FIXTURE_ID, "America/New_York"]
  ]);
});

test("실행 중 등록이 끝난 뒤 해제하고 로그아웃 중 추가 등록을 차단", async () => {
  const gate = deferred();
  const h = registration({ register: async () => { h.calls.push(["register"]); await gate.promise; } });
  const registering = h.api.sync();
  await flush();
  assert.deepEqual(h.calls, [["register"]]);
  const logout = h.api.detachForLogout();
  await h.api.sync();
  assert.deepEqual(h.calls, [["register"]]);
  gate.resolve();
  await Promise.all([registering, logout]);
  assert.deepEqual(h.calls, [["register"], ["unregister", FIXTURE_ID]]);
  await h.api.sync();
  assert.equal(h.calls.length, 2);
});

test("토큰 대기 중 로그아웃은 대기·후속 등록을 무효화", async () => {
  const gate = deferred();
  const h = registration({ token: () => gate.promise });
  const first = h.api.sync();
  await flush();
  const queued = h.api.sync();
  const logout = h.api.detachForLogout();
  gate.resolve(FIXTURE_TOKEN);
  await Promise.all([first, queued, logout]);
  assert.deepEqual(h.calls, [["unregister", FIXTURE_ID]]);
});

test("등록 응답 미확인도 로그아웃에서 기기 해제를 확인", async () => {
  const h = registration({ register: async () => { throw new Error(FIXTURE_TOKEN); } });
  await h.api.sync();
  assert.equal(h.status().state, "error");
  await h.api.detachForLogout();
  assert.deepEqual(h.calls, [["unregister", FIXTURE_ID]]);
  assert.ok(!JSON.stringify(registrationModule.logs).includes(FIXTURE_TOKEN));
});

test("해제 실패는 로그아웃 실패와 안내를 반환하고 사용자 재시도 가능", async () => {
  let failing = true;
  const h = registration({ unregister: async () => { if (failing) throw new Error(FIXTURE_TOKEN); } });
  await assert.rejects(h.api.detachForLogout(), (error) => error instanceof KeepPushError && error.message.includes("로그아웃을 멈췄습니다"));
  assert.equal(h.status().state, "error");
  assert.ok(!h.status().message.includes(FIXTURE_TOKEN));
  failing = false;
  await h.api.detachForLogout();
});

test("권한 거부는 서버 해제를 확인한 후 상태로 표시, 해제 실패는 오류로 표시", async () => {
  const h = registration({ permission: async () => false });
  await h.api.sync();
  assert.deepEqual(h.calls, [["unregister", FIXTURE_ID]]);
  assert.equal(h.status().state, "denied");
  h.ports.unregister = async () => { throw new Error("sample failure"); };
  await h.api.sync();
  assert.equal(h.status().state, "error");
});

test("계정 변경·401은 이전 계정의 늦은 토큰 결과를 등록하지 않음", async () => {
  const gate = deferred();
  const h = registration({ token: () => gate.promise });
  const stale = h.api.sync();
  await flush();
  h.api.setOwner(null);
  gate.resolve(FIXTURE_TOKEN);
  await stale;
  assert.equal(h.calls.length, 0);
  h.api.setOwner(FIXTURE_OTHER);
  await h.api.sync();
  assert.equal(h.calls.length, 1);
});

test("네이티브 토큰 변경은 전달받은 토큰으로 Expo 토큰 조회", async () => {
  let hint;
  const h = registration({ token: async (_, next) => { hint = next; return FIXTURE_TOKEN; } });
  await h.api.sync(FIXTURE_NATIVE);
  assert.deepEqual(hint, FIXTURE_NATIVE);
  assert.equal(h.calls[0][2], FIXTURE_TOKEN);
});

test("설치 ID 없는 로그아웃은 서버 호출 없이 종료", async () => {
  const h = registration({ deviceId: async () => null });
  await h.api.detachForLogout();
  assert.equal(h.calls.length, 0);
});

test("잘못된 프로젝트·UUID 저장 오류·정합 취소 오류는 준비 성공으로 감추지 않음", async () => {
  for (const overrides of [
    { projectId: () => { throw new KeepPushError("샘플 프로젝트 오류"); } },
    { deviceId: async () => { throw new Error("sample storage failure"); } },
    { cleanup: async () => { throw new Error("sample cleanup failure"); } }
  ]) {
    const h = registration(overrides);
    await h.api.sync();
    assert.equal(h.status().state, "error");
    assert.equal(h.calls.length, 0);
  }
});

function taps(initial = null) {
  let listener;
  let ready = false;
  let names = ["Login"];
  let clearCount = 0;
  const calls = [];
  const module = load("../src/notifications/keepPushTap.ts", {
    "expo-notifications": {
      addNotificationResponseReceivedListener: (fn) => { listener = fn; return { remove() {} }; },
      getLastNotificationResponseAsync: async () => initial,
      clearLastNotificationResponseAsync: async () => { clearCount++; }
    },
    "@react-navigation/native": { StackActions: { push: (name, params) => ({ name, params }) } }
  }).api;
  const bound = module.bindKeepNotificationTap({ isReady: () => ready, getRootState: () => ({ routeNames: names }), dispatch: (value) => calls.push(value) });
  return { bound, calls, parse: module.parseKeepReminder, tap: (id, data = { kind: "keep-reminder", ...FIXTURE_WINDOW }) => listener({ notification: { request: { identifier: id, content: { data } } } }),
    authenticate() { ready = true; names = ["Main", "KeepHome"]; bound.flush(); }, clearCount: () => clearCount };
}

test("콜드 스타트는 인증 준비까지 선택을 보관하고 같은 응답은 한 번만 처리", async () => {
  const initial = { notification: { request: { identifier: "sample-response", content: { data: { kind: "keep-reminder", ...FIXTURE_WINDOW } } } } };
  const h = taps(initial);
  h.bound.onReady();
  await flush();
  assert.equal(h.calls.length, 0);
  h.authenticate();
  assert.deepEqual(h.calls, [{ name: "KeepHome", params: { reminder: FIXTURE_WINDOW } }]);
  h.tap("sample-response");
  assert.equal(h.calls.length, 1);
  assert.equal(h.clearCount(), 1);
});

test("실시간 알림 선택은 기존 화면을 보존하는 새 목록으로 진입", async () => {
  const h = taps();
  h.authenticate();
  h.tap("sample-live");
  assert.equal(h.calls.length, 1);
  h.bound.remove();
  h.tap("sample-removed");
  assert.equal(h.calls.length, 1);
});

test("늦은 콜드 응답이 새 실시간 선택을 덮지 않음", async () => {
  const gate = deferred();
  const h = taps(gate.promise);
  h.authenticate();
  h.bound.onReady();
  h.tap("sample-new");
  gate.resolve({ notification: { request: { identifier: "sample-old", content: { data: { kind: "keep-reminder", ...FIXTURE_WINDOW } } } } });
  await flush();
  assert.equal(h.calls.length, 1);
});

test("기한 로컬 알림·잘못된 날짜·슬롯은 대상 목록으로 전달하지 않음", () => {
  const h = taps();
  for (const data of [
    { kind: "keep-due", ...FIXTURE_WINDOW }, { kind: "keep-reminder", localDate: "2001-02-30", slot: "MORNING" },
    { kind: "keep-reminder", localDate: "2001-10-24", slot: "NIGHT" }, { kind: "keep-reminder", localDate: "10/24", slot: "MORNING" }
  ]) assert.equal(h.parse(data), null);
});

function native(overrides = {}, environment = { EXPO_PUBLIC_EXPO_PROJECT_ID: FIXTURE_PROJECT }) {
  const calls = [];
  let stored = null;
  const notifications = {
    AndroidImportance: { DEFAULT: 3 }, setNotificationHandler() {},
    getAllScheduledNotificationsAsync: async () => [
      { identifier: "sample-old", content: { data: { kind: "keep-due" } } },
      { identifier: "sample-other", content: { data: { kind: "other" } } }
    ],
    cancelScheduledNotificationAsync: async (id) => calls.push(["cancel", id]),
    setNotificationChannelAsync: async (...args) => calls.push(["channel", ...args]),
    getPermissionsAsync: async () => ({ granted: true }), requestPermissionsAsync: async () => ({ granted: true }),
    getExpoPushTokenAsync: async (options) => { calls.push(["token", options]); return { data: FIXTURE_TOKEN }; }, ...overrides
  };
  const h = load("../src/notifications/keepPush.ts", {
    "react-native": { Platform: { OS: "android" } }, "expo-notifications": notifications,
    "expo-secure-store": { getItemAsync: async () => stored, setItemAsync: async (_, value) => { stored = value; } },
    "expo-crypto": { randomUUID: () => FIXTURE_ID },
    "../api/dreamDiaryApi": { registerKeepReminderDevice: async (...args) => calls.push(["register", ...args]), unregisterKeepReminderDevice: async (...args) => calls.push(["unregister", ...args]) },
    "./keepPushRegistration": registrationModule.api
  }, environment);
  h.api.keepPushRegistration.setOwner(FIXTURE_OWNER);
  return { ...h, calls, stored: () => stored, setStored: (value) => { stored = value; } };
}

test("단일 서버 경로는 Keep 예약만 취소하고 서버 channelId와 같은 채널로 등록", async () => {
  const h = native();
  await h.api.keepPushRegistration.sync();
  assert.deepEqual(h.calls.filter((call) => call[0] === "cancel"), [["cancel", "sample-old"]]);
  assert.equal(h.calls.find((call) => call[0] === "channel")[1], "keep-reminder");
  assert.equal(h.calls.find((call) => call[0] === "register")[1], FIXTURE_ID);
  assert.equal(h.stored(), FIXTURE_ID);
});

test("프로젝트 설정 누락은 명확한 상태 표시와 서버 등록 생략", async () => {
  const h = native({}, {});
  let status;
  h.api.keepPushRegistration.subscribe((next) => { status = next; });
  await h.api.keepPushRegistration.sync();
  assert.equal(status.state, "error");
  assert.match(status.message, /프로젝트 설정/);
  assert.equal(h.calls.filter((call) => call[0] === "register" || call[0] === "token").length, 0);
});

test("권한 요청은 미결정일 때 실행당 한 번, 거부 후 강제 재요청 생략", async () => {
  let asks = 0;
  const h = native({
    getPermissionsAsync: async () => ({ granted: false, status: "undetermined", canAskAgain: true }),
    requestPermissionsAsync: async () => { asks++; return { granted: false }; }
  });
  await h.api.keepPushRegistration.sync();
  await h.api.keepPushRegistration.sync();
  assert.equal(asks, 1);
});

test("UUID 손상은 서버 쓰기·식별자 재생성 대신 오류", async () => {
  const h = native();
  h.setStored("sample-corrupt-id");
  let status;
  h.api.keepPushRegistration.subscribe((next) => { status = next; });
  await h.api.keepPushRegistration.sync();
  assert.equal(status.state, "error");
  assert.equal(h.stored(), "sample-corrupt-id");
  assert.equal(h.calls.filter((call) => call[0] === "register").length, 0);
});