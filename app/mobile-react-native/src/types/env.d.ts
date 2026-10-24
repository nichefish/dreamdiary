declare namespace NodeJS {
  type ProcessEnv = {
    EXPO_PUBLIC_API_BASE_URL?: string;
    EXPO_PUBLIC_EXPO_PROJECT_ID?: string;
  };
}

declare const process: {
  env: NodeJS.ProcessEnv;
};

