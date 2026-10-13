# DreamDiary Mobile — 실기기용 Android 릴리스 APK 로컬 빌드 (EAS 계정 없이)
# IntelliJ: Gradle > mobile > mobileApk (설치까지: mobileApkInstall)
# 또는: powershell -NoProfile -ExecutionPolicy Bypass -File build-android-apk.ps1 [-Install]
#
# - API 주소: PC 의 Tailscale IPv4 를 자동으로 찾아 http://<ip>:18081 을 쓴다(-ApiBaseUrl 로 지정 가능).
#   EXPO_PUBLIC_* 는 번들할 때 코드에 박힌다. 이 스크립트는 이 프로세스(와 자식 Gradle·Expo)의
#   환경 변수로만 넣는다. Expo(@expo/env)는 이미 있는 환경 변수를 .env 로 덮어쓰지 않으므로
#   에뮬레이터용 .env(10.0.2.2) 는 그대로 두고 쓸 수 있다.
# - android/ 가 없으면 npx expo prebuild 로 만든다(CNG, android/ 는 .gitignore).
# - 서명: Expo prebuild 템플릿의 release 는 debug keystore 로 서명된다(개인 사이드로드용).
# - 결과: dist/dreamdiary-<버전>-release.apk. 번들에 API 주소가 들어갔는지 확인한다.

param(
    # 예: http://100.70.88.5:18081  (생략 시 tailscale ip -4 로 자동)
    [string]$ApiBaseUrl,
    # 공개 GitHub mobile-preview 릴리스에 APK를 게시하고 실제 다운로드 해시를 검증한다.
    [switch]$Publish,
    # Expo 프로젝트 UUID와 Firebase 클라이언트 설정 파일. FCM 서비스 계정 키는 APK에 포함하지 않는다.
    [string]$ExpoProjectId,
    [string]$GoogleServicesJsonPath,
    # 빌드 후 USB 로 연결된 실기기(에뮬레이터 제외)에 adb install -r
    [switch]$Install,
    # 설치 대상 adb 시리얼(여러 대 연결 시, 또는 에뮬레이터에 설치할 때)
    [string]$Serial,
    # 네이티브 ABI. 폰(arm64-v8a) + 에뮬레이터(x86_64). "all" 이면 4종 모두(느림)
    [string]$Architectures = "arm64-v8a,x86_64",
    # android/ 를 지우고 다시 prebuild (app.json·플러그인을 바꾼 뒤)
    [switch]$CleanPrebuild
)

$ErrorActionPreference = "Stop"
$utf8 = New-Object System.Text.UTF8Encoding $false

# IntelliJ Gradle 콘솔(UTF-8)에서 한글이 깨지지 않도록 출력 인코딩을 UTF-8로 맞춘다.
[Console]::OutputEncoding = $utf8
$OutputEncoding = $utf8
& chcp.com 65001 > $null

# IDE가 Node 설치 전에 켜졌으면 PATH에 node가 없다. 레지스트리의 최신 PATH를 합치고,
# 그래도 없으면 기본 설치 경로를 앞에 붙인다.
$machinePath = [Environment]::GetEnvironmentVariable("PATH", "Machine")
$userPath = [Environment]::GetEnvironmentVariable("PATH", "User")
$env:PATH = (@($env:PATH, $machinePath, $userPath) | Where-Object { $_ }) -join ";"
if (-not (Get-Command node -ErrorAction SilentlyContinue)) {
    $defaultNodeDir = Join-Path $env:ProgramFiles "nodejs"
    if (Test-Path (Join-Path $defaultNodeDir "node.exe")) {
        $env:PATH = "$defaultNodeDir;$env:PATH"
    }
}
if (-not (Get-Command npx -ErrorAction SilentlyContinue)) {
    throw "npx not found. Node 22.13+ 를 설치하고 PATH 에 등록하세요 (Expo SDK 57)."
}

$mobileDir = $PSScriptRoot
Set-Location $mobileDir

function Write-Step([string]$message) { Write-Host "[apk] $message" }

