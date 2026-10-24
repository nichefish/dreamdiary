import { useCallback, useEffect, useRef, useState } from "react";
import { ActivityIndicator, Pressable, StyleSheet, Text, View } from "react-native";
import { useFocusEffect, useNavigation } from "@react-navigation/native";
import type { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { getJournalPeriodThreadSummary, type ThreadPeriodQuery } from "../../api/dreamDiaryApi";
import { isUnauthorizedError } from "../../api/client";
import type { RootStackParamList } from "../../navigation/AppNavigator";
import { openThreadResult } from "../../navigation/threadResult";
import { colors } from "../../theme/colors";
import type { JournalThreadPeriodSummary } from "../../types/journalDay";
import { colorFromTextClass } from "./journalTagStyle";

/** 월간은 스레드가 많아 10개 초과 시 펼치기 (웹 MONTHLY_VISIBLE_LIMIT, 주간·일간 미적용) */
const MONTHLY_VISIBLE_LIMIT = 10;
/** 접힘 헤더에 미리 보여줄 스레드 수 */
const COLLAPSED_PREVIEW_COUNT = 2;
/** 웹 btn-light-primary 톤 */
const CHIP_BG = "#E9F3FF";
const CHIP_TEXT = "#1B84FF";

/** 패널 펼침 상태 — 앱 세션 동안 일/주/월 뷰가 공유한다(기본 펼침). 태그 패널과는 따로 기억한다. */
let rememberedOpen = true;

export type JournalPeriodThreadPanelProps = {
  /** 조회 기간 — 일: DAILY+stdrdDt, 주: WEEKLY+weekStartDt, 월: LIST+yy+mnth */
  query: ThreadPeriodQuery;
  /** 헤더 문구 ("이 날의 스레드" 등) */
  title: string;
};

/**
 * 기간 스레드 요약 패널 (접이식). 웹 JournalPeriodThreadSummary.vue 를 옮겼다.
 * 칩 = 말머리 배지(textClass 색, 테두리 같은 색) + 제목 + "N건". 서버 순서 유지(월간은 entryCount 내림차순).
 * 기간이 바뀌면 다시 불러오고, 화면 복귀 시 조용히 갱신한다.
 * 칩을 탭하면 스레드 글 목록 화면(ThreadResult)을 연다 — 웹 기간 스레드 요약 클릭(스레드 상세)에 해당한다.
 */
export function JournalPeriodThreadPanel({ query, title }: JournalPeriodThreadPanelProps) {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  const queryKey = JSON.stringify(query);
  const isMonthly = query.viewType === "LIST";
  const [open, setOpen] = useState(rememberedOpen);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [threads, setThreads] = useState<JournalThreadPeriodSummary[]>([]);
  const [expanded, setExpanded] = useState(false);
  const seqRef = useRef(0);
  const loadedKeyRef = useRef<string | null>(null);
  const mountedRef = useRef(true);

  useEffect(() => {
    mountedRef.current = true;
    return () => {
      mountedRef.current = false;
    };
  }, []);

  const load = useCallback(async () => {
    const seq = ++seqRef.current;
    const silent = loadedKeyRef.current === queryKey;
    if (!silent) {
      setLoading(true);
      setThreads([]);
      setExpanded(false);
    }
    setError(false);
    const target = JSON.parse(queryKey) as ThreadPeriodQuery;
    try {
      const list = await getJournalPeriodThreadSummary(target);
      if (!mountedRef.current || seq !== seqRef.current) return;
      setThreads(list.filter((t) => t != null && t.threadId != null));
      loadedKeyRef.current = queryKey;
    } catch (e) {
      if (!mountedRef.current || seq !== seqRef.current) return;
      if (!isUnauthorizedError(e)) {
        console.error("[JournalPeriodThreadPanel] period thread summary load failed", { query: target }, e);
      }
      setError(true);
      loadedKeyRef.current = null;
    } finally {
      if (mountedRef.current && seq === seqRef.current) setLoading(false);
    }
  }, [queryKey]);

  useFocusEffect(
    useCallback(() => {
      void load();
    }, [load])
  );

  function toggleOpen() {
    setOpen((prev) => {
      rememberedOpen = !prev;
      return !prev;
    });
  }

  const hasOverflow = isMonthly && threads.length > MONTHLY_VISIBLE_LIMIT;
  const visible = hasOverflow && !expanded ? threads.slice(0, MONTHLY_VISIBLE_LIMIT) : threads;

  let headerSub: string;
  if (loading) headerSub = "불러오는 중…";
  else if (error) headerSub = "불러오지 못했습니다";
  else if (threads.length === 0) headerSub = "스레드 없음";
  else if (open) headerSub = `${threads.length}개`;
  else {
    const names = threads.slice(0, COLLAPSED_PREVIEW_COUNT).map((t) => t.title).join(", ");
    const rest = threads.length - COLLAPSED_PREVIEW_COUNT;
    headerSub = rest > 0 ? `${names} 외 ${rest}개` : names;
  }

  return (
    <View style={styles.panel}>
      <Pressable
        accessibilityRole="button"
        accessibilityState={{ expanded: open }}
        accessibilityLabel={`${title}, ${open ? "접기" : "펼치기"}`}
        onPress={toggleOpen}
        style={styles.header}
      >
        <Text style={styles.headerTitle}>{title}</Text>
        <View style={styles.headerSubWrap}>
          {loading && <ActivityIndicator size="small" color={colors.muted} />}
          <Text style={[styles.headerSub, error && !loading && styles.headerError]} numberOfLines={1}>
            {headerSub}
          </Text>
        </View>
        <Text style={styles.caret}>{open ? "▾" : "▸"}</Text>
      </Pressable>

      {open && !loading && error && (
        <View style={styles.body}>
          <Pressable accessibilityRole="button" onPress={() => { void load(); }} hitSlop={6}>
            <Text style={styles.retryText}>다시 시도</Text>
          </Pressable>
        </View>
      )}

      {open && !loading && !error && threads.length > 0 && (
        <View style={[styles.body, styles.chips]}>
          {visible.map((thread) => {
            const prefixColor = colorFromTextClass(thread.prefix?.textClass) ?? colors.secondaryText;
            return (
              <Pressable
                key={thread.threadId}
                accessibilityRole="button"
                accessibilityLabel={`스레드 ${thread.prefix?.name ? `[${thread.prefix.name}] ` : ""}${thread.title}, ${thread.entryCount}건`}
                accessibilityHint="이 스레드의 글 목록 보기"
                onPress={() => openThreadResult(navigation, { threadId: thread.threadId, title: thread.title })}
                style={({ pressed }) => [styles.chip, pressed && styles.chipPressed]}
              >
                <Text style={styles.chipIcon}>🧵</Text>
                {thread.prefix?.name ? (
                  <View style={[styles.prefixBadge, { borderColor: prefixColor }]}>
                    <Text style={[styles.prefixText, { color: prefixColor }]}>{thread.prefix.name}</Text>
                  </View>
                ) : null}
                <Text style={styles.chipTitle} numberOfLines={1}>{thread.title}</Text>
                <Text style={styles.chipCount}>{thread.entryCount}건</Text>
              </Pressable>
            );
          })}
          {hasOverflow && (
            <Pressable accessibilityRole="button" onPress={() => setExpanded((v) => !v)} hitSlop={6} style={styles.moreButton}>
              <Text style={styles.moreText}>{expanded ? "접기 ▴" : `펼치기 (+${threads.length - MONTHLY_VISIBLE_LIMIT}) ▾`}</Text>
            </Pressable>
          )}
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  panel: {
    backgroundColor: colors.input,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 10,
    paddingHorizontal: 12,
    paddingVertical: 4
  },
  header: { flexDirection: "row", alignItems: "center", gap: 8, paddingVertical: 8 },
  headerTitle: { color: colors.text, fontSize: 13, fontWeight: "700" },
  headerSubWrap: { flex: 1, flexDirection: "row", alignItems: "center", gap: 6 },
  headerSub: { flexShrink: 1, color: colors.muted, fontSize: 12 },
  headerError: { color: "#C0392B" },
  caret: { color: colors.accent, fontSize: 12, fontWeight: "700" },
  body: {
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: colors.border,
    paddingTop: 8,
    paddingBottom: 10
  },
  chips: { flexDirection: "row", flexWrap: "wrap", alignItems: "center", gap: 6 },
  chip: {
    maxWidth: "100%",
    flexDirection: "row",
    alignItems: "center",
    gap: 4,
    backgroundColor: CHIP_BG,
    borderRadius: 8,
    paddingHorizontal: 10,
    paddingVertical: 5
  },
  chipPressed: { opacity: 0.6 },
  chipIcon: { fontSize: 11 },
  prefixBadge: { borderWidth: 1, borderRadius: 4, paddingHorizontal: 4, paddingVertical: 0 },
  prefixText: { fontSize: 10, fontWeight: "700" },
  chipTitle: { flexShrink: 1, color: CHIP_TEXT, fontSize: 12.5, fontWeight: "600" },
  chipCount: { color: colors.muted, fontSize: 10.5 },
  moreButton: { paddingHorizontal: 6, paddingVertical: 4 },
  moreText: { color: colors.accent, fontSize: 12, fontWeight: "700" },
  retryText: { color: colors.accent, fontSize: 12, fontWeight: "700", alignSelf: "flex-end" }
});