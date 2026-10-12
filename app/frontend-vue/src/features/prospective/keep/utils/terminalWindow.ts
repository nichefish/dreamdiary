/**
 * 종결 Keep 최근 기간 분할·최근 종결 순 정렬.
 * 모바일 `app/mobile-react-native/src/utils/keepTerminalSort.ts`의 규칙을 웹으로 옮긴 것이다(앱 간 import 없음).
 */

/** 종결 시각 산정에 쓰는 서버 필드. */
export interface TerminalSortItem {
  id: number;
  /** changedAt = 현재 상태로 바뀐 시각, resolvedAt = 완료 전이 시각. 서버는 UTC ISO-8601(Z)로 보낸다. */
  lifecycle?: { changedAt?: string | null; resolvedAt?: string | null } | null;
  /** 서버 로컬(Asia/Seoul) `yyyy-MM-dd HH:mm:ss`. */
  updatedAt?: string | null;
  createdAt?: string | null;
}

/** 종결 보기 기본 표시 기간(일). 이보다 오래전에 끝난 항목은 `이전 종결 N개 더 보기` 뒤로 접는다. */
export const KEEP_TERMINAL_WINDOW_DAYS = 7;

/** 시간대 없는 서버 시각의 기준 오프셋 (Asia/Seoul, DST 없음). */
const SERVER_LOCAL_OFFSET = "+09:00";

/**
 * 서버 시각 → epoch ms. 시간대가 붙은 값은 그대로, 없는 `yyyy-MM-dd[ HH:mm[:ss]]`는 Asia/Seoul 로 해석한다.
 * 브라우저 시간대와 무관하다. 형식이 다르면 NaN.
 */
export function parseServerTime(value: string | null | undefined): number {
  const text = (value ?? "").trim();
  if (!text) return Number.NaN;
  if (/(?:Z|[+-]\d{2}:\d{2})$/.test(text)) return Date.parse(text);
  const m = /^(\d{4})-(\d{2})-(\d{2})(?:[ T](\d{2}):(\d{2})(?::(\d{2}))?)?/.exec(text);
  if (!m) return Number.NaN;
  return Date.parse(`${m[1]}-${m[2]}-${m[3]}T${m[4] ?? "00"}:${m[5] ?? "00"}:${m[6] ?? "00"}${SERVER_LOCAL_OFFSET}`);
}

/** 종결 시각: changedAt → resolvedAt → updatedAt → createdAt. 모두 없으면 NaN. */
export function keepTerminalTime(item: TerminalSortItem): number {
  for (const value of [item.lifecycle?.changedAt, item.lifecycle?.resolvedAt, item.updatedAt, item.createdAt]) {
    const time = parseServerTime(value);
    if (Number.isFinite(time)) return time;
  }
  return Number.NaN;
}

/** 최근 종결 순(내림차순). 시각이 없는 항목은 맨 아래, 같은 시각은 id 내림차순. */
export function byTerminalRecentDesc(a: TerminalSortItem, b: TerminalSortItem): number {
  const left = keepTerminalTime(a);
  const right = keepTerminalTime(b);
  const leftOk = Number.isFinite(left);
  const rightOk = Number.isFinite(right);
  if (leftOk && rightOk && left !== right) return right - left;
  if (leftOk !== rightOk) return leftOk ? -1 : 1;
  return b.id - a.id;
}

/**
 * 종결 항목을 최근 기간(now 기준 days×24시간 이내)과 그 이전으로 나눈다. 입력 순서를 유지한다.
 * 시각을 알 수 없는 항목은 이전으로, 미래 시각(시계 차이)은 최근으로 본다.
 */
export function splitTerminalByWindow<T extends TerminalSortItem>(
  items: T[],
  now: number,
  days: number = KEEP_TERMINAL_WINDOW_DAYS,
): { recent: T[]; older: T[] } {
  const cutoff = now - days * 24 * 60 * 60 * 1000;
  const recent: T[] = [];
  const older: T[] = [];
  for (const item of items) {
    const time = keepTerminalTime(item);
    (Number.isFinite(time) && time >= cutoff ? recent : older).push(item);
  }
  return { recent, older };
}
