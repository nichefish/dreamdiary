import { beforeEach, describe, expect, it, vi } from "vitest";
import { createPinia, setActivePinia } from "pinia";
import axios from "axios";
import { useProspectiveProjectStore } from "./prospectiveProject";

vi.mock("axios", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

describe("prospectiveProject store", () => {
  const mockedGet = vi.mocked(axios.get);
  const mockedPost = vi.mocked(axios.post);
  const mockedPut = vi.mocked(axios.put);
  const mockedDelete = vi.mocked(axios.delete);

  beforeEach(() => {
    setActivePinia(createPinia());
    vi.clearAllMocks();
    mockedGet.mockResolvedValue({
      data: { rslt: true, rsltList: [{ id: 11, title: "가상 프로젝트", sortOrder: 1 }] },
    });
  });

  it("Project 전체 목록을 서버 전역 순서대로 보관한다", async () => {
    const store = useProspectiveProjectStore();
    await store.fetchProjects();
    expect(mockedGet).toHaveBeenCalledWith("/api/prospective/projects");
    expect(store.projects).toEqual([{ id: 11, title: "가상 프로젝트", sortOrder: 1 }]);
  });

  it("OPEN Project만 작업공간 탭 대상으로 투영한다", async () => {
    mockedGet.mockResolvedValueOnce({
      data: {
        rslt: true,
        rsltList: [
          { id: 11, title: "진행 프로젝트", sortOrder: 1, lifecycle: { lifecycleKey: "OPEN" } },
          { id: 12, title: "보류 프로젝트", sortOrder: 2, lifecycle: { lifecycleKey: "PENDING" } },
          { id: 13, title: "기존 프로젝트", sortOrder: 3 },
        ],
      },
    });
    const store = useProspectiveProjectStore();

    await store.fetchProjects();

    expect(store.openProjects.map((project) => project.id)).toEqual([11, 13]);
  });

  it("등록과 수정은 multipart 요청 뒤 목록을 다시 조회한다", async () => {
    mockedPost.mockResolvedValue({ data: { rslt: true } });
    const store = useProspectiveProjectStore();

    await store.saveProject({ title: "새 프로젝트", description: "가상 설명" });
    await store.saveProject({ id: 11, title: "수정 프로젝트", description: "가상 설명" });

    expect(mockedPost.mock.calls[0]?.[0]).toBe("/api/prospective/projects");
    expect(mockedPost.mock.calls[1]?.[0]).toBe("/api/prospective/project/11");
    expect(mockedGet).toHaveBeenCalledTimes(2);
  });

  it("삭제와 전체 순서 변경 뒤 목록을 다시 조회한다", async () => {
    mockedDelete.mockResolvedValue({ data: { rslt: true } });
    mockedPut.mockResolvedValue({ data: { rslt: true } });
    const store = useProspectiveProjectStore();

    await store.deleteProject(11);
    await store.reorderProjects([12, 11]);

    expect(mockedDelete).toHaveBeenCalledWith("/api/prospective/project/11");
    expect(mockedPut).toHaveBeenCalledWith("/api/prospective/projects/sort-orders", [12, 11]);
    expect(mockedGet).toHaveBeenCalledTimes(2);
  });

  it("Project lifecycle을 공통 API로 변경한 뒤 전체 목록을 다시 조회한다", async () => {
    mockedPut.mockResolvedValue({ data: { rslt: true } });
    const store = useProspectiveProjectStore();

    await store.setProjectLifecycle(11, "PENDING");

    expect(mockedPut).toHaveBeenCalledWith("/api/lifecycles", {
      id: 11,
      contentType: "PROSPECTIVE_PROJECT",
      lifecycleKey: "PENDING",
    });
    expect(mockedGet).toHaveBeenCalledTimes(1);
  });
});