# ─── 1. API 주소 (Tailscale) ─────────────────────────────
function Find-Tailscale {
    $cmd = Get-Command tailscale -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }
    $candidate = Join-Path $env:ProgramFiles "Tailscale\tailscale.exe"
    if (Test-Path $candidate) { return $candidate }
    return $null
}

if (-not $ApiBaseUrl) {
    $tailscale = Find-Tailscale
    if (-not $tailscale) {
        throw "Tailscale CLI 를 찾지 못했습니다. Tailscale 을 설치·로그인하거나 -ApiBaseUrl http://<PC-IP>:18081 로 지정하세요."
    }
    # EAP=Stop 에서 네이티브 stderr 리디렉션은 예외가 되므로(Windows PowerShell 5.1) try 로 감싼다.
    $ip = $null
    try {
        $ip = & $tailscale ip -4 2>$null | Where-Object { $_ -match '^\d+\.\d+\.\d+\.\d+$' } | Select-Object -First 1
    } catch {
        $ip = $null
    }
    if (-not $ip) {
        throw "tailscale ip -4 결과가 없습니다(로그아웃·중지 상태?). Tailscale 을 켜거나 -ApiBaseUrl 로 지정하세요."
    }
    $ApiBaseUrl = "http://$($ip.Trim()):18081"
}
$ApiBaseUrl = $ApiBaseUrl.Trim().TrimEnd("/")
if ($ApiBaseUrl -notmatch '^https?://[^/\s]+(:\d+)?$') {
    throw "ApiBaseUrl 형식이 올바르지 않습니다: $ApiBaseUrl (예: http://100.70.88.5:18081)"
}
if ($ApiBaseUrl -match '10\.0\.2\.2|localhost|127\.0\.0\.1') {
    Write-Step "WARN: $ApiBaseUrl 은 실기기에서 PC 백엔드에 닿지 않습니다."
}
# 이 프로세스와 자식(Gradle → expo export:embed)에만 적용. .env 파일은 건드리지 않는다.
$env:EXPO_PUBLIC_API_BASE_URL = $ApiBaseUrl
Write-Step "API base URL: $ApiBaseUrl"
if ($ExpoProjectId) {
    if ($ExpoProjectId -cnotmatch '^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$') {
        throw "ExpoProjectId 는 정규 UUID 형식이어야 합니다."
    }
    $env:EXPO_PUBLIC_EXPO_PROJECT_ID = $ExpoProjectId
}
if ($GoogleServicesJsonPath) {
    $resolvedGoogleServices = (Resolve-Path -LiteralPath $GoogleServicesJsonPath).Path
    if (-not (Test-Path -LiteralPath $resolvedGoogleServices -PathType Leaf)) { throw "GoogleServicesJsonPath 파일이 없습니다." }
    if ((Test-Path -LiteralPath (Join-Path $mobileDir 'android')) -and -not $CleanPrebuild) {
        throw "Firebase 네이티브 설정을 반영하려면 -CleanPrebuild 를 지정하세요."
    }
    $env:GOOGLE_SERVICES_JSON_PATH = $resolvedGoogleServices
}

# ─── 2. JDK 17+ / Android SDK ─────────────────────────────
function Get-JdkMajor([string]$javaHome) {
    if (-not $javaHome) { return 0 }
    $release = Join-Path $javaHome "release"
    if (-not (Test-Path (Join-Path $javaHome "bin\java.exe")) -or -not (Test-Path $release)) { return 0 }
    $line = Get-Content $release | Where-Object { $_ -match '^JAVA_VERSION=' } | Select-Object -First 1
    if ($line -match '"(?:1\.)?(\d+)') { return [int]$Matches[1] }
    return 0
}

