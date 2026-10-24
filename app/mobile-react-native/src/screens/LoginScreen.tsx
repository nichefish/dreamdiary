import { useRef, useState } from "react";
import {
  ActivityIndicator,
  ImageBackground,
  KeyboardAvoidingView,
  Pressable,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  TextInput,
  View
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useAuth } from "../context/AuthContext";
import { getApiBaseUrlDevHint } from "../config/env";
import { authColors } from "../theme/colors";

/**
 * 웹 로그인(frontend-vue AuthLayout + SignIn.vue) 배경 이미지.
 * 원본 public/dreamdiary/img/dreamdiary.jpg(1792x1024) 의 가운데 640x1024 세로 크롭(성·달이 있는 중앙 기둥).
 */
const loginBackground = require("../../assets/images/login-bg.jpg");

/** 웹 SignIn.vue 의 도메인 표시와 동일한 상수 (인증 전 화면이므로 API 대신 상수 사용) */
const DOMAIN = "dreamdiary.nicheblog.io";

/** 웹 messages_ko.properties auth.login.* 문구 */
const TEXT = {
  username: "아이디",
  usernamePlaceholder: "아이디를 입력하세요.",
  password: "비밀번호",
  passwordPlaceholder: "비밀번호를 입력하세요.",
  signIn: "로그인",
  required: "필수 입력 항목입니다.",
  failure: "로그인에 실패했습니다.",
  copyright: "2024. nichefish. All rights reserved."
} as const;

type FieldErrors = { username?: string; password?: string };

