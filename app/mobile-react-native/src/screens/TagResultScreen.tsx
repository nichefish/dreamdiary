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
import {
  getJournalDayTagYears,
  getTagProfile,
  searchEntries,
  searchJournalDaysByTag
} from "../api/dreamDiaryApi";
import { isUnauthorizedError } from "../api/client";
import type { RootStackParamList } from "../navigation/AppNavigator";
import { navigateToDailyHub } from "../navigation/dailyHub";
import type { TagResultKind } from "../navigation/tagResult";
import { colors } from "../theme/colors";
import { displayTagsOf, type DisplayTag, type JournalDay, type JournalEntry } from "../types/journalDay";
import { normalizeDateStr } from "../utils/date";
import { buildResultListCopyText, copyDateLabel, entriesOfDay, htmlToPlainText } from "../utils/journalCopy";
import { JournalTagChips, JournalThreadChips } from "../components/journal/JournalTagChips";
import { JournalCopyShareBar } from "../components/journal/JournalCopyShareBar";
import { TAG_CHIP_PALETTE, TAG_CTGR_COLOR } from "../components/journal/journalTagStyle";

type Props = NativeStackScreenProps<RootStackParamList, "TagResult">;

/** 연도 선택값 — "all" 은 전체 연도 (웹 일자 필터 모달의 빈 연도 옵션과 같음) */
type YearValue = number | "all";

type ResultGroup = {
  /** YYYY-MM-DD (날짜를 알 수 없으면 "") */
  date: string;
  entries: JournalEntry[];
  /** 일자 태그 결과일 때 그 날의 일자 태그 */
  dayTags: DisplayTag[];
};

const KIND_META: Record<TagResultKind, { label: string; contentType: string; entryType?: "DIARY" | "DREAM" }> = {
  day: { label: "일자 태그", contentType: "JOURNAL_DAY" },
  diary: { label: "일기 태그", contentType: "JOURNAL_DIARY", entryType: "DIARY" },
  dream: { label: "꿈 태그", contentType: "JOURNAL_DREAM", entryType: "DREAM" }
};

const PREVIEW_LENGTH = 140;

function errorMessage(e: unknown, fallback: string): string {
  return e instanceof Error && e.message ? e.message : fallback;
}

function yearOf(date: string): number | null {
  const y = Number(date.slice(0, 4));
  return date.length >= 4 && Number.isFinite(y) ? y : null;
}

function entryDate(entry: JournalEntry, fallback?: string): string {
  return normalizeDateStr(entry.stdrdDt ?? "") ?? (fallback ? normalizeDateStr(fallback) ?? "" : "");
}

function profileText(content: string | null | undefined): string {
  const raw = content?.trim() ?? "";
  if (!raw) return "";
  return /<[a-z][\s\S]*>/i.test(raw) ? htmlToPlainText(raw).trim() : raw;
}

/** 결과 엔트리 카드 — 탭하면 엔트리 상세 */
function ResultEntryCard({ entry, onPress }: { entry: JournalEntry; onPress: () => void }) {
  const isDream = entry.contentType === "JOURNAL_DREAM";
  const preview = htmlToPlainText(entry.content).replace(/\s+/g, " ").trim().slice(0, PREVIEW_LENGTH);
  const tags = displayTagsOf(entry.tag);

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityHint="엔트리 상세 열기"
      onPress={onPress}
      style={({ pressed }) => [styles.entryCard, isDream && styles.entryCardDream, pressed && styles.pressed]}
    >
      <Text style={[styles.entryKind, isDream && styles.entryKindDream]}>
        {isDream ? "꿈" : "일기"}
        {entry.sortOrder != null ? ` #${entry.sortOrder}` : ""}
      </Text>
      <Text style={[styles.entryBody, isDream && styles.entryBodyDream]} numberOfLines={3}>
        {preview || "(내용 없음)"}
      </Text>
      <JournalTagChips tags={tags} variant={isDream ? "dream" : "diary"} />
      <JournalThreadChips threads={entry.threadList} />
    </Pressable>
  );
}

