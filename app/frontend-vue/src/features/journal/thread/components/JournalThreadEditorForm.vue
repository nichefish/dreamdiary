<template>
  <!--begin::저널 스레드 등록/수정 공용 폼-->
  <div v-if="store.registLoading" class="d-flex justify-content-center py-10">
    <span class="spinner-border text-primary" role="status"></span>
  </div>

  <form v-else-if="model" id="journalThreadRegistForm" class="form" @submit.prevent>
    <input type="hidden" name="id" :value="model.id ?? ''" />
    <input type="hidden" name="contentType" value="JOURNAL_THREAD" />

    <!--begin::말머리 — 전체 관리는 스레드 목록의 말머리 탭 관리 버튼이 담당하고, 폼은 선택과 빠른 추가만 제공한다.-->
    <div class="row d-flex mb-8">
      <div class="col-12">
        <label class="d-flex align-items-center mb-2">
          <span class="text-gray-700 fs-6 fw-bolder">{{ t("journal.thread.prefix.label") }}</span>
        </label>
        <select
          v-model="model.prefixId"
          class="form-select form-select-solid"
        >
          <option :value="null">{{ t("journal.thread.prefix.select") }}</option>
          <option v-for="prefix in store.prefixOptions" :key="prefix.id" :value="prefix.id">{{ prefix.name }}</option>
        </select>
        <div class="input-group input-group-sm mt-3">
          <input
            v-model="quickAddName"
            type="text"
            class="form-control form-control-solid"
            maxlength="100"
            :placeholder="t('journal.thread.prefix.quick-add.placeholder')"
            :disabled="quickAdding"
            @keydown.enter.prevent="quickAdd"
          />
          <button
            type="button"
            class="btn btn-light-primary"
            :disabled="quickAdding || !quickAddName.trim()"
            @click="quickAdd"
          >
            <span v-if="quickAdding" class="spinner-border spinner-border-sm me-1"></span>
            <i v-else class="bi bi-plus-lg me-1"></i>{{ t("journal.thread.prefix.quick-add.action") }}
          </button>
        </div>
        <div v-if="store.prefixError" class="text-danger fs-8 mt-2">
          {{ store.prefixError }}
        </div>
      </div>
    </div>
    <!--end::말머리-->

    <!--begin::제목-->
    <div class="row d-flex mb-8">
      <div class="col-12">
        <label class="d-flex align-items-center mb-2">
          <span class="text-gray-700 fs-6 fw-bolder">{{ t("common.title") }}</span>
        </label>
        <input
          name="title"
          v-model="model.title"
          class="form-control form-control-solid"
          :placeholder="t('journal.thread.title.placeholder')"
          maxlength="100"
          autocomplete="off"
        />
      </div>
    </div>
    <!--end::제목-->

    <!--begin::본문-->
    <div class="row d-flex mb-8">
      <div class="col-12">
        <label class="d-flex align-items-center mb-2">
          <span class="text-gray-700 fs-6 fw-bolder">{{ t("common.content") }}</span>
        </label>
        <RichEditor :model-value="model.content" @update:model-value="model && (model.content = $event)" />
      </div>
    </div>
    <!--end::본문-->
  </form>
  <!--end::저널 스레드 등록/수정 공용 폼-->
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import axios from "axios";
import RichEditor from "@/shared/ui/editor/RichEditor.vue";
import { useJournalThreadStore } from "@/features/journal/stores/journalThread";
import { useLocaleStore } from "@/shared/i18n/stores/locale";

const store = useJournalThreadStore();
const { t } = useLocaleStore();
const quickAddName = ref("");
const quickAdding = ref(false);

/** 모달과 독립 편집 페이지는 같은 등록/수정 모델을 직접 편집한다. */
const model = computed(() => store.registModel);

async function quickAdd() {
  const name = quickAddName.value.trim();
  if (!name || quickAdding.value) return;
  quickAdding.value = true;
  try {
    await store.quickAddPrefix(name);
    quickAddName.value = "";
  } catch (error) {
    if (axios.isAxiosError(error) && typeof error.response?.data?.message === "string") {
      store.prefixError = error.response.data.message;
    } else {
      store.prefixError = error instanceof Error
        ? error.message
        : t("journal.thread.prefix.quick-add.failure");
    }
  } finally {
    quickAdding.value = false;
  }
}
</script>
