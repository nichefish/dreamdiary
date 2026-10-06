/** 백엔드 JournalEntryDto 최소 필드 */
export interface JournalEntry {
  id: number;
  contentType: string;
  title?: string;
  content?: string;
  /** 기록이 속한 날짜 (YYYY-MM-DD) — 검색 결과 카드에서 날짜 표시에 사용 */
  stdrdDt?: string;
  /** 엔트리 태그 (백엔드 JournalEntryDto.tag — TagCmpstn). 일자 조회 응답에 채워져 온다. */
  tag?: JournalTagCmpstn | null;
  /** 엔트리가 소속된 스레드 (백엔드 JournalEntryDto.threadList). 일자 조회 응답에 채워져 온다. */
  threadList?: JournalThreadEntry[] | null;
  /** 챕터 안 순번 (복사 포맷 `#순번`) */
  sortOrder?: number | null;
  /** 마크다운 처리 후 HTML (복사 시 content 가 없을 때만 사용) */
  markdownContent?: string | null;
  /** 이 엔트리를 target 으로 하는 리플렉션(해석) 목록 — 일자 조회 응답에 채워져 온다 */
  reflectionList?: JournalEntry[] | null;
  /** 라이프사이클 (OPEN | PENDING | RESOLVED) */
  lifecycle?: { lifecycleKey?: string | null } | null;
}

/** 백엔드 TagContentDto 최소 필드 (TagCmpstn.list 항목) */
export interface JournalTagContent {
  id?: number;
  tagId: number;
  refId?: number;
  refContentType?: string | null;
  /** 태그 카테고리 이름. 없으면 null/빈 문자열 */
  ctgr?: string | null;
  /** 표시 이름 (flat). 없으면 tag.name 사용 */
  name?: string | null;
  tag?: { id?: number; name?: string | null; ctgr?: string | null } | null;
  /** 태그 프로필 설명 (꿈 태그 프로필 등) */
  profileContent?: string | null;
}

/** 백엔드 TagCmpstn (DTO.tag) */
export interface JournalTagCmpstn {
  contentType?: string | null;
  list?: JournalTagContent[] | null;
  tagStrList?: string[] | null;
}

/** 백엔드 JournalThreadEntryDto (엔트리 ↔ 스레드 소속) */
export interface JournalThreadEntry {
  id?: number;
  threadId: number;
  entryId?: number;
  sortOrder?: number;
  threadTitle?: string | null;
  entryContentType?: string | null;
}

/** 화면 표시용으로 정규화한 태그 한 개 */
export interface DisplayTag {
  tagId: number;
  name: string;
  ctgr: string | null;
}

/** 백엔드 JournalDreamSectionDto */
export interface JournalDreamSection {
  sectionKey: string;
  title: string;
  dreamerName?: string | null;
  entries?: JournalEntry[];
}

/** 백엔드 PrefixDto 최소 필드 (챕터 개인 말머리) */
export interface JournalChapterPrefix {
  id: number;
  name: string;
  textClass?: string | null;
}

/** 백엔드 JournalChapterDto 최소 필드 */
export interface JournalChapter {
  id: number;
  contentType?: string;
  title?: string | null;
  /** 일반 챕터가 선택한 개인 말머리 (0..1) */
  prefix?: JournalChapterPrefix | null;
  prefixId?: number | null;
  sortOrder?: number;
  /** 챕터 타입 (DIARY | DREAM | NOTE) */
  chapterType?: string;
  /** 시스템 요약 챕터 여부 (Y/N) */
  summaryYn?: string;
  /** 챕터 요약 태그 (소속 일기 축 엔트리 태그 집계 — 서버가 showDiaries=true 일 때만 채움) */
  tag?: JournalTagCmpstn | null;
  /**
   * 챕터 엔트리 목록 (백엔드 JournalChapterDto.journalEntryList).
   * 일기·꿈·노트 구분은 각 엔트리의 contentType(JOURNAL_DIARY | JOURNAL_DREAM)으로 한다.
   */
  journalEntryList?: JournalEntry[];
}

/** 백엔드 JournalDayDto 최소 필드 */
export interface JournalDay {
  id: number;
  stdrdDt: string;
  journalChapterList?: JournalChapter[];
  /** 일자 태그 (백엔드 JournalDayDto.tag — TagCmpstn) */
  tag?: JournalTagCmpstn | null;
  /** 일자 화면용 꿈 가상 섹션 */
  journalDreamSectionList?: JournalDreamSection[];
}

/**
 * 일자 DTO 에서 모든 꿈 엔트리를 펼친다.
 * @param day 조회된 일자 DTO. 일자 데이터가 없는 null/undefined이면 빈 목록을 반환한다.
 */
export function dreamEntriesFromDay(day: JournalDay | null | undefined): JournalEntry[] {
  return (day?.journalDreamSectionList ?? []).flatMap((section) => section.entries ?? []);
}

