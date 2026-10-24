import axios from "axios";
import { useLocaleStore } from "@/shared/i18n/stores/locale";
import { useAttachableModalStore } from "@/features/attachable/stores/attachableModal";
import type { TagContextMenuAction, TagContextMenuPayload } from "@/shared/ui/tag/tagContextMenu";

/**
 * 태그 컨텍스트 메뉴의 「태그 설정」 항목.
 * <pre>
 *  저널·Keep·게시판이 같은 동작을 쓴다 -- contentType 별 프로필을 조회해 프로필 모달을 연다.
 *  조회가 실패해도 모달은 열고 빈 프로필로 시작한다(아직 프로필이 없는 태그가 정상 경로).
 *  태그 프로필은 attachable 소유라 이 액션도 여기에 둔다.
 * </pre>
 */
export function useTagProfileAction() {
  const { t } = useLocaleStore();
  const attachableStore = useAttachableModalStore();

  /**
   * 「태그 설정」 메뉴 항목을 만든다.
   *
   * @param payload 대상 태그
   * @param contentTypeLabel 프로필 모달에 표시할 콘텐츠 타입 이름. 없으면 contentType 원문.
   */
  function tagProfileAction(
    payload: TagContextMenuPayload,
    contentTypeLabel?: string,
  ): TagContextMenuAction {
    return {
      label: t("journal.tag.settings"),
      icon: "bi-sliders2",
      variant: "configure",
      run: () => { void openTagProfile(payload, contentTypeLabel ?? payload.contentType); },
    };
  }

  async function openTagProfile(payload: TagContextMenuPayload, contentTypeLabel: string): Promise<void> {
    const base = {
      tagId: String(payload.tagId),
      name: payload.name,
      ctgr: payload.ctgr,
      contentType: payload.contentType,
      contentTypeLabel,
    };
    try {
      const res = await axios.get(`/api/tags/${payload.tagId}/profile`, {
        params: { contentType: payload.contentType },
      });
      attachableStore.openTagProfile({ ...(res.data?.rsltObj ?? {}), ...base });
    } catch {
      attachableStore.openTagProfile(base);
    }
  }

  return { tagProfileAction };
}
