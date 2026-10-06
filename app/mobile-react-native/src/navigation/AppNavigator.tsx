import { ActivityIndicator, Pressable, StyleSheet, Text, View } from "react-native";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import type { NavigatorScreenParams } from "@react-navigation/native";
import { useAuth } from "../context/AuthContext";
import { AnnualDetailScreen } from "../screens/AnnualDetailScreen";
import { AnnualListScreen } from "../screens/AnnualListScreen";
import { EntryDetailScreen } from "../screens/EntryDetailScreen";
import { KeepDetailScreen } from "../screens/KeepDetailScreen";
import { KeepHomeScreen } from "../screens/KeepHomeScreen";
import { LoginScreen } from "../screens/LoginScreen";
import { MenuPlaceholderScreen } from "../screens/MenuPlaceholderScreen";
import { ProfileScreen } from "../screens/ProfileScreen";
import { SearchScreen } from "../screens/SearchScreen";
import { TagResultScreen } from "../screens/TagResultScreen";
import { ThreadListScreen } from "../screens/ThreadListScreen";
import { ThreadResultScreen } from "../screens/ThreadResultScreen";
import type { TagResultKind } from "./tagResult";
import { TodayScreen } from "../screens/TodayScreen";
import { colors } from "../theme/colors";
import type { DisplayTag, JournalEntry } from "../types/journalDay";
import type { KeepItem } from "../types/prospectiveKeep";

// ─── 타입 ───────────────────────────────────────────────────

export type RootStackParamList = {
  Login: undefined;
  Main: NavigatorScreenParams<MainTabParamList> | undefined;
  /** 엔트리 상세 보기 — 탭 어디서든 push 가능 */
  EntryDetail: {
    entry: JournalEntry;
    isDream: boolean;
    /** 엔트리가 속한 일자의 태그 (일자 목록에서 진입할 때만 전달) */
    dayTags?: DisplayTag[];
    /** true 면 상세가 소속 스레드를 GET /api/journal/entries/{id}/threads 로 다시 읽는다 (스레드 글 목록에서 진입할 때) */
    loadThreads?: boolean;
  };
  /** 태그별 기록 (태그 칩·태그 클라우드 탭) — 일자 태그는 연도별 일자, 일기·꿈 태그는 엔트리 */
  TagResult: { tagId: number; name: string; ctgr?: string | null; kind: TagResultKind };
  /** 스레드 글 목록 (기간 스레드 섹션·스레드 칩 탭) — 스레드 정보 + 소속 엔트리 날짜별. title 은 상세 로드 전 임시 표시 */
  ThreadResult: { threadId: number; title: string };
  /** 햄버거 메뉴 `저널 > 저널 스레드` — 스레드 목록 (읽기 전용) */
  ThreadList: undefined;
  /** 햄버거 메뉴 `저널 > 저널 결산` — 연도별 결산 목록 (읽기 전용) */
  AnnualList: undefined;
  /** 결산 상세 (요약·리뷰). title 은 상세 로드 전 임시 표시 */
  AnnualDetail: { yy: number; title?: string };
  /** 햄버거 메뉴 `Keep > Keep 전수관리` — Keep 목록 (읽기 전용, 모바일 2-0) */
  KeepHome: undefined;
  /**
   * Keep 상세 (읽기 전용). id 로 GET /api/prospective/keep/{id} 를 다시 읽는다.
   * item = 목록 항목(상세 응답이 오기 전 임시 표시), projectTitle = 목록에서 해석한 Project 이름
   */
  KeepDetail: { id: number; item?: KeepItem; projectTitle?: string };
  /** 모바일에 아직 없는 사용자 메뉴의 `준비 중` 화면. url = 웹 메뉴 URL (현재 메뉴 강조용) */
  MenuPlaceholder: { menuName: string; groupName?: string; url?: string };
};

export type MainTabParamList = {
  /** Daily 허브 — 앱 기본 탭 (일/주/월 토글 + 기간 태그 클라우드). date: 다른 화면에서 날짜 지정 진입 */
  Today: { date?: string } | undefined;
  Search: undefined;
  Profile: undefined;
};

// ─── 탭 아이콘 (이모지 기반) ────────────────────────────────

/** 이모지는 tint 색이 먹지 않으므로 비선택 탭은 흐리게. 선택 강조는 탭 항목 배경(tabBarActiveBackgroundColor)으로 한다.
 *  (아이콘 자체에 패딩/배경을 주면 탭바의 고정 아이콘 영역에서 잘려 사라진다) */
function tabIcon(focused: boolean, emoji: string) {
  return <Text style={[styles.tabIconEmoji, !focused && styles.tabIconEmojiInactive]}>{emoji}</Text>;
}

/** 선택 탭 라벨은 강조색·굵게, 비선택은 흐리게 */
function tabLabel(focused: boolean, color: string, label: string) {
  return (
    <Text style={[styles.tabLabel, { color }, focused && styles.tabLabelActive]}>{label}</Text>
  );
}

