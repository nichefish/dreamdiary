<template>
  <!--begin::게시판 게시물 독립 상세 페이지 (route: board-post-detail). 저널 스레드 독립 상세와 같은 구성.-->
  <div class="card mt-3 mb-5">
    <div class="card-header border-0">
      <div class="card-title">
        <h3 class="fw-bold text-gray-900">{{ t('board.post.detail.title') }}</h3>
      </div>
      <div class="card-toolbar gap-2">
        <BoardPostDetailActions @deleted="onDeleted" />
        <button
          type="button"
          class="btn btn-sm btn-light-primary"
          :title="t('board.post.detail.back-to-list')"
          @click="goToList"
        >
          <i class="bi bi-arrow-left me-1"></i>
          {{ t('board.post.detail.back-to-list') }}
        </button>
      </div>
    </div>

    <div class="card-body pt-3">
      <div v-if="store.detailLoading" class="d-flex justify-content-center py-10">
        <span class="spinner-border text-primary" role="status"></span>
      </div>
      <BoardPostDetailContent v-else-if="isPageSurface" :post="store.detailModel" />
    </div>
  </div>
  <!--end::게시판 게시물 독립 상세 페이지-->
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useLocaleStore } from "@/shared/i18n/stores/locale";
import { useBoardPostStore } from "@/features/board/stores/boardPost";
import BoardPostDetailContent from "@/features/board/components/BoardPostDetailContent.vue";
import BoardPostDetailActions from "@/features/board/components/BoardPostDetailActions.vue";

const route = useRoute();
const router = useRouter();
const store = useBoardPostStore();
const { t } = useLocaleStore();

/** 상세가 페이지 표면으로 열려 있는지 여부 (데이터 조회는 BoardPostLayout 의 route 동기화가 담당) */
const isPageSurface = computed(() => store.detailOpen && store.detailSurface === "page");

/**
 * 목록으로 돌아간다. 직전 화면이 있으면 history back 으로 돌아가 목록 상태(페이지·검색 조건)를 유지하고,
 * 직접 진입 등 히스토리가 없으면 같은 게시판 목록으로 이동한다.
 */
function goToList(): void {
  if (typeof window.history.state?.back === "string") {
    router.back();
    return;
  }
  void router.push({ name: "board-post-list", params: { boardKey: String(route.params.boardKey) } });
}

/** 삭제 후에는 더 이상 볼 상세가 없으므로 목록으로 교체 이동한다. */
function onDeleted(): void {
  void router.replace({ name: "board-post-list", params: { boardKey: String(route.params.boardKey) } });
}
</script>