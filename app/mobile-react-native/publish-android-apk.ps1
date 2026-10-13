<#
.SYNOPSIS
검증된 Android APK를 공개 GitHub 사전 릴리스에 게시하고 익명 다운로드 해시를 확인한다.
.DESCRIPTION
Git Credential Manager의 github.com 인증을 사용한다. 기존 자산을 덮어쓰지 않고
버전·파일 빌드 시각(UTC)·SHA-256 접두사를 파일명으로 사용한다. APK만 업로드한다.
#>
param(
    [Parameter(Mandatory = $true)][string]$ApkPath,
    [ValidatePattern('^[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+$')][string]$Repository = 'nichefish/dreamdiary-app'
)
$ErrorActionPreference = 'Stop'
$utf8 = New-Object System.Text.UTF8Encoding $false
[Console]::OutputEncoding = $utf8
$OutputEncoding = $utf8
Add-Type -AssemblyName System.Net.Http
Add-Type -AssemblyName System.IO.Compression.FileSystem

$apk = Get-Item -LiteralPath $ApkPath
if ($apk.PSIsContainer -or $apk.Extension -ne '.apk' -or $apk.Length -le 0) { throw '업로드할 APK 파일을 확인하세요.' }
$versionMatch = [regex]::Match($apk.Name, '^dreamdiary-([0-9]+\.[0-9]+\.[0-9]+)-release\.apk$')
if (-not $versionMatch.Success) { throw 'DreamDiary 버전별 릴리스 APK 파일을 지정하세요.' }
$zip = [IO.Compression.ZipFile]::OpenRead($apk.FullName)
try {
    if (-not $zip.GetEntry('AndroidManifest.xml') -or -not $zip.GetEntry('assets/index.android.bundle')) {
        throw 'Android manifest와 앱 번들이 있는 APK를 지정하세요.'
    }
} finally { $zip.Dispose() }
$hash = (Get-FileHash -LiteralPath $apk.FullName -Algorithm SHA256).Hash.ToLowerInvariant()
$stamp = $apk.LastWriteTimeUtc.ToString('yyyyMMddTHHmmssZ')
$assetName = 'dreamdiary-' + $versionMatch.Groups[1].Value + '-' + $stamp + '-' + $hash.Substring(0,12) + '.apk'
$api = 'https://api.github.com/repos/' + $Repository
$tag = 'mobile-preview'
$priorInteractive = $env:GCM_INTERACTIVE
$priorPrompt = $env:GIT_TERMINAL_PROMPT
$credentialLines = $null
$secretValue = $null
$uploadClient = $null
$downloadClient = $null
try {
    $env:GCM_INTERACTIVE = 'never'
    $env:GIT_TERMINAL_PROMPT = '0'
    $credentialLines = @('protocol=https', 'host=github.com', '') | git credential fill 2>$null
    if ($LASTEXITCODE -ne 0) { throw 'Git Credential Manager에서 github.com 인증을 확인하지 못했습니다.' }
    $passwordLine = @($credentialLines | Where-Object { $_.StartsWith('password=') })
    if ($passwordLine.Count -ne 1) { throw 'github.com 업로드 인증이 필요합니다.' }
    $secretValue = $passwordLine[0].Substring(9)
    $headers = @{ Accept = 'application/vnd.github+json'; Authorization = 'Bearer ' + $secretValue; 'X-GitHub-Api-Version' = '2022-11-28' }
    $repo = Invoke-RestMethod -Uri $api -Headers $headers -TimeoutSec 20
    if ($repo.private -or $repo.permissions.push -ne $true) { throw '승인된 공개 저장소의 업로드 권한을 확인하세요.' }

    $release = $null
    try {
        $release = Invoke-RestMethod -Uri ($api + '/releases/tags/' + $tag) -Headers $headers -TimeoutSec 20
    } catch {
        if (-not $_.Exception.Response -or [int]$_.Exception.Response.StatusCode -ne 404) { throw 'GitHub 릴리스 조회를 완료하지 못했습니다.' }
    }
    if (-not $release) {
        Write-Host '[apk-publish] 공개 모바일 사전 릴리스를 생성합니다.'
        $payload = @{ tag_name = $tag; target_commitish = $repo.default_branch; name = 'DreamDiary 모바일 개발 APK'; prerelease = $true; draft = $false;
            body = '로컬 작업트리에서 빌드한 개발용 APK입니다. 파일명은 앱 버전·빌드 파일 시각(UTC)·SHA-256 앞 12자리를 포함합니다. APK는 현재 파일의 작업트리 코드를 포함합니다. 알림·서버 연결의 동작은 해당 빌드의 앱 및 서버 설정을 따릅니다.' } | ConvertTo-Json
        $release = Invoke-RestMethod -Method Post -Uri ($api + '/releases') -Headers $headers -ContentType 'application/json; charset=utf-8' -Body $utf8.GetBytes($payload) -TimeoutSec 30
    }
    $asset = $null
    for ($page = 1; ; $page++) {
        $items = Invoke-RestMethod -Uri ($api + '/releases/' + $release.id + '/assets?per_page=100&page=' + $page) -Headers $headers -TimeoutSec 20
        foreach ($item in $items) { if ($item.name -eq $assetName) { $asset = $item; break } }
        if ($asset -or $items.Count -lt 100) { break }
    }
    if (-not $asset) {
        Write-Host ('[apk-publish] APK 업로드: ' + $assetName)
        $uploadClient = New-Object System.Net.Http.HttpClient
        $uploadClient.Timeout = [TimeSpan]::FromMinutes(10)
        $uploadClient.DefaultRequestHeaders.Authorization = New-Object System.Net.Http.Headers.AuthenticationHeaderValue('Bearer', $secretValue)
        $uploadClient.DefaultRequestHeaders.UserAgent.ParseAdd('DreamDiary-APK-Publisher')
        $uploadClient.DefaultRequestHeaders.Add('X-GitHub-Api-Version', '2022-11-28')
        $stream = [IO.File]::OpenRead($apk.FullName)
        $content = New-Object System.Net.Http.StreamContent($stream)
        $content.Headers.ContentType = New-Object System.Net.Http.Headers.MediaTypeHeaderValue('application/vnd.android.package-archive')
        try {
            $uri = 'https://uploads.github.com/repos/' + $Repository + '/releases/' + $release.id + '/assets?name=' + [Uri]::EscapeDataString($assetName)
            $response = $uploadClient.PostAsync($uri, $content).GetAwaiter().GetResult()
            try {
                if (-not $response.IsSuccessStatusCode) { throw ('GitHub APK 업로드 실패: HTTP ' + [int]$response.StatusCode) }
                $asset = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult() | ConvertFrom-Json
            } finally { $response.Dispose() }
        } finally { $content.Dispose(); $stream.Dispose() }
    } else { Write-Host '[apk-publish] 같은 빌드가 게시돼 있어 다운로드 검증을 수행합니다.' }
    if ($asset.size -ne $apk.Length -or $asset.state -ne 'uploaded') { throw '게시된 APK 크기와 업로드 상태가 일치하지 않습니다.' }
    $uri = [Uri]$asset.browser_download_url
    if ($uri.Scheme -ne 'https' -or $uri.Host -ne 'github.com' -or -not $uri.AbsolutePath.StartsWith('/' + $Repository + '/releases/download/')) {
        throw '다운로드 주소가 승인된 GitHub 저장소에 속하지 않습니다.'
    }
    Write-Host '[apk-publish] 인증 없는 실제 다운로드와 SHA-256을 확인합니다.'
    $downloadClient = New-Object System.Net.Http.HttpClient
    $downloadClient.Timeout = [TimeSpan]::FromMinutes(5)
    $downloadResponse = $downloadClient.GetAsync($uri, [System.Net.Http.HttpCompletionOption]::ResponseHeadersRead).GetAwaiter().GetResult()
    $downloadStream = $null
    $digest = [Security.Cryptography.SHA256]::Create()
    $cancel = New-Object System.Threading.CancellationTokenSource([TimeSpan]::FromMinutes(5))
    try {
        if (-not $downloadResponse.IsSuccessStatusCode) { throw ('APK 공개 다운로드 실패: HTTP ' + [int]$downloadResponse.StatusCode) }
        $downloadStream = $downloadResponse.Content.ReadAsStreamAsync().GetAwaiter().GetResult()
        $buffer = New-Object byte[] 65536
        $downloaded = [long]0
        while (($count = $downloadStream.ReadAsync($buffer, 0, $buffer.Length, $cancel.Token).GetAwaiter().GetResult()) -gt 0) {
            [void]$digest.TransformBlock($buffer, 0, $count, $buffer, 0)
            $downloaded += $count
        }
        [void]$digest.TransformFinalBlock([byte[]]@(), 0, 0)
        $remoteHash = ([BitConverter]::ToString($digest.Hash)).Replace('-', '').ToLowerInvariant()
        if ($remoteHash -ne $hash -or $downloaded -ne $apk.Length) { throw '공개 다운로드 파일의 SHA-256 또는 크기가 로컬 APK와 다릅니다.' }
    } finally {
        if ($downloadStream) { $downloadStream.Dispose() }
        $digest.Dispose(); $cancel.Dispose(); $downloadResponse.Dispose()
    }
    Write-Host '[apk-publish] 공개 다운로드 검증 완료.'
    [pscustomobject]@{ DownloadUrl = $uri.AbsoluteUri; Sha256 = $hash; Bytes = $apk.Length }
} finally {
    if ($uploadClient) { $uploadClient.Dispose() }
    if ($downloadClient) { $downloadClient.Dispose() }
    $secretValue = $null; $credentialLines = $null; $passwordLine = $null; $headers = $null
    $env:GCM_INTERACTIVE = $priorInteractive
    $env:GIT_TERMINAL_PROMPT = $priorPrompt
}