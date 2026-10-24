import { useCallback, useMemo, useRef, useState } from "react";
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useFocusEffect, useNavigation } from "@react-navigation/native";
import type { NativeStackNavigationProp } from "@react-navigation/native-stack";
import {
  getJournalDayTagCatalog,
  getJournalEntryTagCatalog,
  searchEntries,
  type TagCloudItem
} from "../api/dreamDiaryApi";
import { colors } from "../theme/colors";
import { stripHtml } from "../utils/text";
import { buildResultListCopyText } from "../utils/journalCopy";
import type { RootStackParamList } from "../navigation/AppNavigator";
import type { TagResultKind } from "../navigation/tagResult";
import type { DisplayTag, JournalEntry } from "../types/journalDay";
import { isUnauthorizedError } from "../api/client";
import { JournalTagChips } from "../components/journal/JournalTagChips";
import { JournalCopyShareBar } from "../components/journal/JournalCopyShareBar";
import { AppMenuButton } from "../components/navigation/AppMenu";

// ─── 타입 ───────────────────────────────────────────────────

type Nav = NativeStackNavigationProp<RootStackParamList>;

/** 검색 타입 필터: 일기 / 꿈 (백엔드 /api/journal/entries 는 type 필수 — '전체' 없음) */
type FilterType = "DIARY" | "DREAM";

const FILTERS: Array<{ id: FilterType; label: string }> = [
  { id: "DIARY", label: "일기" },
  { id: "DREAM", label: "꿈" }
];

/** 검색어와 이름이 맞는 태그 묶음 (종류별) — kind 는 태그별 기록 화면 종류와 같다 */
type TagMatchGroup = { kind: TagResultKind; label: string; tags: DisplayTag[] };

/** 태그 카탈로그 캐시 키 — 일자 태그 또는 일기/꿈 엔트리 태그 */
type TagCatalogKey = "DAY" | FilterType;

/** 접힘 상태에서 종류별로 보여줄 태그 수 (넘으면 `더보기`) */
const TAG_PREVIEW_LIMIT = 8;
/** 태그 카탈로그 재사용 시간 — 같은 화면에서 연속 검색할 때 카탈로그를 다시 받지 않는다 */
const TAG_CATALOG_TTL_MS = 60_000;

/** 태그 이름 비교용 정규화 — 대소문자 무시, 공백은 `_` 로 (웹 normalizeTagName 과 같은 공백 규칙) */
function normalizeTagKeyword(raw: string): string {
  return raw.trim().toLowerCase().replace(/\s+/g, "_");
}

/**
 * 카탈로그에서 이름이 검색어를 포함하는 태그를 고른다(부분 일치).
 * 정렬: 이름 정확 일치 → 앞부분 일치 → 사용 빈도(contentSize) 내림차순 → 이름순. 같은 tagId 는 한 번만.
 */
function matchTags(catalog: TagCloudItem[], keyword: string): DisplayTag[] {
  const q = normalizeTagKeyword(keyword);
  if (!q) return [];
  const seen = new Set<number>();
  const scored: Array<{ tag: DisplayTag; rank: number; size: number }> = [];
  for (const item of catalog) {
    const tagId = Number(item.id);
    const name = item.name.trim();
    if (!Number.isFinite(tagId) || !name || seen.has(tagId)) continue;
    const normalized = normalizeTagKeyword(name);
    const idx = normalized.indexOf(q);
    if (idx < 0) continue;
    seen.add(tagId);
    scored.push({
      tag: { tagId, name, ctgr: item.ctgr?.trim() || null },
      rank: normalized === q ? 0 : idx === 0 ? 1 : 2,
      size: item.contentSize
    });
  }
  return scored
    .sort((a, b) => a.rank - b.rank || b.size - a.size || a.tag.name.localeCompare(b.tag.name))
    .map((s) => s.tag);
}

// ─── 하위 컴포넌트 ──────────────────────────────────────────

