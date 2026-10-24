<template>
  <!--begin::Prospective Keep 등록/수정 모달-->
  <div ref="modalEl" class="modal fade" id="prospective_keep_regist_modal" tabindex="-1" aria-hidden="true" data-bs-backdrop="static" data-bs-keyboard="false">
    <div class="modal-dialog modal-xl">
      <div class="modal-content">

        <!--begin::Modal Header-->
        <div class="modal-header">
          <h5 class="modal-title">{{ isModify ? t('prospective.keep.modify.modal.title') : t('prospective.keep.regist.modal.title') }}</h5>
          <button
            type="button"
            class="btn-close"
            :title="closeArmed ? t('common.modal.close-armed.tooltip') : t('common.close')"
            @click="requestSafeClose"
          ></button>
        </div>
        <!--end::Modal Header-->

        <!--begin::Modal Body-->
        <div class="modal-body modal-mbl-body my-5">
          <form v-if="model" id="prospectiveKeepRegistForm" class="form" @submit.prevent>
            <input type="hidden" name="id" :value="model.id ?? ''" />

            <!--begin::Project / 제목 (말머리 + 제목 + 순서)-->
            <div class="row d-flex mb-8">
              <div class="col-lg-3 mb-3 mb-lg-0">
                <label class="d-flex align-items-center mb-2" for="projectId">
                  <span class="text-gray-700 fs-6 fw-bolder">{{ t('prospective.project.field') }}</span>
                </label>
                <select id="projectId" name="projectId" class="form-select form-select-solid" v-model="model.projectId">
                  <option :value="null">{{ t('prospective.project.unassigned') }}</option>
                  <option
                    v-for="project in projectStore.projects"
                    :key="project.id"
                    :value="project.id"
                    :disabled="projectStore.lifecycleKeyOf(project) !== 'OPEN'"
                  >{{ projectDisplayTitle(project) }}</option>
                </select>
                <div v-if="projectLoadError" class="text-danger fs-8 mt-2">{{ projectLoadError }}</div>
              </div>
              <div class="col-lg-9">
                <label class="d-flex align-items-center mb-2 required" for="title">
                  <span class="text-gray-700 fs-6 fw-bolder">{{ t('common.title') }}</span>
                  <span class="text-gray-500 fs-9 ms-2">{{ t('journal.field.title-max-100') }}</span>
                </label>
                <div class="row g-3">
                  <!--begin::말머리 (개인 말머리 목록이 비어 있으면 표시하지 않는다)-->
                  <div v-if="showPrefixField" class="col-lg-3">
                    <select
                      name="prefixId"
                      id="prefixId"
                      class="form-select form-select-solid"
                      v-model="model.prefixId"
                    >
                      <option :value="null">{{ t('prospective.keep.prefix.select') }}</option>
                      <option v-if="currentInactivePrefix" :value="currentInactivePrefix.id" disabled>
                        [{{ currentInactivePrefix.name }}] ({{ t('status.unuse') }})
                      </option>
                      <option
                        v-for="prefix in prefixOptions"
                        :key="prefix.id"
                        :value="prefix.id"
                      >[{{ prefix.name }}]</option>
                    </select>
                  </div>
                  <!--end::말머리-->
                  <div :class="titleColumnClass">
                    <input
                      type="text"
                      name="title"
                      id="title"
                      class="form-control form-control-solid"
                      v-model="model.title"
                      :placeholder="t('prospective.keep.title.placeholder')"
                      maxlength="100"
                      required
                    />
                  </div>
                  <div v-if="isModify" class="col-lg-1">
                    <input
                      type="number"
                      name="sortOrder"
                      id="sortOrder"
                      class="form-control form-control-solid"
                      v-model.number="model.sortOrder"
                      :placeholder="t('prospective.keep.sort-order.placeholder')"
                    />
                  </div>
                </div>
              </div>
            </div>
            <!--end::Project / 제목-->

            <!--begin::일시 / 유효 기한-->
            <div class="row d-flex mb-8">
              <div class="col-lg-6">
                <label class="d-flex align-items-center mb-2" for="dateText">
                  <span class="text-gray-700 fs-6 fw-bolder">{{ t('prospective.keep.field.date-text') }}</span>
                  <span class="text-gray-500 fs-9 ms-2">{{ t('prospective.keep.date-text.format-guide') }}</span>
                </label>
                <input
                  type="text"
                  name="dateText"
                  id="dateText"
                  class="form-control form-control-solid"
                  v-model="model.dateText"
                  :placeholder="t('prospective.keep.date-text.placeholder')"
                  maxlength="200"
                />
              </div>
              <div class="col-lg-6">
                <label class="d-flex align-items-center mb-2" for="expireDt">
                  <span class="text-gray-700 fs-6 fw-bolder">{{ t('prospective.keep.field.expire-dt') }}</span>
                  <span class="text-gray-500 fs-9 ms-2">{{ t('prospective.keep.expire-dt.rule') }}</span>
                </label>
                <div class="input-group">
                  <input
                    type="text"
                    name="expireDt"
                    id="expireDt"
                    ref="expireDtInputRef"
                    class="form-control form-control-solid"
                    v-model="expireDt"
                    :placeholder="t('prospective.keep.expire-dt.placeholder')"
                    autocomplete="off"
                    readonly
                  />
                  <button
                    type="button"
                    class="btn btn-icon btn-light"
                    :title="t('prospective.keep.expire-dt.pick')"
                    @click="openExpireDtPicker"
                  ><i class="bi bi-calendar3"></i></button>
                  <button
                    type="button"
                    class="btn btn-light"
                    :disabled="!canExtractExpireDt"
                    :title="t('prospective.keep.expire-dt.extract.tooltip')"
                    @click="extractExpireDt"
                  >{{ t('prospective.keep.expire-dt.extract') }}</button>
                </div>
                <div class="fs-8 mt-2" :class="expireDtHintClass">{{ expireDtHint }}</div>
              </div>
            </div>
            <!--end::일시 / 유효 기한-->

            <!--begin::위치-->
            <div class="row d-flex mb-8">
              <div class="col-12">
                <label class="d-flex align-items-center mb-2" for="location">
                  <span class="text-gray-700 fs-6 fw-bolder">{{ t('prospective.keep.field.location') }}</span>
                </label>
                <input
                  type="text"
                  name="location"
                  id="location"
                  class="form-control form-control-solid"
                  v-model="model.location"
                  :placeholder="t('prospective.keep.location.placeholder')"
                  maxlength="200"
                />
              </div>
            </div>
            <!--end::위치-->

            <!--begin::내용-->
            <div class="row mb-4">
              <div class="col-12">
                <label class="d-flex align-items-center mb-2" for="content">
                  <span class="text-gray-700 fs-6 fw-bolder">{{ t('common.content') }}</span>
                </label>
              </div>
              <div class="col-12">
                <RichEditor :model-value="model.content" @update:model-value="model && (model.content = $event)" :height="350" />
              </div>
            </div>
            <!--end::내용-->

            <!--begin::태그-->
            <div class="row mb-3">
              <div>
                <label for="tagListStr" class="mb-2">
                  <span class="text-gray-700 fs-6 fw-bolder">{{ t('common.tag') }}</span>
                </label>
              </div>
              <div class="col-xl-12">
                <TagifyEditor v-model="tagListStrWithCtgr" :category-map="keepCategoryMap" />
              </div>
            </div>
            <!--end::태그-->

          </form>
        </div>
        <!--end::Modal Body-->

        <!--begin::Modal Footer-->
        <div class="modal-footer">
          <div class="d-flex justify-content-end gap-2">
            <button
              type="button"
              class="btn btn-sm btn-light-primary"
              :title="t('journal.entry.preview.tooltip')"
              :disabled="!model || submitting"
              @click="preview"
            >
              <i class="bi bi-eye"></i>{{ t('common.preview') }}
            </button>
            <button
              type="button"
              class="btn btn-sm btn-primary"
              :disabled="submitting"
              @click="submit"
            >
              <span v-if="submitting" class="spinner-border spinner-border-sm me-1" role="status"></span>
              {{ t('common.save') }}
            </button>
            <button
              type="button"
              class="btn btn-sm"
              :class="closeArmed ? 'btn-light-warning' : 'btn-light'"
              :title="closeArmed ? t('common.modal.close-armed.tooltip') : t('common.close')"
              @click="requestSafeClose"
            >{{ t('common.close') }}</button>
          </div>
        </div>
        <!--end::Modal Footer-->

      </div>
    </div>
  </div>
  <!--end::Prospective Keep 등록/수정 모달-->
