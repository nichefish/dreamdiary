import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { ActivityIndicator, Pressable, StyleSheet, Text, View } from "react-native";
import { useFocusEffect, useNavigation } from "@react-navigation/native";
import type { NativeStackNavigationProp } from "@react-navigation/native-stack";
import {
  getJournalDayTagCloud,
  getJournalEntryTagCloud,
  type TagCloudItem,
  type TagCloudPeriod
} from "../../api/dreamDiaryApi";
import { isUnauthorizedError } from "../../api/client";
import type { RootStackParamList } from "../../navigation/AppNavigator";
import { openTagResult } from "../../navigation/tagResult";
import { colors } from "../../theme/colors";
import { cloudTagTextStyle, TAG_CTGR_COLOR } from "./journalTagStyle";

type SectionId = "day" | "diary" | "dream";

type SectionState = { tags: TagCloudItem[]; error: boolean };

const SECTIONS: Array<{ id: SectionId; label: string }> = [
  { id: "day", label: "일자 태그" },
  { id: "diary", label: "일기 태그" },
  { id: "dream", label: "꿈 태그" }
];

/** 한 행에 처음 보여줄 태그 수. 넘치면 "+N" 으로 펼친다. */
const ROW_PREVIEW_LIMIT = 18;
/** 접힘 헤더에 미리 보여줄 상위 태그 수 */
const COLLAPSED_PREVIEW_COUNT = 4;

/**
 * 패널 펼침 상태 — 앱 세션 동안 일/주/월 뷰가 공유한다(기본 펼침).
 * 뷰 전환으로 패널이 다시 마운트돼도 마지막 상태를 이어받는다.
 */
let rememberedOpen = true;

export type JournalPeriodTagCloudPanelProps = {
  /** 집계 기간 — 일: { stdrdDt }, 주: { weekStartDt }, 월: { yy, mnth } */
  period: TagCloudPeriod;
  /** 헤더 문구 (예: "이 날의 태그", "이 주의 태그", "이 달의 태그") */
  title: string;
};

/**
 * 기간 태그 클라우드 패널 (접이식). 웹 JournalTagCloudHeader.vue 의 일자·일기·꿈 3행을 모바일로 옮겼다.
 * - 데이터: GET /api/journal/day/tags, /api/journal/entry/tags?type=DIARY|DREAM (웹 스토어 fetchTagCloud 와 같은 기간 파라미터)
 * - 크기·색: 서버가 내려준 tagClass(ts-1~9)·textClass 를 journalTagStyle 로 매핑
 * - 기간이 바뀌면 다시 불러오고, 화면 복귀(focus) 시 조용히 갱신한다. 늦게 온 이전 기간 응답은 버린다.
 * - 태그를 탭하면 태그별 기록 화면(TagResult)을 연다 — 웹 태그 메뉴 "검색"과 같은 동작(행 종류 = 태그 종류).
 */
