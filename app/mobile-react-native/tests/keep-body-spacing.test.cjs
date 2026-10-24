const test = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const ts = require("typescript");

const FIXTURE_FIRST = "가상 첫 항목";
const FIXTURE_SECOND = "가상 둘째 항목";
const FIXTURE_PARAGRAPHS = `<p>${FIXTURE_FIRST}</p><p>${FIXTURE_SECOND}</p>`;
const FIXTURE_BREAKS = `<p>${FIXTURE_FIRST}<br><br>${FIXTURE_SECOND}</p>`;
const FIXTURE_EMPTY = `<p>${FIXTURE_FIRST}</p><p><br></p><p>&nbsp;</p><p>${FIXTURE_SECOND}</p>`;

/** 실제 변환 모듈을 UTF-8로 읽어 기본 복사와 Keep 표시의 경계를 검증한다. */
const cache = new Map();
function load(file) {
  if (cache.has(file)) return cache.get(file).exports;
  const module = { exports: {} }; cache.set(file, module);
  const code = ts.transpileModule(fs.readFileSync(file, "utf8"), {
    compilerOptions: { module: ts.ModuleKind.CommonJS, target: ts.ScriptTarget.ES2022 }
  }).outputText;
  new Function("require", "exports", "module", code)((name) => load(path.resolve(path.dirname(file), name + ".ts")), module.exports, module);
  return module.exports;
}
const { markdownToPlainText } = load(path.resolve(__dirname, "../src/utils/markdownText.ts"));
const { htmlToPlainText } = load(path.resolve(__dirname, "../src/utils/journalCopy.ts"));
const display = (value) => markdownToPlainText(value, { compactParagraphs: true });

test("Keep 읽기의 문단 두 개는 두 줄, 복사 기본 호출은 문단 간 빈 줄 유지", () => {
  assert.equal(display(FIXTURE_PARAGRAPHS), `${FIXTURE_FIRST}\n${FIXTURE_SECOND}`);
  assert.equal(markdownToPlainText(FIXTURE_PARAGRAPHS), `${FIXTURE_FIRST}\n\n${FIXTURE_SECOND}`);
  assert.equal(htmlToPlainText(FIXTURE_PARAGRAPHS), `${FIXTURE_FIRST}\n\n${FIXTURE_SECOND}`);
});

test("사용자가 입력한 br와 연속 빈 문단은 원래 개행 개수를 보존", () => {
  assert.equal(display(FIXTURE_BREAKS), `${FIXTURE_FIRST}\n\n${FIXTURE_SECOND}`);
  assert.equal(display(FIXTURE_EMPTY), `${FIXTURE_FIRST}\n\n\n${FIXTURE_SECOND}`);
});

test("HTML 블록 사이 정렬용 개행·중첩 div·목록은 중복 빈 줄 없이 표시", () => {
  for (const value of [
    `<p>${FIXTURE_FIRST}</p>\n<p>${FIXTURE_SECOND}</p>`,
    `<div>\n<p>${FIXTURE_FIRST}</p>\n<p>${FIXTURE_SECOND}</p>\n</div>`,
    `<ul><li>${FIXTURE_FIRST}</li><li>${FIXTURE_SECOND}</li></ul>`
  ]) assert.equal(display(value), `${FIXTURE_FIRST}\n${FIXTURE_SECOND}`);
});

test("문단 내부 직접 개행과 평문·마크다운 빈 줄의 기존 규칙 유지", () => {
  assert.equal(display(`<p>${FIXTURE_FIRST}\n${FIXTURE_SECOND}</p>`), `${FIXTURE_FIRST}\n${FIXTURE_SECOND}`);
  const plain = `${FIXTURE_FIRST}\n\n${FIXTURE_SECOND}`;
  assert.equal(display(plain), markdownToPlainText(plain));
  assert.equal(display(`- ${FIXTURE_FIRST}\n- ${FIXTURE_SECOND}`), `• ${FIXTURE_FIRST}\n• ${FIXTURE_SECOND}`);
});

test("내부 경계 문자와 같은 원문·굵게·엔티티는 표시에서 보존", () => {
  const value = `<p>${FIXTURE_FIRST}\uE000\uE002 &amp; <strong>가상</strong></p><p>${FIXTURE_SECOND}</p>`;
  assert.equal(display(value), `${FIXTURE_FIRST}\uE000\uE002 & 가상\n${FIXTURE_SECOND}`);
});
test("에디터의 빈 div 문단과 빈 문단 안 연속 br도 개행 수 보존", () => {
  assert.equal(display(`<div>${FIXTURE_FIRST}</div><div><br></div><div>${FIXTURE_SECOND}</div>`), `${FIXTURE_FIRST}\n\n${FIXTURE_SECOND}`);
  assert.equal(display(`<p>${FIXTURE_FIRST}</p><p><br><br></p><p>${FIXTURE_SECOND}</p>`), `${FIXTURE_FIRST}\n\n\n${FIXTURE_SECOND}`);
});