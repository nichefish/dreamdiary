import { ActivityIndicator, Modal, Pressable, StyleSheet, Switch, ScrollView, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { colors } from "../../theme/colors";
import { keepLifecycleKeyOf, keepLifecycleMeta, keepHasState, KEEP_STATE_KEYS, KEEP_STATE_META, type KeepItem, type KeepLifecycleKey, type KeepStateKey } from "../../types/prospectiveKeep";
import { normalizeExpireDt } from "../../utils/keepExpire";

const OPTIONS: readonly KeepLifecycleKey[] = ["OPEN", "PENDING", "RESOLVED", "FAILED", "EXPIRED", "DROPPED"];

/**
 * 현재 단계를 표시하고 여섯 라이프사이클을 단일 선택하는 하단 시트. 저장 중 닫기·중복 선택을 잠근다.
 * 중요·요구 스위치는 서버 속성을 표시하며 항목 영역은 화면 높이 85% 이내에서 스크롤한다.
 */
export function KeepLifecycleMenu({ item, saving, error, onClose, onSelect, onToggleState, onEdit }: {
  item: KeepItem | null;
  saving: boolean;
  error: string | null;
  onClose: () => void;
  onSelect: (key: KeepLifecycleKey) => void;
  onToggleState: (key: KeepStateKey) => void;
  /** 수정 시트 열기 (상세를 먼저 받는다) */
  onEdit?: () => void;

}) {
  const insets = useSafeAreaInsets();
  const close = () => { if (!saving) onClose(); };
  const current = keepLifecycleKeyOf(item);
  return <Modal visible={item != null} transparent animationType="slide" onRequestClose={close} statusBarTranslucent>
    <View style={styles.root}>
      <Pressable accessibilityRole="button" accessibilityLabel="상태 메뉴 닫기" disabled={saving} onPress={close} style={styles.backdrop} />
      <View style={[styles.panel, { paddingBottom: insets.bottom + 12 }]} accessibilityViewIsModal>
        <View style={styles.heading}>
          <Text style={styles.headingText}>상태 변경</Text>
          <Pressable accessibilityRole="button" disabled={saving} onPress={close} hitSlop={8}><Text style={styles.close}>닫기</Text></Pressable>
        </View>
        {error ? <Text style={styles.error}>{error}</Text> : null}
        <ScrollView>
        {OPTIONS.map((key) => {
          const selected = key === current;
          const unavailable = key === "EXPIRED" && !normalizeExpireDt(item?.expireDt);
          const meta = keepLifecycleMeta(key);
          return <Pressable key={key} accessibilityRole="radio"
            accessibilityState={{ checked: selected, disabled: saving || selected || unavailable }}
            disabled={saving || selected || unavailable} onPress={() => onSelect(key)} style={styles.option}>
            <Text style={[styles.optionText, { color: unavailable ? colors.muted : meta.color }]}>
              {selected ? "● " : "○ "}{meta.label}{unavailable ? " · 기한 필요" : ""}
            </Text>
          </Pressable>;
        })}
        {KEEP_STATE_KEYS.map((key) => <View key={key} style={styles.attribute}>
          <Text style={styles.optionText}>{KEEP_STATE_META[key].label}</Text>
          <Switch accessibilityLabel={KEEP_STATE_META[key].label}
            value={keepHasState(item, key)} disabled={saving}
            onValueChange={() => { if (!saving) onToggleState(key); }} />
        </View>)}
        {onEdit ? <View style={styles.actions}>
          {onEdit ? <Pressable accessibilityRole="button" accessibilityLabel="Keep 수정" disabled={saving} onPress={onEdit} style={styles.option}>
            <Text style={styles.optionText}>✎ 수정</Text>
          </Pressable> : null}

        </View> : null}
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
  heading: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginBottom: 12 },
  headingText: { color: colors.text, fontSize: 17, fontWeight: "700" },
  close: { color: colors.accent, fontSize: 14 },
  attribute: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", paddingVertical: 8 },
  option: { paddingVertical: 13 },
  optionText: { fontSize: 15 },
  actions: { marginTop: 8, borderTopWidth: 1, borderTopColor: colors.border },
  error: { color: "#C0392B", marginBottom: 8 }
});
