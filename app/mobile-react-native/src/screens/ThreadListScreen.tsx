import { useCallback, useEffect, useRef, useState } from "react";
import {
  ActivityIndicator,
  FlatList,
  Pressable,
  RefreshControl,
  StyleSheet,
  Text,
  TextInput,
  View
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useNavigation } from "@react-navigation/native";
import type { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { getJournalThreadPage } from "../api/dreamDiaryApi";
import { isUnauthorizedError } from "../api/client";
import type { RootStackParamList } from "../navigation/AppNavigator";
import { MENU_URL } from "../navigation/appMenu";
import { openThreadResult } from "../navigation/threadResult";
import { colors } from "../theme/colors";
import { displayTagsOf, type JournalThread } from "../types/journalDay";
import { normalizeDateStr } from "../utils/date";
import { MenuScreenHeader } from "../components/navigation/AppMenu";
import { JournalTagChips } from "../components/journal/JournalTagChips";
import { colorFromTextClass } from "../components/journal/journalTagStyle";
import { LIFECYCLE_META } from "../components/journal/journalLifecycle";


/** 한 번에 받을 스레드 수 (웹 목록은 10) */
const PAGE_SIZE = 20;
/** 카드에 보여줄 소속 엔트리 태그 수 */
const TAG_PREVIEW_LIMIT = 6;

/** 소속 기간 라벨 — 같은 날이면 하루, 아니면 `first ~ last` (스레드 글 목록 화면과 같은 규칙) */
function periodLabel(thread: JournalThread): string {
  const first = normalizeDateStr(thread.firstEntryDate ?? "");
  const last = normalizeDateStr(thread.lastEntryDate ?? "");
  if (!first || !last) return "";
  return first === last ? first : `${first} ~ ${last}`;
}

function ThreadRow({ thread, onPress }: { thread: JournalThread; onPress: () => void }) {
  const title = thread.title?.trim() || "(제목 없음)";
  const lifecycle = LIFECYCLE_META[thread.lifecycle?.lifecycleKey || "OPEN"];
  const prefixColor = colorFromTextClass(thread.prefix?.textClass) ?? colors.secondaryText;
  const period = periodLabel(thread);
  const count = thread.membershipCount;
  const sub = [period, count != null ? `소속 엔트리 ${count}건` : ""].filter(Boolean).join(" · ");
  const tags = displayTagsOf(thread.tag);

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityHint="스레드 글 목록 열기"
      onPress={onPress}
      style={({ pressed }) => [styles.card, pressed && styles.pressed]}
    >
      <View style={styles.metaRow}>
        {thread.prefix?.name ? (
          <View style={[styles.prefixBadge, { borderColor: prefixColor }]}>
            <Text style={[styles.prefixText, { color: prefixColor }]}>{thread.prefix.name}</Text>
          </View>
        ) : null}
        {lifecycle ? (
          <View style={[styles.lifecycleBadge, { backgroundColor: lifecycle.background }]}>
            <Text style={[styles.lifecycleText, { color: lifecycle.color }]}>{lifecycle.label}</Text>
          </View>
        ) : null}
      </View>
      <Text style={styles.title} numberOfLines={2}>
        🧵 {title}
      </Text>
      {sub ? <Text style={styles.sub}>{sub}</Text> : null}
      {tags.length > 0 ? (
        <JournalTagChips tags={tags.slice(0, TAG_PREVIEW_LIMIT)} variant="diary" pressable={false} />
      ) : null}
    </Pressable>
  );
}

/**
 * 저널 스레드 (햄버거 메뉴 `저널 > 저널 스레드`) — 웹 스레드 목록(JournalThreadList)의 읽기 부분.
 * - GET /api/journal/threads?page=&size= (서버 정렬: 소속 엔트리 최신 날짜 기준), 끝까지 내리면 다음 페이지
 * - 제목 검색: 검색 버튼·키보드 검색 키로만 제출(searchType=title). ✕ 는 입력을 지우고, 검색 중이었다면 전체 목록으로 돌아간다.
 * - 스레드를 누르면 스레드 글 목록(ThreadResult)
 * - 읽기 전용: 등록·수정·말머리 관리·라이프사이클 변경 없음. 말머리 탭·라이프사이클·태그 필터는 아직 없다.
 */
