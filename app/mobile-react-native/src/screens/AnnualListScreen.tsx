import { useCallback, useEffect, useRef, useState } from "react";
import { ActivityIndicator, Pressable, RefreshControl, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { getJournalAnnualList, getJournalAnnualTotal } from "../api/dreamDiaryApi";
import { isUnauthorizedError } from "../api/client";
import type { RootStackParamList } from "../navigation/AppNavigator";
import { MENU_URL } from "../navigation/appMenu";
import { colors } from "../theme/colors";
import { displayTagsOf } from "../types/journalDay";
import type { JournalAnnual } from "../types/journalAnnual";
import { htmlToPlainText } from "../utils/journalCopy";
import { MenuScreenHeader } from "../components/navigation/AppMenu";
import { JournalTagChips } from "../components/journal/JournalTagChips";

type Props = NativeStackScreenProps<RootStackParamList, "AnnualList">;

/** 결산 제목 — 웹과 같이 제목이 없으면 `YYYY년 결산` */
export function annualTitle(annual: Pick<JournalAnnual, "title" | "yy"> | null, fallbackYy?: number): string {
  const title = annual?.title?.trim();
  if (title) return title;
  const yy = annual?.yy ?? fallbackYy;
  return yy != null ? `${yy}년 결산` : "결산";
}

/** 꿈 집계 줄 — 웹 `꿈 (N일 / M회)` */
export function dreamStatLabel(annual: Pick<JournalAnnual, "dreamDayCnt" | "dreamCnt">): string {
  return `${annual.dreamDayCnt ?? 0}일 / ${annual.dreamCnt ?? 0}회`;
}

function AnnualCard({ annual, onPress }: { annual: JournalAnnual; onPress: () => void }) {
  const preview = htmlToPlainText(annual.markdownContent || annual.content).replace(/\s+/g, " ").trim();
  const tags = displayTagsOf(annual.tag);
  const completed = annual.dreamComptYn === "Y";
  return (
    <Pressable
      accessibilityRole="button"
      accessibilityHint="결산 상세 열기"
      onPress={onPress}
      style={({ pressed }) => [styles.card, pressed && styles.pressed]}
    >
      <View style={styles.cardHead}>
        <Text style={styles.cardTitle} numberOfLines={1}>
          📅 {annualTitle(annual)}
        </Text>
        <Text style={styles.cardMore}>상세 ›</Text>
      </View>
      <View style={styles.statRow}>
        <Text style={styles.statLabel}>꿈</Text>
        <Text style={styles.statValue}>{dreamStatLabel(annual)}</Text>
        {completed ? (
          <Text style={styles.completed} accessibilityLabel="꿈 기록을 완료했습니다.">
            🌙✓ 완료
          </Text>
        ) : null}
      </View>
      {preview ? (
        <Text style={styles.preview} numberOfLines={4}>
          {preview}
        </Text>
      ) : null}
      {tags.length > 0 ? <JournalTagChips tags={tags} variant="day" pressable={false} /> : null}
    </Pressable>
  );
}

/**
 * 저널 결산 (햄버거 메뉴 `저널 > 저널 결산`) — 웹 결산 목록(JournalAnnualList)의 읽기 부분.
 * - 총 꿈 기록: GET /api/journal/annual/total (`N일 / M회`)
 * - 연도별 결산: GET /api/journal/annuals (서버 순서), 카드를 누르면 결산 상세(AnnualDetail)
 * - 읽기 전용: 결산 등록·전체 갱신·리뷰 등록 없음.
 */
export function AnnualListScreen({ navigation }: Props) {
  const [list, setList] = useState<JournalAnnual[]>([]);
  const [total, setTotal] = useState<JournalAnnual | null>(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const seqRef = useRef(0);

  /** 목록과 총 집계를 따로 조회 — 총 집계 실패는 카드에 `-` 만 표시하고 목록은 그대로 보여준다 */
  const load = useCallback(async (silent = false) => {
    const seq = ++seqRef.current;
    if (!silent) setLoading(true);
    setError(null);
    const [listRes, totalRes] = await Promise.allSettled([getJournalAnnualList(), getJournalAnnualTotal()]);
    if (seq !== seqRef.current) return;
    if (listRes.status === "fulfilled") {
      setList(listRes.value);
    } else {
      if (!isUnauthorizedError(listRes.reason)) console.error("[AnnualListScreen] annual list load failed", listRes.reason);
      setError("결산 목록을 불러오지 못했습니다.");
      setList([]);
    }
    if (totalRes.status === "fulfilled") {
      setTotal(totalRes.value);
    } else {
      if (!isUnauthorizedError(totalRes.reason)) console.warn("[AnnualListScreen] annual total load failed", totalRes.reason);
      setTotal(null);
    }
    setLoading(false);
    setRefreshing(false);
  }, []);

  useEffect(() => {
    void load();
  }, [load]);

  function onRefresh() {
    setRefreshing(true);
    void load(true);
  }

  return (
    <SafeAreaView edges={["top", "left", "right", "bottom"]} style={styles.safeArea}>
      <MenuScreenHeader title="저널 결산" currentUrl={MENU_URL.JOURNAL_ANNUAL} />
      <ScrollView
        contentContainerStyle={styles.container}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
      >
        <View style={styles.totalCard}>
          <Text style={styles.totalLabel}>총 꿈 기록</Text>
          <Text style={styles.totalValue}>{total ? dreamStatLabel(total) : loading ? "…" : "-"}</Text>
        </View>

        {loading && !refreshing ? (
          <ActivityIndicator style={styles.loader} color={colors.accent} />
        ) : error != null ? (
          <View style={styles.stateBox}>
            <Text style={styles.errorText}>{error}</Text>
            <Pressable accessibilityRole="button" onPress={() => { void load(); }} style={styles.retryButton}>
              <Text style={styles.retryText}>다시 시도</Text>
            </Pressable>
          </View>
        ) : list.length === 0 ? (
          <View style={styles.stateBox}>
            <Text style={styles.emptyText}>결산 데이터가 없습니다.</Text>
          </View>
        ) : (
          list.map((annual, index) => {
            const yy = annual.yy;
            return (
              <AnnualCard
                key={yy ?? annual.id ?? `i${index}`}
                annual={annual}
                onPress={() => {
                  if (yy == null) return;
                  navigation.push("AnnualDetail", { yy, title: annualTitle(annual) });
                }}
              />
            );
          })
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: colors.background },
  container: { padding: 16, gap: 12, paddingBottom: 32 },
  totalCard: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    backgroundColor: colors.surface,
    borderRadius: 12,
    paddingHorizontal: 14,
    paddingVertical: 12
  },
  totalLabel: { color: colors.secondaryText, fontSize: 14, fontWeight: "700" },
  totalValue: { color: colors.accent, fontSize: 15, fontWeight: "800" },
  card: {
    backgroundColor: colors.input,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 12,
    padding: 14,
    gap: 8
  },
  cardHead: { flexDirection: "row", alignItems: "center", gap: 8 },
  cardTitle: { flex: 1, fontSize: 17, fontWeight: "800", color: colors.text },
  cardMore: { fontSize: 12.5, color: colors.accent, fontWeight: "600" },
  statRow: { flexDirection: "row", alignItems: "center", gap: 8 },
  statLabel: { fontSize: 13, fontWeight: "700", color: colors.secondaryText },
  statValue: { fontSize: 13.5, fontWeight: "700", color: colors.text },
  completed: { fontSize: 12, fontWeight: "700", color: "#2F855A" },
  preview: { fontSize: 13.5, lineHeight: 20, color: colors.text },
  loader: { marginTop: 32 },
  stateBox: { alignItems: "center", gap: 10, paddingVertical: 32 },
  errorText: { color: "#C0392B", fontSize: 14, textAlign: "center" },
  emptyText: { color: colors.secondaryText, fontSize: 14 },
  retryButton: {
    borderWidth: 1,
    borderColor: colors.accent,
    borderRadius: 14,
    paddingHorizontal: 14,
    paddingVertical: 6
  },
  retryText: { color: colors.accent, fontWeight: "700" },
  pressed: { opacity: 0.7 }
});
