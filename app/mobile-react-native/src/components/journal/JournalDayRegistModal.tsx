import { useRef, useState } from "react";
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Modal,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { registerJournalDay } from "../../api/dreamDiaryApi";
import { isUnauthorizedError } from "../../api/client";
import { colors } from "../../theme/colors";
import { formatDateDots } from "../../utils/date";

type JournalDayRegistModalProps = {
  /** 모달을 열 때 확정한 미등록 날짜. */
  date: string;
  onClose: () => void;
  /** 등록 성공 후 선택 날짜의 서버 데이터를 다시 조회한다. */
  onRegistered: () => Promise<void>;
};

/**
 * 미등록 날짜와 선택 날씨 입력을 확인하고 등록하는 모달.
 * 전송 중 입력·닫기를 잠그고 동기 잠금으로 중복 전송을 막는다.
 */
export function JournalDayRegistModal({ date, onClose, onRegistered }: JournalDayRegistModalProps) {
  const [weather, setWeather] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const saving = useRef(false);

  function requestClose() {
    if (saving.current) {
      console.info("[JournalDayRegist] close blocked during save", { date });
      return;
    }
    onClose();
  }

  async function save() {
    if (saving.current) {
      console.info("[JournalDayRegist] duplicate submit blocked", { date });
      return;
    }
    saving.current = true;
    setSubmitting(true);
    setError(null);
    try {
      await registerJournalDay(date, weather);
      console.info("[JournalDayRegist] registered", { date });
      await onRegistered();
    } catch (e) {
      if (!isUnauthorizedError(e)) {
        console.warn("[JournalDayRegist] registration failed", { date });
        setError(e instanceof Error ? e.message : "저널일자를 등록하지 못했습니다.");
      }
    } finally {
      saving.current = false;
      setSubmitting(false);
    }
  }

  return (
    <Modal visible transparent animationType="fade" onRequestClose={requestClose}>
      <SafeAreaView style={styles.overlay}>
        <KeyboardAvoidingView
          style={styles.keyboard}
          behavior={Platform.OS === "ios" ? "padding" : "height"}
        >
          <View style={styles.panel}>
            <ScrollView keyboardShouldPersistTaps="handled" contentContainerStyle={styles.body}>
              <Text accessibilityRole="header" style={styles.title}>저널일자 등록</Text>
              <Text style={styles.label}>날짜</Text>
              <Text style={styles.date}>{formatDateDots(date)}</Text>
              <Text style={styles.label}>날씨 (선택)</Text>
              <TextInput
                accessibilityLabel="날씨 (선택)"
                value={weather}
                onChangeText={setWeather}
                editable={!submitting}
                maxLength={100}
                multiline
                placeholder="날씨를 입력해 주세요."
                placeholderTextColor={colors.muted}
                style={styles.input}
              />
              {error != null && <Text accessibilityRole="alert" style={styles.error}>{error}</Text>}
              <View style={styles.actions}>
                <Pressable accessibilityRole="button" disabled={submitting} onPress={requestClose} style={styles.cancel}>
                  <Text style={styles.cancelText}>취소</Text>
                </Pressable>
                <Pressable
                  accessibilityRole="button"
                  accessibilityState={{ disabled: submitting, busy: submitting }}
                  disabled={submitting}
                  onPress={() => { void save(); }}
                  style={[styles.save, submitting && styles.disabled]}
                >
                  {submitting
                    ? <ActivityIndicator color={colors.onAccent} />
                    : <Text style={styles.saveText}>저장</Text>}
                </Pressable>
              </View>
            </ScrollView>
          </View>
        </KeyboardAvoidingView>
      </SafeAreaView>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: { flex: 1, backgroundColor: "rgba(32, 27, 22, 0.4)" },
  keyboard: { flex: 1, justifyContent: "center", padding: 20 },
  panel: { maxHeight: "90%", borderRadius: 16, backgroundColor: colors.background },
  body: { padding: 20, gap: 12 },
  title: { fontSize: 20, fontWeight: "700", color: colors.text, marginBottom: 4 },
  label: { fontSize: 14, fontWeight: "700", color: colors.secondaryText },
  date: { fontSize: 18, fontWeight: "700", color: colors.text },
  input: { minHeight: 72, padding: 12, borderWidth: 1, borderColor: colors.border, borderRadius: 8, backgroundColor: colors.input, color: colors.text, fontSize: 16, textAlignVertical: "top" },
  error: { color: "#C0392B", fontSize: 14 },
  actions: { flexDirection: "row", justifyContent: "flex-end", gap: 12, marginTop: 4 },
  cancel: { minHeight: 44, paddingHorizontal: 18, justifyContent: "center" },
  cancelText: { color: colors.secondaryText, fontSize: 16, fontWeight: "700" },
  save: { minHeight: 44, minWidth: 80, paddingHorizontal: 18, borderRadius: 8, backgroundColor: colors.accent, alignItems: "center", justifyContent: "center" },
  saveText: { color: colors.onAccent, fontSize: 16, fontWeight: "700" },
  disabled: { opacity: 0.5 }
});