/**
 * 챕터의 엔트리 목록을 반환한다. (백엔드 JournalChapterDto.journalEntryList)
 * @param chapter 대상 챕터
 */
export function chapterEntries(chapter: JournalChapter | null | undefined): JournalEntry[] {
  return chapter?.journalEntryList ?? [];
}

/**
 * 챕터 헤더 라벨. 웹 JournalChapterItem.vue 헤더와 동일한 규칙:
 * "{타입}" + (요약 챕터면 ": 요약", 아니면 말머리가 있을 때 ": {말머리}") + (제목이 있으면 " · {제목}").
 * 예) "일기: 요약", "일기: 업무 · 회의 정리", "노트 · 메모", "일기"
 * @param chapter 대상 챕터
 */
export function chapterHeaderLabel(chapter: JournalChapter): string {
  const typeLabel =
    chapter.chapterType === "DREAM" ? "꿈" : chapter.chapterType === "NOTE" ? "노트" : "일기";
  const qualifier = chapter.summaryYn === "Y" ? "요약" : chapter.prefix?.name?.trim() ?? "";
  const title = chapter.title?.trim();
  const base = qualifier ? `${typeLabel}: ${qualifier}` : typeLabel;
  return title ? `${base} · ${title}` : base;
}

/**
 * 해당 일자에 실제 엔트리(일기·꿈·노트 또는 꿈 섹션)가 하나라도 있는지 판별한다.
 * @param day 조회된 일자 DTO
 */
export function dayHasEntries(day: JournalDay): boolean {
  if (dreamEntriesFromDay(day).length > 0) return true;
  return day.journalChapterList?.some((ch) => chapterEntries(ch).length > 0) ?? false;
}

/**
 * TagCmpstn(DTO.tag)을 화면 표시용 태그 목록으로 정규화한다.
 * 이름은 flat `name` 우선, 없으면 nested `tag.name` (백엔드 TagContentDto.resolveDisplayName 과 같은 규칙).
 * 같은 tagId는 한 번만 남기고 서버 순서를 유지한다.
 * @param cmpstn 일자·엔트리·챕터 DTO의 tag 필드
 */
export function displayTagsOf(cmpstn: JournalTagCmpstn | null | undefined): DisplayTag[] {
  const seen = new Set<number>();
  const result: DisplayTag[] = [];
  for (const item of cmpstn?.list ?? []) {
    if (item == null) continue;
    const name = (item.name ?? item.tag?.name ?? "").trim();
    if (!name) continue;
    const tagId = Number(item.tagId ?? item.tag?.id);
    if (Number.isFinite(tagId)) {
      if (seen.has(tagId)) continue;
      seen.add(tagId);
    }
    const ctgr = (item.ctgr ?? item.tag?.ctgr ?? "").trim();
    result.push({ tagId, name, ctgr: ctgr || null });
  }
  return result;
}

/** 백엔드 PrefixDto 최소 필드 (스레드 말머리) */
export interface JournalThreadPrefix {
  id?: number;
  name: string;
  textClassCd?: string | null;
  /** 색 클래스 (text-primary 등). 없으면 빈 문자열 */
  textClass?: string | null;
}

/** 백엔드 JournalThreadPeriodSummaryDto — 기간 안에 엔트리가 등장한 스레드 요약 */
export interface JournalThreadPeriodSummary {
  threadId: number;
  title: string;
  prefix?: JournalThreadPrefix | null;
  /** 기간 안 소속 엔트리 수 */
  entryCount: number;
  firstEntryDate?: string | null;
}

/**
 * 백엔드 JournalThreadDto 중 읽기 전용 화면이 쓰는 필드 — GET /api/journal/threads/{id} 의 rsltObj.
 * membershipCount·firstEntryDate·lastEntryDate·tag 는 서버 상세 조회(viewDetailPage)가 소속 집계로 채운다.
 */
export interface JournalThread {
  id: number;
  contentType?: string | null;
  title?: string | null;
  /** 본문 원문 (마크다운) */
  content?: string | null;
  /** 마크다운 처리 후 HTML */
  markdownContent?: string | null;
  /** 말머리 (0..1) */
  prefix?: JournalThreadPrefix | null;
  prefixId?: number | null;
  /** 활성 소속 엔트리 수 */
  membershipCount?: number | null;
  /** 소속 엔트리 기준일 최소값 (YYYY-MM-DD) */
  firstEntryDate?: string | null;
  /** 소속 엔트리 기준일 최대값 (YYYY-MM-DD) */
  lastEntryDate?: string | null;
  /** 라이프사이클 (OPEN | PENDING | RESOLVED). 없으면 OPEN */
  lifecycle?: { lifecycleKey?: string | null; lifecycleDesc?: string | null } | null;
  /** 소속 엔트리 태그 집계 (스레드 자체 태그 아님. 일기·꿈 구분 없음) */
  tag?: JournalTagCmpstn | null;
}