$jdkCandidates = @($env:JAVA_HOME)
$jdksDir = Join-Path $env:USERPROFILE ".jdks"
if (Test-Path $jdksDir) {
    $jdkCandidates += Get-ChildItem $jdksDir -Directory | Sort-Object Name -Descending | ForEach-Object { $_.FullName }
}
$jdkCandidates += @(
    (Join-Path $env:ProgramFiles "Android\Android Studio\jbr")
)
foreach ($vendorDir in @("Eclipse Adoptium", "Microsoft", "Java")) {
    $root = Join-Path $env:ProgramFiles $vendorDir
    if (Test-Path $root) {
        $jdkCandidates += Get-ChildItem $root -Directory -Filter "jdk*" | Sort-Object Name -Descending | ForEach-Object { $_.FullName }
    }
}
# 17~24 를 우선(Gradle·AGP 검증 범위), 없으면 17 이상 아무거나
$javaHome = $jdkCandidates | Where-Object { $_ } | Where-Object { $m = Get-JdkMajor $_; $m -ge 17 -and $m -le 24 } | Select-Object -First 1
if (-not $javaHome) { $javaHome = $jdkCandidates | Where-Object { $_ } | Where-Object { (Get-JdkMajor $_) -ge 17 } | Select-Object -First 1 }
if (-not $javaHome) { throw "JDK 17 이상을 찾지 못했습니다. JAVA_HOME 을 JDK 17+ 로 설정하세요." }
$env:JAVA_HOME = $javaHome
Write-Step "JAVA_HOME: $javaHome (JDK $(Get-JdkMajor $javaHome))"

if (-not $env:ANDROID_HOME) {
    $sdk = @($env:ANDROID_SDK_ROOT, (Join-Path $env:LOCALAPPDATA "Android\Sdk")) | Where-Object { $_ -and (Test-Path $_) } | Select-Object -First 1
    if (-not $sdk) { throw "Android SDK 를 찾지 못했습니다. ANDROID_HOME 을 설정하세요(Android Studio SDK Manager)." }
    $env:ANDROID_HOME = $sdk
}
Write-Step "ANDROID_HOME: $env:ANDROID_HOME"

function Find-Adb {
    $cmd = Get-Command adb -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }
    $candidate = Join-Path $env:ANDROID_HOME "platform-tools\adb.exe"
    if (Test-Path $candidate) { return $candidate }
    return $null
}

# ─── 3. 네이티브 프로젝트 (prebuild) ─────────────────────
if (-not (Test-Path "node_modules")) {
    Write-Step "npm install ..."
    & npm install
    if ($LASTEXITCODE -ne 0) { throw "npm install 실패" }
}

$androidDir = Join-Path $mobileDir "android"
if ($CleanPrebuild -or -not (Test-Path (Join-Path $androidDir "gradlew.bat"))) {
    # prebuild 가 package.json scripts 를 바꾸지 않게 원본을 지켜 둔다(이 프로젝트는 Expo Go 개발용 scripts 유지).
    $packageJsonPath = Join-Path $mobileDir "package.json"
    $packageJsonBefore = [System.IO.File]::ReadAllBytes($packageJsonPath)
    $prebuildArgs = @("expo", "prebuild", "--platform", "android", "--no-install")
    if ($CleanPrebuild) { $prebuildArgs += "--clean" }
    Write-Step "npx $($prebuildArgs -join ' ') ..."
    $env:CI = "1"   # 대화형 질문 없이 진행
    & npx @prebuildArgs
    $prebuildExit = $LASTEXITCODE
    Remove-Item Env:CI
    $packageJsonAfter = [System.IO.File]::ReadAllBytes($packageJsonPath)
    if ([Convert]::ToBase64String($packageJsonBefore) -ne [Convert]::ToBase64String($packageJsonAfter)) {
        [System.IO.File]::WriteAllBytes($packageJsonPath, $packageJsonBefore)
        Write-Step "prebuild 가 바꾼 package.json 을 원래대로 되돌렸습니다."
    }
    if ($prebuildExit -ne 0) { throw "expo prebuild 실패 (exit $prebuildExit)" }
}