</template>

<script setup lang="ts">
import { swalConfirm, swalAlert, swalRequestError, swalAjaxResult } from "@/shared/utils/swal";
import { useSafeModalClose } from "@/shared/utils/safeModalClose";
import { ref, computed, watch, onMounted, onBeforeUnmount, nextTick } from "vue";
import RichEditor from "@/shared/ui/editor/RichEditor.vue";
import TagifyEditor from "@/shared/ui/tag/TagifyEditor.vue";
import { Modal } from "bootstrap";
import axios from "axios";
import { useProspectiveKeepModalStore } from "@/features/prospective/keep/stores/prospectiveKeepModal";
import { usePersonalPrefixOptionsStore } from "@/features/attachable/stores/personalPrefixOptions";
import { projectDisplayTitle } from "@/features/prospective/project/utils/projectDisplayTitle";
import { useProspectiveKeepCategoryMapStore } from "@/features/prospective/keep/stores/prospectiveKeepCategoryMap";
import { resolveJournalPrefixField } from "@/features/journal/utils/journalPrefixField";
import {
  daysUntilExpire,
  scanLastDate,
} from "@/features/prospective/keep/utils/expireDt";
import { bindSingleDatePicker, destroySingleDatePicker } from "@/shared/utils/flatpickrSingleDate";
import type { Instance as FlatpickrInstance } from "flatpickr/dist/types/instance";
import { useJournalStore } from "@/features/journal/stores/journal";
import { useRoute } from "vue-router";
import { refreshJournalDaysForRoute } from "@/features/journal/utils/journalDayRefresh";
import { useLocaleStore } from "@/shared/i18n/stores/locale";
import { useProspectiveProjectStore } from "@/features/prospective/project/stores/prospectiveProject";
import { openJournalEntryPreview, type KeepPreviewMeta, type KeepPreviewTag } from "@/features/journal/utils/journalEntryPreview";

