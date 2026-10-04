<template>
  <!--begin::게시판 게시물 상세 모달 (목록의 빠른 보기 전용. 기본 상세는 독립 페이지 BoardPostDetailPage)-->
  <div ref="modalEl" class="modal fade" id="board_post_detail_modal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-xxl">
      <div class="modal-content">

        <!--begin::Modal Header-->
        <div class="modal-header">
          <h5 class="modal-title">{{ t('board.post.detail.title') }}</h5>
          <div class="d-flex align-items-center gap-2">
            <BoardPostDetailActions />
            <button type="button" class="btn-close" @click="close"></button>
          </div>
        </div>
        <!--end::Modal Header-->

        <!--begin::Modal Body-->
        <div class="modal-body modal-mbl-body my-5">
          <!--begin::로딩-->
          <div v-if="store.detailLoading" class="d-flex justify-content-center py-10">
            <span class="spinner-border text-primary" role="status"></span>
          </div>
          <!--end::로딩-->

          <BoardPostDetailContent v-else-if="isModalSurface" :post="store.detailModel" />
        </div>
        <!--end::Modal Body-->

        <!--begin::Modal Footer-->
        <div class="modal-footer">
          <div class="d-flex justify-content-end">
            <button type="button" class="btn btn-sm btn-light" @click="close">{{ t('common.close') }}</button>
          </div>
        </div>
        <!--end::Modal Footer-->

      </div>
    </div>
  </div>
  <!--end::게시판 게시물 상세 모달-->
</template>

<script setup lang="ts">
import { useLocaleStore } from "@/shared/i18n/stores/locale";
import { ref, computed, watch, onMounted } from "vue";
import { Modal } from "bootstrap";
import { useBoardPostStore } from "@/features/board/stores/boardPost";
import BoardPostDetailContent from "@/features/board/components/BoardPostDetailContent.vue";
import BoardPostDetailActions from "@/features/board/components/BoardPostDetailActions.vue";

const { t } = useLocaleStore();
const store = useBoardPostStore();

const modalEl = ref<HTMLElement | null>(null);
let bsModal: InstanceType<typeof Modal> | null = null;

/** 상세가 모달 표면으로 열려 있는지 여부 (독립 상세 페이지 표면은 제외) */
const isModalSurface = computed(() => store.detailOpen && store.detailSurface === "modal");

onMounted(() => {
  if (!modalEl.value) return;
  bsModal = new Modal(modalEl.value);
  modalEl.value.addEventListener("hidden.bs.modal", () => {
    /* 사용자가 모달 자체를 숨긴 경우(ESC·backdrop 포함)에만 상세 상태를 정리한다. */
    if (store.detailSurface === "modal") store.closeDetail();
  });
  modalEl.value.addEventListener("shown.bs.modal", () => {
    /*
     * Bootstrap Modal 은 show/hide 전환(fade) 도중의 hide() 호출을 무시한다.
     * 열리는 도중 상세가 닫힌 경우(조회 실패·빠른 닫기) store 와 화면이 어긋나지 않도록 여기서 다시 맞춘다.
     */
    if (!isModalSurface.value) bsModal?.hide();
  });
  if (isModalSurface.value) bsModal.show();
});

watch(isModalSurface, (isOpen) => {
  if (isOpen) bsModal?.show();
  else bsModal?.hide();
});

/**
 * 닫기 — store 상태와 Bootstrap 인스턴스를 함께 닫는다.
 * store 가 이미 닫힌 상태(watch 미발화)여도 화면의 모달이 남지 않게 hide() 를 직접 호출한다.
 */
function close(): void {
  if (store.detailSurface === "modal" || !store.detailOpen) store.closeDetail();
  bsModal?.hide();
}
</script>