export function ThreadListScreen() {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  const [keyword, setKeyword] = useState("");
  const [submittedKeyword, setSubmittedKeyword] = useState("");
  const [threads, setThreads] = useState<JournalThread[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [moreError, setMoreError] = useState(false);
  const seqRef = useRef(0);

  /** 첫 페이지 조회 (검색 제출·새로고침·재시도). 늦게 도착한 이전 응답은 버린다 */
  const loadFirst = useCallback(async (kw: string, silent = false) => {
    const seq = ++seqRef.current;
    if (!silent) setLoading(true);
    setError(null);
    setMoreError(false);
    try {
      const res = await getJournalThreadPage({ page: 0, size: PAGE_SIZE, keyword: kw });
      if (seq !== seqRef.current) return;
      setThreads(res.content);
      setPage(res.page);
      setTotalPages(res.totalPages);
      setTotalElements(res.totalElements);
    } catch (e) {
      if (seq !== seqRef.current) return;
      if (!isUnauthorizedError(e)) console.error("[ThreadListScreen] thread list load failed", { keyword: kw }, e);
      setError("스레드 목록을 불러오지 못했습니다.");
      setThreads([]);
      setTotalElements(0);
      setTotalPages(0);
    } finally {
      if (seq === seqRef.current) {
        setLoading(false);
        setRefreshing(false);
      }
    }
  }, []);

  /** 다음 페이지 — 이미 받은 스레드는 다시 넣지 않는다 */
  const loadMore = useCallback(async () => {
    if (loading || loadingMore || refreshing || error != null || page + 1 >= totalPages) return;
    const seq = seqRef.current;
    setLoadingMore(true);
    setMoreError(false);
    try {
      const res = await getJournalThreadPage({ page: page + 1, size: PAGE_SIZE, keyword: submittedKeyword });
      if (seq !== seqRef.current) return;
      setThreads((prev) => {
        const seen = new Set(prev.map((t) => t.id));
        return [...prev, ...res.content.filter((t) => !seen.has(t.id))];
      });
      setPage(res.page);
      setTotalPages(res.totalPages);
      setTotalElements(res.totalElements);
    } catch (e) {
      if (seq !== seqRef.current) return;
      if (!isUnauthorizedError(e)) console.error("[ThreadListScreen] thread list next page failed", { page: page + 1 }, e);
      setMoreError(true);
    } finally {
      setLoadingMore(false);
    }
  }, [error, loading, loadingMore, page, refreshing, submittedKeyword, totalPages]);

  useEffect(() => {
    void loadFirst("");
  }, [loadFirst]);

  function submitSearch() {
    const kw = keyword.trim();
    setSubmittedKeyword(kw);
    void loadFirst(kw);
  }

  function clearInput() {
    setKeyword("");
    if (submittedKeyword) {
      setSubmittedKeyword("");
      void loadFirst("");
    }
  }

  function onRefresh() {
    setRefreshing(true);
    void loadFirst(submittedKeyword, true);
  }

  const summary = submittedKeyword ? `"${submittedKeyword}" ${totalElements}개` : `전체 ${totalElements}개`;

  return (
    <SafeAreaView edges={["top", "left", "right", "bottom"]} style={styles.safeArea}>
      <MenuScreenHeader title="저널 스레드" currentUrl={MENU_URL.JOURNAL_THREAD} />
      <FlatList
        data={threads}
        keyExtractor={(item) => String(item.id)}
        contentContainerStyle={styles.container}
        keyboardShouldPersistTaps="handled"
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
        onEndReachedThreshold={0.4}
        onEndReached={() => {
          void loadMore();
        }}
        ListHeaderComponent={
          <View style={styles.listHeader}>
            <View style={styles.searchRow}>
              <View style={styles.searchInputWrap}>
                <TextInput
                  autoCapitalize="none"
                  autoCorrect={false}
                  onChangeText={setKeyword}
                  onSubmitEditing={submitSearch}
                  placeholder="제목을 검색하세요."
                  placeholderTextColor={colors.muted}
                  returnKeyType="search"
                  style={styles.searchInput}
                  value={keyword}
                />
                {keyword.length > 0 ? (
                  <Pressable
                    accessibilityRole="button"
                    accessibilityLabel="검색어 지우기"
                    hitSlop={8}
                    onPress={clearInput}
                    style={styles.clearButton}
                  >
                    <Text style={styles.clearButtonText}>✕</Text>
                  </Pressable>
                ) : null}
              </View>
              <Pressable
                accessibilityRole="button"
                onPress={submitSearch}
                style={({ pressed }) => [styles.searchButton, pressed && styles.pressed]}
              >
                <Text style={styles.searchButtonText}>검색</Text>
              </Pressable>
            </View>
            {!loading && error == null ? <Text style={styles.summary}>{summary}</Text> : null}
          </View>
        }
        ListEmptyComponent={
          loading ? (
            <ActivityIndicator style={styles.loader} color={colors.accent} />
          ) : error != null ? (
            <View style={styles.stateBox}>
              <Text style={styles.errorText}>{error}</Text>
              <Pressable accessibilityRole="button" onPress={() => { void loadFirst(submittedKeyword); }} style={styles.retryButton}>
                <Text style={styles.retryText}>다시 시도</Text>
              </Pressable>
            </View>
          ) : (
            <View style={styles.stateBox}>
              <Text style={styles.emptyText}>{submittedKeyword ? "검색 결과가 없습니다." : "스레드가 없습니다."}</Text>
            </View>
          )
        }
        ListFooterComponent={
          loadingMore ? (
            <ActivityIndicator style={styles.footer} color={colors.muted} />
          ) : moreError ? (
            <Pressable accessibilityRole="button" onPress={() => { void loadMore(); }} style={styles.footer}>
              <Text style={styles.moreErrorText}>더 불러오지 못했습니다. 다시 시도 ›</Text>
            </Pressable>
          ) : null
        }
        renderItem={({ item }) => (
          <ThreadRow
            thread={item}
            onPress={() => openThreadResult(navigation, { threadId: item.id, title: item.title })}
          />
        )}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: colors.background },
  container: { padding: 16, gap: 10, paddingBottom: 32, flexGrow: 1 },
  listHeader: { gap: 10, marginBottom: 2 },
  searchRow: { flexDirection: "row", gap: 8, alignItems: "center" },
  searchInputWrap: { flex: 1, justifyContent: "center" },
  searchInput: {
    backgroundColor: colors.input,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: colors.border,
    paddingLeft: 12,
    paddingRight: 36,
    paddingVertical: 9,
    color: colors.text,
    fontSize: 14.5
  },
  clearButton: {
    position: "absolute",
    right: 8,
    width: 22,
    height: 22,
    borderRadius: 11,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: colors.border
  },
  clearButtonText: { color: colors.secondaryText, fontSize: 11, fontWeight: "700" },
  searchButton: {
    backgroundColor: colors.accent,
    borderRadius: 8,
    paddingHorizontal: 14,
    paddingVertical: 9
  },
  searchButtonText: { color: colors.onAccent, fontSize: 14.5, fontWeight: "700" },
  summary: { color: colors.muted, fontSize: 12, fontWeight: "700" },
  card: {
    backgroundColor: "#F2F8FF",
    borderWidth: 1,
    borderColor: "#BFD9FF",
    borderRadius: 12,
    padding: 12,
    gap: 6
  },
  metaRow: { flexDirection: "row", flexWrap: "wrap", alignItems: "center", gap: 6 },
  prefixBadge: { borderWidth: 1, borderRadius: 4, paddingHorizontal: 5, paddingVertical: 0 },
  prefixText: { fontSize: 11, fontWeight: "700" },
  lifecycleBadge: { borderRadius: 6, paddingHorizontal: 6, paddingVertical: 1 },
  lifecycleText: { fontSize: 11, fontWeight: "700" },
  title: { fontSize: 15.5, fontWeight: "700", color: colors.text },
  sub: { fontSize: 12.5, color: colors.secondaryText },
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
  footer: { paddingVertical: 16, alignItems: "center" },
  moreErrorText: { color: colors.accent, fontSize: 13, fontWeight: "600" },
  pressed: { opacity: 0.7 }
});
