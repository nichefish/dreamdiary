<template>
  <!-- 말머리 관리 모달이 위에 열려 있으면(suspended) 이 모달은 inert로 입력·Esc·포커스를 받지 않는다. -->
  <div
    ref="rootEl"
    class="modal fade show d-block"
    tabindex="-1"
    role="dialog"
    aria-modal="true"
    aria-labelledby="prospective-project-modal-title"
    :inert="suspended || undefined"
    :aria-hidden="suspended ? 'true' : undefined"
    @keydown.esc="onEscape"
  >
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <div>
            <h5 id="prospective-project-modal-title" class="modal-title">{{ t("prospective.project.manage.title") }}</h5>
            <p class="text-muted fs-8 mb-0 mt-1">{{ t("prospective.project.manage.description") }}</p>
          </div>
          <button type="button" class="btn-close" :disabled="store.saving" @click="emit('close')"></button>
        </div>
        <div class="modal-body">
          <div v-if="errorMessage" class="alert alert-danger py-3">{{ errorMessage }}</div>
          <div class="d-flex justify-content-end gap-2 mb-4">
            <button type="button" class="btn btn-sm btn-light" :disabled="store.saving" @click="requestPrefixManage">
              <i class="bi bi-bookmark me-1"></i>{{ t("prospective.project.prefix.manage.button") }}
            </button>
            <button type="button" class="btn btn-sm btn-primary" :disabled="store.loading || store.saving" @click="beginNew">
              <i class="bi bi-plus-lg me-1"></i>{{ t("prospective.project.add") }}
            </button>
          </div>

          <ProspectiveProjectEditorForm
            v-if="editing && form.id == null"
            v-model:title="form.title"
            v-model:description="form.description"
            v-model:prefix-id="form.prefixId"
            class="mb-5"
            :saving="store.saving"
            :prefix-options="prefixOptions"
            :inactive-prefix="inactivePrefix"
            :prefix-load-failed="prefixLoadFailed"
            @submit="submit"
            @cancel="cancelEdit"
          />

          <div v-if="store.loading" class="text-center text-muted py-8">
            <span class="spinner-border spinner-border-sm me-2"></span>{{ t("common.loading") }}
          </div>
          <div v-else-if="!store.projects.length" class="text-muted text-center py-8">{{ t("prospective.project.empty") }}</div>
          <template v-else>
            <template v-for="(project, index) in store.projects" :key="project.id">
              <div
                class="d-flex align-items-center gap-3 border-bottom py-3 px-2 rounded"
                :class="{ 'bg-light-primary': editing && form.id === project.id }"
              >
                <div class="flex-grow-1 overflow-hidden">
                  <div class="fw-bold text-truncate">
                    <ProspectiveProjectTitle :project="project" />
                    <span class="badge fs-9 ms-1" :class="lifecycleBadgeClass(store.lifecycleKeyOf(project))">
                      {{ lifecycleLabel(store.lifecycleKeyOf(project)) }}
                    </span>
                  </div>
                  <div v-if="project.description" class="text-muted fs-8 text-truncate">{{ project.description }}</div>
                  <div class="text-muted fs-9">{{ t("prospective.project.keep-count").replace("{0}", String(keepCounts[project.id] ?? 0)) }}</div>
                </div>
                <button type="button" class="btn btn-sm btn-icon btn-light" :title="t('prospective.project.move-up')" :disabled="index === 0 || store.saving" @click="move(index, -1)"><i class="bi bi-arrow-up"></i></button>
                <button type="button" class="btn btn-sm btn-icon btn-light" :title="t('prospective.project.move-down')" :disabled="index === store.projects.length - 1 || store.saving" @click="move(index, 1)"><i class="bi bi-arrow-down"></i></button>
                <!-- Project 행 동작은 Keep·저널 목록과 같은 Metronic 컨텍스트 메뉴를 사용한다. -->
                <div class="d-flex justify-content-end">
                  <button
                    type="button"
                    class="btn btn-sm btn-icon btn-active-light-primary py-2 px-2"
                    data-kt-menu-trigger="click"
                    data-kt-menu-placement="bottom-end"
                    :disabled="store.saving"
                    :title="t('common.menu')"
                  >
                    <i class="bi bi-three-dots-vertical p-0"></i>
                  </button>
                  <div
                    class="menu menu-sub menu-sub-dropdown menu-column menu-rounded menu-gray-800 menu-state-bg-light-primary fw-semibold w-200px py-3"
                    data-kt-menu="true"
                    @click.stop
                  >
                    <div class="menu-item px-3 my-1">
                      <div class="menu-link flex-stack px-3" @click="beginEdit(project)">
                        {{ t("common.edit") }}
                        <i class="bi bi-pencil-square fs-8"></i>
                      </div>
                    </div>
                    <div class="separator my-2"></div>
                    <div class="menu-item px-3" data-kt-menu-trigger="hover" data-kt-menu-placement="left-start">
                      <a href="#" class="menu-link px-3" @click.prevent>
                        <span class="menu-title">{{ t("common.lifecycle") }}</span>
                        <span class="menu-arrow"></span>
                      </a>
                      <div class="menu-sub menu-sub-dropdown w-175px py-4">
                        <template v-for="option in lifecycleOptions" :key="'project-lc-' + project.id + '-' + option.key">
                          <div v-if="option.key === 'RESOLVED'" class="separator my-2"></div>
                          <div class="menu-item px-3">
                            <div class="menu-content px-3">
                              <label class="form-check form-check-custom form-check-solid cursor-pointer">
                                <input
                                  class="form-check-input w-18px h-18px cursor-pointer"
                                  type="radio"
                                  :name="'project-lc-' + project.id"
                                  :value="option.key"
                                  :checked="store.lifecycleKeyOf(project) === option.key"
                                  @click="changeLifecycle(project, option.key)"
                                />
                                <span
                                  class="form-check-label fs-7"
                                  :class="store.lifecycleKeyOf(project) === option.key ? option.activeClass : 'text-muted'"
                                >{{ option.label }}</span>
                              </label>
                            </div>
                          </div>
                        </template>
                      </div>
                    </div>
                    <div class="separator my-2"></div>
                    <div class="menu-item px-3 my-1">
                      <div class="menu-link flex-stack px-3 text-danger" @click="remove(project)">
                        {{ t("common.delete") }}
                        <i class="bi bi-trash text-danger p-0 fs-8"></i>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <ProspectiveProjectEditorForm
                v-if="editing && form.id === project.id"
                v-model:title="form.title"
                v-model:description="form.description"
                v-model:prefix-id="form.prefixId"
                class="mt-2 mb-4 ms-4"
                :saving="store.saving"
                :prefix-options="prefixOptions"
                :inactive-prefix="inactivePrefix"
                :prefix-load-failed="prefixLoadFailed"
                @submit="submit"
                @cancel="cancelEdit"
              />
            </template>
          </template>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-sm btn-light" :disabled="store.saving" @click="emit('close')">{{ t("common.close") }}</button>
        </div>
      </div>
    </div>
  </div>
  <div class="modal-backdrop fade show"></div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from "vue";
