import { useEffect, useMemo, useRef, useState, type ReactNode } from "react";
import { Animated, PanResponder, Pressable, StyleSheet, Text, View } from "react-native";

const ACTION_WIDTH = 88;

/** 가로 드래그 완료 버튼. 세로 이동은 목록 스크롤에 맡기고 쓰기는 버튼을 눌렀을 때만 실행한다. */
export function KeepSwipeRow({ children, enabled, disabled, onComplete }: {
  children: ReactNode;
  enabled: boolean;
  disabled: boolean;
  onComplete: () => void;
}) {
  const translation = useRef(new Animated.Value(0)).current;
  const opened = useRef(false);
  const [revealed, setRevealed] = useState(false);
  const startOffset = useRef(0);
  const settle = (open: boolean) => {
    opened.current = open;
    setRevealed(open);
    Animated.timing(translation, { toValue: open ? -ACTION_WIDTH : 0, duration: 150, useNativeDriver: true }).start();
  };
  useEffect(() => {
    if (!enabled || disabled) {
      opened.current = false;
      setRevealed(false);
      translation.setValue(0);
    }
  }, [enabled, disabled, translation]);
  const responder = useMemo(() => PanResponder.create({
    onMoveShouldSetPanResponder: (_, gesture) => enabled && !disabled
      && Math.abs(gesture.dx) > 12 && Math.abs(gesture.dx) > Math.abs(gesture.dy) * 1.5
      && (gesture.dx < 0 || opened.current),
    onPanResponderGrant: () => {
      startOffset.current = opened.current ? -ACTION_WIDTH : 0;
      translation.stopAnimation();
    },
    onPanResponderMove: (_, gesture) => {
      translation.setValue(Math.max(-ACTION_WIDTH, Math.min(0, startOffset.current + gesture.dx)));
    },
    onPanResponderRelease: (_, gesture) => {
      const open = startOffset.current + gesture.dx < -ACTION_WIDTH / 2;
      console.debug("[KeepSwipeRow] completion action reveal", { open });
      settle(open);
    },
    onPanResponderTerminate: () => settle(false)
  }), [enabled, disabled, translation]);

  return <View style={styles.container} {...responder.panHandlers}>
    {enabled ? <Pressable accessibilityRole="button" accessibilityLabel="Keep 완료"
      disabled={disabled || !revealed} accessibilityElementsHidden={!revealed} importantForAccessibility={revealed ? "auto" : "no-hide-descendants"} onPress={onComplete} style={styles.action}>
      <Text style={styles.actionText}>완료</Text>
    </Pressable> : null}
    <Animated.View style={{ transform: [{ translateX: translation }] }}
      accessibilityActions={enabled && !disabled ? [{ name: "complete", label: "완료" }] : []}
      onAccessibilityAction={(event) => {
        if (enabled && !disabled && event.nativeEvent.actionName === "complete") onComplete();
      }}>
      {children}
    </Animated.View>
  </View>;
}

const styles = StyleSheet.create({
  container: { overflow: "hidden", borderRadius: 10 },
  action: { position: "absolute", right: 0, top: 0, bottom: 0, width: ACTION_WIDTH,
    alignItems: "center", justifyContent: "center", backgroundColor: "#2F855A" },
  actionText: { color: "#FFFFFF", fontSize: 14, fontWeight: "700" }
});
