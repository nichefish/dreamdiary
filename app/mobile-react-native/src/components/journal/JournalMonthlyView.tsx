import type { ReactNode } from "react";
import { useMemo, useState } from "react";
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
import { useJournalMonth } from "../../hooks/useJournalMonth";
import { colors } from "../../theme/colors";
import { dayHasEntries } from "../../types/journalDay";

const MONTH_NAMES = ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"];

/** 오늘이 속한 연·월 "YYYY-MM" */
function currentYearMonth(): string {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
}

/** YYYY-MM-DD 등에서 연·월 "YYYY-MM" 추출. 비정상이면 이번 달. */
function toYearMonth(dateStr: string): string {
  return /^\d{4}-\d{2}/.test(dateStr) ? dateStr.slice(0, 7) : currentYearMonth();
}

/** "YYYY-MM" → { year, month } */
function parseYearMonth(ym: string): { year: number; month: number } {
  const [y, m] = ym.split("-").map(Number);
  return { year: y, month: m };
}

/** 연·월을 delta 달만큼 이동한 "YYYY-MM" */
function shiftMonth(ym: string, delta: number): string {
  const { year, month } = parseYearMonth(ym);
  const d = new Date(year, month - 1 + delta, 1);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}`;
}

export type JournalMonthlyViewProps = {
  /** 날짜 이동 행 왼쪽에 표시하는 일·주·월 보기 선택 컨트롤. */
  modeSelector: ReactNode;
  /** 진입 시 기준 일자 (YYYY-MM-DD). 이 날짜가 속한 달로 시작한다. */
  initialDate: string;
};

/**
 * 월별(MONTHLY) 저널 조회 뷰. Today 탭 일/주/월 토글의 '월' 모드 본문.
 * 기준 연·월을 달 단위로 이동하며, 해당 달의 저널 일자를 날짜 순서 연속 목록으로 읽는다.
 * 연·월 라벨을 누르면 연·월 피커(월 모드)가 열려 먼 달로 바로 이동할 수 있다.
 */
export function JournalMonthlyView({ initialDate, modeSelector }: JournalMonthlyViewProps) {
  const [yearMonth, setYearMonth] = useState<string>(() => toYearMonth(initialDate));
  const [pickerVisible, setPickerVisible] = useState(false);
  const { days, loading, refreshing, error, load, onRefresh } = useJournalMonth(yearMonth);

  const atThisMonth = yearMonth >= currentYearMonth();
  const { year, month } = parseYearMonth(yearMonth);
  const monthLabel = `${year}년 ${MONTH_NAMES[month - 1]}`;

  // 엔트리가 있는 일자만 날짜순으로 (훅이 이미 오름차순 정렬)
  const entryDays = useMemo(() => days.filter(dayHasEntries), [days]);

  function shiftBy(delta: number) {
    setYearMonth((prev) => shiftMonth(prev, delta));
  }

  function goToThisMonth() {
    if (!atThisMonth) setYearMonth(currentYearMonth());
  }

  /** 피커에서 고른 연·월("YYYY-MM")로 이동. 이번 달 이후는 이번 달로 수렴. */
  function handlePickMonth(nextYearMonth: string) {
    const thisMonth = currentYearMonth();
    const normalized = toYearMonth(nextYearMonth);
    setYearMonth(normalized > thisMonth ? thisMonth : normalized);
  }

  return (
    <View style={styles.view}>
      <View style={styles.controlsRow}>
        {modeSelector}
      <View style={styles.monthNav}>
        <Pressable
          accessibilityRole="button"
          accessibilityLabel="이전 달"
          onPress={() => shiftBy(-1)}
          style={styles.monthNavButton}
        >
          <Text style={styles.monthNavArrow}>‹</Text>
        </Pressable>

        <View style={styles.monthLabelWrap}>
          <Pressable
            accessibilityRole="button"
            accessibilityLabel={`${monthLabel}, 연·월 선택`}
            onPress={() => setPickerVisible(true)}
            hitSlop={6}
            style={styles.monthLabelButton}
          >
            <Text style={styles.monthLabel}>{monthLabel}</Text>
            <Text style={styles.monthLabelCaret}>▾</Text>
          </Pressable>
          {!atThisMonth && (
            <Pressable accessibilityRole="button" onPress={goToThisMonth} hitSlop={6}>
              <Text style={styles.monthTodayHint}>이번 달로</Text>
            </Pressable>
          )}
        </View>

        <Pressable
          accessibilityRole="button"
          accessibilityLabel="다음 달"
          disabled={atThisMonth}
          onPress={() => shiftBy(1)}
          style={[styles.monthNavButton, atThisMonth && styles.monthNavButtonDisabled]}
        >
          <Text style={[styles.monthNavArrow, atThisMonth && styles.monthNavArrowDisabled]}>›</Text>
        </Pressable>
      </View>
      </View>
    <ScrollView
      contentContainerStyle={styles.container}
      refreshControl={
        <RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor={colors.accent} />
      }
    >
      <JournalPeriodTagCloudPanel period={{ yy: year, mnth: month }} title="이 달의 태그" />
      <JournalPeriodThreadPanel query={{ viewType: "LIST", yy: year, mnth: month }} title="이 달의 스레드" />

      {!loading && error == null && entryDays.length > 0 && (
        <JournalCopyShareBar
          label="이 달 기록"
          buildText={() => buildEntriesCopyText(entriesOfDays(entryDays))}
          shareTitle={`${monthLabel} 기록`}
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
      ) : entryDays.length === 0 ? (
        <View style={styles.center}>
          <Text style={styles.emptyText}>이 달의 기록이 없습니다.</Text>
        </View>
      ) : (
        <View style={styles.monthList}>
          {entryDays.map((day) => (
            <JournalDaySection key={day.id} day={day} />
          ))}
        </View>
      )}

      <JournalDatePickerModal
        visible={pickerVisible}
        mode="month"
        value={yearMonth}
        onSelect={handlePickMonth}
        onClose={() => setPickerVisible(false)}
      />
    </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  view: { flex: 1 },
  controlsRow: { flexDirection: "row", alignItems: "center", paddingHorizontal: 20, paddingBottom: 12, gap: 8 },
  container: { flexGrow: 1, padding: 20, paddingTop: 4, gap: 16, paddingBottom: 24 },
  monthNav: {
    flex: 1,
    minWidth: 0,
    flexDirection: "row",
    alignItems: "center",
    gap: 4
  },
  monthNavButton: {
    paddingHorizontal: 4,
    paddingVertical: 6
  },
  monthNavButtonDisabled: {
    opacity: 0.3
  },
  monthNavArrow: {
    fontSize: 28,
    color: colors.accent,
    lineHeight: 32,
    fontWeight: "300"
  },
  monthNavArrowDisabled: {
    color: colors.muted
  },
  monthLabelWrap: {
    flex: 1,
    alignItems: "center",
    gap: 2
  },
  monthLabelButton: {
    flexDirection: "row",
    alignItems: "center",
    gap: 4
  },
  monthLabel: {
    color: colors.text,
    fontSize: 14,
    fontWeight: "700"
  },
  monthLabelCaret: {
    color: colors.accent,
    fontSize: 11,
    fontWeight: "700"
  },
  monthTodayHint: {
    color: colors.accent,
    fontSize: 11,
    fontWeight: "600"
  },
  center: { alignItems: "center", justifyContent: "center", gap: 12, paddingVertical: 48 },
  errorText: { color: "#C0392B", fontSize: 14 },
  retryButton: { paddingHorizontal: 12, paddingVertical: 7 },
  retryButtonText: { color: colors.accent, fontSize: 13, fontWeight: "700" },
  emptyText: { color: colors.secondaryText, fontSize: 16, fontWeight: "600" },
  monthList: { gap: 20 }
});
