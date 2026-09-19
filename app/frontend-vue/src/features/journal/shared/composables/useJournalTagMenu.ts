import { useRoute, useRouter } from "vue-router";
import { useLocaleStore } from "@/shared/i18n/stores/locale";
import { useJournalModalStore } from "@/features/journal/stores/journalModal";
import { useTagProfileAction } from "@/features/attachable/composables/useTagProfileAction";
import {
  useTagContextMenuStore,
  type TagContextMenuAction,
  type TagContextMenuPayload,
} from "@/shared/ui/tag/tagContextMenu";
import { assertAuthenticatedBeforePopup } from "@/shared/auth/popupAuth";
import { joinAppBasePath } from "@/shared/utils/appPath";

/**
 * 저널 화면에서 태그 컨텍스트 메뉴를 여는 경로.
 * <pre>
 *  메뉴 컴포넌트는 항목 구성을 알지 않으므로, 저널 전용 동작(엔트리 검색 팝업·일자 필터 모달·
 *  검색 팝업 안에서의 다중 태그 추가)을 여기서 조립해 넘긴다. 「태그 설정」은 도메인 공용이라
 *  attachable 의 액션을 그대로 쓴다.
 * </pre>
 */
export function useJournalTagMenu() {
  const { t } = useLocaleStore();
  const route = useRoute();
  const router = useRouter();
  const store = useTagContextMenuStore();
  const journalModalStore = useJournalModalStore();
  const { tagProfileAction } = useTagProfileAction();

  /** 저널 태그 메뉴를 연다. */
  function openJournalTagMenu(event: MouseEvent, payload: TagContextMenuPayload): void {
    const actions: TagContextMenuAction[] = [{
      label: t("common.search"),
      icon: "bi-search",
      variant: "search",
      run: () => { void search(payload, "single"); },
    }];
    /* 검색 팝업 안에서만 다중 추가를 노출한다. 밖에서는 단일 「검색」이 곧 새 검색이다. */
    if (route.name === "journal-entry-search") {
      actions.push({
        label: t("journal.tag.search-add"),
        icon: "bi-plus-lg",
        variant: "search-add",
        run: () => { void search(payload, "add"); },
      });
    }
    actions.push(tagProfileAction(payload, contentTypeLabel(payload.contentType)));
    store.open(event, payload, actions);
  }

  async function search(payload: TagContextMenuPayload, mode: "single" | "add"): Promise<void> {
    if (payload.contentType === "JOURNAL_DAY") {
      void journalModalStore.openDayFilterModal({
        type: "tag", id: payload.tagId, name: payload.name, ctgr: payload.ctgr,
      });
      return;
    }
    await openEntrySearchPopup(payload, mode);
  }

  async function openEntrySearchPopup(
    payload: TagContextMenuPayload,
    mode: "single" | "add",
  ): Promise<void> {
    const newType = payload.contentType === "JOURNAL_DREAM" ? "DREAM" : "DIARY";
    const newTagId = String(payload.tagId);

    if (route.name === "journal-entry-search") {
      /*
       * 팝업 내부: mode 에 따라 tagIds 를 구성한다. type·sort·키워드 등 다른 검색 축은 유지한다.
       * - "single": tagIds 를 이 태그 하나로 교체(새 단일태그 검색).
       * - "add": 기존 tagIds 에 이 태그를 더한다(AND). 이미 있으면 무시.
       */
      const existingIds = normalizeList(route.query.tagIds);
      let tagIds: string[];
      if (mode === "add") {
        if (existingIds.includes(newTagId)) return;
        tagIds = [...existingIds, newTagId];
      } else {
        if (existingIds.length === 1 && existingIds[0] === newTagId) return;
        tagIds = [newTagId];
      }
      const nextQuery: Record<string, string | string[]> = {
        type: String(route.query.type ?? newType).toUpperCase(),
      };
      const sortVal = String(route.query.sort ?? "").toLowerCase();
      if (sortVal === "asc") nextQuery.sort = "asc";
      const searchKeywords = normalizeList(route.query.searchKeywords);
      if (searchKeywords.length > 0) nextQuery.searchKeywords = searchKeywords;
      void router.replace({ name: "journal-entry-search", query: { ...nextQuery, tagIds } });
      return;
    }

    if (!await assertAuthenticatedBeforePopup(router, route)) return;

    /* 외부: 새 팝업 창 열기 */
    const params = new URLSearchParams({ type: newType, tagIds: newTagId });
    const url = joinAppBasePath(`/journal/entry/search?${params.toString()}`);
    const popupName = payload.contentType === "JOURNAL_DREAM"
      ? "journal-entry-search-DREAM"
      : "journal-entry-search-DIARY";
    const popup = window.open(url, popupName, "width=1960,height=1440,top=0,left=270");
    popup?.focus();
  }

  /** route.query 의 단일값·배열값을 string[] 로 정규화 */
  function normalizeList(value: unknown): string[] {
    const raw = Array.isArray(value) ? value : [value];
    return raw.flatMap((v) => String(v ?? "").split(",")).map((v) => v.trim()).filter(Boolean);
  }

  function contentTypeLabel(contentType: string): string {
    switch (contentType) {
      case "JOURNAL_DAY": return t("common.date");
      case "JOURNAL_DIARY": return t("common.diary");
      case "JOURNAL_DREAM": return t("common.dream");
      case "JOURNAL_TODO": return t("journal.aside.todo.title");
      default: return contentType;
    }
  }

  return { openJournalTagMenu };
}
