<template>
  <!--begin::aside Keep 카드 (레거시 JournalDayAsideKeepCardApp 이식 — 헤더·목록 단일 카드)-->
  <div class="card card-reset card-p-0 p-5 mt-8" style="width:280px; min-width:280px; max-width:280px;">
    <div id="prospective_keep_aside_header" class="card-header min-h-auto mb-5 px-0 border-0">
      <h3 class="card-title text-gray-900 fw-bold fs-3 mb-0">
        <i class="bi bi-list-task fs-2 me-1"></i> {{ t("prospective.keep.aside.title") }}
      </h3>
      <div class="card-toolbar">
        <button
          type="button"
          class="btn btn-sm btn-icon btn-primary"
          :title="t('prospective.keep.aside.add.tooltip')"
          @click.prevent="openKeepRegist"
        >
          <i class="bi bi-plus fs-2 pe-0" id="prospectiveKeepAsideRegistIcon"></i>
        </button>
      </div>
    </div>
    <div id="prospective_keep_list_div">
      <div v-if="store.keepError" class="journal-day d-flex-center text-danger fs-7">
        {{ store.keepError }}
      </div>
      <template v-else-if="store.keepList.length > 0">
        <div
          v-for="item in store.keepList"
          :key="'keep-' + item.id"
          class="row d-flex-align-center justify-content-between g-0 mb-1"
        >
          <div class="col-auto pe-2 d-flex align-items-center">
            <input
              type="checkbox"
              class="form-check-input cursor-pointer"
              :title="t('status.completed')"
              @change.prevent="completeKeep(item.id)"
            />
          </div>
          <div class="col text-truncate cursor-pointer" :title="item.title" @click.prevent="openKeepEdit(item.id)">
            <span v-if="isPending(item)" class="badge badge-light-warning fs-9 me-1">{{ t("lifecycle.pending") }}</span>
            {{ item.title }}
          </div>
          <div class="col-auto d-flex justify-content-end">
            <div class="dropdown">
              <button
                type="button"
                class="btn btn-sm btn-icon btn-active-light-primary py-2 px-2 cursor-pointer"
                data-bs-toggle="dropdown"
                :title="t('prospective.keep.more')"
              >
                <i class="bi bi-three-dots-vertical p-0"></i>
              </button>
              <ul class="dropdown-menu dropdown-menu-end shadow py-2" style="min-width: 0; width: max-content;">
                <li v-if="!isPending(item)">
                  <a class="dropdown-item cursor-pointer" @click.prevent="setPending(item.id)">{{ t("lifecycle.pending") }}</a>
                </li>
                <li v-else>
                  <a class="dropdown-item cursor-pointer" @click.prevent="releasePending(item.id)">{{ t("prospective.keep.pending.release") }}</a>
                </li>
                <li>
                  <a class="dropdown-item text-danger cursor-pointer" @click.prevent="deleteKeep(item.id)">{{ t("common.del") }}</a>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </template>
      <div v-else class="journal-day d-flex-center">
        {{ t("prospective.keep.empty") }}
      </div>
    </div>
  </div>
  <!--end::aside Keep 카드-->
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import axios from "axios";
import { swalAlert, swalConfirm, swalRequestError, swalAjaxResult } from "@/shared/utils/swal";
import { useLocaleStore } from "@/shared/i18n/stores/locale";
import { useJournalStore, type ProspectiveKeepItem, type JournalPrefixDto } from "@/features/journal/stores/journal";
import { useProspectiveKeepModalStore } from "@/features/prospective/keep/stores/prospectiveKeepModal";

const store = useJournalStore();
const modalStore = useProspectiveKeepModalStore();
const { t } = useLocaleStore();

/** Keep 등록 모달을 빈 폼으로 연다. */
function openKeepRegist(): void {
  void modalStore.openRegist({});
}

