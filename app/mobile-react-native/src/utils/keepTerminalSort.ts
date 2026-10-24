/**
 * 종결 Keep 정렬 — 최근에 끝난 항목이 위로.
 * 의존성 없는 순수 모듈 (tests/keep-terminal-sort.test.cjs 가 그대로 로드한다).
 */

/** 정렬에 쓰는 서버 필드 */
export interface TerminalSortItem {
  id: number;
  /** changedAt = 현재 상태로 바뀐 시각(V0.30.16), resolvedAt = 완료 전이 시각. 둘 다 UTC ISO-8601 */
  lifecycle?: { changedAt?: string | null; resolvedAt?: string | null } | null;
  /** 서버 로컬 시각 yyyy-MM-dd HH:mm:ss */
  updatedAt?: string | null;
  createdAt?: string | null;
}

/** 시간대가 붙은 서버 시각(UTC ISO-8601)만 절대 시각으로 쓴다 (keepRecent 와 같은 규칙) */
function zonedTime(value: string | null | undefined): number {
  return value && /(?:Z|[+-]\d{2}:\d{2})$/.test(value) ? Date.parse(value) : Number.NaN;
}

/** yyyy-MM-dd HH:mm:ss(또는 yyyy-MM-dd) → 기기 로컬 시각 epoch. 형식이 다르면 NaN */
function localTime(value: string | null | undefined): number {
  const m = /^(\d{4})-(\d{2})-(\d{2})(?:[ T](\d{2}):(\d{2})(?::(\d{2}))?)?/.exec((value ?? "").trim());
  if (!m) return Number.NaN;
  return new Date(+m[1], +m[2] - 1, +m[3], +(m[4] ?? 0), +(m[5] ?? 0), +(m[6] ?? 0)).getTime();
}

/** 종결 시각: 상태 변경 시각(changedAt) → 완료 전이 시각(resolvedAt) → Keep 수정일시 → 등록일시. 모두 없으면 NaN */
export function keepTerminalSortTime(item: TerminalSortItem): number {
  const changed = zonedTime(item.lifecycle?.changedAt);
  if (Number.isFinite(changed)) return changed;
  const resolved = zonedTime(item.lifecycle?.resolvedAt);
  if (Number.isFinite(resolved)) return resolved;
  const updated = localTime(item.updatedAt);
  if (Number.isFinite(updated)) return updated;
  return localTime(item.createdAt);
}

/** 최근 종결 순(내림차순). 시각이 없는 항목은 맨 아래, 같은 시각은 id 내림차순 */
export function byTerminalRecentDesc(a: TerminalSortItem, b: TerminalSortItem): number {
  const left = keepTerminalSortTime(a);
  const right = keepTerminalSortTime(b);
  const leftOk = Number.isFinite(left);
  const rightOk = Number.isFinite(right);
  if (leftOk && rightOk && left !== right) return right - left;
  if (leftOk !== rightOk) return leftOk ? -1 : 1;
  return b.id - a.id;
}

/** 종결 보기 기본 표시 기간(일). 이보다 오래전에 끝난 항목은 `이전 종결 N개 더 보기` 뒤로 접는다. */
export const KEEP_TERMINAL_WINDOW_DAYS = 7;

/**
 * 종결 항목을 최근 기간(기본 7일, now 기준 7×24시간 이내)과 그 이전으로 나눈다. 입력 순서를 유지한다.
 * 종결 시각은 `keepTerminalSortTime`과 같다(changedAt → resolvedAt → updatedAt → createdAt; 서버 로컬 형식은 기기 로컬=Asia/Seoul로 해석).
 * 시각을 알 수 없는 항목은 이전으로, 미래 시각(기기 시계 차이)은 최근으로 본다.
 */
export function splitTerminalByWindow<T extends TerminalSortItem>(items: T[], now: number, days: number = KEEP_TERMINAL_WINDOW_DAYS): { recent: T[]; older: T[] } {
  const cutoff = now - days * 24 * 60 * 60 * 1000;
  const recent: T[] = [];
  const older: T[] = [];
  for (const item of items) {
    const time = keepTerminalSortTime(item);
    (Number.isFinite(time) && time >= cutoff ? recent : older).push(item);
  }
  return { recent, older };
}
