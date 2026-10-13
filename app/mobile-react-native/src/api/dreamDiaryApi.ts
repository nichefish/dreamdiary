import { normalizeKeepTagCategoryMap } from "../utils/keepTags";
import { toKeepEditFormData, toKeepFormData, type KeepCreateInput, type KeepEditInput } from "../utils/keepFormData";
import { ApiError, request } from "./client";
import type { AjaxResponse, AuthUser } from "../types/auth";
import type {
  JournalDay,
  JournalEntry,
  JournalThread,
  JournalThreadEntry,
  JournalThreadPeriodSummary
} from "../types/journalDay";
import type { JournalAnnual } from "../types/journalAnnual";
import type {
  KeepItem,
  KeepLifecycleKey,
  KeepStateKey,
  KeepPrefix,
  KeepPrefixOption,
  KeepProject,
  KeepTagOption
} from "../types/prospectiveKeep";
import type { MenuDto } from "../types/menu";
import { normalizeDateStr } from "../utils/date";

// ─── 인증 ─────────────────────────────────────────────

/**
 * 현재 인증 사용자 정보 조회 (JWT 쿠키 기반).
 * 앱 시작 시 로그인 전 상태 확인용이므로 401(비로그인)은 정상 흐름 — 오류 토스트를 띄우지 않는다.
 */
export function getAuthInfo() {
  return request<AjaxResponse<AuthUser>>("/api/auth/get-auth-info", {
    expectedErrorStatuses: [401]
  });
}

/** 로그인 — POST /api/auth/login (응답 Authorization 헤더에 access JWT) */
export function login(username: string, password: string) {
  return request<AjaxResponse<AuthUser>>("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password }),
    captureAccessToken: true
  });
}

/** access JWT 재발급 — POST /api/auth/refresh (쿠키 refresh 기반, WebSocket 토큰 갱신용) */
export function refreshAccessToken() {
  return request<AjaxResponse>("/api/auth/refresh", {
    method: "POST",
    body: JSON.stringify({}),
    captureAccessToken: true
  });
}

/** 로그아웃 — POST /api/auth/logout-json */
export function logout() {
  return request<AjaxResponse>("/api/auth/logout-json", {
    method: "POST",
    body: JSON.stringify({})
  });
}

// ─── 저널 일자 ─────────────────────────────────────────

/**
 * 선택 날짜의 저널일자를 등록한다. 소유자와 기본 요약 챕터는 서버가 확정한다.
 * Spring MVC DTO 바인딩에 맞춰 multipart로 전송하며, HTTP 200의 업무 실패도 오류로 반환한다.
 * @param dateStr 등록 대상 날짜 (YYYY-MM-DD)
 * @param weather 선택 날씨 입력 (최대 100자)
 */
export async function registerJournalDay(dateStr: string, weather: string) {
  const journalDate = normalizeDateStr(dateStr);
  if (!journalDate) throw new Error("유효하지 않은 날짜 형식입니다.");
  if (weather.length > 100) throw new Error("날씨는 100자 이내로 입력해 주세요.");
  const body = new FormData();
  body.append("journalDate", journalDate);
  body.append("journalDatePrecision", "EXACT");
  body.append("weather", weather);
  const response = await request<AjaxResponse<JournalDay>>("/api/journal/days", {
    method: "POST",
    body
  });
  if (response.rslt !== true) {
    throw new ApiError(response.message || "저널일자를 등록하지 못했습니다.", 200, response);
  }
  return response;
}

interface DailyListResponse {
  rslt: boolean;
  rsltList?: JournalDay[];
  message?: string;
}

/**
 * 일자 조회 공통 표시 플래그. 웹(journal 스토어 fetchDays)과 같이 일기·꿈을 모두 켠다.
 * 서버 기본값은 false이며, false면 챕터 요약 태그·엔트리 state 병합 등 일부 보강을 건너뛴다.
 */
const JOURNAL_DAY_SHOW_FLAGS = { showDiaries: true, showDreams: true } as const;

/**
 * 특정 날짜의 저널 일자 조회.
 * GET /api/journal/days?viewType=DAILY&stdrdDt=YYYY-MM-DD
 */
