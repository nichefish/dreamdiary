# Prospective 인터랙션 마이그레이션 스펙

## Keep 등록·수정

제목 필수 검증·확인·결과 fallback은 현재 locale의 클라이언트 카탈로그를 사용한다. 저장 API가 `message`를 반환하면 서버 메시지를 우선 표시하며, 성공 시 모달을 닫고 호출 화면의 Keep 목록을 갱신한다.

## 유효 기한 파생

유효 기한(`expireDt`)은 일시(`dateText`) 원문에서 찾은 날짜 중 가장 늦은 단일 날짜다. 해석은 `features/prospective/keep/utils/expireDt.ts`가 담당하고 서버는 확정된 `yyyy-MM-dd` 또는 빈 문자열만 받는다.

- `10/17`, `10-17`, `10.17`, `2026/10/17`, `26/10/17`을 인식하며 2자리 년도는 2000년대로 해석한다.
- 쉼표로 이어 쓴 날은 앞 토큰의 연·월을 사용한다.
- 년도 생략 시 전년·당해·익년 중 오늘과 가장 가까운 해를 고르고 동률이면 미래를 택한다.
- 유효하지 않은 날짜는 버리고 나머지 후보로 판단한다.
- 자동 채움은 유효 기한이 비어 있을 때만 수행한다. 값이 있으면 「기한 추출」로 명시적으로 다시 계산한다.
- 입력은 readonly이며 달력과 「기한 추출」로만 변경한다.
- 기한 경과는 자동 라이프사이클 전이를 만들지 않는다.

## Keep 목록 행 액션

행 컨텍스트 메뉴는 수정·라이프사이클·상태·삭제를 제공한다. 서버 재조회와 Project 탭·말머리·태그·검색어·기한 지남 필터로 행 구성이 바뀐 뒤에는 Keep ID 구성을 감시하고 Vue DOM 반영 후 `reinitMetronicAfterDom()`으로 메뉴를 재바인딩한다. 라이프사이클은 `OPEN`·`PENDING`과 `RESOLVED`·`FAILED`·`EXPIRED`·`DROPPED`를 구분하고 현재 단계를 표시한다. `EXPIRED`는 기한이 구속 조건이어서 놓친 항목, `DROPPED`는 기간을 늘려도 하지 않을 항목에 사용한다. 유효 기한이 없는 항목은 `EXPIRED`로 전이하지 않는다.

라이프사이클은 `PUT /api/lifecycles`, 상태는 `POST /api/states`, 삭제는 `DELETE /api/prospective/keep/:id`를 사용하며 성공 후 전 범위 목록을 재조회한다. 행 체크박스의 즉시 완료(`RESOLVED`)는 유지한다.

저널 aside에서 Keep을 조작하는 진입·표시 계약은 [저널 인터랙션 스펙](../journal/interaction-spec.md)이 소유한다.

## Keep Project 인터랙션

`/keep`의 기본 `Project 미지정` 탭은 `projectId == null`인 Keep만 표시하고 `전체`는 연결 여부와 무관한 전 범위를 표시한다. `OPEN` Project 탭은 해당 `projectId`의 Keep을 투영하며 다른 필터와 AND로 결합한다. `PENDING`·`RESOLVED`·`DROPPED` Project는 관리 모달에 보존하고 탭에서 제외한다. Project 탭에서 신규 Keep을 열면 해당 Project를 초기 선택한다.

Keep 행의 `Project로 이동`은 본문·말머리·태그·상태·라이프사이클·유효 기한을 유지하고 소속만 변경한다. 이동 대상은 미지정 또는 `OPEN` Project이며 비`OPEN` Project는 기존 소속 확인만 허용한다. Project lifecycle 전이는 연결 Keep을 변경하지 않는다. 현재 탭 밖으로 이동하거나 선택 Project가 `OPEN` 밖으로 전이되면 `Project 미지정`으로 돌아간다. Project 삭제는 연결 Keep을 미지정으로 남긴다.

Project 관리 모달은 lifecycle 배지와 직접 노출한 순서 이동 버튼, 수정·lifecycle·삭제 컨텍스트 메뉴를 사용한다. 메뉴 액션은 dropdown을 닫고, 수정은 선택 행 아래에 공통 편집 폼을 연다. lifecycle 전이 후 전체 Project를 재조회하고 loading 종료 뒤 다시 생성된 메뉴 DOM을 재바인딩한다.
