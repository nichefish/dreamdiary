// @vitest-environment happy-dom
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { createApp, nextTick, type App } from "vue";
import axios from "axios";
import { reinitMetronicAfterDom } from "@/shared/utils/metronicReinit";
import KeepPage from "./KeepPage.vue";

const FIXTURE_NOW = new Date("2001-01-02T03:04:05.000Z");
const FIXTURE_TITLE = "샘플 메모";

vi.mock("axios", () => ({ default: { get: vi.fn() } }));
vi.mock("@/shared/utils/swal", () => ({ swalAlert: vi.fn(), swalConfirm: vi.fn(), swalRequestError: vi.fn() }));
vi.mock("@/shared/utils/metronicReinit", () => ({ reinitMetronicAfterDom: vi.fn(async () => {}) }));
vi.mock("@/shared/i18n/stores/locale", () => ({ useLocaleStore: () => ({ t: (key: string) => key }) }));
vi.mock("@/features/prospective/keep/stores/prospectiveKeepModal", () => ({ useProspectiveKeepModalStore: () => ({ registOpen: false }) }));
vi.mock("@/features/attachable/stores/personalPrefixOptions", () => ({ usePersonalPrefixOptionsStore: () => ({ optionsFor: () => [], fetchOptions: vi.fn() }) }));
vi.mock("@/shared/ui/tag/tagContextMenu", () => ({ useTagContextMenuStore: () => ({ open: vi.fn() }) }));
vi.mock("@/features/attachable/composables/useTagProfileAction", () => ({ useTagProfileAction: () => ({ tagProfileAction: vi.fn() }) }));
vi.mock("@/features/prospective/project/stores/prospectiveProject", () => ({ useProspectiveProjectStore: () => ({ openProjects: [], projects: [], fetchProjects: vi.fn(async () => {}) }) }));
vi.mock("@/shared/ui/tag/TagCloud.vue", () => ({ default: { render: () => null } }));
vi.mock("@/shared/ui/tag/TagChip.vue", () => ({ default: { render: () => null } }));
vi.mock("@/shared/ui/tag/TagContextMenu.vue", () => ({ default: { render: () => null } }));
vi.mock("@/features/attachable/TagProfileModal.vue", () => ({ default: { render: () => null } }));
vi.mock("@/features/prospective/keep/modals/ProspectiveKeepRegistModal.vue", () => ({ default: { render: () => null } }));
vi.mock("@/features/prospective/project/modals/ProspectiveProjectManageModal.vue", () => ({ default: { render: () => null } }));
vi.mock("@/features/prospective/project/components/ProspectiveKeepProjectMoveMenu.vue", () => ({ default: { render: () => null } }));

/** 가상 메모와 제어된 시계로 실제 페이지 DOM·메뉴 바인딩·경계 전환을 확인한다. */
describe("Keep 최근 완료 화면", () => {
  let app: App;
  let container: HTMLElement;
  beforeEach(async () => {
    vi.clearAllMocks();
    vi.useFakeTimers();
    vi.setSystemTime(FIXTURE_NOW);
    const recent = Array.from({ length: 6 }, (_, index) => ({
      id: 101 + index, title: `${FIXTURE_TITLE} ${101 + index}`,
      lifecycle: { lifecycleKey: "RESOLVED", resolvedAt: new Date(FIXTURE_NOW.getTime() - index * 1000).toISOString() },
      ...(index === 0 ? { state: { list: [{ stateKey: "IMPRTC" }, { stateKey: "DEMANDED" }] } } : {})
    }));
    vi.mocked(axios.get).mockImplementation(async (url) => ({ data: {
      rslt: true, rsltList: url === "/api/prospective/keeps" ? [
        { id: 201, title: `${FIXTURE_TITLE} 201`, lifecycle: { lifecycleKey: "OPEN" } },
        ...recent,
        { id: 301, title: `${FIXTURE_TITLE} 301`, lifecycle: { lifecycleKey: "RESOLVED", resolvedAt: null } }
      ] : []
    } }));
    container = document.createElement("div");
    document.body.appendChild(container);
    app = createApp(KeepPage);
    app.mount(container);
    await settle();
  });
  afterEach(() => {
    app.unmount();
    container.remove();
    vi.useRealTimers();
  });

  it("진행·최근 완료·종결 순서와 첫 5건을 표시하고 펼침 뒤 메뉴를 재바인딩한다", async () => {
    expect(Array.from(container.querySelectorAll("h4"), (element) => element.textContent?.trim().split(/\s/)[0]))
      .toEqual(["prospective.keep.group.active", "prospective.keep.group.recent", "prospective.keep.group.terminal"]);
    expect(container.textContent).not.toContain(`${FIXTURE_TITLE} 106`);
    const before = vi.mocked(reinitMetronicAfterDom).mock.calls.length;
    const more = Array.from(container.querySelectorAll("button")).find((button) => button.textContent?.includes("prospective.keep.recent.more"));
    expect(more).toBeDefined();
    more!.click();
    await settle();
    expect(container.textContent).toContain(`${FIXTURE_TITLE} 106`);
    expect(vi.mocked(reinitMetronicAfterDom).mock.calls.length).toBeGreaterThan(before);
  });

  it("종결 행도 진행 행처럼 중요 별·굵은 제목·요구 배지를 표시한다", () => {
    const row = Array.from(container.querySelectorAll("[title]")).find((el) => el.getAttribute("title") === `${FIXTURE_TITLE} 101`);
    expect(row).toBeDefined();
    expect(row!.classList.contains("fw-bold")).toBe(true);
    expect(row!.querySelector(".bi-star-fill.text-warning")).not.toBeNull();
    expect(row!.querySelector(".badge-light-danger")?.textContent).toBe("state.demanded");
    const plain = Array.from(container.querySelectorAll("[title]")).find((el) => el.getAttribute("title") === `${FIXTURE_TITLE} 102`);
    expect(plain!.querySelector(".badge-light-danger")).toBeNull();
    expect(plain!.classList.contains("fw-bold")).toBe(false);
  });

  it("1시간 경계에서 API 호출 없이 일반 종결로 이동하고 타이머를 정리한다", async () => {
    const requests = vi.mocked(axios.get).mock.calls.length;
    await vi.advanceTimersByTimeAsync(3600000);
    await settle();
    expect(container.textContent).not.toContain("prospective.keep.group.recent");
    for (let id = 101; id <= 106; id++) expect(container.textContent).toContain(`${FIXTURE_TITLE} ${id}`);
    expect(vi.mocked(axios.get).mock.calls.length).toBe(requests);
    expect(vi.getTimerCount()).toBe(0);
  });
});

/** 조회 Promise와 Vue DOM 반영을 순서대로 마친다. */
async function settle(): Promise<void> {
  for (let index = 0; index < 5; index++) { await Promise.resolve(); await nextTick(); }
}
