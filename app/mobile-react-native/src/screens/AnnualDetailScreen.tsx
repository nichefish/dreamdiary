import { useCallback, useEffect, useRef, useState } from "react";
import { ActivityIndicator, Pressable, RefreshControl, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { getJournalAnnual } from "../api/dreamDiaryApi";
import { isUnauthorizedError } from "../api/client";
import type { RootStackParamList } from "../navigation/AppNavigator";
import { MENU_URL } from "../navigation/appMenu";
import { colors } from "../theme/colors";
import { displayTagsOf } from "../types/journalDay";
import type { JournalAnnual, JournalAnnualReview } from "../types/journalAnnual";
import { htmlToPlainText } from "../utils/journalCopy";
import { MenuScreenHeader } from "../components/navigation/AppMenu";
import { JournalTagChips } from "../components/journal/JournalTagChips";
import { annualTitle, dreamStatLabel } from "./AnnualListScreen";

type Props = NativeStackScreenProps<RootStackParamList, "AnnualDetail">;

function ReviewCard({ review }: { review: JournalAnnualReview }) {
  const title = review.title?.trim() ?? "";
  const body = htmlToPlainText(review.markdownContent || review.content).trim();
  return (
    <View style={styles.reviewCard}>
      {title ? <Text style={styles.reviewTitle}>{title}</Text> : null}
      <Text style={styles.bodyText}>{body || "(내용 없음)"}</Text>
    </View>
  );
}

/**
 * 저널 결산 상세 — 웹 결산 상세(JournalAnnualDetail)의 `요약`·`리뷰` 읽기 부분.
 * GET /api/journal/annual/{yy} (꿈 집계·본문·태그·journalAnnualReviewList).
 * 웹의 일기 결산·꿈 결산 탭(연도별 일기/꿈 목록, 태그 집계)과 등록·수정은 아직 옮기지 않았다.
 */
export function AnnualDetailScreen({ route }: Props) {
  const { yy, title: routeTitle } = route.params;
  const [annual, setAnnual] = useState<JournalAnnual | null>(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const seqRef = useRef(0);

  const load = useCallback(async (silent = false) => {
    const seq = ++seqRef.current;
    if (!silent) setLoading(true);
    setError(null);
    try {
      const res = await getJournalAnnual(yy);
      if (seq !== seqRef.current) return;
      setAnnual(res);
      if (res == null) setError("결산 데이터를 불러올 수 없습니다.");
    } catch (e) {
      if (seq !== seqRef.current) return;
      if (!isUnauthorizedError(e)) console.error("[AnnualDetailScreen] annual detail load failed", { yy }, e);
      setError("결산 데이터를 불러올 수 없습니다.");
    } finally {
      if (seq === seqRef.current) {
        setLoading(false);
        setRefreshing(false);
      }
    }
  }, [yy]);

  useEffect(() => {
    void load();
  }, [load]);

  function onRefresh() {
    setRefreshing(true);
    void load(true);
  }

  const title = annual ? annualTitle(annual, yy) : routeTitle?.trim() || annualTitle(null, yy);
  const body = htmlToPlainText(annual?.markdownContent || annual?.content).trim();
  const tags = displayTagsOf(annual?.tag);
  const reviews = (annual?.journalAnnualReviewList ?? []).filter((r): r is JournalAnnualReview => r != null);

  return (
    <SafeAreaView edges={["top", "left", "right", "bottom"]} style={styles.safeArea}>
      <MenuScreenHeader title={title} currentUrl={MENU_URL.JOURNAL_ANNUAL} />
      <ScrollView
        contentContainerStyle={styles.container}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
      >
        {loading && !refreshing ? (
          <ActivityIndicator style={styles.loader} color={colors.accent} />
        ) : error != null || annual == null ? (
          <View style={styles.stateBox}>
            <Text style={styles.errorText}>{error ?? "결산 데이터를 불러올 수 없습니다."}</Text>
            <Pressable accessibilityRole="button" onPress={() => { void load(); }} style={styles.retryButton}>
              <Text style={styles.retryText}>다시 시도</Text>
            </Pressable>
          </View>
        ) : (
          <>
            <View style={styles.card}>
              <Text style={styles.sectionLabel}>요약</Text>
              <View style={styles.statRow}>
                <Text style={styles.statLabel}>꿈</Text>
                <Text style={styles.statValue}>{dreamStatLabel(annual)}</Text>
                {annual.dreamComptYn === "Y" ? <Text style={styles.completed}>🌙✓ 꿈 기록 완료</Text> : null}
              </View>
              {body ? <Text style={styles.bodyText}>{body}</Text> : <Text style={styles.emptyText}>본문이 없습니다.</Text>}
              {tags.length > 0 ? <JournalTagChips tags={tags} variant="day" pressable={false} /> : null}
            </View>

            {reviews.length > 0 ? (
              <View style={styles.reviewSection}>
                <Text style={styles.sectionLabel}>{`리뷰 ${reviews.length}`}</Text>
                {reviews.map((review, index) => (
                  <ReviewCard key={review.id ?? `r${index}`} review={review} />
                ))}
              </View>
            ) : null}

            <Text style={styles.noteText}>일기 결산·꿈 결산(연도별 목록·태그 집계)은 웹에서 볼 수 있습니다.</Text>
          </>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: colors.background },
  container: { padding: 16, gap: 14, paddingBottom: 32 },
  card: {
    backgroundColor: colors.input,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 12,
    padding: 14,
    gap: 10
  },
  sectionLabel: { color: colors.muted, fontSize: 12, fontWeight: "800", letterSpacing: 0.3 },
  statRow: { flexDirection: "row", alignItems: "center", gap: 8, flexWrap: "wrap" },
  statLabel: { fontSize: 13, fontWeight: "700", color: colors.secondaryText },
  statValue: { fontSize: 14, fontWeight: "800", color: colors.text },
  completed: { fontSize: 12, fontWeight: "700", color: "#2F855A" },
  bodyText: { fontSize: 14, lineHeight: 21, color: colors.text },
  emptyText: { fontSize: 13, color: colors.muted },
  reviewSection: { gap: 8 },
  reviewCard: {
    backgroundColor: colors.surface,
    borderRadius: 10,
    padding: 12,
    gap: 6
  },
  reviewTitle: { fontSize: 14.5, fontWeight: "700", color: colors.text },
  noteText: { fontSize: 12, color: colors.muted, textAlign: "center" },
  loader: { marginTop: 32 },
  stateBox: { alignItems: "center", gap: 10, paddingVertical: 32 },
  errorText: { color: "#C0392B", fontSize: 14, textAlign: "center" },
  retryButton: {
    borderWidth: 1,
    borderColor: colors.accent,
    borderRadius: 14,
    paddingHorizontal: 14,
    paddingVertical: 6
  },
  retryText: { color: colors.accent, fontWeight: "700" }
});
