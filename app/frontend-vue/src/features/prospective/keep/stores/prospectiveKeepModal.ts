import { defineStore } from "pinia";
import { ref } from "vue";
import { assertAuthenticatedBeforeModal } from "@/shared/auth/sessionPing";
import type { JournalPrefixDto } from "@/features/journal/stores/journal";

/** Prospective Keep 등록/수정 폼 모델. */
export interface ProspectiveKeepRegistModel {
  id?: number;
  title?: string;
  sortOrder?: number;
  content?: string;
  prefixId?: number | null;
  prefix?: JournalPrefixDto | null;
  location?: string;
  dateText?: string;
  expireDt?: string;
  projectId?: number | null;
  tag?: { tagListStrWithCtgr?: string };
  /** 중요(IMPRTC) 상태 보유 여부. 미리보기 표시 전용이며 저장 payload 에 싣지 않는다(상태는 POST /api/states 로 토글). */
  important?: boolean;
}

/** Prospective Keep 등록/수정 모달의 단일 상태 소유자. */
export const useProspectiveKeepModalStore = defineStore("prospectiveKeepModal", () => {
  const registOpen = ref(false);
  const registModel = ref<ProspectiveKeepRegistModel | null>(null);

  /** 인증을 확인하고 신규 또는 수정 모델로 모달을 연다. */
  async function openRegist(payload?: ProspectiveKeepRegistModel): Promise<void> {
    if (!await assertAuthenticatedBeforeModal()) return;
    registModel.value = {
      prefixId: null,
      title: "",
      content: "",
      location: "",
      dateText: "",
      expireDt: "",
      projectId: null,
      tag: { tagListStrWithCtgr: "" },
      ...payload,
    };
    registOpen.value = true;
  }

  /** 등록/수정 모달을 닫는다. */
  function closeRegist(): void {
    registOpen.value = false;
  }

  return { registOpen, registModel, openRegist, closeRegist };
});
