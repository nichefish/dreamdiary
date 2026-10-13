# 모바일 앱 실행 가이드 (Android 에뮬레이터 · LTE + Tailscale)

PC의 **Android 에뮬레이터(AVD)** 에서 DreamDiary 모바일 앱을 띄우는 로컬 개발 절차(§5.1)와, 폰을 **LTE(셀룰러)** 로만 쓰고 PC 백엔드에 **Tailscale**로 붙여 띄우는 절차를 다룹니다.  
모바일 v0.1 제품·화면 계약은 [`screen-spec.md`](./screen-spec.md)를 참고하세요.

---

## 1. 사전 준비

| 항목 | 설명 |
|---|---|
| Tailscale | PC·폰 **같은 계정**으로 설치·로그인, 둘 다 **Connected** |
| 백엔드 | Spring Boot API가 PC에서 **18081** 포트로 동작 중 (`local`·`dev` 프로필 기본값 `${SERVER_PORT:18081}`) |
| `.env` | `app/mobile-react-native/.env`. 에뮬레이터는 `EXPO_PUBLIC_API_BASE_URL=http://10.0.2.2:18081`(§5.1, 없으면 `run-android.ps1`이 생성), LTE 실기기 Expo Go는 Tailscale IP(§2.2). 로컬 릴리스 APK(§5.5)는 `.env`를 바꾸지 않고 빌드 스크립트가 Tailscale IP를 넣습니다. **git에 커밋하지 않음** |
| Node.js | **22.13.x 이상, Node 22 LTS** 권장. 선택 근거는 §1.1, PATH에 `npm`이 없으면 §4 Gradle Node 참고 |
| 인증 | REST: 쿠키. WebSocket: login/refresh의 `Authorization: Bearer` → SecureStore (`src/auth/accessToken.ts`) |
| Expo Go (에뮬레이터) | `57.0.9` 설치됨. adb 자동 설치가 실패하면 §5.1의 수동 설치 |

**퍼블릭 IP·포트포워딩은 필요 없습니다.**

### 1.1 Node.js 버전 선택 기준

모바일 개발 환경의 표준 런타임은 **Node 22 LTS**로 둡니다. 새로 설치할 때는 `22.13.x` 이상인 최신 Node 22 LTS를 선택합니다.

판단 근거:

