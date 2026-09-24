# Prospective 화면 마이그레이션 스펙

## `/keep` Keep 전수관리

**Vue 구현**: `app/frontend-vue/src/features/prospective/keep/KeepPage.vue`

전 범위 `prospective_keep(scope=all)`를 진행과 종결 그룹으로 나누어 표시한다. 진행은 `OPEN`·`PENDING`, 종결은 `RESOLVED`·`FAILED`·`EXPIRED`·`DROPPED`이며 행 배지와 아이콘으로 구분한다. 제목·본문 검색, 말머리·태그·기한 지남 필터, 태그클라우드, 신규 등록, 제목 클릭 수정, 체크박스 즉시 완료와 행 컨텍스트 메뉴를 제공한다.

진행 그룹은 유효 기한 오름차순이며 기한이 없는 항목은 서버 순서(`sortOrder` → `id`)를 유지한다. 종결 그룹은 서버 순서를 유지한다. 유효 기한은 제목 뒤에 표시하고, 지난 기한의 미종결 항목은 경고색과 지난 일수를 표시한다. 종결 항목은 날짜만 표시한다. 등록일은 행 우측에 `yyyy.MM`으로 표시한다.

말머리·태그·검색어·기한 지남 필터는 전 범위 목록에 AND로 적용한다. 「기한 지남」은 지난 기한의 미종결 항목만 남기며 라이프사이클을 변경하지 않는다. 태그클라우드와 행 태그는 같은 태그 컨텍스트 메뉴를 사용한다.

상단에는 `Project 미지정`, `전체`, 사용자 `OPEN` Project별 탭을 전역 순서대로 표시한다. 비`OPEN` Project는 관리 모달에 보존하고 탭에서 제외한다. 기본 탭은 `Project 미지정`이며 `전체`는 연결 여부와 무관한 전 범위를 유지한다. Project 범위는 다른 필터와 AND로 결합한다. Project 관리 행은 lifecycle 배지와 위·아래 이동 버튼을 직접 표시하고 수정·lifecycle·삭제는 컨텍스트 메뉴로 제공한다. 추가 폼은 목록 상단, 수정 폼은 선택 행 바로 아래에 표시하며 해당 행을 강조한다. Keep 행의 `Project로 이동` 메뉴는 현재 단일 소속을 표시한다.

## Keep 등록·수정 모달

**Vue 구현**: `app/frontend-vue/src/features/prospective/keep/modals/ProspectiveKeepRegistModal.vue`

- Bootstrap `modal-xl`, 폼 `#prospectiveKeepRegistForm`
- 말머리는 `PROSPECTIVE_KEEP` 개인 활성 선택지를 사용하며 정상 빈 목록이면 필드를 표시하지 않는다.
- 일시(`#dateText`)와 위치(`#location`)는 200자 자유 텍스트다.
- 유효 기한(`#expireDt`)은 일시에서 파생한 단일 날짜이며 readonly 입력·달력·기한 추출 버튼으로 변경한다.
- 본문과 태그를 함께 편집한다.
- 등록 성공은 호출 화면의 Keep 목록을 갱신한다.

저널 화면에 표시되는 `JournalAsideKeepCard`와 그 마운트 계약은 [저널 화면 스펙](../journal/screen-spec.md)이 소유한다.
