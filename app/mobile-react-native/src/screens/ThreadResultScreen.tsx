import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import {
  ActivityIndicator,
  Pressable,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  View
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { getJournalThread, getJournalThreadEntries } from "../api/dreamDiaryApi";
import { isUnauthorizedError } from "../api/client";
import type { RootStackParamList } from "../navigation/AppNavigator";
import { navigateToDailyHub } from "../navigation/dailyHub";
import { colors } from "../theme/colors";
import { displayTagsOf, type JournalEntry, type JournalThread } from "../types/journalDay";
import { normalizeDateStr } from "../utils/date";
import { buildResultListCopyText, copyDateLabel, htmlToPlainText } from "../utils/journalCopy";
import { JournalTagChips } from "../components/journal/JournalTagChips";
import { JournalCopyShareBar } from "../components/journal/JournalCopyShareBar";
import { colorFromTextClass } from "../components/journal/journalTagStyle";
import { LIFECYCLE_META, type LifecycleMeta } from "../components/journal/journalLifecycle";

type Props = NativeStackScreenProps<RootStackParamList, "ThreadResult">;

/** 날짜 묶음 정렬 — desc: 최신 날짜부터(기본), asc: 오래된 날짜부터(웹 스레드 상세와 같은 순서) */
type SortOrder = "desc" | "asc";

type EntryGroup = {
  /** YYYY-MM-DD (날짜를 알 수 없으면 "") */
  date: string;
  /** 같은 날짜 안은 서버 순서(챕터 sortOrder → 엔트리 sortOrder → ID) 유지 */
  entries: JournalEntry[];
};

const PREVIEW_LENGTH = 140;
/** 스레드 본문이 길면 접어서 보여줄 줄 수 */
const DESCRIPTION_COLLAPSED_LINES = 6;
/** 이 길이를 넘거나 줄이 많으면 본문 더보기 토글을 둔다 */
const DESCRIPTION_TOGGLE_LENGTH = 180;

const CONTENT_TYPE_LABELS: Record<string, string> = {
  JOURNAL_DREAM: "꿈",
  JOURNAL_DIARY: "일기",
  JOURNAL_NOTE: "노트",
  JOURNAL_REFLECTION: "해석"
};

function errorMessage(e: unknown, fallback: string): string {
  return e instanceof Error && e.message ? e.message : fallback;
}

/** 소속 기간 라벨 — 웹 formatThreadMembershipPeriod 와 같은 규칙(같은 날이면 하루, 아니면 `first ~ last`) */
function periodLabel(thread: JournalThread | null): string {
  const first = normalizeDateStr(thread?.firstEntryDate ?? "");
  const last = normalizeDateStr(thread?.lastEntryDate ?? "");
  if (!first || !last) return "";
  return first === last ? first : `${first} ~ ${last}`;
}

/**
 * 스레드 소속 엔트리 API 응답에는 threadList 가 비어 온다.
 * 상세 화면에서 최소한 진입한 스레드 칩은 보이도록 현재 스레드를 채워 넘긴다(상세가 loadThreads 로 전체 소속을 다시 읽는다).
 */
function withCurrentThread(entry: JournalEntry, threadId: number, title: string): JournalEntry {
  const list = (entry.threadList ?? []).filter((t) => t != null);
  if (!title || list.some((t) => t.threadId === threadId)) return entry;
  return {
    ...entry,
    threadList: [...list, { threadId, threadTitle: title, entryId: entry.id, entryContentType: entry.contentType }]
  };
}

function LifecycleBadge({ meta }: { meta: LifecycleMeta }) {
  return (
    <View style={[styles.lifecycleBadge, { backgroundColor: meta.background }]}>
      <Text style={[styles.lifecycleText, { color: meta.color }]}>{meta.label}</Text>
    </View>
  );
}

/** 소속 엔트리 카드 — 탭하면 엔트리 상세 */
function ThreadEntryCard({ entry, onPress }: { entry: JournalEntry; onPress: () => void }) {
  const isDream = entry.contentType === "JOURNAL_DREAM";
  const kindLabel = CONTENT_TYPE_LABELS[entry.contentType] ?? "기록";
  const title = entry.title?.trim() ?? "";
  const preview = htmlToPlainText(entry.content || entry.markdownContent)
    .replace(/\s+/g, " ")
    .trim()
    .slice(0, PREVIEW_LENGTH);
  const tags = displayTagsOf(entry.tag);
  const lifecycleKey = entry.lifecycle?.lifecycleKey ?? "";
  const lifecycle = lifecycleKey && lifecycleKey !== "OPEN" ? LIFECYCLE_META[lifecycleKey] : undefined;

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityHint="엔트리 상세 열기"
      onPress={onPress}
      style={({ pressed }) => [styles.entryCard, isDream && styles.entryCardDream, pressed && styles.pressed]}
    >
      <View style={styles.entryHead}>
        <Text style={[styles.entryKind, isDream && styles.entryKindDream]}>
          {kindLabel}
          {entry.sortOrder != null ? ` #${entry.sortOrder}` : ""}
        </Text>
        {lifecycle ? <LifecycleBadge meta={lifecycle} /> : null}
      </View>
      {title ? (
        <Text style={[styles.entryTitle, isDream && styles.entryBodyDream]} numberOfLines={1}>
          {title}
        </Text>
      ) : null}
      <Text style={[styles.entryBody, isDream && styles.entryBodyDream]} numberOfLines={3}>
        {preview || "(내용 없음)"}
      </Text>
      <JournalTagChips tags={tags} variant={isDream ? "dream" : "diary"} />
    </Pressable>
  );
}