import { useLocaleStore } from "@/shared/i18n/stores/locale";
import { swalAlert, swalConfirm, swalRequestError } from "@/shared/utils/swal";
import {
  PROJECT_PREFIX_CONTENT_TYPE,
  useProspectiveProjectStore,
  type ProspectiveProject,
} from "@/features/prospective/project/stores/prospectiveProject";
import { reinitMetronicAfterDom } from "@/shared/utils/metronicReinit";
import { MenuComponent } from "@metronic/assets/ts/components";
import ProspectiveProjectEditorForm from "@/features/prospective/project/components/ProspectiveProjectEditorForm.vue";
import ProspectiveProjectTitle from "@/features/prospective/project/components/ProspectiveProjectTitle.vue";
import { projectDisplayTitle } from "@/features/prospective/project/utils/projectDisplayTitle";
import { usePersonalPrefixOptionsStore } from "@/features/attachable/stores/personalPrefixOptions";
import { resolveJournalPrefixField } from "@/features/journal/utils/journalPrefixField";

const props = withDefaults(defineProps<{
  keepCounts: Record<number, number>;
  /** 말머리 관리 모달이 위에 열려 있는 동안 true. Esc·입력을 받지 않는다. */
  suspended?: boolean;
}>(), { suspended: false });
/** managePrefixes: 말머리 관리 모달을 이 모달 위 별도 레이어로 열어 달라는 요청 (KeepPage가 형제로 렌더링). */
const emit = defineEmits<{ close: []; changed: []; managePrefixes: [] }>();
const store = useProspectiveProjectStore();
const personalPrefixOptionsStore = usePersonalPrefixOptionsStore();
const { t } = useLocaleStore();
const rootEl = ref<HTMLElement | null>(null);
const editing = ref(false);
const errorMessage = ref("");
const form = reactive<{ id?: number; title: string; description: string; prefixId: number | null }>({
  title: "",
  description: "",
  prefixId: null,
});
/** Project 말머리 활성 선택지. 관리 모달 저장 뒤 KeepPage가 강제 재조회하면 즉시 반영된다. */
const prefixOptions = computed(() => personalPrefixOptionsStore.optionsFor(PROJECT_PREFIX_CONTENT_TYPE));
const prefixLoadFailed = computed(() => personalPrefixOptionsStore.hasFailed(PROJECT_PREFIX_CONTENT_TYPE));
/** 수정 중 Project의 기존 비활성 말머리. 활성 목록에 없으면 선택 유지용 옵션으로만 노출한다. */
const inactivePrefix = computed(() => {
  const current = form.id == null ? null : store.projects.find((project) => project.id === form.id)?.prefix ?? null;
  const resolved = resolveJournalPrefixField(prefixOptions.value, current).inactivePrefix;
  return resolved?.id == null ? null : { id: resolved.id, name: resolved.name ?? "" };
});
const lifecycleOptions = computed(() => [
  { key: "OPEN", label: t("journal.entry.lifecycle.open"), activeClass: "text-gray-800" },
  { key: "PENDING", label: t("lifecycle.pending"), activeClass: "text-warning" },
  { key: "RESOLVED", label: t("status.completed"), activeClass: "text-success" },
  { key: "DROPPED", label: t("lifecycle.dropped"), activeClass: "text-muted" },
] as const);
/** 행 추가·삭제·재정렬 뒤 동적으로 바뀐 컨텍스트 메뉴를 다시 바인딩한다. */
const projectRowSignature = computed(() => store.projects.map((project) => project.id).join(","));

