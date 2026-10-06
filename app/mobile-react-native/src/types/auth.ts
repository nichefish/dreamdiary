/** 백엔드 AuthUserDto와 대응 */
export interface AuthUser {
  username: string;
  nickname: string;
  email: string;
  profileImageUrl?: string;
  roles: { roleKey: string }[];
  isMngr: boolean;
  isDev: boolean;
}

/**
 * 백엔드 AjaxResponse 공통 래퍼.
 * T = rsltObj 타입, L = rsltList 항목 타입. 실패는 HTTP 200 + rslt=false + message 로 올 수 있다.
 */
export interface AjaxResponse<T = unknown, L = unknown> {
  rslt: boolean;
  message?: string;
  rsltObj?: T;
  rsltList?: L[] | null;
  rsltMap?: Record<string, unknown> | null;
}
