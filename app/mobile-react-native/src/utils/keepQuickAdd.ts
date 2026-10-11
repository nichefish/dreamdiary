import { parseExpireDt, scanLastDate } from "./keepDateInput";
import { daysUntilExpire } from "./keepExpire";
import type { KeepCreateInput } from "./keepFormData";
import type { KeepProjectScope } from "./keepProjection";
import type { KeepTagPick } from "./keepTags";

/** 제목 최대 길이 (웹 등록 모달 maxlength·`toKeepFormData`와 같음. 서버 DTO에는 길이 검증이 없다) */
export const KEEP_QUICK_TITLE_MAX = 100;
/** 일시 원문 최대 길이 (서버 `@Size(max = 200)`) */
export const KEEP_QUICK_DATE_TEXT_MAX = 200;

/** 빠른 추가 저장 요청. 중요는 등록 API에 필드가 없어 등록 후 `POST /api/states`(IMPRTC)로 따로 보낸다. */
export type KeepQuickAddSubmit = KeepCreateInput & { important: boolean };

/**
 * 빠른 추가 저장 결과.
 * - ok: 새 ID와, 등록은 됐지만 후속 단계(중요 표시·목록 조회)가 실패했을 때의 안내
 * - 실패: 화면에 보일 메시지. 401(공통 로그아웃 처리)은 null
 */
export type KeepQuickAddResult =
  | { ok: true; id: number; notice: string | null }
  | { ok: false; message: string | null };

/** 기한 입력의 해석 결과 */
export type QuickAddExpire = { kind: "empty" } | { kind: "ok"; date: string } | { kind: "invalid" };

/** 기한 아래 안내 문구 색 구분 */
export type QuickAddHintTone = "muted" | "normal" | "warning" | "danger" | "error";

/**
 * 현재 Project 칩에서 빠른 추가의 기본 Project를 정한다.
 * 웹 KeepPage `openAdd`와 같이 Project 칩이면 그 Project, `전체`·`미지정`이면 미지정(null).
 */
export function quickAddDefaultProjectId(scope: KeepProjectScope): number | null {
  return typeof scope === "number" ? scope : null;
}

/** 일시 원문에서 기한으로 잡을 날짜(가장 늦은 날짜). 없으면 빈 문자열. */
export function expireFromDateText(dateText: string, today: Date = new Date()): string {
  return scanLastDate(dateText, today).last ?? "";
}

/**
 * 기한 입력을 해석한다. `2026-10-24`·`10/24`·`26/10/24`처럼 웹 기한 규칙(`parseExpireDt`)이 받는 형식이며,
 * 년도를 뺀 날짜는 오늘 기준 가장 가까운 날로 정한다.
 */
export function resolveQuickAddExpire(input: string, today: Date = new Date()): QuickAddExpire {
  const result = parseExpireDt(input, today);
  if (result.kind === "ok") return { kind: "ok", date: result.date };
  return result.kind === "empty" ? { kind: "empty" } : { kind: "invalid" };
}

/**
 * 기한 아래 안내 문구 (웹 등록 모달 `expireDtHint`와 같은 내용).
 * 기한이 있으면 날짜와 남은 일수, 없으면 일시에서 찾을 수 있는 날짜를 보여 준다.
 */
export function quickAddExpireHint(expireInput: string, dateText: string, today: Date = new Date()): { text: string; tone: QuickAddHintTone } {
  const expire = resolveQuickAddExpire(expireInput, today);
  if (expire.kind === "invalid") {
    return { text: "기한을 해석할 수 없습니다. 예: 10/24, 2026-10-24 (비우면 기한 없음)", tone: "error" };
  }
  if (expire.kind === "ok") {
    const days = daysUntilExpire(expire.date, today);
    if (days === 0) return { text: `기한 ${expire.date} · 오늘`, tone: "danger" };
    if (days < 0) return { text: `기한 ${expire.date} · ${-days}일 지남`, tone: "warning" };
    return { text: `기한 ${expire.date} · ${days}일 남음`, tone: "normal" };
  }
  const scan = scanLastDate(dateText, today);
  if (scan.last === null) return { text: "일시에 날짜가 있으면 마지막 날을 기한으로 잡습니다.", tone: "muted" };
  return { text: `일시에서 찾은 날짜: ${scan.found.join(", ")} → 마지막 ${scan.last}`, tone: "muted" };
}

/** 저장 전 입력 검사 폼 */
export interface QuickAddForm {
  title: string;
  dateText: string;
  expireInput: string;
  projectId: number | null;
  /** 선택한 Keep 말머리 ID. 없으면 null */
  prefixId?: number | null;
  /** 고른 기존 태그 */
  tags?: KeepTagPick[];
  important: boolean;
}

/**
 * 빠른 추가 입력을 검사해 저장 요청으로 바꾼다. `toKeepFormData`와 같은 규칙을 화면에서 먼저 확인해 요청 전에 안내한다.
 * 기한은 해석된 절대 날짜(yyyy-MM-dd)만 보낸다.
 */
export function validateQuickAdd(form: QuickAddForm, today: Date = new Date()): { ok: true; input: KeepQuickAddSubmit } | { ok: false; message: string } {
  const title = form.title.trim();
  if (!title) return { ok: false, message: "제목을 입력해 주세요." };
  if (title.length > KEEP_QUICK_TITLE_MAX) return { ok: false, message: `제목은 ${KEEP_QUICK_TITLE_MAX}자 이내로 입력하세요.` };
  if (form.dateText.length > KEEP_QUICK_DATE_TEXT_MAX) return { ok: false, message: `일시는 ${KEEP_QUICK_DATE_TEXT_MAX}자 이내로 입력하세요.` };
  const expire = resolveQuickAddExpire(form.expireInput, today);
  if (expire.kind === "invalid") return { ok: false, message: "기한을 해석할 수 없습니다. 년도를 포함하거나 비워 주세요." };
  return {
    ok: true,
    input: {
      title,
      dateText: form.dateText,
      expireDt: expire.kind === "ok" ? expire.date : "",
      projectId: form.projectId,
      prefixId: form.prefixId ?? null,
      tags: form.tags ?? [],
      important: form.important
    }
  };
}

/**
 * 등록 실패 메시지. 서버가 응답한 실패(HTTP 상태가 있는 오류)는 서버 메시지를 그대로 쓰고,
 * 응답을 받지 못한 전송 오류는 등록됐을 수도 있으므로 목록 확인을 덧붙인다(비멱등 POST는 자동 재전송하지 않는다).
 */
export function quickAddFailureMessage(error: unknown): string {
  const message = error instanceof Error && error.message ? error.message : "Keep을 등록하지 못했습니다.";
  const status = (error as { status?: unknown } | null)?.status;
  if (typeof status === "number") return message;
  return `${message} 등록 여부를 목록에서 확인한 뒤 다시 시도하세요.`;
}