const modalStore = useProspectiveKeepModalStore();
const journalStore = useJournalStore();
const personalPrefixOptionsStore = usePersonalPrefixOptionsStore();
const categoryMapStore = useProspectiveKeepCategoryMapStore();
const { t } = useLocaleStore();
const route = useRoute();
const projectStore = useProspectiveProjectStore();

const modalEl = ref<HTMLElement | null>(null);
const submitting = ref(false);
const projectLoadError = ref("");
let bsModal: InstanceType<typeof Modal> | null = null;
const { closeArmed, requestSafeClose, resetSafeClose } = useSafeModalClose(() => {
  modalStore.closeRegist();
});

const model = computed(() => modalStore.registModel);
const isModify = computed(() => !!model.value?.id);

/**
 * Keep 말머리 Scope content_type.
 * attachable 정체성 키와 같은 PROSPECTIVE_KEEP 목록 하나를 저널 사이드바·Keep 이 공유한다.
 */
const PREFIX_CONTENT_TYPE = "PROSPECTIVE_KEEP";

/** Keep 태그 Tagify 카테고리 자동완성 맵 (PROSPECTIVE_KEEP 단일 맵). */
const keepCategoryMap = computed(() => categoryMapStore.categoryMap);

/** 활성 개인 말머리 선택지. */
const prefixOptions = computed(() => personalPrefixOptionsStore.optionsFor(PREFIX_CONTENT_TYPE));

/** 활성 목록과 비활성 과거 선택을 합산한 말머리 필드 표시 상태. */
const prefixFieldPresentation = computed(() => resolveJournalPrefixField(
  prefixOptions.value,
  model.value?.prefix,
));

/** 기존 비활성 말머리는 수정 화면에 표시하고 같은 선택 유지 저장만 허용한다. */
const currentInactivePrefix = computed(() => prefixFieldPresentation.value.inactivePrefix);

/** 선택 가능한 말머리가 있을 때만 말머리 영역을 표시한다. */
const showPrefixField = computed(() => prefixFieldPresentation.value.visible);

/**
 * PC 제목 그룹 안에서 말머리(3열)·순서(1열, 수정 시에만) 표시 여부에 맞춰 제목 입력이 남은 열을 사용한다.
 * Project 3열과 제목 그룹 9열의 바깥 배치는 고정하고, 제목 그룹 내부 합이 항상 12열이 되도록 계산한다.
 */
