import type { KeepBodyReader } from "../components/keep/KeepRichEditor";
import { usePreventRemove } from "@react-navigation/native";
import { KeepInlineEditor, type KeepEditField } from "../components/keep/KeepInlineEditor";
import { validateQuickAdd } from "../utils/keepQuickAdd";
import { useCallback, useEffect, useMemo, useRef, useState, type ReactNode } from "react";
import {
  ActivityIndicator,
  Alert,
  KeyboardAvoidingView,
  Pressable,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  View
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import { deleteKeep, getKeepDetail } from "../api/dreamDiaryApi";
import { useKeepEdit } from "../components/keep/useKeepEdit";
import { notifyBrief } from "../components/journal/JournalCopyShareBar";
import { confirmKeepDelete, keepManageFailureMessage } from "../utils/keepManage";
import { isUnauthorizedError } from "../api/client";
import type { RootStackParamList } from "../navigation/AppNavigator";
import { colors } from "../theme/colors";
import {
  keepLifecycleKeyOf,
  keepProjectTitle,
  keepStatesOf,
  type KeepItem
} from "../types/prospectiveKeep";
import { formatKeepDateTime } from "../utils/keepProjection";
import { buildKeepCopyText } from "../utils/keepCopy";
import { copyTextToClipboard, shareText } from "../components/journal/JournalCopyShareBar";
import { KeepLifecycleBadge, KeepStateBadge } from "../components/keep/KeepBadges";

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
 * Keep 읽기·편집 통합 화면 — 목록에서 연다. 웹 수정 모달의 필드를 읽기 형태로 보여 주고 항목 아래에서 편집한다.
 * - GET /api/prospective/keep/{id} 로 다시 읽는다. 로드 전에는 목록 항목(route.params.item)을 임시로 보여준다.
 * - 라이프사이클은 상세 응답 값을 그대로 쓴다(서버가 목록과 같은 방식으로 부착, 행이 없으면 OPEN).
 * - Project 이름은 목록에서 넘겨받고, 목록 항목 없이 열렸거나 당겨서 새로고침하면 Project 목록을 다시 읽어 찾는다.
 * - 본문 원문은 읽기에서 평문으로 표시하고 편집에서 HTML 서식을 표시·보존한다. 복사·공유는 헤더 버튼.
 * - 수정: 항목 아래 편집 영역의 임시 값을 모아 한 번 저장한다. 웹 수정 모달과 같은 POST /api/prospective/keep/{id} multipart로,
 *   태그는 편집 영역에서 고르고(기존 태그만), 위치·본문은 지정된 원문, 생략 필드·순번은 상세 응답에서 옮긴다(`toKeepEditFormData`, 보존 불가 시 저장 차단).
 *   저장 직전 상세를 다시 받아 그사이 웹에서 고쳤으면(updatedAt 변화) 저장하지 않는다. 중요는 바뀐 경우에만 IMPRTC 토글.
 * - 완료·라이프사이클·요구는 목록에서 변경하며 삭제는 이 화면의 파괴적 확인을 거친다. Project는 임시 값과 함께 저장한다.
 */
export function KeepDetailScreen({ route, navigation }: Props) {
  const { id, item: paramItem, projectTitle: paramProjectTitle } = route.params;
  const [detail, setDetail] = useState<KeepItem | null>(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loadedAt, setLoadedAt] = useState<Date>(() => new Date());
  const seqRef = useRef(0);
  const readBusyRef = useRef(false);
  /** 통합 화면의 임시 편집값 — 저장 뒤 상세를 다시 받으며 목록은 복귀 시 갱신한다. */
  const edit = useKeepEdit({ onSaved: () => { void load(); }, onConflict: (fresh) => setDetail(fresh), isBusy: () => manageBusyRef.current, logTag: "KeepDetailScreen" });
  /** 삭제(2-4)는 다른 쓰기·임시 변경과 함께 실행하지 않는다. */
  const manageBusyRef = useRef(false);
  /** 삭제 성공 응답을 받은 화면의 목록 복귀만 나가기 잠금에서 승인한다. */
  const deleteSucceededRef = useRef(false);
  const [manageBusy, setManageBusy] = useState(false);
  const [activeField, setActiveField] = useState<KeepEditField | null>(null);
  const [inputError, setInputError] = useState<string | null>(null);
  const focusedRef = useRef(false);
  /** 본문 bridge의 마지막 입력은 접기·저장 전에 확인한다. */
  const bodyReader: KeepBodyReader = useRef(null);
  const bodyBusyRef = useRef(false);
  const [bodyBusy, setBodyBusy] = useState(false);
  const [bodyPending, setBodyPending] = useState(false);
  const [bodyError, setBodyError] = useState<string | null>(null);
  const mountedRef = useRef(true);
  useEffect(() => () => { mountedRef.current = false; }, []);

  /** 서버 상세와 편집 선택지를 읽는다. Project 이름은 같은 선택지 응답으로 확인한다. */
  const load = useCallback(
    async () => {
      if (readBusyRef.current || edit.loadingRef.current) return;
      readBusyRef.current = true;
      setRefreshing(true);
      const seq = ++seqRef.current;
      setError(null);
      const [detailRes] = await Promise.allSettled([getKeepDetail(id)]);
      if (!mountedRef.current || seq !== seqRef.current) return;
      if (detailRes.status === "fulfilled") {
        setDetail(detailRes.value);
        void edit.initialize(detailRes.value);
        setLoadedAt(new Date());
      } else {
        const e: unknown = detailRes.reason;
        if (!isUnauthorizedError(e)) {
          console.error("[KeepDetailScreen] keep detail load failed", { id }, e);
          setError(errorMessage(e, "Keep을 불러오지 못했습니다."));
        }
      }
      readBusyRef.current = false;
      setLoading(false);
      setRefreshing(false);
    },
    [id, paramItem?.projectId]
  );

  useEffect(() => {
    void load();
  }, [load, paramItem]);

  /** 상세 응답이 오면 그 값(라이프사이클 포함), 오기 전에는 목록 항목 */
  const item = useMemo(() => detail ?? paramItem ?? null, [detail, paramItem]);
  const today = loadedAt;

  /** 삭제: 제목을 밝힌 파괴적 확인 → DELETE 한 번(잠금) → 안내 후 상세를 떠난다(목록은 포커스 갱신). 실패는 서버 메시지. */
  async function deleteThis() {
    const target = detail ?? paramItem ?? null;
    if (target == null || manageBusyRef.current || edit.savingRef.current || edit.dirty || bodyPending || bodyBusyRef.current || readBusyRef.current) return;
    manageBusyRef.current = true;
    setManageBusy(true);
    try {
      if (!(await confirmKeepDelete(target, Alert.alert)) || !mountedRef.current) return;
      try {
        await deleteKeep(target.id);
      } catch (e) {
        if (!mountedRef.current || isUnauthorizedError(e)) return;
        console.error("[KeepDetailScreen] delete failed", { id: target.id }, e);
        notifyBrief(keepManageFailureMessage(e, "Keep을 삭제하지 못했습니다."));
        return;
      }
      notifyBrief("Keep을 삭제했습니다.");
      if (mountedRef.current) {
        deleteSucceededRef.current = true;
        console.info("[KeepDetailScreen] delete succeeded: return to list", { id: target.id });
        navigation.goBack();
      }
    } finally {
      manageBusyRef.current = false;
      if (mountedRef.current) setManageBusy(false);
    }
  }

  /** Project 이동 대상·선택지는 편집값으로 준비하고 저장 잠금 아래 전체 POST로 반영한다. 저장 뒤 상세·Project, 목록 복귀 시 목록을 다시 받는다. */
  function discard() {
    if (edit.savingRef.current || bodyBusyRef.current) return;
    setBodyPending(false); setBodyError(null);
    edit.reset();
    setActiveField(null);
    setInputError(null);
    void load();
  }

  /** 뒤로·제스처로 나갈 때 임시 값을 확인한다. 저장 중에는 화면을 떠나지 않는다. */
  usePreventRemove(edit.dirty || bodyPending || bodyBusy || edit.saving || manageBusy, ({ data }) => {
    if (deleteSucceededRef.current) { navigation.dispatch(data.action); return; }
    if (edit.savingRef.current || manageBusyRef.current || edit.loadingRef.current || bodyBusyRef.current) return;
    Alert.alert("변경 내용 버리기", "저장하지 않은 변경 내용을 버리고 나갈까요?", [
      { text: "계속 편집", style: "cancel" },
      { text: "버리기", style: "destructive", onPress: () => { setBodyPending(false); edit.reset(); navigation.dispatch(data.action); } }
    ]);
  });

  useEffect(() => {
    if (route.params.editField && edit.draft && !focusedRef.current) {
      focusedRef.current = true;
      setActiveField(route.params.editField);
    }
  }, [edit.draft, route.params.editField]);

  /** 최신 본문 확인이 성공한 경우에만 편집 영역 접기·필드 전환·저장을 허용한다. */
  async function confirmBody(): Promise<string | undefined | null> {
    if (!bodyReader.current) return undefined;
    if (bodyBusyRef.current) return null;
    bodyBusyRef.current = true; setBodyBusy(true);
    try {
      const content = await bodyReader.current();
      if (!mountedRef.current) return null;
      edit.patch({ content }); setBodyPending(false); setBodyError(null);
      return content;
    } catch (error) {
      console.error("[KeepDetailScreen] body snapshot failed", error);
      if (mountedRef.current) setBodyError("최신 본문을 확인하지 못해 작업을 멈췄습니다. 입력을 유지하고 있습니다. 다시 시도하세요.");
      return null;
    } finally {
      bodyBusyRef.current = false;
      if (mountedRef.current) setBodyBusy(false);
    }
  }

  async function openField(field: KeepEditField | null) {
    if (bodyBusyRef.current) return;
    if (activeField === "content" && field !== "content") {
      const content = await confirmBody();
      if (content === null || !mountedRef.current) return;
    }
    setActiveField(field);
  }

  async function save() {
    const sourceDraft = edit.draft;
    if (!sourceDraft || edit.savingRef.current || manageBusyRef.current || readBusyRef.current || bodyBusyRef.current) return;
    const content = await confirmBody();
    if (content === null || !mountedRef.current) return;
    const draft = { ...sourceDraft, content: content ?? sourceDraft.content };
    const checked = validateQuickAdd({ ...draft, expireInput: draft.expireDt });
    if (!checked.ok) { setInputError(checked.message); return; }
    if (draft.location.length > 200) { setInputError("위치는 200자 이내로 입력하세요."); return; }
    setInputError(null);
    const result = await edit.save({ ...checked.input, location: draft.location, content: draft.content });
    if (result.ok) setActiveField(null);
  }

  function onRefresh() {
    if (edit.savingRef.current || manageBusyRef.current || edit.loadingRef.current || bodyBusyRef.current) return;
    if (edit.dirty || bodyPending) { Alert.alert("변경 내용 유지", "먼저 변경 내용을 저장하거나 취소한 뒤 새로 고쳐 주세요."); return; }
    setRefreshing(true);
    void load();
  }

  const lifecycleKey = keepLifecycleKeyOf(item);
  const states = keepStatesOf(item).filter((key) => key !== "IMPRTC" || !edit.draft || edit.draft.important);
  if (edit.draft?.important && !states.includes("IMPRTC")) states.unshift("IMPRTC");
  const resolvedProjectTitle = item?.projectId == null ? "" : keepProjectTitle(edit.projects.find((p) => p.id === item.projectId))
    || (item.projectId === paramItem?.projectId ? paramProjectTitle : "") || `Project #${item.projectId}`;
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
              accessibilityLabel="Keep 복사" disabled={edit.dirty || bodyPending || bodyBusy || edit.saving}
              onPress={() => { void copyTextToClipboard(buildKeepCopyText(item, resolvedProjectTitle, today)); }}
              hitSlop={6}
              style={styles.headerAction}
            >
              <Text style={styles.headerActionText}>복사</Text>
            </Pressable>
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="Keep 공유·내보내기" disabled={edit.dirty || bodyPending || bodyBusy || edit.saving}
              onPress={() => { void shareText(buildKeepCopyText(item, resolvedProjectTitle, today), shareTitle); }}
              hitSlop={6}
              style={styles.headerAction}
            >
              <Text style={styles.headerActionText}>공유</Text>
            </Pressable>
          </View>
        ) : null}
      </View>

      <KeyboardAvoidingView style={{ flex: 1 }} behavior="padding">
      <ScrollView keyboardShouldPersistTaps="handled"
        contentContainerStyle={styles.container}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
      >
        {error != null ? (
          <View style={styles.errorBox}>
            <Text style={styles.errorText}>{error}</Text>
            <Pressable accessibilityRole="button" onPress={() => { setLoading(true); void load(); }} style={styles.retryButton}>
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

            <KeepInlineEditor item={item} draft={edit.draft} projects={edit.projects} prefixes={edit.prefixes}
              tagMap={edit.tagMap} active={activeField} disabled={edit.saving || edit.loading || manageBusy || loading || refreshing}
              onOpen={(field) => { void openField(field); }} bodyReader={bodyReader} onBodyPending={setBodyPending} onBodyError={setBodyError} onChange={(patch) => { if (readBusyRef.current) return; setInputError(null); edit.patch(patch); }} />
            <View style={styles.fields}>
              {item.createdAt ? <Field label="등록"><Text style={styles.fieldText}>{formatKeepDateTime(item.createdAt)}</Text></Field> : null}
              {item.updatedAt && item.updatedAt !== item.createdAt ? <Field label="수정"><Text style={styles.fieldText}>{formatKeepDateTime(item.updatedAt)}</Text></Field> : null}
            </View>
            {edit.loading ? <ActivityIndicator color={colors.accent} /> : null}
            {edit.optionError ? <Text style={styles.footerNote}>{edit.optionError}</Text> : null}
            {inputError || edit.error || bodyError ? <Text style={styles.errorText}>{inputError || edit.error || bodyError}</Text> : null}
            {edit.dirty || bodyPending || bodyBusy || edit.saving ? <View style={styles.manageRow}>
              <Pressable accessibilityRole="button" accessibilityLabel="Keep 변경 저장" disabled={edit.saving || bodyBusy}
                onPress={() => { void save(); }} style={styles.manageButton}>
                {edit.saving ? <ActivityIndicator color={colors.accent} /> : <Text style={styles.manageText}>저장</Text>}
              </Pressable>
              <Pressable accessibilityRole="button" accessibilityLabel="Keep 변경 취소" disabled={edit.saving || bodyBusy}
                onPress={discard} style={styles.manageButton}><Text style={styles.manageText}>취소</Text></Pressable>
            </View> : null}
            <View style={styles.manageRow}>
              <Pressable accessibilityRole="button" accessibilityLabel="Keep 삭제" disabled={manageBusy || bodyPending || bodyBusy || edit.saving || edit.dirty || loading || refreshing}
                onPress={() => { void deleteThis(); }} style={[styles.manageButton, styles.deleteButton, (manageBusy || edit.saving || edit.dirty || loading || refreshing) && styles.manageDisabled]}>
                <Text style={[styles.manageText, styles.deleteText]}>삭제</Text>
              </Pressable>
            </View>
            <Text style={styles.footerNote}>항목을 눌러 바로 아래에서 편집합니다. 변경값은 저장해야 반영됩니다. 삭제는 되돌릴 수 없습니다.</Text>
          </>
        )}
      </ScrollView>
      </KeyboardAvoidingView>
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
  fieldTextToday: { color: colors.danger },
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
  manageRow: { flexDirection: "row", gap: 10, paddingTop: 8 },
  manageButton: { flex: 1, alignItems: "center", borderWidth: 1, borderColor: colors.border, borderRadius: 10, paddingVertical: 10 },
  manageDisabled: { opacity: 0.5 },
  manageText: { color: colors.accent, fontSize: 14, fontWeight: "700" },
  deleteButton: { borderColor: colors.danger },
  deleteText: { color: colors.danger },
  footerNote: { color: colors.muted, fontSize: 11.5, textAlign: "center", paddingTop: 8 }
});
