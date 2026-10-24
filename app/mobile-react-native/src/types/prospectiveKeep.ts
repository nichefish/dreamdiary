import { colors } from "../theme/colors";
import type { JournalTagCmpstn } from "./journalDay";
import { colorFromTextClass } from "../components/journal/journalTagStyle";

/**
 * Keep(전수관리, 백엔드 feature `prospective`) 읽기 전용 타입과 도메인 규칙.
 * 필드 이름은 백엔드 DTO(ProspectiveKeepDto · ProspectiveProjectDto · PrefixDto · StateCmpstn/StateDto ·
 * LifecycleCmpstn · TagCmpstn/TagContentDto · TagDto)와 같다. 웹 KeepPage.vue 의 표시 규칙을 옮겼다.
 */

/** 백엔드 PrefixDto (Keep 개인 말머리, contentType=PROSPECTIVE_KEEP) */
export interface KeepPrefix {
  id?: number | null;
  name?: string | null;
  textClassCd?: string | null;
  /** 렌더용 색 클래스. 미지정이면 빈 문자열 */
  textClass?: string | null;
  sortOrder?: number | null;
  activeYn?: string | null;
}

/** 백엔드 StateDto (StateCmpstn.list 항목) */
export interface KeepStateItem {
  id?: number | null;
  stateKey?: string | null;
  refId?: number | null;
  refContentType?: string | null;
}

/** 백엔드 LifecycleCmpstn. 저장 row 가 없으면 null/빈 값 = OPEN */
export interface KeepLifecycle {
  lifecycleKey?: string | null;
  lifecycleDesc?: string | null;
  /** 서버가 저장한 현재 완료 전이 시각(UTC ISO-8601). 미확인 시 null이다. */
  resolvedAt?: string | null;
  /** 현재 상태로 바뀐 시각(UTC ISO-8601, 서버 V0.30.16 lifecycle.changed_at). 마이그레이션 이전 행·구 서버는 null/없음 */
  changedAt?: string | null;
}

/**
 * 백엔드 ProspectiveKeepDto (BaseAttachableDto → BaseAuditDto → BaseAuditRegDto) 중 화면이 쓰는 필드.
 * - 목록(GET /api/prospective/keeps?scope=all)과 상세(GET /api/prospective/keep/{id}) 모두 lifecycle 을 붙여 준다
 *   (서비스 applyKeepLifecycles, 행이 없으면 OPEN). 상세는 이전 백엔드에서 lifecycle 이 없었다 — 없으면 OPEN 으로 보인다.
 */
export interface KeepItem {
  id: number;
  contentType?: string | null;
  title?: string | null;
  /** 본문 마크다운 원문 */
  content?: string | null;
  /** 서버가 렌더한 HTML. 본문이 비면 "-" */
  markdownContent?: string | null;
  location?: string | null;
  /** 일시 자유 텍스트 원문 (예: `10/17, 24`) */
  dateText?: string | null;
  /** 유효 기한 yyyy-MM-dd (없으면 null/빈 문자열) */
  expireDt?: string | null;
  prefix?: KeepPrefix | null;
  prefixId?: number | null;
  /** 사용자 전역 순번 (서버 목록 정렬 sortOrder → id) */
  sortOrder?: number | null;
  projectId?: number | null;
  lifecycle?: KeepLifecycle | null;
  state?: { contentType?: string | null; list?: Array<KeepStateItem | null> | null } | null;
  tag?: JournalTagCmpstn | null;
  /** yyyy-MM-dd HH:mm:ss */
  createdAt?: string | null;
  createdByNm?: string | null;
  updatedAt?: string | null;
  updatedByNm?: string | null;
}

