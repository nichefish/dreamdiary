import { Pressable, StyleSheet, Text, View } from "react-native";
import { colors } from "../../theme/colors";
import type { DisplayTag } from "../../types/journalDay";
import {
  KEEP_PREFIX_COLOR,
  keepPrefixColor,
  KEEP_STATE_META,
  keepLifecycleMeta,
  type KeepStateKey
} from "../../types/prospectiveKeep";
import { TAG_CHIP_PALETTE, TAG_CTGR_COLOR } from "../journal/journalTagStyle";

/** 라이프사이클 배지 (진행 중·보류·완료·실패·만료·내려놓음) */
export function KeepLifecycleBadge({ lifecycleKey }: { lifecycleKey: string }) {
  const meta = keepLifecycleMeta(lifecycleKey);
  return (
    <View style={[styles.badge, { backgroundColor: meta.background }]}>
      <Text style={[styles.badgeText, { color: meta.color }]}>
        {meta.icon ? `${meta.icon} ` : ""}
        {meta.label}
      </Text>
    </View>
  );
}

/** 상태 배지 (중요·요구) */
export function KeepStateBadge({ stateKey }: { stateKey: KeepStateKey }) {
  const meta = KEEP_STATE_META[stateKey];
  return (
    <View style={[styles.badge, { backgroundColor: meta.background }]}>
      <Text style={[styles.badgeText, { color: meta.color }]}>
        {stateKey === "IMPRTC" ? "★ " : ""}
        {meta.label}
      </Text>
    </View>
  );
}

/** 말머리 `[이름]` — 웹 Keep 행과 같이 굵게, 색은 서버 textClass(없으면 청록 #287D94, `keepPrefixColor`) */
export function KeepPrefixText({ name, textClass }: { name: string | null | undefined; textClass?: string | null }) {
  const label = name?.trim();
  if (!label) return null;
  return <Text style={[styles.prefix, { color: keepPrefixColor(textClass) }]}>[{label}]</Text>;
}

export type KeepTagChipsProps = {
  tags: DisplayTag[];
  /** 있으면 칩을 눌러 태그 필터로 쓴다 (목록). 없으면 표시 전용 (상세) */
  onPressTag?: (tag: DisplayTag) => void;
  /** 현재 태그 필터 — 강조 표시 */
  selectedTagId?: number | null;
};

/** Keep 태그 칩 `#[카테고리]이름` — 저널 태그 칩과 같은 모양. 저널 태그별 기록 화면으로는 가지 않는다 */
export function KeepTagChips({ tags, onPressTag, selectedTagId }: KeepTagChipsProps) {
  if (tags.length === 0) return null;
  const palette = TAG_CHIP_PALETTE.diary;
  return (
    <View style={styles.tagRow} accessibilityLabel={`태그: ${tags.map((t) => t.name).join(", ")}`}>
      {tags.map((tag) => {
        const selected = selectedTagId != null && tag.tagId === selectedTagId;
        return (
          <Pressable
            key={`${tag.tagId}:${tag.name}`}
            disabled={!onPressTag}
            accessibilityRole={onPressTag ? "button" : undefined}
            accessibilityLabel={`태그 ${tag.ctgr ? `${tag.ctgr} ` : ""}${tag.name}`}
            accessibilityHint={onPressTag ? (selected ? "태그 필터 해제" : "이 태그로 거르기") : undefined}
            accessibilityState={onPressTag ? { selected } : undefined}
            hitSlop={4}
            onPress={() => onPressTag?.(tag)}
            style={({ pressed }) => [
              styles.chip,
              { backgroundColor: palette.background, borderColor: palette.border },
              selected && styles.chipSelected,
              pressed && styles.pressed
            ]}
          >
            <Text style={styles.chipText}>
              <Text style={styles.hash}>#</Text>
              {tag.ctgr ? <Text style={styles.ctgr}>[{tag.ctgr}]</Text> : null}
              <Text style={[styles.tagName, { color: palette.name }]}>{tag.name}</Text>
            </Text>
          </Pressable>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  badge: { borderRadius: 6, paddingHorizontal: 6, paddingVertical: 1 },
  badgeText: { fontSize: 11, fontWeight: "700" },
  prefix: { color: KEEP_PREFIX_COLOR, fontWeight: "800" },
  tagRow: { flexDirection: "row", flexWrap: "wrap", gap: 6 },
  chip: { borderRadius: 10, borderWidth: 1, paddingHorizontal: 8, paddingVertical: 2 },
  chipSelected: { borderColor: colors.accent, borderWidth: 1.5 },
  chipText: { fontSize: 12 },
  hash: { color: colors.muted, fontWeight: "400" },
  ctgr: { color: TAG_CTGR_COLOR, fontSize: 10.5 },
  tagName: { fontWeight: "500" },
  pressed: { opacity: 0.6 }
});
