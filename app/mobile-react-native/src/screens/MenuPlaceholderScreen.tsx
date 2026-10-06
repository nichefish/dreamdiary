import { StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import type { NativeStackScreenProps } from "@react-navigation/native-stack";
import type { RootStackParamList } from "../navigation/AppNavigator";
import { colors } from "../theme/colors";
import { MenuScreenHeader } from "../components/navigation/AppMenu";

type Props = NativeStackScreenProps<RootStackParamList, "MenuPlaceholder">;

/**
 * 모바일에 아직 없는 사용자 메뉴의 공용 `준비 중` 화면 (햄버거 메뉴에서 진입).
 * 메뉴 이름·묶음을 보여주고 웹에서 이용하도록 안내한다. 요청은 보내지 않는다.
 */
export function MenuPlaceholderScreen({ route }: Props) {
  const { menuName, groupName, url } = route.params;
  const path = [groupName?.trim(), menuName.trim()].filter(Boolean).join(" › ");
  return (
    <SafeAreaView edges={["top", "left", "right", "bottom"]} style={styles.safeArea}>
      <MenuScreenHeader title={menuName} currentUrl={url} />
      <View style={styles.container}>
        <View style={styles.card}>
          <Text style={styles.icon}>🚧</Text>
          <Text style={styles.path}>{path}</Text>
          <Text style={styles.title}>모바일 앱에서는 준비 중입니다</Text>
          <Text style={styles.body}>이 메뉴는 이후 모바일 버전에서 제공할 예정입니다.{"\n"}지금은 웹에서 이용해 주세요.</Text>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: { flex: 1, backgroundColor: colors.background },
  container: { flex: 1, padding: 20, justifyContent: "center" },
  card: {
    alignItems: "center",
    gap: 10,
    backgroundColor: colors.surface,
    borderRadius: 14,
    paddingHorizontal: 20,
    paddingVertical: 28
  },
  icon: { fontSize: 36 },
  path: { color: colors.muted, fontSize: 12.5, fontWeight: "700" },
  title: { color: colors.text, fontSize: 17, fontWeight: "800", textAlign: "center" },
  body: { color: colors.secondaryText, fontSize: 14, lineHeight: 21, textAlign: "center" }
});
