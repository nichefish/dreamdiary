# Jira 로컬 적재 작업 계약

이 계약의 신뢰 판정은 [로컬 적재 신뢰 판정 공통 계약](LOCAL_INGESTION_TRUST_CONTRACT.md)을 따른다.

## 1. 명령 해석

사용자가 **Jira 프로젝트 키와 이슈 번호 범위 및 `적재해`, `로컬에 적재해`, `쌓아줘`, `가보자고`처럼 적재 의사를 명시하면**, 해당 요청은 아래 정형 작업 전체에 대한 실행 승인이다.

- Atlassian API/커넥터로 지정 범위의 이슈를 읽는다.
- 이슈 본문, 댓글, 상태 전이를 로컬 Markdown 파일로 저장한다.
- 해당 프로젝트 미러의 `index.json` 메타데이터를 동기화한다.
- 아래 검증 절차까지 완료한다.

프로젝트 키와 범위가 명확한 정형 요청에는 `AGENTS.md`의 일반적인 수정 직전 승인 질문을 다시 적용하지 않는다. 대상 프로젝트, 범위 또는 저장 위치를 하나로 확정할 수 없는 경우에만 질문한다.

### 1.1 `최신까지 적재`의 의미

`최신 이슈까지 적재`, `최신까지 받고 업데이트`, `업데이트된 것까지 훑어`처럼 최신 상태를 요구하는 명령은 신규 이슈 번호 확장만을 뜻하지 않는다. 다음 작업을 모두 완료해야 최신 동기화로 인정한다.

1. 프로젝트의 원격 이슈 전체를 첫 페이지부터 마지막 페이지까지 조회한다.
2. 원격 이슈 전체의 키와 `updated`를 로컬 인덱스의 각 이슈와 개별 대조한다.
3. 신규 키를 새로 적재한다.
4. `updated`가 달라진 기존 이슈는 본문·댓글·상태 전이를 다시 조회해 안전 규칙에 따라 갱신한다.
5. 원격 전체 목록에서 보이지 않는 기존 이슈는 삭제하지 않고 보존한다.
6. 전체 페이지 조회, 신규 적재, 변경 이슈 갱신과 검증 결과를 원자 관측값으로 판정한다. 기존 `collectedAt`은 갱신하지 않는다.

최대 이슈 번호가 같다는 사실은 최신 상태의 근거가 아니다. 신규 키가 없어도 기존 이슈의 댓글·본문·상태가 바뀔 수 있으므로 전체 원격 `updated` 대조를 생략하지 않는다. 사용자가 `신규 이슈만`이라고 명시한 경우에만 기존 이슈 갱신 검사를 생략한다.

### 1.2 최신 동기화의 필수 원자 명제

최신 동기화는 다음 명제가 모두 `TRUE`일 때만 `VERIFIED`다.

```text
scopeResolved                 = 프로젝트 키와 전체 프로젝트 JQL이 하나로 확정됨
paginationExhausted           = 첫 페이지부터 cursor 연속성을 유지해 종료조건을 관측함
paginationFailureCount        = 0
duplicateIdentityCount        = 0
discoveredButFetchFailedCount = 0
unresolvedTruncationCount     = 0
reconciliationUnknownCount    = 0
localInvariantFailureCount    = 0
```

각 값의 필수 관측값이 없으면 `UNKNOWN`이다. 최대 이슈 번호, 실행 시각, 조회한 페이지 수 또는 발견 건수만으로 위 명제를 대신하지 않는다.

기존 기준선의 생성 과정이 위 명제를 충족했다는 감사 증거가 없으면 모든 기존 이슈를 `UNVERIFIED`로 취급한다. 최초 신뢰 기준선을 만들 때는 원격 전체 목록에서 발견한 모든 이슈의 필수 상세 응답을 다시 조회한다. 이 최초 감사에서는 로컬 `updatedAt`과 원격 `updated`가 같다는 이유로 상세 조회를 생략하지 않는다.

## 2. 기본 저장 위치와 파일명

