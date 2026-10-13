import { KeepRichEditor, type KeepBodyReader } from "./KeepRichEditor";
import { TAG_CTGR_COLOR } from "../journal/journalTagStyle";
import { daysUntilExpire, ddayLabel } from "../../utils/keepExpire";
import { displayTagsOf } from "../../types/journalDay";
import { useState, type ReactNode } from "react";
import { Keyboard, Pressable, StyleSheet, Switch, Text, TextInput, View } from "react-native";
import { colors } from "../../theme/colors";
import { KeepPrefixText } from "./KeepBadges";
import { keepPrefixColor, keepProjectTitle, isKeepTerminal, type KeepItem, type KeepPrefixOption, type KeepProject } from "../../types/prospectiveKeep";
import type { KeepEditDraft } from "../../utils/keepEdit";
import { markdownToPlainText } from "../../utils/markdownText";
import { expireFromDateText, quickAddExpireHint } from "../../utils/keepQuickAdd";
import { parseExpireDt } from "../../utils/keepDateInput";
import { filterKeepTagCandidates, keepTagCandidates, keepTagKey, toggleKeepTag } from "../../utils/keepTags";

export type KeepEditField = keyof KeepEditDraft;

/** 상세형 표시 행. 눌렀을 때 그 바로 아래 편집 영역 하나를 펼치며 접어도 임시 값은 유지한다. */
function InlineField({ label, value, field, active, disabled, onOpen, children, title = false }: {
  label: string; value: ReactNode; field: KeepEditField; active: KeepEditField | null;
  disabled: boolean; onOpen: (field: KeepEditField | null) => void; children: ReactNode; title?: boolean;
}) {
  const expanded = active === field;
  return <View style={title ? styles.titleSection : styles.fieldSection}>
    <Pressable accessibilityRole="button" accessibilityLabel={label + " 편집"}
      accessibilityState={{ expanded, disabled }} disabled={disabled}
      onPress={() => { Keyboard.dismiss(); onOpen(expanded ? null : field); }}>
      {title ? value : <View style={styles.row}>
        <Text style={styles.label}>{label}</Text><View style={styles.value}>{value}</View>
        {!disabled ? <Text style={styles.editMark}>✎</Text> : null}
      </View>}
    </Pressable>
    {expanded && !disabled ? <View style={styles.editor}>
      {children}
      <Pressable accessibilityRole="button" accessibilityLabel={label + " 편집 접기"}
        onPress={() => { Keyboard.dismiss(); onOpen(null); }} style={styles.close}>
        <Text style={styles.link}>접기</Text>
      </Pressable>
    </View> : null}
  </View>;
}

/** API 조회 없는 기한 달력. 텍스트로 해석한 절대 날짜와 같은 yyyy-MM-dd만 임시 값으로 돌려준다. */
function DueCalendar({ value, onSelect }: { value: string; onSelect: (value: string) => void }) {
  const [month, setMonth] = useState(() => {
    const parsed = parseExpireDt(value);
    const date = parsed.kind === "ok" ? new Date(parsed.date + "T12:00:00") : new Date();
    return new Date(date.getFullYear(), date.getMonth(), 1);
  });
  const start = new Date(month.getFullYear(), month.getMonth(), 1 - month.getDay());
  const selected = parseExpireDt(value);
  return <View>
    <View style={styles.calendarHeading}>
      <Pressable accessibilityRole="button" accessibilityLabel="기한 이전 달"
        onPress={() => setMonth(new Date(month.getFullYear(), month.getMonth() - 1, 1))}><Text style={styles.link}>‹</Text></Pressable>
      <Text style={styles.text}>{month.getFullYear()}년 {month.getMonth() + 1}월</Text>
      <Pressable accessibilityRole="button" accessibilityLabel="기한 다음 달"
        onPress={() => setMonth(new Date(month.getFullYear(), month.getMonth() + 1, 1))}><Text style={styles.link}>›</Text></Pressable>
    </View>
    <View style={styles.calendarGrid}>
      {["일", "월", "화", "수", "목", "금", "토"].map((day) => <Text key={day} style={styles.weekday}>{day}</Text>)}
      {Array.from({ length: 42 }, (_, index) => {
        const date = new Date(start.getFullYear(), start.getMonth(), start.getDate() + index);
        const iso = [String(date.getFullYear()).padStart(4, "0"), String(date.getMonth() + 1).padStart(2, "0"), String(date.getDate()).padStart(2, "0")].join("-");
        const chosen = selected.kind === "ok" && selected.date === iso;
        return <Pressable key={iso} accessibilityRole="button" accessibilityLabel={"기한 " + iso}
          accessibilityState={{ selected: chosen }} onPress={() => onSelect(iso)}
          style={[styles.day, chosen && styles.chosen]}>
          <Text style={[styles.text, date.getMonth() !== month.getMonth() && styles.muted]}>{date.getDate()}</Text>
        </Pressable>;
      })}
    </View>
  </View>;
}

