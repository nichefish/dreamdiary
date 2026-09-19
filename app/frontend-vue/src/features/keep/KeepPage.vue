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
          <button
            type="button"
            class="btn btn-sm"
            :class="filterOverdue ? 'btn-warning' : 'btn-light'"
            :title="t('journal.todo.filter.overdue.tooltip')"
            @click.prevent="filterOverdue = !filterOverdue"
          >
            <i class="bi bi-hourglass-bottom fs-6 pe-1"></i>{{ t("journal.todo.filter.overdue") }}
          </button>
          <input
            v-model="keyword"
            type="text"
            class="form-control form-control-sm"
            style="width: 220px;"
            :placeholder="t('common.search')"
          />
          <button type="button" class="btn btn-sm btn-primary" @click.prevent="openAdd">
            <i class="bi bi-plus fs-3 pe-0"></i> {{ t("common.add") }}
          </button>
        </div>
      </div>
      <div class="card-body">
        <div v-if="error" class="text-danger fs-7 mb-3">{{ error }}</div>

        <!--begin::태그 클라우드
          할일 태그는 tag_content.ref_content_type = JOURNAL_TODO 로 저장되므로
          게시판 목록과 같은 /api/tags?contentType=... 계약으로 조회한다.
          태그 클릭은 저널 태그클라우드와 같은 컨텍스트 메뉴를 연다. -->
        <div class="pb-3 mb-5 border-bottom border-gray-200">
          <TagCloud
            :tags="tagCloud"
            :label="t('common.tag')"
            :loading="tagCloudLoading"
            :error-message="tagCloudError || null"
            :tag-title="t('journal.tag-cloud.menu.tooltip')"
            :selected-tag-id="filterTagId"
            key-prefix="keep-tc-"
            @tag-click="openTagContextMenu"
          />
        </div>
        <!--end::태그 클라우드-->

        <!-- 진행 (OPEN·PENDING) -->
        <h4 class="fw-bold fs-5 text-gray-800 mb-3">
          {{ t("journal.todo.group.active") }} <span class="text-muted fs-7 ms-1">{{ openItems.length }}</span>
        </h4>
        <div v-if="openItems.length === 0" class="text-muted fs-7 mb-8">{{ t("journal.todo.group.active.empty") }}</div>
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
              <span v-if="hasState(item, 'DEMANDED')" class="badge badge-light-danger fs-9 me-1">{{ t("state.demanded") }}</span>
              <span
                v-if="item.prefix"
                class="fw-bold me-1"
                :class="item.prefix.textClass" :style="{ color: '#287D94' }"
              >[{{ item.prefix.name }}]</span>
              {{ item.title }}
              <TagChip
                v-for="tag in item.tag?.list"
                :key="'keep-open-tag-' + item.id + '-' + tag.tagId"
                class="fs-8"
                :name="tag.name ?? ''"
                :ctgr="tag.ctgr"
                clickable
                @click="openRowTagMenu($event, tag)"
              />
              <span v-if="item.expireDt" class="fs-8 ms-2" :class="expireLabelClass(item)">{{ expireLabel(item) }}</span>
              <span v-if="registeredMonth(item)" class="text-muted fs-8 ms-2">{{ registeredMonth(item) }}</span>
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
                    <template v-for="lc in lifecycleOptions" :key="'keep-open-lc-' + item.id + '-' + lc.key">
                      <!--진행 2개와 종결 3개 사이 구분선-->
                      <div v-if="lc.key === 'RESOLVED'" class="separator my-2"></div>
                      <div class="menu-item px-3">
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
                    </template>
                  </div>
                </div>
                <!--end::라이프사이클 서브메뉴-->
                <div class="separator my-2"></div>
                <!--begin::상태 서브메뉴 (다중 속성 -- lifecycle 과 별개 축)-->
                <div class="menu-item px-3" data-kt-menu-trigger="hover" data-kt-menu-placement="left-start">
                  <a href="#" class="menu-link px-3" @click.prevent>
                    <span class="menu-title">{{ t("common.status") }}</span>
                    <span class="menu-arrow"></span>
                  </a>
                  <div class="menu-sub menu-sub-dropdown w-175px py-4">
                    <div v-for="st in stateOptions" :key="'keep-open-st-' + item.id + '-' + st.key" class="menu-item px-3">
                      <div class="menu-content px-3">
                        <label class="form-check form-switch form-check-custom form-check-solid cursor-pointer">
                          <input
                            class="form-check-input w-30px h-20px cursor-pointer"
                            type="checkbox"
                            :checked="hasState(item, st.key)"
                            @click="toggleState(item.id, st.key)"
                          />
                          <span
                            class="form-check-label fs-7"
                            :class="hasState(item, st.key) ? st.activeClass : 'text-muted'"
                          >{{ st.label }}</span>
                        </label>
                      </div>
                    </div>
                  </div>
                </div>
                <!--end::상태 서브메뉴-->
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

        <!--begin::종결 (RESOLVED·FAILED·EXPIRED·DROPPED)
          「기한 지남」 필터는 미종결만 남기므로 종결 섹션이 항상 비게 된다.
          빈 섹션과 「종결된 항목이 없습니다」를 띄우는 대신 섹션째 감춘다. -->
        <template v-if="!filterOverdue">
        <h4 class="fw-bold fs-5 text-gray-500 mb-3">
          {{ t("journal.todo.group.terminal") }} <span class="text-muted fs-7 ms-1">{{ resolvedItems.length }}</span>
        </h4>
        <div v-if="resolvedItems.length === 0" class="text-muted fs-7">{{ t("journal.todo.group.terminal.empty") }}</div>
        <div v-else>
          <div
            v-for="item in resolvedItems"
            :key="'keep-done-' + item.id"
            class="d-flex align-items-center py-2 border-bottom border-gray-200 text-muted"
          >
            <i class="bi fs-4 me-3" :class="terminalIconClass(item)"></i>
            <div
              class="flex-grow-1 text-truncate cursor-pointer text-decoration-line-through"
              :title="item.title"
              @click.prevent="openTodoEdit(item.id)"
            >
              <span
                v-if="item.prefix"
                class="fw-bold me-1"
                :class="item.prefix.textClass" :style="{ color: '#287D94' }"
              >[{{ item.prefix.name }}]</span>
              <span
                v-if="terminalBadge(item)"
                class="badge fs-9 me-1"
                :class="terminalBadge(item)!.badgeClass"
              >{{ terminalBadge(item)!.label }}</span>
              {{ item.title }}
              <TagChip
                v-for="tag in item.tag?.list"
                :key="'keep-done-tag-' + item.id + '-' + tag.tagId"
                class="fs-8"
                :name="tag.name ?? ''"
                :ctgr="tag.ctgr"
                clickable
                @click="openRowTagMenu($event, tag)"
              />
              <span v-if="item.expireDt" class="fs-8 ms-2" :class="expireLabelClass(item)">{{ expireLabel(item) }}</span>
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
                    <template v-for="lc in lifecycleOptions" :key="'keep-done-lc-' + item.id + '-' + lc.key">
                      <!--진행 2개와 종결 3개 사이 구분선-->
                      <div v-if="lc.key === 'RESOLVED'" class="separator my-2"></div>
                      <div class="menu-item px-3">
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
                    </template>
                  </div>
                </div>
                <!--end::라이프사이클 서브메뉴-->
                <div class="separator my-2"></div>
                <!--begin::상태 서브메뉴 (다중 속성 -- lifecycle 과 별개 축)-->
                <div class="menu-item px-3" data-kt-menu-trigger="hover" data-kt-menu-placement="left-start">
                  <a href="#" class="menu-link px-3" @click.prevent>
                    <span class="menu-title">{{ t("common.status") }}</span>
                    <span class="menu-arrow"></span>
                  </a>
                  <div class="menu-sub menu-sub-dropdown w-175px py-4">
                    <div v-for="st in stateOptions" :key="'keep-done-st-' + item.id + '-' + st.key" class="menu-item px-3">
                      <div class="menu-content px-3">
                        <label class="form-check form-switch form-check-custom form-check-solid cursor-pointer">
                          <input
                            class="form-check-input w-30px h-20px cursor-pointer"
                            type="checkbox"
                            :checked="hasState(item, st.key)"
                            @click="toggleState(item.id, st.key)"
                          />
                          <span
                            class="form-check-label fs-7"
                            :class="hasState(item, st.key) ? st.activeClass : 'text-muted'"
                          >{{ st.label }}</span>
                        </label>
                      </div>
                    </div>
                  </div>
                </div>
                <!--end::상태 서브메뉴-->
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
        </template>
        <!--end::종결-->
      </div>
    </div>
  </div>
  <JournalTodoRegistModal />
  <TagContextMenu />
  <TagProfileModal @success="fetchTagCloud" />
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
import TagCloud, { type TagCloudTag } from "@/shared/ui/tag/TagCloud.vue";
import TagChip from "@/shared/ui/tag/TagChip.vue";
import JournalTodoRegistModal from "@/features/journal/todo/modals/JournalTodoRegistModal.vue";
import { daysUntilExpire, formatExpireDt } from "@/features/journal/todo/utils/expireDt";
import TagContextMenu from "@/shared/ui/tag/TagContextMenu.vue";
import TagProfileModal from "@/features/attachable/TagProfileModal.vue";
import { useTagContextMenuStore } from "@/shared/ui/tag/tagContextMenu";
import { useTagProfileAction } from "@/features/attachable/composables/useTagProfileAction";

