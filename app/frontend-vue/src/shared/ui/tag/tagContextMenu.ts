/**
 * tagContextMenu.ts
 * 태그 클릭 컨텍스트 메뉴 상태 관리.
 * 레거시 journalDayTagContextMenuShell.ts 의 전역 DOM 방식을 Pinia store 로 대체.
 *
 * 저널·Keep·게시판이 같은 메뉴를 공유하므로 shared 에 둔다. 이 store 자체는
 * 어느 도메인에도 의존하지 않고 좌표·표시 여부·선택된 태그만 들고 있다.
 */
import { defineStore } from "pinia";
import { ref } from "vue";

export interface TagContextMenuPayload {
  tagId: number | string;
  name: string;
  ctgr: string;
  contentType: string;
}

/**
 * 메뉴 항목.
 * <pre>
 *  메뉴는 어떤 동작이 있어야 하는지 알지 않는다. 항목 구성은 여는 화면이 정한다 --
 *  저널은 검색·설정, Keep·게시판은 필터 토글·설정을 넣는다.
 * </pre>
 */
export interface TagContextMenuAction {
  /** 버튼 문구 */
  label: string;
  /** 버튼 아이콘 (bootstrap-icons 클래스) */
  icon: string;
  /** 버튼 변형 클래스 접미사 (ctx-menu-btn--<variant>) */
  variant?: string;
  /** 실행할 동작 */
  run: () => void;
}

export const useTagContextMenuStore = defineStore("tagContextMenu", () => {
  const visible = ref(false);
  const x = ref(0);
  const y = ref(0);
  const payload = ref<TagContextMenuPayload>({ tagId: "", name: "", ctgr: "", contentType: "" });
  /** 표시할 항목 목록. 여는 화면이 구성한다. */
  const actions = ref<TagContextMenuAction[]>([]);

  /**
   * 컨텍스트 메뉴를 열고 클릭 좌표 근처에 위치시킨다.
   * 레거시 getContextMenuPosition 과 동일하게 viewport 경계를 넘지 않도록 clamping.
   */
  function open(
    event: MouseEvent,
    tag: TagContextMenuPayload,
    menuActions: TagContextMenuAction[] = [],
  ): void {
    payload.value = tag;
    actions.value = menuActions;
    const menuWidth = 176;
    const menuHeight = 92;
    let left = event.clientX;
    let top = event.clientY + 8;
    left = Math.min(Math.max(left, 8), Math.max(window.innerWidth - menuWidth - 8, 8));
    top = Math.min(Math.max(top, 8), Math.max(window.innerHeight - menuHeight - 8, 8));
    x.value = left;
    y.value = top;
    visible.value = true;
  }

  function close(): void {
    visible.value = false;
  }

  return { visible, x, y, payload, actions, open, close };
});
