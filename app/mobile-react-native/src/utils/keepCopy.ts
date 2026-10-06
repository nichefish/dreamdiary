import { displayTagsOf, type DisplayTag } from "../types/journalDay";
import {
  isKeepTerminal,
  KEEP_STATE_META,
  keepLifecycleKeyOf,
  keepLifecycleMeta,
  keepStatesOf,
  type KeepItem
} from "../types/prospectiveKeep";
import { JOURNAL_COPY_LINE_BREAK } from "./journalCopy";
import { daysUntilExpire, ddayLabel, formatExpireDt, normalizeExpireDt } from "./keepExpire";
import { formatKeepDateTime } from "./keepProjection";
import { markdownToPlainText } from "./markdownText";

/**
 * Keep 복사·공유 텍스트 — 웹에는 Keep 복사 기능이 없어, 1단계 저널 복사와 같은 줄바꿈(CRLF)·블록 사이 빈 줄 규칙으로 새로 정했다.
 * - 1건(상세): `[말머리] 제목` → 상태·기한·일시·위치·Project·태그·등록 줄 → 빈 줄 → 본문(마크다운 평문화)
 * - 목록: 머리줄(보기·Project·필터·건수) → (기한 보기면 묶음 머리) → 항목마다 `- ★ [말머리] 제목 · 배지` + 들여쓴 요약 줄. 본문은 넣지 않는다.
 */

const BR = JOURNAL_COPY_LINE_BREAK;

function tagText(tags: DisplayTag[]): string {
  return tags.map((t) => `#${t.ctgr ? `[${t.ctgr}]` : ""}${t.name}`).join(" ");
}

/** `[말머리] 제목` (중요면 앞에 ★) */
export function keepTitleLine(item: KeepItem, withStar = true): string {
  const prefix = item.prefix?.name?.trim();
  const title = item.title?.trim() || "(제목 없음)";
  const star = withStar && keepStatesOf(item).includes("IMPRTC") ? "★ " : "";
  return `${star}${prefix ? `[${prefix}] ` : ""}${title}`;
}

/** 기한 표기 — 진행 항목은 D-day 를 붙이고, 종결 항목은 날짜만 (웹 expireLabel 규칙) */
export function keepExpireText(item: KeepItem, today: Date, style: "short" | "full"): string {
  const date = normalizeExpireDt(item.expireDt);
  if (!date) return "";
  const shown = style === "short" ? formatExpireDt(date, today) : date;
  if (isKeepTerminal(item)) return shown;
  const dday = ddayLabel(daysUntilExpire(date, today));
  return dday ? `${shown} (${dday})` : shown;
}

/** 상세 1건 복사 텍스트 */
export function buildKeepCopyText(item: KeepItem, projectTitle: string, today: Date = new Date()): string {
  const status = [
    keepLifecycleMeta(keepLifecycleKeyOf(item)).label,
    ...keepStatesOf(item).map((key) => KEEP_STATE_META[key].label)
  ].join(" · ");
  const tags = displayTagsOf(item.tag);
  const metaLines = [
    `상태: ${status}`,
    keepExpireText(item, today, "full") ? `기한: ${keepExpireText(item, today, "full")}` : "",
    item.dateText?.trim() ? `일시: ${item.dateText.trim()}` : "",
    item.location?.trim() ? `위치: ${item.location.trim()}` : "",
    projectTitle ? `Project: ${projectTitle}` : "",
    tags.length > 0 ? `태그: ${tagText(tags)}` : "",
    item.createdAt ? `등록: ${formatKeepDateTime(item.createdAt)}` : ""
  ].filter(Boolean);
  const body = markdownToPlainText(item.content).replace(/\n/g, BR);
  return [[keepTitleLine(item), ...metaLines].join(BR), body].filter(Boolean).join(`${BR}${BR}`);
}

/** 목록 복사 묶음 — label 이 있으면 묶음 머리(`[지남]`)를 쓴다 */
export type KeepCopySection = { label?: string; items: KeepItem[] };

/** 현재 화면 목록 복사 텍스트 (화면 순서 그대로) */
export function buildKeepListCopyText(
  headerLines: string[],
  sections: KeepCopySection[],
  projectTitleOf: (projectId: number | null | undefined) => string,
  today: Date = new Date()
): string {
  const blocks = sections
    .filter((section) => section.items.length > 0)
    .map((section) => {
      const lines: string[] = section.label ? [`[${section.label}]`] : [];
      for (const item of section.items) {
        const key = keepLifecycleKeyOf(item);
        const badges = [
          key !== "OPEN" ? keepLifecycleMeta(key).label : "",
          keepStatesOf(item).includes("DEMANDED") ? KEEP_STATE_META.DEMANDED.label : ""
        ].filter(Boolean);
        lines.push(`- ${keepTitleLine(item)}${badges.length > 0 ? ` · ${badges.join(" · ")}` : ""}`);
        const tags = displayTagsOf(item.tag);
        const meta = [
          keepExpireText(item, today, "short") ? `기한 ${keepExpireText(item, today, "short")}` : "",
          item.dateText?.trim() ? `일시 ${item.dateText.trim()}` : "",
          projectTitleOf(item.projectId) ? `Project ${projectTitleOf(item.projectId)}` : "",
          tags.length > 0 ? tagText(tags) : ""
        ].filter(Boolean);
        if (meta.length > 0) lines.push(`  ${meta.join(" · ")}`);
      }
      return lines.join(BR);
    });
  const header = headerLines.map((line) => line.trim()).filter(Boolean).join(BR);
  return [header, ...blocks].filter(Boolean).join(`${BR}${BR}`).trim();
}
