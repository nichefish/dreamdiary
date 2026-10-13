import { parseExpireDt } from "./keepDateInput";
import type { KeepItem } from "../types/prospectiveKeep";
import { keepDetailTags, serializeKeepTags, type KeepTagPick } from "./keepTags";

/** 신규 Keep 등록 입력. 기한은 호출자가 확정한 절대 날짜이고 중요 속성은 별도 토글 요청으로 저장한다. */
export interface KeepCreateInput {
  title: string;
  dateText?: string;
  expireDt?: string;
  projectId?: number | null;
  /** 개인 Keep 말머리(PROSPECTIVE_KEEP 목록) ID. 없으면 null — 웹 등록 모달과 같은 `prefixId` 필드(빈 문자열 = 없음) */
  prefixId?: number | null;
  /** 고른 기존 태그. 웹 Tagify 와 같은 `tag.tagListStr` JSON 으로 보낸다(없으면 빈 값). 수정에서 undefined 면 상세의 태그를 그대로 보존 */
  tags?: KeepTagPick[];
}

/**
 * 신규 Keep 등록용 multipart 필드를 만든다.
 * 제목은 공백 제거 후 1~100자, 일시 원문은 최대 200자이며 기한은 실제 존재하는 yyyy-MM-dd 또는 빈 값이다.
 * 말머리는 선택한 ID(없으면 빈 값), 태그는 고른 기존 태그(Tagify JSON), 위치·본문은 신규 빈 값으로 보내고 순번은 서버가 배정한다. 편집의 전체 재전송에는 사용하지 않는다.
 */
export function toKeepFormData(input: KeepCreateInput): FormData {
  const title = input.title.trim();
  const dateText = input.dateText ?? "";
  const expireDt = input.expireDt ?? "";
  const projectId = input.projectId ?? null;
  const prefixId = input.prefixId ?? null;
  if (!title || title.length > 100) {
    console.warn("[KeepCreate] invalid title length", { length: title.length });
    throw new Error("제목은 1~100자로 입력하세요.");
  }
  if (dateText.length > 200) {
    console.warn("[KeepCreate] invalid date text length", { length: dateText.length });
    throw new Error("일시는 200자 이내로 입력하세요.");
  }
  if (expireDt && (!/^\d{4}-\d{2}-\d{2}$/.test(expireDt) || parseExpireDt(expireDt).kind !== "ok")) {
    console.warn("[KeepCreate] invalid confirmed due date");
    throw new Error("기한은 유효한 YYYY-MM-DD 날짜로 입력하세요.");
  }
  if (projectId != null && (!Number.isSafeInteger(projectId) || projectId <= 0)) {
    console.warn("[KeepCreate] invalid project ID");
    throw new Error("유효한 Project를 선택하세요.");
  }
  if (prefixId != null && (!Number.isSafeInteger(prefixId) || prefixId <= 0)) {
    console.warn("[KeepCreate] invalid prefix ID");
    throw new Error("유효한 말머리를 선택하세요.");
  }
  const form = new FormData();
  form.append("title", title);
  form.append("dateText", dateText);
  form.append("expireDt", expireDt);
  form.append("projectId", projectId == null ? "" : String(projectId));
  form.append("prefixId", prefixId == null ? "" : String(prefixId));
  form.append("location", "");
  form.append("content", "");
  form.append("tag.tagListStr", serializeKeepTags(input.tags ?? []));
  return form;
}

/** 앱에서 고칠 수 있는 Keep 필드 (수정 시트). 나머지 필드는 상세 응답에서 그대로 옮긴다. */
export type KeepEditInput = KeepCreateInput & {
  /** 지정하면 원문 그대로 저장하며 빈 문자열은 명시적 지움이다. 생략하면 상세의 현재 위치를 보존한다. */
  location?: string;
  /** 서버 렌더 표시값과 구분되는 저장 원문(웹 리치 에디터의 HTML 포함). 지정하면 저장하고 생략하면 상세 원문을 보존한다. */
  content?: string;
};

/** 수정 요청에서 보존할 수 없는 필드가 있을 때의 오류. 저장을 막고 화면에 그대로 보인다. */
export class KeepEditPreserveError extends Error {
  constructor(message: string, readonly field: string) {
    super(message);
    this.name = "KeepEditPreserveError";
  }
}

function hasOwn(obj: object, key: string): boolean {
  return Object.prototype.hasOwnProperty.call(obj, key);
}