# ─── 3-1. Windows 경로 길이: ninja 1.12+ 가 든 SDK CMake 사용 ─────
# RN 새 아키텍처는 app 모듈에서 자동 연결 라이브러리의 C++ 를 CMake/ninja 로 빌드한다. 오브젝트 경로가
# "…/CMakeFiles/<target>.dir/C_/Dev/…/node_modules/…/X.cpp.o" 처럼 길어져 260자를 넘는데,
# AGP 기본 CMake 3.22.1 의 ninja 1.10.2 는 Windows 긴 경로(LongPathsEnabled)를 무시하고 실패한다
# ("Filename longer than 260 characters"). ninja 1.12+ 는 LongPathsEnabled=1 이면 긴 경로를 쓴다.
# 그래서 SDK 의 CMake 3.30+ 를 android/local.properties 의 cmake.dir 로 지정한다(없으면 sdkmanager 로 설치).
$longPaths = (Get-ItemProperty "HKLM:\SYSTEM\CurrentControlSet\Control\FileSystem" -Name LongPathsEnabled -ErrorAction SilentlyContinue).LongPathsEnabled
if ($longPaths -ne 1) {
    Write-Step "WARN: Windows 긴 경로가 꺼져 있습니다(LongPathsEnabled=0). C++ 빌드가 260자 제한으로 실패할 수 있습니다."
    Write-Step "      관리자 PowerShell: New-ItemProperty HKLM:\SYSTEM\CurrentControlSet\Control\FileSystem -Name LongPathsEnabled -Value 1 -PropertyType DWORD -Force"
}
$cmakeRoot = Join-Path $env:ANDROID_HOME "cmake"
$parsed = [version]"0.0"
$cmakeDir = $null
if (Test-Path $cmakeRoot) {
    $cmakeDir = Get-ChildItem $cmakeRoot -Directory |
        Where-Object { [version]::TryParse($_.Name, [ref]$parsed) -and ([version]$_.Name) -ge [version]"3.30" -and (Test-Path (Join-Path $_.FullName "bin\ninja.exe")) } |
        Sort-Object { [version]$_.Name } -Descending |
        Select-Object -First 1 -ExpandProperty FullName
}
if (-not $cmakeDir) {
    $sdkmanager = Join-Path $env:ANDROID_HOME "cmdline-tools\latest\bin\sdkmanager.bat"
    if (-not (Test-Path $sdkmanager)) {
        throw "SDK CMake 3.30+ 가 없고 sdkmanager 도 없습니다. Android Studio > SDK Manager > SDK Tools > CMake 3.31.6 을 설치하세요."
    }
    Write-Step "SDK CMake 3.31.6 설치 (sdkmanager) ..."
    & cmd /c "`"$sdkmanager`" --install `"cmake;3.31.6`""
    if ($LASTEXITCODE -ne 0) { throw "sdkmanager 로 cmake;3.31.6 설치 실패" }
    $cmakeDir = Join-Path $cmakeRoot "3.31.6"
}
$localProps = Join-Path $androidDir "local.properties"
$propLines = @()
if (Test-Path $localProps) {
    $propLines = @(Get-Content $localProps | Where-Object { $_ -notmatch '^\s*(cmake\.dir|sdk\.dir)\s*=' })
}
$cmakeDirProp = "cmake.dir=" + ($cmakeDir -replace '\\', '/')
$previousCmake = if (Test-Path $localProps) { Get-Content $localProps | Where-Object { $_ -match '^\s*cmake\.dir\s*=' } | Select-Object -First 1 } else { $null }
$propLines += @(("sdk.dir=" + ($env:ANDROID_HOME -replace '\\', '/')), $cmakeDirProp)
[System.IO.File]::WriteAllLines($localProps, [string[]]$propLines, $utf8)
if ($previousCmake -ne $cmakeDirProp) {
    # 다른 CMake 로 구성된 C++ 캐시는 지워야 새 CMake/ninja 로 다시 구성된다.
    $cxx = Join-Path $androidDir "app\.cxx"
    if (Test-Path $cxx) { Remove-Item -Recurse -Force $cxx }
}
Write-Step "CMake: $cmakeDir (android/local.properties cmake.dir)"

# ─── 4. Gradle assembleRelease ───────────────────────────
# Gradle 은 환경 변수를 입력으로 보지 않아 API 주소만 바꾸면 JS 번들 태스크가 UP-TO-DATE 로 건너뛰어진다.
# 번들 태스크는 매번 다시 실행한다(--rerun, Gradle 7.6+). 네이티브 빌드는 그대로 증분.
$gradleArgs = @(":app:createBundleReleaseJsAndAssets", "--rerun", "assembleRelease")
if ($Architectures -and $Architectures -ne "all") { $gradleArgs += "-PreactNativeArchitectures=$Architectures" }
Write-Step "gradlew $($gradleArgs -join ' ') (첫 빌드는 10~20분 이상 걸릴 수 있음) ..."
Push-Location $androidDir
try {
    & .\gradlew.bat @gradleArgs
    if ($LASTEXITCODE -ne 0) { throw "Gradle assembleRelease 실패 (exit $LASTEXITCODE)" }
} finally {
    Pop-Location
}

