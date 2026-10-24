import type { TextStyle } from "react-native";
import { colors } from "../../theme/colors";

/**
 * 태그 표시 색·크기 토큰 — 웹(frontend-vue) 태그 스타일을 모바일 크기에 맞춰 옮긴 값.
 * - 인라인 태그(TagChip.vue): `#[카테고리]태그명`, 카테고리는 text-noti(주황) 작은 글씨
 * - 태그 클라우드(TagCloud.vue + styles/components/tag.scss): 크기 ts-1~ts-9, 색 = textClass 우선, 없으면 ts 스케일 색
 */

/** 웹 $color-map "noti" — 태그 카테고리 `[ctgr]`·건수 표기 색 */
export const TAG_CTGR_COLOR = "#ED7000";

/** 웹 tag.scss --tag-scale-color-1..5 */
const SCALE_COLORS = ["#9CA5AD", "#7C858D", "#5C75AD", "#5C8FB0", "#0D6EFD"] as const;

/**
 * ts-1..ts-9 → 모바일 글자 크기·색.
 * 웹은 0.9em~2.7em 이지만 폰 폭에서 한 줄에 여러 태그가 들어가도록 12~24pt 로 압축했다.
 */
const TAG_SCALE: Record<number, { fontSize: number; color: string; bold?: boolean }> = {
  1: { fontSize: 12, color: SCALE_COLORS[0] },
  2: { fontSize: 13.5, color: SCALE_COLORS[1] },
  3: { fontSize: 15, color: SCALE_COLORS[2] },
  4: { fontSize: 16.5, color: SCALE_COLORS[3] },
  5: { fontSize: 18, color: SCALE_COLORS[3] },
  6: { fontSize: 19.5, color: SCALE_COLORS[3] },
  7: { fontSize: 21, color: SCALE_COLORS[4] },
  8: { fontSize: 22.5, color: SCALE_COLORS[4] },
  9: { fontSize: 24, color: SCALE_COLORS[4], bold: true }
};

/**
 * 서버 TextClass.toCssClass() 결과(text-<code>) → 색.
 * Bootstrap/Metronic 8.2.1 기본 팔레트 + 웹 commons.scss·_variables.scss 커스텀 색.
 */
const TEXT_CLASS_COLORS: Record<string, string> = {
  "text-primary": "#1B84FF",
  "text-success": "#17C653",
  "text-info": "#7239EA",
  "text-warning": "#F6B100",
  "text-danger": "#F8285A",
  "text-alert": "#F8285A",
  "text-secondary": "#4B5675",
  "text-dark": "#071437",
  "text-muted": "#99A1B7",
  "text-dialog": "#5C8FB0",
  "text-noti": "#ED7000",
  "text-emotion": "#EC4899",
  "text-burnt": "#92400E"
};

/** textClass(공백 구분 가능) 중 아는 색 클래스를 찾아 색을 반환. 없으면 null. */
export function colorFromTextClass(textClass: string | null | undefined): string | null {
  if (!textClass) return null;
  for (const cls of textClass.split(/\s+/)) {
    const color = TEXT_CLASS_COLORS[cls.trim().toLowerCase()];
    if (color) return color;
  }
  return null;
}

/** tagClass("ts-N") → 1..9. 비정상이면 null. */
function scaleOf(tagClass: string | null | undefined): number | null {
  const match = /(?:^|\s)ts-([1-9])(?:\s|$)/.exec(tagClass ?? "");
  return match ? Number(match[1]) : null;
}

/**
 * 태그 클라우드 한 항목의 이름 스타일. 웹과 같이 크기는 tagClass, 색은 textClass 가 있으면 우선한다.
 * @param tagClass 서버 빈도 크기 클래스 (ts-1~ts-9)
 * @param textClass 서버 태그 프로필 색 클래스
 */
export function cloudTagTextStyle(tagClass?: string | null, textClass?: string | null): TextStyle {
  const scale = TAG_SCALE[scaleOf(tagClass) ?? 1];
  return {
    fontSize: scale.fontSize,
    color: colorFromTextClass(textClass) ?? scale.color,
    fontWeight: scale.bold ? "700" : "400"
  };
}

/** 일자·일기·꿈 인라인 태그 칩 색 (종류별로 은은하게 구분) */
export const TAG_CHIP_PALETTE = {
  day: { background: colors.surface, border: colors.surface, name: colors.accent },
  diary: { background: colors.input, border: colors.border, name: colors.accent },
  dream: { background: "#FBF7FD", border: "#D7BDE2", name: "#6C3483" }
} as const;