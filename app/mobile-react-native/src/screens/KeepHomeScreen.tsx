import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import {
  ActivityIndicator,
  FlatList,
  Pressable,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useFocusEffect, useNavigation } from "@react-navigation/native";
import type { NativeStackNavigationProp } from "@react-navigation/native-stack";
import { getKeepList, getKeepPrefixOptions, getKeepProjects, getKeepTags } from "../api/dreamDiaryApi";
import { isUnauthorizedError } from "../api/client";
import type { RootStackParamList } from "../navigation/AppNavigator";
import { MENU_URL } from "../navigation/appMenu";
import { colors } from "../theme/colors";
import { displayTagsOf, type DisplayTag } from "../types/journalDay";
import {
  isKeepTerminal,
  keepHasState,
  keepLifecycleKeyOf,
  keepLifecycleMeta,
  keepProjectTitle,
  openKeepProjects,
  type KeepItem,
  type KeepPrefixOption,
  type KeepProject,
  type KeepTagOption
} from "../types/prospectiveKeep";
import { daysUntilExpire, ddayLabel, formatExpireDt, normalizeExpireDt } from "../utils/keepExpire";
import {
  dueKeepGroups,
  EMPTY_KEEP_FILTERS,
  filterKeeps,
  isKeepOverdueOpen,
  keepRegisteredMonth,
  keepViewCounts,
  mergeKeepPrefixOptions,
  mergeKeepTagOptions,
  openKeeps,
  terminalKeeps,
  type KeepDueBucket,
  type KeepFilters,
  type KeepProjectScope,
  type KeepView
} from "../utils/keepProjection";
import { buildKeepListCopyText, type KeepCopySection } from "../utils/keepCopy";
import { MenuScreenHeader } from "../components/navigation/AppMenu";
import { JournalCopyShareBar, notifyBrief } from "../components/journal/JournalCopyShareBar";
import { KeepLifecycleBadge, KeepPrefixText, KeepStateBadge, KeepTagChips } from "../components/keep/KeepBadges";
import { KeepFilterSheet } from "../components/keep/KeepFilterSheet";

const VIEWS: { id: KeepView; label: string }[] = [
  { id: "OPEN", label: "진행" },
  { id: "DUE", label: "기한" },
  { id: "TERMINAL", label: "종결" }
];

const EMPTY_TEXT: Record<KeepView, string> = {
  OPEN: "진행 중인 Keep이 없습니다.",
  DUE: "기한이 있는 진행 중 Keep이 없습니다.",
  TERMINAL: "종결된 Keep이 없습니다."
};

/** 기한 묶음 머리 색 — 지남은 경고색(웹 text-warning), 오늘은 강조 */
const DUE_HEADER_COLOR: Record<KeepDueBucket, string> = {
  PAST: "#B7791F",
  TODAY: colors.accent,
  WEEK: colors.secondaryText,
  LATER: colors.muted
};

/** 지난 기한(미종결) 경고색 — 웹 `text-warning fw-bold` */
const OVERDUE_COLOR = "#B7791F";

type Row =
  | { kind: "header"; key: string; label: string; count: number; bucket: KeepDueBucket }
  | { kind: "item"; key: string; item: KeepItem };

function errorMessage(e: unknown, fallback: string): string {
  return e instanceof Error && e.message ? e.message : fallback;
}

type KeepRowProps = {
  item: KeepItem;
  today: Date;
  /** 표시할 Project 이름 (전체·미지정 범위에서만 넘긴다) */
  projectTitle: string;
  selectedTagId: number | null;
  onPress: () => void;
  onPressTag: (tag: DisplayTag) => void;
};

/**
 * Keep 행 — 웹 KeepPage 행 표기: 중요 별·굵은 제목, 보류·요구 배지, `[말머리]`, 태그 칩, 기한(진행 행의 지난 기한은 경고색 + N일 지남), 등록 년월.
 * 읽기 전용: 완료 체크박스·스와이프 없음. 종결 행은 끝난 이유 아이콘·배지를 보인다.
 */
