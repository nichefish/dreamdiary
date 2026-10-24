import { useEffect, useRef } from "react";
import { NavigationContainer, createNavigationContainerRef } from "@react-navigation/native";
import { StatusBar } from "react-native";
import { SafeAreaProvider } from "react-native-safe-area-context";
import { AuthProvider } from "./src/context/AuthContext";
import { AppNavigator, type RootStackParamList } from "./src/navigation/AppNavigator";
import { configureKeepNotifications, clearKeepLocalSchedules } from "./src/notifications/keepPush";
import { bindKeepNotificationTap } from "./src/notifications/keepPushTap";

const navigationRef = createNavigationContainerRef<RootStackParamList>();
configureKeepNotifications();

export default function App() {
  /** Keep 기한 알림은 인증 화면 준비 후 Keep 전수관리의 최신 대상 목록으로 이동한다. */
  const tapRef = useRef<ReturnType<typeof bindKeepNotificationTap> | null>(null);
  useEffect(() => {
    try {
      tapRef.current = bindKeepNotificationTap(navigationRef);
      if (navigationRef.isReady()) tapRef.current.onReady();
    } catch {
      console.warn("[KeepPush] response listener unavailable");
    }
    void clearKeepLocalSchedules().catch(() => console.warn("[KeepPush] local schedule cleanup failed"));
    return () => { tapRef.current?.remove(); tapRef.current = null; };
  }, []);

  return (
    <SafeAreaProvider>
      <AuthProvider>
        <NavigationContainer ref={navigationRef} onReady={() => tapRef.current?.onReady()} onStateChange={() => tapRef.current?.flush()}>
          <StatusBar barStyle="dark-content" />
          <AppNavigator />
        </NavigationContainer>
      </AuthProvider>
    </SafeAreaProvider>
  );
}