const titleColumnClass = computed(() => {
  if (showPrefixField.value) return isModify.value ? "col-lg-8" : "col-lg-9";
  return isModify.value ? "col-lg-11" : "col-lg-12";
});

/**
 * 유효 기한 (ISO `yyyy-MM-dd`, 빈 문자열이면 기한 없음).
 * 일시 원문에서 파생하거나 사용자가 달력으로 직접 고른 값이며, 이 값이 그대로 저장된다.
 */
const expireDt = ref("");

/** 유효 기한 달력 인스턴스. 모달을 닫을 때 destroy 한다. */
const expireDtInputRef = ref<HTMLInputElement | null>(null);
let expireDtFp: FlatpickrInstance | null = null;

/** 유효 기한 산정 디바운스 간격(ms). 입력을 멈춘 뒤에만 일시 원문을 다시 해석한다. */
const EXPIRE_CALC_DEBOUNCE_MS = 500;

/** 산정에 반영된 일시 원문. 입력 중에는 이전 값에 머물고 디바운스·flush 시점에 갱신된다. */
const settledDateText = ref<string | null | undefined>(model.value?.dateText);
let expireCalcTimer: ReturnType<typeof setTimeout> | null = null;

/** 일시 원문에서 찾아낸 날짜들. 「기한 추출」 가능 여부와 안내 문구가 이 결과를 쓴다. */
const dateTextScan = computed(() => scanLastDate(settledDateText.value));

/** 일시에서 뽑을 날짜가 실제로 있을 때만 추출 버튼을 연다. */
const canExtractExpireDt = computed(() => dateTextScan.value.last !== null);

/**
 * 유효 기한 아래 안내 문구.
 *
 * 기한이 있으면 남은 일수를, 없으면 일시에서 무엇을 뽑을 수 있는지 보여 준다.
 * 뽑을 수 있는 날짜를 모두 나열해 「왜 그 날이 마지막인지」가 눈에 보이게 한다.
 */
const expireDtHint = computed(() => {
  if (expireDt.value) {
    const days = daysUntilExpire(expireDt.value);
    if (Number.isNaN(days)) return "";
    if (days === 0) return t("prospective.keep.expire-dt.relative.today");
    return days > 0
      ? t("prospective.keep.expire-dt.relative.left").replace("{0}", String(days))
      : t("prospective.keep.expire-dt.relative.past").replace("{0}", String(-days));
  }
  const scan = dateTextScan.value;
  if (scan.last === null) return t("prospective.keep.expire-dt.hint.empty");
  return t("prospective.keep.expire-dt.hint.candidates")
    .replace("{0}", scan.found.join(", "))
    .replace("{1}", scan.last);
});

/** 안내 문구 색. 이미 지난 기한을 눈에 띄게 가른다. */
const expireDtHintClass = computed(() => {
  if (!expireDt.value) return "text-gray-500";
  return daysUntilExpire(expireDt.value) < 0 ? "text-warning" : "text-gray-600";
});

/**
 * 일시 원문에서 가장 늦은 날짜를 뽑아 유효 기한에 넣는다.
 * 뽑을 날짜가 없으면 기존 값을 건드리지 않는다.
 */
function extractExpireDt(): void {
  // 버튼 클릭 시점의 원문을 쓰도록 대기 중인 산정을 먼저 반영한다.
  syncSettledDateText();
  const last = dateTextScan.value.last;
  if (last === null) return;
  expireDt.value = last;
  expireDtFp?.setDate(last, false);
}

/**
 * 일시를 고치면 유효 기한을 자동으로 채운다 -- 단 **기한이 비어 있을 때만**.
 *
 * 일시는 사용자가 쓴 원문이라 기계가 되쓸 수 없으므로, 달력으로 교정한 기한과
 * 원문을 일치시킬 방법이 없다. 값이 있는데도 자동으로 덮으면 사용자가 내린 판단이
 * 일시의 오타 수정 한 번에 조용히 사라진다. 이미 값이 있으면 「기한 추출」 버튼으로만 다시 뽑는다.
 */
watch(
  () => model.value?.dateText,
  () => {
    cancelExpireCalc();
    expireCalcTimer = setTimeout(flushExpireCalc, EXPIRE_CALC_DEBOUNCE_MS);
  }
);

