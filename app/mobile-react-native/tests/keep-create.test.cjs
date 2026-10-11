const { test, describe } = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const ts = require("typescript");

const FIXTURE_TITLE = "샘플 메모";
const FIXTURE_ID = 101;
const FIXTURE_PROJECT_ID = 201;
const FIXTURE_ERROR = "샘플 등록 오류";
const FIXTURE_DATE_TEXT = " 10/17, 24 19:30 ";
const FIXTURE_DUE = "2001-10-24";

/** 실제 TS 모듈을 읽고 가상 요청만 주입한다. 날짜·FormData·오류는 호스트와 같은 생성자를 쓴다. */
function modules(request = async () => { throw new Error("Unexpected request"); }) {
  const cache = new Map();
  class ApiError extends Error {
    constructor(message, status, body) { super(message); this.status = status; this.body = body; }
  }
  function load(file) {
    if (cache.has(file)) return cache.get(file).exports;
    const module = { exports: {} };
    cache.set(file, module);
    const source = ts.transpileModule(fs.readFileSync(file, "utf8"), {
      compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022 }
    }).outputText;
    vm.runInNewContext(source, {
      module, exports: module.exports, Error, Date, FormData,
      console: { info() {}, warn() {}, error() {} },
      require(name) {
        if (name === "./client") return { request, ApiError };
        if (!name.startsWith(".")) throw new Error("Unexpected dependency: " + name);
        return load(path.resolve(path.dirname(file), name + ".ts"));
      }
    });
    return module.exports;
  }
  return { load: (relative) => load(path.resolve(__dirname, "../src", relative)) };
}

/** 웹의 날짜 회귀 사례 전체를 모바일 입력·표시 함수에 그대로 적용한다. */
const dateModules = modules();
const dateUtils = { ...dateModules.load("utils/keepDateInput.ts"), ...dateModules.load("utils/keepExpire.ts") };
const it = (name, callback) => test(name, callback);
it.each = (values) => (name, callback) => values.forEach((value, index) =>
  test(name + " (" + (index + 1) + ")", () => callback(value)));
const plain = (value) => JSON.parse(JSON.stringify(value));
const expect = (actual) => ({
  toEqual(expected) { assert.deepEqual(plain(actual), plain(expected)); },
  toBe(expected) { assert.equal(actual, expected); },
  toMatchObject(expected) {
    for (const [key, value] of Object.entries(expected)) assert.deepEqual(plain(actual[key]), plain(value));
  }
});
const webSpecPath = path.resolve(__dirname, "../../frontend-vue/src/features/prospective/keep/utils/expireDt.spec.ts");
const webSpec = ts.transpileModule(fs.readFileSync(webSpecPath, "utf8"), {
  compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022 }
}).outputText;
vm.runInNewContext(webSpec, {
  module: { exports: {} }, exports: {}, Date,
  require(name) {
    if (name === "vitest") return { describe, it, expect };
    if (name === "./expireDt") return dateUtils;
    throw new Error("Unexpected spec dependency: " + name);
  }
});

test("신규 FormData는 제목·일시 원문·확정 기한·Project와 신규 빈 필드만 전송", () => {
  const { toKeepFormData } = modules().load("utils/keepFormData.ts");
  const form = toKeepFormData({ title: " " + FIXTURE_TITLE + " ", dateText: FIXTURE_DATE_TEXT,
    expireDt: FIXTURE_DUE, projectId: FIXTURE_PROJECT_ID });
  assert.ok(form instanceof FormData);
  assert.deepEqual(Object.fromEntries(form.entries()), {
    title: FIXTURE_TITLE, dateText: FIXTURE_DATE_TEXT, expireDt: FIXTURE_DUE,
    projectId: String(FIXTURE_PROJECT_ID), prefixId: "", location: "", content: "", "tag.tagListStr": ""
  });
  for (const key of ["id", "sortOrder", "important", "state", "lifecycle", "resolvedAt"]) assert.equal(form.has(key), false);
});

test("제목만 등록할 때 선택 값은 빈 문자열이며 일시에서 기한을 임의 확정하지 않음", () => {
  const { toKeepFormData } = modules().load("utils/keepFormData.ts");
  const form = toKeepFormData({ title: FIXTURE_TITLE, dateText: FIXTURE_DATE_TEXT });
  assert.equal(form.get("projectId"), "");
  assert.equal(form.get("expireDt"), "");
  assert.equal(form.get("dateText"), FIXTURE_DATE_TEXT);
  assert.equal(form.get("prefixId"), "");
});