/**
 * 태그별 기록 화면 — 웹 태그 메뉴의 "검색"(읽기 전용)을 모바일 스택 화면으로 옮겼다.
 * - 일자 태그: GET /api/journal/day/tag/{tagId}/years 로 연도 목록 → GET /api/journal/days?viewType=SEARCH&tagId=&yy=
 *   (웹 openDayFilterModal 과 같이 현재 연도가 있으면 현재 연도, 없으면 최근 연도. "전체"는 yy 생략)
 * - 일기·꿈 태그: GET /api/journal/entries?tagIds=&type=DIARY|DREAM (웹 엔트리 검색 팝업과 같은 요청), 연도는 결과에서 뽑아 화면에서 거른다
 * - 태그 설명: GET /api/tags/{tagId}/profile?contentType= (웹 "태그 프로필" 의 읽기 부분만, 편집·삭제 없음)
 * - 날짜 헤더 탭 → 오늘 탭 일 모드로 그 날짜, 엔트리 탭 → 엔트리 상세, 카드 안 태그 칩 탭 → 다른 태그 화면
 */
export function TagResultScreen({ route, navigation }: Props) {
  const { tagId, name, ctgr, kind } = route.params;
  const meta = KIND_META[kind];
  const palette = TAG_CHIP_PALETTE[kind];

  const [years, setYears] = useState<number[]>([]);
  const [year, setYear] = useState<YearValue | null>(null);
  const [dayResults, setDayResults] = useState<JournalDay[]>([]);
  const [entryResults, setEntryResults] = useState<JournalEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [profile, setProfile] = useState<string>("");
  const seqRef = useRef(0);

  // 태그 설명 (실패해도 화면은 그대로 — 웹도 실패 시 기본 정보만 보여준다)
  useEffect(() => {
    let alive = true;
    getTagProfile(tagId, meta.contentType)
      .then((p) => {
        if (alive) setProfile(profileText(p?.content));
      })
      .catch((e) => {
        if (!isUnauthorizedError(e)) console.warn("[TagResultScreen] tag profile load failed", { tagId, kind }, e);
      });
    return () => {
      alive = false;
    };
  }, [kind, meta.contentType, tagId]);

  /** 일자 태그: 연도 목록 → 기본 연도 결정 */
  const loadDayYears = useCallback(async () => {
    const current = new Date().getFullYear();
    try {
      const res = await getJournalDayTagYears(tagId);
      const list = Array.from(
        new Set((res.rsltList ?? []).map((y) => Number(y)).filter((y) => Number.isFinite(y)))
      ).sort((a, b) => b - a);
      setYears(list);
      setYear((prev) => prev ?? (list.includes(current) ? current : (list[0] ?? current)));
    } catch (e) {
      if (!isUnauthorizedError(e)) console.warn("[TagResultScreen] day tag years load failed", { tagId }, e);
      setYears([]);
      setYear((prev) => prev ?? current);
    }
  }, [tagId]);

  /** 일자 태그: 선택 연도의 일자 목록 */
  const loadDayResults = useCallback(async (target: YearValue, silent = false) => {
    const seq = ++seqRef.current;
    if (!silent) setLoading(true);
    setError(null);
    try {
      const res = await searchJournalDaysByTag(tagId, target === "all" ? null : target);
      if (seq !== seqRef.current) return;
      setDayResults(res.rsltList ?? []);
    } catch (e) {
      if (seq !== seqRef.current) return;
      if (!isUnauthorizedError(e)) console.error("[TagResultScreen] day tag results load failed", { tagId, target }, e);
      setError(errorMessage(e, "일자 목록을 불러오지 못했습니다."));
      setDayResults([]);
    } finally {
      if (seq === seqRef.current) {
        setLoading(false);
        setRefreshing(false);
      }
    }
  }, [tagId]);

  /** 일기·꿈 태그: 엔트리 전체 (연도는 화면에서 거름) */
  const loadEntryResults = useCallback(async (silent = false) => {
    if (!meta.entryType) return;
    const seq = ++seqRef.current;
    if (!silent) setLoading(true);
    setError(null);
    try {
      const res = await searchEntries({ tagIds: [tagId], type: meta.entryType });
      if (seq !== seqRef.current) return;
      const list = res.rsltList ?? [];
      setEntryResults(list);
      const ys = Array.from(
        new Set(list.map((e) => yearOf(entryDate(e))).filter((y): y is number => y != null))
      ).sort((a, b) => b - a);
      setYears(ys);
      setYear((prev) => (prev == null || (prev !== "all" && !ys.includes(prev)) ? "all" : prev));
    } catch (e) {
      if (seq !== seqRef.current) return;
      if (!isUnauthorizedError(e)) console.error("[TagResultScreen] entry tag results load failed", { tagId, kind }, e);
      setError(errorMessage(e, "엔트리 목록을 불러오지 못했습니다."));
      setEntryResults([]);
    } finally {
      if (seq === seqRef.current) {
        setLoading(false);
        setRefreshing(false);
      }
    }
  }, [kind, meta.entryType, tagId]);

  // 최초 진입
  useEffect(() => {
    if (kind === "day") void loadDayYears();
    else void loadEntryResults();
  }, [kind, loadDayYears, loadEntryResults]);

  // 일자 태그: 연도가 정해지거나 바뀌면 목록 조회
  useEffect(() => {
    if (kind !== "day" || year == null) return;
    void loadDayResults(year);
  }, [kind, loadDayResults, year]);

  const groups = useMemo<ResultGroup[]>(() => {
    if (kind === "day") {
      return dayResults
        .map((day) => {
          const date = normalizeDateStr(day.stdrdDt) ?? day.stdrdDt;
          return {
            date,
            entries: entriesOfDay(day).map((d) => d.entry),
            dayTags: displayTagsOf(day.tag)
          };
        })
        .sort((a, b) => b.date.localeCompare(a.date));
    }
    const byDate = new Map<string, JournalEntry[]>();
    for (const entry of entryResults) {
      const date = entryDate(entry);
      if (year != null && year !== "all" && yearOf(date) !== year) continue;
      const list = byDate.get(date);
      if (list) list.push(entry);
      else byDate.set(date, [entry]);
    }
    return Array.from(byDate.entries())
      .map(([date, entries]) => ({
        date,
        entries: entries.slice().sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0)),
        dayTags: []
      }))
      .sort((a, b) => b.date.localeCompare(a.date));
  }, [dayResults, entryResults, kind, year]);

  const entryCount = groups.reduce((sum, g) => sum + g.entries.length, 0);

  function onRefresh() {
    setRefreshing(true);
    if (kind === "day") {
      void loadDayYears();
      if (year != null) void loadDayResults(year, true);
      else setRefreshing(false);
    } else {
      void loadEntryResults(true);
    }
  }

  function retry() {
    if (kind === "day") {
      if (year != null) void loadDayResults(year);
      else void loadDayYears();
    } else {
      void loadEntryResults();
    }
  }

  /** 화면 순서(날짜 내림차순, 날짜 안은 표시 순서) 그대로 복사 텍스트를 만든다 */
  function buildCopyText(): string {
    const tagLabel = `#${ctgr ? `[${ctgr}]` : ""}${name}`;
    const yearLabel = year == null || year === "all" ? "전체 연도" : `${year}년`;
    const header = [`${meta.label} ${tagLabel}`, `${yearLabel} · ${groups.length}일 · ${entryCount}건`];
    return buildResultListCopyText(
      header,
      groups.flatMap((group) => group.entries.map((entry) => ({ entry, stdrdDt: group.date || null })))
    );
  }

  const yearOptions: YearValue[] = ["all", ...years];
  if (year != null && year !== "all" && !years.includes(year)) yearOptions.push(year);

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
          태그별 기록
        </Text>
      </View>

      <ScrollView
        contentContainerStyle={styles.container}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
      >
        {/* 태그 정보 */}
        <View style={[styles.tagCard, { backgroundColor: palette.background, borderColor: palette.border }]}>
          <Text style={styles.kindLabel}>{meta.label}</Text>
          <Text style={styles.tagTitle} accessibilityRole="header">
            <Text style={styles.tagHash}>#</Text>
            {ctgr ? <Text style={styles.tagCtgr}>[{ctgr}]</Text> : null}
            <Text style={{ color: palette.name }}>{name}</Text>
          </Text>
          {profile ? <Text style={styles.profileText}>{profile}</Text> : null}
        </View>

        {/* 연도 선택 */}
        {yearOptions.length > 1 || kind === "day" ? (
          <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.yearRow}>
            {yearOptions.map((y) => {
              const selected = y === year;
              return (
                <Pressable
                  key={String(y)}
                  accessibilityRole="button"
                  accessibilityState={{ selected }}
                  onPress={() => setYear(y)}
                  style={[styles.yearChip, selected && styles.yearChipSelected]}
                >
                  <Text style={[styles.yearChipText, selected && styles.yearChipTextSelected]}>
                    {y === "all" ? "전체" : `${y}년`}
                  </Text>
                </Pressable>
              );
            })}
          </ScrollView>
        ) : null}

        {loading && !refreshing ? (
          <ActivityIndicator style={styles.loader} color={colors.accent} />
        ) : error ? (
          <View style={styles.stateBox}>
            <Text style={styles.errorText}>{error}</Text>
            <Pressable accessibilityRole="button" onPress={retry} style={styles.retryButton}>
              <Text style={styles.retryText}>다시 시도</Text>
            </Pressable>
          </View>
        ) : groups.length === 0 ? (
          <View style={styles.stateBox}>
            <Text style={styles.emptyText}>
              {kind === "day" ? "이 태그가 붙은 날이 없습니다." : "이 태그가 붙은 기록이 없습니다."}
            </Text>
          </View>
        ) : (
          <>
            {entryCount > 0 ? (
              <JournalCopyShareBar
                label="이 태그 기록"
                buildText={buildCopyText}
                shareTitle={`${meta.label} ${name}`}
                copiedMessage={`태그 기록 ${entryCount}건을 복사했습니다.`}
              />
            ) : null}
            <Text style={styles.summary}>
              {kind === "day" ? `${groups.length}일` : `${groups.length}일 · ${entryCount}건`}
            </Text>
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
                {group.dayTags.length > 0 ? <JournalTagChips tags={group.dayTags} variant="day" /> : null}
                {group.entries.map((entry, idx) => (
                  <ResultEntryCard
                    key={entry.id != null ? `e${entry.id}` : `${group.date}-${idx}`}
                    entry={entry}
                    onPress={() =>
                      navigation.push("EntryDetail", {
                        entry,
                        isDream: entry.contentType === "JOURNAL_DREAM",
                        ...(group.dayTags.length > 0 ? { dayTags: group.dayTags } : {})
                      })
                    }
                  />
                ))}
                {kind === "day" && group.entries.length === 0 ? (
                  <Text style={styles.noEntry}>엔트리 없음</Text>
                ) : null}
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
  tagCard: { borderWidth: 1, borderRadius: 12, padding: 14, gap: 6 },
  kindLabel: { fontSize: 12, color: colors.secondaryText, fontWeight: "600" },
  tagTitle: { fontSize: 20, fontWeight: "700" },
  tagHash: { color: colors.muted, fontWeight: "400" },
  tagCtgr: { color: TAG_CTGR_COLOR, fontSize: 14, fontWeight: "500" },
  profileText: { fontSize: 13.5, lineHeight: 20, color: colors.text },
  yearRow: { gap: 6, paddingVertical: 2 },
  yearChip: {
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 14,
    paddingHorizontal: 12,
    paddingVertical: 5,
    backgroundColor: colors.input
  },
  yearChipSelected: { backgroundColor: colors.accent, borderColor: colors.accent },
  yearChipText: { fontSize: 13, color: colors.text, fontWeight: "600" },
  yearChipTextSelected: { color: colors.onAccent },
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
  summary: { fontSize: 12.5, color: colors.secondaryText },
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
  noEntry: { fontSize: 12.5, color: colors.muted },
  entryCard: {
    backgroundColor: colors.input,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 10,
    padding: 12,
    gap: 6
  },
  entryCardDream: { backgroundColor: "#F5EEF8", borderColor: "#D7BDE2" },
  entryKind: { fontSize: 11.5, fontWeight: "700", color: colors.accent },
  entryKindDream: { color: "#8E44AD" },
  entryBody: { fontSize: 14, lineHeight: 20, color: colors.text },
  entryBodyDream: { color: "#4A235A" },
  pressed: { opacity: 0.7 }
});