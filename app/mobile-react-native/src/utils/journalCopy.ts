import type { JournalDay, JournalEntry } from "../types/journalDay";
import { chapterEntries, dreamEntriesFromDay } from "../types/journalDay";

/**
 * 저널 복사·공유 텍스트 조립 — 웹(frontend-vue) 복사 계약을 그대로 옮겼다.
 * - htmlToPlainText: features/journal/utils/htmlToPlainText.ts (DOMParser 대신 정규식 포트)
 * - 리플렉션·줄바꿈: features/journal/utils/journalCopyReflection.ts (CRLF, 블록 사이 빈 줄 1개)
 * - 엔트리 1건: entry/composables/useEntryCopy.ts copyEntry — `날짜 (요일)` + 본문 + 리플렉션
 * - 여러 엔트리: entry/JournalEntrySearchPage.vue copyAll — 날짜가 바뀔 때만 `날짜 (요일)` 헤더, `#순번` + 본문, 엔트리 사이 빈 줄
 * 요일 표기는 웹 한국어 메시지(journal.weekday.*)와 같은 한자 요일이다.
 */

/** 웹 복사 계약과 같은 Windows 호환 줄바꿈 */
export const JOURNAL_COPY_LINE_BREAK = "\r\n";
const BLOCK_SEPARATOR = `${JOURNAL_COPY_LINE_BREAK}${JOURNAL_COPY_LINE_BREAK}`;

/** 웹 messages_ko journal.weekday.sun..sat */
const WEEKDAY_LABELS = ["日", "月", "火", "水", "木", "金", "土"];

/** 리플렉션 포함 범위 — full: 전부, no-pending: 보류 제외, body: 본문만 (웹 CopyReflectionMode) */
export type CopyReflectionMode = "full" | "no-pending" | "body";

const NAMED_ENTITIES: Record<string, string> = {
  nbsp: "\u00a0",
  amp: "&",
  lt: "<",
  gt: ">",
  quot: "\"",
  apos: "'",
  hellip: "…",
  middot: "·",
  ndash: "–",
  mdash: "—",
  lsquo: "‘",
  rsquo: "’",
  ldquo: "“",
  rdquo: "”",
  laquo: "«",
  raquo: "»",
  bull: "•",
  copy: "©",
  reg: "®",
  trade: "™",
  times: "×",
  divide: "÷",
  deg: "°",
  rarr: "→",
  larr: "←",
  uarr: "↑",
  darr: "↓"
};

/** 이름·10진·16진 HTML 엔티티를 한 번 디코딩한다. 모르는 이름은 그대로 둔다. */
function decodeEntities(text: string): string {
  return text.replace(/&(#x[0-9a-f]+|#\d+|[a-z][a-z0-9]*);/gi, (match, code: string) => {
    if (code.startsWith("#")) {
      const hex = code[1] === "x" || code[1] === "X";
      const n = hex ? parseInt(code.slice(2), 16) : parseInt(code.slice(1), 10);
      return Number.isFinite(n) && n > 0 && n <= 0x10ffff ? String.fromCodePoint(n) : match;
    }
    return NAMED_ENTITIES[code.toLowerCase()] ?? match;
  });
}

/**
 * TinyMCE HTML → 클립보드용 소스 텍스트 (웹 htmlToPlainText 와 같은 규칙).
 * 구조 태그(p·br·div·li·hr)만 개행으로 환원하고, 엔티티를 한 번 디코딩하며, 문단 사이 빈 줄 1개를 유지한다.
 * 빈 문단(<p>&nbsp;</p>, <p><br></p>)은 사용자가 만든 개행으로 보존한다.
 */
