import { API_BASE_URL } from "../config/env";
import {
  captureAccessTokenFromResponse,
  clearAccessToken,
  getAccessToken
} from "../auth/accessToken";

/**
 * 백엔드가 웹/모바일 세션을 구분하기 위한 클라이언트 채널 헤더.
 * 모든 REST 요청에 강제로 붙인다. (향후 WebSocket/STOMP 연결 시 connect headers 에도 이 상수를 사용)
 */
export const CLIENT_CHANNEL_HEADER = "X-Client-Channel";
export const CLIENT_CHANNEL = "MOBILE";
export const CLIENT_CHANNEL_HEADERS = { [CLIENT_CHANNEL_HEADER]: CLIENT_CHANNEL } as const;

// string[]/number[] 를 포함하도록 QueryValue 타입 확장
type QueryValue = string | number | boolean | undefined | null | string[] | number[];

type RequestOptions = RequestInit & {
  query?: Record<string, QueryValue>;
  /** login/refresh 응답 Authorization 헤더에서 access JWT 저장 (WebSocket용) */
  captureAccessToken?: boolean;
  /**
   * 호출 측이 정상 흐름으로 처리하는 오류 상태 코드 (예: 로그인 전 인증정보 조회의 401).
   * 해당 상태는 console.error(개발 빌드 LogBox 오류 토스트) 대신 console.info 로만 남긴다. 예외는 그대로 던진다.
   */
  expectedErrorStatuses?: readonly number[];
};

export class ApiError extends Error {
  constructor(
    message: string,
    readonly status: number,
    readonly body?: unknown
  ) {
    super(message);
    this.name = "ApiError";
  }
}

/** 전체 HTTP 응답과 본문을 기다리는 최대 시간. 자동 재전송 없이 호출자에게 오류를 전달한다. */
export const REQUEST_TIMEOUT_MS = 15_000;

/** 연결 불가·시간 초과는 인증 실패와 구분하고 저장 요청의 결과 미확인을 안내한다. */
export class ApiConnectionError extends ApiError {
  constructor(readonly reason: "timeout" | "network", writeRequest = false) {
    const message = reason === "timeout"
      ? "서버 응답을 15초 동안 받지 못했습니다. 네트워크와 휴대폰·PC의 Tailscale 연결을 확인하세요."
      : "서버에 연결할 수 없습니다. 네트워크와 휴대폰·PC의 Tailscale 연결을 확인하세요.";
    super(message + (writeRequest ? " 저장 요청이 처리됐을 수 있으므로 목록을 확인한 뒤 다시 시도하세요." : ""), 0);
    this.name = "ApiConnectionError";
  }
}
/**
 * 인증 만료·비로그인(401) 오류 여부.
 * 401 은 client 가 자동 로그아웃으로 처리하므로 호출 측은 오류 로그·토스트를 생략한다.
 * @param e catch 로 받은 예외
 */
export function isUnauthorizedError(e: unknown): boolean {
  return e instanceof ApiError && e.status === 401;
}

// ─── 401 전역 핸들러 ──────────────────────────────────────
// 세션 만료(401) 시 AuthContext 가 등록한 콜백을 호출해 자동 로그아웃.
// 순환 의존성 없이 연결하기 위해 모듈 레벨 콜백 패턴 사용.

let _onUnauthorized: (() => void) | null = null;

/** AuthContext 가 마운트 시 등록, 언마운트 시 해제 */
export function setUnauthorizedHandler(handler: () => void): () => void {
  _onUnauthorized = handler;
  return () => { _onUnauthorized = null; };
}

function buildUrl(path: string, query?: RequestOptions["query"]) {
  const url = new URL(path, API_BASE_URL);

  Object.entries(query ?? {}).forEach(([key, value]) => {
    if (value === undefined || value === null) return;
    if (Array.isArray(value)) {
      // Spring MVC @ModelAttribute List<String> 바인딩 — 인덱스 형태 사용
      value.forEach((v, i) => url.searchParams.set(`${key}[${i}]`, String(v)));
    } else {
      url.searchParams.set(key, String(value));
    }
  });

  return url.toString();
}

