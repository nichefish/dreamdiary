<template>
  <!--begin::작성 중 저널 엔트리/리플렉션 미리보기 팝업 (Prospective Keep 공용)-->
  <div class="p-5">
    <div class="card post">
      <div class="card-body">
        <div v-if="loading" class="d-flex justify-content-center py-10">
          <span class="spinner-border text-primary" role="status"></span>
        </div>
        <div v-else-if="errorText" class="text-danger py-10 text-center">
          {{ errorText }}
        </div>
        <template v-else-if="view">
          <div class="mb-0">
            <div class="d-flex flex-wrap align-items-baseline gap-3 mb-2">
              <span class="fs-3 fw-bold text-gray-900">
                <i :class="['bi me-2 text-primary', typeIconClass]"></i>{{ typeLabel }}
              </span>
              <span v-if="view.sortOrder != null" class="text-muted fs-6"># {{ view.sortOrder }}</span>
            </div>
            <div v-if="view.prefixName || view.title" class="d-flex align-items-center flex-wrap fs-5 fw-semibold text-gray-800 mb-2">
              <i v-if="isKeep && view.keepMeta?.important" class="bi bi-star-fill text-warning me-2" :title="t('state.important')"></i>
              <span
                v-if="view.prefixName"
                class="badge me-2 fs-8"
                :class="view.prefixTextClass" :style="{ borderColor: 'currentColor' }"
              >{{ view.prefixName }}</span>
              <span v-if="view.title">{{ view.title }}</span>
            </div>
            <!--begin::Keep 메타 (PROSPECTIVE_KEEP 전용, 값이 있는 항목만)-->
            <div v-if="keepMetaRows.length || keepTags.length" class="d-flex flex-column gap-1 fs-7 text-gray-700 mt-3">
              <div v-for="row in keepMetaRows" :key="row.key">
                <span class="fw-bold text-gray-600 me-2">{{ row.label }}</span>
                <span>{{ row.value }}</span>
                <span v-if="row.hint" class="fs-8 ms-2" :class="row.hintClass">{{ row.hint }}</span>
              </div>
              <div v-if="keepTags.length" class="d-flex flex-wrap align-items-center">
                <span class="fw-bold text-gray-600 me-2">{{ t("common.tag") }}</span>
                <TagChip
                  v-for="(tag, idx) in keepTags"
                  :key="'keep-preview-tag-' + idx"
                  class="fs-8"
                  :name="tag.name"
                  :ctgr="tag.ctgr"
                />
              </div>
            </div>
            <!--end::Keep 메타-->
          </div>

          <div class="separator separator-dashed border-gray-300 my-8"></div>

          <div class="px-2 py-1 pb-4 journal-markdown-preview">
            <div :class="itemClass">
              <div v-if="!isKeep" class="col-1 py-3 d-none d-md-flex border-2 border-gray-300 border-end ps-5 me-4 h-75" style="width:85px;">
                &nbsp;
              </div>
              <div class="col">
                <div :class="contentClass">
                  <div
                    v-if="view.markdownContent"
                    class="journal-content p-2"
                    v-html="view.markdownContent"
                  ></div>
                  <div v-else class="text-muted fs-7 fst-italic">
                    {{ t("journal.entry.view.empty") }}
                  </div>
                </div>
              </div>
              <div v-if="!isKeep" class="col-1 py-3 d-none d-md-flex w-50px ps-2">&nbsp;</div>
            </div>
          </div>
        </template>
      </div>
      <div class="card-footer">
        <div class="d-flex justify-content-end">
          <button type="button" class="btn btn-sm btn-light" @click="closePopup">
            {{ t("common.close") }}
          </button>
        </div>
      </div>
    </div>
  </div>
  <!--end::작성 중 저널 엔트리/리플렉션 미리보기 팝업-->
</template>

<script setup lang="ts">
/**
 * JournalEntryPreviewPage.vue
 * 등록/수정 모달의 작성 중 본문을 목록과 같은 journal-content로 새 창에 표시한다.
 * Prospective Keep 등록/수정 모달의 작성 중 본문도 같은 창으로 표시한다.
 * Keep 은 저널 목록의 좌우 여백 칸 없이 본문을 표시하고, 제목 아래에 일시·유효 기한·위치·Project·태그 메타를 둔다.
 * 페이로드는 opener가 localStorage에 쓴 뒤 storage 이벤트로 전달한다.
 */
