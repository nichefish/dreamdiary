/** Keep 서버 푸시의 네이티브 권한·설치 식별자·채널과 인증 소유자 등록을 연결한다. */
import { Platform } from "react-native";
import * as Notifications from "expo-notifications";
import * as SecureStore from "expo-secure-store";
import * as Crypto from "expo-crypto";
import { registerKeepReminderDevice, unregisterKeepReminderDevice } from "../api/dreamDiaryApi";
import { KeepPushError, KeepPushRegistration, type KeepNativeToken } from "./keepPushRegistration";

export const KEEP_REMINDER_CHANNEL_ID = "keep-reminder";
const DEVICE_ID_KEY = "keep-reminder-install-id";
const PROJECT_ID = process.env.EXPO_PUBLIC_EXPO_PROJECT_ID;
const UUID = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/;
let asked = false;
let handlerSet = false;

/** 앱 전경에서도 알림 배너를 표시한다. 원본 Keep 내용과 상태는 알림 처리로 변경하지 않는다. */
export function configureKeepNotifications(): void {
  if (handlerSet) return;
  handlerSet = true;
  Notifications.setNotificationHandler({
    handleNotification: async () => ({ shouldShowBanner: true, shouldShowList: true, shouldPlaySound: false, shouldSetBadge: false })
  });
}

/** 단일 서버 경로의 예약 정합: 설치에 남아 있는 keep-due 예약만 취소하고 다른 예약은 보존한다. */
export async function clearKeepLocalSchedules(): Promise<void> {
  const scheduled = await Notifications.getAllScheduledNotificationsAsync();
  for (const request of scheduled) {
    if (request.content.data?.kind === "keep-due") await Notifications.cancelScheduledNotificationAsync(request.identifier);
  }
}

/** 설치 UUID는 SecureStore에 보존한다. 저장값 손상은 새 UUID로 감추지 않고 오류로 드러낸다. */
async function deviceId(create: boolean): Promise<string | null> {
  const id = await SecureStore.getItemAsync(DEVICE_ID_KEY);
  if (id) {
    if (!UUID.test(id)) throw new KeepPushError("알림 기기 식별자를 읽을 수 없습니다.");
    return id;
  }
  if (!create) return null;
  const created = Crypto.randomUUID();
  await SecureStore.setItemAsync(DEVICE_ID_KEY, created);
  return created;
}

/** Expo 토큰 조회의 네트워크 대기를 제한한다. 토큰·네이티브 오류 원문은 노출하지 않는다. */
async function expoToken(projectId: string, hint?: KeepNativeToken): Promise<string> {
  let timer: ReturnType<typeof setTimeout> | undefined;
  try {
    const result = await Promise.race([
      Notifications.getExpoPushTokenAsync({ projectId, ...(hint ? { devicePushToken: hint } : {}) }),
      new Promise<never>((_, reject) => { timer = setTimeout(() => reject(new KeepPushError("푸시 토큰 응답을 받지 못했습니다. 연결을 확인한 뒤 다시 시도하세요.")), 20_000); })
    ]);
    return result.data;
  } catch (error) {
    if (error instanceof KeepPushError) throw error;
    throw new KeepPushError("푸시 토큰을 받지 못했습니다. 네트워크와 Android FCM·Expo 설정을 확인하세요.");
  } finally { clearTimeout(timer); }
}

export const keepPushRegistration = new KeepPushRegistration({
  cleanup: clearKeepLocalSchedules,
  prepareChannel: async () => {
    if (Platform.OS === "android") await Notifications.setNotificationChannelAsync(KEEP_REMINDER_CHANNEL_ID, {
      name: "Keep 리마인드", importance: Notifications.AndroidImportance.DEFAULT
    });
  },
  permission: async () => {
    const current = await Notifications.getPermissionsAsync();
    if (current.granted) return true;
    if (asked || current.status !== "undetermined" || !current.canAskAgain) return false;
    asked = true;
    return (await Notifications.requestPermissionsAsync()).granted;
  },
  projectId: () => {
    if (!PROJECT_ID || !UUID.test(PROJECT_ID)) throw new KeepPushError("푸시 프로젝트 설정이 필요합니다. Expo projectId와 Android FCM 설정을 준비한 앱을 설치하세요.");
    return PROJECT_ID;
  },
  token: expoToken,
  deviceId,
  timeZone: () => {
    const zone = Intl.DateTimeFormat().resolvedOptions().timeZone;
    if (!zone) throw new KeepPushError("휴대폰 시간대를 확인하지 못했습니다.");
    return zone;
  },
  register: registerKeepReminderDevice,
  unregister: unregisterKeepReminderDevice
});