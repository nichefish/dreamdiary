# Prospective 컴포넌트 마이그레이션 스펙

## `ProspectiveKeepRegistModal`

**Vue 구현**: `app/frontend-vue/src/features/prospective/keep/modals/ProspectiveKeepRegistModal.vue`

`useProspectiveKeepModalStore.registModel`의 말머리·제목·순서·일시·유효 기한·위치·Project·본문·태그를 편집한다. 제목은 유일한 필수값이며 라벨의 공통 `required` 클래스로 필수 표시를 제공한다. PC에서는 Project를 말머리 왼쪽에 두고 Project·말머리·제목·수정 순서를 한 행에 표시하며 모바일에서는 세로로 표시한다. 말머리가 없으면 제목이 해당 영역까지 확장하고 위치는 전체 행을 사용한다. Project 선택지는 사용자 활성 목록이며 미지정 또는 하나를 선택한다. 선택한 Project 탭의 신규 모달은 해당 Project를 초기값으로 사용한다. 태그는 `TagifyEditor`와 `PROSPECTIVE_KEEP` categoryMap을 사용한다. 말머리는 `PROSPECTIVE_KEEP` 개인 활성 목록을 사용하며, 선택지가 없으면 필드를 표시하지 않는다. 저장된 비활성 말머리는 수정 시 표시해 같은 선택의 유지·해제를 허용한다.

일시 원문은 그대로 보존하고 유효 기한은 클라이언트가 확정한 절대 날짜만 전송한다. 신규는 `POST /api/prospective/keeps`, 수정은 `POST /api/prospective/keep/{id}`를 사용한다.

**미리보기**: 푸터 저장 왼쪽에 `btn-sm btn-light-primary` · `bi-eye` · `common.preview` 버튼을 둔다. 저널 엔트리 등록 모달과 같은 `/journal/entry/preview-pop` 새 창·`POST /api/journal/entries/preview` 계약을 쓰며 콘텐츠 유형은 `PROSPECTIVE_KEEP`이다. 말머리·제목·순서·중요 별(수정 모달을 연 시점의 `IMPRTC` 보유 여부)과 작성 중 본문을 Keep `markdownContent`와 같은 렌더로 표시하고 저장하지 않는다. 제목 아래에는 값이 있는 일시·유효 기한(남은/지난 일수 포함)·위치·Project를 `라벨 값` 줄로, 태그를 `TagChip`으로 표시하며 라벨은 모달 필드와 같은 i18n 키를 쓴다. 본문은 저널 목록의 좌우 여백 칸 없이 표시한다. 툴팁·실패 문구는 `journal.entry.preview.*`, 팝업 차단은 `common.error.popup`을 쓴다.

**현재 Vue 동등**: ✓ 구현 완료

## `ProspectiveProjectManageModal`

`/keep`에서 사용자 Project 전체 목록과 lifecycle 배지를 표시한다. 위·아래 이동은 행에 직접 노출하고 수정·lifecycle 라디오 서브메뉴·삭제는 Metronic 컨텍스트 메뉴에 둔다. 메뉴 액션은 열린 dropdown을 닫는다. 추가는 목록 상단, 수정은 선택 행 바로 아래에 공통 `ProspectiveProjectEditorForm`을 열고 행을 강조한다. 목록 DOM이 바뀌거나 loading이 행을 다시 만들면 `reinitMetronicAfterDom()`으로 메뉴를 재바인딩한다. 상단 탭과 신규 Keep 배치 대상은 `OPEN` Project만 사용한다.

**현재 Vue 동등**: ✓ 구현 완료

## `ProspectiveProjectEditorForm`

Project 이름·설명을 편집하고 저장·취소 이벤트를 관리 모달로 전달한다. 신규와 수정이 같은 필드·검증·저장 동작을 공유하며 배치 위치와 편집 대상은 관리 모달이 결정한다.

**현재 Vue 동등**: ✓ 구현 완료

## `ProspectiveKeepProjectMoveMenu`

Keep 행의 컨텍스트 메뉴에서 Project 미지정과 Project 목록을 단일 선택지로 표시한다. 현재 소속을 체크하고 비`OPEN` Project는 기존 소속 확인만 허용한다. `OPEN` Project 선택 시 `PATCH /api/prospective/keep/{id}/project`로 소속만 변경한다.

**현재 Vue 동등**: ✓ 구현 완료

## Keep lifecycle 조회 데이터

✓ Keep 목록과 상세 DTO는 `lifecycleKey`·`lifecycleDesc`·`resolvedAt`을 포함한다. 상태와 완료 시각은 목록당 한 번의 lifecycle 일괄 조회로 부착하고 상세 본문 캐시와 분리한다. lifecycle 행 부재는 `OPEN`, 완료 시각은 null이다. `resolvedAt`은 UTC ISO-8601 문자열 또는 null이며 현재 `RESOLVED` 전이 시각을 나타낸다.

## Keep 최근 완료 투영

✓ 웹 `KeepPage.vue`는 `utils/recentCompletion.ts`로 최근 완료를 최신 완료순으로 구하고 최근 완료·나머지 종결 그룹의 행 렌더와 컨텍스트 메뉴를 공유한다. `renderedKeepRowSignature`에는 표시 중인 최근 완료 ID도 포함하여 펼침과 만료 이동 후 메뉴를 재바인딩한다. 앱은 같은 경계 계약의 `utils/keepRecent.ts`와 `KeepHomeScreen`을 사용한다. 두 클라이언트는 서버 완료 시각을 표시 목적으로 투영하며 lifecycle 저장은 서버 API가 담당한다.

## 모바일 Keep 라이프사이클 컴포넌트

✓ `KeepSwipeRow`는 진행 행의 가로 제스처를 판정하고 완료 버튼을 노출한다. 완료 요청은 버튼 또는 접근성 완료 액션으로 실행하며 종결 행·저장 중에는 완료 제스처를 잠근다. `KeepLifecycleMenu`는 현재 상태를 라디오로 표시하는 여섯 단계 하단 시트이며 저장 중 선택·배경 닫기·Android 뒤로 닫기를 잠근다. 두 컴포넌트는 상태 저장을 `KeepHomeScreen`에 위임하고 현재 상태·완료 시각은 서버 재조회 결과를 사용한다.

✓ 모바일 `KeepLifecycleMenu`는 라이프사이클 라디오 아래 독립적인 중요·요구 스위치를 표시한다. 서버 `state.list`가 현재 값이며 선택을 `KeepHomeScreen`에 위임한다. 패널 높이는 화면의 85% 이내이고 항목 영역을 내부 스크롤로 탐색한다. 요청 중 스위치를 비활성화하며 속성 요청은 메뉴를 닫고 목록에서 진행 상태를 표시한다.