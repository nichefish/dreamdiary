<template>
  <!--begin::태그 클라우드 한 행 (라벨 열 + 태그 열)
    여러 섹션을 쌓는 화면은 이 컴포넌트를 행마다 반복하고 구분선은 호출부가 둔다. -->
  <div class="row align-items-center min-h-42px">
    <div v-if="label" class="tag-cloud__label col-auto d-none d-md-flex me-6 text-center fs-6">
      <b>{{ label }} :</b>
    </div>
    <div class="col flex-grow-1">
      <span v-if="loading" class="text-muted fs-7">{{ loadingText ?? `${t("common.loading")}...` }}</span>
      <span v-else-if="errorMessage" class="text-danger fs-7">{{ errorMessage }}</span>
      <span v-else-if="tags.length === 0" class="text-muted fs-7">{{ emptyText }}</span>
      <span v-else class="d-flex flex-wrap align-items-center">
        <button
          v-for="tag in tags"
          :key="`${keyPrefix}${String(tag.id)}`"
          type="button"
          class="btn btn-link py-2 me-3 px-0 cursor-pointer opacity-hover text-decoration-none d-inline-flex align-items-center"
          :class="{ 'tag-cloud__tag--on': selectedTagId != null && selectedTagId === tag.id }"
          :title="tagTitle ?? undefined"
          @click.stop="emit('tag-click', tag, $event)"
        >
          <span :class="[tag.tagClass, tag.textClass]" class="d-inline-flex align-items-center">
            <span v-if="tag.ctgr" class="fs-7 text-noti">[{{ tag.ctgr }}]</span>
            <span>{{ tag.name }}</span>
          </span>
          <span class="fs-9 text-noti fw-normal tag-cloud__count">{{ tag.contentSize ?? 0 }}</span>
        </button>
      </span>
    </div>
  </div>
  <!--end::태그 클라우드 한 행-->
</template>

<script setup lang="ts">
import { useLocaleStore } from "@/shared/i18n/stores/locale";

/**
 * 태그 클라우드 항목.
 * 크기(`tagClass` = `ts-1`~`ts-9`)와 색(`textClass`)은 서버가 빈도·태그 프로필로 확정해 내려준다.
 * 화면은 그 값을 해석하지 않고 그대로 적용한다.
 */
export interface TagCloudTag {
  id: number | string;
  name: string;
  ctgr?: string | null;
  contentSize?: number;
  tagClass?: string;
  textClass?: string;
}

/**
 * 태그 클라우드 표시 계약.
 * <pre>
 *  저널·연간·게시판·Keep 이 같은 표시를 공유한다. 클릭이 무엇을 하는지(컨텍스트 메뉴/필터)는
 *  호출부가 `tag-click` 으로 결정하며, 이 컴포넌트는 표시와 선택 상태만 책임진다.
 * </pre>
 */
withDefaults(defineProps<{
  /** 표시할 태그 목록 */
  tags: readonly TagCloudTag[];
  /** 라벨 열 문구. 없으면 라벨 열 자체를 두지 않는다. */
  label?: string | null;
  /** 로딩 중 여부 */
  loading?: boolean;
  /** 조회 실패 문구. 정상 빈 목록과 구분해 표시한다. */
  errorMessage?: string | null;
  /** 정상 빈 목록 문구 */
  emptyText?: string;
  /** 로딩 문구. 없으면 공통 `common.loading` 을 쓴다. */
  loadingText?: string | null;
  /** 태그 버튼 tooltip */
  tagTitle?: string | null;
  /** 선택된 태그 ID. 필터형 호출부만 쓴다. */
  selectedTagId?: number | string | null;
  /** 같은 화면에 여러 행을 둘 때 key 충돌을 막는 접두사 */
  keyPrefix?: string;
}>(), {
  label: null,
  loading: false,
  errorMessage: null,
  emptyText: "-",
  loadingText: null,
  tagTitle: null,
  selectedTagId: null,
  keyPrefix: "tag-cloud-",
});

const emit = defineEmits<{
  "tag-click": [tag: TagCloudTag, event: MouseEvent];
}>();

const { t } = useLocaleStore();
</script>

<style scoped>
.tag-cloud__label {
  width: 6.25rem;
  justify-content: center;
}

.tag-cloud__count {
  margin-left: 0.2em;
}

/* 필터형 호출부의 선택 표시. 글자 스타일(tagClass/textClass)은 건드리지 않고 배경으로만 구분한다. */
.tag-cloud__tag--on {
  background-color: var(--bs-light-primary);
  border-radius: 0.475rem;
  padding-left: 0.5rem !important;
  padding-right: 0.5rem !important;
}
</style>
