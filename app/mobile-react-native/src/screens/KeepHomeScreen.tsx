import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import {
  ActivityIndicator,
  AppState,
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
import { getKeepList, getKeepPrefixOptions, getKeepProjects, getKeepTags, setKeepLifecycle, toggleKeepState } from "../api/dreamDiaryApi";
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
  keepProjectPrefixLabel,
  keepProjectTitle,
  openKeepProjects,
  type KeepItem,
  type KeepLifecycleKey,
  type KeepStateKey,
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
import { KeepSwipeRow } from "../components/keep/KeepSwipeRow";
import { KeepLifecycleMenu } from "../components/keep/KeepLifecycleMenu";
import { colorFromTextClass } from "../components/journal/journalTagStyle";
import { isRecentKeep, recentKeeps, nextKeepCompletionExpiry, KEEP_RECENT_PREVIEW_SIZE } from "../utils/keepRecent";

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

/** 기한 묶음 머리 색 — 지남은 경고색(웹 text-warning), 오늘은 빨강(행의 오늘 기한 글자와 같은 colors.danger) */
const DUE_HEADER_COLOR: Record<KeepDueBucket, string> = {
  PAST: "#B7791F",
  TODAY: colors.danger,
  WEEK: colors.secondaryText,
  LATER: colors.muted
};

/** 지난 기한(미종결) 경고색 — 웹 `text-warning fw-bold` */
const OVERDUE_COLOR = "#B7791F";

type Row =
  | { kind: "header"; key: string; label: string; count: number; bucket: KeepDueBucket }
  | { kind: "section"; key: string; label: string; count: number }
  | { kind: "recentMore"; key: string }
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
  onStatus: () => void;
  saving: boolean;
};

/**
 * Keep 행 — 웹 KeepPage 행 표기: 중요 별·굵은 제목, 보류·요구 배지, `[말머리]`, 태그 칩, 기한(진행 행의 지난 기한은 경고색 + N일 지남), 등록 년월.
 * 진행 행은 드래그 완료 버튼과 상태 메뉴를 제공하고 종결 행은 끝난 이유 아이콘·배지와 상태 메뉴를 보인다.
 */
function KeepRow({ item, today, projectTitle, selectedTagId, onPress, onPressTag, onStatus, saving }: KeepRowProps) {
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
        <Pressable accessibilityRole="button" accessibilityLabel="Keep 상태 변경" disabled={saving}
          onPress={(event) => { event.stopPropagation(); onStatus(); }} hitSlop={8} style={styles.statusButton}>
          <Text style={styles.statusButtonText}>{saving ? "…" : "⋯"}</Text>
        </Pressable>
      </View>
    </Pressable>
  );
}