export function getDailyJournalDay(dateStr: string) {
  const safeDate = normalizeDateStr(dateStr);
  if (!safeDate) throw new Error("유효하지 않은 날짜 형식입니다.");
  return request<DailyListResponse>("/api/journal/days", {
    query: { viewType: "DAILY", stdrdDt: safeDate, ...JOURNAL_DAY_SHOW_FLAGS }
  });
}

/**
 * 월별 저널 일자 목록 조회 (달력 도트 표시용).
 * GET /api/journal/days?viewType=LIST&yy=YYYY&mnth=M
 * (서버 JournalDayViewType에 MONTHLY가 없으므로 웹 월간 화면과 동일하게 LIST + yy/mnth 사용)
 * @param yearMonth "YYYY-MM" 형식
 */
export function getMonthlyJournalDays(yearMonth: string) {
  const match = /^(\d{4})-(\d{2})$/.exec(yearMonth);
  if (!match) {
    throw new Error("유효하지 않은 연월 형식입니다.");
  }
  const yy = Number(match[1]);
  const mnth = Number(match[2]);
  if (mnth < 1 || mnth > 12) throw new Error("유효하지 않은 연월 형식입니다.");
  return request<DailyListResponse>("/api/journal/days", {
    query: { viewType: "LIST", yy, mnth, ...JOURNAL_DAY_SHOW_FLAGS }
  });
}

/**
 * 주별 저널 일자 목록 조회.
 * GET /api/journal/days?viewType=WEEKLY&weekStartDt=YYYY-MM-DD
 * @param weekStartDt 주 시작일(월요일) YYYY-MM-DD
 */
export function getWeeklyJournalDays(weekStartDt: string) {
  const safeWeekStart = normalizeDateStr(weekStartDt);
  if (!safeWeekStart) throw new Error("유효하지 않은 주 시작일 형식입니다.");
  return request<DailyListResponse>("/api/journal/days", {
    query: { viewType: "WEEKLY", weekStartDt: safeWeekStart, ...JOURNAL_DAY_SHOW_FLAGS }
  });
}

/**
 * 일자 태그로 저널 일자 검색.
 * GET /api/journal/days?viewType=SEARCH&tagId=&yy=
 * (웹 openDayFilterModal 과 같이 yy 를 생략하면 전체 연도)
 */
export function searchJournalDaysByTag(tagId: number, yy?: number | null) {
  if (!Number.isFinite(tagId) || (yy != null && !Number.isFinite(yy))) {
    throw new Error("유효하지 않은 태그 또는 연도입니다.");
  }
  return request<DailyListResponse>("/api/journal/days", {
    query: { viewType: "SEARCH", tagId, ...(yy != null ? { yy } : {}) }
  });
}

/**
 * 일자 태그가 붙은 연도 목록.
 * GET /api/journal/day/tag/{tagId}/years
 */
export function getJournalDayTagYears(tagId: number) {
  if (!Number.isFinite(tagId)) {
    throw new Error("유효하지 않은 태그 ID입니다.");
  }
  return request<{ rsltList?: Array<number | string> }>(`/api/journal/day/tag/${tagId}/years`);
}

// ─── 태그 클라우드 (월간) ────────────────────────────────

export interface TagCloudItem {
  id: number | string;
  name: string;
  ctgr?: string;
  contentSize: number;
  /** 빈도 크기 클래스 (ts-1 ~ ts-9). 서버가 빈도·프로필 크기 고정으로 확정 */
  tagClass?: string;
  /** 태그 프로필 색 클래스 (text-primary, text-danger, text-noti …). 없으면 빈 문자열 */
  textClass?: string;
}

interface TagListResponse {
  rslt: boolean;
  rsltList?: TagCloudItem[];
}

function normalizeTagList(rawList: unknown): TagCloudItem[] {
  if (!Array.isArray(rawList)) return [];

  function isTagCloudItem(item: TagCloudItem | null): item is TagCloudItem {
    return item != null;
  }

  return rawList
    .map((raw): TagCloudItem | null => {
      const item = raw as Record<string, unknown>;
      const id = item.id;
      const name = String(item.name ?? "");
      if (id === undefined || id === "" || !name) return null;
      return {
        id: id as number | string,
        name,
        ctgr: item.ctgr != null ? String(item.ctgr) : undefined,
        contentSize: Number(item.contentSize ?? 0),
        tagClass: item.tagClass != null ? String(item.tagClass) : undefined,
        textClass: item.textClass != null ? String(item.textClass) : undefined
      };
    })
    .filter(isTagCloudItem);
}