/** Keep 전 범위 항목 — 백엔드 JournalTodoDto 직렬화(scope=all) 중 화면 표시·검색분. */
interface KeepItem {
  id: number;
  title?: string;
  content?: string;
  /** 등록 일시 (`yyyy-MM-dd HH:mm:ss`). 목록의 시점 표기에 쓴다. */
  createdAt?: string;
  /** 선택된 개인 말머리(표시용). 선택이 없으면 null. */
  prefix?: JournalPrefixDto | null;
  /** 부착된 태그(표시·필터용). `tag_content` 연결에서 조립된다. */
  tag?: { list?: Array<{ tagId?: number; name?: string; ctgr?: string | null }> } | null;
  /** 부착된 상태(다중). 진행 단계(lifecycle)와 별개 축이다. */
  state?: { list?: Array<{ stateKey?: string }> } | null;
  /** 라이프사이클 현재값(백엔드 enrich). 부재 시 OPEN. */
  lifecycle?: { lifecycleKey?: string | null } | null;
  /** 유효 기한 (ISO `yyyy-MM-dd`). 일시 원문에서 파생한 값이며 없을 수 있다. */
  expireDt?: string | null;
}

const { t } = useLocaleStore();
const modalStore = useJournalModalStore();
const personalPrefixOptionsStore = usePersonalPrefixOptionsStore();
const tagContextMenuStore = useTagContextMenuStore();
const { tagProfileAction } = useTagProfileAction();

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

