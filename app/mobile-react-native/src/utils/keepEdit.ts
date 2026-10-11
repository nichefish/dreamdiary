import { keepHasState, openKeepProjects, type KeepItem, type KeepPrefixOption, type KeepProject } from "../types/prospectiveKeep";
import { normalizeExpireDt } from "./keepExpire";

/**
 * 모바일 Keep 수정 규칙. 직렬화(보존 필드 복사)는 `toKeepEditFormData`가 맡고, 여기는 시트 초기값·선택지·실패 메시지를 만든다.
 */

/** 상세 응답에서 수정 시트 초기값(앱에서 고칠 수 있는 필드)을 만든다. */
export function keepEditInitial(detail: KeepItem) {
  return {
    title: detail.title ?? "",
    dateText: detail.dateText ?? "",
    expireDt: normalizeExpireDt(detail.expireDt) ?? "",
    projectId: detail.projectId ?? null,
    prefixId: detail.prefixId ?? detail.prefix?.id ?? null,
    important: keepHasState(detail, "IMPRTC")
  };
}

/**
 * 수정 시트 말머리 선택지 — 활성 말머리 + (웹 수정 모달과 같이) 이미 붙은 비활성 말머리.
 * 서버는 현재 선택과 같은 비활성 말머리는 유지 저장을 허용한다.
 */
export function keepEditPrefixOptions(active: KeepPrefixOption[], detail: KeepItem): KeepPrefixOption[] {
  const current = detail.prefix;
  const currentId = detail.prefixId ?? current?.id ?? null;
  if (currentId == null || active.some((p) => p.id === currentId)) return active;
  const name = current?.name?.trim() || `말머리 #${currentId}`;
  return [...active, { id: currentId, name, sortOrder: Number.MAX_SAFE_INTEGER, textClass: current?.textClass ?? null }];
}

/** 수정 시트 Project 선택지 — OPEN Project + 현재 Project(종결돼도 유지할 수 있게). */
export function keepEditProjectOptions(projects: KeepProject[], detail: KeepItem): KeepProject[] {
  const open = openKeepProjects(projects);
  const currentId = detail.projectId ?? null;
  if (currentId == null || open.some((p) => p.id === currentId)) return open;
  const current = projects.find((p) => p.id === currentId);
  return [...open, current ?? { id: currentId }];
}

/** 같은 Keep을 그사이 다른 곳(웹)에서 고쳤는지. 고쳤으면 옛 응답의 본문·태그로 덮어쓰지 않도록 저장을 막는다. */
export function keepChangedSince(opened: KeepItem, fresh: KeepItem): boolean {
  return (opened.updatedAt ?? null) !== (fresh.updatedAt ?? null);
}

/** 수정 실패 메시지. 서버가 응답한 실패는 서버 메시지, 응답을 못 받았으면 반영 여부 확인을 덧붙인다. */
export function keepEditFailureMessage(error: unknown): string {
  const message = error instanceof Error && error.message ? error.message : "Keep을 수정하지 못했습니다.";
  const status = (error as { status?: unknown } | null)?.status;
  if (typeof status === "number" || (error as { name?: unknown } | null)?.name === "KeepEditPreserveError") return message;
  return `${message} 수정 여부를 상세에서 확인한 뒤 다시 시도하세요.`;
}
