# 모바일 앱 실행 가이드 (LTE + Tailscale)

폰을 **LTE(셀룰러)** 로만 쓰고, PC 백엔드에 **Tailscale**로 붙여 DreamDiary 모바일 앱을 띄우는 절차입니다.  
모바일 v0.1 제품·화면 계약은 [`screen-spec.md`](./screen-spec.md)를 참고하세요.

---

## 1. 사전 준비

| 항목 | 설명 |
|---|---|
| Tailscale | PC·폰 **같은 계정**으로 설치·로그인, 둘 다 **Connected** |
| 백엔드 | Spring Boot API가 PC에서 **8080** 포트로 동작 중 |
| `.env` | `app/mobile-react-native/.env` (`.env.example` 복사 후 수정). **git에 커밋하지 않음** |
| Node.js | **22.13.x 이상, Node 22 LTS** 권장. 선택 근거는 §1.1, PATH에 `npm`이 없으면 §4 Gradle Node 참고 |
| 인증 | REST: 쿠키. WebSocket: login/refresh의 `Authorization: Bearer` → SecureStore (`src/auth/accessToken.ts`) |

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

`node --version` 결과가 `v22.13.0` 이상인지 확인합니다. 저장소의 Gradle Node 설정 `20.11.1`은 Expo SDK 57 최소 요구보다 낮으므로 모바일 실행에는 PATH의 Node 22를 우선 사용합니다. Gradle Node 버전 정합은 별도 설정 변경으로 다룹니다.

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
EXPO_PUBLIC_API_BASE_URL=http://100.70.88.5:8080
```

> `100.70.88.5`는 예시입니다. §2.1에서 확인한 **본인 PC Tailscale IP**로 바꿉니다.  
> 반드시 **`:8080`** 을 붙입니다 (`http://100.x.x.x/` 만 쓰면 80번 포트로 접속 시도).

### 2.3 접속 전 체크 (폰 브라우저)

1. 폰: **LTE** + Tailscale **Connected**
2. PC: 백엔드 실행 → `http://localhost:8080` 확인
3. 폰 Chrome 등에서:

```text
http://<PC-Tailscale-IP>:8080
```

페이지가 열리면 앱도 같은 URL로 API 호출 가능합니다.

---

## 3. 백엔드 · 방화벽

### 3.1 백엔드 실행

루트 또는 백엔드 모듈에서 Spring Boot를 기동합니다.  
PC 브라우저: `http://localhost:8080`

### 3.2 Windows 방화벽

폰(Tailscale 경유) → PC **8080** 인바운드를 허용해야 하는 경우가 많습니다.

PowerShell **관리자**:

```powershell
New-NetFirewallRule -DisplayName "DreamDiary 8080 Tailscale" -Direction Inbound -Protocol TCP -LocalPort 8080 -Action Allow -Profile Private
```

Tailscale만 켠다고 방화벽이 자동으로 열리지는 않습니다.

**주의:** `http://localhost:8080`은 PC에서만 유효합니다.

---

## 4. 의존성 · 타입체크

```bash
cd app/mobile-react-native
npm install
npm run type-check
```

### PATH에 npm이 없을 때 (Gradle Node)

```powershell
$node = "C:\Dev\projects\dreamdiary\workspace\.gradle\nodejs\node-v20.11.1-win-x64\node.exe"
$npmCli = "C:\Dev\projects\dreamdiary\workspace\.gradle\nodejs\node-v20.11.1-win-x64\node_modules\npm\bin\npm-cli.js"
Set-Location C:\Dev\projects\dreamdiary\workspace\app\mobile-react-native
& $node $npmCli install
& $node $npmCli run type-check
```

(경로·Node 버전은 환경에 맞게 조정. RN 권장 Node보다 낮으면 `EBADENGINE` 경고가 날 수 있음.)

---

## 5. 앱 띄우기

### 5.1 EAS Preview APK (LTE 실기기 **권장**)

API URL이 빌드에 포함되므로 LTE에서 가장 단순합니다.

```bash
cd app/mobile-react-native
npm install
npx eas-cli login
npx eas-cli build --platform android --profile preview
```

1. 빌드 **전** `.env`에 Tailscale IP + `:8080` 설정
2. EAS 빌드 완료 → APK를 폰(LTE)에 설치
3. 폰 Tailscale Connected + 백엔드·방화벽 준비 후 실행

`eas.json`의 `preview`: Android **APK**, internal 배포.

### 5.2 Expo Go (선택, 설정 부담 큼)

```bash
cd app/mobile-react-native
npm run start
```

- QR을 Expo Go로 스캔
- LTE에서는 **Metro 번들 서버(PC)** 까지 Tailscale로 접근 가능해야 함
- `.env` 변경 후 Metro **재시작**

문제가 잦으면 §5.1 APK를 사용하세요.

### 5.3 로컬 네이티브 실행 (선택)

```bash
npx expo run:android
```

---

## 6. v0.1 계약 스모크 테스트 체크리스트

- [ ] 폰 LTE + Tailscale Connected
- [ ] 로그인 / 앱 재시작 후 세션 유지
- [ ] 일별 조회 → 상세 읽기
- [ ] 주별 조회 → 상세 읽기
- [ ] 월별 조회 → 상세 읽기
- [ ] 키워드 검색 → 상세 읽기
- [ ] 태그 검색 → 상세 읽기
- [ ] 날짜·검색·상세 흐름에 저널 생성·수정·삭제 조작이 노출되지 않음

---

## 7. 자주 나는 문제

| 증상 | 확인 |
|---|---|
| 네트워크 오류 | `.env`가 Tailscale `100.x.x.x:8080` 인지, `:8080` 누락 여부 |
| 폰 Tailscale offline | 앱에서 Connected, PC와 같은 계정 |
| PC만 되고 폰만 안 됨 | 방화벽 8080, 폰 브라우저로 `http://<Tailscale-IP>:8080` 선행 테스트 |
| 백엔드 미기동 | PC `localhost:8080`, `netstat` 등으로 8080 리스닝 확인 |
| AI 채팅만 실패 | 로그인·refresh 후 토큰, 앱 재시작 |
| Expo Go만 실패 | preview APK 사용 권장 |

---

## 8. 관련 파일

| 경로 | 용도 |
|---|---|
| `app/mobile-react-native/.env.example` | 환경 변수 템플릿 |
| `app/mobile-react-native/README.md` | 패키지 개요 |
| `app/mobile-react-native/eas.json` | EAS 프로필 |
| `docs/migration/mobile/screen-spec.md` | 모바일 v0.1 제품·화면 계약과 구현 현황 |