async function parseResponse(response: Response) {
  const contentType = response.headers.get("content-type") ?? "";

  if (contentType.includes("application/json")) {
    return response.json();
  }

  const text = await response.text();
  return text.length > 0 ? text : null;
}

/** 구조화된 API 오류 응답에서 사용자에게 표시할 서버 메시지만 추출한다. */
function getResponseErrorMessage(responseBody: unknown, status: number): string {
  if (responseBody && typeof responseBody === "object" && !Array.isArray(responseBody)) {
    const message = (responseBody as { message?: unknown }).message;
    if (typeof message === "string" && message.trim().length > 0) return message;
  }
  return `요청 처리 중 오류가 발생했습니다. (${status})`;
}

export async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { query, headers, body, captureAccessToken, expectedErrorStatuses, signal, ...init } = options;
  // FormData 전송 시 Content-Type 을 직접 지정하지 않는다.
  // fetch 가 multipart/form-data + boundary 를 자동으로 설정한다.
  const isFormData = typeof FormData !== "undefined" && body instanceof FormData;
  const token = getAccessToken();

  const controller = new AbortController();
  const writeRequest = !["GET", "HEAD"].includes((init.method ?? "GET").toUpperCase());
  let timedOut = false;
  let timer: ReturnType<typeof setTimeout> | undefined;
  const cancel = () => controller.abort();
  signal?.addEventListener("abort", cancel, { once: true });
  if (signal?.aborted) cancel();
  const deadline = new Promise<never>((_, reject) => {
    timer = setTimeout(() => {
      timedOut = true;
      controller.abort();
      reject(new ApiConnectionError("timeout", writeRequest));
    }, REQUEST_TIMEOUT_MS);
  });
  try {
    const operation = async () => {
      const response = await fetch(buildUrl(path, query), {
        credentials: "include",
        ...init,
        signal: controller.signal,
        body,
        headers: {
          Accept: "application/json",
          ...(!isFormData && { "Content-Type": "application/json" }),
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
          ...headers,
          // 호출 측 headers 로 덮어쓰지 못하도록 마지막에 둔다.
          ...CLIENT_CHANNEL_HEADERS
        }
      });

      if (timedOut) throw new ApiConnectionError("timeout", writeRequest);
      if (captureAccessToken && response.ok) {
        void captureAccessTokenFromResponse(response);
      }

      const responseBody = await parseResponse(response);
      if (timedOut) throw new ApiConnectionError("timeout", writeRequest);

      if (!response.ok) {
        if (response.status === 401) {
          void clearAccessToken();
          _onUnauthorized?.();
        }
        // 401 은 전역 핸들러(자동 로그아웃)가 처리하는 정상 흐름이므로 오류 토스트를 띄우지 않는다.
        if (response.status === 401 || expectedErrorStatuses?.includes(response.status)) {
          console.info("[API] Expected error response", { path, status: response.status });
        } else {
          console.error("[API] Request failed", { path, status: response.status, responseBody });
        }
        throw new ApiError(getResponseErrorMessage(responseBody, response.status), response.status, responseBody);
      }

      return responseBody as T;
    };
    return await Promise.race([operation(), deadline]);
  } catch (error) {
    if (timedOut || error instanceof ApiConnectionError) {
      console.warn("[API] request timed out", { path, method: init.method ?? "GET" });
      throw error instanceof ApiConnectionError ? error : new ApiConnectionError("timeout", writeRequest);
    }
    if (!signal?.aborted && error instanceof TypeError) {
      console.warn("[API] connection unavailable", { path, method: init.method ?? "GET" });
      throw new ApiConnectionError("network", writeRequest);
    }
    throw error;
  } finally {
    clearTimeout(timer);
    signal?.removeEventListener("abort", cancel);
  }
}
