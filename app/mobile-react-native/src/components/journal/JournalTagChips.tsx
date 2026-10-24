import { Pressable, StyleSheet, Text, View } from "react-native";
import { useNavigation } from "@react-navigation/native";
import type { NativeStackNavigationProp } from "@react-navigation/native-stack";
import type { RootStackParamList } from "../../navigation/AppNavigator";
import { openTagResult } from "../../navigation/tagResult";
import { openThreadResult } from "../../navigation/threadResult";
import { colors } from "../../theme/colors";
import type { DisplayTag, JournalThreadEntry } from "../../types/journalDay";
import { TAG_CHIP_PALETTE, TAG_CTGR_COLOR } from "./journalTagStyle";

export type JournalTagChipsVariant = keyof typeof TAG_CHIP_PALETTE;

export type JournalTagChipsProps = {
  /** 표시할 태그 (displayTagsOf 결과) */
  tags: DisplayTag[];
  /** 태그 종류 — day: 일자 태그, diary: 일기 엔트리 태그, dream: 꿈 엔트리 태그 */
  variant: JournalTagChipsVariant;
  /** false 면 표시 전용 (기본 true: 탭하면 태그별 기록 화면) */
  pressable?: boolean;
};

/**
 * 인라인 태그 칩 한 줄. 웹 TagChip.vue 표기(`#[카테고리]태그명`)를 칩 형태로 옮겼다.
 * 칩을 탭하면 태그별 기록 화면(TagResult)을 연다 — 웹 태그 메뉴 "검색"과 같은 동작(종류는 variant).
 * 카드(Pressable) 안에 있어도 칩 탭이 우선한다.
 */
export function JournalTagChips({ tags, variant, pressable = true }: JournalTagChipsProps) {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  if (tags.length === 0) return null;
  const palette = TAG_CHIP_PALETTE[variant];
  const kindLabel = variant === "day" ? "일자 태그" : variant === "dream" ? "꿈 태그" : "일기 태그";

  return (
    <View style={styles.row} accessibilityLabel={`${kindLabel}: ${tags.map((t) => t.name).join(", ")}`}>
      {tags.map((tag) => (
        <Pressable
          key={`${tag.tagId}:${tag.name}`}
          disabled={!pressable}
          accessibilityRole={pressable ? "button" : undefined}
          accessibilityLabel={`${kindLabel} ${tag.ctgr ? `${tag.ctgr} ` : ""}${tag.name}`}
          accessibilityHint={pressable ? "이 태그가 붙은 기록 보기" : undefined}
          hitSlop={4}
          onPress={() => openTagResult(navigation, tag, variant)}
          style={({ pressed }) => [
            styles.chip,
            { backgroundColor: palette.background, borderColor: palette.border },
            pressed && styles.chipPressed
          ]}
        >
          <Text style={styles.chipText}>
            <Text style={styles.hash}>#</Text>
            {tag.ctgr ? <Text style={styles.ctgr}>[{tag.ctgr}]</Text> : null}
            <Text style={[styles.name, { color: palette.name }]}>{tag.name}</Text>
          </Text>
        </Pressable>
      ))}
    </View>
  );
}

export type JournalThreadChipsProps = {
  threads: JournalThreadEntry[] | null | undefined;
  /** false 면 표시 전용 (기본 true: 탭하면 스레드 글 목록 화면) */
  pressable?: boolean;
};

/**
 * 엔트리 소속 스레드 칩. 칩을 탭하면 스레드 글 목록 화면(ThreadResult)을 연다 — 웹 스레드 칩 클릭(스레드 상세)과 같은 동작.
 * 카드(Pressable) 안에 있어도 칩 탭이 우선한다.
 */
export function JournalThreadChips({ threads, pressable = true }: JournalThreadChipsProps) {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  const seen = new Set<number>();
  const list = (threads ?? []).filter((t) => {
    if (t == null || !t.threadTitle?.trim() || seen.has(t.threadId)) return false;
    seen.add(t.threadId);
    return true;
  });
  if (list.length === 0) return null;

  return (
    <View style={styles.row}>
      {list.map((thread) => (
        <Pressable
          key={thread.threadId}
          disabled={!pressable}
          accessibilityRole={pressable ? "button" : undefined}
          accessibilityLabel={`스레드 ${thread.threadTitle?.trim() ?? ""}`}
          accessibilityHint={pressable ? "이 스레드의 글 목록 보기" : undefined}
          hitSlop={4}
          onPress={() => openThreadResult(navigation, { threadId: thread.threadId, title: thread.threadTitle })}
          style={({ pressed }) => [styles.threadChip, pressed && styles.chipPressed]}
        >
          <Text style={styles.threadText} numberOfLines={1}>
            🧵 {thread.threadTitle?.trim()}
          </Text>
        </Pressable>
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  row: { flexDirection: "row", flexWrap: "wrap", gap: 6 },
  chip: {
    borderRadius: 10,
    borderWidth: 1,
    paddingHorizontal: 8,
    paddingVertical: 2
  },
  chipPressed: { opacity: 0.6 },
  chipText: { fontSize: 12 },
  hash: { color: colors.muted, fontWeight: "400" },
  ctgr: { color: TAG_CTGR_COLOR, fontSize: 10.5 },
  name: { fontWeight: "500" },
  threadChip: {
    maxWidth: "100%",
    borderRadius: 10,
    borderWidth: 1,
    borderStyle: "dashed",
    borderColor: colors.border,
    paddingHorizontal: 8,
    paddingVertical: 2
  },
  threadText: { color: colors.secondaryText, fontSize: 11.5, fontWeight: "500" }
});