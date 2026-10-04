# Prospective 인터랙션 마이그레이션 스펙

## Keep 등록·수정

제목 필수 검증·확인·결과 fallback은 현재 locale의 클라이언트 카탈로그를 사용한다. 저장 API가 `message`를 반환하면 서버 메시지를 우선 표시하며, 성공 시 모달을 닫고 성공 알림 확인 후 호출 화면의 Keep 목록을 갱신한다.

## Keep 작성 미리보기

등록·수정 모달 푸터의 `미리보기`는 클릭 즉시 `/journal/entry/preview-pop` 새 창을 열고 `POST /api/journal/entries/preview`로 작성 중 본문을 `markdownContent`로 렌더해 `PROSPECTIVE_KEEP` 유형(노트와 같은 본문 클래스, 좌우 여백 칸 없음)으로 표시한다. 일시·유효 기한·위치·Project 이름·태그는 클릭 시점의 모달 값(미저장 포함)을 함께 전달해 값이 있는 항목만 메타 블록에 표시하며, 유효 기한의 남은/지난 일수는 미리보기 창이 렌더 시점 기준으로 계산한다. 저장·목록 갱신을 일으키지 않으며 팝업 차단 시 `common.error.popup`을 표시한다.

## 유효 기한 파생

유효 기한(`expireDt`)은 일시(`dateText`) 원문에서 찾은 날짜 중 가장 늦은 단일 날짜다. 일시는 사용자가 쓴 다회차·범위·시각 원문을 보존하고 유효 기한은 정렬·임박 판정에 쓰는 기계용 축이다. 해석은 `features/prospective/keep/utils/expireDt.ts`가 담당하고 서버는 확정된 `yyyy-MM-dd` 또는 빈 문자열만 받는다.

- `10/17`, `10-17`, `10.17`, `2026/10/17`, `26/10/17`을 인식하며 2자리 년도는 2000년대로 해석한다.
- 쉼표로 이어 쓴 날은 앞 토큰의 연·월을 사용한다.
- 년도 생략 시 전년·당해·익년 중 오늘과 가장 가까운 해를 고르고 동률이면 미래를 택한다.
- 유효하지 않은 날짜는 버리고 나머지 후보로 판단한다.
- 자동 채움은 유효 기한이 비어 있을 때만 수행한다. 값이 있으면 「기한 추출」로 명시적으로 다시 계산한다.
- 입력은 readonly이며 달력과 「기한 추출」로만 변경한다.
- 후보가 없으면 「기한 추출」을 비활성화하고 기존 값을 유지한다. 기한이 비었을 때는 찾은 후보와 마지막 날짜를, 채워졌을 때는 남은 일수 또는 지난 일수를 표시한다.
- 기한 경과는 자동 라이프사이클 전이를 만들지 않는다. 「기한 지남」 필터도 상태를 쓰지 않는다.

## Keep 목록 행 액션

행 컨텍스트 메뉴는 수정·라이프사이클·Project 이동·상태·삭제를 제공한다. Project 탭과 필터로 행 구성이 바뀐 뒤에는 Keep ID 구성을 감시하고 Vue DOM 반영 후 `reinitMetronicAfterDom()`으로 메뉴를 재바인딩한다. 라이프사이클은 `OPEN`·`PENDING`과 `RESOLVED`·`FAILED`·`EXPIRED`·`DROPPED`를 구분하고 현재 단계를 표시한다. `EXPIRED`는 기한이 구속 조건이어서 놓친 항목, `DROPPED`는 기간을 늘려도 하지 않을 항목에 사용한다. 유효 기한이 없는 항목은 `EXPIRED`로 전이하지 않는다.

라이프사이클은 `PUT /api/lifecycles`, 상태는 `POST /api/states`, 삭제는 `DELETE /api/prospective/keep/:id`를 사용하며 성공 후 전 범위 목록을 재조회한다. 라이프사이클 행이 없는 항목은 `OPEN`으로 본다. 상태의 `DEMANDED`(요구)·`IMPRTC`(중요)는 진행 단계가 아닌 다중 속성으로 행 컨텍스트 메뉴의 상태 서브메뉴에서 토글하며(서버 허용 집합 `AttachableContentStatePolicy`), 중요 별 아이콘은 표시 전용이다. 행 체크박스의 즉시 완료(`RESOLVED`)는 유지한다.

저널 aside에서 Keep을 조작하는 진입·표시 계약은 [저널 인터랙션 스펙](../journal/interaction-spec.md)이 소유한다.

## Keep Project 인터랙션

`/keep`의 기본 `Project 미지정` 탭은 `projectId == null`인 Keep만 표시하고 `전체`는 연결 여부와 무관한 전 범위를 표시한다. `OPEN` Project 탭은 해당 `projectId`의 Keep을 투영하며 다른 필터와 AND로 결합한다. `PENDING`·`RESOLVED`·`DROPPED` Project는 관리 모달에 보존하고 탭에서 제외한다. Project 탭에서 신규 Keep을 열면 해당 Project를 초기 선택한다.

Keep 행의 `Project로 이동`은 본문·말머리·태그·상태·라이프사이클·유효 기한을 유지하고 소속만 변경한다. 이동 대상은 미지정 또는 `OPEN` Project이며 비`OPEN` Project는 기존 소속 확인만 허용한다. Project lifecycle 전이는 연결 Keep을 변경하지 않는다. 현재 탭 밖으로 이동하거나 선택 Project가 `OPEN` 밖으로 전이되면 `Project 미지정`으로 돌아간다. Project 삭제는 연결 Keep을 미지정으로 남긴다.

Project 관리 모달은 lifecycle 배지와 직접 노출한 순서 이동 버튼, 수정·lifecycle·삭제 컨텍스트 메뉴를 사용한다. 메뉴 액션은 dropdown을 닫고, 수정은 선택 행 아래에 공통 편집 폼을 연다. lifecycle 전이 후 전체 Project를 재조회하고 loading 종료 뒤 다시 생성된 메뉴 DOM을 재바인딩한다.
