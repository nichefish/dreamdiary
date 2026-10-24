/** Project 표시에 필요한 개인 말머리 최소 계약 (백엔드 PrefixDto 표시 필드). */
export interface ProjectPrefixLike {
  id?: number | null;
  name?: string | null;
  /** 색 의미 코드의 CSS 클래스 (예: text-primary). 없으면 기본색. */
  textClass?: string | null;
  activeYn?: string | null;
}

/** Project 이름 표시에 필요한 최소 계약. */
export interface ProjectTitleLike {
  title?: string | null;
  prefix?: ProjectPrefixLike | null;
}

/** Project 말머리 표시 문자열. 말머리가 없거나 이름이 비면 빈 문자열이다. 예: "[게임]" */
export function projectPrefixLabel(project: ProjectTitleLike | null | undefined): string {
  const name = project?.prefix?.name?.trim();
  return name ? `[${name}]` : "";
}

/**
 * 말머리를 포함한 Project 표시 이름. 예: "[게임]왕국건설스토리" (말머리와 이름 사이 공백 없음).
 * <option>·확인 문구·미리보기처럼 색을 입힐 수 없는 평문 위치에서 사용한다.
 */
export function projectDisplayTitle(project: ProjectTitleLike | null | undefined): string {
  return `${projectPrefixLabel(project)}${project?.title ?? ""}`;
}