function EntryCard({ entry }: { entry: JournalEntry }) {
  const navigation = useNavigation<Nav>();
  const isDream = entry.contentType === "JOURNAL_DREAM";
  const plain = entry.content ? stripHtml(entry.content) : "";
  const preview = plain.slice(0, 140);
  const hasMore = plain.length > 140;
  // stdrdDt가 있으면 YYYY.MM.DD 형태로 변환해 표시
  const dateLabel = entry.stdrdDt ? entry.stdrdDt.replace(/-/g, ".") : null;

  return (
    <Pressable
      accessibilityRole="button"
      onPress={() => navigation.navigate("EntryDetail", { entry, isDream })}
      style={[styles.entryCard, isDream && styles.entryCardDream]}
    >
      <View style={styles.cardMeta}>
        <View style={[styles.typeBadge, isDream && styles.typeBadgeDream]}>
          <Text style={[styles.typeBadgeText, isDream && styles.typeBadgeTextDream]}>
            {isDream ? "꿈" : "일기"}
          </Text>
        </View>
        {dateLabel ? (
          <Text style={styles.dateLabel}>{dateLabel}</Text>
        ) : null}
      </View>
      {entry.title ? (
        <Text style={[styles.entryTitle, isDream && styles.entryTitleDream]} numberOfLines={1}>
          {entry.title}
        </Text>
      ) : null}
      <Text style={[styles.entryContent, isDream && styles.entryContentDream]} numberOfLines={3}>
        {preview || "(내용 없음)"}
      </Text>
      {hasMore && (
        <Text style={[styles.entryMore, isDream && styles.entryMoreDream]}>더 보기 →</Text>
      )}
    </Pressable>
  );
}

// ─── 메인 화면 ──────────────────────────────────────────────

