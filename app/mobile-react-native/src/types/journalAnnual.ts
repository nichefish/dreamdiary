import type { JournalTagCmpstn } from "./journalDay";

/** 백엔드 JournalAnnualReviewDto 중 화면에 쓰는 필드 */
export interface JournalAnnualReview {
  id?: number | null;
  title?: string | null;
  /** 본문 원문 */
  content?: string | null;
  /** 마크다운 처리 후 HTML */
  markdownContent?: string | null;
  categoryCode?: string | null;
  yy?: number | null;
}

/**
 * 백엔드 JournalAnnualDto 중 화면에 쓰는 필드.
 * 목록(GET /api/journal/annuals)·총 집계(GET /api/journal/annual/total)·상세(GET /api/journal/annual/{yy}) 공통.
 */
export interface JournalAnnual {
  id?: number | null;
  yy?: number | null;
  title?: string | null;
  content?: string | null;
  markdownContent?: string | null;
  /** 꿈을 기록한 일수 */
  dreamDayCnt?: number | null;
  /** 꿈 기록 수 */
  dreamCnt?: number | null;
  /** 꿈 기록 완료 여부 (Y/N) */
  dreamComptYn?: string | null;
  tag?: JournalTagCmpstn | null;
  /** 상세 응답에만 담긴다 */
  journalAnnualReviewList?: Array<JournalAnnualReview | null> | null;
}