/** 대기 중인 유효 기한 산정 타이머를 취소한다. */
function cancelExpireCalc(): void {
  if (expireCalcTimer !== null) clearTimeout(expireCalcTimer);
  expireCalcTimer = null;
}

/** 산정 기준 원문을 현재 입력값으로 맞춘다(자동 채움 없음). */
function syncSettledDateText(): void {
  settledDateText.value = model.value?.dateText;
}

/**
 * 대기 중인 산정을 즉시 실행한다. 디바운스 만료·저장·미리보기 직전에 호출해
 * 마지막 입력 기준 기한이 반영되도록 한다. 기한이 비어 있을 때만 자동으로 채운다.
 */
function flushExpireCalc(): void {
  const pending = expireCalcTimer !== null;
  cancelExpireCalc();
  syncSettledDateText();
  if (pending && !expireDt.value) extractExpireDt();
}

/** 유효 기한 달력을 연다. 읽기 전용 입력이라 달력이 유일한 직접 수정 경로다. */
function openExpireDtPicker(): void {
  if (!expireDtFp) attachExpireDtPicker();
  expireDtFp?.open();
}

/** 유효 기한 달력을 현재 값으로 바인딩한다. */
function attachExpireDtPicker(): void {
  destroyExpireDtPicker();
  const input = expireDtInputRef.value;
  if (!input) return;
  expireDtFp = bindSingleDatePicker(input, {
    initial: expireDt.value,
    onValue: (dateStr) => { expireDt.value = dateStr; },
  });
}

function destroyExpireDtPicker(): void {
  destroySingleDatePicker(expireDtFp);
  expireDtFp = null;
}

/** 태그 문자열 바인딩 */
const tagListStrWithCtgr = computed({
  get: () => model.value?.tag?.tagListStrWithCtgr ?? "",
  set: (v) => { if (model.value?.tag) model.value.tag.tagListStrWithCtgr = v; },
});

onBeforeUnmount(() => {
  cancelExpireCalc();
  destroyExpireDtPicker();
});

onMounted(() => {
  if (modalEl.value) {
    bsModal = new Modal(modalEl.value, { backdrop: "static", keyboard: false });
    modalEl.value.addEventListener("hidden.bs.modal", () => {
      resetSafeClose();
      modalStore.closeRegist();
    });
  }
});

watch(
  () => modalStore.registOpen,
  (isOpen) => {
    if (isOpen) {
      resetSafeClose();
      expireDt.value = model.value?.expireDt ?? "";
      // 열 때는 저장된 기한을 그대로 두고 산정 기준만 현재 원문으로 맞춘다.
      cancelExpireCalc();
      syncSettledDateText();
      void personalPrefixOptionsStore.fetchOptions(PREFIX_CONTENT_TYPE);
      void categoryMapStore.ensure();
      projectLoadError.value = "";
      void projectStore.fetchProjects().catch(() => {
        projectLoadError.value = t("prospective.project.list.failure");
      });
      bsModal?.show();
      void nextTick(() => attachExpireDtPicker());
    } else {
      cancelExpireCalc();
      destroyExpireDtPicker();
      bsModal?.hide();
    }
  }
);

/**
 * 미리보기 창에 표시할 말머리.
 * 저장된 말머리가 현재 선택과 같으면 그 표시값을, 아니면 활성 선택지·비활성 과거 선택에서 찾는다.
 */
function resolvePreviewPrefix(): { prefixName?: string; prefixTextClass?: string } {
  const current = model.value;
  if (!current?.prefixId) return {};
  if (current.prefix && current.prefix.id === current.prefixId) {
    return { prefixName: current.prefix.name, prefixTextClass: current.prefix.textClass ?? undefined };
  }
  const option = prefixOptions.value.find((p) => p.id === current.prefixId) ?? currentInactivePrefix.value;
  return { prefixName: option?.name, prefixTextClass: option?.textClass ?? undefined };
}

/**
 * Tagify JSON([{ value, data: { ctgr } }])을 미리보기 태그 목록으로 변환한다.
 * 파싱에 실패하면 빈 목록을 반환하고 경고 로그를 남긴다.
 */