/**
 * Keep 수정 모달 열기.
 * <pre>
 *  상세(GET /api/prospective/keep/:id)를 조회해 기존 데이터로 채운 뒤 등록/수정 모달을
 *  modify 모드(id 포함)로 연다. 저장 성공 시 모달 submit 이 fetchKeeps 로 목록을 재조회한다.
 * </pre>
 * @param id 수정할 Keep 식별자
 */
async function openKeepEdit(id: number): Promise<void> {
  try {
    const res = await axios.get(`/api/prospective/keep/${id}`);
    const dto = (res.data?.rsltObj ?? res.data?.obj) as {
      id?: number;
      title?: string;
      sortOrder?: number;
      content?: string;
      location?: string;
      dateText?: string;
      expireDt?: string;
      prefixId?: number | null;
      prefix?: JournalPrefixDto | null;
      tag?: { tagListStrWithCtgr?: string };
    } | undefined;
    if (res.data?.rslt !== true || dto?.id == null) {
      void swalAlert(res.data?.message ?? t("common.result.failure"));
      return;
    }
    void modalStore.openRegist({
      id: dto.id,
      title: dto.title,
      sortOrder: dto.sortOrder,
      content: dto.content,
      prefixId: dto.prefixId ?? null,
      prefix: dto.prefix ?? null,
      location: dto.location ?? "",
      dateText: dto.dateText ?? "",
      expireDt: dto.expireDt ?? "",
      tag: { tagListStrWithCtgr: dto.tag?.tagListStrWithCtgr ?? "" },
    });
  } catch (e: unknown) {
    void swalRequestError(e);
  }
}

/** 현재 보류(PENDING) 상태인지 여부. */
function isPending(item: ProspectiveKeepItem): boolean {
  return item.lifecycle?.lifecycleKey === "PENDING";
}

/**
 * Keep 라이프사이클 전이 (PUT /api/lifecycles). 성공 시 목록을 재조회한다.
 * 활성 목록은 OPEN·PENDING만 비추므로 RESOLVED 전이는 카드에서 사라진다.
 * 리스트 캐시가 없어 cacheContext 없이 호출하며, 서버 updater 는 PROSPECTIVE_KEEP 미등록이라 no-op 이다.
 */
async function setLifecycle(id: number, lifecycleKey: "OPEN" | "PENDING" | "RESOLVED"): Promise<void> {
  try {
    const res = await axios.put("/api/lifecycles", {
      id,
      contentType: "PROSPECTIVE_KEEP",
      lifecycleKey,
    });
    if (res.data?.rslt === true) {
      void store.fetchKeeps();
    } else {
      void swalAlert(res.data?.message ?? t("common.result.failure"));
    }
  } catch (e: unknown) {
    void swalRequestError(e);
  }
}

/** 완료 처리 (RESOLVED 전이) — 체크 시 활성 목록에서 제외된다. */
function completeKeep(id: number): void {
  void setLifecycle(id, "RESOLVED");
}

/** 보류 처리 (PENDING 전이). */
function setPending(id: number): void {
  void setLifecycle(id, "PENDING");
}

/** 보류 해제 (OPEN 전이). */
function releasePending(id: number): void {
  void setLifecycle(id, "OPEN");
}

/** Keep 삭제 (하드 삭제 — 확인 후 목록 갱신). */
async function deleteKeep(id: number): Promise<void> {
  if (!await swalConfirm(t("prospective.keep.delete.confirm"))) return;
  try {
    const res = await axios.delete(`/api/prospective/keep/${id}`);
    const ok = res.data?.rslt === true;
    await swalAjaxResult({
      rslt: ok,
      message: res.data?.message,
      successFallback: t("common.result.deleted"),
      failureFallback: t("common.result.failure"),
    });
    if (ok) {
      void store.fetchKeeps();
    }
  } catch (e: unknown) {
    void swalRequestError(e);
  }
}

onMounted(() => {
  void store.fetchKeeps();
});
</script>