function beginNew(): void {
  closeProjectMenus();
  Object.assign(form, { id: undefined, title: "", description: "", prefixId: null });
  editing.value = true;
}

function beginEdit(project: ProspectiveProject): void {
  closeProjectMenus();
  Object.assign(form, {
    id: project.id,
    title: project.title,
    description: project.description ?? "",
    prefixId: project.prefixId ?? project.prefix?.id ?? null,
  });
  editing.value = true;
}

function cancelEdit(): void { editing.value = false; }

async function submit(): Promise<void> {
  if (!form.title) {
    void swalAlert(t("prospective.project.title.required"));
    return;
  }
  try {
    await store.saveProject({ id: form.id, title: form.title, description: form.description, prefixId: form.prefixId });
    editing.value = false;
    errorMessage.value = "";
    emit("changed");
  } catch (error: unknown) {
    errorMessage.value = t("prospective.project.save.failure");
    void swalRequestError(error);
  }
}

async function remove(project: ProspectiveProject): Promise<void> {
  closeProjectMenus();
  const count = props.keepCounts[project.id] ?? 0;
  const message = t("prospective.project.delete.confirm")
    .replace("{0}", projectDisplayTitle(project))
    .replace("{1}", String(count));
  if (!await swalConfirm(message)) return;
  try {
    await store.deleteProject(project.id);
    errorMessage.value = "";
    emit("changed");
  } catch (error: unknown) {
    errorMessage.value = t("prospective.project.delete.failure");
    void swalRequestError(error);
  }
}

/** Project만 전이하며 소속 Keep의 lifecycle과 projectId는 유지한다. */
async function changeLifecycle(
  project: ProspectiveProject,
  lifecycleKey: "OPEN" | "PENDING" | "RESOLVED" | "DROPPED",
): Promise<void> {
  closeProjectMenus();
  if (lifecycleKey === store.lifecycleKeyOf(project)) return;
  try {
    await store.setProjectLifecycle(project.id, lifecycleKey);
    errorMessage.value = "";
    emit("changed");
  } catch (error: unknown) {
    errorMessage.value = t("prospective.project.lifecycle.failure");
    void swalRequestError(error);
  }
}

/** 말머리 관리는 이 모달 안에 중첩하지 않고 KeepPage가 위 레이어에 형제로 연다. */
function requestPrefixManage(): void {
  closeProjectMenus();
  emit("managePrefixes");
}

/** 위에 말머리 관리 모달이 열려 있으면 Esc를 무시해 두 모달이 함께 닫히지 않게 한다. */
function onEscape(): void {
  if (props.suspended) return;
  emit("close");
}

/** 말머리 관리 모달이 닫힌 뒤 포커스를 이 모달로 되돌린다. */
function focus(): void {
  rootEl.value?.focus();
}

defineExpose({ focus });

/** 메뉴 액션이 인라인 편집·확인창 뒤에 남지 않도록 열린 Metronic dropdown을 닫는다. */
function closeProjectMenus(): void {
  MenuComponent.hideDropdowns(undefined);
}

function lifecycleLabel(lifecycleKey: string): string {
  return lifecycleOptions.value.find((option) => option.key === lifecycleKey)?.label ?? lifecycleKey;
}

function lifecycleBadgeClass(lifecycleKey: string): string {
  if (lifecycleKey === "PENDING") return "badge-light-warning";
  if (lifecycleKey === "RESOLVED") return "badge-light-success";
  if (lifecycleKey === "DROPPED") return "badge-light";
  return "badge-light-primary";
}

async function move(index: number, delta: number): Promise<void> {
  const next = index + delta;
  if (next < 0 || next >= store.projects.length) return;
  const orderedIds = store.projects.map((project) => project.id);
  [orderedIds[index], orderedIds[next]] = [orderedIds[next], orderedIds[index]];
  try {
    await store.reorderProjects(orderedIds);
    errorMessage.value = "";
    emit("changed");
  } catch (error: unknown) {
    errorMessage.value = t("prospective.project.reorder.failure");
    void swalRequestError(error);
  }
}

watch(projectRowSignature, () => { void reinitMetronicAfterDom(); }, { flush: "post" });
// 목록 조회 중 v-if가 행 DOM을 제거하므로, 같은 ID 구성이 돌아와도 loading 종료 뒤 메뉴를 다시 바인딩한다.
watch(
  () => store.loading,
  (loading, wasLoading) => {
    if (wasLoading && !loading) void reinitMetronicAfterDom();
  },
  { flush: "post" },
);
onMounted(() => {
  void reinitMetronicAfterDom();
  void personalPrefixOptionsStore.fetchOptions(PROJECT_PREFIX_CONTENT_TYPE);
  rootEl.value?.focus();
});
</script>
