import { CommonActions } from "@react-navigation/native";
import type { NavigationProp, ParamListBase } from "@react-navigation/native";
import { getUserMenus } from "../api/dreamDiaryApi";
import { isUnauthorizedError } from "../api/client";
import type { MenuDto } from "../types/menu";

/**
 * 햄버거 메뉴(사용자 메뉴) 모델과 이동 규칙.
 * 원천은 웹 사이드바와 같은 GET /api/menus?mode=USER (서버 menu 테이블 + 게시판 하위 메뉴 주입 + 권한 필터).
 * 관리자 메뉴(mode=MNGR)는 모바일에 두지 않는다.
 */

/** 웹 메뉴 URL (서버 menu.url = 웹 urlMapping.ts 의 키) */
export const MENU_URL = {
  JOURNAL_DAY: "/app/journal/day/home",
  JOURNAL_THREAD: "/app/journal/thread/list.do",
  JOURNAL_ANNUAL: "/app/journal/annual/list.do",
  /** Keep 전수관리 (웹 /keep, 서버 menu.url 도 "/keep") */
  KEEP: "/keep",
  USER_MY: "/app/user/my/page.do"
} as const;

/** 메뉴 → 모바일 화면. tab: 하단 탭, screen: 읽기 전용 스택 화면, placeholder: `준비 중` 안내 화면 */
export type AppMenuTarget =
  | { kind: "tab"; tab: "Today" | "Profile" }
  | { kind: "screen"; screen: "ThreadList" | "AnnualList" | "KeepHome" }
  | { kind: "placeholder" };

export type AppMenuItem = {
  key: string;
  name: string;
  /** 정규화한 웹 메뉴 URL — 현재 메뉴 강조 비교 키 */
  url: string;
  /** 속한 묶음 이름 (준비 중 화면 표시용) */
  groupName: string;
  target: AppMenuTarget;
};

export type AppMenuSection = { key: string; name: string; items: AppMenuItem[] };

export type AppMenu = { sections: AppMenuSection[]; source: "server" | "fallback" };

/** 메뉴 URL 비교용 정규화 — origin·끝 슬래시 제거, 쿼리(게시판 contentType)는 유지 */
export function normalizeMenuUrl(url: string | null | undefined): string {
  const raw = (url ?? "").trim();
  if (!raw) return "";
  const withoutOrigin = raw.replace(/^https?:\/\/[^/]+/i, "");
  const queryIndex = withoutOrigin.indexOf("?");
  const path = queryIndex >= 0 ? withoutOrigin.slice(0, queryIndex) : withoutOrigin;
  const query = queryIndex >= 0 ? withoutOrigin.slice(queryIndex + 1) : "";
  const cleanPath = path.replace(/\/+$/, "") || "/";
  return query ? `${cleanPath}?${query}` : cleanPath;
}

/** 웹 메뉴 URL → 모바일 이동 대상 */
export function resolveMenuTarget(url: string): AppMenuTarget {
  const path = normalizeMenuUrl(url).split("?")[0];
  switch (path) {
    case MENU_URL.JOURNAL_DAY:
      return { kind: "tab", tab: "Today" };
    case MENU_URL.JOURNAL_THREAD:
      return { kind: "screen", screen: "ThreadList" };
    case MENU_URL.JOURNAL_ANNUAL:
      return { kind: "screen", screen: "AnnualList" };
    case MENU_URL.KEEP:
      return { kind: "screen", screen: "KeepHome" };
    case MENU_URL.USER_MY:
      return { kind: "tab", tab: "Profile" };
    default:
      return { kind: "placeholder" };
  }
}

function isVisible(menu: MenuDto): boolean {
  return menu.useYn !== "N" && menu.sidebarVisibleYn !== "N" && !!menu.menuName?.trim();
}

function childrenOf(menu: MenuDto): MenuDto[] {
  return (menu.subMenuList ?? []).filter((m): m is MenuDto => m != null && isVisible(m));
}

/** 묶음 안 항목 — 하위가 더 있는 노드는 펼쳐 잎 메뉴만 둔다(웹 사이드바는 아코디언이 항상 펼쳐져 있다) */
function collectItems(nodes: MenuDto[], groupName: string, out: AppMenuItem[]): void {
  for (const node of nodes) {
    const sub = childrenOf(node);
    if (sub.length > 0) {
      collectItems(sub, groupName, out);
      continue;
    }
    const url = normalizeMenuUrl(node.url);
    if (!url) continue;
    out.push({
      key: `${node.id ?? node.menuName}:${url}`,
      name: node.menuName.trim(),
      url,
      groupName,
      target: resolveMenuTarget(url)
    });
  }
}

/**
 * 서버 메뉴 트리 → 드로어 묶음 목록 (웹 SidebarMenuItem 규칙).
 * - MAIN 루트(`사용자`)는 컨테이너라 자식만 펼친다.
 * - 그 아래 하위가 있는 메뉴(저널·Keep·일반게시판·일정 …)가 묶음 머리, 그 잎 메뉴가 항목. 순서는 서버 sortOrder 그대로.
 * - 하위 없이 URL 만 있는 메뉴는 이름 없는 묶음에 모으고, 하위도 URL 도 없는 메뉴(게시판이 없는 일반게시판 등)는 빈 묶음으로 둔다.
 */
