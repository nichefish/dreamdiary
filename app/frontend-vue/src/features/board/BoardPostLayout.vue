<template>
  <!--begin::게시판 레이아웃 (모달 컨테이너 포함)-->
  <div class="board-post-layout-vue">
    <!--begin::뷰 툴바 — 등록은 저널 스레드·결산과 동일하게 툴바 우측. ASIDE 없음. 독립 상세 페이지에서는 숨긴다(스레드와 동일).-->
    <BoardPostViewToolbar v-if="route.name !== 'board-post-detail'" />
    <!--end::뷰 툴바-->
    <router-view />
    <!--begin::게시판 모달 컨테이너-->
    <BoardPostRegistModal />
    <BoardPostDetailModal />
    <CommentListModal />
    <!--end::게시판 모달 컨테이너-->
  </div>
  <!--end::게시판 레이아웃-->
</template>

<script setup lang="ts">
import { onBeforeUnmount, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import BoardPostViewToolbar from "./components/BoardPostViewToolbar.vue";
import BoardPostRegistModal from "./modals/BoardPostRegistModal.vue";
import BoardPostDetailModal from "./modals/BoardPostDetailModal.vue";
import CommentListModal from "@/features/attachable/CommentListModal.vue";
import { useBoardPostStore } from "@/features/board/stores/boardPost";

const route = useRoute();
const router = useRouter();
const store = useBoardPostStore();

let syncToken = 0;

/**
 * route 와 상세 표면을 동기화한다.
 * <pre>
 *  board-post-detail: 독립 상세 페이지 데이터를 조회한다. 조회 실패 시 같은 게시판 목록으로 돌아간다.
 *  그 외(목록): 페이지 표면 상세가 남아 있으면 정리한다. 목록 위 빠른 보기 모달은 건드리지 않는다.
 * </pre>
 */
async function syncBoardRoute(): Promise<void> {
  const token = ++syncToken;
  const routeName = String(route.name ?? "");
  const routeId = Number(route.params.id);

  if (routeName === "board-post-detail" && Number.isInteger(routeId) && routeId > 0) {
    const loaded = await store.openDetailPage(routeId);
    if (token !== syncToken || loaded) return;
    console.warn("[boardPost] detail route returned to list: detail load failed", { routeId });
    await router.replace({ name: "board-post-list", params: { boardKey: String(route.params.boardKey) } });
    return;
  }

  if (store.detailOpen && store.detailSurface === "page") store.closeDetail();
}

watch(
  () => route.fullPath,
  () => {
    void syncBoardRoute();
  },
  { immediate: true }
);

/** 게시판 영역을 벗어나면 열린 상세·편집 상태가 다음 route 위에 남지 않도록 정리한다. */
onBeforeUnmount(() => {
  if (store.registOpen) store.closeRegist();
  if (store.detailOpen) store.closeDetail();
});
</script>