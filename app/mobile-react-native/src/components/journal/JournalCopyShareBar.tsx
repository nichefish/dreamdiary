import { Alert, Platform, Pressable, Share, StyleSheet, Text, ToastAndroid, View } from "react-native";
import * as Clipboard from "expo-clipboard";
import { colors } from "../../theme/colors";

/** 짧은 결과 알림 — Android 토스트, iOS 는 알림창 */
export function notifyBrief(message: string) {
  if (Platform.OS === "android") ToastAndroid.show(message, ToastAndroid.SHORT);
  else Alert.alert(message);
}

/** 텍스트를 클립보드에 복사하고 결과를 알린다 */
export async function copyTextToClipboard(text: string, successMessage = "복사했습니다.") {
  if (!text.trim()) {
    notifyBrief("복사할 내용이 없습니다.");
    return;
  }
  try {
    await Clipboard.setStringAsync(text);
    notifyBrief(successMessage);
  } catch (e) {
    console.error("[journal-copy] clipboard copy failed", e);
    notifyBrief("복사하지 못했습니다.");
  }
}

/** 시스템 공유 시트로 텍스트를 내보낸다 (메신저·메모·파일 저장 등) */
export async function shareText(text: string, title: string) {
  if (!text.trim()) {
    notifyBrief("공유할 내용이 없습니다.");
    return;
  }
  try {
    await Share.share({ message: text, title }, { dialogTitle: title, subject: title });
  } catch (e) {
    console.error("[journal-share] share failed", e);
    notifyBrief("공유하지 못했습니다.");
  }
}

export type JournalCopyShareBarProps = {
  /** 왼쪽 라벨 (예: "이 날 기록") */
  label: string;
  /** 누를 때 복사·공유할 텍스트를 만든다 */
  buildText: () => string;
  /** 공유 시트 제목 */
  shareTitle: string;
  /** 성공 토스트 문구 */
  copiedMessage?: string;
};

/** 기간(일·주·월) 기록 복사·공유 버튼 줄 — 읽기 전용 */
export function JournalCopyShareBar({ label, buildText, shareTitle, copiedMessage }: JournalCopyShareBarProps) {
  return (
    <View style={styles.bar}>
      <Text style={styles.label}>{label}</Text>
      <View style={styles.actions}>
        <Pressable
          accessibilityRole="button"
          accessibilityLabel={`${label} 복사`}
          onPress={() => { void copyTextToClipboard(buildText(), copiedMessage); }}
          hitSlop={6}
          style={styles.button}
        >
          <Text style={styles.buttonText}>복사</Text>
        </Pressable>
        <Pressable
          accessibilityRole="button"
          accessibilityLabel={`${label} 공유·내보내기`}
          onPress={() => { void shareText(buildText(), shareTitle); }}
          hitSlop={6}
          style={styles.button}
        >
          <Text style={styles.buttonText}>공유</Text>
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  bar: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", paddingHorizontal: 2 },
  label: { color: colors.muted, fontSize: 12, fontWeight: "700" },
  actions: { flexDirection: "row", gap: 6 },
  button: {
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 14,
    paddingHorizontal: 12,
    paddingVertical: 4,
    backgroundColor: colors.input
  },
  buttonText: { color: colors.accent, fontSize: 12, fontWeight: "700" }
});