/**
 * Keep 전수관리 (햄버거 메뉴 `Keep > Keep 전수관리`) — 웹 /keep(KeepPage.vue)의 조회·라이프사이클 조작을 제공한다.
 * - GET /api/prospective/keeps?scope=all 한 번으로 전 범위를 받고, Project 범위·보기·검색·필터는 화면에서 투영한다(웹과 같음).
 * - Project 칩(전체 · 미지정 · OPEN Project), 보기(진행 · 기한 · 종결), 검색(제목·본문, 제출 시에만), 필터 시트(기한 지남 · 중요 · 말머리 · 태그)
 * - 행을 누르면 Keep 상세(읽기 전용). 행의 태그 칩은 태그 필터 토글.
 * - 중요·요구 토글은 성공·오류 모두 Keep 목록으로 서버 현재 값을 확인한다. 요청 중 다른 쓰기를 잠근다.
 * - 완료·라이프사이클은 서버 저장 후 Keep 목록만 재조회한다. 쓰기 범위는 docs/migration/mobile/keep-todo-plan.md의 2-1 완료·라이프사이클 계약을 따른다.
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
  const [completionNow, setCompletionNow] = useState(Date.now);
  const [recentOpen, setRecentOpen] = useState(false);
  const [showAllRecent, setShowAllRecent] = useState(false);
  const [view, setView] = useState<KeepView>("OPEN");
  const [filters, setFilters] = useState<KeepFilters>(EMPTY_KEEP_FILTERS);
  const [keyword, setKeyword] = useState("");
  const [sheetOpen, setSheetOpen] = useState(false);
  const seqRef = useRef(0);
  const loadedRef = useRef(false);
  const [lifecycleItem, setLifecycleItem] = useState<KeepItem | null>(null);
  const [savingId, setSavingId] = useState<number | null>(null);
  const [actionError, setActionError] = useState<{ id: number; message: string; saved: boolean } | null>(null);
  const savingRef = useRef(false);
  const awaitingListRef = useRef(false);
  const mountedRef = useRef(true);
  useEffect(() => {
    mountedRef.current = true;
    return () => { mountedRef.current = false; ++seqRef.current; };
  }, []);

  /**
   * 목록 + 보조 목록(Project · 태그 · 말머리) 조회. 보조 목록 실패는 목록 표시를 막지 않는다.
   * initial: 전체 로딩 표시 / refresh: 당겨서 새로고침 / silent: 화면 복귀 시 조용히 갱신(실패해도 기존 목록 유지)
   */
  const load = useCallback(async (mode: "initial" | "refresh" | "silent") => {
    if (savingRef.current) {
      console.info("[KeepHomeScreen] refresh waits for lifecycle request");
      setRefreshing(false);
      return;
    }
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
      awaitingListRef.current = false;
      setActionError(null);
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

  /** Keep만 재조회해 서버 상태·완료 시각을 표시하고 이전 목록 응답을 무효화한다. */
  async function reloadKeepListOnly(): Promise<void> {
    ++seqRef.current;
    const fresh = await getKeepList();
    if (!mountedRef.current) return;
    awaitingListRef.current = false;
    setActionError(null);
    setList(fresh);
    setLoadedAt(new Date());
    setError(null);
    loadedRef.current = true;
  }

  /** 서버 확정 상태를 반영한다. 저장 실패와 저장 후 조회 실패를 구분하며 전체 요청 동안 중복 쓰기를 잠근다. */
  async function changeLifecycle(item: KeepItem, key: KeepLifecycleKey): Promise<void> {
    if (savingRef.current || awaitingListRef.current || key === keepLifecycleKeyOf(item)) {
      console.info("[KeepHomeScreen] lifecycle request skipped", { id: item.id, key, busy: savingRef.current });
      return;
    }
    if (key === "EXPIRED" && !normalizeExpireDt(item.expireDt)) {
      console.warn("[KeepHomeScreen] expiry requires a due date", { id: item.id });
      return;
    }
    savingRef.current = true;
    ++seqRef.current;
    setSavingId(item.id);
    setRefreshing(false);
    setActionError(null);
    let saved = false;
    try {
      await setKeepLifecycle(item.id, key);
      saved = true;
      awaitingListRef.current = true;
      if (!mountedRef.current) return;
      setLifecycleItem(null);
      await reloadKeepListOnly();
      if (mountedRef.current) console.info("[KeepHomeScreen] lifecycle saved and list refreshed", { id: item.id, key });
    } catch (e) {
      if (!mountedRef.current || isUnauthorizedError(e)) return;
      console.error("[KeepHomeScreen] lifecycle action failed", { id: item.id, key, saved }, e);
      setActionError({ id: item.id, saved, message: saved
        ? "상태는 저장됐지만 목록을 새로 받지 못했습니다. 목록을 다시 조회하세요."
        : errorMessage(e, "Keep 상태를 변경하지 못했습니다.") });
    } finally {
      savingRef.current = false;
      if (mountedRef.current) setSavingId(null);
    }
  }


  /** 비멱등 토글은 한 번 전송하고 오류에도 서버 목록을 확인한다. 조회 실패 뒤 재시도는 GET만 실행한다. */
  async function changeState(item: KeepItem, key: KeepStateKey): Promise<void> {
    if (savingRef.current || awaitingListRef.current) {
      console.info("[KeepHomeScreen] state toggle skipped", { id: item.id, key });
      return;
    }
    savingRef.current = true;
    awaitingListRef.current = true;
    ++seqRef.current;
    setSavingId(item.id);
    setRefreshing(false);
    setActionError(null);
    setLifecycleItem(null);
    let writeError: string | null = null;
    try {
      try {
        await toggleKeepState(item.id, key);
      } catch (e) {
        if (!mountedRef.current || isUnauthorizedError(e)) return;
        console.error("[KeepHomeScreen] state toggle failed; checking list", { id: item.id, key }, e);
        writeError = errorMessage(e, "Keep 속성 변경 결과를 확인하지 못했습니다.");
      }
      if (!mountedRef.current) return;
      await reloadKeepListOnly();
      if (!mountedRef.current) return;
      if (writeError) setActionError({ id: item.id, saved: false, message: writeError });
      console.info("[KeepHomeScreen] state toggle list checked", { id: item.id, key, writeFailed: writeError != null });
    } catch (e) {
      if (!mountedRef.current || isUnauthorizedError(e)) return;
      console.error("[KeepHomeScreen] state toggle list check failed", { id: item.id, key }, e);
      setActionError({ id: item.id, saved: true, message: writeError
        ? `${writeError} 목록을 다시 조회해 현재 상태를 확인하세요.`
        : "속성은 저장됐지만 목록을 새로 받지 못했습니다. 목록을 다시 조회하세요." });
    } finally {
      savingRef.current = false;
      if (mountedRef.current) setSavingId(null);
    }
  }
  /**
   * 저장 성공 뒤 조회만 실패한 경우 쓰기를 재전송하지 않고 목록만 다시 받는다.
   * 비멱등 토글의 결과가 미확인인 경우에도 같은 조회 전용 재시도를 사용한다.
   */
  async function retryActionRefresh(): Promise<void> {
    if (savingRef.current) return;
    savingRef.current = true;
    setSavingId(actionError?.id ?? null);
    try {
      await reloadKeepListOnly();
      if (mountedRef.current) setActionError(null);
    } catch (e) {
      if (mountedRef.current && !isUnauthorizedError(e)) {
        console.error("[KeepHomeScreen] lifecycle list refresh retry failed", e);
        setActionError((previous) => previous ? { ...previous, message: errorMessage(e, "목록을 다시 조회하지 못했습니다.") } : previous);
      }
    } finally {
      savingRef.current = false;
      if (mountedRef.current) setSavingId(null);
    }
  }

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
  const recentItems = useMemo(() => recentKeeps(filtered, completionNow), [filtered, completionNow]);
  const visibleRecentItems = useMemo(() => showAllRecent ? recentItems : recentItems.slice(0, KEEP_RECENT_PREVIEW_SIZE), [recentItems, showAllRecent]);
  const otherTerminalItems = useMemo(() => terminalKeeps(filtered).filter((item) => !isRecentKeep(item, completionNow)), [filtered, completionNow]);

  /** 완료 경계와 앱 복귀에서 표시 시각만 갱신한다. */
  useEffect(() => { setCompletionNow(Date.now()); }, [list]);
  useFocusEffect(useCallback(() => { setCompletionNow(Date.now()); }, []));
  useEffect(() => {
    const subscription = AppState.addEventListener("change", (state) => {
      if (state === "active") setCompletionNow(Date.now());
    });
    return () => subscription.remove();
  }, []);
  useEffect(() => {
    const expiry = nextKeepCompletionExpiry(filtered, completionNow);
    if (expiry == null) return;
    const timer = setTimeout(() => {
      console.info("[KeepHomeScreen] recent completion expiry reached");
      setCompletionNow(Date.now());
    }, Math.max(1, expiry - Date.now()));
    return () => clearTimeout(timer);
  }, [filtered, completionNow]);

  const sections = useMemo((): KeepCopySection[] => {
    if (view === "TERMINAL") return [
      ...(recentItems.length ? [{ label: "최근 완료", items: visibleRecentItems }] : []),
      ...(otherTerminalItems.length ? [{ label: "종결", items: otherTerminalItems }] : [])
    ];
    const main = view === "OPEN" ? [{ items: openKeeps(filtered) }]
      : dueKeepGroups(filtered, today).map((g) => ({ label: g.label, items: g.items }));
    return recentOpen && recentItems.length ? [...main, { label: "최근 완료", items: visibleRecentItems }] : main;
  }, [filtered, today, view, recentOpen, recentItems, visibleRecentItems, otherTerminalItems]);
  const rows = useMemo((): Row[] => {
    if (view === "OPEN") return openKeeps(filtered).map((item) => ({ kind: "item", key: `k${item.id}`, item }));
    if (view === "TERMINAL") return sections.flatMap((section): Row[] => [
      { kind: "section", key: `h${section.label}`, label: section.label ?? "", count: section.label === "최근 완료" ? recentItems.length : section.items.length },
      ...section.items.map((item): Row => ({ kind: "item", key: `k${item.id}`, item })),
      ...(section.label === "최근 완료" && recentItems.length > KEEP_RECENT_PREVIEW_SIZE ? [{ kind: "recentMore", key: "recent-more" } as Row] : [])
    ]);
    return dueKeepGroups(filtered, today).flatMap((g): Row[] => [
      { kind: "header", key: `h${g.bucket}`, label: g.label, count: g.items.length, bucket: g.bucket },
      ...g.items.map((item): Row => ({ kind: "item", key: `k${item.id}`, item }))
    ]);
  }, [filtered, sections, today, view, recentItems]);
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

  /** 상세 진입·태그 필터를 공유하는 Keep 표시 행. */
  function renderKeepRow(item: KeepItem) {
    return <KeepSwipeRow key={item.id} enabled={!isKeepTerminal(item)} disabled={savingId != null || actionError?.saved === true}
      onComplete={() => { void changeLifecycle(item, "RESOLVED"); }}>
      <KeepRow item={item} today={today}
        projectTitle={filters.scope === "ALL" ? projectTitleOf(item.projectId) : ""}
        selectedTagId={filters.tagId}
        onPress={() => navigation.navigate("KeepDetail", { id: item.id, item, projectTitle: projectTitleOf(item.projectId) })}
        onPressTag={toggleTag} saving={savingId != null || actionError?.saved === true}
        onStatus={() => {
          if (savingRef.current || awaitingListRef.current) return;
          setActionError(null);
          setLifecycleItem(item);
        }} />
    </KeepSwipeRow>;
  }

  /** 최근 완료의 첫 5건과 전체 목록을 전환한다. */
  function renderRecentMore() {
    return <Pressable accessibilityRole="button" accessibilityState={{ expanded: showAllRecent }}
      onPress={() => setShowAllRecent((value) => !value)} style={styles.retryButton}>
      <Text style={styles.retryText}>{showAllRecent ? "접기" : "더 보기"}</Text>
    </Pressable>;
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

  // Project 칩은 [말머리]이름. 말머리 부분만 서버 textClass 색으로 칠한다(모르는 클래스면 칩 글자색).
  const scopeChips: { key: string; label: string; prefix?: string; prefixColor?: string | null; scope: KeepProjectScope }[] = [
    { key: "all", label: "전체", scope: "ALL" },
    { key: "none", label: "미지정", scope: "UNASSIGNED" },
    ...openProjects.map((p) => ({
      key: `p${p.id}`,
      label: keepProjectTitle(p),
      prefix: keepProjectPrefixLabel(p),
      prefixColor: colorFromTextClass(p.prefix?.textClass),
      scope: p.id as KeepProjectScope
    }))
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
            {actionError ? <View style={styles.stateBox}>
              <Text style={styles.errorText}>{actionError.message}</Text>
              {actionError.saved ? <Pressable accessibilityRole="button" disabled={savingId != null}
                onPress={() => { void retryActionRefresh(); }} style={styles.retryButton}>
                <Text style={styles.retryText}>목록 다시 조회</Text>
              </Pressable> : null}
            </View> : null}
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
                      {chip.prefix ? (
                        <Text style={chip.prefixColor ? { color: chip.prefixColor } : null}>{chip.prefix}</Text>
                      ) : null}
                      {chip.prefix ? chip.label.slice(chip.prefix.length) : chip.label}
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
            <View style={{ gap: 10 }}>
              {view !== "TERMINAL" && recentItems.length > 0 ? (
                <View style={{ gap: 10 }}>
                  <Pressable accessibilityRole="button" accessibilityState={{ expanded: recentOpen }}
                    onPress={() => setRecentOpen((value) => !value)}>
                    <Text style={styles.groupHeader}>최근 완료 {recentItems.length}건 {recentOpen ? "⌃" : "⌄"}</Text>
                  </Pressable>
                  {recentOpen ? visibleRecentItems.map(renderKeepRow) : null}
                  {recentOpen && recentItems.length > KEEP_RECENT_PREVIEW_SIZE ? renderRecentMore() : null}
                </View>
              ) : null}
              <Text style={styles.footerNote}>진행 중 행을 왼쪽으로 밀면 완료 버튼이 나타납니다. ⋯에서 상태를 변경합니다.</Text>
            </View>
          ) : null
        }
        renderItem={({ item: row }) =>
          row.kind === "header" ? (
            <Text style={[styles.groupHeader, { color: DUE_HEADER_COLOR[row.bucket] }]} accessibilityRole="header">
              {row.label} <Text style={styles.groupCount}>{row.count}</Text>
            </Text>
          ) : row.kind === "section" ? (
            <Text style={styles.groupHeader} accessibilityRole="header">{row.label} <Text style={styles.groupCount}>{row.count}</Text></Text>
          ) : row.kind === "recentMore" ? renderRecentMore() : renderKeepRow(row.item)
        }
      />
      <KeepLifecycleMenu item={lifecycleItem} saving={savingId != null}
        error={actionError?.id === lifecycleItem?.id && !actionError?.saved ? actionError?.message ?? null : null}
        onClose={() => { if (!savingRef.current) setLifecycleItem(null); }}
        onSelect={(key) => { if (lifecycleItem) void changeLifecycle(lifecycleItem, key); }}
        onToggleState={(key) => { if (lifecycleItem) void changeState(lifecycleItem, key); }} />
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
  statusButton: { paddingHorizontal: 8, paddingVertical: 2 },
  statusButtonText: { color: colors.accent, fontSize: 23, fontWeight: "700" },
  container: { padding: 16, gap: 10, paddingBottom: 32, flexGrow: 1 },
  listHeader: { gap: 10, marginBottom: 2 },
  scopeRow: { gap: 8, paddingRight: 8 },
  scopeChip: {
    // [말머리]이름 이 들어가도 대부분 한 줄에 보이도록 180 → 220. 넘치면 꼬리 말줄임(말머리는 앞이라 남는다).
    maxWidth: 220,
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
  /** 오늘 기한 — 굵게 대신 빨강 글자 (깜빡임 없음) */
  expireToday: { color: colors.danger },
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
