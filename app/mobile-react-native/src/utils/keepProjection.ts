import {
  isKeepTerminal,
  keepHasState,
  type KeepItem,
  type KeepPrefixOption,
  type KeepTagOption
} from "../types/prospectiveKeep";
import { displayTagsOf } from "../types/journalDay";
import { daysUntilExpire, normalizeExpireDt } from "./keepExpire";

/**
 * Keep 목록 클라이언트 투영 — 웹 KeepPage.vue 의 filtered / openItems / resolvedItems 규칙을 옮기고 `기한` 보기를 더했다.
 * 서버는 전 범위(scope=all)를 sortOrder → id 순으로 한 번에 주고(페이징 없음), 거르기·정렬은 모두 여기서 한다.
 */

/** Project 범위 — 전체 · 미지정 · Project ID */
export type KeepProjectScope = "ALL" | "UNASSIGNED" | number;

/** 보기 — 진행(OPEN·PENDING) · 기한(진행 중 기한 있는 것) · 종결(종결 4종) */
export type KeepView = "OPEN" | "DUE" | "TERMINAL";

export interface KeepFilters {
  scope: KeepProjectScope;
  /** 제출된 검색어 (제목·본문 부분 일치, 대소문자 무시) */
  keyword: string;
  prefixId: number | null;
  tagId: number | null;
  /** 기한 지남 — 종결 안 된 채 기한이 지난 항목만 (웹 filterOverdue) */
  overdue: boolean;
  /** 중요(IMPRTC)만 (웹 filterImportant) */
  important: boolean;
}

export const EMPTY_KEEP_FILTERS: KeepFilters = {
  scope: "ALL",
  keyword: "",
  prefixId: null,
  tagId: null,
  overdue: false,
  important: false
};

/** 기한이 지났는데 아직 종결되지 않은 항목 (웹 isOverdueOpen) */
export function isKeepOverdueOpen(item: KeepItem, today: Date): boolean {
  if (!normalizeExpireDt(item.expireDt)) return false;
  if (isKeepTerminal(item)) return false;
  return daysUntilExpire(item.expireDt, today) < 0;
}

function hasTag(item: KeepItem, tagId: number): boolean {
  return displayTagsOf(item.tag).some((tag) => tag.tagId === tagId);
}

/** Project 범위 · 말머리 · 태그 · 기한 지남 · 중요 · 검색어 (모두 AND, 웹 filtered) */
export function filterKeeps(list: KeepItem[], filters: KeepFilters, today: Date): KeepItem[] {
  const k = filters.keyword.trim().toLowerCase();
  return list.filter((it) => {
    if (filters.scope === "UNASSIGNED" && it.projectId != null) return false;
    if (typeof filters.scope === "number" && it.projectId !== filters.scope) return false;
    if (filters.prefixId != null && it.prefix?.id !== filters.prefixId) return false;
    if (filters.tagId != null && !hasTag(it, filters.tagId)) return false;
    if (filters.overdue && !isKeepOverdueOpen(it, today)) return false;
    if (filters.important && !keepHasState(it, "IMPRTC")) return false;
    if (!k) return true;
    return (it.title ?? "").toLowerCase().includes(k) || (it.content ?? "").toLowerCase().includes(k);
  });
}

/** 기한 오름차순 비교자. 기한 없는 항목은 뒤로 (웹 byExpireDtAsc). Array.sort 는 안정 정렬이라 서버 순서가 보존된다 */
function byExpireDtAsc(a: KeepItem, b: KeepItem): number {
  const left = normalizeExpireDt(a.expireDt);
  const right = normalizeExpireDt(b.expireDt);
  if (left === right) return 0;
  if (!left) return 1;
  if (!right) return -1;
  return left < right ? -1 : 1;
}

/** 진행 = OPEN·PENDING, 기한 오름차순(지난 기한이 맨 위), 기한 없음은 아래에 서버 순서 (웹 openItems) */
export function openKeeps(list: KeepItem[]): KeepItem[] {
  return list.filter((it) => !isKeepTerminal(it)).slice().sort(byExpireDtAsc);
}

/** 종결 = RESOLVED·FAILED·EXPIRED·DROPPED, 서버 순서 그대로 (웹 resolvedItems) */
export function terminalKeeps(list: KeepItem[]): KeepItem[] {
  return list.filter((it) => isKeepTerminal(it));
}

export type KeepDueBucket = "PAST" | "TODAY" | "WEEK" | "LATER";

export const KEEP_DUE_BUCKET_LABELS: Record<KeepDueBucket, string> = {
  PAST: "지남",
  TODAY: "오늘",
  WEEK: "7일 이내",
  LATER: "그 이후"
};

export type KeepDueGroup = { bucket: KeepDueBucket; label: string; items: KeepItem[] };