/**
 * 태그 클라우드 집계 기간 (웹 journal 스토어 getTagPeriodParams 와 동일).
 * 서버 TagSearchParam 은 좁은 기간을 우선한다: stdrdDt > weekStartDt > yy+mnth.
 */
export type TagCloudPeriod =
  | { stdrdDt: string }
  | { weekStartDt: string }
  | { yy: number; mnth: number };

/**
 * 기간 일자 태그 클라우드.
 * GET /api/journal/day/tags?stdrdDt= | weekStartDt= | yy=&mnth=
 */
export async function getJournalDayTagCloud(period: TagCloudPeriod) {
  const res = await request<TagListResponse>("/api/journal/day/tags", {
    query: { ...period }
  });
  return normalizeTagList(res.rsltList);
}

/**
 * 기간 일기/꿈 엔트리 태그 클라우드.
 * GET /api/journal/entry/tags?(기간)&type=DIARY|DREAM
 */
export async function getJournalEntryTagCloud(period: TagCloudPeriod, type: "DIARY" | "DREAM") {
  const res = await request<TagListResponse>("/api/journal/entry/tags", {
    query: { ...period, type }
  });
  return normalizeTagList(res.rsltList);
}

/**
 * 일자 태그 전체 카탈로그 (기간 파라미터 없음 → 서버가 사용자 일자 태그 전체를 돌려준다).
 * 웹 JournalDayMetaModal 태그 카탈로그와 같은 요청. 서버에 이름 검색 파라미터가 없어 검색 탭이 화면에서 부분 일치로 거른다.
 * GET /api/journal/day/tags
 */
export async function getJournalDayTagCatalog() {
  const res = await request<TagListResponse>("/api/journal/day/tags");
  return normalizeTagList(res.rsltList);
}

/**
 * 일기/꿈 엔트리 태그 전체 카탈로그 (기간 파라미터 없음 → 서버 getMyTagList).
 * 웹 엔트리 검색 태그 셀렉터(useSearchTagCatalog)와 같은 요청. 이름 검색은 화면에서 한다.
 * GET /api/journal/entry/tags?type=DIARY|DREAM
 */
export async function getJournalEntryTagCatalog(type: "DIARY" | "DREAM") {
  const res = await request<TagListResponse>("/api/journal/entry/tags", {
    query: { type }
  });
  return normalizeTagList(res.rsltList);
}

/** 백엔드 TagProfileDto 중 화면에 쓰는 필드 */
export interface TagProfile {
  tagId?: number;
  ctgr?: string | null;
  contentType?: string | null;
  /** 태그 설명 */
  content?: string | null;
  textClassCd?: string | null;
}

/**
 * 태그 프로필(설명) 조회 — 읽기 전용. 웹 useTagProfileAction 과 같은 요청.
 * GET /api/tags/{tagId}/profile?contentType=JOURNAL_DAY|JOURNAL_DIARY|JOURNAL_DREAM
 */
export async function getTagProfile(tagId: number, contentType: string) {
  if (!Number.isFinite(tagId)) throw new Error("유효하지 않은 태그 ID입니다.");
  const res = await request<AjaxResponse<TagProfile | null>>(`/api/tags/${tagId}/profile`, {
    query: { contentType }
  });
  return res.rsltObj ?? null;
}

// ─── 저널 스레드 (읽기 전용) ─────────────────────────────

/**
 * 기간 스레드 요약 조회 조건 (웹 JournalPeriodThreadSummary.vue periodQuery 와 동일).
 * 일: DAILY + stdrdDt, 주: WEEKLY + weekStartDt, 월: LIST + yy + mnth
 */
export type ThreadPeriodQuery =
  | { viewType: "DAILY"; stdrdDt: string }
  | { viewType: "WEEKLY"; weekStartDt: string }
  | { viewType: "LIST"; yy: number; mnth: number };

/**
 * 기간에 엔트리가 등장한 스레드 요약.
 * GET /api/journal/threads/period-summary?viewType=…
 */