/**
 * 스레드 글 목록 화면 — 웹 스레드 상세(JournalThreadDetailContent)의 읽기 부분을 모바일 스택 화면으로 옮겼다.
 * - 스레드 정보: GET /api/journal/threads/{id} (말머리·라이프사이클·제목·소속 기간·본문·소속 엔트리 태그 집계)
 * - 소속 엔트리: GET /api/journal/threads/{id}/entries (full JournalEntryDto, 서버 정렬 일자 오름차순)
 *   → 날짜별로 묶어 기본 최신 날짜부터 보여준다(정렬 토글로 오래된 날짜부터). 같은 날짜 안은 서버 순서 유지.
 * - 날짜 헤더 탭 → 오늘 탭 일 모드로 그 날짜, 엔트리 탭 → 엔트리 상세, 카드 안 태그 칩 탭 → 태그별 기록
 * - 복사·공유 바: 화면에 보이는 순서 그대로 소속 엔트리 전체를 텍스트로 (머리줄 = 스레드 제목·기간·건수·정렬)
 * - 읽기 전용: 라이프사이클 변경·소속 추가/해제·연관 스레드·수정·삭제·댓글은 두지 않는다.
 */
export function ThreadResultScreen({ route, navigation }: Props) {
  const { threadId, title: routeTitle } = route.params;

  const [thread, setThread] = useState<JournalThread | null>(null);
  const [entries, setEntries] = useState<JournalEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [order, setOrder] = useState<SortOrder>("desc");
  const [descExpanded, setDescExpanded] = useState(false);
  const seqRef = useRef(0);
  const mountedRef = useRef(true);

  useEffect(() => {
    mountedRef.current = true;
    return () => {
      mountedRef.current = false;
    };
  }, []);

  /** 스레드 정보와 소속 엔트리를 함께 조회. 정보 조회 실패는 헤더만 기본값으로 두고, 엔트리 조회 실패는 오류 상태로 보여준다. */
  const load = useCallback(async (silent = false) => {
    const seq = ++seqRef.current;
    if (!silent) setLoading(true);
    setError(null);
    const [threadRes, entriesRes] = await Promise.allSettled([
      getJournalThread(threadId),
      getJournalThreadEntries(threadId)
    ]);
    if (!mountedRef.current || seq !== seqRef.current) return;

    if (threadRes.status === "fulfilled") {
      setThread(threadRes.value);
    } else if (!isUnauthorizedError(threadRes.reason)) {
      console.warn("[ThreadResultScreen] thread detail load failed", { threadId }, threadRes.reason);
    }

    if (entriesRes.status === "fulfilled") {
      setEntries(entriesRes.value);
    } else {
      if (!isUnauthorizedError(entriesRes.reason)) {
        console.error("[ThreadResultScreen] thread entries load failed", { threadId }, entriesRes.reason);
      }
      setError(errorMessage(entriesRes.reason, "스레드 글 목록을 불러오지 못했습니다."));
      setEntries([]);
    }
    setLoading(false);
    setRefreshing(false);
  }, [threadId]);

  useEffect(() => {
    void load();
  }, [load]);

  const groups = useMemo<EntryGroup[]>(() => {
    const byDate = new Map<string, JournalEntry[]>();
    for (const entry of entries) {
      const date = normalizeDateStr(entry.stdrdDt ?? "") ?? "";
      const list = byDate.get(date);
      if (list) list.push(entry);
      else byDate.set(date, [entry]);
    }
    return Array.from(byDate.entries())
      .map(([date, items]) => ({ date, entries: items }))
      .sort((a, b) => {
        // 날짜 없는 묶음은 항상 맨 뒤
        if (!a.date) return b.date ? 1 : 0;
        if (!b.date) return -1;
        return order === "desc" ? b.date.localeCompare(a.date) : a.date.localeCompare(b.date);
      });
  }, [entries, order]);

  const displayTitle = thread?.title?.trim() || routeTitle.trim() || "스레드";
  const description = htmlToPlainText(thread?.markdownContent || thread?.content).trim();
  const descriptionLong =
    description.length > DESCRIPTION_TOGGLE_LENGTH ||
    description.split("\n").length > DESCRIPTION_COLLAPSED_LINES;
  const threadTags = displayTagsOf(thread?.tag);
  const period = periodLabel(thread);
  const lifecycleKey = thread ? (thread.lifecycle?.lifecycleKey || "OPEN") : "";
  const lifecycle = lifecycleKey ? LIFECYCLE_META[lifecycleKey] : undefined;
  const prefixColor = colorFromTextClass(thread?.prefix?.textClass) ?? colors.secondaryText;
  const memberCount = loading ? (thread?.membershipCount ?? null) : entries.length;

  function onRefresh() {
    setRefreshing(true);
    void load(true);
  }

  /** 화면 순서(날짜 묶음 정렬 + 날짜 안 서버 순서) 그대로 복사 텍스트를 만든다 */
  function buildCopyText(): string {
    const prefix = thread?.prefix?.name ? `[${thread.prefix.name}] ` : "";
    const header = [
      `스레드: ${prefix}${displayTitle}`,
      [period, `소속 엔트리 ${entries.length}건`, order === "desc" ? "최신순" : "오래된순"].filter(Boolean).join(" · ")
    ];
    return buildResultListCopyText(
      header,
      groups.flatMap((group) => group.entries.map((entry) => ({ entry, stdrdDt: group.date || null })))
    );
  }

  return (
    <SafeAreaView edges={["top", "left", "right", "bottom"]} style={styles.safeArea}>
      <View style={styles.header}>
        <Pressable
          accessibilityRole="button"
          accessibilityLabel="뒤로 가기"
          onPress={() => navigation.goBack()}
          style={styles.backButton}
          hitSlop={8}
        >
          <Text style={styles.backArrow}>‹</Text>
          <Text style={styles.backLabel}>뒤로</Text>
        </Pressable>
        <Text style={styles.headerTitle} numberOfLines={1}>
          스레드 글 목록
        </Text>
      </View>

      <ScrollView
        contentContainerStyle={styles.container}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
      >
        {/* 스레드 정보 */}
        <View style={styles.threadCard}>
          <View style={styles.threadMetaRow}>
            <Text style={styles.kindLabel}>🧵 스레드</Text>
            {thread?.prefix?.name ? (
              <View style={[styles.prefixBadge, { borderColor: prefixColor }]}>
                <Text style={[styles.prefixText, { color: prefixColor }]}>{thread.prefix.name}</Text>
              </View>
            ) : null}
            {lifecycle ? <LifecycleBadge meta={lifecycle} /> : null}
          </View>
          <Text style={styles.threadTitle} accessibilityRole="header">
            {displayTitle}
          </Text>
          <Text style={styles.threadSub}>
            {[period, memberCount != null ? `소속 엔트리 ${memberCount}건` : ""].filter(Boolean).join(" · ")}
          </Text>
          {description ? (
            <>
              <Text
                style={styles.descriptionText}
                numberOfLines={descriptionLong && !descExpanded ? DESCRIPTION_COLLAPSED_LINES : undefined}
              >
                {description}
              </Text>
              {descriptionLong ? (
                <Pressable
                  accessibilityRole="button"
                  onPress={() => setDescExpanded((v) => !v)}
                  hitSlop={6}
                  style={styles.descToggle}
                >
                  <Text style={styles.descToggleText}>{descExpanded ? "접기 ▴" : "더보기 ▾"}</Text>
                </Pressable>
              ) : null}
            </>
          ) : null}
          {threadTags.length > 0 ? (
            <View style={styles.threadTags}>
              <Text style={styles.threadTagsLabel}>소속 엔트리 태그</Text>
              <JournalTagChips tags={threadTags} variant="diary" pressable={false} />
            </View>
          ) : null}
        </View>

        {loading && !refreshing ? (
          <ActivityIndicator style={styles.loader} color={colors.accent} />
        ) : error ? (
          <View style={styles.stateBox}>
            <Text style={styles.errorText}>{error}</Text>
            <Pressable accessibilityRole="button" onPress={() => { void load(); }} style={styles.retryButton}>
              <Text style={styles.retryText}>다시 시도</Text>
            </Pressable>
          </View>
        ) : groups.length === 0 ? (
          <View style={styles.stateBox}>
            <Text style={styles.emptyText}>이 스레드에 소속된 엔트리가 없습니다.</Text>
          </View>
        ) : (
          <>
            <JournalCopyShareBar
              label="이 스레드 글"
              buildText={buildCopyText}
              shareTitle={`스레드 ${displayTitle}`}
              copiedMessage={`스레드 글 ${entries.length}건을 복사했습니다.`}
            />
            <View style={styles.listHead}>
              <Text style={styles.summary}>{`${groups.length}일 · ${entries.length}건`}</Text>
              <View style={styles.orderRow}>
                {(["desc", "asc"] as const).map((value) => {
                  const selected = order === value;
                  return (
                    <Pressable
                      key={value}
                      accessibilityRole="button"
                      accessibilityState={{ selected }}
                      onPress={() => setOrder(value)}
                      style={[styles.orderChip, selected && styles.orderChipSelected]}
                    >
                      <Text style={[styles.orderChipText, selected && styles.orderChipTextSelected]}>
                        {value === "desc" ? "최신순" : "오래된순"}
                      </Text>
                    </Pressable>
                  );
                })}
              </View>
            </View>
            {groups.map((group) => (
              <View key={group.date || "no-date"} style={styles.group}>
                {group.date ? (
                  <Pressable
                    accessibilityRole="button"
                    accessibilityHint="오늘 탭에서 이 날짜 보기"
                    onPress={() => navigateToDailyHub(navigation, group.date)}
                    style={({ pressed }) => [styles.dateRow, pressed && styles.pressed]}
                  >
                    <Text style={styles.dateText}>{copyDateLabel(group.date)}</Text>
                    <Text style={styles.dateMore}>이 날 보기 ›</Text>
                  </Pressable>
                ) : (
                  <View style={styles.dateRow}>
                    <Text style={styles.dateText}>날짜 없음</Text>
                  </View>
                )}
                {group.entries.map((entry, idx) => (
                  <ThreadEntryCard
                    key={entry.id != null ? `e${entry.id}` : `${group.date}-${idx}`}
                    entry={entry}
                    onPress={() =>
                      navigation.push("EntryDetail", {
                        entry: withCurrentThread(entry, threadId, displayTitle),
                        isDream: entry.contentType === "JOURNAL_DREAM",
                        loadThreads: true
                      })
                    }
                  />
                ))}
              </View>
            ))}
          </>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: colors.background },
  header: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: colors.border,
    backgroundColor: colors.background
  },
  backButton: { flexDirection: "row", alignItems: "center", gap: 2, paddingRight: 12 },
  backArrow: { fontSize: 28, color: colors.accent, lineHeight: 32, fontWeight: "300" },
  backLabel: { fontSize: 15, color: colors.accent },
  headerTitle: { flex: 1, fontSize: 16, fontWeight: "700", color: colors.text },
  container: { padding: 16, gap: 12, paddingBottom: 32 },
  threadCard: {
    borderWidth: 1,
    borderColor: "#BFD9FF",
    backgroundColor: "#F2F8FF",
    borderRadius: 12,
    padding: 14,
    gap: 6
  },
  threadMetaRow: { flexDirection: "row", flexWrap: "wrap", alignItems: "center", gap: 6 },
  kindLabel: { fontSize: 12, color: colors.secondaryText, fontWeight: "600" },
  prefixBadge: { borderWidth: 1, borderRadius: 4, paddingHorizontal: 5, paddingVertical: 0 },
  prefixText: { fontSize: 11, fontWeight: "700" },
  lifecycleBadge: { borderRadius: 6, paddingHorizontal: 6, paddingVertical: 1 },
  lifecycleText: { fontSize: 11, fontWeight: "700" },
  threadTitle: { fontSize: 20, fontWeight: "700", color: colors.text },
  threadSub: { fontSize: 12.5, color: colors.secondaryText },
  descriptionText: { fontSize: 13.5, lineHeight: 20, color: colors.text },
  descToggle: { alignSelf: "flex-start", paddingVertical: 2 },
  descToggleText: { fontSize: 12.5, color: colors.accent, fontWeight: "700" },
  threadTags: { gap: 4, marginTop: 2 },
  threadTagsLabel: { fontSize: 11.5, color: colors.muted, fontWeight: "600" },
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
  listHead: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", gap: 8 },
  summary: { fontSize: 12.5, color: colors.secondaryText },
  orderRow: { flexDirection: "row", gap: 6 },
  orderChip: {
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 14,
    paddingHorizontal: 10,
    paddingVertical: 4,
    backgroundColor: colors.input
  },
  orderChipSelected: { backgroundColor: colors.accent, borderColor: colors.accent },
  orderChipText: { fontSize: 12, color: colors.text, fontWeight: "600" },
  orderChipTextSelected: { color: colors.onAccent },
  group: { gap: 8 },
  dateRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: colors.border
  },
  dateText: { fontSize: 15, fontWeight: "700", color: colors.text },
  dateMore: { fontSize: 12.5, color: colors.accent, fontWeight: "600" },
  entryCard: {
    backgroundColor: colors.input,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 10,
    padding: 12,
    gap: 6
  },
  entryCardDream: { backgroundColor: "#F5EEF8", borderColor: "#D7BDE2" },
  entryHead: { flexDirection: "row", alignItems: "center", gap: 6 },
  entryKind: { fontSize: 11.5, fontWeight: "700", color: colors.accent },
  entryKindDream: { color: "#8E44AD" },
  entryTitle: { fontSize: 14.5, fontWeight: "700", color: colors.text },
  entryBody: { fontSize: 14, lineHeight: 20, color: colors.text },
  entryBodyDream: { color: "#4A235A" },
  pressed: { opacity: 0.7 }
});
