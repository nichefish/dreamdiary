/** Expo 프로젝트 식별자와 Firebase 클라이언트 설정 경로를 빌드 환경에서 제공한다. */
module.exports = ({ config }) => {
  const projectId = process.env.EXPO_PUBLIC_EXPO_PROJECT_ID;
  const googleServicesFile = process.env.GOOGLE_SERVICES_JSON_PATH;
  if (projectId && !/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/.test(projectId)) {
    throw new Error("EXPO_PUBLIC_EXPO_PROJECT_ID must be a canonical UUID");
  }
  return {
    ...config,
    ...(projectId ? { extra: { ...config.extra, eas: { ...config.extra?.eas, projectId } } } : {}),
    android: { ...config.android, ...(googleServicesFile ? { googleServicesFile } : {}) }
  };
};