/** 태그클라우드 (JOURNAL_TODO 전체 태그). 표시 계약은 공통 `TagCloud` 를 따른다. */
const tagCloud = ref<TagCloudTag[]>([]);
const tagCloudLoading = ref(false);
const tagCloudError = ref("");
/** 태그 필터 (클라이언트 필터). null 이면 전체. */
const filterTagId = ref<number | null>(null);
/**
 * 기한 지남 필터 (클라이언트 필터).
 * 켜면 「유효 기한이 지났는데 아직 종결되지 않은」 항목만 남긴다 -- 처리하지 않고 쌓인 것을 모아 본다.
 * 종결된 항목은 이미 처리가 끝났으므로 기한이 지났어도 대상이 아니다.
 */
const filterOverdue = ref(false);

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
    if (filterOverdue.value && !isOverdueOpen(it)) return false;
    if (!k) return true;
    return (it.title ?? "").toLowerCase().includes(k)
      || (it.content ?? "").toLowerCase().includes(k);
  });
});
/**
 * 기한 오름차순 비교자. 기한이 없는 항목은 뒤로 보낸다.
 * ISO `yyyy-MM-dd` 는 사전순이 곧 날짜순이라 문자열 비교로 충분하다.
 */
function byExpireDtAsc(a: KeepItem, b: KeepItem): number {
  const left = a.expireDt ?? "";
  const right = b.expireDt ?? "";
  if (left === right) return 0;
  if (!left) return 1;
  if (!right) return -1;
  return left < right ? -1 : 1;
}

/**
 * 진행 투영 = OPEN·PENDING (종결 4종 제외). 임박한 것이 위로 오도록 기한 오름차순으로 정렬한다.
 *
 * 기한이 없는 항목은 맨 아래에 모으고, 그 안에서는 서버가 준 순서(sortOrder → id)를 그대로 둔다.
 * `Array.prototype.sort` 가 안정 정렬이라 기한이 같은 항목들의 상대 순서도 보존된다.
 * 즉 사용자가 손으로 맞춰 둔 순번은 기한이 붙지 않은 항목 사이에서 그대로 유지되고,
 * 기한이 붙은 항목만 그 위로 끌어올려진다. 지난 기한이 가장 이른 날짜라 자연히 맨 위에 선다.
 */
