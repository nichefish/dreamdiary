import { useEffect, useRef, useState } from "react";
import {
  ActivityIndicator,
  Animated,
  Modal,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  useWindowDimensions,
  View
} from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { useNavigation, type NavigationProp, type ParamListBase } from "@react-navigation/native";
import { colors } from "../../theme/colors";
import {
  getCachedAppMenu,
  isCurrentMenu,
  loadAppMenu,
  navigateToMenu,
  type AppMenu,
  type AppMenuItem
} from "../../navigation/appMenu";

type DrawerProps = {
  visible: boolean;
  onClose: () => void;
  /** 현재 화면의 웹 메뉴 URL — 일치하는 항목을 강조한다 */
  currentUrl?: string;
};

/**
 * 햄버거 메뉴 드로어 — 왼쪽에서 밀려 나오는 Modal 기반 사이드 시트(네이티브 의존성 없음).
 * 웹 사이드바의 사용자 메뉴(GET /api/menus?mode=USER)를 같은 묶음·이름·순서로 보여준다. 관리자 메뉴는 없다.
 * 닫기: 배경 탭, ✕, Android 뒤로 가기(onRequestClose), 항목 선택.
 */
export function AppMenuDrawer({ visible, onClose, currentUrl }: DrawerProps) {
  const navigation = useNavigation<NavigationProp<ParamListBase>>();
  const insets = useSafeAreaInsets();
  const { width } = useWindowDimensions();
  const panelWidth = Math.min(320, Math.round(width * 0.82));
  const progress = useRef(new Animated.Value(0)).current;
  const [mounted, setMounted] = useState(visible);
  const [menu, setMenu] = useState<AppMenu | null>(() => getCachedAppMenu());

  useEffect(() => {
    if (visible) {
      setMounted(true);
      Animated.timing(progress, { toValue: 1, duration: 220, useNativeDriver: true }).start();
      let alive = true;
      void loadAppMenu().then((next) => {
        if (alive) setMenu(next);
      });
      return () => {
        alive = false;
      };
    }
    Animated.timing(progress, { toValue: 0, duration: 180, useNativeDriver: true }).start(({ finished }) => {
      if (finished) setMounted(false);
    });
    return undefined;
  }, [visible, progress]);

  if (!mounted) return null;

  const translateX = progress.interpolate({ inputRange: [0, 1], outputRange: [-panelWidth, 0] });
  const backdropOpacity = progress.interpolate({ inputRange: [0, 1], outputRange: [0, 0.4] });

  function select(item: AppMenuItem) {
    onClose();
    if (isCurrentMenu(item, currentUrl)) return;
    navigateToMenu(navigation, item);
  }

  return (
    <Modal visible transparent animationType="none" statusBarTranslucent onRequestClose={onClose}>
      <View style={styles.root}>
        <Animated.View style={[styles.backdrop, { opacity: backdropOpacity }]}>
          <Pressable
            accessibilityRole="button"
            accessibilityLabel="메뉴 닫기"
            onPress={onClose}
            style={StyleSheet.absoluteFill}
          />
        </Animated.View>
        <Animated.View
          accessibilityViewIsModal
          style={[
            styles.panel,
            {
              width: panelWidth,
              paddingTop: insets.top + 12,
              paddingBottom: insets.bottom + 12,
              transform: [{ translateX }]
            }
          ]}
        >
          <View style={styles.panelHeader}>
            <View style={styles.panelHeaderText}>
              <Text style={styles.kicker}>DreamDiary</Text>
              <Text style={styles.panelTitle} accessibilityRole="header">
                메뉴
              </Text>
            </View>
            <Pressable
              accessibilityRole="button"
              accessibilityLabel="메뉴 닫기"
              hitSlop={8}
              onPress={onClose}
              style={({ pressed }) => [styles.closeButton, pressed && styles.pressed]}
            >
              <Text style={styles.closeText}>✕</Text>
            </Pressable>
          </View>

          <ScrollView contentContainerStyle={styles.list}>
            {menu == null ? (
              <ActivityIndicator color={colors.accent} style={styles.loader} />
            ) : (
              menu.sections.map((section) => (
                <View key={section.key} style={styles.section}>
                  {section.name ? <Text style={styles.sectionTitle}>{section.name}</Text> : null}
                  {section.items.length === 0 ? (
                    <Text style={styles.emptyText}>표시할 메뉴가 없습니다.</Text>
                  ) : (
                    section.items.map((item) => {
                      const current = isCurrentMenu(item, currentUrl);
                      const ready = item.target.kind !== "placeholder";
                      return (
                        <Pressable
                          key={item.key}
                          accessibilityRole="button"
                          accessibilityState={{ selected: current }}
                          accessibilityHint={ready ? undefined : "모바일 준비 중 안내 화면으로 이동"}
                          onPress={() => select(item)}
                          style={({ pressed }) => [styles.item, current && styles.itemCurrent, pressed && styles.pressed]}
                        >
                          <View style={[styles.itemBar, current && styles.itemBarCurrent]} />
                          <Text
                            style={[styles.itemText, !ready && styles.itemTextPending, current && styles.itemTextCurrent]}
                            numberOfLines={1}
                          >
                            {item.name}
                          </Text>
                          {!ready ? <Text style={styles.pendingBadge}>준비 중</Text> : null}
                        </Pressable>
                      );
                    })
                  )}
                </View>
              ))
            )}
            {menu?.source === "fallback" ? (
              <Text style={styles.noteText}>서버 메뉴를 불러오지 못해 기본 메뉴를 보여줍니다.</Text>
            ) : null}
          </ScrollView>
        </Animated.View>
      </View>
    </Modal>
  );
}