/** 상세의 위치·본문 원문을 확인한다. 필드 누락·원문 없는 렌더 본문은 편집 초기화와 저장 모두 차단한다. */
export function keepEditSourceText(detail: KeepItem): { location: string; content: string } {
  if (!hasOwn(detail, "content") || (detail.content != null && typeof detail.content !== "string")) {
    console.warn("[KeepEdit] content not preservable", { id: detail.id });
    throw new KeepEditPreserveError("본문을 확인하지 못해 저장하지 않았습니다. 상세를 새로 고친 뒤 다시 시도하세요.", "content");
  }
  const markdown = typeof detail.markdownContent === "string" ? detail.markdownContent.trim() : "";
  if (detail.content == null && markdown && markdown !== "-") {
    console.warn("[KeepEdit] content missing but rendered body present", { id: detail.id });
    throw new KeepEditPreserveError("본문 원문을 받지 못해 저장하지 않았습니다. 웹에서 수정하세요.", "content");
  }
  if (!hasOwn(detail, "location") || (detail.location != null && typeof detail.location !== "string")) {
    console.warn("[KeepEdit] location not preservable", { id: detail.id });
    throw new KeepEditPreserveError("위치를 확인하지 못해 저장하지 않았습니다. 상세를 새로 고친 뒤 다시 시도하세요.", "location");
  }
  return { location: detail.location ?? "", content: detail.content ?? "" };
}

/**
 * Keep 수정용 multipart 필드를 만든다 — 웹 `ProspectiveKeepRegistModal.submit`(수정)과 같은 필드·순서·형식:
 * `id, title, sortOrder(있을 때만), prefixId, projectId, location, dateText, expireDt, content, tag.tagListStr`.
 * 서버는 받은 값으로 덮어쓰므로(빈 값 = 지움) 앱에서 고치지 않는 위치·본문·태그·순번은 상세 응답(`GET /api/prospective/keep/{id}`)에서
 * 그대로 옮긴다(태그는 시트에서 고친 값, 고치지 않았으면 상세 값). 응답 모양이 예상과 달라 보존할 수 없으면 `KeepEditPreserveError`를 던져 저장을 막는다(지우지 않는다).
 * 위치·본문이 편집값에 지정돼 있으면 그 원문을 저장하며, 생략된 필드는 위 보존 규칙을 따른다. 빈 문자열은 명시적 지움이다.
 * 상태(중요·요구)·라이프사이클은 웹도 이 요청에 싣지 않으며 서버가 따로 저장한다. Keep 에는 첨부 파일 필드가 없다.
 */
export function toKeepEditFormData(detail: KeepItem, edit: KeepEditInput): FormData {
  if (!Number.isSafeInteger(detail.id) || detail.id <= 0) {
    throw new KeepEditPreserveError("수정할 Keep ID를 확인하지 못했습니다.", "id");
  }
  const sourceText = keepEditSourceText(detail);
  if (edit.location !== undefined && (typeof edit.location !== "string" || edit.location.length > 200)) {
    console.warn("[KeepEdit] invalid edited location", { id: detail.id });
    throw new KeepEditPreserveError("위치는 200자 이내로 입력하세요.", "location");
  }
  if (edit.content !== undefined && typeof edit.content !== "string") {
    console.warn("[KeepEdit] invalid edited content", { id: detail.id });
    throw new KeepEditPreserveError("본문은 에디터의 HTML 원문으로 전달하세요.", "content");
  }
  // 태그: 시트에서 고친 목록이 있으면 그것을, 없으면(undefined) 상세의 현재 태그를 그대로 보낸다.
  const detailTags = edit.tags === undefined ? keepDetailTags(detail) : edit.tags;
  const tagListStr = detailTags == null ? null : serializeKeepTags(detailTags);
  if (tagListStr == null) {
    console.warn("[KeepEdit] tags not preservable", { id: detail.id });
    throw new KeepEditPreserveError("태그를 그대로 옮길 수 없어 저장하지 않았습니다. 웹에서 수정하세요.", "tag");
  }
  const sortOrder = detail.sortOrder;
  if (sortOrder != null && !Number.isSafeInteger(sortOrder)) {
    throw new KeepEditPreserveError("순번을 확인하지 못해 저장하지 않았습니다.", "sortOrder");
  }
  // 고칠 수 있는 필드는 신규 등록과 같은 규칙으로 검사·직렬화한다.
  const editable = toKeepFormData(edit);
  const value = (key: string) => String(editable.get(key) ?? "");
  const form = new FormData();
  form.append("id", String(detail.id));
  form.append("title", value("title"));
  if (sortOrder != null) form.append("sortOrder", String(sortOrder));
  form.append("prefixId", value("prefixId"));
  form.append("projectId", value("projectId"));
  form.append("location", edit.location === undefined ? sourceText.location : edit.location);
  form.append("dateText", value("dateText"));
  form.append("expireDt", value("expireDt"));
  form.append("content", edit.content === undefined ? sourceText.content : edit.content);
  form.append("tag.tagListStr", tagListStr);
  return form;
}
