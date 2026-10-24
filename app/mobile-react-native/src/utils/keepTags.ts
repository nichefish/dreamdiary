/**
 * Keep 태그 선택 규칙 (빠른 추가·수정 시트). 웹 Keep 폼(TagifyEditor + prospectiveKeepCategoryMap)과 같은 자료를 쓴다.
 * - 후보: `GET /api/prospective/keep/tag/categories` → rsltMap { 태그명: [카테고리…] } (웹 자동완성 맵). 이미 있는 태그만 고른다.
 * - 전송: 웹 Tagify 직렬화와 같은 `tag.tagListStr` = JSON `[{"value":이름,"data":{"ctgr":카테고리}}]`(카테고리 없으면 data 생략), 없으면 빈 값.
 */

/** 선택한 태그 하나 (이름 + 카테고리, 카테고리 없으면 null) */
export interface KeepTagPick {
  name: string;
  ctgr: string | null;
}

/** 후보 맵을 웹 normalizeCategoryMap 과 같이 정리한다. */
export function normalizeKeepTagCategoryMap(raw: unknown): Record<string, string[]> {
  if (!raw || typeof raw !== "object" || Array.isArray(raw)) return {};
  const result: Record<string, string[]> = {};
  for (const [tagName, categories] of Object.entries(raw as Record<string, unknown>)) {
    if (!tagName.trim() || !Array.isArray(categories)) continue;
    result[tagName] = categories.map((c) => String(c ?? "")).filter(Boolean);
  }
  return result;
}

export function keepTagKey(tag: KeepTagPick): string {
  return `${tag.ctgr ?? ""}\u0000${tag.name}`;
}

/** 후보 맵을 (이름, 카테고리) 쌍으로 펼친다. 카테고리가 없는 태그는 카테고리 없이 한 번. 이름·카테고리 순. */
export function keepTagCandidates(map: Record<string, string[]>): KeepTagPick[] {
  const out: KeepTagPick[] = [];
  for (const [name, categories] of Object.entries(map)) {
    if (categories.length === 0) out.push({ name, ctgr: null });
    else for (const ctgr of categories) out.push({ name, ctgr });
  }
  return out.sort((a, b) => a.name.localeCompare(b.name) || (a.ctgr ?? "").localeCompare(b.ctgr ?? ""));
}

/** 검색어(이름·카테고리 부분 일치, 대소문자 무시)로 거르고 이미 고른 태그는 뺀다. */
export function filterKeepTagCandidates(candidates: KeepTagPick[], query: string, selected: KeepTagPick[], limit = 30): KeepTagPick[] {
  const q = query.trim().toLowerCase();
  const picked = new Set(selected.map(keepTagKey));
  return candidates
    .filter((c) => !picked.has(keepTagKey(c)))
    .filter((c) => !q || c.name.toLowerCase().includes(q) || (c.ctgr ?? "").toLowerCase().includes(q))
    .slice(0, limit);
}

/** 고른 태그를 넣거나 뺀다(같은 이름·카테고리면 제거). */
export function toggleKeepTag(selected: KeepTagPick[], tag: KeepTagPick): KeepTagPick[] {
  const key = keepTagKey(tag);
  return selected.some((t) => keepTagKey(t) === key) ? selected.filter((t) => keepTagKey(t) !== key) : [...selected, tag];
}

/** 웹 Tagify 와 같은 `tag.tagListStr` 값. 없으면 빈 문자열. */
export function serializeKeepTags(tags: KeepTagPick[]): string {
  if (tags.length === 0) return "";
  return JSON.stringify(tags.map((t) => (t.ctgr ? { value: t.name, data: { ctgr: t.ctgr } } : { value: t.name })));
}

/** Tagify JSON(서버 tagListStrWithCtgr 포함)을 태그 목록으로. 모양이 다르면 null. */
export function parseKeepTagListStr(raw: string): KeepTagPick[] | null {
  try {
    const parsed: unknown = JSON.parse(raw);
    if (!Array.isArray(parsed)) return null;
    const out: KeepTagPick[] = [];
    for (const item of parsed) {
      const value = (item as { value?: unknown } | null)?.value;
      if (typeof value !== "string" || !value.trim()) return null;
      const ctgr = (item as { data?: { ctgr?: unknown } | null }).data?.ctgr;
      if (ctgr != null && typeof ctgr !== "string") return null;
      out.push({ name: value, ctgr: ctgr ? ctgr : null });
    }
    return out;
  } catch {
    return null;
  }
}

/**
 * 상세 응답의 현재 태그(TagCmpstn: list + tagListStrWithCtgr). 태그가 없으면 [], 확인할 수 없으면 null(수정 저장을 막는다).
 * 서버는 태그가 없으면 tagListStrWithCtgr 를 null 로 준다.
 */
export function keepDetailTags(detail: { tag?: unknown }): KeepTagPick[] | null {
  if (!Object.prototype.hasOwnProperty.call(detail, "tag")) return null;
  const tag = detail.tag as ({ list?: unknown[] | null; tagListStrWithCtgr?: unknown } | null | undefined);
  const count = Array.isArray(tag?.list) ? tag!.list!.filter((t) => t != null).length : 0;
  const raw = tag?.tagListStrWithCtgr;
  if (count === 0) return raw == null ? [] : null;
  if (typeof raw !== "string") return null;
  const parsed = parseKeepTagListStr(raw);
  return parsed != null && parsed.length === count ? parsed : null;
}
