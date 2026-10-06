import { htmlToPlainText } from "./journalCopy";

/**
 * 마크다운 원문 → 읽기 쉬운 평문 (새 네이티브 의존성 없이 화면·복사에 쓰는 가벼운 변환).
 * - 제목(#)·굵게·기울임·취소선·인라인 코드 표시 기호를 걷어낸다.
 * - 링크 `[글](url)` → `글 (url)`, 이미지 → `[이미지: 설명]`
 * - 목록 `- `·`* `·`+ ` → `• `, 인용 `> ` → `│ `, 체크박스 `- [x]` → `☑`, `- [ ]` → `☐`
 * - 코드 블록 울타리(```)는 줄째 지우고 안의 내용은 그대로 둔다.
 * - HTML 태그가 섞여 있으면 먼저 웹 복사 규칙(htmlToPlainText)으로 평문화한다.
 * 줄바꿈은 LF 로 돌려준다.
 */
export function markdownToPlainText(markdown: string | null | undefined): string {
  let text = (markdown ?? "").replace(/\r\n|\r/g, "\n");
  if (!text.trim()) return "";
  if (/<\/?(?:p|br|div|li|ul|ol|span|strong|em|b|i|a|h[1-6])\b[^>]*>/i.test(text)) {
    text = htmlToPlainText(text.replace(/\n/g, "<br>"));
  }
  let inFence = false;
  const lines = text.split("\n").flatMap((line) => {
    if (/^\s*(```|~~~)/.test(line)) {
      inFence = !inFence;
      return [];
    }
    if (inFence) return [line];
    let out = line
      .replace(/^\s{0,3}#{1,6}\s+/, "")
      .replace(/^\s{0,3}>\s?/, "│ ")
      .replace(/^(\s*)[-*+]\s+\[[xX]\]\s+/, "$1☑ ")
      .replace(/^(\s*)[-*+]\s+\[\s\]\s+/, "$1☐ ")
      .replace(/^(\s*)[-*+]\s+/, "$1• ")
      .replace(/^\s{0,3}([-*_])(\s*\1){2,}\s*$/, "──────");
    out = out
      .replace(/!\[([^\]]*)\]\(([^)\s]*)[^)]*\)/g, (_m, alt: string) => `[이미지${alt ? `: ${alt}` : ""}]`)
      .replace(/\[([^\]]+)\]\(([^)\s]+)[^)]*\)/g, (_m, label: string, url: string) => (label === url ? url : `${label} (${url})`))
      .replace(/(\*\*|__)(?=\S)([\s\S]*?\S)\1/g, "$2")
      .replace(/(^|[^*\w])\*(?=\S)([^*]*?\S)\*(?!\*)/g, "$1$2")
      .replace(/~~(?=\S)([\s\S]*?\S)~~/g, "$1")
      .replace(/`([^`]+)`/g, "$1")
      .replace(/\\([\\`*_{}[\]()#+\-.!>~|])/g, "$1");
    return [out.replace(/\s+$/, "")];
  });
  return lines.join("\n").replace(/\n{3,}/g, "\n\n").trim();
}
