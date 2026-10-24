import { colors } from "../../theme/colors";

export type LifecycleMeta = { label: string; color: string; background: string };

/** 스레드·엔트리 라이프사이클 배지 (웹 journal.entry.lifecycle.* 문구: 진행 중 / 보류 / 완료) */
export const LIFECYCLE_META: Record<string, LifecycleMeta> = {
  OPEN: { label: "진행 중", color: colors.secondaryText, background: colors.surface },
  PENDING: { label: "보류", color: "#B7791F", background: "#FFF4DE" },
  RESOLVED: { label: "완료", color: "#2F855A", background: "#E6F4EA" }
};
