import { colors } from "../theme/colors";
import type { JournalTagCmpstn } from "./journalDay";

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
}

/**
 * 백엔드 ProspectiveKeepDto (BaseAttachableDto → BaseAuditDto → BaseAuditRegDto) 중 화면이 쓰는 필드.
 * - 목록(GET /api/prospective/keeps?scope=all)은 lifecycle 을 붙여 준다.
 * - 상세(GET /api/prospective/keep/{id})는 lifecycle 을 붙이지 않는다(서비스가 applyKeepLifecycles 를 목록에서만 호출).
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

/** 표시용 Project 이름 */
export function keepProjectTitle(project: KeepProject | null | undefined): string {
  return project?.title?.trim() || (project ? `Project #${project.id}` : "");
}

/** 웹 Keep 말머리 표기 색 (KeepPage.vue 가 textClass 대신 인라인 #287D94 로 고정) */
export const KEEP_PREFIX_COLOR = "#287D94";