export function htmlToPlainText(html: string | null | undefined): string {
  if (!html) return "";
  let emptyParagraphToken = "\uE000";
  while (html.includes(emptyParagraphToken)) emptyParagraphToken += "\uE001";

  const withTokens = html.replace(/<p\b[^>]*>([\s\S]*?)<\/p\s*>/gi, (match, inner: string) => {
    if (/<\s*(?:img|table|hr|iframe|video|audio|object|embed)\b/i.test(inner)) return match;
    const plain = decodeEntities(inner.replace(/<[^>]*>/g, "")).replace(/\u00a0/g, " ").trim();
    return plain ? match : emptyParagraphToken;
  });

  const normalizedHtml = withTokens
    .replace(/<\s*hr\b[^>]*\/?>/gi, "\n------\n")
    .replace(/<\s*br\s*\/?>/gi, "\n")
    .replace(/<\s*(?:div|li)\b[^>]*>/gi, "\n")
    .replace(/<\s*\/?p\b[^>]*>/gi, "\n");

  const text = decodeEntities(normalizedHtml.replace(/<[^>]*>/g, ""))
    .split("\n").map((line) => line.trim()).join("\n")
    .replace(/\n{2,}/g, "\n\n")
    .trim();

  return text.split(emptyParagraphToken).join("\n");
}

/** LF·CR·CRLF 혼용을 CRLF 로 정규화 */
function normalizeLineBreaks(text: string): string {
  return text.replace(/\r\n|\r|\n/g, JOURNAL_COPY_LINE_BREAK);
}

/** `YYYY-MM-DD (요일)` — 웹 getWeekDayStr 와 같은 정오 기준 로컬 요일 */
export function copyDateLabel(stdrdDt: string | null | undefined): string {
  const dt = stdrdDt?.trim();
  if (!dt) return "";
  const d = new Date(dt.includes("T") ? dt : `${dt}T12:00:00`);
  const weekDay = WEEKDAY_LABELS[d.getDay()];
  return weekDay ? `${dt} (${weekDay})` : dt;
}

function entrySourceText(entry: JournalEntry): string {
  /* content = 저작 원문 HTML 우선, 없을 때만 markdownContent (웹 복사 계약) */
  return htmlToPlainText(entry.content ?? entry.markdownContent ?? "");
}

function includeReflection(mode: CopyReflectionMode, lifecycleKey: string | null | undefined): boolean {
  if (mode === "body") return false;
  if (mode === "no-pending") return lifecycleKey !== "PENDING";
  return true;
}

/** 본문 뒤에 리플렉션 본문을 빈 줄 1개로 이어 붙인다 (웹 appendReflectionsToCopyText) */
function appendReflections(baseText: string, reflections: JournalEntry[] | null | undefined, mode: CopyReflectionMode): string {
  const base = normalizeLineBreaks(baseText);
  const reflectionText = (reflections ?? [])
    .filter((r) => r != null && includeReflection(mode, r.lifecycle?.lifecycleKey))
    .map(entrySourceText)
    .filter(Boolean)
    .map(normalizeLineBreaks)
    .join(BLOCK_SEPARATOR);
  if (!reflectionText) return base;
  if (!base) return reflectionText;
  return `${base}${BLOCK_SEPARATOR}${reflectionText}`;
}

/**
 * 엔트리 1건 복사 텍스트 (웹 useEntryCopy.copyEntry): `날짜 (요일)` → 본문 → 리플렉션.
 * @param entry 대상 엔트리
 * @param fallbackDate entry.stdrdDt 가 없을 때 쓸 일자
 * @param mode 리플렉션 포함 범위 (기본 full = 웹 주 버튼)
 */
export function buildEntryCopyText(entry: JournalEntry, fallbackDate?: string | null, mode: CopyReflectionMode = "full"): string {
  const dateLine = copyDateLabel(entry.stdrdDt ?? fallbackDate);
  const base = [dateLine, entrySourceText(entry)].filter(Boolean).join(JOURNAL_COPY_LINE_BREAK);
  return appendReflections(base, entry.reflectionList, mode);
}

/** 복사 대상 엔트리 + 소속 일자 */
export type DatedEntry = { entry: JournalEntry; stdrdDt: string };

/**
 * 여러 엔트리 복사 텍스트 (웹 검색 팝업 copyAll · 스레드 복사와 같은 블록 계약).
 * 날짜가 바뀔 때만 `날짜 (요일)` 헤더, 각 엔트리는 `#순번` + 본문 (+ 리플렉션), 엔트리 사이 빈 줄.
 */