const openItems = computed<KeepItem[]>(() =>
  filtered.value
    .filter((it) => !TERMINAL_KEYS.includes(lifecycleKeyOf(it)))
    .slice()
    .sort(byExpireDtAsc)
);
/** 종결 투영 = RESOLVED·FAILED·EXPIRED·DROPPED. 끝난 이유는 행 배지로 구분한다. */
const resolvedItems = computed<KeepItem[]>(() =>
  filtered.value.filter((it) => TERMINAL_KEYS.includes(lifecycleKeyOf(it)))
);

/**
 * 기한이 지났는데 아직 종결되지 않은 항목인지 여부.
 * 종결 어휘가 붙은 항목은 이미 처리가 끝났으므로 기한이 지났어도 제외한다.
 */
function isOverdueOpen(item: KeepItem): boolean {
  if (!item.expireDt) return false;
  if (TERMINAL_KEYS.includes(lifecycleKeyOf(item))) return false;
  return daysUntilExpire(item.expireDt) < 0;
}

/**
 * 목록 행에 붙일 유효 기한 표기.
 *
 * 진행 행에서는 지난 기한에 남은 일수를 덧붙여 눈에 띄게 한다.
 * 종결 행에서는 날짜만 보인다 -- 끝난 이유가 EXPIRED 인지 DROPPED 인지는 기한을 근거로 갈리므로,
 * 나중에 그 판단을 다시 읽으려면 기한 자체는 남아 있어야 한다.
 */
function expireLabel(item: KeepItem): string {
  if (!item.expireDt) return "";
  const short = formatExpireDt(item.expireDt);
  // 종결 항목에는 남은 일수를 붙이지 않는다 -- 이미 끝나 행동을 재촉할 값이 아니다.
  if (TERMINAL_KEYS.includes(lifecycleKeyOf(item))) return short;
  const days = daysUntilExpire(item.expireDt);
  if (Number.isNaN(days) || days >= 0) return short;
  return `${short} · ${t("journal.todo.expire-dt.relative.past").replace("{0}", String(-days))}`;
}

/** 기한 표기 색. 미종결인 채 지난 기한만 경고색으로 가른다. */
function expireLabelClass(item: KeepItem): string {
  return isOverdueOpen(item) ? "text-warning fw-bold" : "text-muted";
}

/** 현재 보류(PENDING) 상태 여부. */
function isPending(item: KeepItem): boolean {
  return item.lifecycle?.lifecycleKey === "PENDING";
}

/** 항목의 현재 라이프사이클 키. 저장 row 가 없으면 OPEN 이 기본값이다. */
function lifecycleKeyOf(item: KeepItem): string {
  return item.lifecycle?.lifecycleKey ?? "OPEN";
}

/**
 * 라이프사이클 단계.
 * <pre>
 *  Keep 은 저널과 종결 어휘가 다르다. 저널 기록은 시간이 지나도 사라지지 않지만 Keep 항목은
 *  기회가 만료되고(EXPIRED), 의미를 잃어 내려놓기도 한다(DROPPED). 메뉴에서 진행 2개와 종결
 *  3개를 구분선으로 가른다.
 * </pre>
 */
const lifecycleOptions = computed(() => [
  { key: "OPEN", label: t("journal.entry.lifecycle.open"), activeClass: "text-gray-800", terminal: false },
  { key: "PENDING", label: t("lifecycle.pending"), activeClass: "text-gray-600", terminal: false },
  { key: "RESOLVED", label: t("status.completed"), activeClass: "text-success", terminal: true },
  { key: "FAILED", label: t("lifecycle.failed"), activeClass: "text-danger", terminal: true },
  { key: "EXPIRED", label: t("lifecycle.expired"), activeClass: "text-warning", terminal: true },
  { key: "DROPPED", label: t("lifecycle.dropped"), activeClass: "text-muted", terminal: true },
]);

/** 종결(RESOLVED·FAILED·EXPIRED·DROPPED) 키 집합. 진행 투영은 이들을 제외한다. */
const TERMINAL_KEYS = ["RESOLVED", "FAILED", "EXPIRED", "DROPPED"];

/**
 * Keep 상태 옵션 (다중 속성).
 * lifecycle 이 「어디까지 왔나」라면 state 는 「어떤 성격인가」다.
 * 요구(DEMANDED)는 내가 담아둔 것과 외부에서 요구받아 담긴 것을 가른다.
 */
const stateOptions = computed(() => [
  { key: "DEMANDED", label: t("state.demanded"), activeClass: "text-danger" },
]);

/** 항목이 해당 상태를 갖고 있는지. */
function hasState(item: KeepItem, stateKey: string): boolean {
  return (item.state?.list ?? []).some((st) => st.stateKey === stateKey);
}

