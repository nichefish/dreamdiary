import { useEffect, useState } from "react";
import {
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import type { RootStackParamList } from "../navigation/AppNavigator";
import { getJournalEntryThreads } from "../api/dreamDiaryApi";
import { isUnauthorizedError } from "../api/client";
import { colors } from "../theme/colors";
import { stripHtml } from "../utils/text";
import { displayTagsOf, type JournalThreadEntry } from "../types/journalDay";
import { JournalTagChips, JournalThreadChips } from "../components/journal/JournalTagChips";
import { copyTextToClipboard, shareText } from "../components/journal/JournalCopyShareBar";
import { buildEntryCopyText } from "../utils/journalCopy";

type Props = NativeStackScreenProps<RootStackParamList, "EntryDetail">;

const CONTENT_TYPE_LABELS: Record<string, string> = {
  JOURNAL_DREAM: "꿈",
  JOURNAL_DIARY: "일기",
  JOURNAL_NOTE: "노트"
};

export function EntryDetailScreen({ route, navigation }: Props) {
  const { entry, isDream, dayTags = [], loadThreads = false } = route.params;
  const entryTags = displayTagsOf(entry.tag);
  // 소속 스레드: 기본은 파라미터의 entry.threadList. 스레드 글 목록에서 들어오면(loadThreads) 서버에서 다시 읽는다
  // (스레드 소속 엔트리 API 응답에는 threadList 가 비어 오기 때문 — 그 전까지는 진입한 스레드만 보인다).
  const [threadList, setThreadList] = useState<JournalThreadEntry[]>(entry.threadList ?? []);
  useEffect(() => {
    if (!loadThreads || entry.id == null) return;
    let alive = true;
    getJournalEntryThreads(entry.id)
      .then((list) => {
        if (alive) setThreadList(list);
      })
      .catch((e) => {
        if (!isUnauthorizedError(e)) console.warn("[EntryDetailScreen] entry threads load failed", { entryId: entry.id }, e);
      });
    return () => {
      alive = false;
    };
  }, [entry.id, loadThreads]);
  const hasThreads = threadList.some((t) => t != null && !!t.threadTitle?.trim());
  const hasMeta = entryTags.length > 0 || hasThreads || dayTags.length > 0;
  const body = entry.content ? stripHtml(entry.content) : "";
  const typeLabel = CONTENT_TYPE_LABELS[entry.contentType] ?? entry.contentType;
  // stdrdDt가 있으면 "YYYY.MM.DD" 형태로 변환해 날짜 표시
  const dateLabel = entry.stdrdDt ? entry.stdrdDt.replace(/-/g, ".") : null;

  return (
    <SafeAreaView style={[styles.safeArea, isDream && styles.safeAreaDream]}>
      {/* 커스텀 헤더 */}
      <View style={[styles.header, isDream && styles.headerDream]}>
        <Pressable
          accessibilityRole="button"
          accessibilityLabel="뒤로 가기"
          onPress={() => navigation.goBack()}
          style={styles.backButton}
        >
          <Text style={[styles.backArrow, isDream && styles.backArrowDream]}>‹</Text>
          <Text style={[styles.backLabel, isDream && styles.backLabelDream]}>뒤로</Text>
        </Pressable>

        <View style={styles.typeBadgeWrap}>
          <View style={[styles.typeBadge, isDream && styles.typeBadgeDream]}>
            <Text style={[styles.typeBadgeText, isDream && styles.typeBadgeTextDream]}>
              {typeLabel}
            </Text>
          </View>
        </View>

        {/* 복사·공유 (웹 엔트리 복사 포맷: 날짜 (요일) + 본문 + 해석) */}
        <View style={styles.headerActions}>
          <Pressable
            accessibilityRole="button"
            accessibilityLabel="엔트리 복사"
            onPress={() => { void copyTextToClipboard(buildEntryCopyText(entry)); }}
            hitSlop={6}
            style={[styles.headerAction, isDream && styles.headerActionDream]}
          >
            <Text style={[styles.headerActionText, isDream && styles.headerActionTextDream]}>복사</Text>
          </Pressable>
          <Pressable
            accessibilityRole="button"
            accessibilityLabel="엔트리 공유·내보내기"
            onPress={() => { void shareText(buildEntryCopyText(entry), `${dateLabel ?? ""} ${typeLabel}`.trim()); }}
            hitSlop={6}
            style={[styles.headerAction, isDream && styles.headerActionDream]}
          >
            <Text style={[styles.headerActionText, isDream && styles.headerActionTextDream]}>공유</Text>
          </Pressable>
        </View>
      </View>

      <ScrollView contentContainerStyle={styles.container}>
        {/* 기록 날짜 (stdrdDt가 있을 때만 표시) */}
        {dateLabel != null && (
          <Text style={[styles.dateLabel, isDream && styles.dateLabelDream]}>{dateLabel}</Text>
        )}

        {/* 제목 */}
        {entry.title ? (
          <Text style={[styles.title, isDream && styles.titleDream]}>{entry.title}</Text>
        ) : null}

        {/* 본문 */}
        <Text style={[styles.body, isDream && styles.bodyDream]}>
          {body || "(내용 없음)"}
        </Text>

        {/* 태그·스레드 (웹 엔트리 상세와 같이 본문 아래) */}
        {hasMeta && (
          <View style={[styles.meta, isDream && styles.metaDream]}>
            {entryTags.length > 0 && (
              <View style={styles.metaRow}>
                <Text style={[styles.metaLabel, isDream && styles.metaLabelDream]}>
                  {isDream ? "꿈 태그" : "태그"}
                </Text>
                <JournalTagChips tags={entryTags} variant={isDream ? "dream" : "diary"} />
              </View>
            )}
            {hasThreads && (
              <View style={styles.metaRow}>
                <Text style={[styles.metaLabel, isDream && styles.metaLabelDream]}>스레드</Text>
                <JournalThreadChips threads={threadList} />
              </View>
            )}
            {dayTags.length > 0 && (
              <View style={styles.metaRow}>
                <Text style={[styles.metaLabel, isDream && styles.metaLabelDream]}>일자 태그</Text>
                <JournalTagChips tags={dayTags} variant="day" />
              </View>
            )}
          </View>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

// ─── 스타일 ─────────────────────────────────────────────────

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: colors.background },
  safeAreaDream: { backgroundColor: "#F5EEF8" },
  header: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: colors.border,
    backgroundColor: colors.background
  },
  headerDream: { backgroundColor: "#F5EEF8", borderBottomColor: "#D7BDE2" },
  backButton: {
    flexDirection: "row",
    alignItems: "center",
    gap: 2,
    paddingRight: 12
  },
  backArrow: { fontSize: 28, color: colors.accent, lineHeight: 32, fontWeight: "300" },
  backArrowDream: { color: "#8E44AD" },
  backLabel: { color: colors.accent, fontSize: 16, fontWeight: "600" },
  backLabelDream: { color: "#8E44AD" },
  typeBadgeWrap: { flex: 1 },
  typeBadge: {
    alignSelf: "flex-start",
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 12,
    backgroundColor: colors.surface,
    borderWidth: 1,
    borderColor: colors.border
  },
  typeBadgeDream: { backgroundColor: "#E8DAEF", borderColor: "#C39BD3" },
  typeBadgeText: { color: colors.secondaryText, fontSize: 12, fontWeight: "700" },
  typeBadgeTextDream: { color: "#6C3483" },
  headerActions: { flexDirection: "row", gap: 6 },
  headerAction: {
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 14,
    paddingHorizontal: 12,
    paddingVertical: 4
  },
  headerActionDream: { borderColor: "#C39BD3" },
  headerActionText: { color: colors.accent, fontSize: 13, fontWeight: "700" },
  headerActionTextDream: { color: "#8E44AD" },
  // 본문
  container: { padding: 24, gap: 12, flexGrow: 1 },
  dateLabel: { color: colors.muted, fontSize: 12, fontWeight: "500" },
  dateLabelDream: { color: "#9B59B6" },
  title: { color: colors.text, fontSize: 22, fontWeight: "800", lineHeight: 30 },
  titleDream: { color: "#4A235A" },
  body: { color: colors.secondaryText, fontSize: 16, lineHeight: 26 },
  bodyDream: { color: "#6C3483" },
  // 태그·스레드
  meta: {
    marginTop: 8,
    paddingTop: 14,
    borderTopWidth: 1,
    borderTopColor: colors.border,
    gap: 12
  },
  metaDream: { borderTopColor: "#D7BDE2" },
  metaRow: { gap: 6 },
  metaLabel: { color: colors.muted, fontSize: 11.5, fontWeight: "700" },
  metaLabelDream: { color: "#9B59B6" }
});