export async function getJournalPeriodThreadSummary(query: ThreadPeriodQuery) {
  const res = await request<{ rslt: boolean; rsltList?: JournalThreadPeriodSummary[] }>(
    "/api/journal/threads/period-summary",
    { query: { ...query } }
  );
  return Array.isArray(res.rsltList) ? res.rsltList : [];
}

/**
 * 스레드 상세 (웹 journalThreadDetail 스토어와 같은 요청). 화면은 읽기 필드만 쓴다.
 * GET /api/journal/threads/{id} → rsltObj: JournalThreadDto
 */
export async function getJournalThread(threadId: number) {
  if (!Number.isFinite(threadId)) throw new Error("유효하지 않은 스레드 ID입니다.");
  const res = await request<AjaxResponse<JournalThread | null>>(`/api/journal/threads/${threadId}`);
  return res.rsltObj ?? null;
}

/**
 * 스레드 소속 엔트리 — full JournalEntryDto 목록 (웹 스레드 상세의 소속 엔트리 목록과 같은 요청).
 * 서버 정렬: 일자 → 챕터 sortOrder → 엔트리 sortOrder → ID 오름차순. 연관 스레드 합성(relatedThreadIds)은 쓰지 않는다.
 * 응답 엔트리에는 threadList 가 채워지지 않는다(빈 목록).
 * GET /api/journal/threads/{id}/entries → rsltList: JournalEntryDto[]
 */
export async function getJournalThreadEntries(threadId: number) {
  if (!Number.isFinite(threadId)) throw new Error("유효하지 않은 스레드 ID입니다.");
  const res = await request<{ rslt: boolean; rsltList?: JournalEntry[] }>(
    `/api/journal/threads/${threadId}/entries`
  );
  return Array.isArray(res.rsltList) ? res.rsltList.filter((e) => e != null) : [];
}

/**
 * 엔트리가 속한 스레드 목록 (등록 순).
 * GET /api/journal/entries/{entryId}/threads → rsltList: JournalThreadEntryDto[]
 */
export async function getJournalEntryThreads(entryId: number) {
  if (!Number.isFinite(entryId)) throw new Error("유효하지 않은 엔트리 ID입니다.");
  const res = await request<{ rslt: boolean; rsltList?: JournalThreadEntry[] }>(
    `/api/journal/entries/${entryId}/threads`
  );
  return Array.isArray(res.rsltList) ? res.rsltList.filter((t) => t != null && t.threadId != null) : [];
}

/** Spring Page 직렬화 (웹 journalThreadList 가 읽는 content·totalElements·totalPages·number). VIA_DTO 모양(page{})도 받아 준다 */
interface SpringPage<T> {
  content?: T[] | null;
  totalElements?: number | null;
  totalPages?: number | null;
  number?: number | null;
  page?: { totalElements?: number | null; totalPages?: number | null; number?: number | null } | null;
}

export interface JournalThreadPage {
  content: JournalThread[];
  totalElements: number;
  totalPages: number;
  /** 0-based 페이지 번호 */
  page: number;
}

/**
 * 스레드 목록 (페이지) — 웹 스레드 목록(journalThreadList.fetchList)과 같은 요청. 정렬은 서버(소속 엔트리 최신 날짜 기준).
 * keyword 가 있으면 제목 검색(searchType=title).
 * GET /api/journal/threads?page=&size=(&searchType=title&searchKeyword=) → rsltObj: Page<JournalThreadDto>
 */
export async function getJournalThreadPage(params: { page: number; size: number; keyword?: string }): Promise<JournalThreadPage> {
  const keyword = params.keyword?.trim();
  const res = await request<AjaxResponse<SpringPage<JournalThread>>>("/api/journal/threads", {
    query: {
      page: params.page,
      size: params.size,
      ...(keyword ? { searchType: "title", searchKeyword: keyword } : {})
    }
  });
  const p = res.rsltObj ?? {};
  return {
    content: (p.content ?? []).filter((t): t is JournalThread => t != null && Number.isFinite(Number(t.id))),
    totalElements: p.totalElements ?? p.page?.totalElements ?? 0,
    totalPages: p.totalPages ?? p.page?.totalPages ?? 0,
    page: p.number ?? p.page?.number ?? params.page
  };
}

// ─── 저널 결산 (읽기 전용) ─────────────────────────────

