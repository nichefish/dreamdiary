import { useEffect, useRef, useState } from "react";
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Modal,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Switch,
  Text,
  TextInput,
  View
} from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { TAG_CTGR_COLOR } from "../journal/journalTagStyle";
import { filterKeepTagCandidates, keepTagCandidates, keepTagKey, toggleKeepTag, type KeepTagPick } from "../../utils/keepTags";
import { colors } from "../../theme/colors";
import { keepPrefixColor, keepProjectTitle, type KeepPrefixOption, type KeepProject } from "../../types/prospectiveKeep";
import {
  expireFromDateText,
  KEEP_QUICK_DATE_TEXT_MAX,
  KEEP_QUICK_TITLE_MAX,
  quickAddExpireHint,
  validateQuickAdd,
  type KeepQuickAddResult,
  type KeepQuickAddSubmit,
  type QuickAddHintTone
} from "../../utils/keepQuickAdd";

/** 지난 기한 경고색 — 목록 행과 같은 웹 `text-warning` */
const WARNING_COLOR = "#B7791F";

const HINT_COLOR: Record<QuickAddHintTone, string> = {
  muted: colors.muted,
  normal: colors.secondaryText,
  warning: WARNING_COLOR,
  danger: colors.danger,
  error: colors.danger
};

export type KeepQuickAddSheetProps = {
  visible: boolean;
  /** 고를 수 있는 Project (OPEN Project, 정렬 순) */
  projects: KeepProject[];
  /** 고를 수 있는 활성 Keep 말머리 (`/api/my/prefixes/options?contentType=PROSPECTIVE_KEEP`). 비면 말머리 줄을 숨긴다 */
  prefixes: KeepPrefixOption[];
  /** 열 때의 기본 Project — 현재 Project 칩, `전체`·`미지정`이면 null(미지정) */
  defaultProjectId: number | null;
  /** 저장. 등록·중요 표시·목록 조회는 화면이 맡고 결과만 돌려준다. */
  onSubmit: (input: KeepQuickAddSubmit) => Promise<KeepQuickAddResult>;
  onClose: () => void;
  /** 수정 모드 초기값(상세 응답). 없으면 신규 빠른 추가. 저장 시 위치·본문·태그 보존은 화면(`toKeepEditFormData`)이 맡는다 */
  initial?: KeepSheetInitial | null;
  /** 기존 태그 후보 맵 (`GET /api/prospective/keep/tag/categories`, 웹 자동완성과 같음). 여기 있는 태그만 고른다 */
  tagMap?: Record<string, string[]>;
};

/** 수정 시트 초기값 — 앱에서 고칠 수 있는 필드만 */
export type KeepSheetInitial = {
  title: string;
  dateText: string;
  expireDt: string;
  projectId: number | null;
  prefixId: number | null;
  important: boolean;
  /** 현재 태그 (상세 tagListStrWithCtgr) */
  tags: KeepTagPick[];
};

/**
 * Keep 빠른 추가 하단 시트 (keep-todo-plan §5.5, 2-2B).
 * - 제목(필수, 100자) · 일시 원문(선택, 200자) · 기한(일시에서 자동, 직접 고치거나 비울 수 있음) · 말머리 · Project · 중요
 * - 기한 규칙은 웹 등록 모달과 같다: 일시를 고치면 기한을 일시의 가장 늦은 날짜로 채우되, 사용자가 기한을 직접 고치거나 비운 뒤에는
 *   덮어쓰지 않는다(`기한 추출`로만 다시 뽑는다).
 * - 저장 중에는 버튼·닫기를 잠그고 자동 재전송하지 않는다. 연속 입력은 없다: 등록되면 화면(KeepHomeScreen)이 시트를 닫고
 *   목록에서 짧게 알린다. 서버가 응답한 실패만 시트에 남아 입력을 보존하고 메시지를 보인다.
 */
