<template>
  <!--begin::Keep 전수관리 화면 (전 범위 journal_todo projection; 사이드바는 활성 projection으로 별도 유지)-->
  <div class="d-flex flex-column flex-column-fluid p-5">
    <div class="card">
      <div class="card-header d-flex align-items-center justify-content-between flex-wrap gap-2">
        <h2 class="card-title fw-bold fs-2 m-0">
          <i class="bi bi-bookmark-star fs-2 me-2"></i> Keep
        </h2>
        <div class="d-flex align-items-center gap-2">
          <!--활성 말머리가 없으면 선택지가 비므로 select 자체를 숨긴다-->
          <select
            v-if="prefixOptions.length"
            v-model="filterPrefixId"
            class="form-select form-select-sm w-auto flex-shrink-0"
          >
            <option :value="null">{{ t("journal.todo.filter.all-prefixes") }}</option>
            <option v-for="prefix in prefixOptions" :key="prefix.id" :value="prefix.id">
              {{ prefix.name }}
            </option>
          </select>
          <input
            v-model="keyword"
            type="text"
            class="form-control form-control-sm"
            style="width: 220px;"
            placeholder="검색"
          />
          <button type="button" class="btn btn-sm btn-primary" @click.prevent="openAdd">
            <i class="bi bi-plus fs-3 pe-0"></i> 추가
          </button>
        </div>
      </div>
      <div class="card-body">
        <div v-if="error" class="text-danger fs-7 mb-3">{{ error }}</div>

        <!--begin::태그 클라우드
          할일 태그는 tag_content.ref_content_type = JOURNAL_TODO 로 저장되므로
          게시판 목록과 같은 /api/tags?contentType=... 계약으로 조회한다.
          같은 태그를 다시 누르면 필터가 해제된다. -->
        <div class="row align-items-center pb-3 mb-5 border-bottom border-gray-200 min-h-42px">
          <div class="keep-tag-cloud__label col-auto d-none d-md-flex text-center fs-6">
            <b>{{ t("common.tag") }} :</b>
          </div>
          <div class="col flex-grow-1">
            <span v-if="tagCloudLoading" class="text-muted fs-7">{{ t("common.loading") }}...</span>
            <span v-else-if="tagCloudError" class="text-danger fs-7">{{ tagCloudError }}</span>
            <span v-else-if="!tagCloud.length" class="text-muted fs-7">-</span>
            <span v-else class="d-flex flex-wrap align-items-center">
              <button
                v-for="tag in tagCloud"
                :key="'keep-tc-' + tag.id"
                type="button"
                class="btn btn-link py-2 me-3 px-0 cursor-pointer opacity-hover text-decoration-none d-inline-flex align-items-center"
                :class="{ 'keep-tag-cloud__btn--on': filterTagId === tag.id }"
                :title="t('journal.todo.tag-cloud.filter.tooltip')"
                @click="toggleTagFilter(tag.id)"
              >
                <span :class="[tag.tagClass, tag.textClass]" class="d-inline-flex align-items-center">
                  <span v-if="tag.ctgr" class="fs-7 text-noti">[{{ tag.ctgr }}]</span>
                  <span :class="`em_${tag.name}`">{{ tag.name }}</span>
                </span>
                <span class="fs-9 text-noti fw-normal tag-count">{{ tag.contentSize }}</span>
              </button>
            </span>
          </div>
        </div>
        <!--end::태그 클라우드-->

        <!-- 미해결 (OPEN·PENDING) -->
        <h4 class="fw-bold fs-5 text-gray-800 mb-3">
          미해결 <span class="text-muted fs-7 ms-1">{{ openItems.length }}</span>
        </h4>
        <div v-if="openItems.length === 0" class="text-muted fs-7 mb-8">미해결 항목이 없습니다.</div>
        <div v-else class="mb-8">
          <div
            v-for="item in openItems"
            :key="'keep-open-' + item.id"
            class="d-flex align-items-center py-2 border-bottom border-gray-200"
          >
            <input
              type="checkbox"
              class="form-check-input cursor-pointer me-3"
              :title="t('status.completed')"
              @change.prevent="completeTodo(item.id)"
            />
            <div
              class="flex-grow-1 text-truncate cursor-pointer"
              :title="item.title"
              @click.prevent="openTodoEdit(item.id)"
            >
              <span v-if="isPending(item)" class="badge badge-light-warning fs-9 me-1">{{ t("lifecycle.pending") }}</span>
              <span
                v-if="item.prefix"
                class="fw-bold me-1"
                :style="{ color: item.prefix.color || '#287D94' }"
              >[{{ item.prefix.name }}]</span>
              {{ item.title }}
              <span
                v-for="tag in item.tag?.list"
                :key="'keep-open-tag-' + item.id + '-' + tag.tagId"
                class="text-muted fs-8 ps-1"
              >
                <span v-if="tag.ctgr" class="text-noti">[{{ tag.ctgr }}]</span>#{{ tag.name }}
              </span>
              <span v-if="item.yy" class="text-muted fs-8 ms-2">{{ item.yy }}.{{ padMonth(item.mnth) }}</span>
            </div>
            <!--begin::컨텍스트 메뉴
              SSOT: 저널 스레드·엔트리 목록과 동일 Metronic data-kt-menu.
              라이프사이클은 전이 단축 항목이 아니라 OPEN·PENDING·RESOLVED 단계 라디오로 노출한다.
              비동기 목록 렌더 후 reinitMetronicAfterDom() 으로 재바인딩한다.
            -->
            <div class="d-flex justify-content-end">
              <button
                type="button"
                class="btn btn-sm btn-icon btn-active-light-primary py-2 px-2"
                data-kt-menu-trigger="click"
                data-kt-menu-placement="bottom-end"
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
                  <div class="menu-link flex-stack px-3" @click="openTodoEdit(item.id)">
                    {{ t("common.edit") }}
                    <i class="bi bi-pencil-square fs-8"></i>
                  </div>
                </div>
                <div class="separator my-2"></div>
                <!--begin::라이프사이클 서브메뉴-->
                <div class="menu-item px-3" data-kt-menu-trigger="hover" data-kt-menu-placement="left-start">
                  <a href="#" class="menu-link px-3" @click.prevent>
                    <span class="menu-title">{{ t("common.lifecycle") }}</span>
                    <span class="menu-arrow"></span>
                  </a>
                  <div class="menu-sub menu-sub-dropdown w-175px py-4">
                    <div v-for="lc in lifecycleOptions" :key="'keep-open-lc-' + item.id + '-' + lc.key" class="menu-item px-3">
                      <div class="menu-content px-3">
                        <label class="form-check form-check-custom form-check-solid cursor-pointer">
                          <input
                            class="form-check-input w-18px h-18px cursor-pointer"
                            type="radio"
                            :name="'keep-open-lc-' + item.id"
                            :value="lc.key"
                            :checked="lifecycleKeyOf(item) === lc.key"
                            @click="setLifecycle(item.id, lc.key)"
                          />
                          <span
                            class="form-check-label fs-7"
                            :class="lifecycleKeyOf(item) === lc.key ? lc.activeClass : 'text-muted'"
                          >{{ lc.label }}</span>
                        </label>
                      </div>
                    </div>
                  </div>
                </div>
                <!--end::라이프사이클 서브메뉴-->
                <div class="separator my-2"></div>
                <div class="menu-item px-3 my-1">
                  <div class="menu-link flex-stack px-3 text-danger" @click="deleteTodo(item.id)">
                    {{ t("common.delete") }}
                    <i class="bi bi-trash text-danger p-0 fs-8"></i>
                  </div>
                </div>
              </div>
            </div>
            <!--end::컨텍스트 메뉴-->
          </div>
        </div>

        <!-- 완료 (RESOLVED) -->
        <h4 class="fw-bold fs-5 text-gray-500 mb-3">
          완료 <span class="text-muted fs-7 ms-1">{{ resolvedItems.length }}</span>
        </h4>
        <div v-if="resolvedItems.length === 0" class="text-muted fs-7">완료된 항목이 없습니다.</div>
        <div v-else>
          <div
            v-for="item in resolvedItems"
            :key="'keep-done-' + item.id"
            class="d-flex align-items-center py-2 border-bottom border-gray-200 text-muted"
          >
            <i class="bi bi-check2 text-success fs-4 me-3"></i>
            <div
              class="flex-grow-1 text-truncate cursor-pointer text-decoration-line-through"
              :title="item.title"
              @click.prevent="openTodoEdit(item.id)"
            >
              <span
                v-if="item.prefix"
                class="fw-bold me-1"
                :style="{ color: item.prefix.color || '#287D94' }"
              >[{{ item.prefix.name }}]</span>
              {{ item.title }}
              <span
                v-for="tag in item.tag?.list"
                :key="'keep-done-tag-' + item.id + '-' + tag.tagId"
                class="text-muted fs-8 ps-1"
              >
                <span v-if="tag.ctgr" class="text-noti">[{{ tag.ctgr }}]</span>#{{ tag.name }}
              </span>
            </div>
            <!--begin::컨텍스트 메뉴
              SSOT: 저널 스레드·엔트리 목록과 동일 Metronic data-kt-menu.
              라이프사이클은 전이 단축 항목이 아니라 OPEN·PENDING·RESOLVED 단계 라디오로 노출한다.
              비동기 목록 렌더 후 reinitMetronicAfterDom() 으로 재바인딩한다.
            -->
            <div class="d-flex justify-content-end">
              <button
                type="button"
                class="btn btn-sm btn-icon btn-active-light-primary py-2 px-2"
                data-kt-menu-trigger="click"
                data-kt-menu-placement="bottom-end"
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
                  <div class="menu-link flex-stack px-3" @click="openTodoEdit(item.id)">
                    {{ t("common.edit") }}
                    <i class="bi bi-pencil-square fs-8"></i>
                  </div>
                </div>
                <div class="separator my-2"></div>
                <!--begin::라이프사이클 서브메뉴-->
                <div class="menu-item px-3" data-kt-menu-trigger="hover" data-kt-menu-placement="left-start">
                  <a href="#" class="menu-link px-3" @click.prevent>
                    <span class="menu-title">{{ t("common.lifecycle") }}</span>
                    <span class="menu-arrow"></span>
                  </a>
                  <div class="menu-sub menu-sub-dropdown w-175px py-4">
                    <div v-for="lc in lifecycleOptions" :key="'keep-done-lc-' + item.id + '-' + lc.key" class="menu-item px-3">
                      <div class="menu-content px-3">
                        <label class="form-check form-check-custom form-check-solid cursor-pointer">
                          <input
                            class="form-check-input w-18px h-18px cursor-pointer"
                            type="radio"
                            :name="'keep-done-lc-' + item.id"
                            :value="lc.key"
                            :checked="lifecycleKeyOf(item) === lc.key"
                            @click="setLifecycle(item.id, lc.key)"
                          />
                          <span
                            class="form-check-label fs-7"
                            :class="lifecycleKeyOf(item) === lc.key ? lc.activeClass : 'text-muted'"
                          >{{ lc.label }}</span>
                        </label>
                      </div>
                    </div>
                  </div>
                </div>
                <!--end::라이프사이클 서브메뉴-->
                <div class="separator my-2"></div>
                <div class="menu-item px-3 my-1">
                  <div class="menu-link flex-stack px-3 text-danger" @click="deleteTodo(item.id)">
                    {{ t("common.delete") }}
                    <i class="bi bi-trash text-danger p-0 fs-8"></i>
                  </div>
                </div>
              </div>
            </div>
            <!--end::컨텍스트 메뉴-->
          </div>
        </div>
      </div>
    </div>
  </div>
  <JournalTodoRegistModal />
  <!--end::Keep 전수관리 화면-->
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import axios from "axios";
import { swalAlert, swalConfirm, swalRequestError } from "@/shared/utils/swal";
import { reinitMetronicAfterDom } from "@/shared/utils/metronicReinit";
import { useLocaleStore } from "@/shared/i18n/stores/locale";
import { useJournalModalStore } from "@/features/journal/stores/journalModal";
import type { JournalPrefixDto } from "@/features/journal/stores/journal";
import { usePersonalPrefixOptionsStore } from "@/features/attachable/stores/personalPrefixOptions";
import JournalTodoRegistModal from "@/features/journal/todo/modals/JournalTodoRegistModal.vue";

