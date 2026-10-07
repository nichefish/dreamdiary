import { useCallback, useEffect, useMemo, useRef, useState, type ReactNode } from "react";
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
import { getKeepDetail, getKeepProjects } from "../api/dreamDiaryApi";
import { isUnauthorizedError } from "../api/client";
import type { RootStackParamList } from "../navigation/AppNavigator";
import { colors } from "../theme/colors";
import { displayTagsOf } from "../types/journalDay";
import {
  isKeepTerminal,
  keepLifecycleKeyOf,
  keepProjectTitle,
  keepStatesOf,
  type KeepItem
} from "../types/prospectiveKeep";
import { daysUntilExpire, normalizeExpireDt } from "../utils/keepExpire";
import { formatKeepDateTime } from "../utils/keepProjection";
import { buildKeepCopyText, keepExpireText } from "../utils/keepCopy";
import { markdownToPlainText } from "../utils/markdownText";
import { copyTextToClipboard, shareText } from "../components/journal/JournalCopyShareBar";
import { KeepLifecycleBadge, KeepPrefixText, KeepStateBadge, KeepTagChips } from "../components/keep/KeepBadges";

type Props = NativeStackScreenProps<RootStackParamList, "KeepDetail">;

/** 지난 기한(미종결) 경고색 — 웹 `text-warning fw-bold` */
const OVERDUE_COLOR = "#B7791F";

function errorMessage(e: unknown, fallback: string): string {
  return e instanceof Error && e.message ? e.message : fallback;
}

function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <View style={styles.field}>
      <Text style={styles.fieldLabel}>{label}</Text>
      <View style={styles.fieldValue}>{children}</View>
    </View>
  );
}

/**
 * Keep 상세 (읽기 전용) — 목록에서 연다. 웹에는 별도 상세 화면이 없어(제목 클릭 = 수정 모달) 수정 모달의 필드를 읽기 형태로 보여준다.
 * - GET /api/prospective/keep/{id} 로 다시 읽는다. 로드 전에는 목록 항목(route.params.item)을 임시로 보여준다.
 * - 라이프사이클은 상세 응답 값을 그대로 쓴다(서버가 목록과 같은 방식으로 부착, 행이 없으면 OPEN).
 * - Project 이름은 목록에서 넘겨받고, 목록 항목 없이 열렸거나 당겨서 새로고침하면 Project 목록을 다시 읽어 찾는다.
 * - 본문 마크다운은 평문으로 바꿔 보여준다(새 의존성 없음). 복사·공유는 헤더 버튼.
 * - 쓰기(완료·라이프사이클·상태·수정·삭제) 없음.
 */
