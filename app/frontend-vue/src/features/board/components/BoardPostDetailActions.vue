<template>
  <!--begin::게시판 게시물 상세 액션 (수정·삭제) — 상세 모달·독립 상세 페이지 공용-->
  <template v-if="postId">
    <button
      type="button"
      class="btn btn-sm btn-light-primary"
      :title="t('common.edit')"
      @click="onEdit"
    >
      <i class="bi bi-pencil-square me-1"></i>
      {{ t('common.edit') }}
    </button>
    <button
      type="button"
      class="btn btn-sm btn-light-danger"
      :title="t('common.delete')"
      @click="onDelete"
    >
      <i class="bi bi-trash me-1"></i>
      {{ t('common.delete') }}
    </button>
  </template>
  <!--end::게시판 게시물 상세 액션-->
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useLocaleStore } from "@/shared/i18n/stores/locale";
import { useBoardPostStore } from "@/features/board/stores/boardPost";

const emit = defineEmits<{
  /** 삭제 성공 후 (호출 표면이 닫기·목록 복귀를 결정한다) */
  (e: "deleted", id: number): void;
}>();

const { t } = useLocaleStore();
const store = useBoardPostStore();

const postId = computed(() => store.detailModel?.id ?? null);

/** 열린 상세를 그대로 둔 채 수정 모달을 연다. 저장 성공 시 store 가 열린 상세를 다시 조회한다. */
function onEdit(): void {
  const id = postId.value;
  if (id == null) return;
  void store.openModify(id);
}

/** 확인 후 삭제한다. 성공하면 store 가 상세를 닫고, 호출 표면에 알린다. */
async function onDelete(): Promise<void> {
  const id = postId.value;
  if (id == null) return;
  if (await store.deletePost(id)) emit("deleted", id);
}
</script>