/** Keep 전 범위 항목 — 백엔드 JournalTodoDto 직렬화(scope=all) 중 화면 표시·검색분. */
interface KeepItem {
  id: number;
  title?: string;
  content?: string;
  yy?: number;
  mnth?: number;
  /** 선택된 개인 말머리(표시용). 선택이 없으면 null. */
  prefix?: JournalPrefixDto | null;
  /** 부착된 태그(표시·필터용). `tag_content` 연결에서 조립된다. */
  tag?: { list?: Array<{ tagId?: number; name?: string; ctgr?: string | null }> } | null;
  /** 라이프사이클 현재값(백엔드 enrich). 부재 시 OPEN. */
  lifecycle?: { lifecycleKey?: string | null } | null;
}

const { t } = useLocaleStore();
const modalStore = useJournalModalStore();
const personalPrefixOptionsStore = usePersonalPrefixOptionsStore();

/** 할일 말머리 Scope content_type. 등록/수정 모달과 같은 목록을 쓴다. */
const PREFIX_CONTENT_TYPE = "JOURNAL_TODO";

/** 필터 선택지로 쓰는 활성 개인 말머리 목록. */
const prefixOptions = computed(() => personalPrefixOptionsStore.optionsFor(PREFIX_CONTENT_TYPE));

/** 전 범위 항목(미해결+완료) 원본 목록 */
const list = ref<KeepItem[]>([]);
/** 조회 실패 메시지 */
const error = ref<string | null>(null);
/** 제목·본문 검색어 (클라이언트 필터) */
const keyword = ref("");
/** 말머리 필터 (클라이언트 필터). null 이면 전체. */
const filterPrefixId = ref<number | null>(null);