1. 기본 저장 위치는 `daily/text/raw/jira/{PROJECT_KEY}/`이다.
2. 이슈별 파일명은 `{ISSUE_KEY}__{안전한 원격 summary}.md` 형식을 사용한다.
3. Windows 파일명에 사용할 수 없는 문자는 정보 손실이 가장 적은 전각 문자 또는 `_`로 치환한다.
4. 파일명 끝의 마침표와 공백은 제거하고, 과도하게 긴 파일명은 이슈 키를 보존한 채 summary 부분만 줄인다.
5. 같은 이슈 키의 기존 파일이 있으면 자동으로 덮어쓰지 않는다. 기존 적재 갱신을 사용자가 명시하지 않은 경우 충돌로 보고하고 해당 범위의 쓰기를 중단한다.

### 2.1 재개 가능한 감사 ledger

전체 감사의 영속 관측값은 `daily/text/raw/jira/{PROJECT_KEY}/.audit/{AUDIT_ID}/`에 저장한다. 이 디렉터리는 임시 전달 파일이나 별도 보고서가 아니라 중단된 감사를 같은 원격 발견 집합에서 재개하고 신뢰 상태를 재계산하기 위한 정식 적재 자산이다.

```text
.audit/{AUDIT_ID}/
├─ manifest.json
├─ plan.json
├─ staged/
│  ├─ index.json
│  └─ {ISSUE_KEY}__{제목}.md
└─ issues/
   └─ {ISSUE_KEY}.json
```

- `AUDIT_ID`는 감사 시작 UTC 시각을 파일명에 안전한 기본 형식으로 사용한다.
- `manifest.json`은 프로젝트 키, JQL, 각 목록 페이지의 요청 cursor·응답 end cursor·`hasNextPage`·반환 key 목록과 발견한 전체 key·`updated` 집합을 기록한다.
- `issues/{ISSUE_KEY}.json`은 해당 상세 응답의 원격 snapshot과 요청 key를 보존한다. 댓글과 changelog의 `total`, 실제 반환 배열과 원격 본문 필드를 포함한다.
- `plan.json`은 전체 discovery와 실행 직전 로컬 인덱스·파일에서 결정론적으로 계산한 신규·변경·무변경·원격 부재 key, 필수 상세 조회 key, 재사용 snapshot과 로컬 회귀 기준선의 hash·identity를 기록한다.
- `staged/`는 reconciliation 결과를 검증하기 위한 정식 후보 미러다. 실제 프로젝트 미러는 staged 검증이 `VERIFIED`가 된 뒤에만 승격 결과를 받는다.
- 같은 감사에서 이미 존재하는 이슈 snapshot은 요청 key, 응답 key와 원격 `updated`가 모두 같고 파일 SHA-256이 검증될 때만 재사용한다.
- payload가 없거나 key가 다른 응답은 정상 snapshot 파일로 만들지 않는다. 다음 실행에서 해당 key를 다시 조회한다.
- 부분 감사 ledger는 `index.json`, 이슈 Markdown 또는 `collectedAt`을 신뢰 상태로 승격하지 않는다.
- 다음 실행은 `manifest.json`의 발견 key 중 검증된 snapshot 파일이 없는 key만 상세 조회한다.
- 목록 발견 집합이 달라지면 기존 감사에 섞지 않고 새 `AUDIT_ID`로 시작한다.
- 감사가 `VERIFIED`로 계산된 뒤에도 ledger는 해당 판정을 재현하는 증거로 보존한다.

감사 상태는 `python scripts/verify_jira_audit.py <감사 디렉터리> <감사 디렉터리>/staged`가 원자 관측값과 후보 `index.json`·Markdown에서 계산한다. ledger에 사람이 작성한 `SUCCESS` 또는 `VERIFIED` 필드를 두지 않으며, ledger의 자체 성공 표식을 로컬 불변식의 증거로 사용하지 않는다.

### 2.2 소스 관리되는 실행 경로

감사 자료의 생성·정규화·후보 계산·snapshot 재사용은 `scripts/jira_audit_pipeline.py`만 수행한다.