/**
 * 연간 결산 목록 — 웹 결산 목록(journalAnnual.fetchList)과 같은 요청.
 * GET /api/journal/annuals → rsltList: JournalAnnualDto[]
 */
export async function getJournalAnnualList(): Promise<JournalAnnual[]> {
  const res = await request<{ rslt: boolean; rsltList?: Array<JournalAnnual | null> }>("/api/journal/annuals");
  return Array.isArray(res.rsltList) ? res.rsltList.filter((a): a is JournalAnnual => a != null) : [];
}

/**
 * 전체 기간 꿈 기록 집계 (결산 목록 상단 카드).
 * GET /api/journal/annual/total → rsltObj: JournalAnnualDto (dreamDayCnt·dreamCnt)
 */
export async function getJournalAnnualTotal(): Promise<JournalAnnual | null> {
  const res = await request<AjaxResponse<JournalAnnual | null>>("/api/journal/annual/total");
  return res.rsltObj ?? null;
}

/**
 * 연간 결산 상세 (요약·리뷰).
 * GET /api/journal/annual/{yy} → rsltObj: JournalAnnualDto (journalAnnualReviewList 포함)
 */
export async function getJournalAnnual(yy: number): Promise<JournalAnnual | null> {
  if (!Number.isFinite(yy)) throw new Error("유효하지 않은 연도입니다.");
  const res = await request<AjaxResponse<JournalAnnual | null>>(`/api/journal/annual/${yy}`);
  return res.rsltObj ?? null;
}

// ─── Keep 전수관리 (읽기 전용, 모바일 2-0) ───────────────

/**
 * 읽기 응답의 rslt 를 확인한다. 서버가 HTTP 200 + rslt=false(+message)로 실패를 알리는 경우를 오류로 바꾼다.
 * (쓰기용 rslt 래퍼는 2-1 에서 따로 둔다.)
 */
function assertReadOk<R extends { rslt?: boolean; message?: string | null }>(res: R | null | undefined, fallback: string): R {
  if (res == null || res.rslt !== true) {
    const message = typeof res?.message === "string" && res.message.trim() ? res.message.trim() : fallback;
    throw new ApiError(message, 200, res);
  }
  return res;
}

function isKeepItem(item: KeepItem | null | undefined): item is KeepItem {
  return item != null && Number.isFinite(Number(item.id));
}

/**
 * Keep 전 범위 목록 — 웹 KeepPage(fetchList)와 같은 요청. 진행·종결을 함께, 페이징 없이 sortOrder → id 순으로 받는다.
 * 항목마다 lifecycle(없으면 OPEN)·state·tag·prefix 가 붙어 온다. Project 는 projectId 만.
 * GET /api/prospective/keeps?scope=all → rsltList: ProspectiveKeepDto[]
 */
export async function getKeepList(): Promise<KeepItem[]> {
  const res = assertReadOk(
    await request<AjaxResponse<unknown, KeepItem | null>>("/api/prospective/keeps", { query: { scope: "all" } }),
    "Keep 목록을 불러오지 못했습니다."
  );
  return Array.isArray(res.rsltList) ? res.rsltList.filter(isKeepItem) : [];
}

/**
 * Keep 현재 라이프사이클을 서버에 설정한다. 완료 시각은 서버가 관리한다.
 * HTTP 성공이어도 rslt=false이면 서버 메시지를 오류로 전달한다.
 *
 * @param id 변경할 Keep ID
 * @param lifecycleKey 현재값으로 설정할 라이프사이클 키
 */
export async function setKeepLifecycle(id: number, lifecycleKey: KeepLifecycleKey): Promise<void> {
  const result = await request<AjaxResponse>("/api/lifecycles", {
    method: "PUT", body: JSON.stringify({ id, contentType: "PROSPECTIVE_KEEP", lifecycleKey })
  });
  if (result?.rslt !== true) {
    throw new ApiError(result?.message?.trim() || "Keep 상태를 변경하지 못했습니다.", 200, result);
  }
}

/**
 * Keep 상세 — 웹 수정 모달 진입과 같은 요청(서버 캐시, 소유자 확인).
 * lifecycle 은 목록과 같은 방식으로 붙어 온다(서버 applyKeepLifecycles, 행이 없으면 OPEN — 본문 캐시와 별도로 매 조회 부착).
 * GET /api/prospective/keep/{id} → rsltObj: ProspectiveKeepDto (없으면 rslt=false)
 */
