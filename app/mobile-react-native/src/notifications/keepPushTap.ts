import * as Notifications from "expo-notifications";
import { StackActions, type NavigationContainerRefWithCurrent } from "@react-navigation/native";
import type { RootStackParamList } from "../navigation/AppNavigator";

/** 인증 서버의 고정 날짜·슬롯을 최신 대상 목록 조회에 전달한다. */
export interface KeepReminderWindow { localDate: string; slot: "MORNING" | "EVENING" }

/** 알림 본문은 해석하지 않고 엄격한 ISO 날짜와 고정 슬롯을 검증한다. */
export function parseKeepReminder(data: Record<string, unknown> | undefined): KeepReminderWindow | null {
  if (data?.kind !== "keep-reminder" || typeof data.localDate !== "string"
      || !/^\d{4}-\d{2}-\d{2}$/.test(data.localDate) || (data.slot !== "MORNING" && data.slot !== "EVENING")) return null;
  const date = new Date(data.localDate + "T00:00:00.000Z");
  if (!Number.isFinite(date.getTime()) || date.toISOString().slice(0, 10) !== data.localDate) return null;
  return { localDate: data.localDate, slot: data.slot };
}

/** Keep 알림은 인증 화면 준비 후 새 Keep 목록으로 연다. 이전 화면의 편집과 필터를 보존한다. */
export function bindKeepNotificationTap(nav: NavigationContainerRefWithCurrent<RootStackParamList>): {
  onReady: () => void; flush: () => void; remove: () => void
} {
  let pending: { id: string; window: KeepReminderWindow } | null = null;
  let lastHandled: string | null = null;
  let removed = false;
  let liveRevision = 0;
  const flush = () => {
    if (removed || !pending || !nav.isReady() || !(nav.getRootState()?.routeNames ?? []).includes("KeepHome")) return;
    const next = pending;
    pending = null;
    lastHandled = next.id;
    nav.dispatch(StackActions.push("KeepHome", { reminder: next.window }));
    console.info("[KeepPush] reminder list opened", { slot: next.window.slot });
    void Notifications.clearLastNotificationResponseAsync().catch(() => {
      console.warn("[KeepPush] notification response clear failed");
    });
  };
  const handle = (response: Notifications.NotificationResponse | null) => {
    if (removed || !response) return;
    const request = response.notification.request;
    const window = parseKeepReminder(request.content.data);
    if (!window || request.identifier === lastHandled || request.identifier === pending?.id) return;
    pending = { id: request.identifier, window };
    console.info("[KeepPush] reminder tap queued", { slot: window.slot });
    flush();
  };
  const subscription = Notifications.addNotificationResponseReceivedListener((response) => { ++liveRevision; handle(response); });
  return {
    flush,
    onReady: () => {
      flush();
      const revision = liveRevision;
      void Notifications.getLastNotificationResponseAsync().then((response) => {
        if (revision === liveRevision) handle(response);
      }).catch(() => {
        console.warn("[KeepPush] initial notification response unavailable");
      });
    },
    remove: () => { removed = true; pending = null; subscription.remove(); }
  };
}