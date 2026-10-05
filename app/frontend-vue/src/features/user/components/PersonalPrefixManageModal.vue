<template>
  <!--begin::개인 말머리 관리 모달 — 내 설정 말머리 탭과 저널 스레드 목록이 공유한다.-->
  <div
    class="modal fade show d-block"
    tabindex="-1"
    role="dialog"
    aria-modal="true"
    aria-labelledby="user-prefix-modal-title"
    @keydown.esc="requestClose"
  >
    <div class="modal-dialog modal-xl">
      <div class="modal-content">
        <div class="modal-header">
          <div>
            <h5 id="user-prefix-modal-title" class="modal-title">{{ title }}</h5>
            <p class="text-muted fs-8 mb-0 mt-1">{{ description }}</p>
          </div>
          <button type="button" class="btn-close" :disabled="store.saving" @click="requestClose"></button>
        </div>

        <div class="modal-body">
          <div v-if="errorMessage" class="alert alert-danger py-3">{{ errorMessage }}</div>

          <div class="d-flex justify-content-end mb-4">
            <button
              type="button"
              class="btn btn-sm btn-primary"
              :disabled="store.loading || store.saving"
              @click="beginNewPrefix"
            >
              <i class="bi bi-plus-lg me-1"></i>{{ t("user.my.prefixes.add") }}
            </button>
          </div>

          <form v-if="editing" class="border rounded p-4 mb-5 bg-light" @submit.prevent="submitPrefix">
            <div class="row g-3 align-items-end">
              <div class="col-md-5">
                <label class="form-label required">{{ t("common.name") }}</label>
                <input v-model.trim="form.name" class="form-control form-control-sm" maxlength="100" />
              </div>
              <div class="col-md-3">
                <label class="form-label">{{ t("user.my.prefixes.color") }}</label>
                <select
                  v-model="form.textClassCd"
                  class="form-select form-select-sm"
                  :class="colorSelectClass"
                >
                  <option value="">{{ t("prefix.color.default") }}</option>
                  <option
                    v-for="option in attachableStore.tagProfileTextClassOptions"
                    :key="option.code"
                    :value="option.code"
                    :class="option.description"
                  >{{ option.codeName }}</option>
                </select>
              </div>
              <div class="col-md-2">
                <label class="form-label">{{ t("common.sort-order") }}</label>
                <input v-model.number="form.sortOrder" type="number" min="0" class="form-control form-control-sm" />
              </div>
              <div class="col-md-2 d-flex gap-2">
                <button type="button" class="btn btn-sm btn-light flex-grow-1" @click="cancelEdit">{{ t("common.cancel") }}</button>
                <button type="submit" class="btn btn-sm btn-primary flex-grow-1" :disabled="store.saving">{{ t("common.save") }}</button>
              </div>
            </div>
          </form>

          <div v-if="store.loading" class="user-prefix-loading">
            <span class="spinner-border spinner-border-sm me-2"></span>{{ t("common.loading") }}
          </div>
          <div v-else-if="!errorMessage && !store.prefixes.length" class="text-muted text-center py-8">
            {{ t("user.my.prefixes.empty") }}
          </div>
          <div v-for="prefix in store.prefixes" v-else :key="prefix.id" class="d-flex align-items-center border-bottom py-3">
            <span class="prefix-color me-3" :class="prefix.textClass" :style="{ backgroundColor: prefix.textClass ? 'currentColor' : '#A1A5B7' }"></span>
            <div class="flex-grow-1">
              <span :class="{ 'text-muted text-decoration-line-through': prefix.activeYn === 'N' }">{{ prefix.name }}</span>
              <span class="text-muted fs-8 ms-2">{{ t("common.sort-order") }} {{ prefix.sortOrder }}</span>
            </div>
            <button
              type="button"
              class="btn btn-sm btn-light me-2"
              :disabled="store.saving"
              @click="beginEditPrefix(prefix)"
            >
              {{ t("common.edit") }}
            </button>
            <button
              type="button"
              :class="prefix.activeYn === 'Y' ? 'btn-light-danger' : 'btn-light-success'"
              class="btn btn-sm"
              :disabled="store.saving"
              @click="togglePrefixActive(prefix)"
            >
              {{ prefix.activeYn === "Y" ? t("status.unuse") : t("status.use") }}
            </button>
          </div>
        </div>

        <div class="modal-footer">
          <button type="button" class="btn btn-sm btn-light" :disabled="store.saving" @click="requestClose">
            {{ t("common.close") }}
          </button>
        </div>
      </div>
    </div>
  </div>
  <div class="modal-backdrop fade show"></div>
  <!--end::개인 말머리 관리 모달-->
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from "vue";
import axios from "axios";
import { useLocaleStore } from "@/shared/i18n/stores/locale";
import { swalConfirm } from "@/shared/utils/swal";
import { useUserPrefixesStore, type UserPrefix } from "@/features/user/stores/userPrefixes";
import { useAttachableModalStore } from "@/features/attachable/stores/attachableModal";

