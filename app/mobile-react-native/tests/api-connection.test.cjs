const test = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const vm = require("node:vm");
const ts = require("typescript");
const path = require("node:path");

const FIXTURE_PATH = "/api/sample";
const FIXTURE_MESSAGE = "샘플 서버 오류";

/** 실제 공통 요청을 가상 fetch·수동 시계로 실행한다. */
function client(fetch) {
  const timers = new Map();
  let timerId = 0;
  let unauthorized = 0;
  let clearedTokens = 0;
  const module = { exports: {} };
  const source = ts.transpileModule(fs.readFileSync(path.resolve(__dirname, "../src/api/client.ts"), "utf8"), {
    compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022 }
  }).outputText;
  vm.runInNewContext(source, {
    module, exports: module.exports, Error, TypeError, AbortController, URL, FormData,
    console: { info() {}, warn() {}, error() {} },
    setTimeout(callback, delay) { assert.equal(delay, 15000); timers.set(++timerId, callback); return timerId; },
    clearTimeout(id) { timers.delete(id); }, fetch,
    require(name) {
      if (name === "../config/env") return { API_BASE_URL: "http://example.invalid" };
      if (name === "../auth/accessToken") return {
        getAccessToken: () => null, clearAccessToken: () => { clearedTokens++; },
        captureAccessTokenFromResponse() {}
      };
      throw new Error("Unexpected dependency: " + name);
    }
  });
  module.exports.setUnauthorizedHandler(() => unauthorized++);
  return { api: module.exports, timers, expire() { for (const cb of [...timers.values()]) cb(); },
    unauthorized: () => unauthorized, clearedTokens: () => clearedTokens };
}
function response(status = 200, body = { rslt: true }) {
  return { ok: status < 400, status, headers: { get: () => "application/json" }, json: async () => body };
}

test("연결되지 않는 GET은 15초 뒤 안내 오류와 요청 중단, 인증 보존", async () => {
  let signal;
  let calls = 0;
  const c = client(async (_, init) => { calls++; signal = init.signal; return new Promise(() => {}); });
  const pending = c.api.request(FIXTURE_PATH);
  c.expire();
  await assert.rejects(pending, (e) => e instanceof c.api.ApiConnectionError
    && e.reason === "timeout" && e.message.includes("휴대폰·PC의 Tailscale"));
  assert.equal(signal.aborted, true);
  assert.equal(calls, 1);
  assert.equal(c.timers.size, 0);
  assert.equal(c.unauthorized(), 0);
  assert.equal(c.clearedTokens(), 0);
});

test("응답 헤더 이후 본문이 멈춰도 15초 제한으로 종료", async () => {
  const c = client(async () => ({ ...response(), json: () => new Promise(() => {}) }));
  const pending = c.api.request(FIXTURE_PATH);
  await Promise.resolve();
  c.expire();
  await assert.rejects(pending, (e) => e.reason === "timeout");
  assert.equal(c.timers.size, 0);
});

test("빠른 연결 실패는 서버 연결 안내로 바꾸고 재전송하지 않음", async () => {
  let calls = 0;
  const c = client(async () => { calls++; throw new TypeError("Network request failed"); });
  await assert.rejects(c.api.request(FIXTURE_PATH), (e) => e.reason === "network" && e.message.includes("서버에 연결"));
  assert.equal(calls, 1);
  assert.equal(c.timers.size, 0);
  assert.equal(c.unauthorized(), 0);
});

test("저장 연결 실패는 서버 처리 가능성과 목록 확인을 안내", async () => {
  for (const method of ["POST", "PUT", "PATCH", "DELETE"]) {
    let calls = 0;
    const c = client(async () => { calls++; throw new TypeError("Network request failed"); });
    await assert.rejects(c.api.request(FIXTURE_PATH, { method }), (e) =>
      e.message.includes("처리됐을 수") && e.message.includes("목록"));
    assert.equal(calls, 1);
  }
});

test("성공은 원래 응답과 헤더를 보존하고 타이머 해제", async () => {
  let init;
  const body = { rslt: true };
  const c = client(async (_, options) => { init = options; return response(200, body); });
  assert.equal(await c.api.request(FIXTURE_PATH), body);
  assert.equal(init.headers["X-Client-Channel"], "MOBILE");
  assert.equal(init.headers["Content-Type"], "application/json");
  assert.equal(c.timers.size, 0);
});

