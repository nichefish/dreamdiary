import { computed, ref } from "vue";
import { defineStore } from "pinia";
import axios from "axios";

/** Prospective Keep을 조직하는 사용자 소유 Project. */
export interface ProspectiveProject {
  id: number;
  title: string;
  description?: string | null;
  sortOrder?: number;
  lifecycle?: { lifecycleKey?: string | null } | null;
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

  /** Project를 등록하거나 수정한 뒤 목록을 다시 조회한다. */
  async function saveProject(payload: Pick<ProspectiveProject, "title" | "description"> & { id?: number }): Promise<void> {
    saving.value = true;
    try {
      const formData = new FormData();
      formData.append("title", payload.title);
      formData.append("description", payload.description ?? "");
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