- 현재 모바일 프로젝트는 Expo SDK `57.0.19`, React Native `0.86.3`을 사용합니다.
- [Expo SDK 버전 표](https://docs.expo.dev/versions/latest/)에서 Expo SDK 57의 최소 Node.js 버전은 `22.13.x`입니다.
- [Expo 빌드 서버 환경](https://docs.expo.dev/build-reference/infrastructure/)의 SDK 57 이미지도 Node 22 계열을 사용합니다.
- Expo의 [프로젝트 생성 가이드](https://docs.expo.dev/get-started/create-a-project/)는 로컬 개발 환경에 Node.js LTS를 요구합니다.
- React Native `0.86.3` 패키지는 Node `^20.19.4`, `^22.13.0`, `^24.3.0`을 허용하지만, Expo SDK 57의 최소 버전과 공식 빌드 환경을 함께 맞추면 Node 22 LTS가 가장 직접적인 선택입니다.

Node 24는 React Native 허용 범위에 포함되므로 사용할 수 있지만, 이 프로젝트의 표준 버전으로 채택하지 않습니다. Expo SDK 57 공식 빌드 환경과 같은 주 버전을 사용해 로컬과 원격 빌드 사이의 차이를 줄이는 것이 우선입니다.

설치 확인:

```powershell
node --version
npm --version
```

`node --version` 결과가 `v22.13.0` 이상인지 확인합니다. 저장소의 Gradle Node 설정 `20.11.1`은 Expo SDK 57 최소 요구보다 낮으므로 모바일 실행에는 PATH의 Node 22를 우선 사용합니다. Gradle Node 버전 정합은 별도 설정 변경으로 다룹니다. Gradle `mobile` 태스크와 `run-android.ps1`도 PATH의 `npm`(Node 22)을 먼저 쓰고, PATH에 `npm`이 없을 때만 Gradle Node로 대체합니다(§4.1, §5.1).

### 1.2 Android Studio·SDK·AVD 구성

Android 에뮬레이터로 저장 결과를 바로 확인하는 개발 환경은 Android Studio 본체, Android SDK, Android Emulator, AVD(Android Virtual Device)로 구성합니다. Android Studio 설치만으로 필요한 SDK 플랫폼과 가상기기가 모두 준비되었다고 간주하지 않고 SDK Manager와 Device Manager에서 각각 확인합니다.

#### SDK 플랫폼 선택

Android Studio 시작 화면의 `More Actions → SDK Manager → SDK Platforms`에서 다음 항목을 설치합니다.

- `Android 16.0 ("Baklava")`
- `API Level 36.0`

현재 모바일 프로젝트는 Expo SDK `57.0.19`를 사용합니다. [Expo SDK 버전 표](https://docs.expo.dev/versions/latest/)에 따르면 Expo SDK 57의 Android `compileSdkVersion`과 `targetSdkVersion`은 모두 `36`입니다. Android 16의 정식 플랫폼 API 수준이 36이므로 기본 `Android SDK Platform 36` 패키지를 기준 SDK로 설치합니다.

- `compileSdkVersion 36`: 앱을 Android API 36 기준으로 컴파일합니다.
- `targetSdkVersion 36`: 앱이 Android 16의 동작·보안 정책을 대상으로 한다고 선언합니다.
- API 36 AVD: 타깃 플랫폼과 같은 Android 16 실행 환경에서 앱 동작을 확인합니다.

SDK Manager에 함께 표시되는 항목은 다음 기준으로 구분합니다.

| 항목 | 이 프로젝트의 선택 | 이유 |
|---|---|---|
| API `36.0` | 설치 | Expo SDK 57이 요구하는 기본 `compileSdkVersion 36` 플랫폼 |
| API `36.1` | 설치 불필요 | Android 16 QPR2의 minor SDK입니다. 사용하려면 `minorApiLevel = 1` 구성이 필요하며 현재 프로젝트 계약에는 없습니다. |
| API `36.0-ext*` | 설치 불필요 | SDK Extension 전용 API를 컴파일할 때 쓰는 패키지입니다. 현재 앱은 `compileSdkExtension`을 선언하지 않습니다. |
| API `37` | 설치되어 있어도 유지 가능 | Android SDK 플랫폼은 함께 설치될 수 있습니다. API 37의 존재가 API 36 빌드·AVD를 대체하거나 방해하지 않습니다. |

Android의 [SDK Extensions 문서](https://developer.android.com/guide/sdk-extensions)는 Extension API가 필요한 기능에서 해당 Extension Level을 선택하고 `compileSdkExtension`을 선언하도록 규정합니다. [Android 16 QPR2 SDK 문서](https://developer.android.com/about/versions/16/qpr2/setup-sdk)는 API 36.1 사용 시 `minorApiLevel = 1` 설정을 요구합니다. 이 앱은 두 기능을 사용하지 않으므로 기본 API 36.0만 설치합니다.

#### SDK 도구 선택

`SDK Manager → SDK Tools`에서 다음 항목을 설치합니다.

| 도구 | 용도 |
|---|---|
| Android SDK Build-Tools | Android 앱 패키징과 빌드에 필요한 도구 모음 |
| Android SDK Platform-Tools | 에뮬레이터 연결 상태와 로그를 다루는 `adb` 제공 |
| Android SDK Command-line Tools (latest) | SDK 패키지 관리와 Android 명령줄 도구 제공 |
| Android Emulator | AVD를 실제 가상 Android 기기로 실행 |

버전 상세 선택이 필요한 경우 Build-Tools는 설치된 안정 버전 중 최신 `36.x.x`를 사용합니다. 설치 화면에서 `Apply → OK`를 누르고 라이선스에 동의한 뒤 설치를 완료합니다.

#### 첫 AVD 생성

Android Studio 시작 화면에서 `More Actions → Virtual Device Manager`로 이동해 다음 순서로 생성합니다.

1. `Create Virtual Device`를 선택합니다.
2. `Phone` 범주의 `Medium Phone` 하드웨어 프로필을 선택합니다.
3. Android 16 / API 36의 `Google Play Intel x86_64 Atom System Image`를 선택하고, 설치되지 않았다면 내려받습니다.
4. 기본 AVD 설정을 유지하고 `Finish`를 선택합니다.
5. 생성된 가상기기의 실행 버튼을 눌러 Android 홈 화면까지 부팅되는지 확인합니다.

[Android AVD 공식 문서](https://developer.android.com/studio/run/managing-avds)는 Device Manager에서 하드웨어 프로필과 특정 API 수준의 시스템 이미지를 결합해 AVD를 만들도록 안내합니다. 첫 기준 기기를 API 36으로 두면 프로젝트의 타깃 버전과 같은 환경에서 최신 동작·보안 정책을 먼저 검증할 수 있습니다. Expo SDK 57은 Android 7 이상을 지원하므로 하위 버전 호환성이 필요할 때는 낮은 API 수준의 AVD를 별도로 추가합니다.

#### Medium Phone 선택 기준

`Medium Phone`은 특정 제조사나 실제 제품 모델을 앱의 기준으로 삼지 않는 Generic 하드웨어 프로필입니다. 개발 기준은 Samsung Galaxy나 Google Pixel 같은 모델명이 아니라 화면의 논리 크기, Safe Area, 글꼴 배율과 현재 창 크기입니다. 휴대폰을 교체할 때 기종명에 맞춘 코드나 설정을 변경하는 방식은 사용하지 않습니다.

Android 화면에 표시되는 `1080 × 2400` 같은 값은 물리 픽셀 수입니다. React Native 레이아웃은 밀도가 반영된 논리 단위 `dp`를 사용하며 관계는 다음과 같습니다.

```text
dp = px × 160 ÷ dpi
```

현재 선택한 Medium Phone 프로필은 `1080 × 2400`, `420dpi`이므로 약 `411 × 914dp`입니다. Galaxy S10e의 물리 해상도 `1080 × 2280`을 같은 420dpi로 환산하면 약 `411 × 869dp`여서 폭이 같고 세로 길이만 조금 다릅니다. 실제 기기의 논리 크기는 화면 확대·글자 크기·해상도 설정에 따라 달라질 수 있으므로 물리 해상도를 AVD와 완전히 일치시키지 않습니다.

실제 Android 기기를 연결한 뒤 다음 명령으로 현재 물리 해상도와 밀도를 확인할 수 있습니다.

```powershell
adb shell wm size
adb shell wm density
```

기준 AVD 하나만 운영할 때는 Medium Phone/API 36을 사용합니다. 화면 호환성 검증이 필요하면 특정 모델 분기를 추가하는 대신 약 `360dp`의 작은 너비, 글꼴 확대, 세로로 짧은 화면과 실제 기기에서 레이아웃을 추가 확인합니다.

#### 시스템 이미지 선택 기준

일반 개발용 기준 이미지는 `Google Play Intel x86_64 Atom System Image`입니다.

- `Google Play`: Play 서비스가 포함된 실제 사용자 기기에 가까운 실행 환경을 제공합니다.
- `x86_64`: Windows의 Android Emulator에서 사용하는 표준 PC용 시스템 이미지입니다.
- 일반 이미지: JavaScript·React Native 화면과 기본 네이티브 기능을 반복 개발하는 기준 환경입니다.

`16 KB Page Size Google Play Intel x86_64 Atom System Image`는 16KB 메모리 페이지를 사용하는 Android 환경에서 네이티브 라이브러리 호환성을 별도로 검증하기 위한 이미지입니다. 첫 개발 AVD에는 일반 이미지를 사용하고, 네이티브 의존성의 16KB 페이지 호환성 검증이 필요할 때 별도 AVD로 추가합니다.

---

## 2. API URL (`EXPO_PUBLIC_API_BASE_URL`)

LTE에서는 PC의 **Tailscale IP** (`100.x.x.x`)만 사용합니다.

### 2.1 PC Tailscale IP 확인

Windows PowerShell:

```powershell
tailscale ip -4
```

Tailscale 앱에서도 PC 기기의 `100.x.x.x` 주소를 확인할 수 있습니다.

### 2.2 `.env` 설정

```env
EXPO_PUBLIC_API_BASE_URL=http://100.70.88.5:18081
```

> `100.70.88.5`는 예시입니다. §2.1에서 확인한 **본인 PC Tailscale IP**로 바꿉니다.  
> 반드시 **`:18081`** 을 붙입니다 (`http://100.x.x.x/` 만 쓰면 80번 포트로 접속 시도).

### 2.3 접속 전 체크 (폰 브라우저)

1. 폰: **LTE** + Tailscale **Connected**
2. PC: 백엔드 실행 → `http://localhost:18081` 확인
3. 폰 Chrome 등에서:

```text
http://<PC-Tailscale-IP>:18081
```

페이지가 열리면 앱도 같은 URL로 API 호출 가능합니다.

---

## 3. 백엔드 · 방화벽

### 3.1 백엔드 실행

루트 또는 백엔드 모듈에서 Spring Boot를 기동합니다.  
PC 브라우저: `http://localhost:18081`

### 3.2 Windows 방화벽

폰(Tailscale 경유) → PC **18081** 인바운드를 허용해야 하는 경우가 많습니다.

PowerShell **관리자**:

```powershell
New-NetFirewallRule -DisplayName "DreamDiary 18081 Tailscale" -Direction Inbound -Protocol TCP -LocalPort 18081 -Action Allow -Profile Private
```

Tailscale만 켠다고 방화벽이 자동으로 열리지는 않습니다.

**주의:** `http://localhost:18081`은 PC에서만 유효합니다. Android 에뮬레이터 안에서는 `http://10.0.2.2:18081`이 PC의 `localhost`를 가리킵니다.

---

## 4. 의존성 · 타입체크

```bash
cd app/mobile-react-native
npm install
npm run type-check
```

- `babel-preset-expo`(`~57.0.10`)를 `devDependencies`에 명시했습니다. `babel.config.js`가 프리셋을 이름(`"babel-preset-expo"`)으로 참조하므로 전이 의존성에 기대지 않고 직접 설치합니다.
- **새 의존성 `expo-clipboard`(`~57.0.2`)**: 복사 기능(엔트리 상세·이 날/주/달 `복사`)이 사용합니다. 이 변경을 pull한 뒤에는 `app/mobile-react-native`에서 `npm install`을 실행하고 Metro를 재시작합니다(실행 중인 Expo/Metro를 종료하고 §5.1 방식으로 다시 띄움). `run-android.ps1`(`mobileAndroid`)은 `node_modules`가 없을 때만 `npm install`을 실행하므로, 이미 설치된 환경에서는 새 패키지를 자동으로 받지 않습니다. Gradle에서는 `mobileInstall`을 먼저 실행해도 됩니다.
  - Expo Go에는 `expo-clipboard` 네이티브 모듈이 포함되어 있어 Expo Go 재설치는 필요 없습니다. EAS Preview APK(§5.2)·로컬 네이티브 빌드(§5.4)·로컬 릴리스 APK(§5.5)는 네이티브 모듈이 추가되었으므로 새로 빌드해야 복사가 동작합니다.
- **새 의존성 `expo-build-properties`(`~57.0.22`, 2026-10-06)**: 빌드 설정 전용 config plugin입니다(JS 런타임 코드 없음). `app.json`에서 `android.usesCleartextTraffic: true`를 켜 릴리스 APK가 `http://<Tailscale-IP>:18081`(평문 HTTP)에 접속할 수 있게 합니다. Android는 API 28+ 릴리스 빌드에서 평문 HTTP를 기본으로 막고, Expo 템플릿은 debug 매니페스트에만 허용을 넣기 때문입니다. prebuild 때마다 매니페스트에 다시 반영되므로 `android/`를 손으로 고치지 않습니다. Expo Go 개발에는 영향이 없습니다.
  - 공유는 React Native 내장 `Share` API라 추가 패키지가 없습니다.
- Expo CLI가 `expo` `57.0.19` → `57.0.26` 업데이트를 제안하지만 이번에는 보류했습니다. 후속 작업에서 `npx expo install --fix`로 SDK 57 기대 버전에 맞춥니다([`sdk-upgrade.md`](./sdk-upgrade.md) 후속 기록).

- **네이티브 푸시 의존성**: `expo-notifications`(`~57.0.22`)와 `expo-crypto`(`~57.0.4`)는 Expo SDK 57 호환 버전을 사용한다. `app.json`의 notifications 플러그인은 알림 색을 제공하며 네이티브 매니페스트의 Android 알림 권한을 병합한다. 앱이 실행 중 `keep-reminder` 채널을 만든다. 플러그인·Firebase 클라이언트 구성 변경은 `build-android-apk.ps1 -CleanPrebuild`로 네이티브 구성을 반영한다. 실제 수신 구성은 아래 Keep 서버 푸시 설정과 검증 절을 따른다.
- **Expo Go(SDK 57)**: 로컬 알림 예약·표시는 Expo Go에서도 동작한다(Expo Go에서 빠진 것은 Android 원격 푸시뿐). 다만 시작 시 `expo-notifications functionality is not fully supported in Expo Go` 경고가 콘솔에 한 번 뜨고, 알림 채널 이름·아이콘 색은 Expo Go 앱 기준이다. 확인은 APK(§5.5)를 권장한다.

### 4.1 Gradle 태스크 (`gradle/mobile.gradle`)

IntelliJ Gradle 창의 `mobile` 그룹에서 실행하거나 루트에서 `.\gradlew.bat <태스크>`로 실행합니다.

| 태스크 | 실행 내용 |
|---|---|
| `mobileAndroid` | Windows: `powershell -NoProfile -ExecutionPolicy Bypass -File app\mobile-react-native\run-android.ps1` (§5.1). 그 외 OS는 안내 메시지와 함께 실패 |
| `mobileInstall` | `app/mobile-react-native`에서 `npm install` |
| `mobileTypeCheck` | `mobileInstall` 후 `npm run type-check` (`tsc --noEmit`) |
| `mobileApk` | Windows: `build-android-apk.ps1` — 실기기용 릴리스 APK를 `app/mobile-react-native/dist/`에 만듭니다 (§5.5) |
| `mobileApkInstall` | Windows: `build-android-apk.ps1 -Install` — 빌드 후 USB로 연결된 폰(에뮬레이터 제외)에 `adb install -r` (§5.5) |

- 모든 태스크는 **`Exec` 타입**입니다. 저장소가 configuration cache(`gradle.properties`의 `org.gradle.configuration-cache=true`)를 켜 두었으므로, 실행 단계에서 스크립트 메서드를 호출하던 `doLast { exec {} }` 방식 대신 경로와 명령을 구성 단계에서 계산하는 `Exec` 태스크로 바꿨습니다.
- npm 선택 순서: PATH에 `npm`(Windows는 `npm.cmd`)이 있으면 `cmd /c npm …`을 쓰고, 없을 때만 `.gradle/nodejs/node-v*` 중 최신 디렉터리의 `node.exe` + `npm-cli.js`를 씁니다. Expo SDK 57은 Node `22.13.x` 이상이 필요한데, Gradle Node 핀(`build.gradle`의 `node.version = '20.11.1'`, frontend-vue와 공유)은 그보다 낮기 때문입니다.
- `mobileInstall`·`mobileTypeCheck`의 PATH 판단은 Gradle을 실행한 프로세스의 환경 변수를 기준으로 합니다. Node를 설치하기 전에 열어 둔 IDE에서는 PATH에 `npm`이 없어 Gradle Node로 대체될 수 있으므로 IDE를 다시 시작합니다. `mobileAndroid`는 `run-android.ps1`이 레지스트리 PATH를 다시 읽으므로 영향을 덜 받습니다.

### 4.2 PATH에 npm이 없을 때 (Gradle Node)

```powershell
$node = "C:\Dev\projects\dreamdiary\workspace\.gradle\nodejs\node-v20.11.1-win-x64\node.exe"
$npmCli = "C:\Dev\projects\dreamdiary\workspace\.gradle\nodejs\node-v20.11.1-win-x64\node_modules\npm\bin\npm-cli.js"
Set-Location C:\Dev\projects\dreamdiary\workspace\app\mobile-react-native
& $node $npmCli install
& $node $npmCli run type-check
```

(경로·Node 버전은 환경에 맞게 조정. RN 권장 Node보다 낮으면 `EBADENGINE` 경고가 날 수 있음. Gradle Node `20.11.1`은 Expo SDK 57 최소 요구 `22.13.x`보다 낮으므로 임시 수단으로만 사용.)

---

## 5. 앱 띄우기

### 5.1 Android 에뮬레이터 + Expo Go (로컬 개발)

PC에서 AVD를 부팅하고 백엔드(`localhost:18081`)를 띄운 뒤 다음 중 하나로 실행합니다.

```powershell
# IntelliJ: Gradle 창 > mobile > mobileAndroid 실행, 또는 저장소 루트에서
.\gradlew.bat mobileAndroid

# 스크립트 직접 실행
.\app\mobile-react-native\run-android.bat
powershell -NoProfile -ExecutionPolicy Bypass -File .\app\mobile-react-native\run-android.ps1
```

에뮬레이터 안에서 `10.0.2.2`는 PC의 `localhost`를 가리키므로 `.env`는 다음과 같습니다.

```env
EXPO_PUBLIC_API_BASE_URL=http://10.0.2.2:18081
```

`run-android.ps1`은 다음 순서로 동작합니다.

1. 출력 인코딩을 UTF-8로 맞춥니다(`[Console]::OutputEncoding`, `$OutputEncoding`, `chcp 65001`). IntelliJ Gradle 콘솔과 cmd 자식 프로세스에서 한글이 깨지지 않게 하기 위해서입니다.
2. 현재 PATH에 레지스트리의 시스템·사용자 PATH를 합칩니다. Node 설치 전에 켠 IDE에서도 `node`를 찾게 하기 위해서이며, 그래도 없으면 `C:\Program Files\nodejs\node.exe`가 있을 때 그 디렉터리를 PATH 앞에 붙입니다.
3. `.env`가 없으면 `EXPO_PUBLIC_API_BASE_URL=http://10.0.2.2:18081`로 만들고, 있는데 `10.0.2.2`가 없으면 경고만 출력합니다.
4. `node_modules`가 없으면 `npm install`을 실행합니다. PATH의 `npm`을 먼저 쓰고, 없을 때만 Gradle Node를 씁니다(이때 Gradle Node 디렉터리를 PATH 앞에 추가).
5. 8081부터 LISTEN 중이 아닌 첫 포트를 Metro 포트로 고릅니다(`[mobile] Metro port: …` 출력). Gradle·IDE 실행은 비대화형이라 Expo가 "다른 포트를 쓸까요?"를 물으면 그대로 멈추기 때문입니다. 8081은 다른 Java 앱이 쓰고 있을 수 있습니다.
6. `adb`(PATH → `ANDROID_HOME` → `ANDROID_SDK_ROOT` → `%LOCALAPPDATA%\Android\Sdk\platform-tools`)를 찾아, 이미 연결된 기기마다 `adb reverse tcp:<포트> tcp:<포트>`를 겁니다(`[mobile] adb reverse …` 출력). 실행 중인 기기가 없으면 건너뛰고, Expo가 AVD를 띄워 앱을 열 때 직접 reverse합니다.
7. `NODE_OPTIONS`에 `--dns-result-order=ipv4first`를 붙입니다(이미 있으면 그대로, 기존 값이 있으면 뒤에 추가). Metro가 `127.0.0.1`에 listen하게 하기 위해서입니다(아래 「Failed to download remote update」 참고).
8. `npm run android -- --port <포트> --localhost`(`expo start --android --port <포트> --localhost`)를 실행합니다. `--localhost`로 Expo Go가 `exp://127.0.0.1:<포트>`로 접속하므로, PC LAN IP가 바뀌거나 방화벽이 끼어도 Metro 연결이 끊기지 않습니다.

> **왜 localhost + adb reverse인가 (2026-10-06):** 에뮬레이터가 PC LAN IP(예: `223.x.x.x:8082`)로 Metro에 붙어 있다가 연결이 조용히 끊겨, 코드를 고쳐도 에뮬레이터에 옛 번들이 계속 보이는 문제가 있었습니다. 백엔드 주소(`.env`의 `10.0.2.2:18081`)는 그대로이며 reverse 대상이 아닙니다. 에뮬레이터를 재시작했거나 연결이 끊겼으면 스크립트를 다시 실행하거나 `adb reverse tcp:<포트> tcp:<포트>` 후 Expo Go에서 `exp://127.0.0.1:<포트>`를 다시 엽니다. LTE 실기기(Tailscale)는 USB 연결이 아니므로 이 방식 대신 §2.2를 따릅니다.

> **Expo Go "Something went wrong" / `Uncaught Error: java.io.IOException: Failed to download remote update` (2026-10-11):** `exp://127.0.0.1:<포트>`를 열어도 번들을 못 받는 경우입니다.
> - **원인:** `--localhost`면 Expo CLI가 Metro를 호스트명 `localhost`로 listen하는데, Node 22는 `localhost`를 `::1`(IPv6)로 먼저 풀어 Metro가 `[::1]:<포트>`에만 뜹니다. manifest의 번들 URL과 `adb reverse`는 PC의 `127.0.0.1:<포트>`(IPv4)로 붙으므로 연결이 거절됩니다. 번들 자체는 정상 빌드되고(PC에서 `http://localhost:<포트>/status`는 `packager-status:running`), Tailscale·LAN IP와는 무관합니다.
> - **확인:** `Get-NetTCPConnection -LocalPort <포트> -State Listen`이 `::1`만 보이고 `curl.exe http://127.0.0.1:<포트>/status`가 `Failed to connect`이면 이 문제입니다.
> - **해결:** `run-android.ps1`(=`mobileAndroid`)이 `NODE_OPTIONS=--dns-result-order=ipv4first`를 자동으로 넣습니다. 이 수정 전에 띄운 Metro는 Ctrl+C로 끄고 다시 실행합니다. 스크립트 없이 직접 띄울 때는 다음처럼 합니다.
>
> ```powershell
> cd app\mobile-react-native
> $env:NODE_OPTIONS = '--dns-result-order=ipv4first'
> adb -s emulator-5554 reverse tcp:8082 tcp:8082
> npx.cmd expo start --android --port 8082 --localhost
> ```
>
> 정상이면 `Get-NetTCPConnection -LocalPort 8082 -State Listen`에 `127.0.0.1`이 보이고 `curl.exe http://127.0.0.1:8082/status`가 `packager-status:running`을 돌려줍니다.

> **인코딩 주의:** `run-android.ps1`은 **UTF-8(BOM 포함)** 으로 저장합니다. Gradle과 `run-android.bat`이 호출하는 Windows PowerShell 5.1(`powershell.exe`)은 BOM 없는 스크립트를 시스템 ANSI 코드 페이지(한국어 Windows는 CP949)로 읽으므로 한글 문자열이 깨집니다. 편집기에서 다시 저장할 때 BOM이 빠지지 않게 합니다.

#### Expo Go 설치

- `expo start --android`가 에뮬레이터에 Expo Go를 adb로 자동 설치하고 실행합니다. 현재 에뮬레이터에는 Expo Go `57.0.9`가 설치되어 있습니다.
- 설치 버전 확인:

```powershell
adb shell dumpsys package host.exp.exponent | Select-String versionName
```

- 자동 설치가 실패하면 수동으로 설치합니다. [Expo Go 페이지](https://expo.dev/go)에서 SDK 57용 Android APK를 내려받아 `adb install -r <APK 경로>`로 설치한 뒤 `mobileAndroid`를 다시 실행합니다. Google Play 시스템 이미지의 Play 스토어 Expo Go는 최신 SDK만 지원하므로, SDK 57이 최신일 때만 대안이 됩니다.

### 5.2 EAS Preview APK (선택, EAS 계정 필요)

EAS 클라우드에서 APK를 빌드합니다. EAS 계정이 없으면 §5.5 로컬 릴리스 APK를 사용합니다(현재 권장).

```bash
cd app/mobile-react-native
npm install
npx eas-cli login
npx eas-cli build --platform android --profile preview
```

1. 빌드 **전** API URL을 EAS 쪽에 설정합니다(아래 주의).
2. EAS 빌드 완료 → APK를 폰(LTE)에 설치
3. 폰 Tailscale Connected + 백엔드·방화벽 준비 후 실행

`eas.json`의 `preview`: Android **APK**, internal 배포.

> **`.env` 주의:** `.env`는 `.gitignore` 대상이라 EAS가 클라우드로 올리는 프로젝트에 포함되지 않을 수 있습니다(EAS는 `.easignore`가 없으면 `.gitignore`를 따름). 로컬 `.env`에 Tailscale IP를 넣어도 클라우드 빌드에는 반영되지 않거나, 에뮬레이터용 `10.0.2.2`가 들어갈 수 있습니다. 다음 중 하나로 설정합니다.
> - `eas.json`의 `build.preview.env`에 `"EXPO_PUBLIC_API_BASE_URL": "http://<PC-Tailscale-IP>:18081"` 추가(Tailscale 사설 IP라 공개 저장소가 아니면 커밋해도 위험은 낮음)
> - 또는 EAS 환경 변수: `npx eas-cli env:create --environment preview --name EXPO_PUBLIC_API_BASE_URL --value http://<PC-Tailscale-IP>:18081 --visibility plaintext` 후 `preview` 프로필에 `"environment": "preview"` 지정
>
> 평문 HTTP 허용(`expo-build-properties`, §4)은 `app.json`에 있으므로 EAS 빌드에도 적용됩니다.

### 5.3 Expo Go — LTE 실기기 (선택, 설정 부담 큼)

```bash
cd app/mobile-react-native
npm run start
```

- QR을 Expo Go로 스캔
- LTE에서는 **Metro 번들 서버(PC)** 까지 Tailscale로 접근 가능해야 함
- `.env` 변경 후 Metro **재시작**

문제가 잦으면 §5.5 로컬 릴리스 APK를 사용하세요.

### 5.4 로컬 네이티브 실행 (선택)

```bash
npx expo run:android
```

`android/`가 없으면 prebuild로 만듭니다(`.gitignore` 대상, §5.5). Windows에서 C++ 빌드가 `Filename longer than 260 characters`로 실패하면 §5.5의 CMake 설정(`android/local.properties`의 `cmake.dir`)이 필요합니다. `build-android-apk.ps1`을 한 번 실행하면 설정됩니다.

### 5.5 로컬 릴리스 APK + USB 설치 (LTE 실기기 **권장**, EAS 계정 불필요)

PC에서 릴리스 APK를 직접 빌드해 폰에 USB로 설치합니다. JS 번들이 APK 안에 들어가므로 Metro 없이 실행되고, API 주소(PC Tailscale IP)가 빌드 시점에 박혀 LTE에서 바로 동작합니다.

#### 사전 준비 (PC)

| 항목 | 확인 (2026-10-06 이 PC) |
|---|---|
| Android SDK | `%LOCALAPPDATA%\Android\Sdk` (`ANDROID_HOME`이 없으면 스크립트가 이 경로를 씀). platform `android-36`, build-tools `36.0.0` |
| NDK | `27.1.12297006` — 첫 빌드 때 Gradle이 자동 설치(build-tools `35.0.0`도 자동 설치됨) |
| CMake | SDK `cmake;3.31.6`(ninja `1.12.1`) — 없으면 스크립트가 `sdkmanager`로 설치. 이유는 아래 "Windows 경로 길이" |
| JDK | 17 이상. `JAVA_HOME`(현재 `%USERPROFILE%\.jdks\ms-17.0.19`) → `%USERPROFILE%\.jdks\*` → Android Studio `jbr` 순으로 찾고 17~24를 우선합니다(Android Studio jbr은 JDK 25) |
| Node | 22.13+ (`C:\Program Files\nodejs`, 현재 v22.23.3) |
| Windows 긴 경로 | `HKLM\SYSTEM\CurrentControlSet\Control\FileSystem\LongPathsEnabled = 1` (현재 1). 0이면 관리자 PowerShell: `New-ItemProperty HKLM:\SYSTEM\CurrentControlSet\Control\FileSystem -Name LongPathsEnabled -Value 1 -PropertyType DWORD -Force` 후 재부팅 |
| Tailscale | PC IPv4 `tailscale ip -4` (현재 `100.70.88.5`) |
| 백엔드·방화벽 | §3 (18081 인바운드 허용) |

#### 빌드

```powershell
# IntelliJ: Gradle 창 > mobile > mobileApk (설치까지: mobileApkInstall), 또는 저장소 루트에서
.\gradlew.bat mobileApk

# 스크립트 직접 실행 (app/mobile-react-native)
powershell -NoProfile -ExecutionPolicy Bypass -File .\build-android-apk.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File .\build-android-apk.ps1 -ApiBaseUrl http://100.70.88.5:18081   # 주소 직접 지정
powershell -NoProfile -ExecutionPolicy Bypass -File .\build-android-apk.ps1 -Install                                # 빌드 후 USB 폰에 설치
```

| 옵션 | 설명 |
|---|---|
| `-ApiBaseUrl <url>` | API 주소. 생략하면 `tailscale ip -4`로 `http://<ip>:18081`. Tailscale이 없거나 꺼져 있으면 멈추고 안내합니다(주소를 추측하지 않음) |
| `-Install` | 빌드 후 USB로 연결된 실기기(`emulator-*` 제외)에 `adb install -r`. 한 대일 때만 자동 선택 |
| `-Serial <serial>` | 설치 대상 지정(여러 대이거나 에뮬레이터에 설치할 때, 예: `emulator-5554`) |
| `-Architectures <abis>` | 기본 `arm64-v8a,x86_64`(폰 + 에뮬레이터). `all`이면 4개 ABI 모두(빌드가 더 느림) |
| `-CleanPrebuild` | `android/`를 지우고 다시 prebuild (`app.json`·플러그인·네이티브 의존성을 바꾼 뒤) |

스크립트 동작:

1. 출력 UTF-8, PATH 새로 읽기·Node 기본 경로 보충(`run-android.ps1`과 같음).
2. API 주소를 정하고 **이 프로세스의 환경 변수** `EXPO_PUBLIC_API_BASE_URL`로만 넣습니다. `.env`(에뮬레이터 `10.0.2.2`)는 바꾸지 않습니다. Expo SDK 57의 `@expo/env`는 이미 있는 환경 변수를 `.env*` 값으로 덮어쓰지 않으므로(`loadEnvFiles`: "already defined and IS NOT overwritten") 프로세스 값이 이깁니다. `EXPO_PUBLIC_*`는 번들할 때 코드에 박힙니다.
3. JDK 17+, `ANDROID_HOME`을 찾습니다.
4. `android/gradlew.bat`이 없으면(또는 `-CleanPrebuild`) `npx expo prebuild --platform android --no-install`. prebuild가 `package.json`을 고치므로(`Updated package.json` — scripts를 `expo run:*`로 바꾸는 등), 스크립트가 prebuild 전 내용으로 되돌립니다(Expo Go 개발용 scripts 유지).
5. SDK CMake 3.30+를 `android/local.properties`의 `cmake.dir`로 지정합니다(아래).
6. `gradlew :app:createBundleReleaseJsAndAssets --rerun assembleRelease -PreactNativeArchitectures=…`. Gradle은 환경 변수를 입력으로 보지 않아 API 주소만 바꾸면 JS 번들이 `UP-TO-DATE`로 건너뛰어지므로 번들 태스크는 매번 다시 실행합니다(약 30초). 네이티브 부분은 증분 빌드입니다.
7. `android/app/build/outputs/apk/release/app-release.apk`를 `dist/dreamdiary-<app.json version>-release.apk`로 복사하고, APK 안 `assets/index.android.bundle`(Hermes 바이트코드)에 API 주소 문자열이 있는지 확인합니다. 없으면 실패로 끝냅니다. `10.0.2.2:18081`이 있으면 경고합니다.
8. `-Install`이면 adb 설치.

소요 시간(이 PC): 첫 빌드 약 9분(Gradle 9.3.1·NDK 다운로드 포함, 경로 길이 문제로 한 번 실패) + 7분, 이후 재빌드 약 30~40초.

> **CNG(Continuous Native Generation):** `android/`·`ios/`는 `app.json` 기준으로 prebuild가 생성하는 산출물이라 `app/mobile-react-native/.gitignore`에 넣었습니다(`/android/`, `/ios/`). 네이티브 설정은 `app.json`·config plugin으로만 바꿉니다. `dist/`도 무시 대상입니다.

> **서명:** Expo prebuild 템플릿의 `release` 빌드는 `android/app/debug.keystore`(공용 Android Debug 인증서, `CN=Android Debug`)로 서명됩니다. 개인 사이드로드에는 충분하지만 스토어 배포용이 아니며, 같은 공용 키라 보안상 배포용으로 쓰지 않습니다. 템플릿 키가 같으므로 다시 prebuild해도 서명은 같아 `install -r` 업데이트가 됩니다. 다른 키로 서명된 같은 패키지(`com.dreamdiary.mobile`)가 폰에 있으면 `INSTALL_FAILED_UPDATE_INCOMPATIBLE`이 나므로 기존 앱을 삭제하고 설치합니다(로그인 정보도 지워짐).

> **Windows 경로 길이:** RN 새 아키텍처는 `app` 모듈에서 자동 연결 라이브러리(react-native-screens·safe-area-context)의 C++를 CMake/ninja로 빌드합니다. 오브젝트 경로가 `…/CMakeFiles/react_codegen_safeareacontext.dir/C_/Dev/projects/dreamdiary/workspace/app/mobile-react-native/node_modules/…/X.cpp.o`처럼 260자를 넘는데, AGP 기본 CMake 3.22.1에 든 ninja 1.10.2는 `LongPathsEnabled=1`이어도 긴 경로를 거부합니다(`ninja: error: Stat(…): Filename longer than 260 characters`). ninja 1.12+는 긴 경로를 쓰므로 SDK CMake 3.31.6(ninja 1.12.1)을 `cmake.dir`로 지정해 해결했습니다. `local.properties`는 `android/` 안이라 prebuild `--clean` 뒤에도 스크립트가 다시 씁니다.

#### 폰 준비 (Galaxy S10e 등)

1. **개발자 옵션 켜기**: 설정 → 휴대전화 정보 → 소프트웨어 정보 → **빌드번호**를 7번 누름.
2. **USB 디버깅 켜기**: 설정 → 개발자 옵션 → **USB 디버깅** 켬.
3. USB 케이블로 PC에 연결 → 폰에 뜨는 **"USB 디버깅을 허용하시겠습니까?"** 에서 이 PC의 RSA 키를 **허용**(항상 허용 체크). USB 모드는 "파일 전송"이 안정적입니다.
4. PC에서 확인: `adb devices` → `<시리얼>    device` (`unauthorized`면 3번 다시, 목록에 없으면 케이블·Samsung USB 드라이버 확인).
5. 설치: `.\gradlew.bat mobileApkInstall` 또는 `build-android-apk.ps1 -Install`(이미 빌드했으면 `adb -s <시리얼> install -r app\mobile-react-native\dist\dreamdiary-0.1.0-release.apk`도 됨). 폰에 Play 프로텍트 경고가 뜨면 "무시하고 설치"를 고릅니다(디버그 서명 앱).
   - USB 없이 설치하려면 APK를 폰으로 옮겨 파일 앱에서 열고 "출처를 알 수 없는 앱 설치"를 그 앱에 허용합니다.
6. 폰 **Tailscale 앱 켜기**(PC와 같은 계정, Connected). 앱 실행 전에 폰 브라우저로 `http://<PC-Tailscale-IP>:18081`이 열리는지 확인하면 좋습니다(§2.3).
7. PC 백엔드 실행·방화벽(§3) 확인 후 앱 **DreamDiary** 실행 → 로그인.

PC Tailscale IP가 바뀌면(기기 재등록 등) 다시 빌드해 설치합니다. 백엔드만 다시 띄우는 것은 재빌드가 필요 없습니다.

#### 이미 설치된 폰에 새 버전 올리기 (업데이트)

새 APK를 기존 앱 위에 덮어 설치하면 **앱 데이터와 로그인이 그대로 남습니다**. 조건은 패키지(`com.dreamdiary.mobile`)와 **서명 키가 같을 것**입니다. 로그인 토큰은 `expo-secure-store`(`src/auth/accessToken.ts`)에 있어 덮어 설치로는 지워지지 않고, 앱을 삭제하면 함께 지워집니다. `versionCode`는 1로 고정이지만 같은 버전 덮어쓰기는 허용됩니다(`adb install -r`, 파일 앱 설치 모두).

1. **USB로 한 번에 (권장)**: 폰에서 개발자 옵션 → **USB 디버깅**을 켜고(위 "폰 준비" 1~2) USB로 연결합니다. 처음 연결할 때 뜨는 **"USB 디버깅을 허용하시겠습니까?"** 를 허용합니다. 그다음 PC에서
   ```powershell
   powershell -NoProfile -ExecutionPolicy Bypass -File .\build-android-apk.ps1 -Install                 # 빌드 + adb install -r
   powershell -NoProfile -ExecutionPolicy Bypass -File .\build-android-apk.ps1 -Install -Serial <시리얼>  # 기기가 여러 대일 때
   ```
   (또는 `.\gradlew.bat mobileApkInstall`). 스크립트는 빌드 후 `emulator-*`가 아닌 기기가 **한 대일 때만** 자동으로 고르고, 없거나 여러 대면 멈추고 안내합니다. `-Serial`을 주면 그 기기(에뮬레이터 포함)에 설치합니다.
2. **USB 없이**: `dist\dreamdiary-<버전>-release.apk`를 폰으로 옮겨(메신저·클라우드·케이블 파일 복사) 파일 앱에서 열고 **업데이트**를 누릅니다. 처음이면 그 파일 앱에 "출처를 알 수 없는 앱 설치"를 허용합니다.

**서명이 다르면 덮어 설치가 안 됩니다.** 폰에 `앱이 설치되지 않음`(adb는 `INSTALL_FAILED_UPDATE_INCOMPATIBLE`, 스크립트는 "서명이 다른 기존 앱을 폰에서 삭제한 뒤 다시 설치하세요")이 뜨면 기존 앱을 삭제한 뒤 설치해야 하고, 이때 **로그인 정보와 앱 데이터가 지워집니다**.

- 이 스크립트의 APK는 `android/app/debug.keystore`로 서명됩니다. 이 파일은 Expo 템플릿(`expo-template-bare-minimum`)에 들어 있는 **고정 파일**이라 `-CleanPrebuild`로 `android/`를 다시 만들어도 같은 파일이 복사되어 키가 바뀌지 않습니다(2026-10-06 확인: 생성된 `android/app/debug.keystore`와 템플릿 `57.0.29`의 파일 SHA-256 일치, 인증서 SHA1 `5E:8F:16:06:2E:A3:CD:2C:4A:0D:54:78:76:BA:A6:F3:8C:AB:F6:25`). 따라서 `-CleanPrebuild` 뒤에도 업데이트 설치가 됩니다.
- 서명이 달라지는 경우: EAS로 만든 APK(§5.2, EAS가 관리하는 키)가 깔려 있는 폰에 이 APK를 올릴 때(또는 그 반대), `android/app/debug.keystore`를 다른 키로 바꾸거나 `app.json`·config plugin으로 릴리스 서명 설정을 바꾼 뒤. 서명 방식을 바꿀 계획이면 그 전에 폰의 로그인·데이터가 지워져도 되는지 먼저 확인합니다.

---

## 6. v0.1 계약 스모크 테스트 체크리스트

- [ ] (에뮬레이터) `mobileAndroid` 실행 → Expo Go에서 앱 로드, Gradle 콘솔 한글 정상
- [ ] (LTE 실기기) 폰 LTE + Tailscale Connected
- [ ] (LTE 실기기, 로컬 릴리스 APK §5.5) Metro 없이 앱이 열리고, LTE에서 로그인·일별 조회가 됨(평문 HTTP 허용 확인)
- [ ] 비로그인 첫 실행: 오류 토스트 없이 로그인 화면 표시 (`get-auth-info` 401은 info 로그)
- [ ] 로그인 화면: 빈 아이디·비밀번호에서 `필수 입력 항목입니다.` 표시, 키보드가 입력란을 가리지 않음
- [ ] 로그인 / 앱 재시작 후 세션 유지
- [ ] 같은 계정으로 웹에 로그인한 상태에서 모바일 로그인 → 웹 세션 유지 (채널별 세션)
- [ ] 일별 조회 → 상세 읽기
- [ ] 주별 조회 → 상세 읽기
- [ ] 월별 조회 → 상세 읽기
- [ ] 일·주·월 기간 라벨(▾) → 날짜 선택 피커로 이동, 미래 날짜 비활성
- [ ] 키워드 검색(일기·꿈 필터) → 상세 읽기
- [ ] 하단 탭이 `오늘 · 검색 · 나` 3개 (태그 탭 없음)
- [ ] 일·주·월 기간 태그 패널: `이 날/주/달의 태그` 제목, 펼침·접힘(▾/▸), 18개 초과 행의 `+N 더보기`, 기간 이동 시 갱신
- [ ] 엔트리 카드의 `#[분류]이름` 태그·`🧵` 스레드 칩, 주·월 일자 헤더 아래 일자 태그 칩
- [ ] 일·주·월 기간 스레드 섹션: `이 날/주/달의 스레드`, 펼침·접힘, 월 보기 10개 초과 시 `펼치기` (스레드를 눌러도 동작 없음)
- [ ] 엔트리 상세 본문 아래 `태그`(꿈 태그)·`스레드`·`일자 태그` 행
- [ ] 패널 태그·태그 칩 → 태그별 기록 화면(연도 선택, 날짜별 결과, 태그 설명) → 엔트리 상세 / 날짜 헤더 → 오늘 탭 그 날짜
- [ ] 엔트리 상세·이 날/주/달 `복사`(토스트) · `공유`(시스템 공유 시트)
- [ ] 실기기 미검증 항목: [`screen-spec.md`](./screen-spec.md) §11 체크리스트 (태그 크기·색, `threadList`·기간 스레드, 태그 설명, 일자 태그 결과 엔트리, 검색 결과 날짜, 스택 화면 → 오늘 탭, 클립보드·공유 시트)
- [ ] 날짜·검색·상세 흐름에 저널 생성·수정·삭제 조작이 노출되지 않음

---

## 7. 자주 나는 문제

| 증상 | 확인 |
|---|---|
| 네트워크 오류 | `.env`가 에뮬레이터 `10.0.2.2:18081` 또는 Tailscale `100.x.x.x:18081` 인지, 포트 누락 여부. `.env` 변경 후 Metro 재시작 |
| 폰 Tailscale offline | 앱에서 Connected, PC와 같은 계정 |
| PC만 되고 폰만 안 됨 | 방화벽 18081, 폰 브라우저로 `http://<Tailscale-IP>:18081` 선행 테스트 |
| 백엔드 미기동 | PC `localhost:18081`, `netstat` 등으로 18081 리스닝 확인 |
| AI 채팅만 실패 | 로그인·refresh 후 토큰, 앱 재시작 |
| Expo Go만 실패 | 로컬 릴리스 APK(§5.5) 사용 권장 |
| APK 빌드: `ninja: error: Stat(…): Filename longer than 260 characters` | `LongPathsEnabled=1`인지, `android/local.properties`에 SDK CMake 3.30+(`cmake.dir`)가 지정됐는지(§5.5). `build-android-apk.ps1`을 다시 실행하면 설정됨 |
| APK 빌드: `번들에서 http://… 을 찾지 못했습니다` | 번들 태스크가 옛 결과를 썼을 수 있음. `android\app\build`를 지우고 다시 빌드 |
| APK 설치: `INSTALL_FAILED_UPDATE_INCOMPATIBLE` | 다른 서명의 같은 패키지가 있음. 폰에서 DreamDiary 삭제 후 설치(§5.5 서명) |
| APK 설치: `adb devices`에 폰이 없음 / `unauthorized` | USB 디버깅·허용 팝업·케이블(데이터 지원)·Samsung USB 드라이버(§5.5 폰 준비) |
| 설치한 APK에서 네트워크 오류 | 폰 Tailscale Connected인지, 폰 브라우저로 `http://<PC-Tailscale-IP>:18081`, PC 방화벽 18081. PC Tailscale IP가 빌드 때와 같은지 |
| 코드를 고쳐도 에뮬레이터 화면이 그대로(옛 번들) | Metro 연결 끊김. `adb reverse tcp:<포트> tcp:<포트>` 후 Expo Go에서 `exp://127.0.0.1:<포트>` 다시 열기, 또는 `run-android.ps1` 재실행(§5.1 6·7단계) |
| Gradle·IDE 실행이 Metro 포트 질문에서 멈춤 | `run-android.ps1`이 빈 포트를 골랐는지 `[mobile] Metro port:` 로그 확인 (§5.1) |
| Gradle 콘솔에서 한글이 깨짐 | `run-android.ps1`이 UTF-8 BOM으로 저장되어 있는지 확인 (§5.1 인코딩 주의) |
| `npm not found` / `node`를 찾지 못함 | Node 22 설치 여부, IDE 재시작. `run-android.ps1`은 레지스트리 PATH와 `C:\Program Files\nodejs`를 확인 |
| 에뮬레이터에 Expo Go 자동 설치 실패 | §5.1 Expo Go 수동 설치 |
| `Unable to resolve module expo-clipboard` / `Cannot find native module 'ExpoClipboard'` | pull 뒤 `npm install` 후 Metro 재시작(§4). Preview APK·네이티브 빌드는 다시 빌드 |
| 앱 시작 시 `get-auth-info` 401 로그 | 비로그인 정상 흐름(info 로그)이며 오류가 아님 ([`screen-spec.md`](./screen-spec.md) §7.3) |
| 웹 로그인 상태에서 모바일 로그인 시 중복 로그인 처리·웹 세션 끊김 | 백엔드가 채널별 세션 코드로 기동했는지, 요청에 `X-Client-Channel: MOBILE` 헤더가 붙는지 확인 ([`screen-spec.md`](./screen-spec.md) §7.2) |
| PowerShell에서 `npm.ps1` 실행 차단 | §7.1의 실행 정책 확인 또는 `npm.cmd` 사용 |

### 7.1 PowerShell에서 `npm.ps1` 실행이 차단될 때

Node.js와 npm이 정상 설치되어도 PowerShell 실행 정책이 `npm.ps1`을 차단하면 `npm --version`에서 `PSSecurityException`과 `UnauthorizedAccess` 오류가 발생할 수 있습니다. 같은 터미널에서 `npm.cmd --version`이 성공하면 npm 설치가 아니라 PowerShell 실행 정책 문제입니다.

개발용 사용자 계정에서는 다음 명령으로 현재 사용자 범위에 `RemoteSigned` 정책을 적용합니다. 관리자 권한은 필요하지 않습니다.

```powershell
Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned
```

확인 질문에 동의한 뒤 PowerShell을 완전히 닫고 새로 열어 검증합니다.

```powershell
Get-ExecutionPolicy -Scope CurrentUser
npm --version
```

`RemoteSigned`는 로컬에서 작성된 스크립트의 실행을 허용하고, 인터넷에서 내려받은 PowerShell 스크립트에는 서명 요구를 적용합니다. 정책 변경을 원하지 않으면 동일한 npm CLI를 직접 실행하는 `npm.cmd`를 사용합니다.

```powershell
npm.cmd install
npm.cmd run type-check
npm.cmd run android
```

---

## 8. 관련 파일

| 경로 | 용도 |
|---|---|
| `app/mobile-react-native/.env.example` | 환경 변수 템플릿 |
| `app/mobile-react-native/.env` | 로컬 API URL (커밋하지 않음) |
| `app/mobile-react-native/run-android.ps1` | 에뮬레이터 실행 스크립트 (UTF-8 BOM) |
| `app/mobile-react-native/run-android.bat` | `run-android.ps1` 실행 래퍼 |
| `app/mobile-react-native/build-android-apk.ps1` | 실기기용 로컬 릴리스 APK 빌드·설치 스크립트 (UTF-8 BOM, §5.5) |
| `app/mobile-react-native/android/` | prebuild 생성 네이티브 프로젝트 (커밋하지 않음, `local.properties`에 `cmake.dir`) |
| `app/mobile-react-native/dist/` | 빌드한 APK (`dreamdiary-<버전>-release.apk`, 커밋하지 않음) |
| `gradle/mobile.gradle` | Gradle `mobile` 태스크 (`mobileAndroid`·`mobileApk`·`mobileApkInstall`·`mobileInstall`·`mobileTypeCheck`) |
| `app/mobile-react-native/README.md` | 패키지 개요 |
| `app/mobile-react-native/eas.json` | EAS 프로필 |
| `docs/migration/mobile/screen-spec.md` | 모바일 v0.1 제품·화면 계약과 구현 현황 |
| `docs/migration/mobile/sdk-upgrade.md` | Expo SDK 업그레이드 분석과 실행 기록 |
| `docs/migration/mobile/tagcloud-thread-port-plan.md` | 태그 클라우드·스레드 포팅 계획과 진행 현황 |

### Keep 본문 리치 텍스트 편집 의존성

모바일 본문 편집은 `react-native-pell-rich-editor`와 Expo SDK 57 호환 `react-native-webview`(13.16.1)를 사용한다. 에디터 HTML·JavaScript는 앱 번들에 포함된다. WebView 네이티브 모듈 추가를 반영한 APK를 다시 빌드·설치해야 한다. 기존 Android 프로젝트의 자동 연결은 Gradle 빌드에서 갱신되며 app.json 플러그인 변경은 필요하지 않다.

### Keep 서버 푸시 설정과 검증

앱은 서버 고정 리마인드와 Expo 푸시를 사용한다. 서버에 `V0.30.17__create_keep_reminder_push.sql`을 수동 적용하고 새 서버 코드를 실행해야 기기 API가 동작한다. 실제 전달에는 Expo 프로젝트 UUID, Android Firebase 클라이언트 `google-services.json`, 같은 Firebase 프로젝트의 FCM v1 발송 자격 증명을 Expo/EAS 프로젝트에 등록한 구성이 필요하다. FCM 서비스 계정 개인 키는 APK와 저장소에 포함하지 않는다.

로컬 APK 빌드에서 `-ExpoProjectId <UUID> -GoogleServicesJsonPath <로컬 google-services.json 경로> -CleanPrebuild`를 전달한다. `EXPO_PUBLIC_EXPO_PROJECT_ID`는 앱 번들과 Expo 설정에 들어가고 `GOOGLE_SERVICES_JSON_PATH`는 네이티브 prebuild에서 Android 클라이언트 설정을 지정한다. API 주소는 기존 `-ApiBaseUrl`로 지정한다. 설정을 준비하지 않은 앱은 ‘나’ 화면에서 푸시 준비 오류를 표시하며 Keep 기능은 사용할 수 있다.

검증은 실기기 로그인 → ‘나’의 수신 준비 확인 → 서버 등록 확인 → 앱을 닫은 상태에서 고정 슬롯 알림 수신 → 선택 후 최신 대상 목록 → 권한 변경 후 전경 복귀 → 로그아웃 해제 순서로 수행한다. Expo 접수·FCM 인계·실기기 표시는 각각 확인한다. 코드·단위 테스트·APK 빌드는 실제 전달 확인을 대신하지 않는다. 연결 오류는 서버 주소 접근부터 확인한다.


### APK 공개 다운로드 배포

`build-android-apk.ps1 -Publish`는 빌드·번들 주소 검증 후 공개 저장소 `nichefish/dreamdiary-app`의 `mobile-preview` 사전 릴리스에 APK를 게시한다. 현재 파일을 게시할 때는 `publish-android-apk.ps1 -ApkPath ./dist/dreamdiary-0.1.0-release.apk`를 실행한다. 인증은 Git Credential Manager의 github.com 자격을 사용한다. 토큰·서명 키·환경 파일은 게시 대상에 포함하지 않는다.

파일명은 앱 버전·APK 파일 시각(UTC)·SHA-256 접두사를 포함한다. 기존 파일을 덮어쓰지 않으며 같은 파일이 있으면 다운로드 검증으로 완료한다. 성공은 인증 없이 실제 APK를 내려받아 크기·SHA-256을 확인한 뒤 출력하는 HTTPS 주소로 판정한다. 사용자는 이 주소를 휴대폰 브라우저에서 열어 다운로드·업데이트 설치한다. APK는 공개되며 앱의 서버 주소·권한·접속 조건은 APK에 포함된 설정을 따른다. 릴리스는 로컬 작업트리 개발 APK의 배포 공간이다.