test("말머리를 고르면 웹 등록 모달과 같은 prefixId 필드에 ID 문자열로 보냄", () => {
  const { toKeepFormData } = modules().load("utils/keepFormData.ts");
  assert.equal(toKeepFormData({ title: FIXTURE_TITLE, prefixId: 7 }).get("prefixId"), "7");
  assert.equal(toKeepFormData({ title: FIXTURE_TITLE, prefixId: null }).get("prefixId"), "");
});

test("입력 길이·실재 날짜·Project 검증은 요청 전에 실패", async () => {
  let requests = 0;
  const { createKeep } = modules(async () => { requests++; return { rslt: true, rsltObj: { id: FIXTURE_ID } }; })
    .load("api/dreamDiaryApi.ts");
  const invalid = [
    { title: " " }, { title: "가".repeat(101) },
    { title: FIXTURE_TITLE, dateText: "가".repeat(201) },
    ...["10/24", "2001-02-29", "2001-04-31", "2001-13-01", "2001-00-01", "2001-01-00", "2001-1-01"].map((expireDt) => ({ title: FIXTURE_TITLE, expireDt })),
    ...[0, -1, 1.5, Number.NaN, Number.POSITIVE_INFINITY].map((projectId) => ({ title: FIXTURE_TITLE, projectId })),
    ...[0, -1, 1.5, Number.NaN].map((prefixId) => ({ title: FIXTURE_TITLE, prefixId }))
  ];
  for (const input of invalid) await assert.rejects(createKeep(input));
  assert.equal(requests, 0);
});

test("허용 길이 경계·윤년 기한은 원문 보존으로 직렬화", () => {
  const { toKeepFormData } = modules().load("utils/keepFormData.ts");
  const form = toKeepFormData({ title: "가".repeat(100), dateText: "나".repeat(200), expireDt: "2000-02-29" });
  assert.equal(form.get("title").length, 100);
  assert.equal(form.get("dateText").length, 200);
  assert.equal(form.get("expireDt"), "2000-02-29");
});

test("등록은 multipart POST 한 번으로 전송하고 새 서버 ID 반환", async () => {
  const calls = [];
  const fixture = { id: FIXTURE_ID, title: FIXTURE_TITLE };
  const { createKeep } = modules(async (url, options) => {
    calls.push({ url, options }); return { rslt: true, rsltObj: fixture };
  }).load("api/dreamDiaryApi.ts");
  assert.equal(await createKeep({ title: FIXTURE_TITLE }), fixture);
  assert.equal(calls.length, 1);
  assert.equal(calls[0].url, "/api/prospective/keeps");
  assert.equal(calls[0].options.method, "POST");
  assert.ok(calls[0].options.body instanceof FormData);
  assert.equal(calls[0].options.headers, undefined);
});

test("HTTP 200 업무 실패·401·전송 오류는 자동 재전송 없이 전달", async () => {
  for (const failure of [{ rslt: false, message: FIXTURE_ERROR }, null]) {
    let calls = 0;
    const { createKeep } = modules(async () => { calls++; return failure; }).load("api/dreamDiaryApi.ts");
    await assert.rejects(createKeep({ title: FIXTURE_TITLE }), (error) =>
      error.status === 200 && (failure == null || error.message === FIXTURE_ERROR));
    assert.equal(calls, 1);
  }
  for (const status of [401, undefined]) {
    const error = Object.assign(new Error(FIXTURE_ERROR), { status });
    let calls = 0;
    const { createKeep } = modules(async () => { calls++; throw error; }).load("api/dreamDiaryApi.ts");
    await assert.rejects(createKeep({ title: FIXTURE_TITLE }), (received) => received === error);
    assert.equal(calls, 1);
  }
});

test("성공 응답의 새 ID 누락·비정상 ID는 결과 확인 오류이며 POST를 반복하지 않음", async () => {
  for (const item of [null, {}, { id: 0 }, { id: -1 }, { id: 1.5 }, { id: "101" }]) {
    let calls = 0;
    const { createKeep } = modules(async () => { calls++; return { rslt: true, rsltObj: item }; }).load("api/dreamDiaryApi.ts");
    await assert.rejects(createKeep({ title: FIXTURE_TITLE }), (error) =>
      error.status === 200 && error.message.includes("목록을 확인"));
    assert.equal(calls, 1);
  }
});
