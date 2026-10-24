import React, { createContext, useCallback, useContext, useEffect, useRef, useState } from "react";
import {
  getAuthInfo,
  login as apiLogin,
  logout as apiLogout,
  refreshAccessToken
} from "../api/dreamDiaryApi";
import { ApiError, ApiConnectionError, setUnauthorizedHandler } from "../api/client";
import {
  clearAccessToken,
  hydrateAccessTokenFromSecureStore
} from "../auth/accessToken";
import type { AuthUser } from "../types/auth";
import { AppState } from "react-native";
import * as Notifications from "expo-notifications";
import { keepPushRegistration } from "../notifications/keepPush";
import { IDLE_KEEP_PUSH, type KeepPushStatus } from "../notifications/keepPushRegistration";

interface AuthContextValue {
  user: AuthUser | null;
  isAuthenticated: boolean;
  /** 앱 시작 시 기존 세션·SecureStore 토큰 확인 중 */
  isLoading: boolean;
  authError: string | null;
  login: (username: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  retryAuth: () => Promise<void>;
  /** 푸시 준비 실패는 인증·Keep 사용과 별도로 표시한다. */
  pushStatus: KeepPushStatus;
  retryPush: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [authError, setAuthError] = useState<string | null>(null);
  const [pushStatus, setPushStatus] = useState<KeepPushStatus>(IDLE_KEEP_PUSH);
  const logoutBusy = useRef(false);
  const retryPush = useCallback(() => keepPushRegistration.sync(), []);

  /** 인증 소유자와 수신 상태를 연결하고 전경 복귀·네이티브 토큰 변경을 다시 반영한다. */
  useEffect(() => keepPushRegistration.subscribe(setPushStatus), []);
  useEffect(() => {
    keepPushRegistration.setOwner(user?.username ?? null);
    if (!user) return;
    void keepPushRegistration.sync();
    const foreground = AppState.addEventListener("change", (state) => {
      if (state === "active") void keepPushRegistration.sync();
    });
    let lastToken: string | null = null;
    const token = Notifications.addPushTokenListener((next) => {
      if ((next.type !== "android" && next.type !== "ios") || typeof next.data !== "string" || next.data === lastToken) return;
      lastToken = next.data;
      // 전달받은 네이티브 토큰으로 Expo 토큰을 조회해 토큰 리스너의 재귀 호출을 방지한다.
      void keepPushRegistration.sync({ type: next.type, data: next.data });
    });
    return () => { foreground.remove(); token.remove(); };
  }, [user?.username]);

  /**
   * 기존 세션(쿠키) 유효 여부 확인 — 앱 시작 시 1회 호출.
   * SecureStore 토큰 hydrate 후 refresh 로 JWT 갱신(WebSocket·REST Authorization).
   */
  const loadAuth = useCallback(async ({ showLoading = true }: { showLoading?: boolean } = {}): Promise<boolean> => {
    // showLoading=false: 로그인 직후 확인 — 전체 로딩 화면으로 LoginScreen 을 내리지 않아 실패 문구를 그 화면에 표시할 수 있다.
    if (showLoading) setIsLoading(true);
    setAuthError(null);
    try {
      await hydrateAccessTokenFromSecureStore();
      const data = await getAuthInfo();
      if (data.rslt && data.rsltObj) {
        try {
          await refreshAccessToken();
        } catch {
          // refresh 실패 시 REST 쿠키만 유지 — AI 채팅 WS 는 토큰 없으면 연결 실패 가능
          console.warn("[Auth] refreshAccessToken failed after getAuthInfo");
        }
        // 토큰 갱신 후 사용자 설정 → 메인 화면 첫 요청이 갱신된 토큰을 쓰도록 (로그인 직후엔 로딩 화면이 없으므로)
        setUser(data.rsltObj);
        return true;
      } else {
        setUser(null);
        await clearAccessToken();
        return false;
      }
    } catch (e) {
      setUser(null);
      if (e instanceof ApiError && e.status === 401) {
        // 비로그인(401)은 정상 흐름 — getAuthInfo 가 오류 로그 없이 던진다.
        await clearAccessToken();
        return false;
      }
      const status = e instanceof ApiError ? e.status : undefined;
      setAuthError(
        e instanceof ApiConnectionError ? e.message : status === 403
          ? "현재 계정으로는 인증 상태를 확인할 권한이 없습니다."
          : "인증 상태를 확인하는 중 오류가 발생했습니다."
      );
      return false;
    } finally {
      if (showLoading) setIsLoading(false);
    }
  }, []);

  const checkAuth = useCallback(async () => {
    await loadAuth();
  }, [loadAuth]);

  useEffect(() => {
    void checkAuth();
  }, [checkAuth]);

  // 401 응답 시 client.ts 가 이 핸들러를 호출 → 자동 로그아웃
  useEffect(() => {
    return setUnauthorizedHandler(() => {
      keepPushRegistration.setOwner(null);
      void clearAccessToken();
      setUser(null);
      setAuthError(null);
    });
  }, []);

  const login = useCallback(
    async (username: string, password: string) => {
      const data = await apiLogin(username, password);
      if (!data.rslt) throw new Error(data.message ?? "로그인에 실패했습니다.");
      // 로그인 성공 → 서버가 JWT 쿠키·Authorization 헤더 발급 → 인증 상태 갱신
      const authenticated = await loadAuth({ showLoading: false });
      if (!authenticated) {
        // 로그인 직후 인증정보 401 은 실제 실패 — 시작 시 401 과 달리 화면에 표시한다.
        console.warn("[Auth] login succeeded but get-auth-info did not return a user");
        throw new Error("로그인 후 인증 정보를 확인하지 못했습니다. 다시 시도해 주세요.");
      }
    },
    [loadAuth]
  );

  /** 푸시 해제 확인 전에는 인증을 보존한다. 확인 후 서버 로그아웃과 로컬 인증 정리를 수행한다. */
  const logout = useCallback(async () => {
    if (logoutBusy.current) return;
    logoutBusy.current = true;
    try {
      await keepPushRegistration.detachForLogout();
      try {
        await apiLogout();
      } finally {
        await clearAccessToken();
        keepPushRegistration.setOwner(null);
        setUser(null);
      }
    } finally {
      logoutBusy.current = false;
    }
  }, []);

  return (
    <AuthContext.Provider
      value={{ user, isAuthenticated: user !== null, isLoading, authError, login, logout, retryAuth: checkAuth, pushStatus, retryPush }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