/** 백엔드 ProspectiveProjectDto (GET /api/prospective/projects) */
export interface KeepProject {
  id: number;
  title?: string | null;
  description?: string | null;
  sortOrder?: number | null;
  /** OPEN · PENDING · RESOLVED · DROPPED. 저장 row 가 없으면 OPEN */
  lifecycle?: KeepLifecycle | null;
  /** 선택된 개인 말머리 ID (PROSPECTIVE_PROJECT 목록). 없으면 null */
  prefixId?: number | null;
  /** 표시용 Project 말머리 (백엔드 PrefixDto). 웹 Project 관리 모달에서만 고른다. 비활성이어도 기존 선택이면 내려온다 */
  prefix?: KeepPrefix | null;
}

/** 태그 필터 선택지 — 백엔드 TagDto (GET /api/tags?contentType=PROSPECTIVE_KEEP) 정규화 */
export interface KeepTagOption {
  id: number;
  name: string;
  ctgr: string | null;
  /** 이 태그가 붙은 Keep 수 (서버 집계) */
  contentSize: number;
}

/** 말머리 필터 선택지 — 백엔드 PrefixDto (GET /api/my/prefixes/options?contentType=PROSPECTIVE_KEEP) 정규화 */
export interface KeepPrefixOption {
  id: number;
  name: string;
  sortOrder: number;
  /** 렌더용 색 클래스 (서버 PrefixDto.textClass, 예: text-primary). 없으면 기본색 */
  textClass?: string | null;
}

// ─── 라이프사이클 ─────────────────────────────────────────

/** Keep 라이프사이클 6종 (백엔드 LifecycleKey, KEEP 정책 전부 허용) */
export type KeepLifecycleKey = "OPEN" | "PENDING" | "RESOLVED" | "FAILED" | "EXPIRED" | "DROPPED";

/** 종결 4종 (백엔드 LifecycleKey.isTerminal) — 진행 투영에서 제외 */
export const KEEP_TERMINAL_KEYS: readonly string[] = ["RESOLVED", "FAILED", "EXPIRED", "DROPPED"];

export type KeepLifecycleMeta = {
  label: string;
  color: string;
  background: string;
  /** 종결 행 왼쪽 아이콘 (웹 terminalIconClass: 체크·X·모래시계·슬래시) */
  icon: string;
};

/**
 * 라이프사이클 배지 — 웹 KeepPage terminalBadge(badge-light-success/danger/warning/light)와 같은 의미 색.
 * OPEN 문구는 웹 journal.entry.lifecycle.open 과 같은 `진행 중`(모바일 저널 배지와 같은 값).
 */
export const KEEP_LIFECYCLE_META: Record<KeepLifecycleKey, KeepLifecycleMeta> = {
  OPEN: { label: "진행 중", color: colors.secondaryText, background: colors.surface, icon: "" },
  PENDING: { label: "보류", color: "#B7791F", background: "#FFF4DE", icon: "" },
  RESOLVED: { label: "완료", color: "#2F855A", background: "#E6F4EA", icon: "✓" },
  FAILED: { label: "실패", color: "#C53030", background: "#FDECEF", icon: "✕" },
  EXPIRED: { label: "만료", color: "#B7791F", background: "#FFF4DE", icon: "⌛" },
  DROPPED: { label: "내려놓음", color: colors.muted, background: colors.surface, icon: "⊘" }
};

/** 항목의 현재 라이프사이클 키. 저장 row 가 없으면 OPEN (웹 lifecycleKeyOf) */
export function keepLifecycleKeyOf(item: Pick<KeepItem, "lifecycle"> | null | undefined): string {
  const key = item?.lifecycle?.lifecycleKey?.trim();
  return key ? key : "OPEN";
}

/** 라이프사이클 배지 정보. 모르는 키는 키 이름 그대로 회색 배지 */
export function keepLifecycleMeta(key: string): KeepLifecycleMeta {
  return (KEEP_LIFECYCLE_META as Record<string, KeepLifecycleMeta | undefined>)[key]
    ?? { label: key, color: colors.secondaryText, background: colors.surface, icon: "" };
}