/** 태그클라우드 항목 — 백엔드 TagDto 직렬화 중 화면 표시분. */
interface KeepTagCloudItem {
  id: number;
  name?: string;
  ctgr?: string | null;
  tagClass?: string;
  textClass?: string;
  contentSize?: number;
}
/** 태그클라우드 (JOURNAL_TODO 전체 태그) */
const tagCloud = ref<KeepTagCloudItem[]>([]);
const tagCloudLoading = ref(false);
const tagCloudError = ref("");
/** 태그 필터 (클라이언트 필터). null 이면 전체. */
const filterTagId = ref<number | null>(null);

/**
 * 말머리·태그·검색어로 거른 목록.
 * 말머리와 태그는 선택 일치(AND), 검색어는 제목·본문 부분일치다.
 * 전 범위 목록을 이미 받아 두므로 세 필터 모두 클라이언트에서 투영한다.
 */
const filtered = computed<KeepItem[]>(() => {
  const k = keyword.value.trim().toLowerCase();
  const prefixId = filterPrefixId.value;
  const tagId = filterTagId.value;
  return list.value.filter((it) => {
    if (prefixId != null && it.prefix?.id !== prefixId) return false;
    if (tagId != null && !(it.tag?.list ?? []).some((tag) => tag.tagId === tagId)) return false;
    if (!k) return true;
    return (it.title ?? "").toLowerCase().includes(k)
      || (it.content ?? "").toLowerCase().includes(k);
  });
});
/** 미해결 투영 = OPEN·PENDING (RESOLVED 제외) */
const openItems = computed<KeepItem[]>(() => filtered.value.filter((it) => it.lifecycle?.lifecycleKey !== "RESOLVED"));
/** 완료 투영 = RESOLVED */
const resolvedItems = computed<KeepItem[]>(() => filtered.value.filter((it) => it.lifecycle?.lifecycleKey === "RESOLVED"));

