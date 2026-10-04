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