/** 헤더 왼쪽 ☰ 버튼 — 누르면 메뉴 드로어를 연다 (탭 화면·메뉴 화면 공통) */
export function AppMenuButton({ currentUrl }: { currentUrl?: string }) {
  const [open, setOpen] = useState(false);
  return (
    <>
      <Pressable
        accessibilityRole="button"
        accessibilityLabel="메뉴 열기"
        hitSlop={8}
        onPress={() => setOpen(true)}
        style={({ pressed }) => [styles.menuButton, pressed && styles.pressed]}
      >
        <Text style={styles.menuButtonText}>☰</Text>
      </Pressable>
      <AppMenuDrawer visible={open} onClose={() => setOpen(false)} currentUrl={currentUrl} />
    </>
  );
}

/** 메뉴로 여는 스택 화면(저널 스레드·저널 결산·준비 중)의 헤더 — ‹ 뒤로 + ☰ + 제목 */
export function MenuScreenHeader({ title, currentUrl }: { title: string; currentUrl?: string }) {
  const navigation = useNavigation();
  return (
    <View style={styles.header}>
      {navigation.canGoBack() ? (
        <Pressable
          accessibilityRole="button"
          accessibilityLabel="뒤로 가기"
          hitSlop={8}
          onPress={() => navigation.goBack()}
          style={styles.backButton}
        >
          <Text style={styles.backArrow}>‹</Text>
        </Pressable>
      ) : null}
      <AppMenuButton currentUrl={currentUrl} />
      <Text style={styles.headerTitle} numberOfLines={1} accessibilityRole="header">
        {title}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1 },
  backdrop: { position: "absolute", top: 0, right: 0, bottom: 0, left: 0, backgroundColor: "#000" },
  panel: {
    position: "absolute",
    top: 0,
    bottom: 0,
    left: 0,
    backgroundColor: colors.background,
    borderRightWidth: 1,
    borderRightColor: colors.border,
    paddingHorizontal: 12,
    elevation: 8,
    shadowColor: "#000",
    shadowOpacity: 0.18,
    shadowRadius: 12,
    shadowOffset: { width: 2, height: 0 }
  },
  panelHeader: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 8,
    paddingBottom: 12,
    borderBottomWidth: 1,
    borderBottomColor: colors.border
  },
  panelHeaderText: { flex: 1, gap: 2 },
  kicker: {
    color: colors.accent,
    fontSize: 11,
    fontWeight: "700",
    letterSpacing: 1.5,
    textTransform: "uppercase"
  },
  panelTitle: { color: colors.text, fontSize: 20, fontWeight: "800" },
  closeButton: { width: 32, height: 32, alignItems: "center", justifyContent: "center", borderRadius: 16 },
  closeText: { color: colors.secondaryText, fontSize: 16, fontWeight: "700" },
  list: { paddingVertical: 12, gap: 16 },
  loader: { marginTop: 24 },
  section: { gap: 2 },
  sectionTitle: {
    color: colors.muted,
    fontSize: 12,
    fontWeight: "800",
    letterSpacing: 0.3,
    paddingHorizontal: 8,
    paddingBottom: 4
  },
  item: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    paddingVertical: 11,
    paddingRight: 10,
    borderRadius: 10
  },
  itemCurrent: { backgroundColor: colors.surface },
  itemBar: { width: 3, alignSelf: "stretch", borderRadius: 2, marginLeft: 4, backgroundColor: "transparent" },
  itemBarCurrent: { backgroundColor: colors.accent },
  itemText: { flex: 1, color: colors.text, fontSize: 15, fontWeight: "500" },
  itemTextPending: { color: colors.secondaryText },
  itemTextCurrent: { color: colors.accent, fontWeight: "800" },
  pendingBadge: {
    color: colors.muted,
    fontSize: 10.5,
    fontWeight: "600",
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 8,
    paddingHorizontal: 6,
    paddingVertical: 1,
    overflow: "hidden"
  },
  emptyText: { color: colors.muted, fontSize: 12.5, paddingHorizontal: 17 },
  noteText: { color: colors.muted, fontSize: 11.5, paddingHorizontal: 8 },
  pressed: { opacity: 0.7 },
  menuButton: {
    width: 34,
    height: 34,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: colors.border,
    backgroundColor: colors.surface,
    alignItems: "center",
    justifyContent: "center"
  },
  menuButtonText: { color: colors.text, fontSize: 17, lineHeight: 20 },
  header: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    paddingHorizontal: 16,
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: colors.border,
    backgroundColor: colors.background
  },
  backButton: { paddingRight: 2 },
  backArrow: { fontSize: 28, color: colors.accent, lineHeight: 32, fontWeight: "300" },
  headerTitle: { flex: 1, fontSize: 16, fontWeight: "700", color: colors.text }
});
