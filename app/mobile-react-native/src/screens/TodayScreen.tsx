import { useCallback, useState } from "react";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useFocusEffect, useNavigation, useRoute } from "@react-navigation/native";
import type { BottomTabNavigationProp } from "@react-navigation/bottom-tabs";
import type { RouteProp } from "@react-navigation/native";
import { JournalDailyView } from "../components/journal/JournalDailyView";
import { JournalWeeklyView } from "../components/journal/JournalWeeklyView";
import { JournalMonthlyView } from "../components/journal/JournalMonthlyView";
import { useSelectedJournalDate } from "../hooks/useSelectedJournalDate";
import type { MainTabParamList } from "../navigation/AppNavigator";
import { MENU_URL } from "../navigation/appMenu";
import { AppMenuButton } from "../components/navigation/AppMenu";
import { colors } from "../theme/colors";

type TodayNav = BottomTabNavigationProp<MainTabParamList, "Today">;
type TodayRoute = RouteProp<MainTabParamList, "Today">;

/** 날짜 조회 보기 모드 */
type ViewMode = "daily" | "weekly" | "monthly";

const VIEW_MODES: Array<{ id: ViewMode; label: string }> = [
  { id: "daily", label: "일" },
  { id: "weekly", label: "주" },
  { id: "monthly", label: "월" }
];

/**
 * Today 탭 — 날짜 조회 허브. 일/주/월 토글로 일별·주별·월별 조회 뷰를 전환한다.
 * 태그별 기록·스레드 글 목록 화면의 날짜 헤더(navigateToDailyHub)가 `{ date }` 로 진입시키면 일별 모드로 전환해 해당 날짜를 보여준다.
 */
export function TodayScreen() {
  const navigation = useNavigation<TodayNav>();
  const route = useRoute<TodayRoute>();
  const { selectedDate, setSelectedDate, shiftDay, goToToday, atToday } = useSelectedJournalDate();
  const [viewMode, setViewMode] = useState<ViewMode>("daily");

  // 스택 화면(태그별 기록·스레드 글 목록)의 날짜 헤더에서 `{ date }` 로 진입 시 일별 모드로 전환 + 선택일 동기화 (1회 소비)
  useFocusEffect(
    useCallback(() => {
      const paramDate = route.params?.date;
      if (!paramDate) return;
      setSelectedDate(paramDate);
      setViewMode("daily");
      navigation.setParams({ date: undefined });
    }, [navigation, route.params?.date, setSelectedDate])
  );

  const modeSelector = (
        <View style={styles.segment}>
          {VIEW_MODES.map((item) => {
            const active = item.id === viewMode;
            return (
              <Pressable
                accessibilityRole="button"
                accessibilityState={{ selected: active }}
                key={item.id}
                onPress={() => setViewMode(item.id)}
                style={[styles.segmentButton, active && styles.segmentButtonActive]}
              >
                <Text style={[styles.segmentText, active && styles.segmentTextActive]}>
                  {item.label}
                </Text>
              </Pressable>
            );
          })}
        </View>
  );

  return (
    <SafeAreaView edges={["top", "left", "right"]} style={styles.safeArea}>
      <View style={styles.topBar}>
        <View style={styles.kickerRow}>
          <AppMenuButton currentUrl={MENU_URL.JOURNAL_DAY} />
          <Text style={styles.kicker}>DreamDiary</Text>
        </View>
      </View>

      {viewMode === "daily" ? (
        <JournalDailyView
          modeSelector={modeSelector}
          selectedDate={selectedDate}
          atToday={atToday}
          shiftDay={shiftDay}
          goToToday={goToToday}
          setSelectedDate={setSelectedDate}
        />
      ) : viewMode === "weekly" ? (
        <JournalWeeklyView initialDate={selectedDate} modeSelector={modeSelector} />
      ) : (
        <JournalMonthlyView initialDate={selectedDate} modeSelector={modeSelector} />
      )}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: colors.background },
  topBar: {
    paddingTop: 8,
    paddingHorizontal: 20,
    paddingBottom: 12,
    gap: 10
  },
  kickerRow: { flexDirection: "row", alignItems: "center", gap: 10 },
  kicker: {
    color: colors.accent,
    fontSize: 12,
    fontWeight: "700",
    letterSpacing: 0.4,
    textTransform: "uppercase"
  },
  segment: {
    flexDirection: "row",
    gap: 2,
    backgroundColor: colors.surface,
    borderRadius: 8,
    padding: 3,
    alignSelf: "flex-start"
  },
  segmentButton: {
    minWidth: 30,
    alignItems: "center",
    borderRadius: 6,
    paddingVertical: 7,
    paddingHorizontal: 6
  },
  segmentButtonActive: { backgroundColor: colors.text },
  segmentText: { color: colors.secondaryText, fontSize: 12, fontWeight: "700" },
  segmentTextActive: { color: colors.onDark }
});