/** 현재 보류(PENDING) 상태 여부. */
function isPending(item: KeepItem): boolean {
  return item.lifecycle?.lifecycleKey === "PENDING";
}

/** 항목의 현재 라이프사이클 키. 저장 row 가 없으면 OPEN 이 기본값이다. */
function lifecycleKeyOf(item: KeepItem): string {
  return item.lifecycle?.lifecycleKey ?? "OPEN";
}

/**
 * 라이프사이클 단계 (OPEN/PENDING/RESOLVED).
 * 저널 엔트리·스레드 컨텍스트 메뉴와 같은 단계 집합·라벨을 쓴다.
 */
const lifecycleOptions = computed(() => [
  { key: "OPEN", label: t("journal.entry.lifecycle.open"), activeClass: "text-gray-800" },
  { key: "PENDING", label: t("lifecycle.pending"), activeClass: "text-gray-600" },
  { key: "RESOLVED", label: t("status.completed"), activeClass: "text-success" },
]);

/** 월 2자리 표기(발생지 표시용). 값이 없으면 빈 문자열. */
function padMonth(mnth?: number): string {
  return mnth != null ? String(mnth).padStart(2, "0") : "";
}

/**
 * 전 범위 목록 조회 (GET /api/journal/todos?scope=all).
 * 미해결·완료를 함께 받아 클라이언트에서 그룹·검색 투영한다.
 */
