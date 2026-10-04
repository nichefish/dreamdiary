import { beforeEach, describe, expect, it, vi } from "vitest";
import { createPinia, setActivePinia } from "pinia";
import axios from "axios";
import { useBoardPostStore } from "./boardPost";

vi.mock("axios", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    delete: vi.fn(),
  },
}));

vi.mock("@/shared/i18n/stores/locale", () => ({
  useLocaleStore: () => ({
    t: (key: string) => key,
  }),
}));

vi.mock("@/shared/auth/sessionPing", () => ({
  assertAuthenticatedBeforeModal: vi.fn().mockResolvedValue(true),
}));

vi.mock("@/shared/utils/swal", () => ({
  swalConfirm: vi.fn(),
  swalAlert: vi.fn(),
  swalRequestError: vi.fn(),
  swalFire: vi.fn(),
  swalAjaxResult: vi.fn(),
}));

const FIXTURE_BOARD_KEY = "FIXTURE_BOARD";
const FIXTURE_PREFIX = {
  id: 21,
  name: "가상 말머리",
  color: "#009EF7",
  sortOrder: 0,
  activeYn: "Y",
};

describe("boardPost store 게시판 재진입 조회", () => {
  const mockedGet = vi.mocked(axios.get);

  beforeEach(() => {
    setActivePinia(createPinia());
    vi.clearAllMocks();
  });

  it("같은 boardKey에 재진입하면 활성 Prefix 선택지를 다시 조회한다", async () => {
    let prefixRequestCount = 0;
    mockedGet.mockImplementation((url) => {
      if (url === "/api/board/posts") {
        return Promise.resolve({
          data: {
            rsltObj: { content: [], totalElements: 0, totalPages: 0, number: 0 },
          },
        });
      }
      if (url === `/api/board/${FIXTURE_BOARD_KEY}/prefixes`) {
        prefixRequestCount += 1;
        return Promise.resolve({
          data: { rsltList: prefixRequestCount === 1 ? [] : [FIXTURE_PREFIX] },
        });
      }
      if (url === "/api/tags") {
        return Promise.resolve({ data: { rsltList: [] } });
      }
      return Promise.reject(new Error(`예상하지 않은 URL: ${String(url)}`));
    });

    const store = useBoardPostStore();
    await store.setBoard(FIXTURE_BOARD_KEY);
    expect(store.prefixOptions).toEqual([]);

    await store.setBoard(FIXTURE_BOARD_KEY);

    expect(prefixRequestCount).toBe(2);
    expect(store.prefixOptions).toEqual([FIXTURE_PREFIX]);
  });
});

describe("boardPost store 상세 표면", () => {
  const mockedGet = vi.mocked(axios.get);

  beforeEach(() => {
    setActivePinia(createPinia());
    vi.clearAllMocks();
  });

  it("상세 조회는 경로 변수 URL을 쓰고 요청한 표면으로 연다", async () => {
    mockedGet.mockResolvedValue({ data: { rslt: true, rsltObj: { id: 7, title: "가상 게시물" } } });
    const store = useBoardPostStore();

    expect(await store.openDetailPage(7)).toBe(true);

    expect(mockedGet).toHaveBeenCalledWith("/api/board/posts/7");
    expect(store.detailOpen).toBe(true);
    expect(store.detailSurface).toBe("page");
    expect(store.detailModel?.id).toBe(7);
  });

  it("상세 조회가 실패하면 상세 상태를 닫는다", async () => {
    mockedGet.mockRejectedValue(new Error("가상 오류"));
    const store = useBoardPostStore();

    expect(await store.openDetail(7)).toBe(false);

    expect(store.detailOpen).toBe(false);
    expect(store.detailSurface).toBeNull();
    expect(store.detailLoading).toBe(false);
  });

  it("같은 게시판에 재진입하면 보존된 페이지를 다시 조회한다", async () => {
    const pageParams: unknown[] = [];
    mockedGet.mockImplementation((url, config) => {
      if (url === "/api/board/posts") {
        const page = (config?.params as { page?: number } | undefined)?.page ?? 0;
        pageParams.push(page);
        return Promise.resolve({
          data: { rslt: true, rsltObj: { content: [], totalElements: 30, totalPages: 3, number: page } },
        });
      }
      return Promise.resolve({ data: { rsltList: [] } });
    });
    const store = useBoardPostStore();

    await store.setBoard(FIXTURE_BOARD_KEY);
    await store.fetchList(2);
    await store.setBoard(FIXTURE_BOARD_KEY);

    expect(pageParams).toEqual([0, 2, 2]);
    expect(store.currentPage).toBe(2);
  });
});