export async function getKeepDetail(id: number): Promise<KeepItem> {
  if (!Number.isFinite(id)) throw new Error("유효하지 않은 Keep ID입니다.");
  const res = assertReadOk(
    await request<AjaxResponse<KeepItem | null>>(`/api/prospective/keep/${id}`),
    "Keep을 찾을 수 없습니다."
  );
  if (!isKeepItem(res.rsltObj)) throw new ApiError("Keep을 찾을 수 없습니다.", 200, res);
  return res.rsltObj;
}

/**
 * 신규 Keep을 한 번 등록하고 서버가 반환한 새 ID를 확인한다.
 * 입력 검사 후 FormData를 보내며 비멱등 POST를 자동 재전송하지 않는다. 중요 토글·목록 조회는 호출자가 관리한다.
 */
export async function createKeep(input: KeepCreateInput): Promise<KeepItem> {
  const body = toKeepFormData(input);
  const result = await request<AjaxResponse<KeepItem | null>>("/api/prospective/keeps", { method: "POST", body });
  if (result?.rslt !== true) {
    console.warn("[KeepCreate] business failure");
    throw new ApiError(result?.message?.trim() || "Keep을 등록하지 못했습니다.", 200, result);
  }
  const item = result.rsltObj;
  if (!item || !Number.isSafeInteger(item.id) || item.id <= 0) {
    console.error("[KeepCreate] successful response missing valid ID");
    throw new ApiError("등록 결과의 Keep ID를 확인하지 못했습니다. 목록을 확인하세요.", 200, result);
  }
  console.info("[KeepCreate] created", { id: item.id });
  return item;
}

/**
 * Keep을 한 번 수정한다 — 웹 수정 모달과 같은 `POST /api/prospective/keep/{id}` multipart.
 * 서버는 받은 필드로 덮어쓰므로 `toKeepEditFormData`가 상세 응답에서 위치·본문·태그·순번을 그대로 옮긴다
 * (옮길 수 없으면 요청 전에 KeepEditPreserveError). 비멱등 취급: 자동 재전송하지 않는다.
 */
export async function modifyKeep(detail: KeepItem, edit: KeepEditInput): Promise<void> {
  const body = toKeepEditFormData(detail, edit);
  const result = await request<AjaxResponse<KeepItem | null>>(`/api/prospective/keep/${detail.id}`, { method: "POST", body });
  if (result?.rslt !== true) {
    console.warn("[KeepEdit] business failure", { id: detail.id });
    throw new ApiError(result?.message?.trim() || "Keep을 수정하지 못했습니다.", 200, result);
  }
  console.info("[KeepEdit] modified", { id: detail.id });
}

/**
 * Keep 태그 자동완성 맵 — 웹 Keep 폼(prospectiveKeepCategoryMap)과 같은 요청. 빠른 추가·수정 시트의 기존 태그 후보.
 * GET /api/prospective/keep/tag/categories → rsltMap: { 태그명: 카테고리[] }
 */
export async function getKeepTagCategoryMap(): Promise<Record<string, string[]>> {
  const res = await request<AjaxResponse<unknown> & { rsltMap?: unknown }>("/api/prospective/keep/tag/categories");
  if (res?.rslt !== true) throw new ApiError(res?.message?.trim() || "Keep 태그 후보를 불러오지 못했습니다.", 200, res);
  return normalizeKeepTagCategoryMap(res.rsltMap ?? (res as { rsltObj?: unknown }).rsltObj);
}

/**
 * Keep을 삭제한다 — 웹 KeepPage `deleteKeep`과 같은 `DELETE /api/prospective/keep/{id}`(본문 없음, 서버가 소유자 확인).
 * HTTP 200 + rslt=false 는 서버 메시지로 실패. 호출자가 확인·중복 전송 잠금을 맡는다.
 */
export async function deleteKeep(id: number): Promise<void> {
  if (!Number.isSafeInteger(id) || id <= 0) throw new Error("유효하지 않은 Keep ID입니다.");
  const result = await request<AjaxResponse>(`/api/prospective/keep/${id}`, { method: "DELETE" });
  if (result?.rslt !== true) {
    console.warn("[KeepDelete] business failure", { id });
    throw new ApiError(result?.message?.trim() || "Keep을 삭제하지 못했습니다.", 200, result);
  }
  console.info("[KeepDelete] deleted", { id });
}