function parsePreviewTags(tagListStr: string): KeepPreviewTag[] {
  if (!tagListStr) return [];
  try {
    const parsed: unknown = JSON.parse(tagListStr);
    if (!Array.isArray(parsed)) return [];
    return (parsed as Array<{ value?: unknown; data?: { ctgr?: unknown } }>)
      .map((item) => ({
        name: String(item?.value ?? "").trim(),
        ctgr: item?.data?.ctgr ? String(item.data.ctgr) : null,
      }))
      .filter((tag) => tag.name);
  } catch (error) {
    console.warn("[ProspectiveKeepRegistModal] preview tag parse failed", error);
    return [];
  }
}

/** 미리보기 창 메타 블록에 표시할 작성 중 Keep 메타. */
function resolvePreviewMeta(): KeepPreviewMeta {
  const current = model.value;
  const projectTitle = current?.projectId == null
    ? null
    : (() => {
      const project = projectStore.projects.find((candidate) => candidate.id === current.projectId);
      return project ? projectDisplayTitle(project) : null;
    })();
  return {
    dateText: current?.dateText ?? null,
    expireDt: expireDt.value || null,
    location: current?.location ?? null,
    projectTitle,
    tags: parsePreviewTags(tagListStrWithCtgr.value),
    important: current?.important === true,
  };
}

/** 작성 중 본문·메타를 Keep 목록 markdownContent와 같은 렌더로 새 창에 띄운다. 저장하지 않는다. */
async function preview(): Promise<void> {
  if (!model.value) return;
  flushExpireCalc();
  try {
    const result = await openJournalEntryPreview({
      contentType: "PROSPECTIVE_KEEP",
      title: model.value.title,
      sortOrder: model.value.sortOrder,
      content: model.value.content,
      ...resolvePreviewPrefix(),
      keepMeta: resolvePreviewMeta(),
    }, t("journal.entry.preview.failure"));
    if (result === "blocked") {
      void swalAlert(t("common.error.popup"));
    }
  } catch (error) {
    console.error("[ProspectiveKeepRegistModal] preview failed", error);
    void swalAlert(error instanceof Error ? error.message : t("journal.entry.preview.failure"));
  }
}

function close() {
  resetSafeClose();
  modalStore.closeRegist();
}

/** 등록/수정 처리 */
async function submit() {
  if (!model.value) return;
  // 디바운스 대기 중인 유효 기한 산정을 먼저 반영해 마지막 입력 기준 값으로 저장한다.
  flushExpireCalc();
  if (!model.value.title) {
    void swalAlert(t("prospective.keep.title.required"));
    return;
  }

  const confirmed = await swalConfirm(isModify.value ? t("common.confirm.mdf") : t("common.confirm.reg"));
  if (!confirmed) return;

  submitting.value = true;
  try {
    const formData = new FormData();
    if (isModify.value) formData.append("id", String(model.value.id));
    formData.append("title", model.value.title ?? "");
    if (model.value.sortOrder != null) formData.append("sortOrder", String(model.value.sortOrder));
    formData.append("prefixId", model.value.prefixId == null ? "" : String(model.value.prefixId));
    formData.append("projectId", model.value.projectId == null ? "" : String(model.value.projectId));
    formData.append("location", model.value.location ?? "");
    formData.append("dateText", model.value.dateText ?? "");
    // 확정된 절대 날짜만 보낸다. 서버는 일시 원문을 해석하지 않는다.
    formData.append("expireDt", expireDt.value);
    formData.append("content", model.value.content ?? "");
    formData.append("tag.tagListStr", tagListStrWithCtgr.value);

    const url = isModify.value
      ? `/api/prospective/keep/${model.value.id}`
      : "/api/prospective/keeps";
    const res = await axios.post(url, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    if (res.data?.rslt) {
      close();
      await swalAjaxResult({
        rslt: true,
        message: res.data?.message,
        successFallback: isModify.value ? t("common.result.modified") : t("common.result.registered"),
      });
      void refreshJournalDaysForRoute(journalStore, route);
      void journalStore.fetchKeeps();
      // 방금 쓴 태그가 다음 자동완성에 바로 나오도록 세션 캐시를 다시 조회한다.
      void categoryMapStore.refresh();
    } else {
      void swalAjaxResult({
        rslt: false,
        message: res.data?.message,
        failureFallback: t("common.result.failure"),
      });
    }
  } catch (e: unknown) {
    void swalRequestError(e);
  } finally {
    submitting.value = false;
  }
}
</script>
