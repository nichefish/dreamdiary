# 로컬 적재 신뢰 판정 공통 계약

## 1. 목적

이 계약은 원격 또는 로컬 원천을 로컬 저장소에 적재할 때 `최신`, `완료`, `검증됨`으로 판정할 수 있는 공통 조건을 정의한다. 개별 소스 계약은 이 계약의 원자 명제와 판정 규칙을 구체화한다.

시각 필드는 작업이 일어난 시점을 나타낼 뿐 완전성이나 정합성의 증거가 아니다. `collectedAt`, `fetchedAt`, `ingestedAt` 또는 같은 의미의 값만으로 적재 결과를 신뢰 상태로 승격하지 않는다.

## 2. 삼값 판정

각 원자 명제는 다음 세 값 중 하나다.

- `TRUE`: 명제의 필수 관측값이 모두 존재하고 판정식을 충족한다.
- `FALSE`: 명제의 필수 관측값이 모두 존재하며 판정식을 위반한다.
- `UNKNOWN`: 필수 관측값이 없거나 불완전하여 참과 거짓을 결정할 수 없다.

`UNKNOWN`은 `TRUE` 또는 `FALSE`로 대체하지 않는다. 검증되지 않은 상태는 오류가 입증된 상태와 구분하지만, 둘 다 신뢰 상태로 승격할 수 없다.

## 3. 신뢰 상태 전이

적재 범위의 신뢰 상태는 다음 규칙으로만 계산한다.

```text
UNVERIFIED
    └─ 감사 시작 → AUDITING
         ├─ 필수 원자 명제 중 FALSE 존재   → FAILED
         ├─ FALSE가 없고 UNKNOWN 존재       → INCOMPLETE
         └─ 필수 원자 명제가 모두 TRUE      → VERIFIED
```

- `VERIFIED`, `SUCCESS`와 같은 결과를 사람이나 AI가 직접 기록하지 않는다.
- 신뢰 상태는 소스 계약에 열거된 필수 원자 명제의 값에서 결정론적으로 파생한다.
- 최근 성공 시각이 필요하면 직접 수정하는 필드를 두지 않고, `VERIFIED`로 계산된 가장 최근 감사 증거의 완료 시각에서 파생한다.
- 이전의 `VERIFIED` 결과는 이후의 실패·중단·부분 실행만으로 덮어쓰지 않는다. 새 실행은 별도 결과로 남으며, 최신 신뢰 상태는 완결된 감사 결과만으로 계산한다.

## 4. 감사 증거

감사 증거는 성공 선언이 아니라 다음 판정을 재계산할 수 있는 관측 사실이다.

- 감사 범위와 원천 식별자
- 목록 조회의 시작점, 페이지 식별자 또는 cursor 연속성, 원격 종료조건
- 요청·성공·실패 건수
- 발견한 원격 identity 수와 중복 identity 수
- 필수 상세 조회의 대상·성공·실패 건수
- 잘림, 페이지 제한 또는 필수 필드 누락 건수
- reconciliation 분기별 대상 identity와 결과
- 로컬 불변식 검증 결과
- 실행 시작·완료 시각

감사 증거에 `status: SUCCESS`가 있더라도 신뢰 판정 입력으로 사용하지 않는다. 결과는 원자 관측값으로 다시 계산한다.

## 5. 발견·조회 완료 조건

- `FULL SCAN`, `전수조회`, `끝까지 조회`라는 자연어 실행 선언은 완료 증거가 아니다.
- 페이지 기반 원천은 첫 페이지에서 시작해 모든 페이지 요청이 성공하고, cursor 연속성이 유지되며, 원천이 제공하는 종료조건이 관측됐을 때만 `paginationExhausted = TRUE`다.
- 종료조건을 제공하지 않거나 관측하지 못하면 `paginationExhausted = UNKNOWN`이다.
- 발견한 identity의 중복 여부와 필수 상세 조회 대상 수는 관측값으로 계산한다.
- 원격에서 발견한 필수 대상 중 상세 조회에 실패한 대상이 있으면 `requiredFetchFailureCount > 0`이다. 계약상 정상 보존 분기는 조회 실패로 분류하지 않고 별도의 reconciliation 결과로 기록한다.

## 6. 동일성과 정합성

원격 현재 상태와 로컬 결과의 단순 동일성은 공통 hard gate가 아니다. 로컬 저장소가 과거 원문 보존 또는 정규화를 수행하는 경우 정합성은 다음 계산 결과로 판정한다.

```text
expectedLocal = reconcile(previousLocal, remoteSnapshot, sourceRules)
localInvariantSatisfied = (actualLocal == expectedLocal)
```

- `reconcile`의 모든 분기는 관측 가능한 입력과 상호 배타적인 판정식으로 정의한다.
- 분기 입력이 없거나 불완전하면 결과는 `UNKNOWN`이다.
- `적절한`, `필요한 경우`, `문제가 없으면`, `합리적인`, `축소된 것으로 판단되면`처럼 별도 의미 판단이 필요한 표현을 hard gate와 reconciliation 분기에 사용하지 않는다.
- 계약상 보존 결과는 허용된 실패가 아니라 정상적인 reconciliation 결과다.

## 7. 원자 명제와 실패 조건

`failurePolicySatisfied`처럼 하위 판단을 숨기는 포괄 명제를 사용하지 않는다. 각 소스 계약은 다음과 같은 직접 계산 가능한 명제를 필요한 만큼 정의한다.

```text
paginationExhausted
paginationFailureCount
duplicateIdentityCount
requiredFetchFailureCount
unresolvedTruncationCount
reconciliationUnknownCount
localInvariantFailureCount
```

일부 결과를 정상으로 취급하려면 해당 결과를 identity와 판정식이 있는 reconciliation 분기로 정의한다. 비율, 중요도 또는 AI의 의미 판단으로 실패를 성공 처리하지 않는다.

## 8. 실행과 보고 경계

- AI는 조회를 실행하고 관측값을 설명할 수 있지만 신뢰 상태의 근원이 아니다.
- 결정론적 검증기가 없는 동안에는 필수 명제를 관측값에서 재계산해 제시하고, 하나라도 `UNKNOWN`이면 `최신`, `완료`, `VERIFIED`라고 보고하지 않는다.
- 문서에 판정식을 정의한 사실은 검증기 구현 완료를 의미하지 않는다. 실행기와 결정론적 검증기가 구현되기 전까지 기존 기준선과 시각 필드는 `UNKNOWN`으로 취급한다.
