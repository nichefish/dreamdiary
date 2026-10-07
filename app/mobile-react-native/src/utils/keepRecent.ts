/** 최근 완료 판단에 사용하는 서버 lifecycle 필드. */
interface CompletionItem {
  lifecycle?: { lifecycleKey?: string | null; resolvedAt?: string | null } | null;
}

export const KEEP_RECENT_DURATION_MS = 60 * 60 * 1000;
export const KEEP_RECENT_PREVIEW_SIZE = 5;

/** 시간대가 명시된 서버 완료 시각만 절대 시각으로 해석한다. */
function completionTime(item: CompletionItem): number {
  const value = item.lifecycle?.resolvedAt;
  return value && /(?:Z|[+-]\d{2}:\d{2})$/.test(value) ? Date.parse(value) : Number.NaN;
}

/** 현재 RESOLVED이며 완료 후 0 이상 1시간 미만인 항목만 최근 완료다. */
export function isRecentKeep(item: CompletionItem, now: number): boolean {
  const age = now - completionTime(item);
  return item.lifecycle?.lifecycleKey === "RESOLVED" && age >= 0 && age < KEEP_RECENT_DURATION_MS;
}

/** 최근 완료를 최신 완료 순으로 반환하며 동시 완료의 입력 순서를 유지한다. */
export function recentKeeps<T extends CompletionItem>(items: T[], now: number): T[] {
  return items.filter((item) => isRecentKeep(item, now))
    .sort((a, b) => completionTime(b) - completionTime(a));
}

/** 표시 중인 최근 완료가 처음 만료되는 절대 시각. 해당 항목이 없으면 null이다. */
export function nextKeepCompletionExpiry(items: CompletionItem[], now: number): number | null {
  const recent = recentKeeps(items, now);
  return recent.length ? Math.min(...recent.map((item) => completionTime(item) + KEEP_RECENT_DURATION_MS)) : null;
}