/**
 * 개인 말머리 관리 모달.
 * <p>
 * 한 `(user, content_type)` Scope의 평면 Prefix 목록을 조회하고 생성·수정·정렬값·색·활성 상태를 관리한다.
 * 마운트 시 해당 `contentType` 목록만 조회하고, 언마운트 시 관리 목록을 비워 다른 대상의 항목이 남지 않게 한다.
 * 저장·활성 변경이 성공하면 `changed`를 발생시켜 호출 화면이 자신의 선택지·목록을 서버 확정 상태로 다시 맞추게 한다.
 * </p>
 */
const props = defineProps<{
  /** 관리 대상 개인 Prefix Scope의 content_type. */
  contentType: string;
  /** 모달 제목. */
  title: string;
  /** 모달 제목 아래 설명. */
  description: string;
}>();
const emit = defineEmits<{ close: []; changed: [] }>();

const store = useUserPrefixesStore();
const attachableStore = useAttachableModalStore();
const { t } = useLocaleStore();
const errorMessage = ref("");
/**
 * 선택한 색 의미 코드의 CSS 클래스.
 * 태그 프로필 모달과 같은 규약으로, select 자체에 선택 색을 입혀 현재 색이 바로 보이게 한다.
 */
const colorSelectClass = computed(() =>
  attachableStore.tagProfileTextClassOptions.find((o) => o.code === form.textClassCd)?.description ?? ""
);

const editing = ref(false);
const form = reactive<UserPrefix>({ name: "", textClassCd: "", sortOrder: 0 });

/** 관리 요청 실패를 서버 메시지 우선으로 모달 상단에 표시한다. */
function report(error: unknown) {
  console.error("[PersonalPrefixManageModal] 말머리 관리 요청 실패", { contentType: props.contentType }, error);
  if (axios.isAxiosError(error) && typeof error.response?.data?.message === "string") {
    errorMessage.value = error.response.data.message;
    return;
  }
  errorMessage.value = error instanceof Error ? error.message : t("common.error");
}

/** 편집 폼을 신규 입력 기본값으로 되돌린다. */
function resetForm() {
  Object.assign(form, { id: undefined, name: "", textClassCd: "", sortOrder: 0 });
}

/** 신규 Prefix 입력 폼을 연다. */
function beginNewPrefix() {
  resetForm();
  editing.value = true;
}

/** 선택한 Prefix의 현재 값으로 수정 폼을 연다. */
function beginEditPrefix(prefix: UserPrefix) {
  Object.assign(form, { ...prefix, textClassCd: prefix.textClassCd === "DEFAULT" ? "" : (prefix.textClassCd ?? "") });
  editing.value = true;
}

/** 편집을 취소하고 폼을 비운다. */
function cancelEdit() {
  editing.value = false;
  resetForm();
}

/** 편집 폼을 저장하고 성공 시 `changed`를 발생시킨다. */
async function submitPrefix() {
  if (!form.name) {
    errorMessage.value = t("user.my.prefixes.name.required");
    return;
  }
  try {
    await store.savePrefix(props.contentType, { ...form });
    editing.value = false;
    errorMessage.value = "";
    emit("changed");
  } catch (error) {
    report(error);
  }
}

/** 확인 후 Prefix 활성 상태를 전환하고 성공 시 `changed`를 발생시킨다. 비활성화는 기존 콘텐츠 선택을 보존한다. */
async function togglePrefixActive(prefix: UserPrefix) {
  if (!prefix.id || !await swalConfirm(t(
    prefix.activeYn === "Y"
      ? "user.my.prefixes.disable.confirm"
      : "user.my.prefixes.enable.confirm",
  ))) return;
  try {
    await store.setPrefixActive(props.contentType, prefix.id, prefix.activeYn !== "Y");
    emit("changed");
  } catch (error) {
    report(error);
  }
}

/** 저장 중에는 닫기를 무시하고, 편집 중이면 미저장 변경 폐기를 확인한 뒤 `close`를 발생시킨다. */
async function requestClose() {
  if (store.saving) {
    console.warn("[PersonalPrefixManageModal] 저장 중 모달 닫기 요청 무시", { contentType: props.contentType });
    return;
  }
  if (editing.value && !await swalConfirm(t("user.my.prefixes.modal.close.confirm"))) return;
  emit("close");
}

onMounted(async () => {
  store.clearPrefixes();
  void attachableStore.loadTagProfileTextClassOptions();
  try {
    await store.fetchPrefixes(props.contentType);
  } catch (error) {
    report(error);
  }
});

onBeforeUnmount(() => {
  store.clearPrefixes();
});
</script>

<style scoped>
.prefix-color { width:14px; height:14px; border-radius:50%; flex:0 0 auto; }
.user-prefix-loading {
  display:flex;
  align-items:center;
  justify-content:center;
  min-height:120px;
  color:var(--bs-gray-600);
}
</style>
