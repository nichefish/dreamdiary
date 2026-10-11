import { ActivityIndicator, Modal, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { colors } from "../../theme/colors";
import type { KeepMoveOption } from "../../utils/keepManage";

/**
 * Keep Project 이동 하단 시트 — `미지정` + Project 목록, 현재 소속은 `✓ … · 현재`, OPEN 이 아닌 Project 는 흐리게(고를 수 없음).
 * 저장 중에는 닫기·선택을 잠근다.
 */
export function KeepProjectMoveSheet({ visible, title, options, saving, error, onClose, onSelect }: {
  visible: boolean;
  title: string;
  options: KeepMoveOption[];
  saving: boolean;
  error: string | null;
  onClose: () => void;
  onSelect: (option: KeepMoveOption) => void;
}) {
  const insets = useSafeAreaInsets();
  const close = () => { if (!saving) onClose(); };
  return <Modal visible={visible} transparent animationType="slide" onRequestClose={close} statusBarTranslucent>
    <View style={styles.root}>
      <Pressable accessibilityRole="button" accessibilityLabel="Project 이동 닫기" disabled={saving} onPress={close} style={styles.backdrop} />
      <View style={[styles.panel, { paddingBottom: insets.bottom + 12 }]} accessibilityViewIsModal>
        <View style={styles.heading}>
          <Text style={styles.headingText} numberOfLines={1}>Project 이동 · {title}</Text>
          <Pressable accessibilityRole="button" disabled={saving} onPress={close} hitSlop={8}><Text style={styles.close}>닫기</Text></Pressable>
        </View>
        {error ? <Text style={styles.error}>{error}</Text> : null}
        <ScrollView>
          {options.map((option) => {
            const blocked = saving || option.current || option.disabled;
            return <Pressable key={option.id ?? "none"} accessibilityRole="radio"
              accessibilityLabel={`${option.label}${option.current ? ", 현재" : ""}${option.disabled && !option.current ? ", 진행 중이 아님" : ""}`}
              accessibilityState={{ checked: option.current, disabled: blocked }}
              disabled={blocked} onPress={() => onSelect(option)} style={styles.option}>
              <Text style={[styles.optionText, option.current && styles.current, option.disabled && !option.current && styles.disabled]} numberOfLines={1}>
                {option.current ? "✓ " : "   "}{option.label}{option.current ? " · 현재" : ""}{option.disabled && !option.current ? " · 진행 중 아님" : ""}
              </Text>
            </Pressable>;
          })}
        </ScrollView>
        {saving ? <ActivityIndicator color={colors.accent} /> : null}
      </View>
    </View>
  </Modal>;
}

const styles = StyleSheet.create({
  root: { flex: 1, justifyContent: "flex-end" },
  backdrop: { position: "absolute", top: 0, right: 0, bottom: 0, left: 0, backgroundColor: "rgba(0,0,0,0.3)" },
  panel: { maxHeight: "85%", backgroundColor: colors.background, padding: 18, borderTopLeftRadius: 18, borderTopRightRadius: 18 },
  heading: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginBottom: 12, gap: 12 },
  headingText: { flex: 1, color: colors.text, fontSize: 17, fontWeight: "700" },
  close: { color: colors.accent, fontSize: 14 },
  option: { paddingVertical: 13 },
  optionText: { fontSize: 15, color: colors.text },
  current: { color: colors.accent, fontWeight: "700" },
  disabled: { color: colors.muted },
  error: { color: "#C0392B", marginBottom: 8 }
});