test("multipart와 서버 오류 메시지·401 로그아웃 계약 유지", async () => {
  let init;
  const c = client(async (_, options) => { init = options; return response(403, { message: FIXTURE_MESSAGE }); });
  await assert.rejects(c.api.request(FIXTURE_PATH, { method: "POST", body: new FormData() }),
    (e) => e.status === 403 && e.message === FIXTURE_MESSAGE);
  assert.equal(init.headers["Content-Type"], undefined);
  assert.equal(c.timers.size, 0);
  const unauthorized = client(async () => response(401, { message: FIXTURE_MESSAGE }));
  await assert.rejects(unauthorized.api.request(FIXTURE_PATH), (e) => unauthorized.api.isUnauthorizedError(e));
  assert.equal(unauthorized.unauthorized(), 1);
  assert.equal(unauthorized.clearedTokens(), 1);
});

test("호출자 취소는 연결 오류로 바꾸지 않고 신호 전달·구독 해제", async () => {
  const controller = new AbortController();
  const aborted = new Error("sample abort");
  let removed = 0;
  const remove = controller.signal.removeEventListener.bind(controller.signal);
  controller.signal.removeEventListener = (...args) => { removed++; return remove(...args); };
  const c = client((_, init) => new Promise((_, reject) =>
    init.signal.addEventListener("abort", () => reject(aborted), { once: true })));
  const pending = c.api.request(FIXTURE_PATH, { signal: controller.signal });
  controller.abort();
  await assert.rejects(pending, (e) => e === aborted);
  assert.equal(c.timers.size, 0);
  assert.equal(removed, 1);
});

test("본문 JSON 형식 오류를 연결 실패로 오인하지 않음", async () => {
  const parseError = new SyntaxError("sample invalid JSON");
  const c = client(async () => ({ ...response(), json: async () => { throw parseError; } }));
  await assert.rejects(c.api.request(FIXTURE_PATH), (e) => e === parseError);
});

/** 실제 인증 Provider의 로딩 종료·오류·사용자 재시도를 가상 훅으로 관측한다. */
function auth(c, getAuthInfo) {
  const slots = [];
  let cursor = 0;
  const module = { exports: {} };
  const jsx = (type, props) => ({ type, props });
  vm.runInNewContext(ts.transpileModule(fs.readFileSync(path.resolve(__dirname, "../src/context/AuthContext.tsx"), "utf8"), {
    compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022, jsx: ts.JsxEmit.ReactJSX }
  }).outputText, {
    module, exports: module.exports, Error, console: { warn() {} },
    require(name) {
      if (name === "react") return {
        createContext: () => ({ Provider: "Provider" }),
        useState(initial) { const i = cursor++; if (!(i in slots)) slots[i] = initial; return [slots[i], (value) => { slots[i] = value; }]; },
        useCallback: (fn) => fn, useEffect() {}
      };
      if (name === "react/jsx-runtime") return { jsx, jsxs: jsx };
      if (name === "../api/client") return c.api;
      if (name === "../api/dreamDiaryApi") return { getAuthInfo, refreshAccessToken: async () => {}, login() {}, logout() {} };
      if (name === "../auth/accessToken") return { hydrateAccessTokenFromSecureStore: async () => {}, clearAccessToken: async () => {} };
      throw new Error("Unexpected auth dependency: " + name);
    }
  });
  return { render() { cursor = 0; return module.exports.AuthProvider({ children: null }).props.value; } };
}

test("앱 시작 인증 지연은 로딩 종료·연결 안내로 전환하고 다시 시도 가능", async () => {
  let calls = 0;
  const c = client(async () => ++calls === 1 ? new Promise(() => {}) : response(401));
  const a = auth(c, () => c.api.request("/api/auth/get-auth-info"));
  const pending = a.render().retryAuth();
  for (let i = 0; i < 5; i++) await Promise.resolve();
  c.expire();
  await pending;
  const failed = a.render();
  assert.equal(failed.isLoading, false);
  assert.equal(failed.isAuthenticated, false);
  assert.equal(failed.authError.includes("Tailscale"), true);
  await failed.retryAuth();
  assert.equal(a.render().authError, null);
  assert.equal(a.render().isLoading, false);
  assert.equal(calls, 2);
});

test("시간 초과 뒤 늦게 온 401은 인증을 지우거나 화면 상태를 바꾸지 않음", async () => {
  let finish;
  const c = client(() => new Promise((resolve) => { finish = resolve; }));
  const pending = c.api.request(FIXTURE_PATH);
  c.expire();
  await assert.rejects(pending, (e) => e.reason === "timeout");
  finish(response(401));
  for (let i = 0; i < 8; i++) await Promise.resolve();
  assert.equal(c.unauthorized(), 0);
  assert.equal(c.clearedTokens(), 0);
});
