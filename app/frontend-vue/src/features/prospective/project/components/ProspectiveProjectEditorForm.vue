<template>
  <form class="border rounded p-4 bg-light" @submit.prevent="emit('submit')">
    <!-- 이름 행: 왼쪽 좁은 개인 말머리(0..1) 선택 + 나머지 너비 이름 입력. 말머리 목록은 PROSPECTIVE_PROJECT 개인 Scope이며 관리 모달 상단 버튼에서만 관리한다. -->
    <div class="mb-3">
      <label class="form-label required">{{ t("common.name") }}</label>
      <div class="d-flex align-items-center gap-2">
        <select
          v-model="selectedPrefixId"
          class="form-select form-select-sm flex-shrink-0 w-auto"
          style="max-width: 10rem"
          :aria-label="t('prospective.project.prefix.field')"
          :disabled="saving"
        >
          <option :value="null">{{ t("prospective.project.prefix.none") }}</option>
          <option v-if="inactivePrefix" :value="inactivePrefix.id" disabled>
            [{{ inactivePrefix.name }}] ({{ t("status.unuse") }})
          </option>
          <option v-for="prefix in prefixOptions" :key="prefix.id" :value="prefix.id">[{{ prefix.name }}]</option>
        </select>
        <input
          :value="title"
          class="form-control form-control-sm flex-grow-1"
          maxlength="200"
          :aria-label="t('common.name')"
          @input="emit('update:title', ($event.target as HTMLInputElement).value.trim())"
        />
      </div>
      <div v-if="prefixLoadFailed" class="text-danger fs-8 mt-1">{{ t("prospective.project.prefix.options.failure") }}</div>
    </div>
    <div class="mb-3">
      <label class="form-label">{{ t("prospective.project.description") }}</label>
      <textarea
        :value="description"
        class="form-control form-control-sm"
        rows="3"
        @input="emit('update:description', ($event.target as HTMLTextAreaElement).value)"
      ></textarea>
    </div>
    <div class="d-flex justify-content-end gap-2">
      <button type="button" class="btn btn-sm btn-light" :disabled="saving" @click="emit('cancel')">
        {{ t("common.cancel") }}
      </button>
      <button type="submit" class="btn btn-sm btn-primary" :disabled="saving">
        {{ t("common.save") }}
      </button>
    </div>
  </form>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useLocaleStore } from "@/shared/i18n/stores/locale";
import type { PersonalPrefixOption } from "@/features/attachable/stores/personalPrefixOptions";

const props = withDefaults(defineProps<{
  title: string;
  description: string;
  saving: boolean;
  /** 선택한 말머리 ID. null이면 말머리 없음. */
  prefixId?: number | null;
  /** 활성 개인 말머리 선택지 (PROSPECTIVE_PROJECT). */
  prefixOptions?: PersonalPrefixOption[];
  /** 활성 선택지에 없는 비활성 기존 선택. 선택 유지용으로만 표시하고 새로 고를 수는 없다. */
  inactivePrefix?: { id: number; name?: string | null } | null;
  /** 말머리 선택지 조회 실패 여부. */
  prefixLoadFailed?: boolean;
}>(), {
  prefixId: null,
  prefixOptions: () => [],
  inactivePrefix: null,
  prefixLoadFailed: false,
});

const emit = defineEmits<{
  "update:title": [value: string];
  "update:description": [value: string];
  "update:prefixId": [value: number | null];
  submit: [];
  cancel: [];
}>();

const { t } = useLocaleStore();

const selectedPrefixId = computed<number | null>({
  get: () => props.prefixId ?? null,
  set: (value) => emit("update:prefixId", value ?? null),
});
</script>