export function KeepQuickAddSheet({ visible, projects, prefixes, defaultProjectId, onSubmit, onClose, initial = null, tagMap }: KeepQuickAddSheetProps) {
  const editing = initial != null;
  const insets = useSafeAreaInsets();
  const [title, setTitle] = useState("");
  const [dateText, setDateText] = useState("");
  const [expireInput, setExpireInput] = useState("");
  /** auto: 일시에서 뽑은 값(일시를 고치면 따라감) / manual: 사용자가 고치거나 비운 값(덮어쓰지 않음) */
  const [expireSource, setExpireSource] = useState<"auto" | "manual">("auto");
  const [projectId, setProjectId] = useState<number | null>(defaultProjectId);
  const [prefixId, setPrefixId] = useState<number | null>(null);
  const [important, setImportant] = useState(false);
  const [tags, setTags] = useState<KeepTagPick[]>([]);
  const [tagQuery, setTagQuery] = useState("");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const savingRef = useRef(false);
  const titleRef = useRef<TextInput | null>(null);

  // 열 때마다 새 입력으로 시작하고 Project는 현재 칩을 따른다.
  useEffect(() => {
    if (!visible) return;
    setTitle(initial?.title ?? "");
    setDateText(initial?.dateText ?? "");
    setExpireInput(initial?.expireDt ?? "");
    // 수정: 저장된 기한은 사용자가 확정한 값이므로 일시를 고쳐도 덮어쓰지 않는다(웹 "기한이 비었을 때만 자동").
    setExpireSource(initial?.expireDt ? "manual" : "auto");
    setProjectId(initial ? initial.projectId : defaultProjectId);
    setPrefixId(initial?.prefixId ?? null);
    setImportant(initial?.important ?? false);
    setTags(initial?.tags ?? []);
    setTagQuery("");
    setError(null);
    // defaultProjectId는 연 순간의 값만 쓴다(열린 동안 칩이 바뀌어도 입력을 지우지 않는다).
  }, [visible]);

  const today = new Date();
  const hint = quickAddExpireHint(expireInput, dateText, today);
  const extractable = expireFromDateText(dateText, today);
  const projectOptions: { id: number | null; label: string }[] = [
    { id: null, label: "미지정" },
    ...projects.map((p) => ({ id: p.id, label: keepProjectTitle(p) || `Project #${p.id}` }))
  ];
  // 기본 Project가 선택지에 없으면(웹에서 종결 등) 그대로 보여 주되 이름은 번호로 둔다.
  if (projectId != null && !projectOptions.some((o) => o.id === projectId)) {
    projectOptions.push({ id: projectId, label: `Project #${projectId}` });
  }

  const tagCandidates = keepTagCandidates(tagMap ?? {});
  const tagMatches = filterKeepTagCandidates(tagCandidates, tagQuery, tags);

  function changeDateText(value: string) {
    setDateText(value);
    if (expireSource === "auto") setExpireInput(expireFromDateText(value, new Date()));
  }

  function changeExpire(value: string) {
    setExpireInput(value);
    setExpireSource("manual");
  }

  function clearExpire() {
    setExpireInput("");
    setExpireSource("manual");
  }

  function extractExpire() {
    if (!extractable) return;
    setExpireInput(extractable);
    setExpireSource("auto");
  }

  function requestClose() {
    if (savingRef.current) return;
    onClose();
  }

  async function save() {
    if (savingRef.current) return;
    // 선택한 말머리가 그사이 선택지에서 빠졌으면(웹에서 비활성화 등) 말머리 없이 보내지 않고 다시 고르게 한다.
    if (prefixId != null && !prefixes.some((p) => p.id === prefixId)) {
      setPrefixId(null);
      setError("선택한 말머리를 더 이상 쓸 수 없습니다. 다시 골라 주세요.");
      return;
    }
    const checked = validateQuickAdd({ title, dateText, expireInput, projectId, prefixId, tags, important }, new Date());
    if (!checked.ok) {
      setError(checked.message);
      return;
    }
    savingRef.current = true;
    setSaving(true);
    setError(null);
    try {
      const result = await onSubmit(checked.input);
      if (result.ok) {
        // 시트 닫기·완료 안내는 화면이 맡는다(연속 입력 없음).
        console.info("[KeepQuickAddSheet] saved", { id: result.id, important: checked.input.important });
      } else if (result.message) {
        setError(result.message);
      }
    } finally {
      savingRef.current = false;
      setSaving(false);
    }
  }

  return (
    <Modal visible={visible} transparent animationType="slide" onRequestClose={requestClose} statusBarTranslucent>
      <KeyboardAvoidingView style={styles.root} behavior={Platform.OS === "ios" ? "padding" : "height"}>
        <Pressable accessibilityRole="button" accessibilityLabel="빠른 추가 닫기" onPress={requestClose} style={styles.backdrop} />
        <View style={[styles.panel, { paddingBottom: insets.bottom + 12 }]} accessibilityViewIsModal>
          <View style={styles.panelHeader}>
            <Text style={styles.panelTitle} accessibilityRole="header">{editing ? "Keep 수정" : "새 Keep"}</Text>
            <Pressable accessibilityRole="button" accessibilityLabel="빠른 추가 닫기" disabled={saving} onPress={requestClose}
              hitSlop={8} style={({ pressed }) => [styles.closeButton, (pressed || saving) && styles.pressed]}>
              <Text style={styles.closeText}>닫기</Text>
            </Pressable>
          </View>
          <ScrollView keyboardShouldPersistTaps="handled" contentContainerStyle={styles.body}>
            <TextInput
              ref={titleRef}
              accessibilityLabel="제목"
              autoFocus={!editing}
              editable={!saving}
              maxLength={KEEP_QUICK_TITLE_MAX}
              onChangeText={(value) => { setTitle(value); if (error) setError(null); }}
              onSubmitEditing={() => { void save(); }}
              placeholder="제목을 입력하세요. (필수)"
              placeholderTextColor={colors.muted}
              returnKeyType="done"
              submitBehavior="submit"
              style={styles.input}
              value={title}
            />
            <Text style={styles.label}>일시 <Text style={styles.labelNote}>(자유 입력)</Text></Text>
            <TextInput
              accessibilityLabel="일시"
              editable={!saving}
              maxLength={KEEP_QUICK_DATE_TEXT_MAX}
              onChangeText={changeDateText}
              placeholder="예: 10/17,18 · 10/24,25 19:30"
              placeholderTextColor={colors.muted}
              style={styles.input}
              value={dateText}
            />
            <Text style={styles.label}>기한 <Text style={styles.labelNote}>(일시에서 자동 추출)</Text></Text>
            <View style={styles.expireRow}>
              <TextInput
                accessibilityLabel="기한"
                autoCapitalize="none"
                autoCorrect={false}
                editable={!saving}
                keyboardType="numbers-and-punctuation"
                onChangeText={changeExpire}
                placeholder="일시를 입력하면 자동으로 채워집니다"
                placeholderTextColor={colors.muted}
                style={[styles.input, styles.expireInput]}
                value={expireInput}
              />
              {expireInput ? (
                <Pressable accessibilityRole="button" accessibilityLabel="기한 비우기" disabled={saving} onPress={clearExpire}
                  hitSlop={6} style={styles.smallButton}>
                  <Text style={styles.smallButtonText}>비우기</Text>
                </Pressable>
              ) : null}
              <Pressable accessibilityRole="button" accessibilityLabel="기한 추출" accessibilityState={{ disabled: !extractable || saving }}
                disabled={!extractable || saving} onPress={extractExpire} hitSlop={6}
                style={[styles.smallButton, (!extractable || saving) && styles.disabled]}>
                <Text style={styles.smallButtonText}>기한 추출</Text>
              </Pressable>
            </View>
            <Text style={[styles.hint, { color: HINT_COLOR[hint.tone] }]}>{hint.text}</Text>

            {prefixes.length > 0 ? (
              <>
                <Text style={styles.label}>말머리</Text>
                <ScrollView horizontal showsHorizontalScrollIndicator={false} keyboardShouldPersistTaps="handled" contentContainerStyle={styles.chipRow}>
                  {[{ id: null as number | null, name: "없음", textClass: null as string | null | undefined }, ...prefixes].map((option) => {
                    const active = option.id === prefixId;
                    const color = option.id == null ? null : keepPrefixColor(option.textClass);
                    return (
                      <Pressable key={option.id ?? "none"} accessibilityRole="button" accessibilityLabel={option.id == null ? "말머리 없음" : `말머리 ${option.name}`}
                        accessibilityState={{ selected: active }} disabled={saving} onPress={() => setPrefixId(option.id)}
                        style={[styles.chip, active && styles.chipActive]}>
                        <Text style={[styles.chipText, !active && color ? { color } : null, active && styles.chipTextActive]} numberOfLines={1}>
                          {option.id == null ? option.name : `[${option.name}]`}
                        </Text>
                      </Pressable>
                    );
                  })}
                </ScrollView>
              </>
            ) : null}

            <Text style={styles.label}>태그 <Text style={styles.labelNote}>(기존 태그에서 선택)</Text></Text>
            {tags.length > 0 ? (
              <View style={styles.tagWrap}>
                {tags.map((tag) => (
                  <Pressable key={keepTagKey(tag)} accessibilityRole="button" accessibilityLabel={`태그 ${tag.ctgr ? `[${tag.ctgr}]` : ""}${tag.name} 빼기`}
                    disabled={saving} onPress={() => setTags((prev) => toggleKeepTag(prev, tag))} style={[styles.chip, styles.chipActive]}>
                    <Text style={[styles.chipText, styles.chipTextActive]} numberOfLines={1}>
                      #{tag.ctgr ? `[${tag.ctgr}]` : ""}{tag.name} ✕
                    </Text>
                  </Pressable>
                ))}
              </View>
            ) : null}
            {tagCandidates.length === 0 ? (
              <Text style={[styles.hint, { color: colors.muted }]}>고를 수 있는 태그가 없습니다. 새 태그는 웹에서 만듭니다.</Text>
            ) : (
              <>
                <TextInput
                  accessibilityLabel="태그 검색"
                  autoCapitalize="none"
                  autoCorrect={false}
                  editable={!saving}
                  onChangeText={setTagQuery}
                  placeholder="태그 검색"
                  placeholderTextColor={colors.muted}
                  style={styles.input}
                  value={tagQuery}
                />
                <ScrollView horizontal showsHorizontalScrollIndicator={false} keyboardShouldPersistTaps="handled" contentContainerStyle={styles.chipRow}>
                  {tagMatches.length === 0 ? (
                    <Text style={[styles.hint, { color: colors.muted }]}>일치하는 태그가 없습니다.</Text>
                  ) : tagMatches.map((tag) => (
                    <Pressable key={keepTagKey(tag)} accessibilityRole="button" accessibilityLabel={`태그 ${tag.ctgr ? `[${tag.ctgr}]` : ""}${tag.name} 추가`}
                      disabled={saving} onPress={() => setTags((prev) => toggleKeepTag(prev, tag))} style={styles.chip}>
                      <Text style={styles.chipText} numberOfLines={1}>
                        #{tag.ctgr ? <Text style={{ color: TAG_CTGR_COLOR }}>[{tag.ctgr}]</Text> : null}{tag.name}
                      </Text>
                    </Pressable>
                  ))}
                </ScrollView>
              </>
            )}

            <Text style={styles.label}>Project</Text>
            <ScrollView horizontal showsHorizontalScrollIndicator={false} keyboardShouldPersistTaps="handled" contentContainerStyle={styles.chipRow}>
              {projectOptions.map((option) => {
                const active = option.id === projectId;
                return (
                  <Pressable key={option.id ?? "none"} accessibilityRole="button" accessibilityState={{ selected: active }}
                    disabled={saving} onPress={() => setProjectId(option.id)}
                    style={[styles.chip, active && styles.chipActive]}>
                    <Text style={[styles.chipText, active && styles.chipTextActive]} numberOfLines={1}>{option.label}</Text>
                  </Pressable>
                );
              })}
            </ScrollView>

            <View style={styles.importantRow}>
              <Text style={styles.importantLabel}><Text style={styles.star}>★</Text> 중요</Text>
              <Switch accessibilityLabel="중요" disabled={saving} value={important} onValueChange={setImportant}
                trackColor={{ false: colors.border, true: colors.accent }} thumbColor={colors.input} />
            </View>

            {error ? <Text style={styles.errorText} accessibilityLiveRegion="polite">{error}</Text> : null}

            <Pressable accessibilityRole="button" accessibilityLabel="Keep 저장" accessibilityState={{ disabled: saving, busy: saving }}
              disabled={saving} onPress={() => { void save(); }}
              style={({ pressed }) => [styles.saveButton, (pressed || saving) && styles.pressed]}>
              {saving ? <ActivityIndicator color={colors.onAccent} /> : <Text style={styles.saveText}>저장</Text>}
            </Pressable>
            <Text style={styles.footerNote}>{editing ? "위치·본문은 그대로 저장됩니다. 고치려면 웹에서 편집하세요. 새 태그도 웹에서 만듭니다." : "위치·본문·새 태그는 웹 편집에서 입력합니다."}</Text>
          </ScrollView>
        </View>
      </KeyboardAvoidingView>
    </Modal>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1, justifyContent: "flex-end" },
  backdrop: { position: "absolute", top: 0, right: 0, bottom: 0, left: 0, backgroundColor: "rgba(32, 27, 22, 0.35)" },
  panel: {
    maxHeight: "90%",
    backgroundColor: colors.background,
    borderTopLeftRadius: 18,
    borderTopRightRadius: 18,
    paddingTop: 14
  },
  panelHeader: { flexDirection: "row", alignItems: "center", paddingHorizontal: 18, paddingBottom: 8 },
  panelTitle: { flex: 1, fontSize: 17, fontWeight: "800", color: colors.text },
  closeButton: { paddingHorizontal: 10, paddingVertical: 6 },
  closeText: { color: colors.secondaryText, fontSize: 14.5, fontWeight: "700" },
  body: { paddingHorizontal: 18, paddingBottom: 8, gap: 8 },
  label: { marginTop: 6, color: colors.secondaryText, fontSize: 13, fontWeight: "800" },
  labelNote: { color: colors.muted, fontSize: 11.5, fontWeight: "600" },
  input: {
    backgroundColor: colors.input,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: colors.border,
    paddingHorizontal: 12,
    paddingVertical: 9,
    color: colors.text,
    fontSize: 15
  },
  expireRow: { flexDirection: "row", alignItems: "center", gap: 6 },
  expireInput: { flex: 1 },
  smallButton: {
    borderRadius: 8,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface,
    paddingHorizontal: 10,
    paddingVertical: 9
  },
  smallButtonText: { color: colors.accent, fontSize: 13, fontWeight: "700" },
  disabled: { opacity: 0.45 },
  hint: { fontSize: 12, lineHeight: 17 },
  tagWrap: { flexDirection: "row", flexWrap: "wrap", gap: 6 },
  chipRow: { gap: 8, paddingRight: 8 },
  chip: {
    maxWidth: 180,
    paddingHorizontal: 13,
    paddingVertical: 6,
    borderRadius: 18,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.input
  },
  chipActive: { backgroundColor: colors.accent, borderColor: colors.accent },
  chipText: { color: colors.secondaryText, fontSize: 13, fontWeight: "600" },
  chipTextActive: { color: colors.onAccent },
  importantRow: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", marginTop: 6 },
  importantLabel: { color: colors.text, fontSize: 14.5, fontWeight: "700" },
  star: { color: "#F6B100" },
  errorText: { color: colors.danger, fontSize: 13.5 },
  saveButton: {
    marginTop: 6,
    backgroundColor: colors.accent,
    borderRadius: 10,
    alignItems: "center",
    justifyContent: "center",
    minHeight: 46
  },
  saveText: { color: colors.onAccent, fontSize: 16, fontWeight: "800" },
  footerNote: { color: colors.muted, fontSize: 11.5, textAlign: "center", paddingTop: 4 },
  pressed: { opacity: 0.6 }
});