- `init`: schema version과 범위가 있는 `manifest.json`을 생성한다.
- `record-page`: 커넥터 원문 응답에서 cursor 연속성, 종료조건, key와 `updated`를 추출한다.
- `plan`: manifest와 실행 직전 로컬 미러를 비교해 후보 집합과 회귀 기준선을 `plan.json`으로 계산한다.
- `reuse`: plan에 기록된 개별 snapshot의 source hash를 다시 계산하고 일치한 파일만 복사한다.
- `record-issue`: 상세 응답의 요청 key·응답 key를 확인하고 댓글·changelog 관측값과 원격 snapshot을 정규화한다.
- `accept-status-limit`: 댓글 snapshot이 완전하고 changelog 제한 및 기존 로컬 상태 이력이 확인된 이슈에만 기존 상태 이력 보존 승인을 기록한다.
- `stage`: 실행 전 index와 Markdown hash를 재검증한 후 `staged/` 후보 미러를 생성하고 reconciliation을 적용한다.
- `promote`: `staged/`를 다시 검증하고 실행 전 기준선이 그대로일 때만 실제 미러에 승격한다.

`record-page`와 `record-issue`의 payload 경로에 `-`를 주면 한 줄 UTF-8 JSON을 표준입력으로 받는다. 원문 전달용 임시 파일을 만들지 않는다. AI나 사용자는 manifest의 발견 집합, 후보 집합, snapshot 재사용 개수, reconciliation 결과를 직접 계산해 기록하지 않는다. 커넥터 호출과 원문 응답 전달은 실행 경계이며, 그 응답에서 ledger를 만드는 의미 변환은 위 스크립트가 담당한다.

실제 미러 쓰기는 `promote` 명령 하나로 제한한다. 이 명령은 staged 감사의 모든 hard gate가 `TRUE`인지 확인하고, `plan.json`에 기록된 실행 전 index 및 Markdown hash가 현재 실제 미러와 일치하는지 다시 확인한다. 어느 조건이든 충족되지 않으면 실제 미러를 수정하지 않는다.

## 3. Jira 조회 계약