/** Keep 읽기·편집 통합 영역. 위치·본문은 원문으로 편집하고 화면은 표시할 때만 평문을 만든다. */
export function KeepInlineEditor({ item, draft, projects, prefixes, tagMap, active, disabled, onOpen, onChange, bodyReader, onBodyPending, onBodyError }: {
  item: KeepItem; draft: KeepEditDraft | null; projects: KeepProject[]; prefixes: KeepPrefixOption[];
  tagMap: Record<string, string[]>; active: KeepEditField | null; disabled: boolean;
  onOpen: (field: KeepEditField | null) => void; onChange: (patch: Partial<KeepEditDraft>) => void;
  bodyReader?: KeepBodyReader; onBodyPending?: (pending: boolean) => void; onBodyError?: (message: string) => void;
}) {
  const [query, setQuery] = useState("");
  const value = draft;
  const prefixId = value ? value.prefixId : item.prefixId ?? item.prefix?.id ?? null;
  const prefix = prefixes.find((option) => option.id === prefixId);
  const projectId = value ? value.projectId : item.projectId;
  const project = projects.find((option) => option.id === projectId);
  const prefixName = prefixId == null ? "" : prefix?.name ?? item.prefix?.name ?? "";
  const prefixClass = prefix?.textClass ?? item.prefix?.textClass;
  const candidates = filterKeepTagCandidates(keepTagCandidates(tagMap), query, value?.tags ?? []);
  const fieldProps = { active, disabled: disabled || value == null, onOpen };
  const input = (field: "title" | "dateText" | "location" | "content" | "expireDt", label: string, maxLength?: number) =>
    <TextInput accessibilityLabel={label + " 입력"} value={value?.[field] ?? ""} autoFocus
      maxLength={maxLength} multiline={field === "content"} textAlignVertical="top"
      onChangeText={(text) => onChange({ [field]: text })}
      style={[styles.input, field === "content" && styles.bodyInput]} />;
  const text = (v: string | null | undefined, empty = "없음") => <Text style={[styles.text, !v && styles.muted]}>{v || empty}</Text>;
  /** 기한은 일시의 유효 날짜를 따라가며 이번 편집에서 직접 지정한 날짜·빈 값은 저장 훅이 보존한다. */
  const terminal = isKeepTerminal(item);
  const rawDue = value ? value.expireDt : item.expireDt ?? "";
  const parsedDue = parseExpireDt(rawDue);
  const dueText = parsedDue.kind === "ok" ? parsedDue.date + (terminal ? "" : " (" + ddayLabel(daysUntilExpire(parsedDue.date)) + ")") : rawDue;
  const selectedTags = value ? value.tags : displayTagsOf(item.tag);
  const sourceContentKnown = typeof item.content === "string" || (item.content === null && (!item.markdownContent || item.markdownContent === "-"));
  const bodyText = value ? markdownToPlainText(value.content, { compactParagraphs: true }) : sourceContentKnown ? markdownToPlainText(item.content, { compactParagraphs: true }) : "(본문 원문 확인 필요)";
  const dueHint = quickAddExpireHint(value?.expireDt ?? item.expireDt ?? "", value?.dateText ?? item.dateText ?? "");
  return <View style={styles.root}>
    <InlineField {...fieldProps} field="title" label="제목" title value={<Text style={styles.title}>
      {prefixName ? <KeepPrefixText name={prefixName} textClass={prefixClass} /> : null}
      {prefixName ? " " : ""}{(value ? value.title : item.title) || "(제목 없음)"}
    </Text>}>{input("title", "제목", 100)}</InlineField>
    <View style={styles.fields}>
      <InlineField {...fieldProps} field="expireDt" label="기한" value={<Text style={[styles.text,
        !terminal && dueHint.tone === "warning" && styles.warning, !terminal && dueHint.tone === "danger" && styles.danger]}>
        {dueText || "없음"}
      </Text>}>
        {input("expireDt", "기한")}
        <Text style={styles.note}>{dueHint.text}</Text>
        <View style={styles.choices}>
          <Pressable accessibilityRole="button" onPress={() => onChange({ expireDt: "" })} style={styles.choice}><Text style={styles.link}>기한 없음</Text></Pressable>
          <Pressable accessibilityRole="button" disabled={!expireFromDateText(value?.dateText ?? "")}
            onPress={() => onChange({ expireDt: expireFromDateText(value?.dateText ?? "") })} style={styles.choice}>
            <Text style={styles.link}>일시에서 추출</Text>
          </Pressable>
        </View>
        <DueCalendar value={value?.expireDt ?? ""} onSelect={(expireDt) => onChange({ expireDt })} />
      </InlineField>
      <InlineField {...fieldProps} field="dateText" label="일시" value={text(value ? value.dateText : item.dateText)}>{input("dateText", "일시", 200)}</InlineField>
      <InlineField {...fieldProps} field="location" label="위치" value={text(value ? value.location : item.location)}>{input("location", "위치", 200)}</InlineField>
      <InlineField {...fieldProps} field="projectId" label="Project" value={text(projectId == null ? "" : keepProjectTitle(project) || "Project #" + projectId, "미지정")}>
        <View style={styles.choices}>
          {[{ id: null, label: "미지정" }, ...projects.map((p) => ({ id: p.id, label: keepProjectTitle(p) || "Project #" + p.id }))].map((p) =>
            <Pressable key={p.id ?? "none"} accessibilityRole="radio" accessibilityLabel={"Project " + p.label}
              accessibilityState={{ checked: value?.projectId === p.id }} onPress={() => onChange({ projectId: p.id })}
              style={[styles.choice, value?.projectId === p.id && styles.chosen]}><Text style={styles.text}>{p.label}</Text></Pressable>)}
        </View>
      </InlineField>
      <InlineField {...fieldProps} field="prefixId" label="말머리" value={prefixName ? <KeepPrefixText name={prefixName} textClass={prefixClass} /> : text("")}>
        <View style={styles.choices}>
          <Pressable accessibilityRole="radio" accessibilityLabel="말머리 없음" accessibilityState={{ checked: value?.prefixId == null }}
            onPress={() => onChange({ prefixId: null })} style={styles.choice}><Text style={styles.text}>없음</Text></Pressable>
          {prefixes.map((p) => <Pressable key={p.id} accessibilityRole="radio" accessibilityLabel={"말머리 " + p.name}
            accessibilityState={{ checked: value?.prefixId === p.id }} onPress={() => onChange({ prefixId: p.id })}
            style={[styles.choice, value?.prefixId === p.id && styles.chosen]}>
            <Text style={[styles.text, { color: keepPrefixColor(p.textClass) }]}>[{p.name}]</Text>
          </Pressable>)}
        </View>
      </InlineField>
      <InlineField {...fieldProps} field="tags" label="태그" value={selectedTags.length ? <View style={styles.choices}>{selectedTags.map((tag) => <Text key={JSON.stringify([tag.ctgr, tag.name])} style={styles.tagText}>
        <Text style={styles.muted}>#</Text>{tag.ctgr ? <Text style={{ color: TAG_CTGR_COLOR, fontSize: 10.5 }}>[{tag.ctgr}]</Text> : null}{tag.name}
      </Text>)}</View> : text("")}>
        <View style={styles.choices}>
          {value?.tags.map((tag) => <Pressable key={keepTagKey(tag)} accessibilityRole="button"
            accessibilityLabel={"태그 " + tag.name + " 해제"} onPress={() => onChange({ tags: toggleKeepTag(value.tags, tag) })} style={[styles.choice, styles.chosen]}>
            <Text style={styles.text}>{tag.ctgr ? "[" + tag.ctgr + "]" : ""}{tag.name} ×</Text>
          </Pressable>)}
        </View>
        <TextInput accessibilityLabel="태그 검색" placeholder="기존 태그 검색" value={query} onChangeText={setQuery} style={styles.input} />
        <View style={styles.choices}>{candidates.map((tag) => <Pressable key={keepTagKey(tag)} accessibilityRole="button"
          accessibilityLabel={"태그 " + tag.name + " 추가"} onPress={() => onChange({ tags: toggleKeepTag(value?.tags ?? [], tag) })} style={styles.choice}>
          <Text style={styles.text}>{tag.ctgr ? "[" + tag.ctgr + "]" : ""}{tag.name}</Text>
        </Pressable>)}</View>
        {!candidates.length ? <Text style={styles.note}>추가할 태그가 없습니다.</Text> : null}
      </InlineField>
      <InlineField {...fieldProps} field="important" label="중요" value={text(value?.important ? "★ 중요" : "", "보통")}>
        <Switch accessibilityLabel="중요 표시" value={value?.important ?? false} onValueChange={(important) => onChange({ important })} />
      </InlineField>
    </View>
    <InlineField {...fieldProps} field="content" label="내용" value={<Text style={styles.body} selectable>
      {bodyText || "(내용 없음)"}
    </Text>}><KeepRichEditor value={value?.content ?? ""} reader={bodyReader} onChange={(content) => onChange({ content })} onPending={onBodyPending} onError={onBodyError} /></InlineField>
  </View>;
}