export function LoginScreen() {
  const { login } = useAuth();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
  const passwordInputRef = useRef<TextInput>(null);
  const apiDevHint = getApiBaseUrlDevHint();

  /** 웹과 동일: 아이디·비밀번호가 비어 있으면 필드 아래 안내만 표시하고 로그인 API 를 호출하지 않는다. */
  function validateLoginForm(): boolean {
    const next: FieldErrors = {};
    if (!username.trim()) next.username = TEXT.required;
    if (!password) next.password = TEXT.required;
    setFieldErrors(next);
    return !next.username && !next.password;
  }

  async function handleLogin() {
    if (isSubmitting) return;
    setError(null);
    if (!validateLoginForm()) return;
    setIsSubmitting(true);
    try {
      await login(username.trim(), password);
      // 성공 시 AuthContext isAuthenticated 변경 → AppNavigator가 Home으로 전환
    } catch (e) {
      setError(e instanceof Error ? e.message : TEXT.failure);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <ImageBackground
      source={loginBackground}
      resizeMode="cover"
      style={styles.background}
    >
      {/* 웹 AuthLayout 과 같이 로그인 화면은 어두운 야경 배경 위에 표시 → 밝은 상태바 아이콘 */}
      <StatusBar barStyle="light-content" />
      <SafeAreaView style={styles.safeArea}>
        <KeyboardAvoidingView
          // edge-to-edge Android 는 adjustResize 로 창이 줄지 않으므로 padding 으로 키보드 높이만큼 비운다.
          behavior="padding"
          style={styles.keyboardArea}
        >
          <ScrollView
            contentContainerStyle={styles.scrollContent}
            keyboardShouldPersistTaps="handled"
            showsVerticalScrollIndicator={false}
          >
            <View style={styles.panel}>
              {/* 도메인 표시 (웹: bi-cloud-moon · 도메인 · bi-stars) */}
              <View style={styles.domainRow}>
                <Text style={styles.domainIcon}>☾</Text>
                <Text style={styles.domainText}>{DOMAIN}</Text>
                <Text style={styles.domainIcon}>✧</Text>
              </View>

              {/* 아이디 */}
              <View style={styles.fieldGroup}>
                <Text style={styles.label}>{TEXT.username}</Text>
                <TextInput
                  autoCapitalize="none"
                  autoComplete="username"
                  autoCorrect={false}
                  maxLength={20}
                  onChangeText={(value) => {
                    setUsername(value);
                    if (fieldErrors.username) setFieldErrors((prev) => ({ ...prev, username: undefined }));
                  }}
                  onSubmitEditing={() => passwordInputRef.current?.focus()}
                  placeholder={TEXT.usernamePlaceholder}
                  placeholderTextColor={authColors.placeholder}
                  returnKeyType="next"
                  style={styles.input}
                  submitBehavior="submit"
                  value={username}
                />
              </View>
              <Text style={styles.fieldError}>{fieldErrors.username ?? ""}</Text>

              {/* 비밀번호 */}
              <View style={styles.fieldGroup}>
                <Text style={styles.label}>{TEXT.password}</Text>
                <TextInput
                  ref={passwordInputRef}
                  autoComplete="password"
                  maxLength={20}
                  onChangeText={(value) => {
                    setPassword(value);
                    if (fieldErrors.password) setFieldErrors((prev) => ({ ...prev, password: undefined }));
                  }}
                  onSubmitEditing={() => { void handleLogin(); }}
                  placeholder={TEXT.passwordPlaceholder}
                  placeholderTextColor={authColors.placeholder}
                  returnKeyType="done"
                  secureTextEntry
                  style={styles.input}
                  value={password}
                />
              </View>
              <Text style={[styles.fieldError, styles.fieldErrorLast]}>{fieldErrors.password ?? ""}</Text>

              {apiDevHint != null && (
                <View style={styles.devHintBox}>
                  <Text style={styles.devHintText}>{apiDevHint}</Text>
                </View>
              )}
              {error != null && (
                <Text style={styles.errorText}>{error}</Text>
              )}

              {/* 로그인 버튼 (웹: btn-lg btn-light-primary opacity-75 + 로딩 스피너) */}
              <Pressable
                accessibilityRole="button"
                accessibilityState={{ busy: isSubmitting, disabled: isSubmitting }}
                disabled={isSubmitting}
                onPress={() => { void handleLogin(); }}
                style={({ pressed }) => [styles.loginButton, pressed && styles.loginButtonPressed]}
              >
                {({ pressed }) => (
                  <View style={styles.loginButtonInner}>
                    <Text style={[styles.loginButtonText, pressed && styles.loginButtonTextPressed]}>
                      {TEXT.signIn}
                    </Text>
                    {isSubmitting && (
                      <ActivityIndicator
                        color={pressed ? authColors.onPrimary : authColors.primary}
                        size="small"
                      />
                    )}
                  </View>
                )}
              </Pressable>
            </View>

            <Text style={styles.copyright}>{TEXT.copyright}</Text>
          </ScrollView>
        </KeyboardAvoidingView>
      </SafeAreaView>
    </ImageBackground>
  );
}

/** 어두운 사진 위 밝은 글자 가독성용 그림자 (웹에는 없음, 모바일 소형 화면 보정) */
const textShadow = {
  textShadowColor: "rgba(0, 0, 0, 0.45)",
  textShadowOffset: { width: 0, height: 1 },
  textShadowRadius: 3
} as const;

const styles = StyleSheet.create({
  // 웹 AuthLayout: background-size cover, position center → 세로 화면에서도 가운데(성·달) 기준으로 잘림
  background: {
    flex: 1,
    backgroundColor: authColors.backdrop
  },
  safeArea: {
    flex: 1
  },
  keyboardArea: {
    flex: 1
  },
  scrollContent: {
    flexGrow: 1,
    paddingHorizontal: 30,
    paddingBottom: 16
  },
  // 웹: w-lg-500px mt-20 p-10 (상단 정렬 패널, 배경 없음)
  panel: {
    width: "84%",
    maxWidth: 320,
    alignSelf: "center",
    marginTop: 56
  },
  // 웹: d-flex justify-content-center fs-4 mb-15 text-secondary fw-bold ls-1
  domainRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    marginBottom: 28
  },
  domainIcon: {
    color: authColors.textSecondary,
    fontSize: 20,
    ...textShadow
  },
  domainText: {
    color: authColors.textSecondary,
    fontSize: 16,
    fontWeight: "600",
    letterSpacing: 1.3,
    paddingHorizontal: 8,
    ...textShadow
  },
  // 웹: opacity-75 래퍼 안의 label + form-control-lg
  fieldGroup: {
    opacity: 0.75
  },
  label: {
    color: authColors.textSecondary,
    fontSize: 14,
    fontWeight: "700",
    marginBottom: 6,
    ...textShadow
  },
  input: {
    backgroundColor: authColors.inputBackground,
    borderColor: authColors.inputBorder,
    borderRadius: 8,
    borderWidth: 1,
    color: authColors.inputText,
    fontSize: 15,
    paddingHorizontal: 14,
    paddingVertical: 8
  },
  // 웹: h-15px mt-1 mb-5 text-danger (오류 문구 자리 고정)
  fieldError: {
    color: authColors.danger,
    fontSize: 12,
    lineHeight: 15,
    minHeight: 15,
    marginTop: 4,
    marginBottom: 8,
    ...textShadow
  },
  fieldErrorLast: {
    marginBottom: 12
  },
  devHintBox: {
    backgroundColor: "rgba(255, 248, 225, 0.85)",
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 10,
    marginBottom: 8
  },
  devHintText: { color: "#7D6608", fontSize: 13, lineHeight: 18 },
  // 웹: #errorMsgSpan.text-danger
  errorText: {
    color: authColors.danger,
    fontSize: 14,
    lineHeight: 20,
    ...textShadow
  },
  // 웹: mt-12 btn btn-lg btn-light-primary opacity-75 w-100
  loginButton: {
    alignItems: "center",
    backgroundColor: authColors.primaryLight,
    borderRadius: 8,
    justifyContent: "center",
    marginTop: 16,
    minHeight: 48,
    opacity: 0.75,
    paddingHorizontal: 22,
    paddingVertical: 12
  },
  // 웹 hover/active: 배경 primary, 글자 흰색
  loginButtonPressed: {
    backgroundColor: authColors.primary
  },
  loginButtonInner: {
    alignItems: "center",
    flexDirection: "row",
    gap: 8,
    justifyContent: "center"
  },
  loginButtonText: {
    color: authColors.primary,
    fontSize: 15,
    fontWeight: "600"
  },
  loginButtonTextPressed: {
    color: authColors.onPrimary
  },
  // 웹: text-end text-secondary fs-8
  copyright: {
    color: authColors.textSecondary,
    fontSize: 11,
    marginTop: "auto",
    paddingTop: 24,
    textAlign: "right",
    width: "100%",
    maxWidth: 500,
    alignSelf: "center",
    ...textShadow
  }
});
