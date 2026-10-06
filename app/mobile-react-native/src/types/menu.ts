/**
 * 백엔드 MenuDto 중 햄버거 메뉴에 쓰는 필드 (웹 shared/menu/stores/menu.ts 의 MenuDto 와 같은 이름).
 * GET /api/menus?mode=USER → rsltList: MenuDto[] (MAIN 루트 → 하위 메뉴 트리)
 */
export interface MenuDto {
  id?: number | null;
  parentMenuId?: number | string | null;
  /** MAIN(루트 컨테이너) | SUB */
  menuType?: string | null;
  menuName: string;
  /** 사이드바 머리 라벨 코드 (JOURNAL, KEEP …) */
  menuLabel?: string | null;
  /** 웹 메뉴 URL (FreeMarker 시절 URL — 웹은 urlMapping 으로 SPA 경로로 바꾼다) */
  url?: string | null;
  /** LIST | COLLAPSE | EXTEND | BOARD | NO_SUB */
  submenuExpandType?: string | null;
  useYn?: string | null;
  sidebarVisibleYn?: string | null;
  subMenuList?: Array<MenuDto | null> | null;
}
