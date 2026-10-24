import { keepProjectLifecycleKeyOf, keepProjectTitle, type KeepItem, type KeepProject } from "../types/prospectiveKeep";

/**
 * Keep 삭제·Project 이동 규칙 (모바일 2-4). 요청은 `deleteKeep`·`moveKeepProject`, 화면 잠금은 각 화면이 맡는다.
 */

/** 확인 대화상자 버튼 (React Native Alert.alert 와 같은 모양) */
export type KeepConfirmButton = { text: string; style?: "default" | "cancel" | "destructive"; onPress?: () => void };
export type KeepConfirmAlert = (title: string, message: string, buttons: KeepConfirmButton[], options?: { cancelable?: boolean; onDismiss?: () => void }) => void;

/** 삭제 확인 문구 — 제목을 밝히고 되돌릴 수 없음을 알린다. */
export function keepDeleteConfirmText(item: Pick<KeepItem, "title">): { title: string; message: string } {
  const name = item.title?.trim() || "(제목 없음)";
  return { title: "Keep 삭제", message: `'${name}'을(를) 삭제할까요?\n삭제하면 되돌릴 수 없습니다.` };
}

/**
 * 파괴적 확인 대화상자를 띄우고 사용자의 답을 돌려준다. `삭제`(destructive)만 true, 취소·바깥 누름은 false.
 * 한 번만 resolve 한다.
 */
export function confirmKeepDelete(item: Pick<KeepItem, "title">, alert: KeepConfirmAlert): Promise<boolean> {
  const { title, message } = keepDeleteConfirmText(item);
  return new Promise((resolve) => {
    let done = false;
    const finish = (value: boolean) => { if (!done) { done = true; resolve(value); } };
    alert(title, message, [
      { text: "취소", style: "cancel", onPress: () => finish(false) },
      { text: "삭제", style: "destructive", onPress: () => finish(true) }
    ], { cancelable: true, onDismiss: () => finish(false) });
  });
}

/** Project 이동 선택지 한 줄 */
export interface KeepMoveOption {
  id: number | null;
  label: string;
  /** 지금 소속 */
  current: boolean;
  /** 고를 수 없음(OPEN 이 아닌 Project — 웹과 같이 표시만) */
  disabled: boolean;
}

/**
 * Project 이동 선택지 — 웹 ProspectiveKeepProjectMoveMenu 와 같다: `미지정` + 전체 Project(정렬 순),
 * OPEN 이 아닌 Project 는 비활성 표시, 현재 소속에 표시. 목록에 없는 현재 Project 는 번호로 덧붙인다.
 */
export function keepMoveOptions(projects: KeepProject[], currentProjectId: number | null | undefined): KeepMoveOption[] {
  const current = currentProjectId ?? null;
  const options: KeepMoveOption[] = [
    { id: null, label: "미지정", current: current == null, disabled: false },
    ...projects.map((p) => ({
      id: p.id,
      label: keepProjectTitle(p) || `Project #${p.id}`,
      current: p.id === current,
      disabled: keepProjectLifecycleKeyOf(p) !== "OPEN"
    }))
  ];
  if (current != null && !projects.some((p) => p.id === current)) {
    options.push({ id: current, label: `Project #${current}`, current: true, disabled: true });
  }
  return options;
}

/** 실제로 보낼 이동인지(현재 소속·비활성 Project 는 보내지 않는다). */
export function keepMoveTarget(option: KeepMoveOption): number | null | undefined {
  if (option.current || option.disabled) return undefined;
  return option.id;
}

/** 이동 성공 안내 */
export function keepMoveNotice(option: Pick<KeepMoveOption, "id" | "label">): string {
  return option.id == null ? "Project 미지정으로 옮겼습니다." : `'${option.label}'(으)로 옮겼습니다.`;
}

/** 삭제·이동 실패 메시지 — 서버가 응답했으면 서버 메시지, 아니면 결과 확인 안내를 덧붙인다. */
export function keepManageFailureMessage(error: unknown, fallback: string): string {
  const message = error instanceof Error && error.message ? error.message : fallback;
  const status = (error as { status?: unknown } | null)?.status;
  return typeof status === "number" ? message : `${message} 결과를 목록에서 확인하세요.`;
}
