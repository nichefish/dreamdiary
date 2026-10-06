import { useState } from "react";
import {
  ActivityIndicator,
  Pressable,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  View
} from "react-native";
import { JournalDatePickerModal } from "./JournalDatePickerModal";
import { JournalDaySection } from "./JournalDaySection";
import { JournalPeriodTagCloudPanel } from "./JournalPeriodTagCloudPanel";
import { JournalPeriodThreadPanel } from "./JournalPeriodThreadPanel";
import { JournalCopyShareBar } from "./JournalCopyShareBar";
import { buildEntriesCopyText, entriesOfDays } from "../../utils/journalCopy";
import { useJournalWeek } from "../../hooks/useJournalWeek";
import { colors } from "../../theme/colors";
import { addDays, formatDateDots, getWeekStartDateStr, toDateStr } from "../../utils/date";

export type JournalWeeklyViewProps = {
  /** 진입 시 기준 일자 (YYYY-MM-DD). 이 날짜가 속한 주로 시작한다. */
  initialDate: string;
};

/**
 * 주별(WEEKLY) 저널 조회 뷰. Today 탭 일/주 토글의 '주' 모드 본문.
 * 월요일 시작 7일 주를 단위로 이동하며, 해당 주의 저널 일자를 날짜 순서로 읽는다.
 * 기간 라벨을 누르면 달력 피커(주 모드)가 열려 먼 주로 바로 이동할 수 있다.
 */
export function JournalWeeklyView({ initialDate }: JournalWeeklyViewProps) {
  const [weekStartDt, setWeekStartDt] = useState<string>(() => getWeekStartDateStr(initialDate));
  const [pickerVisible, setPickerVisible] = useState(false);
  const { days, loading, refreshing, error, hasAny, load, onRefresh } = useJournalWeek(weekStartDt);

  const thisWeekStart = getWeekStartDateStr(toDateStr(new Date()));
  const atThisWeek = weekStartDt === thisWeekStart;
  const weekEndDt = addDays(weekStartDt, 6);
  const rangeLabel = `${formatDateDots(weekStartDt)} – ${formatDateDots(weekEndDt)}`;

  function shiftWeek(delta: number) {
    setWeekStartDt((prev) => addDays(prev, delta * 7));
  }

  function goToThisWeek() {
    if (!atThisWeek) setWeekStartDt(thisWeekStart);
  }

  /** 피커에서 고른 주(월요일 시작)로 이동. 이번 주 이후는 이번 주로 수렴. */
  function handlePickWeek(nextWeekStart: string) {
    const normalized = getWeekStartDateStr(nextWeekStart);
    setWeekStartDt(normalized > thisWeekStart ? thisWeekStart : normalized);
  }

  return (
    <ScrollView
      contentContainerStyle={styles.container}
      refreshControl={
        <RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor={colors.accent} />
      }
    >
      <View style={styles.weekNav}>
        <Pressable
          accessibilityRole="button"
          accessibilityLabel="이전 주"
          onPress={() => shiftWeek(-1)}
          style={styles.weekNavButton}
        >
          <Text style={styles.weekNavArrow}>‹</Text>
        </Pressable>

        <View style={styles.weekLabelWrap}>
          <Pressable
            accessibilityRole="button"
            accessibilityLabel={`${rangeLabel}, 주 선택`}
            onPress={() => setPickerVisible(true)}
            hitSlop={6}
            style={styles.weekLabelButton}
          >
            <Text style={styles.weekLabel}>{rangeLabel}</Text>
            <Text style={styles.weekLabelCaret}>▾</Text>
          </Pressable>
          {!atThisWeek && (
            <Pressable accessibilityRole="button" onPress={goToThisWeek} hitSlop={6}>
              <Text style={styles.weekTodayHint}>이번 주로</Text>
            </Pressable>
          )}
        </View>

        <Pressable
          accessibilityRole="button"
          accessibilityLabel="다음 주"
          disabled={atThisWeek}
          onPress={() => shiftWeek(1)}
          style={[styles.weekNavButton, atThisWeek && styles.weekNavButtonDisabled]}
        >
          <Text style={[styles.weekNavArrow, atThisWeek && styles.weekNavArrowDisabled]}>›</Text>
        </Pressable>
      </View>

      <JournalPeriodTagCloudPanel period={{ weekStartDt }} title="이 주의 태그" />
      <JournalPeriodThreadPanel query={{ viewType: "WEEKLY", weekStartDt }} title="이 주의 스레드" />

      {!loading && error == null && hasAny && (
        <JournalCopyShareBar
          label="이 주 기록"
          buildText={() => buildEntriesCopyText(entriesOfDays(days))}
          shareTitle={`${rangeLabel} 기록`}
        />
      )}

      {loading ? (
        <View style={styles.center}>
          <ActivityIndicator size="large" color={colors.accent} />
        </View>
      ) : error != null ? (
        <View style={styles.center}>
          <Text style={styles.errorText}>{error}</Text>
          <Pressable
            accessibilityRole="button"
            onPress={() => { void load(); }}
            style={styles.retryButton}
          >
            <Text style={styles.retryButtonText}>다시 시도</Text>
          </Pressable>
        </View>
      ) : !hasAny ? (
        <View style={styles.center}>
          <Text style={styles.emptyText}>이번 주 기록이 없습니다.</Text>
        </View>
      ) : (
        <View style={styles.weekList}>
          {days.map((day) => (
            <JournalDaySection key={day.id} day={day} />
          ))}
        </View>
      )}

      <JournalDatePickerModal
        visible={pickerVisible}
        mode="week"
        value={weekStartDt}
        onSelect={handlePickWeek}
        onClose={() => setPickerVisible(false)}
      />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flexGrow: 1, padding: 20, gap: 16, paddingBottom: 24 },
  weekNav: {
    flexDirection: "row",
    alignItems: "center",
    gap: 4
  },
  weekNavButton: {
    paddingHorizontal: 12,
    paddingVertical: 6
  },
  weekNavButtonDisabled: {
    opacity: 0.3
  },
  weekNavArrow: {
    fontSize: 28,
    color: colors.accent,
    lineHeight: 32,
    fontWeight: "300"
  },
  weekNavArrowDisabled: {
    color: colors.muted
  },
  weekLabelWrap: {
    flex: 1,
    alignItems: "center",
    gap: 2
  },
  weekLabelButton: {
    flexDirection: "row",
    alignItems: "center",
    gap: 4
  },
  weekLabel: {
    color: colors.text,
    fontSize: 15,
    fontWeight: "700"
  },
  weekLabelCaret: {
    color: colors.accent,
    fontSize: 11,
    fontWeight: "700"
  },
  weekTodayHint: {
    color: colors.accent,
    fontSize: 11,
    fontWeight: "600"
  },
  center: { alignItems: "center", justifyContent: "center", gap: 12, paddingVertical: 48 },
  errorText: { color: "#C0392B", fontSize: 14 },
  retryButton: { paddingHorizontal: 12, paddingVertical: 7 },
  retryButtonText: { color: colors.accent, fontSize: 13, fontWeight: "700" },
  emptyText: { color: colors.secondaryText, fontSize: 16, fontWeight: "600" },
  weekList: { gap: 20 }
});