- Atlassian 커넥터의 Jira 이슈 조회를 사용한다.
- 최신 동기화에서는 JQL로 프로젝트 전체 이슈의 키와 `updated`를 페이지 끝까지 조회한다. 첫 페이지만 조회하거나 최신 번호 몇 건만 조회한 결과를 전체 최신 상태로 간주하지 않는다.
- 프로젝트 전체 목록 조회가 중단되거나 페이지 누락 여부를 확인할 수 없으면 `paginationExhausted`를 `UNKNOWN`으로 판정하고 `collectedAt`을 변경하지 않는다.
- 기존 이슈의 변경 후보 판정은 전역 수집 시각이 아니라 각 인덱스 항목의 `updatedAt`과 원격 `updated`를 비교해 수행한다. 과거 `collectedAt`은 변경 후보를 제외하는 필터로 사용하지 않는다.
- 이슈 필드는 summary, description, status, issuetype, priority, labels, components, assignee, reporter, created, updated, resolution, project, parent, comment를 요청한다.
- changelog를 함께 요청하고 `field == status`인 항목만 상태 전이로 보존한다.
- 댓글은 생성 시각 오름차순, 상태 전이는 전이 시각 오름차순으로 저장한다.
- 각 응답의 `key`가 요청한 이슈 키와 정확히 같은지 파일 작성 전에 검증한다.
- 요청 키와 응답 키가 다르면 이동·리다이렉트된 다른 프로젝트 이슈로 간주한다. 응답으로 반환된 다른 키의 이슈는 저장하지 않고 원래 요청 키를 조회 불가로 처리한다.
- API가 이슈 없음과 조회 권한 없음을 구분하지 않으면 해당 키를 `존재하지 않거나 보이지 않음`으로 취급한다.
- 존재하지 않거나 보이지 않는 키에는 빈 파일이나 placeholder 파일을 만들지 않는다.
- 이미 적재된 이슈가 원격에서 삭제되거나 조회할 수 없는 상태가 되면 해당 이슈의 로컬 Markdown과 인덱스 항목을 보존한다. 원격 삭제·조회 불가는 기존 적재 이슈의 변경 동기화 대상으로 취급하지 않는다.
- 이미 적재된 이슈를 갱신할 때 본문·댓글·상태 이력이 비어 있는 응답으로 기존 로컬 원문을 축소하거나 덮어쓰지 않는다. 기존 기록보다 내용이 줄어드는 응답은 동기화하지 않고 완료 보고에 해당 키와 보존 사유를 명시한다.
- 기존 적재 파일의 댓글 ID 집합과 새 Jira 응답의 댓글 ID 집합을 비교한다. 기존 댓글 ID가 새 응답에서 하나라도 사라지면 댓글 수가 같거나 증가했더라도 해당 이슈의 갱신을 중단하고 기존 Markdown과 인덱스 항목을 그대로 보존한다.
- Jira `changelog`는 댓글 작성·수정·삭제 이력을 보장하는 원천으로 사용하지 않는다. 사라진 댓글 ID는 삭제로 확정하지 않고 `원격 응답에서 사라짐`으로 보고하며, 해당 댓글의 기존 로컬 원문에 삭제 표식이나 추정 내용을 추가하지 않는다.
- 응답이 제공하는 `total`과 반환 배열 길이를 비교하여 페이지 제한 여부를 확인한다.
- 커넥터가 다음 페이지 조회를 지원하지 않는 changelog 제한이 발견되면 반환된 범위를 그대로 보존하고, 기존 로컬 상태 이력을 교체하지 않는다. 댓글이 완전하게 반환됐고 사용자가 해당 이슈의 상태 이력 제한을 명시적으로 수용한 경우 ledger에 `statusHistoryDisposition = PREVIOUS_LOCAL_PRESERVED_CONNECTOR_LIMIT`을 기록한다. 이 분기는 상태 이력 완전성을 주장하지 않는 정상 보존 결과이며, 제한된 이슈와 전체·반환량을 완료 보고에 명시한다. 누락 내용을 추정해 채우지 않는다.
- Jira 응답 본문은 요약·교정·번역하지 않고 커넥터가 반환한 Markdown을 보존한다.

### 3.1 기존 이슈 reconciliation

기존 이슈의 갱신 결과는 원격과 로컬의 단순 댓글 수 일치가 아니라 다음 분기로 계산한다.

```text
필수 상세 응답 또는 댓글·changelog 페이지 종료조건 미관측
    → UNKNOWN, 기존 로컬 보존

previousCommentIds ⊆ remoteCommentIds
    → 동일 ID 댓글 본문과 신규 ID 댓글을 원격 snapshot에 맞춰 갱신

previousCommentIds ⊄ remoteCommentIds
    → 기존 Markdown과 기존 인덱스 항목 보존

기존 이슈가 프로젝트 전체 목록에 없음
    → 기존 Markdown과 기존 인덱스 항목 보존

원격 updated가 기존 인덱스 updatedAt보다 과거임
    → 기존 Markdown과 기존 인덱스 항목 보존

완전한 원격 changelog에서 기존 상태 전이 identity가 사라짐
    → 기존 Markdown과 기존 인덱스 항목 보존

기존 설명이 존재하고 원격 설명이 빈 값임
    → 기존 Markdown과 기존 인덱스 항목 보존
```

동일 댓글 ID의 본문이 다르면 원격 현재 본문을 갱신 대상으로 사용한다. 댓글 ID, 본문 또는 페이지 종료조건이 제공되지 않아 위 분기를 하나로 결정할 수 없으면 `reconciliationUnknownCount`에 포함한다. 기존 댓글 ID 부재와 원격 목록 부재에 따른 보존은 정상 reconciliation 결과이며 fetch 실패로 세지 않는다.