function KeepRow({ item, today, projectTitle, selectedTagId, onPress, onPressTag }: KeepRowProps) {
  const lifecycleKey = keepLifecycleKeyOf(item);
  const terminal = isKeepTerminal(item);
  const meta = keepLifecycleMeta(lifecycleKey);
  const important = keepHasState(item, "IMPRTC");
  const demanded = keepHasState(item, "DEMANDED");
  const expire = normalizeExpireDt(item.expireDt);
  const days = expire ? daysUntilExpire(expire, today) : Number.NaN;
  const overdue = isKeepOverdueOpen(item, today);
  const expireText = expire
    ? `${formatExpireDt(expire, today)}${!terminal && ddayLabel(days) ? ` · ${ddayLabel(days)}` : ""}`
    : "";
  const tags = displayTagsOf(item.tag);
  const month = keepRegisteredMonth(item);
  const dateText = item.dateText?.trim() ?? "";
  const showBadges = lifecycleKey === "PENDING" || terminal || demanded;

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityHint="Keep 상세 보기"
      onPress={onPress}
      style={({ pressed }) => [styles.card, terminal && styles.cardTerminal, pressed && styles.pressed]}
    >
      <View style={styles.cardTop}>
        {terminal && meta.icon ? (
          <Text style={[styles.terminalIcon, { color: meta.color }]} accessibilityLabel={meta.label}>
            {meta.icon}
          </Text>
        ) : null}
        <View style={styles.cardMain}>
          <Text
            style={[styles.title, important && styles.titleImportant, terminal && styles.titleTerminal]}
            numberOfLines={2}
          >
            {important ? <Text style={styles.star}>★ </Text> : null}
            {item.prefix?.name?.trim() ? (
              <>
                <KeepPrefixText name={item.prefix.name} />{" "}
              </>
            ) : null}
            {item.title?.trim() || "(제목 없음)"}
          </Text>
          {showBadges ? (
            <View style={styles.badgeRow}>
              {lifecycleKey === "PENDING" || terminal ? <KeepLifecycleBadge lifecycleKey={lifecycleKey} /> : null}
              {demanded ? <KeepStateBadge stateKey="DEMANDED" /> : null}
            </View>
          ) : null}
          {expireText || dateText || projectTitle || month ? (
            <Text style={styles.sub} numberOfLines={2}>
              {expireText ? (
                <Text style={overdue ? styles.expireOverdue : days === 0 && !terminal ? styles.expireToday : undefined}>
                  {`기한 ${expireText}`}
                </Text>
              ) : null}
              {[dateText ? `일시 ${dateText}` : "", projectTitle ? `📁 ${projectTitle}` : "", month]
                .filter(Boolean)
                .map((part, index) => `${expireText || index > 0 ? " · " : ""}${part}`)
                .join("")}
            </Text>
          ) : null}
          <KeepTagChips tags={tags} selectedTagId={selectedTagId} onPressTag={onPressTag} />
        </View>
      </View>
    </Pressable>
  );
}

/**
 * Keep 전수관리 (햄버거 메뉴 `Keep > Keep 전수관리`) — 웹 /keep(KeepPage.vue)의 읽기 부분. 모바일 2단계 2-0.
 * - GET /api/prospective/keeps?scope=all 한 번으로 전 범위를 받고, Project 범위·보기·검색·필터는 화면에서 투영한다(웹과 같음).
 * - Project 칩(전체 · 미지정 · OPEN Project), 보기(진행 · 기한 · 종결), 검색(제목·본문, 제출 시에만), 필터 시트(기한 지남 · 중요 · 말머리 · 태그)
 * - 행을 누르면 Keep 상세(읽기 전용). 행의 태그 칩은 태그 필터 토글.
 * - 읽기 전용: 완료·라이프사이클·상태·등록·수정·삭제 없음(쓰기는 2-1 이후, docs/migration/mobile/keep-todo-plan.md).
 */
