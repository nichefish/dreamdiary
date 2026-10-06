import type { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { clampDateToToday, normalizeDateStr, toDateStr } from "../utils/date";
import type { RootStackParamList } from "./AppNavigator";

/**
 * 스택 화면(태그별 기록 등)에서 Daily 허브(오늘 탭)로 날짜를 전달한다.
 * TodayScreen 이 `{ date }` 를 1회 소비해 일 모드로 전환하고 해당 날짜를 연다.
 * popTo 라 위에 쌓인 스택 화면(태그별 기록·상세)은 닫히고 기존 Main 으로 돌아간다(새 Main 을 쌓지 않음).
 */
export function navigateToDailyHub(
  navigation: NativeStackNavigationProp<RootStackParamList>,
  date: string
): void {
  const normalized = normalizeDateStr(date);
  const safeDate = clampDateToToday(normalized ?? toDateStr(new Date()));
  navigation.popTo("Main", { screen: "Today", params: { date: safeDate } });
}