async function fetchList(): Promise<void> {
  error.value = null;
  try {
    const res = await axios.get("/api/journal/todos", { params: { scope: "all" } });
    if (!res.data?.rslt) {
      error.value = res.data?.message ?? t("journal.todo.list.load.failure");
      return;
    }
    list.value = (res.data?.rsltList ?? []) as KeepItem[];
    // 행 액션이 data-kt-menu 라, 비동기로 교체된 DOM 에는 핸들러가 붙어 있지 않다.
    void reinitMetronicAfterDom();
  } catch (e: unknown) {
    error.value = t("journal.todo.list.load.failure");
    console.error("[keep] fetchList failed", e);
  }
}

/**
 * 태그클라우드 조회 (GET /api/tags?contentType=JOURNAL_TODO).
 * 게시판 목록과 같은 계약이며, 실패는 빈 목록으로 가장하지 않고 별도 문구로 표시한다.
 */
async function fetchTagCloud(): Promise<void> {
  tagCloudLoading.value = true;
  tagCloudError.value = "";
  try {
    const res = await axios.get("/api/tags", { params: { contentType: "JOURNAL_TODO" } });
    tagCloud.value = Array.isArray(res.data?.rsltList) ? res.data.rsltList : [];
  } catch (e: unknown) {
    console.error("[keep] fetchTagCloud failed", e);
    tagCloud.value = [];
    tagCloudError.value = t("journal.todo.tag-cloud.load.failure");
  } finally {
    tagCloudLoading.value = false;
  }
}

/** 태그클라우드 클릭 — 같은 태그를 다시 누르면 해제한다. 목록은 클라이언트 투영이라 재조회하지 않는다. */
function toggleTagFilter(tagId: number): void {
  filterTagId.value = filterTagId.value === tagId ? null : tagId;
}

/**
 * 라이프사이클 전이 (PUT /api/lifecycles). 성공 시 목록을 재조회한다.
 * 아사이드 카드와 동일 계약이며, keep 은 완료(RESOLVED)도 화면에 유지한다.
 */
async function setLifecycle(id: number, lifecycleKey: string): Promise<void> {
  try {
    const res = await axios.put("/api/lifecycles", { id, contentType: "JOURNAL_TODO", lifecycleKey });
    if (res.data?.rslt === true) {
      void fetchList();
    } else {
      void swalAlert(res.data?.message ?? t("common.result.failure"));
    }
  } catch (e: unknown) {
    void swalRequestError(e);
  }
}
/** 완료 처리 (RESOLVED 전이) — 체크박스 전용 단축. 완료 그룹으로 이동한다. */
function completeTodo(id: number): void { void setLifecycle(id, "RESOLVED"); }