export function KeepHomeScreen() {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  const [list, setList] = useState<KeepItem[]>([]);
  const [projects, setProjects] = useState<KeepProject[]>([]);
  const [projectError, setProjectError] = useState(false);
  const [serverTags, setServerTags] = useState<KeepTagOption[] | null>(null);
  const [serverPrefixes, setServerPrefixes] = useState<KeepPrefixOption[] | null>(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loadedAt, setLoadedAt] = useState<Date>(() => new Date());
  const [view, setView] = useState<KeepView>("OPEN");
  const [filters, setFilters] = useState<KeepFilters>(EMPTY_KEEP_FILTERS);
  const [keyword, setKeyword] = useState("");
  const [sheetOpen, setSheetOpen] = useState(false);
  const seqRef = useRef(0);
  const loadedRef = useRef(false);

  /**
   * 목록 + 보조 목록(Project · 태그 · 말머리) 조회. 보조 목록 실패는 목록 표시를 막지 않는다.
   * initial: 전체 로딩 표시 / refresh: 당겨서 새로고침 / silent: 화면 복귀 시 조용히 갱신(실패해도 기존 목록 유지)
   */
  const load = useCallback(async (mode: "initial" | "refresh" | "silent") => {
    const seq = ++seqRef.current;
    if (mode === "initial") {
      setLoading(true);
      setError(null);
    }
    const [listRes, projectRes, tagRes, prefixRes] = await Promise.allSettled([
      getKeepList(),
      getKeepProjects(),
      getKeepTags(),
      getKeepPrefixOptions()
    ]);
    if (seq !== seqRef.current) return;

    if (listRes.status === "fulfilled") {
      setList(listRes.value);
      setError(null);
      setLoadedAt(new Date());
      loadedRef.current = true;
    } else {
      const e: unknown = listRes.reason;
      if (!isUnauthorizedError(e)) {
        console.error("[KeepHomeScreen] keep list load failed", { mode }, e);
        if (loadedRef.current) {
          if (mode === "refresh") notifyBrief("Keep 목록을 새로 고치지 못했습니다.");
        } else {
          setError(errorMessage(e, "Keep 목록을 불러오지 못했습니다."));
        }
      }
    }
    if (projectRes.status === "fulfilled") {
      setProjects(projectRes.value);
      setProjectError(false);
    } else {
      if (!isUnauthorizedError(projectRes.reason)) console.warn("[KeepHomeScreen] project list load failed", projectRes.reason);
      setProjectError(true);
    }
    if (tagRes.status === "fulfilled") setServerTags(tagRes.value);
    else if (!isUnauthorizedError(tagRes.reason)) console.warn("[KeepHomeScreen] keep tag list load failed", tagRes.reason);
    if (prefixRes.status === "fulfilled") setServerPrefixes(prefixRes.value);
    else if (!isUnauthorizedError(prefixRes.reason)) console.warn("[KeepHomeScreen] keep prefix options load failed", prefixRes.reason);

    setLoading(false);
    setRefreshing(false);
  }, []);

  // 처음 들어올 때 전체 로딩, 상세에서 돌아오는 등 다시 포커스되면 조용히 갱신 (웹에서 바뀐 내용 반영)
  useFocusEffect(
    useCallback(() => {
      void load(loadedRef.current ? "silent" : "initial");
    }, [load])
  );

  const today = loadedAt;
  const openProjects = useMemo(() => openKeepProjects(projects), [projects]);
  const projectById = useMemo(() => new Map(projects.map((p) => [p.id, p])), [projects]);
  const projectTitleOf = useCallback(
    (projectId: number | null | undefined): string => {
      if (projectId == null) return "";
      return keepProjectTitle(projectById.get(projectId)) || `Project #${projectId}`;
    },
    [projectById]
  );

  // 선택한 Project 가 더 이상 OPEN 이 아니면(웹에서 종결·삭제) 전체로 돌아간다
  useEffect(() => {
    const scope = filters.scope;
    if (typeof scope !== "number" || projectError || !loadedRef.current) return;
    if (!openProjects.some((p) => p.id === scope)) setFilters((prev) => ({ ...prev, scope: "ALL" }));
  }, [filters.scope, openProjects, projectError]);

  const filtered = useMemo(() => filterKeeps(list, filters, today), [list, filters, today]);
  const counts = useMemo(() => keepViewCounts(filtered, today), [filtered, today]);
  const sections = useMemo((): KeepCopySection[] => {
    if (view === "OPEN") return [{ items: openKeeps(filtered) }];
    if (view === "TERMINAL") return [{ items: terminalKeeps(filtered) }];
    return dueKeepGroups(filtered, today).map((g) => ({ label: g.label, items: g.items }));
  }, [filtered, today, view]);
  const rows = useMemo((): Row[] => {
    if (view !== "DUE") return (sections[0]?.items ?? []).map((item) => ({ kind: "item", key: `k${item.id}`, item }));
    return dueKeepGroups(filtered, today).flatMap((g): Row[] => [
      { kind: "header", key: `h${g.bucket}`, label: g.label, count: g.items.length, bucket: g.bucket },
      ...g.items.map((item): Row => ({ kind: "item", key: `k${item.id}`, item }))
    ]);
  }, [filtered, sections, today, view]);
  const itemCount = useMemo(() => sections.reduce((sum, s) => sum + s.items.length, 0), [sections]);

  const tagOptions = useMemo(() => mergeKeepTagOptions(serverTags, list), [serverTags, list]);
  const prefixOptions = useMemo(() => mergeKeepPrefixOptions(serverPrefixes, list), [serverPrefixes, list]);
  const optionNote =
    serverTags == null || serverPrefixes == null ? "일부 선택지를 서버에서 받지 못해 목록에 있는 값으로 보여줍니다." : null;

  const scopeLabel =
    filters.scope === "ALL" ? "전체" : filters.scope === "UNASSIGNED" ? "미지정" : projectTitleOf(filters.scope);
  const viewLabel = VIEWS.find((v) => v.id === view)?.label ?? "";
  const prefixName = prefixOptions.find((p) => p.id === filters.prefixId)?.name ?? "";
  const tagName = tagOptions.find((t) => t.id === filters.tagId)?.name ?? "";

  /** 걸려 있는 조건 칩 (눌러서 해제) */
  const activeChips: { key: string; label: string; clear: () => void }[] = [
    filters.keyword
      ? { key: "kw", label: `"${filters.keyword}"`, clear: () => { setKeyword(""); setFilters((p) => ({ ...p, keyword: "" })); } }
      : null,
    filters.overdue ? { key: "overdue", label: "기한 지남", clear: () => setFilters((p) => ({ ...p, overdue: false })) } : null,
    filters.important ? { key: "imp", label: "★ 중요", clear: () => setFilters((p) => ({ ...p, important: false })) } : null,
    filters.prefixId != null
      ? { key: "prefix", label: `[${prefixName || filters.prefixId}]`, clear: () => setFilters((p) => ({ ...p, prefixId: null })) }
      : null,
    filters.tagId != null
      ? { key: "tag", label: `#${tagName || filters.tagId}`, clear: () => setFilters((p) => ({ ...p, tagId: null })) }
      : null
  ].filter((c): c is { key: string; label: string; clear: () => void } => c != null);
  const sheetFilterCount =
    (filters.overdue ? 1 : 0) + (filters.important ? 1 : 0) + (filters.prefixId != null ? 1 : 0) + (filters.tagId != null ? 1 : 0);

  function submitSearch() {
    const kw = keyword.trim();
    setFilters((prev) => (prev.keyword === kw ? prev : { ...prev, keyword: kw }));
  }

  function clearInput() {
    setKeyword("");
    if (filters.keyword) setFilters((prev) => ({ ...prev, keyword: "" }));
  }

  function resetConditions() {
    setKeyword("");
    setFilters((prev) => ({ ...EMPTY_KEEP_FILTERS, scope: prev.scope }));
  }

  function toggleTag(tag: DisplayTag) {
    if (!Number.isFinite(tag.tagId)) return;
    setFilters((prev) => ({ ...prev, tagId: prev.tagId === tag.tagId ? null : tag.tagId }));
  }

  function buildCopyText(): string {
    const conditions = activeChips.map((c) => c.label);
    const header = [
      `Keep 전수관리 · ${viewLabel} · ${scopeLabel} · ${itemCount}건`,
      conditions.length > 0 ? `조건: ${conditions.join(" · ")}` : ""
    ];
    return buildKeepListCopyText(header, sections, filters.scope === "ALL" ? projectTitleOf : () => "", today);
  }

  function onRefresh() {
    setRefreshing(true);
    void load("refresh");
  }

  const scopeChips: { key: string; label: string; scope: KeepProjectScope }[] = [
    { key: "all", label: "전체", scope: "ALL" },
    { key: "none", label: "미지정", scope: "UNASSIGNED" },
    ...openProjects.map((p) => ({ key: `p${p.id}`, label: keepProjectTitle(p), scope: p.id as KeepProjectScope }))
  ];

  const hasConditions = activeChips.length > 0;

  return (
    <SafeAreaView edges={["top", "left", "right", "bottom"]} style={styles.safeArea}>
      <MenuScreenHeader title="Keep 전수관리" currentUrl={MENU_URL.KEEP} />
      <FlatList
        data={loading || error != null ? [] : rows}
        keyExtractor={(row) => row.key}
        contentContainerStyle={styles.container}
        keyboardShouldPersistTaps="handled"
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
        ListHeaderComponent={
          <View style={styles.listHeader}>
            {/* Project 범위 (웹 Project 탭: 전체 · 미지정 · OPEN Project) */}
            <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.scopeRow}>
              {scopeChips.map((chip) => {
                const active = filters.scope === chip.scope;
                return (
                  <Pressable
                    key={chip.key}
                    accessibilityRole="button"
                    accessibilityState={{ selected: active }}
                    onPress={() => setFilters((prev) => ({ ...prev, scope: chip.scope }))}
                    style={[styles.scopeChip, active && styles.scopeChipActive]}
                  >
                    <Text style={[styles.scopeChipText, active && styles.scopeChipTextActive]} numberOfLines={1}>
                      {chip.label}
                    </Text>
                  </Pressable>
                );
              })}
            </ScrollView>
            {projectError ? <Text style={styles.noteText}>Project 목록을 불러오지 못했습니다. 당겨서 다시 시도하세요.</Text> : null}

            {/* 보기 전환 */}
            <View style={styles.segment} accessibilityRole="tablist">
              {VIEWS.map((v) => {
                const active = v.id === view;
                return (
                  <Pressable
                    key={v.id}
                    accessibilityRole="tab"
                    accessibilityState={{ selected: active }}
                    onPress={() => setView(v.id)}
                    style={[styles.segmentItem, active && styles.segmentItemActive]}
                  >
                    <Text style={[styles.segmentText, active && styles.segmentTextActive]}>
                      {v.label}
                      {!loading && error == null ? <Text style={styles.segmentCount}> {counts[v.id]}</Text> : null}
                    </Text>
                  </Pressable>
                );
              })}
            </View>

            {/* 검색 (제목·본문, 검색 버튼·키보드 검색 키로만 제출) + 필터 */}
            <View style={styles.searchRow}>
              <View style={styles.searchInputWrap}>
                <TextInput
                  autoCapitalize="none"
                  autoCorrect={false}
                  onChangeText={setKeyword}
                  onSubmitEditing={submitSearch}
                  placeholder="제목·본문 검색"
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
              <Pressable
                accessibilityRole="button"
                accessibilityLabel={`필터${sheetFilterCount > 0 ? ` ${sheetFilterCount}개 적용 중` : ""}`}
                onPress={() => setSheetOpen(true)}
                style={({ pressed }) => [styles.filterButton, sheetFilterCount > 0 && styles.filterButtonActive, pressed && styles.pressed]}
              >
                <Text style={[styles.filterButtonText, sheetFilterCount > 0 && styles.filterButtonTextActive]}>
                  필터{sheetFilterCount > 0 ? ` ${sheetFilterCount}` : ""}
                </Text>
              </Pressable>
            </View>

            {hasConditions ? (
              <View style={styles.conditionRow}>
                {activeChips.map((chip) => (
                  <Pressable
                    key={chip.key}
                    accessibilityRole="button"
                    accessibilityLabel={`${chip.label} 조건 해제`}
                    onPress={chip.clear}
                    style={({ pressed }) => [styles.conditionChip, pressed && styles.pressed]}
                  >
                    <Text style={styles.conditionText} numberOfLines={1}>
                      {chip.label} ✕
                    </Text>
                  </Pressable>
                ))}
                <Pressable accessibilityRole="button" onPress={resetConditions} hitSlop={6} style={styles.resetButton}>
                  <Text style={styles.resetText}>초기화</Text>
                </Pressable>
              </View>
            ) : null}

            {!loading && error == null && itemCount > 0 ? (
              <JournalCopyShareBar
                label={`${viewLabel} ${itemCount}건`}
                buildText={buildCopyText}
                shareTitle={`Keep ${viewLabel}`}
                copiedMessage={`Keep ${itemCount}건을 복사했습니다.`}
              />
            ) : null}
          </View>
        }
        ListEmptyComponent={
          loading ? (
            <ActivityIndicator style={styles.loader} color={colors.accent} />
          ) : error != null ? (
            <View style={styles.stateBox}>
              <Text style={styles.errorText}>{error}</Text>
              <Pressable accessibilityRole="button" onPress={() => { void load("initial"); }} style={styles.retryButton}>
                <Text style={styles.retryText}>다시 시도</Text>
              </Pressable>
            </View>
          ) : (
            <View style={styles.stateBox}>
              <Text style={styles.emptyText}>{hasConditions || filters.scope !== "ALL" ? "조건에 맞는 Keep이 없습니다." : EMPTY_TEXT[view]}</Text>
              {filters.overdue && view === "TERMINAL" ? (
                <Text style={styles.noteText}>기한 지남 필터는 종결되지 않은 항목만 남깁니다.</Text>
              ) : null}
            </View>
          )
        }
        ListFooterComponent={
          !loading && error == null ? (
            <Text style={styles.footerNote}>모바일 Keep은 읽기 전용입니다. 등록·완료·수정은 웹 Keep 전수관리에서 합니다.</Text>
          ) : null
        }
        renderItem={({ item: row }) =>
          row.kind === "header" ? (
            <Text style={[styles.groupHeader, { color: DUE_HEADER_COLOR[row.bucket] }]} accessibilityRole="header">
              {row.label} <Text style={styles.groupCount}>{row.count}</Text>
            </Text>
          ) : (
            <KeepRow
              item={row.item}
              today={today}
              projectTitle={filters.scope === "ALL" ? projectTitleOf(row.item.projectId) : ""}
              selectedTagId={filters.tagId}
              onPress={() =>
                navigation.navigate("KeepDetail", {
                  id: row.item.id,
                  item: row.item,
                  projectTitle: projectTitleOf(row.item.projectId)
                })
              }
              onPressTag={toggleTag}
            />
          )
        }
      />
      <KeepFilterSheet
        visible={sheetOpen}
        onClose={() => setSheetOpen(false)}
        filters={filters}
        onChange={(patch) => setFilters((prev) => ({ ...prev, ...patch }))}
        onReset={() => setFilters((prev) => ({ ...prev, prefixId: null, tagId: null, overdue: false, important: false }))}
        prefixOptions={prefixOptions}
        tagOptions={tagOptions}
        optionNote={optionNote}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: colors.background },
  container: { padding: 16, gap: 10, paddingBottom: 32, flexGrow: 1 },
  listHeader: { gap: 10, marginBottom: 2 },
  scopeRow: { gap: 8, paddingRight: 8 },
  scopeChip: {
    maxWidth: 180,
    paddingHorizontal: 13,
    paddingVertical: 6,
    borderRadius: 18,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.input
  },
  scopeChipActive: { backgroundColor: colors.accent, borderColor: colors.accent },
  scopeChipText: { color: colors.secondaryText, fontSize: 13, fontWeight: "600" },
  scopeChipTextActive: { color: colors.onAccent },
  segment: {
    flexDirection: "row",
    backgroundColor: colors.surface,
    borderRadius: 10,
    padding: 3,
    gap: 3
  },
  segmentItem: { flex: 1, alignItems: "center", paddingVertical: 7, borderRadius: 8 },
  segmentItemActive: { backgroundColor: colors.input, borderWidth: 1, borderColor: colors.border },
  segmentText: { color: colors.secondaryText, fontSize: 14, fontWeight: "600" },
  segmentTextActive: { color: colors.accent, fontWeight: "800" },
  segmentCount: { fontSize: 12, fontWeight: "600" },
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
  searchButton: { backgroundColor: colors.accent, borderRadius: 8, paddingHorizontal: 14, paddingVertical: 9 },
  searchButtonText: { color: colors.onAccent, fontSize: 14.5, fontWeight: "700" },
  filterButton: {
    borderRadius: 8,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.input,
    paddingHorizontal: 12,
    paddingVertical: 9
  },
  filterButtonActive: { borderColor: colors.accent, backgroundColor: colors.surface },
  filterButtonText: { color: colors.secondaryText, fontSize: 14.5, fontWeight: "700" },
  filterButtonTextActive: { color: colors.accent },
  conditionRow: { flexDirection: "row", flexWrap: "wrap", alignItems: "center", gap: 6 },
  conditionChip: {
    maxWidth: 200,
    borderRadius: 14,
    borderWidth: 1,
    borderColor: colors.accent,
    backgroundColor: colors.surface,
    paddingHorizontal: 10,
    paddingVertical: 3
  },
  conditionText: { color: colors.accent, fontSize: 12, fontWeight: "700" },
  resetButton: { marginLeft: "auto", paddingHorizontal: 4, paddingVertical: 4 },
  resetText: { color: colors.muted, fontSize: 12.5, fontWeight: "600", textDecorationLine: "underline" },
  groupHeader: { fontSize: 13, fontWeight: "800", marginTop: 6 },
  groupCount: { fontSize: 12, fontWeight: "600", color: colors.muted },
  card: {
    backgroundColor: colors.input,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 12,
    padding: 12
  },
  cardTerminal: { backgroundColor: colors.surface },
  cardTop: { flexDirection: "row", gap: 8 },
  cardMain: { flex: 1, gap: 5 },
  terminalIcon: { fontSize: 15, fontWeight: "800", width: 18, textAlign: "center", marginTop: 1 },
  title: { fontSize: 15, fontWeight: "600", color: colors.text, lineHeight: 21 },
  titleImportant: { fontWeight: "800" },
  titleTerminal: { color: colors.secondaryText },
  star: { color: "#F6B100" },
  badgeRow: { flexDirection: "row", flexWrap: "wrap", gap: 6 },
  sub: { fontSize: 12.5, color: colors.muted, lineHeight: 18 },
  expireOverdue: { color: OVERDUE_COLOR, fontWeight: "800" },
  expireToday: { color: colors.accent, fontWeight: "800" },
  loader: { marginTop: 32 },
  stateBox: { alignItems: "center", gap: 10, paddingVertical: 32 },
  errorText: { color: "#C0392B", fontSize: 14, textAlign: "center" },
  emptyText: { color: colors.secondaryText, fontSize: 14 },
  noteText: { color: colors.muted, fontSize: 12 },
  footerNote: { color: colors.muted, fontSize: 11.5, textAlign: "center", paddingTop: 12 },
  retryButton: {
    borderWidth: 1,
    borderColor: colors.accent,
    borderRadius: 14,
    paddingHorizontal: 14,
    paddingVertical: 6
  },
  retryText: { color: colors.accent, fontWeight: "700" },
  pressed: { opacity: 0.7 }
});
