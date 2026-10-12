export const colors = {
  background: "#F7F2EA",
  surface: "#EEE5D8",
  input: "#FFFDF9",
  border: "#D7C9B8",
  text: "#201B16",
  secondaryText: "#6D6256",
  muted: "#9A8F83",
  accent: "#6E4C2F",
  onAccent: "#FFF8EF",
  onDark: "#FFF8EF",
  /** 위험·임박 강조 빨강 (오늘 기한 등). background(#F7F2EA) 대비 약 5.9:1 (WCAG AA) */
  danger: "#B42318"
} as const;

/**
 * 로그인 화면 전용 팔레트 — 웹 로그인(frontend-vue SignIn.vue, Metronic 8.2.1 라이트 테마) 색을 옮긴 값.
 * Metronic 소스가 저장소에 없어 8.2.1 기본 변수값 기준으로 맞췄다.
 */
export const authColors = {
  /** 배경 이미지 로드 전/여백용 — dreamdiary.jpg 평균 색 */
  backdrop: "#1D3E5E",
  /** .text-secondary (gray-300) */
  textSecondary: "#DBDFE9",
  /** .form-control */
  inputBackground: "#FFFFFF",
  inputBorder: "#DBDFE9",
  inputText: "#4B5675",
  placeholder: "#99A1B7",
  /** .btn-light-primary (평상시 primary-light 배경 + primary 글자, 눌림 시 반전) */
  primary: "#1B84FF",
  primaryLight: "#E9F3FF",
  onPrimary: "#FFFFFF",
  /** .text-danger */
  danger: "#F8285A"
} as const;
