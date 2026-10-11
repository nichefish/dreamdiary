import { defineStore } from "pinia";
import { ref } from "vue";
import axios from "axios";

export const PROSPECTIVE_KEEP_CATEGORY_MAP_URL = "/api/prospective/keep/tag/categories";

function normalizeCategoryMap(raw: unknown): Record<string, string[]> {
  if (!raw || typeof raw !== "object" || Array.isArray(raw)) return {};
  const result: Record<string, string[]> = {};
  for (const [tagName, categories] of Object.entries(raw as Record<string, unknown>)) {
    if (!Array.isArray(categories)) continue;
    result[tagName] = categories.map((category) => String(category ?? "")).filter(Boolean);
  }
  return result;
}

/** Prospective Keep 태그 카테고리 맵의 세션 캐시. */
export const useProspectiveKeepCategoryMapStore = defineStore("prospectiveKeepCategoryMap", () => {
  const categoryMap = ref<Record<string, string[]>>({});
  const loaded = ref(false);
  let request: Promise<Record<string, string[]>> | null = null;

  /** 최초 소비 시 한 번 조회하며 동시 호출은 같은 요청을 공유한다. */
  async function ensure(): Promise<Record<string, string[]>> {
    if (loaded.value) return categoryMap.value;
    if (request) return request;
    request = axios.get(PROSPECTIVE_KEEP_CATEGORY_MAP_URL)
      .then((response) => {
        if (!response.data?.rslt) {
          console.error("[prospectiveKeepCategoryMap] categoryMap 조회 rslt=false");
          return {};
        }
        categoryMap.value = normalizeCategoryMap(response.data.rsltMap ?? response.data.rsltObj);
        loaded.value = true;
        return categoryMap.value;
      })
      .catch((error: unknown) => {
        console.error("[prospectiveKeepCategoryMap] categoryMap 조회 실패", error);
        return {};
      })
      .finally(() => { request = null; });
    return request;
  }

  /**
   * 저장으로 새 태그가 생겼을 수 있으므로 캐시를 무시하고 다시 조회한다.
   * 진행 중이던 ensure 요청은 저장 전 상태일 수 있어 기다리지 않고 새로 요청한다.
   */
  async function refresh(): Promise<Record<string, string[]>> {
    loaded.value = false;
    request = null;
    return ensure();
  }

  /** 사용자 세션 전환 시 저장된 맵을 비운다. */
  function reset(): void {
    request = null;
    categoryMap.value = {};
    loaded.value = false;
  }

  return { categoryMap, ensure, refresh, reset };
});
