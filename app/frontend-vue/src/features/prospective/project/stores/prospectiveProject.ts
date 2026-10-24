import { computed, ref } from "vue";
import { defineStore } from "pinia";
import axios from "axios";
import type { ProjectPrefixLike } from "@/features/prospective/project/utils/projectDisplayTitle";

/** Project 개인 말머리 목록 키 (PERSONAL + PROSPECTIVE_PROJECT Scope). Project 관리 모달에서만 관리한다. */
export const PROJECT_PREFIX_CONTENT_TYPE = "PROSPECTIVE_PROJECT";

/** Prospective Keep을 조직하는 사용자 소유 Project. */
export interface ProspectiveProject {
  id: number;
  title: string;
  description?: string | null;
  sortOrder?: number;
  lifecycle?: { lifecycleKey?: string | null } | null;
  /** 현재 선택된 개인 말머리 ID. 없으면 null. */
  prefixId?: number | null;
  /** 표시용 개인 말머리. 비활성이어도 기존 선택이면 내려온다. */
  prefix?: (ProjectPrefixLike & { id?: number | null }) | null;
}

/** Project 등록·수정 요청 값. */
export interface ProspectiveProjectSavePayload {
  id?: number;
  title: string;
  description?: string | null;
  /** 선택한 말머리 ID. null이면 선택 해제. 서버는 누락도 해제로 해석하므로 항상 보낸다. */
  prefixId: number | null;
}

/** Keep 화면과 등록 모달이 공유하는 Project 목록 상태. */
export const useProspectiveProjectStore = defineStore("prospectiveProject", () => {
  const projects = ref<ProspectiveProject[]>([]);
  const loading = ref(false);
  const saving = ref(false);
  /** 현재 작업공간 탭과 신규 Keep 배치에 노출할 진행 중 Project. */
  const openProjects = computed(() => projects.value.filter((project) => lifecycleKeyOf(project) === "OPEN"));

  /** 저장 행이 없는 기존 Project를 OPEN으로 해석한다. */
  function lifecycleKeyOf(project: ProspectiveProject): string {
    return project.lifecycle?.lifecycleKey ?? "OPEN";
  }

  /** 현재 사용자의 soft-delete되지 않은 Project 전체를 서버 전역 순서대로 조회한다. */
  async function fetchProjects(): Promise<void> {
    loading.value = true;
    try {
      const response = await axios.get("/api/prospective/projects");
      if (response.data?.rslt !== true) throw new Error(response.data?.message ?? "Project list request failed");
      projects.value = (response.data?.rsltList ?? []) as ProspectiveProject[];
    } catch (error) {
      console.error("[prospectiveProject] project list load failed", error);
      throw error;
    } finally {
      loading.value = false;
    }
  }

  /** Project를 등록하거나 수정한 뒤 목록을 다시 조회한다. prefixId는 빈 값(해제)이라도 항상 전송한다. */
  async function saveProject(payload: ProspectiveProjectSavePayload): Promise<void> {
    saving.value = true;
    try {
      const formData = new FormData();
      formData.append("title", payload.title);
      formData.append("description", payload.description ?? "");
      formData.append("prefixId", payload.prefixId == null ? "" : String(payload.prefixId));
      const url = payload.id == null ? "/api/prospective/projects" : `/api/prospective/project/${payload.id}`;
      const response = await axios.post(url, formData, { headers: { "Content-Type": "multipart/form-data" } });
      if (response.data?.rslt !== true) throw new Error(response.data?.message ?? "Project save request failed");
      await fetchProjects();
    } finally {
      saving.value = false;
    }
  }

  /** Project를 삭제한다. 연결된 Keep은 서버 계약에 따라 미지정 상태로 남는다. */
  async function deleteProject(id: number): Promise<void> {
    saving.value = true;
    try {
      const response = await axios.delete(`/api/prospective/project/${id}`);
      if (response.data?.rslt !== true) throw new Error(response.data?.message ?? "Project delete request failed");
      await fetchProjects();
    } finally {
      saving.value = false;
    }
  }

  /** 전체 Project ID를 화면 순서대로 보내 서버 전역 순서를 수렴시킨다. */
  async function reorderProjects(orderedIds: number[]): Promise<void> {
    saving.value = true;
    try {
      const response = await axios.put("/api/prospective/projects/sort-orders", orderedIds);
      if (response.data?.rslt !== true) throw new Error(response.data?.message ?? "Project reorder request failed");
      await fetchProjects();
    } finally {
      saving.value = false;
    }
  }

  /** Project의 진행 단계를 바꾸고 전체 목록을 다시 조회한다. */
  async function setProjectLifecycle(id: number, lifecycleKey: "OPEN" | "PENDING" | "RESOLVED" | "DROPPED"): Promise<void> {
    saving.value = true;
    try {
      const response = await axios.put("/api/lifecycles", {
        id,
        contentType: "PROSPECTIVE_PROJECT",
        lifecycleKey,
      });
      if (response.data?.rslt !== true) throw new Error(response.data?.message ?? "Project lifecycle request failed");
      await fetchProjects();
    } finally {
      saving.value = false;
    }
  }

  return {
    projects,
    openProjects,
    loading,
    saving,
    lifecycleKeyOf,
    fetchProjects,
    saveProject,
    deleteProject,
    reorderProjects,
    setProjectLifecycle,
  };
});