const styles = StyleSheet.create({
  root: { gap: 14 }, titleSection: { gap: 8 }, title: { color: colors.text, fontSize: 22, fontWeight: "800", lineHeight: 30 },
  fields: { gap: 10, padding: 14, borderRadius: 10, borderWidth: 1, borderColor: colors.border, backgroundColor: colors.input },
  fieldSection: { gap: 8 }, row: { flexDirection: "row", gap: 12, alignItems: "flex-start" },
  label: { width: 56, color: colors.muted, fontSize: 12.5, fontWeight: "700", paddingTop: 1 },
  value: { flex: 1 }, text: { color: colors.text, fontSize: 14.5, lineHeight: 20 },
  tagText: { color: colors.text, fontSize: 14.5, fontWeight: "500" },
  body: { color: colors.secondaryText, fontSize: 16, lineHeight: 24 }, muted: { color: colors.muted },
  warning: { color: "#B7791F", fontWeight: "800" }, danger: { color: colors.danger },
  editMark: { color: colors.accent, fontSize: 12 }, editor: { gap: 10, padding: 12, borderWidth: 1, borderColor: colors.border, borderRadius: 10, backgroundColor: colors.background },
  input: { color: colors.text, backgroundColor: colors.input, borderWidth: 1, borderColor: colors.border, borderRadius: 8, padding: 10, fontSize: 16 },
  bodyInput: { minHeight: 180 }, note: { color: colors.muted, fontSize: 12 }, link: { color: colors.accent, fontWeight: "700" },
  close: { alignSelf: "flex-end", padding: 6 }, choices: { flexDirection: "row", flexWrap: "wrap", gap: 8 },
  choice: { borderWidth: 1, borderColor: colors.border, borderRadius: 8, paddingHorizontal: 10, paddingVertical: 7 },
  chosen: { backgroundColor: colors.surface, borderColor: colors.accent },
  calendarHeading: { flexDirection: "row", justifyContent: "space-between", padding: 8 },
  calendarGrid: { flexDirection: "row", flexWrap: "wrap" }, weekday: { width: "14.2857%", textAlign: "center", color: colors.muted, paddingVertical: 4 },
  day: { width: "14.2857%", paddingVertical: 6, alignItems: "center", borderRadius: 6 }
});