상태 이력은 원격 changelog의 페이지 종료조건을 관측한 경우에만 완전한 snapshot으로 판정한다. 커넥터가 다음 페이지를 지원하지 않아 종료 여부를 결정할 수 없으면 해당 이슈의 상태 이력 완전성은 `UNKNOWN`이다. 단, 댓글 snapshot이 완전하고 사용자가 해당 제한을 명시적으로 수용하여 `statusHistoryDisposition = PREVIOUS_LOCAL_PRESERVED_CONNECTOR_LIMIT`으로 기록된 이슈는 기존 로컬 상태 이력을 byte 단위로 보존하는 reconciliation 분기로 계산한다. 이 표식은 상태 이력을 완전하다고 바꾸지 않는다.

보존 분기의 성공 조건은 원격 현재 개수와의 일치가 아니라 실행 전 로컬 파일 SHA-256과 실행 후 파일 SHA-256의 일치다. 비교용 Markdown parser는 LF와 CRLF를 동일한 줄바꿈으로 해석하지만 보존 검증 hash는 원본 byte를 사용한다.

### 3.2 감사 간 회귀 판정

schema version 2 감사는 다음 명제를 추가 hard gate로 계산한다.

```text
regressionEvidenceReadable             = plan.json과 manifest의 auditId가 일치함
candidateSetMatches                    = discovery와 실행 전 index에서 재계산한 후보 집합이 plan과 일치함
remoteUpdatedRegressionCountZero       = 원격 updated가 실행 전 updatedAt보다 과거인 key가 0건임
localKeyLossCountZero                  = 실행 전 index의 모든 key가 실행 후 index에 존재함
remoteAbsentPreservationSatisfied      = 원격 부재 key의 index 항목과 파일 byte hash가 실행 전과 같음
reusedSnapshotIntegritySatisfied       = 모든 재사용 snapshot의 source·destination SHA-256이 plan과 일치함
```

위 명제는 전체 건수의 허용 비율이나 AI 판단으로 완화하지 않는다. 하나라도 `FALSE`이면 감사 상태는 `FAILED`, 필수 관측값이 없으면 `INCOMPLETE`다. 원격 부재와 삭제 의심 데이터는 정상 보존 결과로 기록하며 로컬에서 제거하지 않는다.

## 4. Markdown 저장 형식

각 이슈 파일은 현재 Jira 미러 형식을 따른다.

1. 제목: `# {ISSUE_KEY} {summary}`
2. 메타데이터: sourceUrl, id, issuetype, status, priority, assignee, reporter, created, updated, resolution, parent, labels
3. `## 설명`: Jira description 원문
4. `## 댓글`: 각 댓글의 생성 시각, 작성자, 댓글 ID와 본문
5. `## 상태 이력`: 시각, 작성자, 이전 상태, 이후 상태 표

파일은 UTF-8 BOM 없음으로 저장한다. 댓글이나 상태 전이가 없어도 해당 섹션과 상태 이력 표 헤더는 유지한다.

## 5. 인덱스 병합

- `index.json`을 이슈 파일 작성 직전 또는 직후의 최신 상태로 다시 읽는다.
- 다른 에이전트·세션이 추가한 이슈, 누락 키, notes와 메타데이터를 보존한다.
- 현재 요청 범위의 기존 인덱스 항목이 나타나면 중복 병합하지 않고 충돌로 중단한다.
- 성공한 이슈만 `issues`에 추가하고 이슈 번호 숫자순으로 정렬한다.
- 조회할 수 없는 키는 `scope.missingKeys`에 추가하고 숫자순으로 정렬한다.
- `scope.missingKeys` 추가는 아직 적재되지 않은 신규 조회 키에만 적용한다. 이미 `issues`에 존재하는 키가 삭제·조회 불가가 되어도 기존 인덱스 항목을 유지하며 `missingKeys`로 이동하지 않는다.
- 요청 키와 다른 키로 반환된 이슈는 `issues`에 추가하지 않고 원래 요청 키만 `scope.missingKeys`에 추가한다.
- `scope.issueCount`는 실제 `issues` 배열 길이와 일치시킨다.
- `scope.keyTo`는 현재까지 적재를 요청받은 가장 큰 이슈 번호를 반영한다.
- 기존 `collectedAt`은 과거 실행 시각을 나타내는 비신뢰 정보로 취급한다. 결정론적 검증기가 감사 증거에서 `VERIFIED`를 계산하는 구조가 구현되기 전에는 `collectedAt`을 갱신하거나 최신 상태의 근거로 사용하지 않는다.
- 의도적으로 건너뛴 병렬 작업 범위를 별도 미수집 주석으로 추가하지 않는다.

