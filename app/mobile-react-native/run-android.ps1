# DreamDiary Mobile — Android 에뮬레이터에서 Expo 실행
# IntelliJ: Gradle > mobile > mobileAndroid 우클릭 Run
# 또는 run-android.bat / 이 ps1 실행

$ErrorActionPreference = "Stop"
$utf8 = New-Object System.Text.UTF8Encoding $false

# IntelliJ Gradle 콘솔(UTF-8)에서 한글이 깨지지 않도록 출력 인코딩을 UTF-8로 맞춘다.
# (cmd 자식 프로세스의 오류 메시지도 UTF-8로 나오도록 코드 페이지를 65001로 바꾼다.)
[Console]::OutputEncoding = $utf8
$OutputEncoding = $utf8
& chcp.com 65001 > $null

# IDE가 Node 설치 전에 켜졌으면 PATH에 node가 없다. 레지스트리의 최신 PATH를 합치고,
# 그래도 없으면 기본 설치 경로를 앞에 붙인다. (expo.cmd 같은 bin 스크립트가 "node"를 PATH에서 찾음)
$machinePath = [Environment]::GetEnvironmentVariable("PATH", "Machine")
$userPath = [Environment]::GetEnvironmentVariable("PATH", "User")
$env:PATH = (@($env:PATH, $machinePath, $userPath) | Where-Object { $_ }) -join ";"
if (-not (Get-Command node -ErrorAction SilentlyContinue)) {
    $defaultNodeDir = Join-Path $env:ProgramFiles "nodejs"
    if (Test-Path (Join-Path $defaultNodeDir "node.exe")) {
        $env:PATH = "$defaultNodeDir;$env:PATH"
    }
}

$mobileDir = $PSScriptRoot
$workspaceRoot = Resolve-Path (Join-Path $mobileDir "..\..")

function Find-GradleNode {
    $nodejsRoot = Join-Path $workspaceRoot ".gradle\nodejs"
    if (-not (Test-Path $nodejsRoot)) { return $null }
    $dirs = Get-ChildItem -Path $nodejsRoot -Directory -Filter "node-v*" -ErrorAction SilentlyContinue |
        Sort-Object Name -Descending
    if (-not $dirs) { return $null }
    $nodeExe = Join-Path $dirs[0].FullName "node.exe"
    if (-not (Test-Path $nodeExe)) { return $null }
    $npmCli = Join-Path $dirs[0].FullName "node_modules\npm\bin\npm-cli.js"
    return @{ Node = $nodeExe; NpmCli = $npmCli }
}

function Invoke-Npm {
    param([string[]]$NpmArgs)
    $gradle = Find-GradleNode
    if (Get-Command npm -ErrorAction SilentlyContinue) {
        & npm @NpmArgs
        return
    }
    if ($null -eq $gradle) {
        throw "npm not found. Node 20.19+ PATH 등록 또는 루트에서: .\gradlew.bat npmSetup"
    }
    $env:PATH = "$(Split-Path $gradle.Node);$env:PATH"
    & $gradle.Node $gradle.NpmCli @NpmArgs
}

$envFile = Join-Path $mobileDir ".env"
$envExample = Join-Path $mobileDir ".env.example"
if (-not (Test-Path $envFile)) {
    if (-not (Test-Path $envExample)) { throw ".env.example not found" }
    $defaultEnv = "EXPO_PUBLIC_API_BASE_URL=http://10.0.2.2:18081`r`n"
    [System.IO.File]::WriteAllText($envFile, $defaultEnv, $utf8)
    Write-Host "[mobile] Created .env for Android emulator (10.0.2.2:18081)"
} else {
    $envText = [System.IO.File]::ReadAllText($envFile, $utf8)
    if ($envText -notmatch "10\.0\.2\.2") {
        Write-Host "[mobile] WARN: .env may not target emulator. Android AVD expects http://10.0.2.2:18081"
    }
}

Set-Location $mobileDir
if (-not (Test-Path "node_modules")) {
    Write-Host "[mobile] npm install ..."
    Invoke-Npm @("install")
}

Write-Host "[mobile] Starting Expo on Android (AVD 실행 + backend localhost:18081 확인) ..."
# Gradle/IDE 실행은 비대화형이라 Expo가 "다른 포트 쓸까요?"를 물으면 그대로 멈춘다.
# 8081부터 비어 있는 포트를 골라 명시적으로 넘긴다. (8081은 다른 Java 앱이 쓰는 경우가 있음)
$metroPort = 8081
while (Get-NetTCPConnection -LocalPort $metroPort -State Listen -ErrorAction SilentlyContinue) { $metroPort++ }
Write-Host "[mobile] Metro port: $metroPort"

# 에뮬레이터가 PC의 LAN IP(예: 223.x.x.x:8082)로 Metro에 붙으면 IP 변경·방화벽 등으로 연결이 조용히 끊겨
# 옛 번들이 계속 보이는 문제가 있었다. --localhost 로 exp://127.0.0.1:<port> 를 쓰게 하고,
# adb reverse 로 에뮬레이터의 localhost:<port> 를 PC Metro로 직접 연결한다. (Expo도 열 때 reverse 하지만,
# 이미 켜져 있는 기기에는 미리 걸어 둔다.) 백엔드는 .env 의 10.0.2.2:18081 그대로라 reverse 불필요.
function Find-Adb {
    $cmd = Get-Command adb -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }
    foreach ($root in @($env:ANDROID_HOME, $env:ANDROID_SDK_ROOT, (Join-Path $env:LOCALAPPDATA "Android\Sdk"))) {
        if (-not $root) { continue }
        $candidate = Join-Path $root "platform-tools\adb.exe"
        if (Test-Path $candidate) { return $candidate }
    }
    return $null
}
$adb = Find-Adb
if ($adb) {
    $devices = & $adb devices | Select-String -Pattern "^(\S+)\s+device$" | ForEach-Object { $_.Matches[0].Groups[1].Value }
    foreach ($serial in $devices) {
        & $adb -s $serial reverse "tcp:$metroPort" "tcp:$metroPort" | Out-Null
        if ($LASTEXITCODE -eq 0) { Write-Host "[mobile] adb reverse tcp:$metroPort -> $serial" }
        else { Write-Host "[mobile] WARN: adb reverse 실패 ($serial)" }
    }
    if (-not $devices) { Write-Host "[mobile] 실행 중인 기기 없음 — Expo가 AVD를 띄운 뒤 reverse를 건다" }
} else {
    Write-Host "[mobile] WARN: adb를 찾지 못해 reverse 생략 (ANDROID_HOME 확인)"
}

Invoke-Npm @("run", "android", "--", "--port", "$metroPort", "--localhost")