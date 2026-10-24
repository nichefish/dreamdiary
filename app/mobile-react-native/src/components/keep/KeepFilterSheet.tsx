import type { ReactNode } from "react";
import { Modal, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { colors } from "../../theme/colors";
import { keepPrefixColor, type KeepPrefixOption, type KeepTagOption } from "../../types/prospectiveKeep";
import type { KeepFilters } from "../../utils/keepProjection";
import { TAG_CTGR_COLOR } from "../journal/journalTagStyle";

type SheetFilters = Pick<KeepFilters, "prefixId" | "tagId" | "overdue" | "important">;

export type KeepFilterSheetProps = {
  visible: boolean;
  onClose: () => void;
  filters: SheetFilters;
  onChange: (patch: Partial<SheetFilters>) => void;
  /** 말머리·태그·기한 지남·중요를 모두 끈다 (Project 범위·검색어·보기는 그대로) */
  onReset: () => void;
  prefixOptions: KeepPrefixOption[];
  tagOptions: KeepTagOption[];
  /** 서버 선택지를 못 받아 목록에서 모은 선택지를 쓰는 중이면 안내 */
  optionNote?: string | null;
};

function Chip({ label, active, onPress, color }: { label: ReactNode; active: boolean; onPress: () => void; color?: string }) {
  return (
    <Pressable
      accessibilityRole="button"
      accessibilityState={{ selected: active }}
      onPress={onPress}
      style={({ pressed }) => [styles.chip, active && styles.chipActive, pressed && styles.pressed]}
    >
      <Text style={[styles.chipText, color != null && !active && { color }, active && styles.chipTextActive]}>{label}</Text>
    </Pressable>
  );
}

/**
 * Keep 필터 시트 — 웹 Keep 필터(말머리·태그·기한 지남·중요)를 하단 시트로. 고르는 즉시 목록에 반영된다(클라이언트 투영).
 * 같은 칩을 다시 누르면 해제한다(웹 태그 필터 토글과 같은 규칙).
 */
export function KeepFilterSheet({
  visible,
  onClose,
  filters,
  onChange,
  onReset,
  prefixOptions,
  tagOptions,
  optionNote
}: KeepFilterSheetProps) {
  const insets = useSafeAreaInsets();
  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={onClose} statusBarTranslucent>
      <View style={styles.root}>
        <Pressable accessibilityRole="button" accessibilityLabel="필터 닫기" onPress={onClose} style={styles.backdrop} />
        <View style={[styles.panel, { paddingBottom: insets.bottom + 12 }]} accessibilityViewIsModal>
          <View style={styles.panelHeader}>
            <Text style={styles.panelTitle} accessibilityRole="header">필터</Text>
            <Pressable accessibilityRole="button" onPress={onReset} hitSlop={8} style={styles.resetButton}>
              <Text style={styles.resetText}>모두 해제</Text>
            </Pressable>
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="필터 닫기"
              onPress={onClose}
              style={({ pressed }) => [styles.doneButton, pressed && styles.pressed]}
            >
              <Text style={styles.doneText}>완료</Text>
            </Pressable>
          </View>
          <ScrollView contentContainerStyle={styles.body}>
            <View style={styles.section}>
              <Text style={styles.sectionLabel}>조건</Text>
              <View style={styles.chipRow}>
                <Chip label="기한 지남" active={filters.overdue} onPress={() => onChange({ overdue: !filters.overdue })} />
                <Chip label="★ 중요" active={filters.important} onPress={() => onChange({ important: !filters.important })} />
              </View>
              <Text style={styles.hint}>기한 지남은 종결되지 않은 채 기한이 지난 항목만 남깁니다.</Text>
            </View>

            <View style={styles.section}>
              <Text style={styles.sectionLabel}>말머리</Text>
              {prefixOptions.length === 0 ? (
                <Text style={styles.hint}>말머리가 없습니다.</Text>
              ) : (
                <View style={styles.chipRow}>
                  <Chip label="전체" active={filters.prefixId == null} onPress={() => onChange({ prefixId: null })} />
                  {prefixOptions.map((p) => (
                    <Chip
                      key={p.id}
                      label={`[${p.name}]`}
                      color={keepPrefixColor(p.textClass)}
                      active={filters.prefixId === p.id}
                      onPress={() => onChange({ prefixId: filters.prefixId === p.id ? null : p.id })}
                    />
                  ))}
                </View>
              )}
            </View>

            <View style={styles.section}>
              <Text style={styles.sectionLabel}>태그</Text>
              {tagOptions.length === 0 ? (
                <Text style={styles.hint}>태그가 없습니다.</Text>
              ) : (
                <View style={styles.chipRow}>
                  <Chip label="전체" active={filters.tagId == null} onPress={() => onChange({ tagId: null })} />
                  {tagOptions.map((t) => (
                    <Chip
                      key={t.id}
                      active={filters.tagId === t.id}
                      onPress={() => onChange({ tagId: filters.tagId === t.id ? null : t.id })}
                      label={
                        <>
                          #{t.ctgr ? <Text style={filters.tagId === t.id ? undefined : styles.ctgr}>[{t.ctgr}]</Text> : null}
                          {t.name}
                          {t.contentSize > 0 ? <Text style={styles.count}> {t.contentSize}</Text> : null}
                        </>
                      }
                    />
                  ))}
                </View>
              )}
            </View>
            {optionNote ? <Text style={styles.hint}>{optionNote}</Text> : null}
          </ScrollView>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1, justifyContent: "flex-end" },
  backdrop: { position: "absolute", top: 0, right: 0, bottom: 0, left: 0, backgroundColor: "rgba(0,0,0,0.35)" },
  panel: {
    maxHeight: "75%",
    backgroundColor: colors.background,
    borderTopLeftRadius: 16,
    borderTopRightRadius: 16,
    paddingTop: 14,
    paddingHorizontal: 16
  },
  panelHeader: { flexDirection: "row", alignItems: "center", gap: 10, paddingBottom: 10 },
  panelTitle: { flex: 1, color: colors.text, fontSize: 17, fontWeight: "800" },
  resetButton: { paddingHorizontal: 4, paddingVertical: 6 },
  resetText: { color: colors.muted, fontSize: 13, fontWeight: "600", textDecorationLine: "underline" },
  doneButton: { backgroundColor: colors.accent, borderRadius: 8, paddingHorizontal: 14, paddingVertical: 7 },
  doneText: { color: colors.onAccent, fontSize: 14, fontWeight: "700" },
  body: { gap: 18, paddingBottom: 8 },
  section: { gap: 8 },
  sectionLabel: { color: colors.muted, fontSize: 12, fontWeight: "800" },
  chipRow: { flexDirection: "row", flexWrap: "wrap", gap: 8 },
  chip: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 18,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface
  },
  chipActive: { backgroundColor: colors.accent, borderColor: colors.accent },
  chipText: { color: colors.secondaryText, fontSize: 13, fontWeight: "600" },
  chipTextActive: { color: colors.onAccent },
  ctgr: { color: TAG_CTGR_COLOR, fontSize: 11 },
  count: { fontSize: 11, fontWeight: "500" },
  hint: { color: colors.muted, fontSize: 12 },
  pressed: { opacity: 0.7 }
});