export function buildEntriesCopyText(items: DatedEntry[], mode: CopyReflectionMode = "full"): string {
  let prevDate: string | null = null;
  const blocks = items.map(({ entry, stdrdDt }) => {
    const dateLabel = copyDateLabel(entry.stdrdDt ?? stdrdDt);
    let block = "";
    if (dateLabel !== prevDate) {
      block += `${JOURNAL_COPY_LINE_BREAK}${dateLabel}${JOURNAL_COPY_LINE_BREAK}`;
      prevDate = dateLabel;
    }
    block += [`#${entry.sortOrder ?? ""}`, entrySourceText(entry)].join(JOURNAL_COPY_LINE_BREAK);
    return appendReflections(block, entry.reflectionList, mode);
  });
  return blocks.join(BLOCK_SEPARATOR).trim();
}

/** 결과 목록 복사의 엔트리 유형 라벨 */
const CONTENT_TYPE_COPY_LABELS: Record<string, string> = {
  JOURNAL_DIARY: "일기",
  JOURNAL_DREAM: "꿈",
  JOURNAL_NOTE: "노트",
  JOURNAL_REFLECTION: "해석"
};

/** 결과 목록 복사 대상 — 화면 순서 그대로. stdrdDt 는 entry.stdrdDt 가 없을 때 쓸 일자 */
export type ResultListCopyItem = { entry: JournalEntry; stdrdDt?: string | null };

/**
 * 결과 목록(검색·태그별 기록·스레드 글 목록) 복사 텍스트 — 웹 copyAll 블록 계약에 머리줄과 엔트리 머리줄을 더했다.
 * - 머리줄(headerLines, 예: `검색: 바다 · 일기 · 12건`) 다음 빈 줄
 * - 날짜가 바뀔 때만 `날짜 (요일)` 줄 (날짜를 모르는 엔트리는 날짜 줄 없음)
 * - 엔트리마다 `유형 #순번 · 제목`(순번·제목은 있을 때만) + 본문 (+ 해석), 엔트리 사이 빈 줄
 * - 순서는 호출부가 넘긴 화면 순서를 그대로 쓴다 (여기서 정렬하지 않는다)
 */
export function buildResultListCopyText(
  headerLines: string[],
  items: ResultListCopyItem[],
  mode: CopyReflectionMode = "full"
): string {
  let prevDate: string | null = null;
  const blocks = items.map(({ entry, stdrdDt }) => {
    const lines: string[] = [];
    const dateLabel = copyDateLabel(entry.stdrdDt ?? stdrdDt);
    if (dateLabel && dateLabel !== prevDate) {
      lines.push(dateLabel);
      prevDate = dateLabel;
    }
    const typeLabel = CONTENT_TYPE_COPY_LABELS[entry.contentType] ?? "기록";
    const title = entry.title?.trim();
    lines.push(`${typeLabel}${entry.sortOrder != null ? ` #${entry.sortOrder}` : ""}${title ? ` · ${title}` : ""}`);
    const body = entrySourceText(entry);
    if (body) lines.push(body);
    return appendReflections(lines.join(JOURNAL_COPY_LINE_BREAK), entry.reflectionList, mode);
  });
  const header = headerLines.map((line) => line.trim()).filter(Boolean).join(JOURNAL_COPY_LINE_BREAK);
  return [header, ...blocks].filter(Boolean).join(BLOCK_SEPARATOR).trim();
}

/**
 * 일자 하나의 복사 대상 엔트리 (화면 순서: 챕터 순 → 꿈).
 * 다른 엔트리의 reflectionList 로 이미 붙는 리플렉션은 중복되지 않게 뺀다.
 */
export function entriesOfDay(day: JournalDay): DatedEntry[] {
  const all = [
    ...(day.journalChapterList ?? []).flatMap((ch) => chapterEntries(ch)),
    ...dreamEntriesFromDay(day)
  ].filter((e): e is JournalEntry => e != null);
  const attachedReflectionIds = new Set(
    all.flatMap((e) => (e.reflectionList ?? []).map((r) => r?.id)).filter((id): id is number => id != null)
  );
  return all
    .filter((e) => !(e.contentType === "JOURNAL_REFLECTION" && attachedReflectionIds.has(e.id)))
    .map((entry) => ({ entry, stdrdDt: day.stdrdDt }));
}

/** 여러 일자(주·월)의 복사 대상 엔트리 — 일자 오름차순 */
export function entriesOfDays(days: JournalDay[]): DatedEntry[] {
  return days
    .slice()
    .sort((a, b) => a.stdrdDt.localeCompare(b.stdrdDt))
    .flatMap(entriesOfDay);
}