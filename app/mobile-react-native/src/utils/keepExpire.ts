/**
 * Keep 유효 기한(expireDt) 표기 — 웹 features/prospective/keep/utils/expireDt.ts 의 읽기용 함수 포트.
 * (dateText → expireDt 추출(parseExpireDt·scanLastDate)은 쓰기 단계(2-2)에서 옮긴다.)
 */

const ISO_DATE = /^(\d{4})-(\d{2})-(\d{2})$/;

/** 저장값을 yyyy-MM-dd 로 정규화. 비었거나 형식이 다르면 "" */
export function normalizeExpireDt(date: string | null | undefined): string {
  const raw = (date ?? "").trim().slice(0, 10);
  return ISO_DATE.test(raw) ? raw : "";
}

/**
 * 목록용 짧은 표기 (웹 formatExpireDt). 기준일과 같은 해면 `~10/25`, 다른 해면 `~27/01/03`.
 * @param date yyyy-MM-dd. 없거나 형식이 다르면 빈 문자열
 * @param today 기준일 (기본: 오늘)
 */
export function formatExpireDt(date: string | null | undefined, today: Date = new Date()): string {
  const m = ISO_DATE.exec(normalizeExpireDt(date));
  if (!m) return "";
  const [, year = "", month = "", day = ""] = m;
  if (Number(year) === today.getFullYear()) return `~${month}/${day}`;
  return `~${year.slice(2)}/${month}/${day}`;
}

/**
 * 기한까지 남은 일수 (웹 daysUntilExpire). 오늘이면 0, 지났으면 음수, 형식이 다르면 NaN.
 * @param date yyyy-MM-dd
 */
export function daysUntilExpire(date: string | null | undefined, today: Date = new Date()): number {
  const m = ISO_DATE.exec(normalizeExpireDt(date));
  if (!m) return Number.NaN;
  const base = new Date(today.getFullYear(), today.getMonth(), today.getDate());
  const target = new Date(Number(m[1]), Number(m[2]) - 1, Number(m[3]));
  return Math.round((target.getTime() - base.getTime()) / 86400000);
}

/**
 * D-day 표기 — 지났으면 `N일 지남`(웹 prospective.keep.expire-dt.relative.past), 오늘이면 `오늘`, 남았으면 `D-N`.
 * 형식이 다르면 "".
 */
export function ddayLabel(days: number): string {
  if (!Number.isFinite(days)) return "";
  if (days < 0) return `${-days}일 지남`;
  if (days === 0) return "오늘";
  return `D-${days}`;
}