export function JournalPeriodTagCloudPanel({ period, title }: JournalPeriodTagCloudPanelProps) {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  const periodKey = JSON.stringify(period);
  const [open, setOpen] = useState(rememberedOpen);
  const [loading, setLoading] = useState(true);
  const [sections, setSections] = useState<Record<SectionId, SectionState> | null>(null);
  const [expandedRows, setExpandedRows] = useState<Record<SectionId, boolean>>({ day: false, diary: false, dream: false });
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
    const silent = loadedKeyRef.current === periodKey;
    if (!silent) {
      setLoading(true);
      setSections(null);
      setExpandedRows({ day: false, diary: false, dream: false });
    }
    const target = JSON.parse(periodKey) as TagCloudPeriod;
    const results = await Promise.allSettled([
      getJournalDayTagCloud(target),
      getJournalEntryTagCloud(target, "DIARY"),
      getJournalEntryTagCloud(target, "DREAM")
    ]);
    if (!mountedRef.current || seq !== seqRef.current) return;

    const toState = (r: PromiseSettledResult<TagCloudItem[]>, id: SectionId): SectionState => {
      if (r.status === "fulfilled") return { tags: r.value, error: false };
      if (!isUnauthorizedError(r.reason)) {
        console.error("[JournalPeriodTagCloudPanel] tag cloud load failed", { section: id, period: target }, r.reason);
      }
      return { tags: [], error: true };
    };
    setSections({
      day: toState(results[0], "day"),
      diary: toState(results[1], "diary"),
      dream: toState(results[2], "dream")
    });
    loadedKeyRef.current = periodKey;
    setLoading(false);
  }, [periodKey]);

  // 포커스 중 기간 변경 시 콜백이 바뀌어 다시 실행된다 (최초 마운트 포함)
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

  const totalCount = sections ? sections.day.tags.length + sections.diary.tags.length + sections.dream.tags.length : 0;
  const anyError = sections ? sections.day.error || sections.diary.error || sections.dream.error : false;

  const previewText = useMemo(() => {
    if (!sections) return "";
    return [...sections.day.tags, ...sections.diary.tags, ...sections.dream.tags]
      .slice()
      .sort((a, b) => (b.contentSize ?? 0) - (a.contentSize ?? 0))
      .slice(0, COLLAPSED_PREVIEW_COUNT)
      .map((t) => `#${t.name}`)
      .join(" ");
  }, [sections]);

  let headerSub: string;
  if (loading) headerSub = "불러오는 중…";
  else if (totalCount === 0) headerSub = anyError ? "불러오지 못했습니다" : "태그 없음";
  else headerSub = open ? `${totalCount}개` : previewText;

  function renderRow(id: SectionId, label: string) {
    const state = sections?.[id];
    const tags = state?.tags ?? [];
    const expanded = expandedRows[id];
    const visible = expanded ? tags : tags.slice(0, ROW_PREVIEW_LIMIT);
    const hidden = tags.length - visible.length;

    return (
      <View key={id} style={styles.row}>
        <Text style={styles.rowLabel}>{label}</Text>
        <View style={styles.rowTags}>
          {state == null ? (
            <Text style={styles.rowHint}>…</Text>
          ) : state.error ? (
            <Text style={styles.rowError}>불러오지 못했습니다</Text>
          ) : tags.length === 0 ? (
            <Text style={styles.rowHint}>-</Text>
          ) : (
            <>
              {visible.map((tag) => (
                <Text
                  key={`${id}-${String(tag.id)}`}
                  accessibilityRole="button"
                  accessibilityLabel={`${tag.ctgr ? `${tag.ctgr} ` : ""}${tag.name} ${tag.contentSize}건`}
                  accessibilityHint="이 태그가 붙은 기록 보기"
                  onPress={() => openTagResult(navigation, { tagId: tag.id, name: tag.name, ctgr: tag.ctgr }, id)}
                  style={styles.tag}
                >
                  {tag.ctgr ? <Text style={styles.tagCtgr}>[{tag.ctgr}]</Text> : null}
                  <Text style={cloudTagTextStyle(tag.tagClass, tag.textClass)}>{tag.name}</Text>
                  <Text style={styles.tagCount}> {tag.contentSize ?? 0}</Text>
                </Text>
              ))}
              {hidden > 0 && (
                <Pressable
                  accessibilityRole="button"
                  onPress={() => setExpandedRows((prev) => ({ ...prev, [id]: true }))}
                  hitSlop={6}
                >
                  <Text style={styles.moreText}>+{hidden} 더보기</Text>
                </Pressable>
              )}
            </>
          )}
        </View>
      </View>
    );
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
          <Text style={styles.headerSub} numberOfLines={1}>{headerSub}</Text>
        </View>
        <Text style={styles.caret}>{open ? "▾" : "▸"}</Text>
      </Pressable>

      {open && !loading && (totalCount > 0 || anyError) && (
        <View style={styles.body}>
          {SECTIONS.map((s) => renderRow(s.id, s.label))}
          {anyError && (
            <Pressable accessibilityRole="button" onPress={() => { loadedKeyRef.current = null; void load(); }} hitSlop={6}>
              <Text style={styles.retryText}>다시 시도</Text>
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
  header: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    paddingVertical: 8
  },
  headerTitle: { color: colors.text, fontSize: 13, fontWeight: "700" },
  headerSubWrap: { flex: 1, flexDirection: "row", alignItems: "center", gap: 6 },
  headerSub: { flexShrink: 1, color: colors.muted, fontSize: 12 },
  caret: { color: colors.accent, fontSize: 12, fontWeight: "700" },
  body: {
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: colors.border,
    paddingTop: 6,
    paddingBottom: 8,
    gap: 6
  },
  row: { flexDirection: "row", alignItems: "flex-start", gap: 8 },
  rowLabel: {
    width: 52,
    paddingTop: 4,
    color: colors.secondaryText,
    fontSize: 11.5,
    fontWeight: "700"
  },
  rowTags: {
    flex: 1,
    flexDirection: "row",
    flexWrap: "wrap",
    alignItems: "baseline",
    columnGap: 10,
    rowGap: 2,
    minHeight: 24
  },
  rowHint: { color: colors.muted, fontSize: 12, paddingTop: 4 },
  rowError: { color: "#C0392B", fontSize: 12, paddingTop: 4 },
  tag: { paddingVertical: 1 },
  tagCtgr: { color: TAG_CTGR_COLOR, fontSize: 10.5 },
  tagCount: { color: TAG_CTGR_COLOR, fontSize: 9.5 },
  moreText: { color: colors.accent, fontSize: 12, fontWeight: "700" },
  retryText: { color: colors.accent, fontSize: 12, fontWeight: "700", alignSelf: "flex-end" }
});