/**
 * 상태 토글 (POST /api/states). 성공 시 목록을 재조회한다.
 * 저널 엔트리와 같은 계약이며 Keep 은 캐시 문맥이 없어 contentType 만 보낸다.
 */
async function toggleState(id: number, stateKey: string): Promise<void> {
  try {
    const res = await axios.post("/api/states", { id, contentType: "JOURNAL_TODO", stateKey });
    if (res.data?.rslt === true) {
      void fetchList();
    } else {
      void swalAlert(res.data?.message ?? t("common.result.failure"));
    }
  } catch (e: unknown) {
    void swalRequestError(e);
  }
}

/** 종결 항목의 좌측 아이콘. 끝난 이유를 한눈에 가른다. */
function terminalIconClass(item: KeepItem): string {
  const key = lifecycleKeyOf(item);
  if (key === "FAILED") return "bi-x-circle text-danger";
  if (key === "EXPIRED") return "bi-hourglass-bottom text-warning";
  if (key === "DROPPED") return "bi-slash-circle text-muted";
  return "bi-check2 text-success";
}

/** 종결 항목의 배지 표시 정보. 끝난 이유가 서로 달라 목록에서 구분해야 한다. */
function terminalBadge(item: KeepItem): { label: string; badgeClass: string } | null {
  const key = lifecycleKeyOf(item);
  if (key === "FAILED") return { label: t("lifecycle.failed"), badgeClass: "badge-light-danger" };
  if (key === "EXPIRED") return { label: t("lifecycle.expired"), badgeClass: "badge-light-warning" };
  if (key === "DROPPED") return { label: t("lifecycle.dropped"), badgeClass: "badge-light" };
  return null;
}

/**
 * 목록 행 우측의 시점 표기. 등록일(`createdAt`)의 년월을 `2026.09` 로 줄인다.
 * 값이 없거나 형식이 다르면 빈 문자열이라 표기 자체를 숨긴다.
 */
function registeredMonth(item: KeepItem): string {
  const m = /^(\d{4})-(\d{2})/.exec(item.createdAt ?? "");
  return m ? `${m[1]}.${m[2]}` : "";
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

/** 태그 필터 토글 — 같은 태그를 다시 누르면 해제한다. 목록은 클라이언트 투영이라 재조회하지 않는다. */
function toggleTagFilter(tagId: number): void {
  filterTagId.value = filterTagId.value === tagId ? null : tagId;
}

/**
 * 태그 컨텍스트 메뉴를 연다 (저널 태그클라우드와 같은 메뉴·버튼 모양).
 * <pre>
 *  Keep 에는 저널 엔트리 검색 문맥이 없으므로 첫 항목 자리에 이 화면의 주 동작인
 *  목록 필터 토글을 끼운다. 「태그 설정」(프로필)은 저널과 동일하게 동작한다.
 * </pre>
 */
function openTagMenu(event: MouseEvent, tagId: number, name: string, ctgr: string | null): void {
  const on = filterTagId.value === tagId;
  const payload = { tagId, name, ctgr: ctgr ?? "", contentType: "JOURNAL_TODO" };
  tagContextMenuStore.open(event, payload, [
    {
      label: on ? t("tag.filter.off") : t("tag.filter.on"),
      icon: on ? "bi-funnel" : "bi-funnel-fill",
      variant: "search",
      run: () => toggleTagFilter(tagId),
    },
    tagProfileAction(payload, t("journal.aside.todo.title")),
  ]);
}

/** 태그클라우드 버튼 클릭. */
function openTagContextMenu(tag: TagCloudTag, event: MouseEvent): void {
  openTagMenu(event, Number(tag.id), tag.name, tag.ctgr ?? null);
}

/**
 * 목록 행에 붙은 태그 클릭. 태그클라우드와 같은 메뉴를 연다.
 * 두 자리의 태그가 같은 대상을 가리키므로 조작도 같아야 한다.
 * 태그 ID 가 없는 표시 전용 항목은 메뉴를 열지 않는다.
 */
function openRowTagMenu(event: MouseEvent, tag: { tagId?: number; name?: string; ctgr?: string | null }): void {
  if (tag.tagId == null) return;
  openTagMenu(event, tag.tagId, tag.name ?? "", tag.ctgr ?? null);
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

/** 신규 추가 모달 열기. 초기값 없이 빈 폼으로 연다. */
function openAdd(): void {
  modalStore.openTodoRegist({});
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
    modalStore.openTodoRegist({
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