$builtApk = Join-Path $androidDir "app\build\outputs\apk\release\app-release.apk"
if (-not (Test-Path $builtApk)) { throw "APK 를 찾지 못했습니다: $builtApk" }

$appJson = [System.IO.File]::ReadAllText((Join-Path $mobileDir "app.json"), $utf8) | ConvertFrom-Json
$version = $appJson.expo.version
$distDir = Join-Path $mobileDir "dist"
New-Item -ItemType Directory -Force $distDir | Out-Null
$apk = Join-Path $distDir "dreamdiary-$version-release.apk"
Copy-Item $builtApk $apk -Force

# ─── 5. 번들에 API 주소가 들어갔는지 확인 ───────────────
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead($apk)
try {
    $entry = $zip.Entries | Where-Object { $_.FullName -eq "assets/index.android.bundle" } | Select-Object -First 1
    if (-not $entry) { throw "APK 에 assets/index.android.bundle 이 없습니다." }
    $stream = $entry.Open()
    $memory = New-Object System.IO.MemoryStream
    $stream.CopyTo($memory)
    $stream.Dispose()
    # Hermes 바이트코드의 ASCII 문자열은 그대로 들어 있으므로 Latin1 로 읽어 검색한다.
    $bundleText = [System.Text.Encoding]::GetEncoding(28591).GetString($memory.ToArray())
} finally {
    $zip.Dispose()
}
if (-not $bundleText.Contains($ApiBaseUrl)) {
    throw "번들에서 $ApiBaseUrl 을 찾지 못했습니다. Metro 캐시 문제일 수 있습니다: android\app\build 를 지우고 다시 빌드하세요."
}
if ($bundleText.Contains("10.0.2.2:18081")) {
    Write-Step "WARN: 번들에 에뮬레이터 주소(10.0.2.2:18081)도 들어 있습니다."
}
$sizeMb = [Math]::Round((Get-Item $apk).Length / 1MB, 1)
Write-Step "OK: $apk ($sizeMb MB), 번들 API 주소 확인: $ApiBaseUrl"

if ($Publish) {
    $published = & (Join-Path $mobileDir 'publish-android-apk.ps1') -ApkPath $apk
    Write-Step ("다운로드: " + $published.DownloadUrl)
    Write-Step ("SHA-256: " + $published.Sha256)
}

# ─── 6. (선택) USB 설치 ──────────────────────────────────
if ($Install) {
    $adb = Find-Adb
    if (-not $adb) { throw "adb 를 찾지 못했습니다 (ANDROID_HOME\platform-tools)." }
    $devices = & $adb devices | Select-String -Pattern "^(\S+)\s+device$" | ForEach-Object { $_.Matches[0].Groups[1].Value }
    if ($Serial) {
        $target = $Serial
    } else {
        $physical = @($devices | Where-Object { $_ -notmatch '^emulator-' })
        if ($physical.Count -eq 0) {
            throw "USB 로 연결된 기기가 없습니다. 폰에서 USB 디버깅을 켜고 'USB 디버깅 허용'을 승인한 뒤 다시 실행하세요 (adb devices 로 확인)."
        }
        if ($physical.Count -gt 1) { throw "기기가 여러 대입니다: $($physical -join ', '). -Serial 로 지정하세요." }
        $target = $physical[0]
    }
    Write-Step "adb -s $target install -r $apk ..."
    & $adb -s $target install -r $apk
    if ($LASTEXITCODE -ne 0) {
        throw "설치 실패. INSTALL_FAILED_UPDATE_INCOMPATIBLE 이면 서명이 다른 기존 앱을 폰에서 삭제한 뒤 다시 설치하세요."
    }
    Write-Step "설치 완료 ($target). 폰에서 Tailscale 을 켜고 DreamDiary 를 실행하세요."
}