/**
 * 기한 보기 — 진행 항목 중 기한 있는 것만 지남 / 오늘 / 7일 이내(1~7일) / 그 이후로 묶는다. 빈 묶음은 뺀다.
 * 묶음 안은 진행 정렬(기한 오름차순, 같은 기한은 서버 순서)을 따른다.
 */
export function dueKeepGroups(list: KeepItem[], today: Date): KeepDueGroup[] {
  const buckets: Record<KeepDueBucket, KeepItem[]> = { PAST: [], TODAY: [], WEEK: [], LATER: [] };
  for (const item of openKeeps(list)) {
    const days = daysUntilExpire(item.expireDt, today);
    if (!Number.isFinite(days)) continue;
    const bucket: KeepDueBucket = days < 0 ? "PAST" : days === 0 ? "TODAY" : days <= 7 ? "WEEK" : "LATER";
    buckets[bucket].push(item);
  }
  return (Object.keys(buckets) as KeepDueBucket[])
    .filter((bucket) => buckets[bucket].length > 0)
    .map((bucket) => ({ bucket, label: KEEP_DUE_BUCKET_LABELS[bucket], items: buckets[bucket] }));
}

/** 보기별 건수 (필터 적용 후) */
export function keepViewCounts(filtered: KeepItem[], today: Date): Record<KeepView, number> {
  const open = openKeeps(filtered);
  return {
    OPEN: open.length,
    DUE: open.filter((it) => Number.isFinite(daysUntilExpire(it.expireDt, today))).length,
    TERMINAL: terminalKeeps(filtered).length
  };
}

/**
 * 태그 필터 선택지 — 목록에 실제로 붙어 있는 태그만, 건수 = 그 태그가 붙은 Keep 수(전 범위 목록 기준).
 * 순서는 서버 태그 목록(GET /api/tags?contentType=PROSPECTIVE_KEEP, 서버 정렬)을 따르고, 서버 목록에 없는 태그는 뒤에 붙인다.
 * (서버 contentSize 는 집계 범위가 Keep 전용인지 보장되지 않아 쓰지 않는다. 서버 목록을 못 받았으면 목록 순서만 쓴다.)
 */
export function mergeKeepTagOptions(serverTags: KeepTagOption[] | null, list: KeepItem[]): KeepTagOption[] {
  const counts = new Map<number, KeepTagOption>();
  for (const item of list) {
    for (const tag of displayTagsOf(item.tag)) {
      if (!Number.isFinite(tag.tagId)) continue;
      const prev = counts.get(tag.tagId);
      counts.set(tag.tagId, prev
        ? { ...prev, contentSize: prev.contentSize + 1 }
        : { id: tag.tagId, name: tag.name, ctgr: tag.ctgr, contentSize: 1 });
    }
  }
  const ordered: KeepTagOption[] = [];
  for (const tag of serverTags ?? []) {
    const local = counts.get(tag.id);
    if (!local) continue;
    ordered.push(local);
    counts.delete(tag.id);
  }
  return [...ordered, ...counts.values()];
}

/**
 * 말머리 필터 선택지 — 활성 개인 말머리(GET /api/my/prefixes/options) 순서를 쓰고,
 * 비활성이 되었지만 Keep 에 남아 있는 말머리는 뒤에 붙인다.
 */
export function mergeKeepPrefixOptions(serverPrefixes: KeepPrefixOption[] | null, list: KeepItem[]): KeepPrefixOption[] {
  const base = (serverPrefixes ?? []).slice();
  const seen = new Set(base.map((p) => p.id));
  for (const item of list) {
    const id = item.prefix?.id;
    const name = item.prefix?.name?.trim();
    if (id == null || !name || seen.has(id)) continue;
    seen.add(id);
    base.push({ id, name, sortOrder: item.prefix?.sortOrder ?? Number.MAX_SAFE_INTEGER });
  }
  return base;
}

/** 표시용 등록 년월 `2026.09` (웹 registeredMonth). 형식이 다르면 "" */
export function keepRegisteredMonth(item: KeepItem): string {
  const m = /^(\d{4})-(\d{2})/.exec(item.createdAt ?? "");
  return m ? `${m[1]}.${m[2]}` : "";
}

/** 서버 일시 문자열(yyyy-MM-dd HH:mm:ss 또는 ISO) → `2026.10.04 21:48`. 형식이 다르면 원문 */
export function formatKeepDateTime(value: string | null | undefined): string {
  const raw = (value ?? "").trim();
  const m = /^(\d{4})-(\d{2})-(\d{2})(?:[ T](\d{2}):(\d{2}))?/.exec(raw);
  if (!m) return raw;
  const date = `${m[1]}.${m[2]}.${m[3]}`;
  return m[4] != null ? `${date} ${m[4]}:${m[5]}` : date;
}
