/**
 * 저널 날짜 유틸.
 * 주간 뷰용 (백엔드 DateUtils.getWeekStartDateStr 와 동일: 월요일 시작) +
 * 클립보드 복사 등에서 사용하는 요일 문자열 변환.
 */

/** YYYY-MM-DD → 요일 한자(일/월/화/수/목/금/토). stdrdDt 가 없으면 빈 문자열.
 * 변경 후: 기존 한국어 한자 표기를 보존하면서 현재 locale의 공용 요일 카탈로그를 사용한다. */
export function getWeekDayStr(stdrdDt: string | null | undefined, t: (key: string) => string): string {
  if (!stdrdDt?.trim()) return "";
  const days = ["sun", "mon", "tue", "wed", "thu", "fri", "sat"] as const;
  const d = new Date(stdrdDt.includes("T") ? stdrdDt : `${stdrdDt}T12:00:00`);
  const day = days[d.getDay()];
  return day ? t(`journal.weekday.${day}`) : "";
}

/**
 * 사용자가 입력한 날짜 문자열을 YYYY-MM-DD 로 정규화한다.
 * 허용: "2026-10-8", "2026.10.08", "2026/10/08", "2026 10 08", "20261008", "2026년 10월 8일", 앞뒤 공백.
 * 실제 달력에 없는 날짜(2026-02-30 등)는 거절한다.
 * (변경 전: 입력값을 그대로 전송해 서버 @Pattern 검증에서 HTTP 400.)
 * @param raw 입력 문자열
 * @returns 비어 있으면 "", 유효하지 않으면 null, 유효하면 YYYY-MM-DD
 */
export function normalizeJournalDateInput(raw: string | null | undefined): string | null {
  const value = (raw ?? "").trim();
  if (!value) return "";
  const match =
    /^(\d{4})\s*[-./\s]\s*(\d{1,2})\s*[-./\s]\s*(\d{1,2})\.?$/.exec(value) ??
    /^(\d{4})(\d{2})(\d{2})$/.exec(value) ??
    /^(\d{4})\s*년\s*(\d{1,2})\s*월\s*(\d{1,2})\s*일?$/.exec(value);
  if (!match) return null;
  const y = Number(match[1]);
  const m = Number(match[2]);
  const d = Number(match[3]);
  if (y < 1000 || m < 1 || m > 12 || d < 1 || d > 31) return null;
  const date = new Date(y, m - 1, d);
  if (date.getFullYear() !== y || date.getMonth() !== m - 1 || date.getDate() !== d) return null;
  return formatLocalDateStr(date);
}

/** Date → YYYY-MM-DD (로컬) */
export function formatLocalDateStr(date: Date): string {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, "0");
  const d = String(date.getDate()).padStart(2, "0");
  return `${y}-${m}-${d}`;
}

/**
 * 기준일이 속한 주의 월요일(YYYY-MM-DD).
 * @param stdrdDt YYYY-MM-DD 등; 없으면 오늘
 */
export function getWeekStartDateStr(stdrdDt?: string | null): string {
  const base = stdrdDt?.trim()
    ? new Date(stdrdDt.includes("T") ? stdrdDt : `${stdrdDt}T12:00:00`)
    : new Date();
  const d = new Date(base);
  const dow = d.getDay();
  const offset = dow === 0 ? -6 : 1 - dow;
  d.setDate(d.getDate() + offset);
  return formatLocalDateStr(d);
}

/**
 * 주간 조회용 weekStartDt. 백엔드는 weekStartDt·stdrdDt 가 모두 비면 빈 목록을 반환한다.
 */
export function resolveWeekStartDt(opts: {
  stdrdDt?: string | null;
  yy?: number;
  mnth?: number;
}): string {
  if (opts.stdrdDt?.trim()) {
    return getWeekStartDateStr(opts.stdrdDt);
  }
  const now = new Date();
  if (opts.yy != null && opts.mnth != null) {
    if (opts.yy === now.getFullYear() && opts.mnth === now.getMonth() + 1) {
      return getWeekStartDateStr(formatLocalDateStr(now));
    }
    const mm = String(opts.mnth).padStart(2, "0");
    return getWeekStartDateStr(`${opts.yy}-${mm}-01`);
  }
  return getWeekStartDateStr(formatLocalDateStr(now));
}