export function KeepDetailScreen({ route, navigation }: Props) {
  const { id, item: paramItem, projectTitle: paramProjectTitle } = route.params;
  const [detail, setDetail] = useState<KeepItem | null>(null);
  const [projectTitle, setProjectTitle] = useState(paramProjectTitle ?? "");
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loadedAt, setLoadedAt] = useState<Date>(() => new Date());
  const seqRef = useRef(0);

  /** withProjects=true 면 Project 목록도 다시 읽어 Project 이름을 찾는다 */
  const load = useCallback(
    async (withProjects: boolean) => {
      const seq = ++seqRef.current;
      setError(null);
      const [detailRes, projectRes] = await Promise.allSettled([
        getKeepDetail(id),
        withProjects ? getKeepProjects() : Promise.resolve(null)
      ]);
      if (seq !== seqRef.current) return;
      if (detailRes.status === "fulfilled") {
        setDetail(detailRes.value);
        setLoadedAt(new Date());
      } else {
        const e: unknown = detailRes.reason;
        if (!isUnauthorizedError(e)) {
          console.error("[KeepDetailScreen] keep detail load failed", { id }, e);
          setError(errorMessage(e, "Keep을 불러오지 못했습니다."));
        }
      }
      if (projectRes.status === "fulfilled" && projectRes.value) {
        const projectId = detailRes.status === "fulfilled" ? detailRes.value.projectId : paramItem?.projectId;
        const project = projectRes.value.find((p) => p.id === projectId);
        setProjectTitle(projectId == null ? "" : keepProjectTitle(project) || `Project #${projectId}`);
      } else if (projectRes.status === "rejected" && !isUnauthorizedError(projectRes.reason)) {
        console.warn("[KeepDetailScreen] project list load failed", projectRes.reason);
      }
      setLoading(false);
      setRefreshing(false);
    },
    [id, paramItem?.projectId]
  );

  useEffect(() => {
    void load(paramItem == null);
  }, [load, paramItem]);

  /** 상세 응답이 오면 그 값(라이프사이클 포함), 오기 전에는 목록 항목 */
  const item = useMemo(() => detail ?? paramItem ?? null, [detail, paramItem]);
  const today = loadedAt;

  function onRefresh() {
    setRefreshing(true);
    void load(true);
  }

  const lifecycleKey = keepLifecycleKeyOf(item);
  const states = keepStatesOf(item);
  const tags = displayTagsOf(item?.tag);
  const expire = normalizeExpireDt(item?.expireDt);
  const overdue = !!item && !!expire && !isKeepTerminal(item) && daysUntilExpire(expire, today) < 0;
  const body = markdownToPlainText(item?.content);
  const resolvedProjectTitle = item?.projectId == null ? "" : projectTitle || `Project #${item.projectId}`;
  const shareTitle = `Keep ${item?.title?.trim() ?? ""}`.trim();

  return (
    <SafeAreaView style={styles.safeArea}>
      <View style={styles.header}>
        <Pressable
          accessibilityRole="button"
          accessibilityLabel="뒤로 가기"
          onPress={() => navigation.goBack()}
          style={styles.backButton}
        >
          <Text style={styles.backArrow}>‹</Text>
          <Text style={styles.backLabel}>뒤로</Text>
        </Pressable>
        <View style={styles.typeBadgeWrap}>
          <View style={styles.typeBadge}>
            <Text style={styles.typeBadgeText}>Keep</Text>
          </View>
        </View>
        {item ? (
          <View style={styles.headerActions}>
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="Keep 복사"
              onPress={() => { void copyTextToClipboard(buildKeepCopyText(item, resolvedProjectTitle, today)); }}
              hitSlop={6}
              style={styles.headerAction}
            >
              <Text style={styles.headerActionText}>복사</Text>
            </Pressable>
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="Keep 공유·내보내기"
              onPress={() => { void shareText(buildKeepCopyText(item, resolvedProjectTitle, today), shareTitle); }}
              hitSlop={6}
              style={styles.headerAction}
            >
              <Text style={styles.headerActionText}>공유</Text>
            </Pressable>
          </View>
        ) : null}
      </View>

      <ScrollView
        contentContainerStyle={styles.container}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
      >
        {error != null ? (
          <View style={styles.errorBox}>
            <Text style={styles.errorText}>{error}</Text>
            <Pressable accessibilityRole="button" onPress={() => { setLoading(true); void load(paramItem == null); }} style={styles.retryButton}>
              <Text style={styles.retryText}>다시 시도</Text>
            </Pressable>
          </View>
        ) : null}

        {item == null ? (
          loading ? <ActivityIndicator style={styles.loader} color={colors.accent} /> : null
        ) : (
          <>
            <View style={styles.badgeRow}>
              <KeepLifecycleBadge lifecycleKey={lifecycleKey} />
              {states.map((key) => (
                <KeepStateBadge key={key} stateKey={key} />
              ))}
              {loading && detail == null ? <ActivityIndicator size="small" color={colors.muted} /> : null}
            </View>

            <Text style={styles.title} selectable>
              {item.prefix?.name?.trim() ? (
                <>
                  <KeepPrefixText name={item.prefix.name} />{" "}
                </>
              ) : null}
              {item.title?.trim() || "(제목 없음)"}
            </Text>

            <View style={styles.fields}>
              <Field label="기한">
                <Text style={[styles.fieldText, overdue && styles.fieldTextOverdue, !expire && styles.fieldTextEmpty]}>
                  {expire ? keepExpireText(item, today, "full") : "없음"}
                </Text>
              </Field>
              {item.dateText?.trim() ? (
                <Field label="일시">
                  <Text style={styles.fieldText} selectable>{item.dateText.trim()}</Text>
                </Field>
              ) : null}
              {item.location?.trim() ? (
                <Field label="위치">
                  <Text style={styles.fieldText} selectable>{item.location.trim()}</Text>
                </Field>
              ) : null}
              <Field label="Project">
                <Text style={[styles.fieldText, !resolvedProjectTitle && styles.fieldTextEmpty]}>
                  {resolvedProjectTitle || "미지정"}
                </Text>
              </Field>
              {item.prefix?.name?.trim() ? (
                <Field label="말머리">
                  <KeepPrefixText name={item.prefix.name} />
                </Field>
              ) : null}
              {tags.length > 0 ? (
                <Field label="태그">
                  <KeepTagChips tags={tags} />
                </Field>
              ) : null}
              {item.createdAt ? (
                <Field label="등록">
                  <Text style={styles.fieldText}>{formatKeepDateTime(item.createdAt)}</Text>
                </Field>
              ) : null}
              {item.updatedAt && item.updatedAt !== item.createdAt ? (
                <Field label="수정">
                  <Text style={styles.fieldText}>{formatKeepDateTime(item.updatedAt)}</Text>
                </Field>
              ) : null}
            </View>

            <View style={styles.bodySection}>
              <Text style={styles.sectionLabel}>내용</Text>
              {detail == null && loading ? (
                <Text style={styles.bodyEmpty}>불러오는 중…</Text>
              ) : (
                <Text style={[styles.body, !body && styles.bodyEmpty]} selectable>
                  {body || "(내용 없음)"}
                </Text>
              )}
            </View>

            <Text style={styles.footerNote}>상세는 읽기 전용입니다. 상태 변경은 Keep 목록에서, 수정은 웹에서 합니다.</Text>
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
  backLabel: { color: colors.accent, fontSize: 16, fontWeight: "600" },
  typeBadgeWrap: { flex: 1 },
  typeBadge: {
    alignSelf: "flex-start",
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 12,
    backgroundColor: colors.surface,
    borderWidth: 1,
    borderColor: colors.border
  },
  typeBadgeText: { color: colors.secondaryText, fontSize: 12, fontWeight: "700" },
  headerActions: { flexDirection: "row", gap: 6 },
  headerAction: {
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 14,
    paddingHorizontal: 12,
    paddingVertical: 4
  },
  headerActionText: { color: colors.accent, fontSize: 13, fontWeight: "700" },
  container: { padding: 24, gap: 14, flexGrow: 1 },
  loader: { marginTop: 32 },
  errorBox: {
    alignItems: "center",
    gap: 8,
    padding: 12,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: "#F5C6CB",
    backgroundColor: "#FDF2F2"
  },
  errorText: { color: "#C0392B", fontSize: 14, textAlign: "center" },
  retryButton: {
    borderWidth: 1,
    borderColor: colors.accent,
    borderRadius: 14,
    paddingHorizontal: 14,
    paddingVertical: 6
  },
  retryText: { color: colors.accent, fontWeight: "700" },
  badgeRow: { flexDirection: "row", flexWrap: "wrap", alignItems: "center", gap: 6 },
  title: { color: colors.text, fontSize: 22, fontWeight: "800", lineHeight: 30 },
  fields: {
    gap: 10,
    padding: 14,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.input
  },
  field: { flexDirection: "row", gap: 12, alignItems: "flex-start" },
  fieldLabel: { width: 56, color: colors.muted, fontSize: 12.5, fontWeight: "700", paddingTop: 1 },
  fieldValue: { flex: 1 },
  fieldText: { color: colors.text, fontSize: 14.5, lineHeight: 20 },
  fieldTextOverdue: { color: OVERDUE_COLOR, fontWeight: "800" },
  fieldTextEmpty: { color: colors.muted },
  bodySection: {
    gap: 8,
    paddingTop: 14,
    borderTopWidth: 1,
    borderTopColor: colors.border
  },
  sectionLabel: { color: colors.muted, fontSize: 11.5, fontWeight: "700" },
  body: { color: colors.secondaryText, fontSize: 16, lineHeight: 26 },
  bodyEmpty: { color: colors.muted, fontSize: 14 },
  footerNote: { color: colors.muted, fontSize: 11.5, textAlign: "center", paddingTop: 8 }
});
