# DreamDiary Mobile

React Native / Expo based mobile starter. It is intentionally independent from `app/backend`, `app/frontend-vue`, and `app/frontend-react` builds.

**LTE + Tailscale 실기기 실행** 절차는 [`docs/migration/mobile/run-guide.md`](../../docs/migration/mobile/run-guide.md) 를 참고하세요.

### IntelliJ / PC Android 에뮬레이터 (UI 선검증)

1. Android Studio AVD 실행, 백엔드 `localhost:18081` 기동
2. `.env`: `EXPO_PUBLIC_API_BASE_URL=http://10.0.2.2:18081`
3. 실행 (택 1):
   - **Gradle** (우클릭 Run): 루트 프로젝트 → Tasks → **mobile** → **mobileAndroid**
   - `app/mobile-react-native/run-android.bat` 더블클릭
   - `npm run android` (mobile-react-native 디렉터리)

Gradle Node만 있을 때는 루트에서 `.\gradlew.bat npmSetup` 후 `mobileAndroid` 실행.

## v0.1 Goals

- Read journal entries by day, week, and month
- Search journal entries by keyword and tag
- Open a search or period result and read its full content
- Keep journal data read-only in the mobile user flow

The product and screen contract is documented in
[`docs/migration/mobile/screen-spec.md`](../../docs/migration/mobile/screen-spec.md).

## Start

If Node/npm is not available on PATH, set up Node 20.19.x or newer first.

```bash
cd app/mobile-react-native
npm install
npm run start
```

Copy `.env.example` to `.env` and adjust the API URL when connecting to a local backend.

```bash
EXPO_PUBLIC_API_BASE_URL=http://localhost:18081
```

Android emulators usually reach the host machine through `http://10.0.2.2:18081`. Physical devices need the PC IP address on the same network.

### API URL by Runtime

- Expo Go on Android emulator (AVD): `http://10.0.2.2:18081`
- iOS simulator: `http://localhost:18081` (host setup dependent)
- Physical device (Android/iOS): `http://<YOUR_PC_LAN_IP>:18081`

Quick LAN IP examples:

- Windows PowerShell: `ipconfig`
- macOS/Linux: `ifconfig` or `ip a`

If the app runs on a physical device, make sure backend port `18081` is reachable from the same Wi-Fi network.

## Build Flow

### 1. Development Server

```bash
cd app/mobile-react-native
npm install
npm run start
```

This starts Metro. Expo Go or a development build loads the JavaScript bundle from this server.

### 2. Type Check

```bash
npm run type-check
```

This checks the React Native screens and API client TypeScript.

### 3. Native Project Generation

```bash
npx expo prebuild
```

This creates `android/` and `ios/` only when needed. In the starter phase, keep the app in Expo managed workflow and avoid committing generated native folders unless native code changes become necessary. Both folders are listed in `.gitignore` (CNG: native settings live in `app.json` and config plugins).

### 4. App Artifacts

Local release APK for a real phone (no EAS account, Windows), recommended for device QA:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\build-android-apk.ps1           # dist/dreamdiary-<version>-release.apk
powershell -NoProfile -ExecutionPolicy Bypass -File .\build-android-apk.ps1 -Install  # + adb install -r on the USB phone
```

The script bakes the PC Tailscale IP (`tailscale ip -4`, or `-ApiBaseUrl`) into the bundle without touching `.env`, and signs with the template debug keystore (personal sideloading only). Details: `docs/migration/mobile/run-guide.md` §5.5.

With EAS Build:

```bash
npx eas-cli build --platform android --profile preview
npx eas-cli build --platform ios --profile production
```

Local native run:

```bash
npx expo run:android
npx expo run:ios
```

The `preview` profile in `eas.json` is for internal Android APK testing. The `production` profile is for store-ready builds.

## EAS Preview (optional, needs an EAS account)

1. Install dependencies and login:

```bash
cd app/mobile-react-native
npm install
npx eas-cli login
```

2. Build preview APK:

```bash
npx eas-cli build --platform android --profile preview
```

3. Install APK from the EAS build link on a physical device.
4. Set the API URL **before** building via `eas.json` `build.preview.env.EXPO_PUBLIC_API_BASE_URL` or EAS environment variables — `.env` is gitignored and may not be uploaded to EAS cloud builds. Then run backend and verify:
   - Login
   - Today date navigation
   - Calendar/Tag/Search to EntryDetail

## Structure

```text
app/mobile-react-native/
  App.tsx
  src/
    api/
    screens/
    theme/
    types/
```

Authentication uses cookie-based JWT (`credentials: "include"`) for REST. Login and `POST /api/auth/refresh` also return `Authorization: Bearer …`; the app persists the access token in SecureStore with an in-memory cache (`src/auth/accessToken.ts`) for WebSocket handshake headers on real devices where cookies may not attach to `WebSocket`.