// ─── 하단 탭 (로그인 후) ──────────────────────────────────

const Tab = createBottomTabNavigator<MainTabParamList>();

function MainTabs() {
  return (
    <Tab.Navigator
      initialRouteName="Today"
      screenOptions={{
        headerShown: false,
        tabBarActiveTintColor: colors.accent,
        tabBarInactiveTintColor: colors.muted,
        tabBarStyle: {
          backgroundColor: colors.background,
          borderTopColor: colors.border
        },
        tabBarActiveBackgroundColor: colors.surface,
        tabBarItemStyle: { borderRadius: 14, marginHorizontal: 10, marginVertical: 4 }
      }}
    >
      <Tab.Screen
        name="Today"
        component={TodayScreen}
        options={{
          tabBarLabel: ({ focused, color }) => tabLabel(focused, color, "오늘"),
          tabBarIcon: ({ focused }) => tabIcon(focused, "📖")
        }}
      />
      <Tab.Screen
        name="Search"
        component={SearchScreen}
        options={{
          tabBarLabel: ({ focused, color }) => tabLabel(focused, color, "검색"),
          tabBarIcon: ({ focused }) => tabIcon(focused, "🔍")
        }}
      />
      <Tab.Screen
        name="Profile"
        component={ProfileScreen}
        options={{
          tabBarLabel: ({ focused, color }) => tabLabel(focused, color, "나"),
          tabBarIcon: ({ focused }) => tabIcon(focused, "👤")
        }}
      />
    </Tab.Navigator>
  );
}

// ─── 루트 스택 (인증 분기 + 공유 상세 화면) ──────────────────

const Stack = createNativeStackNavigator<RootStackParamList>();

export function AppNavigator() {
  const { authError, isAuthenticated, isLoading, retryAuth } = useAuth();

  if (isLoading) {
    return (
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center", backgroundColor: colors.background }}>
        <ActivityIndicator size="large" color={colors.accent} />
      </View>
    );
  }

  if (authError) {
    return (
      <View style={styles.authErrorContainer}>
        <Text style={styles.authErrorTitle}>인증 상태 확인 오류</Text>
        <Text style={styles.authErrorMessage}>{authError}</Text>
        <Pressable
          accessibilityRole="button"
          onPress={() => { void retryAuth(); }}
          style={styles.authRetryButton}
        >
          <Text style={styles.authRetryButtonText}>다시 시도</Text>
        </Pressable>
      </View>
    );
  }

  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      {isAuthenticated ? (
        <>
          <Stack.Screen name="Main" component={MainTabs} />
          <Stack.Screen
            name="EntryDetail"
            component={EntryDetailScreen}
            options={{ animation: "slide_from_right" }}
          />
          <Stack.Screen
            name="TagResult"
            component={TagResultScreen}
            options={{ animation: "slide_from_right" }}
          />
          <Stack.Screen
            name="ThreadResult"
            component={ThreadResultScreen}
            options={{ animation: "slide_from_right" }}
          />
          <Stack.Screen
            name="ThreadList"
            component={ThreadListScreen}
            options={{ animation: "slide_from_right" }}
          />
          <Stack.Screen
            name="AnnualList"
            component={AnnualListScreen}
            options={{ animation: "slide_from_right" }}
          />
          <Stack.Screen
            name="AnnualDetail"
            component={AnnualDetailScreen}
            options={{ animation: "slide_from_right" }}
          />
          <Stack.Screen
            name="KeepHome"
            component={KeepHomeScreen}
            options={{ animation: "slide_from_right" }}
          />
          <Stack.Screen
            name="KeepDetail"
            component={KeepDetailScreen}
            options={{ animation: "slide_from_right" }}
          />
          <Stack.Screen
            name="MenuPlaceholder"
            component={MenuPlaceholderScreen}
            options={{ animation: "slide_from_right" }}
          />
        </>
      ) : (
        <Stack.Screen name="Login" component={LoginScreen} />
      )}
    </Stack.Navigator>
  );
}

const styles = StyleSheet.create({
  tabIconEmoji: { fontSize: 20 },
  tabIconEmojiInactive: { opacity: 0.4 },
  tabLabel: { fontSize: 11, fontWeight: "500" },
  tabLabelActive: { fontWeight: "800" },
  authErrorContainer: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: colors.background,
    paddingHorizontal: 28,
    gap: 12
  },
  authErrorTitle: {
    color: colors.text,
    fontSize: 20,
    fontWeight: "800"
  },
  authErrorMessage: {
    color: colors.secondaryText,
    fontSize: 14,
    lineHeight: 21,
    textAlign: "center"
  },
  authRetryButton: {
    alignItems: "center",
    backgroundColor: colors.accent,
    borderRadius: 8,
    marginTop: 8,
    paddingHorizontal: 18,
    paddingVertical: 12
  },
  authRetryButtonText: {
    color: colors.onAccent,
    fontSize: 14,
    fontWeight: "800"
  }
});