export function SearchScreen() {
  const [keyword, setKeyword] = useState("");
  const [filterType, setFilterType] = useState<FilterType>("DIARY");
  const [results, setResults] = useState<JournalEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  /** 최초 검색 실행 여부 — 빈 결과와 미검색 상태를 구별 */
  const [searched, setSearched] = useState(false);
  /** 마지막으로 실행한 검색 조건 (결과·태그·복사 머리줄 기준) */
  const [submitted, setSubmitted] = useState<{ keyword: string; type: FilterType } | null>(null);
  const [tagGroups, setTagGroups] = useState<TagMatchGroup[]>([]);
  const [tagLoading, setTagLoading] = useState(false);
  /** 태그 카탈로그 조회 실패 안내 (일부/전체 실패). null 이면 정상 */
  const [tagError, setTagError] = useState<string | null>(null);
  const [tagsExpanded, setTagsExpanded] = useState(false);
  const entrySeqRef = useRef(0);
  const submittedRef = useRef<{ keyword: string; type: FilterType } | null>(null);
  const tagSeqRef = useRef(0);
  const catalogCacheRef = useRef(new Map<TagCatalogKey, { at: number; list: TagCloudItem[] }>());
  /** 진행 중인 카탈로그 요청 — 같은 카탈로그를 동시에 두 번 받지 않는다 */
  const catalogInflightRef = useRef(new Map<TagCatalogKey, Promise<TagCloudItem[]>>());
  const inputRef = useRef<TextInput>(null);

  /** 태그 카탈로그 (TTL 캐시 + 진행 중 요청 공유). 실패는 캐시하지 않는다 */
  const loadTagCatalog = useCallback((key: TagCatalogKey): Promise<TagCloudItem[]> => {
    const cached = catalogCacheRef.current.get(key);
    if (cached && Date.now() - cached.at < TAG_CATALOG_TTL_MS) return Promise.resolve(cached.list);
    const inflight = catalogInflightRef.current.get(key);
    if (inflight) return inflight;
    const promise = (key === "DAY" ? getJournalDayTagCatalog() : getJournalEntryTagCatalog(key))
      .then((list) => {
        catalogCacheRef.current.set(key, { at: Date.now(), list });
        return list;
      })
      .finally(() => {
        catalogInflightRef.current.delete(key);
      });
    catalogInflightRef.current.set(key, promise);
    return promise;
  }, []);

  /**
   * 태그 매칭 — 엔트리 검색과 따로 조회한다(한쪽 실패가 다른 쪽을 막지 않음).
   * 일기 필터: 일기 태그 + 일자 태그. 꿈 필터: 꿈 태그만 (일자 태그는 넣지 않는다 — 사용자 결정).
   * 카탈로그 일부만 실패하면 받은 쪽 결과는 보여주고 "일부 …" 안내를 붙인다.
   */
  const searchTags = useCallback(async (kw: string, ft: FilterType) => {
    const seq = ++tagSeqRef.current;
    setTagLoading(true);
    setTagError(null);
    const sources: Array<{ key: TagCatalogKey; kind: TagResultKind; label: string }> =
      ft === "DREAM"
        ? [{ key: "DREAM", kind: "dream", label: "꿈 태그" }]
        : [
            { key: "DIARY", kind: "diary", label: "일기 태그" },
            { key: "DAY", kind: "day", label: "일자 태그" }
          ];
    const settled = await Promise.allSettled(sources.map((source) => loadTagCatalog(source.key)));
    if (seq !== tagSeqRef.current) return;

    const groups: TagMatchGroup[] = [];
    let failedCount = 0;
    settled.forEach((res, i) => {
      const source = sources[i];
      if (!source) return;
      if (res.status === "fulfilled") {
        const tags = matchTags(res.value, kw);
        if (tags.length > 0) groups.push({ kind: source.kind, label: source.label, tags });
        return;
      }
      failedCount += 1;
      if (!isUnauthorizedError(res.reason)) {
        console.warn("[SearchScreen] tag catalog load failed", { catalog: source.key }, res.reason);
      }
    });
    setTagGroups(groups);
    setTagError(
      failedCount === 0
        ? null
        : failedCount === sources.length
          ? "태그 목록을 불러오지 못했습니다."
          : "일부 태그 목록을 불러오지 못했습니다."
    );
    setTagLoading(false);
  }, [loadTagCatalog]);

  const doSearch = useCallback(async (kw: string, ft: FilterType) => {
    const trimmed = kw.trim();
    if (trimmed.length === 0) return;

    const seq = ++entrySeqRef.current;
    setLoading(true);
    setError(null);
    setSearched(true);
    const prev = submittedRef.current;
    if (prev?.keyword !== trimmed || prev.type !== ft) {
      // 다른 검색 조건이면 이전 태그 칩을 바로 지운다 (새 카탈로그 매칭이 끝날 때까지 남아 있지 않게)
      setTagsExpanded(false);
      setTagGroups([]);
      setTagError(null);
    }
    submittedRef.current = { keyword: trimmed, type: ft };
    setSubmitted({ keyword: trimmed, type: ft });
    void searchTags(trimmed, ft);

    try {
      const type = ft;
      const res = await searchEntries({ keyword: trimmed, type });
      if (seq !== entrySeqRef.current) return;
      setResults(res.rsltList ?? []);
    } catch (e) {
      if (seq !== entrySeqRef.current) return;
      if (!isUnauthorizedError(e)) console.error("[SearchScreen] entry search failed", { keyword: trimmed, filterType: ft }, e);
      setError(e instanceof Error ? e.message : "검색 중 오류가 발생했습니다.");
    } finally {
      if (seq === entrySeqRef.current) setLoading(false);
    }
  }, [searchTags]);

  /** 표시·복사 순서 — 날짜 내림차순(최신 먼저), 같은 날짜·날짜 없음은 서버 순서 유지(날짜 없는 결과는 맨 뒤) */
  const sortedResults = useMemo(
    () =>
      results
        .map((entry, index) => ({ entry, index }))
        .sort((a, b) => (b.entry.stdrdDt ?? "").localeCompare(a.entry.stdrdDt ?? "") || a.index - b.index)
        .map((item) => item.entry),
    [results]
  );

  const tagTotal = tagGroups.reduce((sum, g) => sum + g.tags.length, 0);
  const hiddenTagCount = tagGroups.reduce((sum, g) => sum + Math.max(0, g.tags.length - TAG_PREVIEW_LIMIT), 0);

  /** 현재 결과 목록 전체 복사 텍스트 (머리줄 = 검색어·필터·건수, 엔트리는 화면 순서) */
  function buildSearchCopyText(): string {
    const filterLabel = FILTERS.find((f) => f.id === submitted?.type)?.label ?? "";
    const header = [`검색: "${submitted?.keyword ?? ""}" · ${filterLabel} · ${sortedResults.length}건`];
    return buildResultListCopyText(header, sortedResults.map((entry) => ({ entry })));
  }

  /**
   * 필터 변경 — 이미 검색을 제출했으면 "마지막으로 제출한 검색어"로 재검색한다.
   * 입력창에서 고치는 중인 글자는 쓰지 않는다(검색은 검색 버튼·키보드 검색 키로만 제출).
   */
  function handleFilterChange(ft: FilterType) {
    if (ft === filterType) return;
    setFilterType(ft);
    const last = submittedRef.current;
    if (last) void doSearch(last.keyword, ft);
  }

  /** 입력창 ✕ — 입력 글자만 지운다(결과·태그·마지막 검색 조건은 그대로) */
  function clearInput() {
    setKeyword("");
    inputRef.current?.focus();
  }

  /**
   * 초기화 — 입력·결과·태그 칩·마지막 검색 조건을 비운다. 일기/꿈 필터는 그대로 둔다.
   * 진행 중인 요청의 응답은 순번(ref)을 올려 버린다.
   */
  function resetSearch() {
    entrySeqRef.current += 1;
    tagSeqRef.current += 1;
    submittedRef.current = null;
    setKeyword("");
    setResults([]);
    setLoading(false);
    setError(null);
    setSearched(false);
    setSubmitted(null);
    setTagGroups([]);
    setTagLoading(false);
    setTagError(null);
    setTagsExpanded(false);
  }

  /**
   * 화면에 다시 들어오면 마지막으로 제출한 검색을 새로 고친다.
   * 입력 글자(keyword)는 의존성에 넣지 않는다 — 타이핑만으로는 요청이 나가지 않아야 한다.
   */
  useFocusEffect(
    useCallback(() => {
      const last = submittedRef.current;
      if (last) void doSearch(last.keyword, last.type);
    }, [doSearch])
  );

  return (
    <SafeAreaView edges={["top", "left", "right"]} style={styles.safeArea}>
      {/* 키보드가 올라올 때 검색 입력 영역이 가려지지 않게 iOS/Android 모두 대응 */}
      <KeyboardAvoidingView
        behavior={Platform.select({ ios: "padding", android: undefined })}
        style={styles.keyboardArea}
      >
      <ScrollView
        contentContainerStyle={styles.container}
        keyboardShouldPersistTaps="handled"
      >
        {/* 헤더 */}
        <View style={styles.header}>
          {/* 검색은 웹 사용자 메뉴에 없어 햄버거 메뉴에서 강조할 항목이 없다 */}
          <View style={styles.kickerRow}>
            <AppMenuButton />
            <Text style={styles.kicker}>DreamDiary</Text>
          </View>
          <Text style={styles.title}>검색</Text>
        </View>

        {/* 검색 입력 */}
        <View style={styles.searchRow}>
          <View style={styles.searchInputWrap}>
            <TextInput
              ref={inputRef}
              autoCapitalize="none"
              autoCorrect={false}
              onChangeText={setKeyword}
              onSubmitEditing={() => { void doSearch(keyword, filterType); }}
              placeholder="키워드를 입력하세요"
              placeholderTextColor={colors.muted}
              returnKeyType="search"
              style={styles.searchInput}
              value={keyword}
            />
            {keyword.length > 0 && (
              <Pressable
                accessibilityRole="button"
                accessibilityLabel="검색어 지우기"
                hitSlop={8}
                onPress={clearInput}
                style={styles.clearButton}
              >
                <Text style={styles.clearButtonText}>✕</Text>
              </Pressable>
            )}
          </View>
          <Pressable
            accessibilityRole="button"
            disabled={keyword.trim().length === 0 || loading}
            onPress={() => { void doSearch(keyword, filterType); }}
            style={[
              styles.searchButton,
              (keyword.trim().length === 0 || loading) && styles.searchButtonDisabled
            ]}
          >
            {loading
              ? <ActivityIndicator size="small" color={colors.onAccent} />
              : <Text style={styles.searchButtonText}>검색</Text>
            }
          </Pressable>
        </View>

        {/* 타입 필터 */}
        <View style={styles.filterRow}>
          {FILTERS.map(f => {
            const active = f.id === filterType;
            return (
              <Pressable
                accessibilityRole="button"
                accessibilityState={{ selected: active }}
                key={f.id}
                onPress={() => handleFilterChange(f.id)}
                style={[styles.filterChip, active && styles.filterChipActive]}
              >
                <Text style={[styles.filterChipText, active && styles.filterChipTextActive]}>
                  {f.label}
                </Text>
              </Pressable>
            );
          })}
          {searched && (
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="검색 초기화"
              accessibilityHint="검색어, 결과, 태그를 비웁니다 (필터는 유지)"
              hitSlop={8}
              onPress={resetSearch}
              style={styles.resetButton}
            >
              <Text style={styles.resetButtonText}>초기화</Text>
            </Pressable>
          )}
        </View>

        {/* 태그 매칭 (엔트리 결과와 별도 조회, 일치 태그가 없으면 숨김) */}
        {searched && tagGroups.length > 0 && (
          <View style={styles.tagSection}>
            <Text style={styles.resultCount}>{`태그 ${tagTotal}개`}</Text>
            {tagGroups.map((group) => (
              <View key={group.kind} style={styles.tagGroup}>
                <Text style={styles.tagGroupLabel}>{group.label}</Text>
                <JournalTagChips
                  tags={tagsExpanded ? group.tags : group.tags.slice(0, TAG_PREVIEW_LIMIT)}
                  variant={group.kind}
                />
              </View>
            ))}
            {hiddenTagCount > 0 && (
              <Pressable
                accessibilityRole="button"
                onPress={() => setTagsExpanded((v) => !v)}
                hitSlop={6}
                style={styles.tagMoreButton}
              >
                <Text style={styles.tagMoreText}>
                  {tagsExpanded ? "접기 ▴" : `더보기 (+${hiddenTagCount}) ▾`}
                </Text>
              </Pressable>
            )}
          </View>
        )}
        {searched && tagLoading && tagGroups.length === 0 && (
          <Text style={styles.tagStatusText}>태그 찾는 중…</Text>
        )}
        {searched && !tagLoading && tagError != null && (
          <Text style={styles.tagStatusText}>{tagError}</Text>
        )}

        {/* 결과 영역 */}
        {!loading && error != null && (
          <View style={styles.center}>
            <Text style={styles.errorText}>{error}</Text>
          </View>
        )}

        {!loading && !error && searched && results.length === 0 && (
          <View style={styles.center}>
            <Text style={styles.emptyText}>검색 결과가 없습니다.</Text>
          </View>
        )}

        {!loading && !error && results.length > 0 && (
          <View style={styles.resultList}>
            <JournalCopyShareBar
              label="검색 결과"
              buildText={buildSearchCopyText}
              shareTitle={`검색 ${submitted?.keyword ?? ""}`.trim()}
              copiedMessage={`검색 결과 ${sortedResults.length}건을 복사했습니다.`}
            />
            <Text style={styles.resultCount}>{results.length}건</Text>
            {sortedResults.map(entry => (
              <EntryCard key={entry.id} entry={entry} />
            ))}
          </View>
        )}
      </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

// ─── 스타일 ─────────────────────────────────────────────────

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: colors.background },
  keyboardArea: { flex: 1 },
  container: { padding: 20, gap: 16, flexGrow: 1 },
  // 헤더
  header: { gap: 2 },
  kickerRow: { flexDirection: "row", alignItems: "center", gap: 10, marginBottom: 4 },
  kicker: {
    color: colors.accent,
    fontSize: 11,
    fontWeight: "700",
    letterSpacing: 1.5,
    textTransform: "uppercase"
  },
  title: { color: colors.text, fontSize: 26, fontWeight: "800" },
  // 검색 입력행
  searchRow: { flexDirection: "row", gap: 8, alignItems: "center" },
  searchInputWrap: { flex: 1, justifyContent: "center" },
  searchInput: {
    backgroundColor: colors.input,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: colors.border,
    paddingLeft: 14,
    // 오른쪽 ✕ 버튼 자리
    paddingRight: 38,
    paddingVertical: 10,
    color: colors.text,
    fontSize: 15
  },
  clearButton: {
    position: "absolute",
    right: 8,
    width: 24,
    height: 24,
    borderRadius: 12,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: colors.border
  },
  clearButtonText: { color: colors.secondaryText, fontSize: 12, fontWeight: "700" },
  searchButton: {
    backgroundColor: colors.accent,
    borderRadius: 8,
    paddingHorizontal: 16,
    paddingVertical: 10,
    minWidth: 60,
    alignItems: "center",
    justifyContent: "center"
  },
  searchButtonDisabled: { opacity: 0.45 },
  searchButtonText: { color: colors.onAccent, fontSize: 15, fontWeight: "700" },
  // 타입 필터
  filterRow: { flexDirection: "row", gap: 8, alignItems: "center" },
  resetButton: { marginLeft: "auto", paddingHorizontal: 4, paddingVertical: 6 },
  resetButtonText: { color: colors.muted, fontSize: 13, fontWeight: "600", textDecorationLine: "underline" },
  filterChip: {
    paddingHorizontal: 14,
    paddingVertical: 6,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface
  },
  filterChipActive: { backgroundColor: colors.accent, borderColor: colors.accent },
  filterChipText: { color: colors.secondaryText, fontSize: 13, fontWeight: "600" },
  filterChipTextActive: { color: colors.onAccent },
  // 결과 목록
  resultList: { gap: 10 },
  resultCount: {
    color: colors.muted,
    fontSize: 12,
    fontWeight: "700",
    textTransform: "uppercase",
    letterSpacing: 0.5
  },
  // 태그 매칭
  tagSection: {
    gap: 8,
    padding: 12,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.input
  },
  tagGroup: { gap: 4 },
  tagGroupLabel: { color: colors.secondaryText, fontSize: 11.5, fontWeight: "700" },
  tagMoreButton: { alignSelf: "flex-start", paddingVertical: 2 },
  tagMoreText: { color: colors.accent, fontSize: 12, fontWeight: "700" },
  tagStatusText: { color: colors.muted, fontSize: 12 },
  // 상태
  center: { paddingVertical: 40, alignItems: "center" },
  errorText: { color: "#C0392B", fontSize: 14 },
  emptyText: { color: colors.secondaryText, fontSize: 15 },
  // 엔트리 카드
  entryCard: {
    backgroundColor: colors.surface,
    borderRadius: 10,
    padding: 14,
    gap: 6,
    borderLeftWidth: 3,
    borderLeftColor: colors.border
  },
  entryCardDream: { backgroundColor: "#F5EEF8", borderLeftColor: "#8E44AD" },
  cardMeta: { flexDirection: "row", alignItems: "center", gap: 8 },
  typeBadge: {
    paddingHorizontal: 8,
    paddingVertical: 2,
    borderRadius: 10,
    backgroundColor: colors.border
  },
  typeBadgeDream: { backgroundColor: "#E8DAEF" },
  typeBadgeText: { color: colors.secondaryText, fontSize: 11, fontWeight: "700" },
  typeBadgeTextDream: { color: "#6C3483" },
  dateLabel: { color: colors.muted, fontSize: 11 },
  entryTitle: { color: colors.text, fontSize: 14, fontWeight: "700" },
  entryTitleDream: { color: "#5B2C6F" },
  entryContent: { color: colors.secondaryText, fontSize: 14, lineHeight: 20 },
  entryContentDream: { color: "#6C3483" },
  entryMore: { color: colors.accent, fontSize: 12, fontWeight: "600" },
  entryMoreDream: { color: "#8E44AD" }
});
