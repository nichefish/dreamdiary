# 모바일 앱 실행 가이드 (Android 에뮬레이터 · LTE + Tailscale)

PC의 **Android 에뮬레이터(AVD)** 에서 DreamDiary 모바일 앱을 띄우는 로컬 개발 절차(§5.1)와, 폰을 **LTE(셀룰러)** 로만 쓰고 PC 백엔드에 **Tailscale**로 붙여 띄우는 절차를 다룹니다.  
모바일 v0.1 제품·화면 계약은 [`screen-spec.md`](./screen-spec.md)를 참고하세요.

---

## 1. 사전 준비

| 항목 | 설명 |
|---|---|
| Tailscale | PC·폰 **같은 계정**으로 설치·로그인, 둘 다 **Connected** |
| 백엔드 | Spring Boot API가 PC에서 **18081** 포트로 동작 중 (`local`·`dev` 프로필 기본값 `${SERVER_PORT:18081}`) |
| `.env` | `app/mobile-react-native/.env`. 에뮬레이터는 `EXPO_PUBLIC_API_BASE_URL=http://10.0.2.2:18081`(§5.1, 없으면 `run-android.ps1`이 생성), LTE 실기기는 Tailscale IP(§2.2). **git에 커밋하지 않음** |
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
  - Expo Go에는 `expo-clipboard` 네이티브 모듈이 포함되어 있어 Expo Go 재설치는 필요 없습니다. EAS Preview APK(§5.2)·로컬 네이티브 빌드(§5.4)는 네이티브 모듈이 추가되었으므로 새로 빌드해야 복사가 동작합니다.
  - 공유는 React Native 내장 `Share` API라 추가 패키지가 없습니다.
- Expo CLI가 `expo` `57.0.19` → `57.0.26` 업데이트를 제안하지만 이번에는 보류했습니다. 후속 작업에서 `npx expo install --fix`로 SDK 57 기대 버전에 맞춥니다([`sdk-upgrade.md`](./sdk-upgrade.md) 후속 기록).

### 4.1 Gradle 태스크 (`gradle/mobile.gradle`)

IntelliJ Gradle 창의 `mobile` 그룹에서 실행하거나 루트에서 `.\gradlew.bat <태스크>`로 실행합니다.

| 태스크 | 실행 내용 |
|---|---|
| `mobileAndroid` | Windows: `powershell -NoProfile -ExecutionPolicy Bypass -File app\mobile-react-native\run-android.ps1` (§5.1). 그 외 OS는 안내 메시지와 함께 실패 |
| `mobileInstall` | `app/mobile-react-native`에서 `npm install` |
| `mobileTypeCheck` | `mobileInstall` 후 `npm run type-check` (`tsc --noEmit`) |

- 세 태스크는 모두 **`Exec` 타입**입니다. 저장소가 configuration cache(`gradle.properties`의 `org.gradle.configuration-cache=true`)를 켜 두었으므로, 실행 단계에서 스크립트 메서드를 호출하던 `doLast { exec {} }` 방식 대신 경로와 명령을 구성 단계에서 계산하는 `Exec` 태스크로 바꿨습니다.
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
7. `npm run android -- --port <포트> --localhost`(`expo start --android --port <포트> --localhost`)를 실행합니다. `--localhost`로 Expo Go가 `exp://127.0.0.1:<포트>`로 접속하므로, PC LAN IP가 바뀌거나 방화벽이 끼어도 Metro 연결이 끊기지 않습니다.

> **왜 localhost + adb reverse인가 (2026-10-06):** 에뮬레이터가 PC LAN IP(예: `223.x.x.x:8082`)로 Metro에 붙어 있다가 연결이 조용히 끊겨, 코드를 고쳐도 에뮬레이터에 옛 번들이 계속 보이는 문제가 있었습니다. 백엔드 주소(`.env`의 `10.0.2.2:18081`)는 그대로이며 reverse 대상이 아닙니다. 에뮬레이터를 재시작했거나 연결이 끊겼으면 스크립트를 다시 실행하거나 `adb reverse tcp:<포트> tcp:<포트>` 후 Expo Go에서 `exp://127.0.0.1:<포트>`를 다시 엽니다. LTE 실기기(Tailscale)는 USB 연결이 아니므로 이 방식 대신 §2.2를 따릅니다.

> **인코딩 주의:** `run-android.ps1`은 **UTF-8(BOM 포함)** 으로 저장합니다. Gradle과 `run-android.bat`이 호출하는 Windows PowerShell 5.1(`powershell.exe`)은 BOM 없는 스크립트를 시스템 ANSI 코드 페이지(한국어 Windows는 CP949)로 읽으므로 한글 문자열이 깨집니다. 편집기에서 다시 저장할 때 BOM이 빠지지 않게 합니다.

#### Expo Go 설치

- `expo start --android`가 에뮬레이터에 Expo Go를 adb로 자동 설치하고 실행합니다. 현재 에뮬레이터에는 Expo Go `57.0.9`가 설치되어 있습니다.
- 설치 버전 확인:

```powershell
adb shell dumpsys package host.exp.exponent | Select-String versionName
```

- 자동 설치가 실패하면 수동으로 설치합니다. [Expo Go 페이지](https://expo.dev/go)에서 SDK 57용 Android APK를 내려받아 `adb install -r <APK 경로>`로 설치한 뒤 `mobileAndroid`를 다시 실행합니다. Google Play 시스템 이미지의 Play 스토어 Expo Go는 최신 SDK만 지원하므로, SDK 57이 최신일 때만 대안이 됩니다.

### 5.2 EAS Preview APK (LTE 실기기 **권장**)

API URL이 빌드에 포함되므로 LTE에서 가장 단순합니다.

```bash
cd app/mobile-react-native
npm install
npx eas-cli login
npx eas-cli build --platform android --profile preview
```

1. 빌드 **전** `.env`에 Tailscale IP + `:18081` 설정
2. EAS 빌드 완료 → APK를 폰(LTE)에 설치
3. 폰 Tailscale Connected + 백엔드·방화벽 준비 후 실행

`eas.json`의 `preview`: Android **APK**, internal 배포.

### 5.3 Expo Go — LTE 실기기 (선택, 설정 부담 큼)

```bash
cd app/mobile-react-native
npm run start
```

- QR을 Expo Go로 스캔
- LTE에서는 **Metro 번들 서버(PC)** 까지 Tailscale로 접근 가능해야 함
- `.env` 변경 후 Metro **재시작**

문제가 잦으면 §5.2 APK를 사용하세요.

### 5.4 로컬 네이티브 실행 (선택)

```bash
npx expo run:android
```

---

## 6. v0.1 계약 스모크 테스트 체크리스트

- [ ] (에뮬레이터) `mobileAndroid` 실행 → Expo Go에서 앱 로드, Gradle 콘솔 한글 정상
- [ ] (LTE 실기기) 폰 LTE + Tailscale Connected
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
| Expo Go만 실패 | preview APK 사용 권장 |
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
| `gradle/mobile.gradle` | Gradle `mobile` 태스크 (`mobileAndroid`·`mobileInstall`·`mobileTypeCheck`) |
| `app/mobile-react-native/README.md` | 패키지 개요 |
| `app/mobile-react-native/eas.json` | EAS 프로필 |
| `docs/migration/mobile/screen-spec.md` | 모바일 v0.1 제품·화면 계약과 구현 현황 |
| `docs/migration/mobile/sdk-upgrade.md` | Expo SDK 업그레이드 분석과 실행 기록 |
| `docs/migration/mobile/tagcloud-thread-port-plan.md` | 태그 클라우드·스레드 포팅 계획과 진행 현황 |