/** 종결(RESOLVED·FAILED·EXPIRED·DROPPED) 여부 */
export function isKeepTerminal(item: Pick<KeepItem, "lifecycle"> | null | undefined): boolean {
  return KEEP_TERMINAL_KEYS.includes(keepLifecycleKeyOf(item));
}

// ─── 상태 (다중 속성) ─────────────────────────────────────

/** Keep 상태 키 (백엔드 AttachableContentStatePolicy: PROSPECTIVE_KEEP → DEMANDED, IMPRTC). 그 외(레거시 COLLAPSED 등)는 무시 */
export type KeepStateKey = "IMPRTC" | "DEMANDED";
export const KEEP_STATE_KEYS: readonly KeepStateKey[] = ["IMPRTC", "DEMANDED"];

export const KEEP_STATE_META: Record<KeepStateKey, { label: string; color: string; background: string }> = {
  IMPRTC: { label: "중요", color: "#B7791F", background: "#FFF4DE" },
  DEMANDED: { label: "요구", color: "#C53030", background: "#FDECEF" }
};

/** 항목이 해당 상태를 갖고 있는지 (웹 hasState) */
export function keepHasState(item: Pick<KeepItem, "state"> | null | undefined, stateKey: KeepStateKey): boolean {
  return (item?.state?.list ?? []).some((st) => st?.stateKey === stateKey);
}

/** 정책에 있는 상태 키만, 고정 순서(중요 → 요구)로 */
export function keepStatesOf(item: Pick<KeepItem, "state"> | null | undefined): KeepStateKey[] {
  return KEEP_STATE_KEYS.filter((key) => keepHasState(item, key));
}

// ─── Project ─────────────────────────────────────────────

/** Project 라이프사이클. 저장 row 가 없으면 OPEN (웹 prospectiveProject lifecycleKeyOf) */
export function keepProjectLifecycleKeyOf(project: KeepProject): string {
  return project.lifecycle?.lifecycleKey?.trim() || "OPEN";
}

/** 웹 Project 탭과 같이 OPEN Project 만, 서버 순서(sortOrder → id) 그대로 */
export function openKeepProjects(projects: KeepProject[]): KeepProject[] {
  return projects.filter((p) => keepProjectLifecycleKeyOf(p) === "OPEN");
}

/** Project 말머리 표기. 없으면 빈 문자열. 예: "[게임]" */
export function keepProjectPrefixLabel(project: KeepProject | null | undefined): string {
  const name = project?.prefix?.name?.trim();
  return name ? `[${name}]` : "";
}

/**
 * 표시용 Project 이름 — 말머리를 공백 없이 앞에 붙인다. 예: "[게임]왕국건설스토리" (웹 projectDisplayTitle 과 같은 규칙).
 * 칩·필터·빠른 추가·행 📁 표기·상세·복사 텍스트가 모두 이 함수를 쓴다.
 */
export function keepProjectTitle(project: KeepProject | null | undefined): string {
  if (!project) return "";
  const name = project.title?.trim() || `Project #${project.id}`;
  return `${keepProjectPrefixLabel(project)}${name}`;
}

/**
 * 색 지정이 없는 Keep 말머리의 기본 색. 웹 KeepPage.vue 는 `:class="prefix.textClass"` + 인라인 `#287D94`를 함께 주는데,
 * text-* 클래스가 `!important`라 색을 고른 말머리는 그 색으로, 고르지 않은(DEFAULT → 빈 클래스) 말머리만 #287D94 로 보인다.
 */
export const KEEP_PREFIX_COLOR = "#287D94";

/**
 * Keep 말머리 표시 색 — 목록·상세·빠른 추가/수정 칩·필터 칩이 함께 쓴다.
 * 서버 PrefixDto.textClass(`text-primary`·`text-emotion` 등)를 웹과 같은 색으로 바꾸고, 없거나 모르는 클래스면 KEEP_PREFIX_COLOR.
 */
export function keepPrefixColor(textClass: string | null | undefined): string {
  return colorFromTextClass(textClass) ?? KEEP_PREFIX_COLOR;
}
