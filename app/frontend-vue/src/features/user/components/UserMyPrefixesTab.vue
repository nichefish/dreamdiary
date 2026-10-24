<template>
  <div class="user-prefix-settings">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h4 class="mb-1">{{ t("user.my.prefixes.title") }}</h4>
        <p class="text-muted mb-0">{{ t("user.my.prefixes.description") }}</p>
      </div>
    </div>

    <section
      v-for="group in prefixTargetGroups"
      :key="group.labelKey"
      class="user-prefix-domain-group"
    >
      <h5 class="text-muted fs-8 fw-bold mb-2 px-1">{{ t(group.labelKey) }}</h5>
      <div
        class="list-group user-prefix-target-list"
        role="group"
        :aria-label="`${t('user.my.prefixes.target.aria-label')}: ${t(group.labelKey)}`"
      >
        <button
          v-for="target in group.targets"
          :key="target.contentType"
          type="button"
          class="list-group-item list-group-item-action d-flex align-items-center justify-content-between gap-4 py-4"
          @click="openModal(target.contentType)"
        >
          <span>
            <span class="d-block fw-bold">{{ t(target.labelKey) }}</span>
            <span class="d-block text-muted fs-8 mt-1">{{ t(target.descriptionKey) }}</span>
          </span>
          <i class="bi bi-chevron-right text-muted"></i>
        </button>
      </div>
    </section>

    <PersonalPrefixManageModal
      v-if="selectedTarget"
      :key="selectedTarget.contentType"
      :content-type="selectedTarget.contentType"
      :title="t(selectedTarget.labelKey)"
      :description="t(selectedTarget.descriptionKey)"
      @close="closeModal"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { useLocaleStore } from "@/shared/i18n/stores/locale";
import PersonalPrefixManageModal from "@/features/user/components/PersonalPrefixManageModal.vue";

const { t } = useLocaleStore();

type PersonalPrefixContentType =
  | "JOURNAL_CHAPTER_DIARY"
  | "JOURNAL_CHAPTER_NOTE"
  | "JOURNAL_DIARY"
  | "JOURNAL_DREAM"
  | "JOURNAL_NOTE"
  | "PROSPECTIVE_KEEP";

interface PersonalPrefixTarget {
  contentType: PersonalPrefixContentType;
  labelKey: string;
  descriptionKey: string;
}

interface PersonalPrefixTargetGroup {
  labelKey: string;
  targets: readonly PersonalPrefixTarget[];
}

/**
 * 개인 말머리 관리 대상 그룹.
 * 저널 도메인의 개인 말머리 content_type 다섯 종류와 Keep 항목 말머리를 정해진 순서로 노출한다.
 * 스레드 말머리(JOURNAL_THREAD)는 저널 스레드 목록 화면의 탭 관리 버튼에서 같은 관리 모달로 관리하므로 이 카탈로그에 두지 않는다.
 * 챕터 말머리는 일기 챕터(JOURNAL_CHAPTER_DIARY)와 노트 챕터(JOURNAL_CHAPTER_NOTE)로 각각 분리 관리한다.
 * 다른 도메인을 지원할 때는 영속 구조를 바꾸지 않고 이 화면 카탈로그에 그룹을 추가한다.
 * Prefix Scope가 아직 없어도 대상 행은 표시하며 첫 Prefix 등록 시 서버가 Scope를 lazy 생성한다.
 */
const prefixTargetGroups: readonly PersonalPrefixTargetGroup[] = [{
  labelKey: "user.my.prefixes.domain.journal",
  targets: [
    {
      contentType: "JOURNAL_CHAPTER_DIARY",
      labelKey: "user.my.prefixes.target.chapter-diary",
      descriptionKey: "user.my.prefixes.target.chapter-diary.description",
    },
    {
      contentType: "JOURNAL_CHAPTER_NOTE",
      labelKey: "user.my.prefixes.target.chapter-note",
      descriptionKey: "user.my.prefixes.target.chapter-note.description",
    },
    {
      contentType: "JOURNAL_DIARY",
      labelKey: "user.my.prefixes.target.diary",
      descriptionKey: "user.my.prefixes.target.diary.description",
    },
    {
      contentType: "JOURNAL_DREAM",
      labelKey: "user.my.prefixes.target.dream",
      descriptionKey: "user.my.prefixes.target.dream.description",
    },
    {
      contentType: "JOURNAL_NOTE",
      labelKey: "user.my.prefixes.target.note",
      descriptionKey: "user.my.prefixes.target.note.description",
    },
  ],
}, {
  labelKey: "user.my.prefixes.domain.keep",
  targets: [
    {
      // Keep 항목의 영속 content_type 이 PROSPECTIVE_KEEP 이며 말머리 Scope 도 같은 값을 쓴다.
      contentType: "PROSPECTIVE_KEEP",
      labelKey: "user.my.prefixes.target.keep",
      descriptionKey: "user.my.prefixes.target.keep.description",
    },
  ],
}];
const prefixTargets = prefixTargetGroups.flatMap((group) => group.targets);
const selectedContentType = ref<PersonalPrefixContentType | null>(null);
const selectedTarget = computed<PersonalPrefixTarget | null>(() =>
  prefixTargets.find((target) => target.contentType === selectedContentType.value) ?? null,
);

/** 선택한 대상의 관리 모달을 연다. 목록 조회는 모달이 마운트될 때 해당 contentType만 수행한다. */
function openModal(contentType: PersonalPrefixContentType) {
  selectedContentType.value = contentType;
}

/** 관리 모달이 닫기를 확정하면 대상 선택을 비운다. 미저장 폐기 확인은 모달이 담당한다. */
function closeModal() {
  selectedContentType.value = null;
}
</script>

<style scoped>
.user-prefix-domain-group:not(:last-of-type) { margin-bottom:2rem; }
.user-prefix-target-list .list-group-item { border-left:0; border-right:0; }
</style>
