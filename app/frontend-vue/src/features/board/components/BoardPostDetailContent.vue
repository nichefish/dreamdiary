<template>
  <!--begin::게시판 게시물 상세 본문 — 상세 모달·독립 상세 페이지가 공유한다 (저널 스레드 JournalThreadDetailContent 와 같은 역할)-->
  <div v-if="post" class="board-post-dtl-vue-root">
    <!--begin::헤더 (제목 + 작성자/일시)-->
    <div class="mb-0">
      <div class="d-flex align-items-center flex-wrap gap-2 mb-2">
        <span v-if="post.prefix" class="ctgr-span ctgr-gray">{{ post.prefix.name }}</span>
        <span class="fs-3 fw-bolder text-gray-900">{{ post.title }}</span>
      </div>
      <div class="d-flex align-items-center flex-wrap gap-3 text-muted fs-7">
        <span v-if="post.createdByNm"><i class="bi bi-person pe-1"></i>{{ post.createdByNm }}</span>
        <span v-if="post.createdDt"><i class="bi bi-clock pe-1"></i>{{ post.createdDt }}</span>
      </div>
    </div>
    <!--end::헤더-->

    <div class="separator separator-dashed border-gray-300 my-8"></div>

    <!--begin::본문-->
    <div
      class="fs-4 fw-normal text-gray-800 px-5 py-1 pb-6 min-h-150px"
      v-html="post.markdownContent || post.content || ''"
    ></div>
    <!--end::본문-->

    <!--begin::태그-->
    <div v-if="hasTags" class="mt-4">
      <i class="bi bi-tag me-1"></i>
      <TagChip
        v-for="tag in post.tag?.list"
        :key="'board-dtl-tag-' + String(tag.tagId)"
        :name="tag.name"
        :ctgr="tag.ctgr"
      />
    </div>
    <!--end::태그-->

    <!--begin::댓글 영역 (TODO: 댓글 연동 예정)-->
    <div class="separator separator-dashed border-gray-200 my-6"></div>
    <div class="text-muted fs-7 text-center py-3">{{ t('board.post.detail.comment-todo') }}</div>
    <!--end::댓글 영역-->
  </div>
  <!--end::게시판 게시물 상세 본문-->
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useLocaleStore } from "@/shared/i18n/stores/locale";
import TagChip from "@/shared/ui/tag/TagChip.vue";
import type { BoardPostDto } from "@/features/board/stores/boardPost";

const props = defineProps<{
  /** 표시할 게시물 상세 DTO */
  post: BoardPostDto | null;
}>();

const { t } = useLocaleStore();

const hasTags = computed(() =>
  Array.isArray(props.post?.tag?.list) && props.post!.tag!.list!.length > 0
);
</script>