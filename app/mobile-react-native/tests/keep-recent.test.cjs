const test = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const ts = require("typescript");

const FIXTURE_NOW = Date.parse("2001-01-02T03:04:05.000Z");
const FIXTURE_DURATION = 3600000;

/** 가상 완료 시각으로 웹·앱의 동일한 시간 경계 계약을 검증한다. */
function fixture(id, age, key = "RESOLVED") {
  return { id, lifecycle: { lifecycleKey: key, resolvedAt: new Date(FIXTURE_NOW - age).toISOString() } };
}

/** 네트워크·인증 의존성이 없는 실제 TypeScript 판단 모듈을 로드한다. */
function load(relativePath) {
  const source = fs.readFileSync(path.join(__dirname, relativePath), "utf8");
  const compiled = ts.transpileModule(source, {
    compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022 }
  }).outputText;
  const module = { exports: {} };
  vm.runInNewContext(compiled, { module, exports: module.exports });
  return module.exports;
}

for (const [platform, relativePath] of [
  ["mobile", "../src/utils/keepRecent.ts"],
  ["web", "../../frontend-vue/src/features/prospective/keep/utils/recentCompletion.ts"]
]) {
  const helpers = load(relativePath);
  test(`${platform}: 1시간 경계 직전은 최근 완료, 정확한 경계부터 일반 종결`, () => {
    assert.equal(helpers.isRecentKeep(fixture(101, FIXTURE_DURATION - 1), FIXTURE_NOW), true);
    assert.equal(helpers.isRecentKeep(fixture(102, FIXTURE_DURATION), FIXTURE_NOW), false);
    assert.equal(helpers.isRecentKeep(fixture(103, FIXTURE_DURATION + 1), FIXTURE_NOW), false);
    assert.equal(helpers.isRecentKeep(fixture(104, 0), FIXTURE_NOW), true);
  });
  test(`${platform}: 다른 상태와 미래 완료는 최근 완료에서 제외`, () => {
    for (const key of ["OPEN", "PENDING", "FAILED", "EXPIRED", "DROPPED"]) {
      assert.equal(helpers.isRecentKeep(fixture(101, 1000, key), FIXTURE_NOW), false);
    }
    assert.equal(helpers.isRecentKeep(fixture(101, -1), FIXTURE_NOW), false);
  });
  test(`${platform}: 미확인·잘못된·시간대 없는 시각은 추정하지 않음`, () => {
    for (const resolvedAt of [null, undefined, "", "invalidZ", "2001-01-02T03:04:04"]) {
      assert.equal(helpers.isRecentKeep({ lifecycle: { lifecycleKey: "RESOLVED", resolvedAt } }, FIXTURE_NOW), false);
    }
    assert.equal(helpers.isRecentKeep({}, FIXTURE_NOW), false);
  });
  test(`${platform}: 서버 UTC 소수점 6자리와 명시적 오프셋 해석`, () => {
    for (const resolvedAt of ["2001-01-02T03:04:04.123456Z", "2001-01-02T12:04:04.123456+09:00"]) {
      assert.equal(helpers.isRecentKeep({ lifecycle: { lifecycleKey: "RESOLVED", resolvedAt } }, FIXTURE_NOW), true);
    }
  });
  test(`${platform}: 최신 완료순·동시 완료 입력순·원본 목록 보존`, () => {
    const items = [fixture(101, 3000), fixture(102, 1000), fixture(103, 1000), fixture(104, FIXTURE_DURATION)];
    assert.equal(helpers.recentKeeps(items, FIXTURE_NOW).map((item) => item.id).join(","), "102,103,101");
    assert.equal(items.map((item) => item.id).join(","), "101,102,103,104");
  });
  test(`${platform}: 첫 만료 예약과 만료 후 다음 경계 이동`, () => {
    const items = [fixture(101, FIXTURE_DURATION - 1000), fixture(102, 1000)];
    assert.equal(helpers.nextKeepCompletionExpiry(items, FIXTURE_NOW), FIXTURE_NOW + 1000);
    assert.equal(helpers.recentKeeps(items, FIXTURE_NOW + 1000).map((item) => item.id).join(","), "102");
    assert.equal(helpers.nextKeepCompletionExpiry(items, FIXTURE_NOW + 1000), FIXTURE_NOW + FIXTURE_DURATION - 1000);
    assert.equal(helpers.nextKeepCompletionExpiry(items, FIXTURE_NOW + FIXTURE_DURATION), null);
    assert.equal(helpers.nextKeepCompletionExpiry([], FIXTURE_NOW), null);
  });
  test(`${platform}: 5건 미리보기와 나머지 종결은 중복 없이 분리`, () => {
    const items = Array.from({ length: 7 }, (_, i) => fixture(101 + i, i * 1000));
    items.push(fixture(201, FIXTURE_DURATION), fixture(202, 1000, "FAILED"));
    const recent = helpers.recentKeeps(items, FIXTURE_NOW);
    assert.equal(recent.slice(0, helpers.KEEP_RECENT_PREVIEW_SIZE).length, 5);
    const other = items.filter((item) => !helpers.isRecentKeep(item, FIXTURE_NOW));
    assert.equal(other.map((item) => item.id).join(","), "201,202");
    assert.equal(recent.length + other.length, items.length);
  });
}