import { computed, onMounted, onUnmounted, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { useLocaleStore } from "@/shared/i18n/stores/locale";
import TagChip from "@/shared/ui/tag/TagChip.vue";
import { daysUntilExpire } from "@/features/prospective/keep/utils/expireDt";
import {
  clearPreviewView,
  isPreviewId,
  previewStorageKey,
  readPreviewView,
  type JournalEntryPreviewView,
} from "@/features/journal/utils/journalEntryPreview";

const WAIT_MS = 20000;
const POLL_MS = 200;

const { t } = useLocaleStore();
const route = useRoute();
const loading = ref(true);
const view = ref<JournalEntryPreviewView | null>(null);
const timedOut = ref(false);

let pollTimer: number | null = null;
let timeoutTimer: number | null = null;
let waitingPreviewId: string | null = null;

const previewId = computed(() => {
  const raw = route.query.previewId;
  const value = Array.isArray(raw) ? raw[0] : raw;
  return isPreviewId(value) ? value : "";
});

const errorText = computed(() => {
  if (!previewId.value) return t("journal.entry.preview.missing");
  if (view.value?.error) return view.value.error;
  if (timedOut.value) return t("journal.entry.preview.missing");
  return "";
});

/** Keep 미리보기 여부. 좌우 여백 칸 생략과 메타 블록 표시를 가른다. */
const isKeep = computed(() => view.value?.contentType === "PROSPECTIVE_KEEP");

/** Keep 메타 한 줄. hint 는 유효 기한의 남은/지난 일수다. */
interface KeepMetaRow {
  key: string;
  label: string;
  value: string;
  hint?: string;
  hintClass?: string;
}

/** 유효 기한 옆 상대 일수 문구. 등록 모달 안내와 같은 i18n 키를 쓴다. */
function expireRelativeHint(expireDt: string): string {
  const days = daysUntilExpire(expireDt);
  if (Number.isNaN(days)) return "";
  if (days === 0) return t("prospective.keep.expire-dt.relative.today");
  return days > 0
    ? t("prospective.keep.expire-dt.relative.left").replace("{0}", String(days))
    : t("prospective.keep.expire-dt.relative.past").replace("{0}", String(-days));
}

/** 값이 있는 Keep 메타만 일시·유효 기한·위치·Project 순으로 나열한다. */
const keepMetaRows = computed<KeepMetaRow[]>(() => {
  const meta = isKeep.value ? view.value?.keepMeta : null;
  if (!meta) return [];
  const rows: KeepMetaRow[] = [];
  if (meta.dateText?.trim()) {
    rows.push({ key: "dateText", label: t("prospective.keep.field.date-text"), value: meta.dateText });
  }
  if (meta.expireDt) {
    const overdue = daysUntilExpire(meta.expireDt) < 0;
    rows.push({
      key: "expireDt",
      label: t("prospective.keep.field.expire-dt"),
      value: meta.expireDt,
      hint: expireRelativeHint(meta.expireDt),
      hintClass: overdue ? "text-warning" : "text-gray-500",
    });
  }
  if (meta.location?.trim()) {
    rows.push({ key: "location", label: t("prospective.keep.field.location"), value: meta.location });
  }
  if (meta.projectTitle) {
    rows.push({ key: "project", label: t("prospective.project.field"), value: meta.projectTitle });
  }
  return rows;
});

/** Keep 메타 태그 목록. */
const keepTags = computed(() => (isKeep.value ? view.value?.keepMeta?.tags ?? [] : []));

const typeLabel = computed(() => {
  const type = view.value?.contentType;
  if (type === "JOURNAL_DREAM") return t("common.dream");
  if (type === "JOURNAL_NOTE") return t("journal.note");
  if (type === "JOURNAL_REFLECTION") return t("journal.reflection.label");
  if (type === "PROSPECTIVE_KEEP") return t("prospective.keep.label");
  return t("common.diary");
});

const typeIconClass = computed(() => {
  const type = view.value?.contentType;
  if (type === "JOURNAL_DREAM") return "bi-moon-stars";
  if (type === "JOURNAL_REFLECTION") return "bi-chat-quote";
  if (type === "JOURNAL_NOTE") return "bi-journal";
  if (type === "PROSPECTIVE_KEEP") return "bi-bookmark-check";
  return "bi-journal-text";
});

const itemClass = computed(() => {
  const type = view.value?.contentType;
  if (type === "JOURNAL_DREAM") return "journal-dream-item";
  if (type === "JOURNAL_NOTE" || type === "PROSPECTIVE_KEEP") return "journal-note-item";
  if (type === "JOURNAL_REFLECTION") return "journal-reflection-embed";
  return "journal-diary-item";
});

const contentClass = computed(() => {
  const type = view.value?.contentType;
  if (type === "JOURNAL_DREAM") return "journal-dream-content";
  if (type === "JOURNAL_NOTE" || type === "PROSPECTIVE_KEEP") return "journal-note-content";
  if (type === "JOURNAL_REFLECTION") return "journal-reflection-content";
  return "journal-diary-content";
});

function stopWaiting(): void {
  if (pollTimer != null) {
    window.clearInterval(pollTimer);
    pollTimer = null;
  }
  if (timeoutTimer != null) {
    window.clearTimeout(timeoutTimer);
    timeoutTimer = null;
  }
  waitingPreviewId = null;
}

function applyView(previewKey: string, next: JournalEntryPreviewView): void {
  stopWaiting();
  view.value = next;
  loading.value = false;
  timedOut.value = false;
  clearPreviewView(previewKey);
}

function tryRead(previewKey: string): boolean {
  const stored = readPreviewView(previewKey);
  if (!stored) return false;
  applyView(previewKey, stored);
  return true;
}

function startWaiting(previewKey: string): void {
  stopWaiting();
  waitingPreviewId = previewKey;
  loading.value = true;
  view.value = null;
  timedOut.value = false;
  if (tryRead(previewKey)) return;

  pollTimer = window.setInterval(() => {
    if (waitingPreviewId) tryRead(waitingPreviewId);
  }, POLL_MS);
  timeoutTimer = window.setTimeout(() => {
    if (!view.value) {
      loading.value = false;
      timedOut.value = true;
      console.warn("[JournalEntryPreviewPage] preview payload wait timed out", { previewId: previewKey });
    }
    stopWaiting();
  }, WAIT_MS);
}

function onStorage(event: StorageEvent): void {
  if (!waitingPreviewId) return;
  if (event.key && event.key !== previewStorageKey(waitingPreviewId)) return;
  tryRead(waitingPreviewId);
}

function closePopup(): void {
  window.close();
}

watch(previewId, (id) => {
  if (!id) {
    stopWaiting();
    loading.value = false;
    view.value = null;
    return;
  }
  startWaiting(id);
}, { immediate: true });

onMounted(() => {
  window.addEventListener("storage", onStorage);
});

onUnmounted(() => {
  window.removeEventListener("storage", onStorage);
  stopWaiting();
});
</script>