/**
 * 항목 삭제 (DELETE /api/journal/todo/:id). 확인 후 목록을 재조회한다.
 * 사이드바 TODO 카드와 같은 계약이다.
 */
async function deleteTodo(id: number): Promise<void> {
  if (!await swalConfirm(t("journal.todo.delete.confirm"))) return;
  try {
    const res = await axios.delete(`/api/journal/todo/${id}`);
    if (res.data?.rslt === true) {
      void fetchList();
    } else {
      void swalAlert(res.data?.message ?? t("common.result.failure"));
    }
  } catch (e: unknown) {
    void swalRequestError(e);
  }
}

/**
 * 신규 추가 모달 열기.
 * keep 에서 만든 항목은 저널 맥락이 없어, 발생지(yy/mnth)로 현재 년/월을 기본 전달한다.
 * (발생지 의미의 재정의는 별도 결정 대상이다.)
 */
function openAdd(): void {
  const now = new Date();
  modalStore.openTodoRegist({ yy: now.getFullYear(), mnth: now.getMonth() + 1 });
}

/**
 * 수정 모달 열기 — 상세(GET /api/journal/todo/:id)를 조회해 기존 데이터로 modify 모드로 연다.
 * 저장 성공 시 모달 close 를 watch 하여 목록을 재조회한다.
 */
async function openTodoEdit(id: number): Promise<void> {
  try {
    const res = await axios.get(`/api/journal/todo/${id}`);
    const dto = (res.data?.rsltObj ?? res.data?.obj) as {
      id?: number;
      yy?: number;
      mnth?: number;
      title?: string;
      sortOrder?: number;
      content?: string;
      location?: string;
      dateText?: string;
      prefixId?: number | null;
      prefix?: JournalPrefixDto | null;
      tag?: { tagListStrWithCtgr?: string };
    } | undefined;
    if (res.data?.rslt !== true || dto?.id == null) {
      void swalAlert(res.data?.message ?? t("common.result.failure"));
      return;
    }
    modalStore.openTodoRegist({
      id: dto.id,
      yy: dto.yy,
      mnth: dto.mnth,
      title: dto.title,
      sortOrder: dto.sortOrder,
      content: dto.content,
      prefixId: dto.prefixId ?? null,
      prefix: dto.prefix ?? null,
      location: dto.location ?? "",
      dateText: dto.dateText ?? "",
      tag: { tagListStrWithCtgr: dto.tag?.tagListStrWithCtgr ?? "" },
    });
  } catch (e: unknown) {
    void swalRequestError(e);
  }
}

// 등록/수정 모달이 닫히면(저장 포함) 전 범위 목록을 재조회한다.
// 모달 submit 은 journalStore.fetchTodos(활성)만 갱신하므로 keep 은 여기서 별도 재조회한다.
watch(
  () => modalStore.todoRegistOpen,
  (open, prev) => {
    if (prev && !open) {
      void fetchList();
      // 저장으로 태그가 추가·제거됐을 수 있어 클라우드도 다시 조회한다.
      void fetchTagCloud();
    }
  }
);

onMounted(() => {
  void personalPrefixOptionsStore.fetchOptions(PREFIX_CONTENT_TYPE);
  void fetchTagCloud();
  void fetchList();
});
</script>

<style scoped>
/* 저널 태그클라우드(JournalTagCloudHeader)와 같은 라벨 폭·카운트 간격을 쓴다. */
.keep-tag-cloud__label {
  width: 6.25rem;
  justify-content: center;
}

.tag-count {
  margin-left: 0.2em;
}

/* 저널 태그클라우드에 없는 축 -- Keep 태그는 목록 필터라 선택 상태를 표시해야 한다.
   글자 스타일(tagClass/textClass)은 건드리지 않고 배경 pill 로만 구분한다. */
.keep-tag-cloud__btn--on {
  background-color: var(--bs-light-primary);
  border-radius: 0.475rem;
  padding-left: 0.5rem !important;
  padding-right: 0.5rem !important;
}
</style>