/**
 * Keep의 Project 소속만 옮긴다 — 웹 `moveKeepProject`와 같은 `PATCH /api/prospective/keep/{id}/project`,
 * JSON 본문 `{ projectId }`(ProspectiveKeepProjectRequest, null = 미지정). 다른 필드는 서버가 건드리지 않는다.
 */
export async function moveKeepProject(id: number, projectId: number | null): Promise<void> {
  if (!Number.isSafeInteger(id) || id <= 0) throw new Error("유효하지 않은 Keep ID입니다.");
  if (projectId != null && (!Number.isSafeInteger(projectId) || projectId <= 0)) throw new Error("유효한 Project를 선택하세요.");
  const result = await request<AjaxResponse>(`/api/prospective/keep/${id}/project`, {
    method: "PATCH", body: JSON.stringify({ projectId })
  });
  if (result?.rslt !== true) {
    console.warn("[KeepMove] business failure", { id, projectId });
    throw new ApiError(result?.message?.trim() || "Project를 옮기지 못했습니다.", 200, result);
  }
  console.info("[KeepMove] moved", { id, projectId });
}

/** Keep의 중요·요구 속성을 한 번 토글한다. 비멱등 요청이므로 호출자가 중복 전송을 잠근다. */
export async function toggleKeepState(id: number, stateKey: KeepStateKey): Promise<void> {
  const res = await request<AjaxResponse>("/api/states", {
    method: "POST",
    body: JSON.stringify({ id, contentType: "PROSPECTIVE_KEEP", stateKey })
  });
  if (res.rslt !== true) throw new ApiError(res.message || "Keep 속성을 변경하지 못했습니다.", 200, res);
}
/**
 * 내 Project 목록 (전 라이프사이클, sortOrder → id). 화면은 OPEN 만 칩으로 쓰고 나머지는 이름 표시에만 쓴다.
 * GET /api/prospective/projects → rsltList: ProspectiveProjectDto[]
 */
export async function getKeepProjects(): Promise<KeepProject[]> {
  const res = assertReadOk(
    await request<AjaxResponse<unknown, KeepProject | null>>("/api/prospective/projects"),
    "Project 목록을 불러오지 못했습니다."
  );
  return Array.isArray(res.rsltList)
    ? res.rsltList.filter((p): p is KeepProject => p != null && Number.isFinite(Number(p.id)))
    : [];
}

/**
 * Keep 태그 목록 (웹 Keep 태그 클라우드와 같은 요청) — 태그 필터 선택지의 순서에 쓴다.
 * 화면 건수는 목록에서 직접 센다(mergeKeepTagOptions) — 서버 contentSize 의 집계 범위가 Keep 전용인지 보장되지 않기 때문.
 * GET /api/tags?contentType=PROSPECTIVE_KEEP → rsltList: TagDto[] (id·name·ctgr·contentSize)
 */
export async function getKeepTags(): Promise<KeepTagOption[]> {
  const res = assertReadOk(
    await request<AjaxResponse<unknown, Record<string, unknown> | null>>("/api/tags", {
      query: { contentType: "PROSPECTIVE_KEEP" }
    }),
    "Keep 태그를 불러오지 못했습니다."
  );
  if (!Array.isArray(res.rsltList)) return [];
  return res.rsltList.flatMap((raw): KeepTagOption[] => {
    if (raw == null) return [];
    const id = Number(raw.id);
    const name = String(raw.name ?? "").trim();
    if (!Number.isFinite(id) || !name) return [];
    const ctgr = String(raw.ctgr ?? "").trim();
    return [{ id, name, ctgr: ctgr || null, contentSize: Number(raw.contentSize ?? 0) || 0 }];
  });
}

/**
 * Keep 활성 개인 말머리 (웹 personalPrefixOptions 와 같은 요청) — 말머리 필터 선택지.
 * GET /api/my/prefixes/options?contentType=PROSPECTIVE_KEEP → rsltList: PrefixDto[]
 */