## 6. Jira에서 허용되는 동작

적재는 **읽기 전용 수집**이다. Jira에는 API 접근 로그 외 상태 변화를 남기지 않는다.

허용:

- 이슈와 메타데이터 조회
- 댓글 조회
- changelog와 상태 전이 조회

금지:

- 이슈 생성·수정·삭제
- 댓글 작성·수정·삭제
- 상태 전환
- 담당자·우선순위·라벨·링크 변경
- 작업 보고나 완료 흔적 게시

## 7. 사용하지 않는 우회 경로

- Atlassian 커넥터가 접근할 수 없는 원문을 브라우저 스크래핑으로 우회하지 않는다.
- 브라우저 쿠키·로컬 저장소·세션 저장소를 읽지 않는다.
- 로컬 DreamDiary API나 DB에 원문을 저장하지 않는다.
- 원문 전달용 임시 파일, 변환 스크립트, 별도 보고서를 만들지 않는다. 2.1의 감사 ledger와 읽기 전용 결정론적 검증기는 이 금지 대상이 아니다.
- 누락된 댓글, changelog 또는 상태 전이를 추정하여 채우지 않는다.

## 8. 완료 검증과 보고

1. 요청 범위의 성공 이슈 수와 조회 불가 키를 확인하고, 요청 키와 다른 응답 키가 저장·인덱싱되지 않았는지 대조한다.
2. 최신 동기화에서는 원격 전체 목록의 조회 페이지 수와 이슈 수, 신규 키 수, `updated` 변경 후보 수를 확인한다. 전체 페이지를 완주하지 못하면 이후 검증과 무관하게 실패다.
3. 기존 적재 이슈의 원격 삭제·조회 불가·내용 축소 응답이 로컬 Markdown과 인덱스 항목을 삭제하거나 축소하지 않았는지 확인한다. 기존 댓글 ID 집합에서 사라진 ID가 있는 이슈는 갱신되지 않았는지 대조한다.
4. 각 대상 Markdown 파일이 존재하고 크기가 0보다 큰지 확인한다.
5. 갱신 분기에서는 파일의 원격 현재 댓글 헤더 수와 `index.json.commentCount`가 일치하는지 확인한다. 보존 분기에서는 기존 파일과 기존 인덱스 항목의 byte/hash 및 count가 실행 전과 일치하는지 확인한다.
6. 완전한 changelog 갱신 분기에서는 상태 이력 표의 데이터 행 수와 `index.json.statusTransitionCount`가 일치하는지 확인한다. 상태 이력 완전성이 `UNKNOWN`이면 최신 동기화를 `VERIFIED`로 보고하지 않는다.
7. `index.json`을 다시 파싱하고 `scope.issueCount`와 실제 배열 길이를 대조한다.
8. UTF-8로 대상 파일의 한글 제목과 첫 본문 구간을 읽어 표시 문자열을 육안 확인한다.
9. `python scripts/check_encoding.py`로 UTF-8 게이트를 확인한다.
10. 다른 세션 소유의 워킹트리 변경이 유지됐는지 `git status --short`로 확인한다.
11. 결과 보고에는 전체 목록 조회 건수·페이지 수, 신규·갱신·보존 건수, 조회 불가 키, 요청 키/응답 키 불일치, 댓글 수, 상태 전이 수, 페이지 제한 이슈와 검증 결과만 간결하게 남긴다.
12. `python -m unittest discover -s scripts/tests -p 'test_jira_*.py' -v`로 cursor 단절, 후보 변조, snapshot hash 변조, 로컬 key 유실, 댓글·상태 이력 축소 보존 회귀를 확인한다.
