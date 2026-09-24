<template>
  <form class="border rounded p-4 bg-light" @submit.prevent="emit('submit')">
    <div class="mb-3">
      <label class="form-label required">{{ t("common.name") }}</label>
      <input
        :value="title"
        class="form-control form-control-sm"
        maxlength="200"
        @input="emit('update:title', ($event.target as HTMLInputElement).value.trim())"
      />
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
import { useLocaleStore } from "@/shared/i18n/stores/locale";

defineProps<{
  title: string;
  description: string;
  saving: boolean;
}>();

const emit = defineEmits<{
  "update:title": [value: string];
  "update:description": [value: string];
  submit: [];
  cancel: [];
}>();

const { t } = useLocaleStore();
</script>
