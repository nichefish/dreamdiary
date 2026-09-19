import { useRoute } from "vue-router";
import { useJournalStore, type TagCloudSection } from "@/features/journal/stores/journal";
import { useJournalAnnualStore } from "@/features/journal/stores/journalAnnual";
import { useJournalThreadStore } from "@/features/journal/stores/journalThread";
import { refreshJournalDaysForRoute } from "@/features/journal/utils/journalDayRefresh";

/**
 * 태그 프로필 저장·삭제 후 저널 화면 갱신.
 * <pre>
 *  프로필 모달 자체는 어느 화면을 갱신해야 하는지 알지 않는다(공용 모듈이라 알 수 없다).
 *  저널 화면들이 모달의 success 를 받아 이 경로로 자기 화면을 갱신한다.
 *
 *  화면별 SSOT 가 달라 분기가 필요하다.
 *  - 스레드 상세: 소속 엔트리와 엔트리 태그 집계가 SSOT 라 열린 상세를 다시 조회한다.
 *    상세가 다른 화면 위에 모달로 열려 있을 수 있어 route 와 무관하게 detailOpen 으로도 판정하고,
 *    그 경우 배경 화면 갱신까지 이어서 수행한다.
 *  - 검색 팝업: 로컬 entries 가 SSOT 라 여기서는 아무것도 하지 않고 화면 자신이 다시 읽는다.
 *  - 결산 상세: /api/journal/annual/{yy}/tags 행이 SSOT 이므로 fetchTagRows 로 재조회한다.
 *  - 그 외(월간/주간/일간): 일자 목록 + contentType 대응 태그클라우드 섹션.
 *    색(textClass)은 클라우드 API 응답에만 실려서 해당 섹션을 다시 읽어야 한다.
 * </pre>
 */
export function useJournalTagProfileRefresh() {
  const route = useRoute();
  const journalStore = useJournalStore();
  const threadStore = useJournalThreadStore();

  /** contentType 에 대응하는 태그 클라우드 섹션. 대응이 없으면 클라우드를 건드리지 않는다. */
  function tagCloudSectionsFor(contentType: string): TagCloudSection[] | undefined {
    if (contentType === "JOURNAL_DAY") return ["day"];
    if (contentType === "JOURNAL_DIARY") return ["diary"];
    if (contentType === "JOURNAL_DREAM") return ["dream"];
    return undefined;
  }

  /**
   * 프로필 변경을 현재 저널 화면에 반영한다.
   *
   * @param contentType 변경된 프로필의 콘텐츠 타입
   */
  async function refreshAfterTagProfileChange(contentType: string): Promise<void> {
    if (threadStore.detailOpen || route.name === "thread-detail") {
      await threadStore.refreshOpenDetail();
      if (route.name === "thread-detail") return;
    }

    if (route.name === "journal-entry-search") return;

    if (route.name === "annual-detail") {
      const annualStore = useJournalAnnualStore();
      const yy = Number(route.params.yy) || Number(annualStore.filterYy) || 0;
      if (Number.isFinite(yy) && yy > 0) {
        await annualStore.fetchTagRows(yy, annualStore.activeSection);
      }
      return;
    }

    const sections = tagCloudSectionsFor(contentType);
    const tasks: Promise<unknown>[] = [refreshJournalDaysForRoute(journalStore, route)];
    if (sections) tasks.push(journalStore.fetchTagCloud({ sections }));
    await Promise.all(tasks);
  }

  return { refreshAfterTagProfileChange };
}
