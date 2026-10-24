import { keepHasState, openKeepProjects, type KeepItem, type KeepPrefixOption, type KeepProject } from "../types/prospectiveKeep";
import { normalizeExpireDt } from "./keepExpire";
import { keepEditSourceText, KeepEditPreserveError, type KeepEditInput } from "./keepFormData";
import { keepDetailTags, keepTagKey, type KeepTagPick } from "./keepTags";

/** 수정 저장 입력. 중요는 수정 POST에 포함하지 않고 기존 저장 흐름의 별도 토글로 처리한다. */
export type KeepEditSubmit = KeepEditInput & { important: boolean };

/** 상세형 편집의 임시 값. 위치·본문은 원문이고 태그 선택 배열은 서버 응답과 분리한다. */
export interface KeepEditDraft {
  title: string;
  dateText: string;
  expireDt: string;
  projectId: number | null;
  prefixId: number | null;
  important: boolean;
  tags: KeepTagPick[];
  location: string;
  content: string;
}

/** 상세 응답의 원문과 태그를 확인한 뒤 편집 초깃값을 만든다. API 호출과 저장은 수행하지 않는다. */
export function keepEditDraft(detail: KeepItem): KeepEditDraft {
  const source = keepEditSourceText(detail);
  const tags = keepDetailTags(detail);
  if (tags == null) {
    console.warn("[KeepEdit] draft blocked: tags not readable", { id: detail.id });
    throw new KeepEditPreserveError("태그를 확인하지 못해 수정할 수 없습니다. 상세를 새로 고친 뒤 다시 시도하세요.", "tag");
  }
  return { ...keepEditInitial(detail), ...source, tags: tags.map((tag) => ({ ...tag })) };
}

/** 취소·재시작용 임시 값 복사. 태그 객체까지 복사해 기준값이 편집 중 변경되지 않게 한다. */
export function copyKeepEditDraft(draft: KeepEditDraft): KeepEditDraft {
  return { ...draft, tags: draft.tags.map((tag) => ({ ...tag })) };
}

/** 저장 전 버림 확인 여부. 모든 편집 필드의 값과 태그 집합을 비교하고 태그 표시 순서는 무시한다. */
export function keepEditDraftChanged(initial: KeepEditDraft, draft: KeepEditDraft): boolean {
  const fields = ["title", "dateText", "expireDt", "projectId", "prefixId", "important", "location", "content"] as const;
  if (fields.some((field) => initial[field] !== draft[field])) return true;
  const tags = (value: KeepEditDraft) => JSON.stringify(value.tags.map(keepTagKey).sort());
  return tags(initial) !== tags(draft);
}

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