export function toMenuSections(list: MenuDto[]): AppMenuSection[] {
  const top: MenuDto[] = [];
  const visit = (menu: MenuDto): void => {
    if (!isVisible(menu)) return;
    const sub = childrenOf(menu);
    if (menu.menuType === "MAIN" && sub.length > 0) sub.forEach(visit);
    else top.push(menu);
  };
  list.forEach(visit);

  const sections: AppMenuSection[] = [];
  let loose: AppMenuSection | null = null;
  for (let index = 0; index < top.length; index += 1) {
    const menu = top[index];
    if (!menu) continue;
    const name = menu.menuName.trim();
    const sub = childrenOf(menu);
    if (sub.length === 0 && normalizeMenuUrl(menu.url)) {
      if (loose == null) {
        loose = { key: `loose-${index}`, name: "", items: [] };
        sections.push(loose);
      }
      collectItems([menu], "", loose.items);
      continue;
    }
    loose = null;
    const items: AppMenuItem[] = [];
    collectItems(sub, name, items);
    sections.push({ key: `${menu.id ?? name}-${index}`, name, items });
  }
  return sections;
}

const fallbackItem = (id: number, menuName: string, url: string): MenuDto => ({
  id,
  menuType: "SUB",
  menuName,
  url,
  submenuExpandType: "NO_SUB",
  subMenuList: []
});

/**
 * 서버 메뉴를 못 받았을 때 쓰는 기본 사용자 메뉴 — 2026-10-06 dreamdiary_private menu 테이블의 사용자 메뉴와 같은 구조·이름·순서.
 * (웹 menu 스토어의 FALLBACK_USER_MENU_LIST 는 예전 구조라 따르지 않는다.) 게시판 목록은 서버에서만 온다.
 */
export const FALLBACK_USER_MENU: MenuDto[] = [
  {
    id: 6,
    menuType: "MAIN",
    menuName: "사용자",
    submenuExpandType: "LIST",
    subMenuList: [
      {
        id: 8,
        menuType: "SUB",
        menuName: "저널",
        submenuExpandType: "LIST",
        subMenuList: [
          fallbackItem(13, "저널 일자", MENU_URL.JOURNAL_DAY),
          fallbackItem(14, "저널 스레드", MENU_URL.JOURNAL_THREAD),
          fallbackItem(12, "저널 결산", MENU_URL.JOURNAL_ANNUAL)
        ]
      },
      {
        id: 51,
        menuType: "SUB",
        menuName: "Keep",
        submenuExpandType: "LIST",
        subMenuList: [fallbackItem(52, "Keep 전수관리", MENU_URL.KEEP)]
      },
      { id: 9, menuType: "SUB", menuName: "일반게시판", submenuExpandType: "BOARD", subMenuList: [] },
      {
        id: 10,
        menuType: "SUB",
        menuName: "일정",
        submenuExpandType: "LIST",
        subMenuList: [fallbackItem(15, "일정 달력", "/app/schedule/calendar.do")]
      }
    ]
  }
];

const MENU_CACHE_TTL_MS = 5 * 60_000;
let menuCache: { at: number; menu: AppMenu } | null = null;
let menuInflight: Promise<AppMenu> | null = null;

/** 마지막으로 받은 서버 메뉴 (드로어를 다시 열 때 바로 그린다) */
export function getCachedAppMenu(): AppMenu | null {
  return menuCache?.menu ?? null;
}

/**
 * 사용자 메뉴 조회 (5분 캐시, 진행 중 요청 공유).
 * 실패하거나 빈 목록이면 기본 메뉴(FALLBACK_USER_MENU)를 돌려준다 — 웹 menu 스토어와 같은 규칙. 기본 메뉴는 캐시하지 않는다.
 */
export function loadAppMenu(force = false): Promise<AppMenu> {
  if (!force && menuCache && Date.now() - menuCache.at < MENU_CACHE_TTL_MS) return Promise.resolve(menuCache.menu);
  if (menuInflight) return menuInflight;
  const promise: Promise<AppMenu> = getUserMenus()
    .then((list): AppMenu => {
      const sections = toMenuSections(list);
      if (sections.length === 0) throw new Error("사용자 메뉴가 비어 있습니다.");
      const menu: AppMenu = { sections, source: "server" };
      menuCache = { at: Date.now(), menu };
      return menu;
    })
    .catch((e: unknown): AppMenu => {
      if (!isUnauthorizedError(e)) console.warn("[appMenu] user menu load failed — fallback menu", e);
      return { sections: toMenuSections(FALLBACK_USER_MENU), source: "fallback" };
    })
    .finally(() => {
      menuInflight = null;
    });
  menuInflight = promise;
  return promise;
}

export function isCurrentMenu(item: AppMenuItem, currentUrl?: string): boolean {
  return !!currentUrl && normalizeMenuUrl(currentUrl) === item.url;
}

/**
 * 메뉴 항목으로 이동한다. 이미 스택에 있는 메뉴 화면이면 그 화면으로 돌아가고(pop), 없으면 push 한다.
 * 하단 탭 메뉴는 루트(Main)로 돌아가 그 탭을 연다.
 */
export function navigateToMenu(navigation: NavigationProp<ParamListBase>, item: AppMenuItem): void {
  const target = item.target;
  if (target.kind === "tab") {
    navigation.dispatch(CommonActions.navigate({ name: "Main", params: { screen: target.tab }, pop: true }));
    return;
  }
  if (target.kind === "screen") {
    navigation.dispatch(CommonActions.navigate({ name: target.screen, pop: true }));
    return;
  }
  navigation.dispatch(
    CommonActions.navigate({
      name: "MenuPlaceholder",
      params: { menuName: item.name, groupName: item.groupName, url: item.url },
      pop: true
    })
  );
}