export async function getKeepPrefixOptions(): Promise<KeepPrefixOption[]> {
  const res = assertReadOk(
    await request<AjaxResponse<unknown, KeepPrefix | null>>("/api/my/prefixes/options", {
      query: { contentType: "PROSPECTIVE_KEEP" }
    }),
    "말머리 목록을 불러오지 못했습니다."
  );
  if (!Array.isArray(res.rsltList)) return [];
  return res.rsltList.flatMap((p, index): KeepPrefixOption[] => {
    const id = Number(p?.id);
    const name = p?.name?.trim();
    if (p == null || !Number.isFinite(id) || !name) return [];
    return [{ id, name, sortOrder: p.sortOrder ?? index, textClass: p.textClass ?? null }];
  });
}

// ─── 사용자 메뉴 (햄버거 메뉴) ─────────────────────────

/**
 * 사용자 사이드바 메뉴 — 웹 사이드바(menu 스토어 fetchUserMenu)와 같은 요청. 관리자 모드(MNGR)는 쓰지 않는다.
 * GET /api/menus?mode=USER → rsltList: MenuDto[] (서버가 사용 중·사이드바 표시 메뉴만, 게시판 하위 메뉴 주입, 권한 필터)
 */
export async function getUserMenus(): Promise<MenuDto[]> {
  const res = await request<{ rslt: boolean; rsltList?: Array<MenuDto | null> }>("/api/menus", {
    query: { mode: "USER" }
  });
  if (!res.rslt || !Array.isArray(res.rsltList)) return [];
  return res.rsltList.filter((m): m is MenuDto => m != null);
}

// ─── 저널 엔트리 검색 ──────────────────────────────────

interface EntrySearchResponse {
  rslt: boolean;
  rsltList?: JournalEntry[];
}

export interface EntrySearchParams {
  keyword?: string;
  type?: "DREAM" | "DIARY";
  tagIds?: number[];
}

/**
 * 저널 엔트리 검색 (키워드 또는 tagIds, 둘 중 하나 이상 필요).
 * GET /api/journal/entries
 */
export function searchEntries(params: EntrySearchParams) {
  const keyword = params.keyword?.trim();
  const tagIds = params.tagIds?.filter((id) => Number.isFinite(id));
  if (!keyword && (!tagIds || tagIds.length === 0)) {
    throw new Error("검색 조건이 없습니다.");
  }
  return request<EntrySearchResponse>("/api/journal/entries", {
    query: {
      ...(keyword ? { searchKeywords: [keyword] } : {}),
      ...(tagIds && tagIds.length > 0 ? { tagIds } : {}),
      ...(params.type ? { type: params.type } : {})
    }
  });
}
/** 현재 인증 계정의 설치 UUID·Expo 토큰·시간대를 등록한다. 토큰은 로그와 응답에 노출하지 않는다. */
export async function registerKeepReminderDevice(deviceId: string, expoToken: string, timeZone: string): Promise<void> {
  const response = await request<AjaxResponse>("/api/prospective/keep/reminder-devices", {
    method: "POST", body: JSON.stringify({ deviceId, expoToken, timeZone })
  });
  if (response.rslt !== true) throw new ApiError(response.message || "알림 기기를 등록하지 못했습니다.", 200);
}

/** 로그아웃 전에 인증 소유자의 등록 해제를 확인한다. HTTP·업무 실패는 호출자에게 전달한다. */
export async function unregisterKeepReminderDevice(deviceId: string): Promise<void> {
  const response = await request<AjaxResponse>(`/api/prospective/keep/reminder-devices/${encodeURIComponent(deviceId)}`, { method: "DELETE" });
  if (response.rslt !== true) throw new ApiError(response.message || "알림 기기를 해제하지 못했습니다.", 200);
}

/** 알림의 날짜·슬롯에 해당하는 현재 인증 소유자의 최신 Keep 목록을 서버에서 판정한다. */
export async function getKeepReminderList(localDate: string, slot: "MORNING" | "EVENING"): Promise<KeepItem[]> {
  const response = await request<AjaxResponse<unknown, KeepItem>>("/api/prospective/keep/reminder-targets", { query: { localDate, slot } });
  if (response.rslt !== true) throw new ApiError(response.message || "알림 대상 목록을 조회하지 못했습니다.", 200);
  if (!Array.isArray(response.rsltList)) throw new ApiError("알림 대상 목록 응답을 확인하지 못했습니다.", 200);
  return response.rsltList;
}