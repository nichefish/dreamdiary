const test = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const ts = require("typescript");

/** Project 표시 이름 헬퍼가 있는 실제 타입 모듈을 로드한다 (theme 만 스텁). */
function load(relativePath) {
  const source = fs.readFileSync(path.join(__dirname, relativePath), "utf8");
  const compiled = ts.transpileModule(source, {
    compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022 }
  }).outputText;
  const module = { exports: {} };
  const stubs = {
    "../theme/colors": { colors: new Proxy({}, { get: () => "#000000" }) },
    "../components/journal/journalTagStyle": { colorFromTextClass: (c) => (c === "text-primary" ? "#1B84FF" : null) }
  };
  vm.runInNewContext(compiled, {
    module,
    exports: module.exports,
    require(name) {
      if (stubs[name]) return stubs[name];
      throw new Error("Unexpected module: " + name);
    }
  });
  return module.exports;
}

const { keepProjectTitle, keepProjectPrefixLabel } = load("../src/types/prospectiveKeep.ts");

test("Project 말머리는 이름 앞에 공백 없이 붙는다 — [게임]왕국건설스토리", () => {
  const project = { id: 7, title: "왕국건설스토리", prefix: { id: 3, name: "게임", textClass: "text-primary" } };
  assert.equal(keepProjectTitle(project), "[게임]왕국건설스토리");
  assert.equal(keepProjectPrefixLabel(project), "[게임]");
});

test("말머리가 없거나 이름이 비면 Project 이름만 (기존 표기 유지)", () => {
  assert.equal(keepProjectTitle({ id: 7, title: "가상 프로젝트" }), "가상 프로젝트");
  assert.equal(keepProjectTitle({ id: 7, title: "가상 프로젝트", prefix: null }), "가상 프로젝트");
  assert.equal(keepProjectTitle({ id: 7, title: "가상 프로젝트", prefix: { name: "  " } }), "가상 프로젝트");
  assert.equal(keepProjectPrefixLabel({ id: 7, title: "가상" }), "");
});

test("이름이 비면 번호 표기에 말머리를 붙이고, Project 가 없으면 빈 문자열", () => {
  assert.equal(keepProjectTitle({ id: 9, title: " ", prefix: { name: "게임" } }), "[게임]Project #9");
  assert.equal(keepProjectTitle(null), "");
  assert.equal(keepProjectTitle(undefined), "");
});

test("비활성 말머리도 기존 선택이면 그대로 표시한다", () => {
  assert.equal(keepProjectTitle({ id: 1, title: "보관함", prefix: { name: "옛날", activeYn: "N" } }), "[옛날]보관함");
});
