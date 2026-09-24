# Prospective Project 계약

## 1. 목적

Prospective Project는 실행을 약속하지 않은 미래의 가능성을 하나의 이름 있는 맥락으로 조직한다. Project 자체는 Todo, 일정, 목표점 또는 완료 의무가 아니다.

## 2. 현재 구현 범위

| 계약 | 상태 |
|---|---|
| 사용자별 Project 등록·조회·수정 | ✓ |
| 사용자별 Project 순서 변경 | ✓ |
| Project soft-delete | ✓ |
| 동일 사용자의 동일 제목 Project 허용 | ✓ |
| Keep과 Project 연결 | ✓ Keep 0..1 : Project 1 관계 |
| 기존 Keep 화면의 Project 탭·관리 모달 | ✓ |
| Project 라이프사이클·작업공간 투영 | ✓ `OPEN`만 탭 노출 |

## 3. 데이터 계약

`prospective_project`는 `id`, `title`, `description`, `sort_order`와 감사 필드를 가진다.

- `title`은 필수이며 최대 200자다.
- `description`은 선택값이다.
- `sort_order`는 사용자별 활성 Project 목록의 1부터 시작하는 전역 순번이다.
- `created_by`가 소유권의 단일 기준이다.
- 삭제는 `deleted_at`을 기록하는 soft-delete다.
- 제목은 식별자가 아니며 같은 사용자가 같은 제목을 여러 번 사용할 수 있다.
- 라이프사이클은 공통 `lifecycle` 테이블의 `(PROSPECTIVE_PROJECT, projectId)` 현재값으로 보관한다.
- 저장 행이 없는 Project는 `OPEN`이다. 허용 단계는 `OPEN`·`PENDING`·`RESOLVED`·`DROPPED`다.
- Project의 `RESOLVED`는 표시 접힘 상태를 파생하지 않는다.

## 4. 접근과 변경 계약

- 목록은 로그인 사용자가 소유한 soft-delete되지 않은 Project 전체를 라이프사이클과 함께 `sort_order`, `id` 오름차순으로 반환한다.
- 상세·수정·삭제는 원본 `created_by`가 로그인 사용자와 같을 때만 허용한다.
- 등록 시 현재 사용자의 마지막 순번 다음 값을 서버가 부여한다.
- 일반 수정 API는 `sort_order`를 변경하지 않는다.
- 순서 변경은 현재 사용자의 활성 Project ID 전체를 중복 없이 제출해야 한다. 부분 목록, 중복 ID, 존재하지 않는 ID와 타인 소유 ID가 포함된 요청은 변경 없이 거부한다.

## 5. API

| 작업 | API |
|---|---|
| 목록 | `GET /api/prospective/projects` |
| 상세 | `GET /api/prospective/project/{id}` |
| 등록 | `POST /api/prospective/projects` |
| 수정 | `POST /api/prospective/project/{id}` |
| 삭제 | `DELETE /api/prospective/project/{id}` |
| 전체 순서 변경 | `PUT /api/prospective/projects/sort-orders` |
| 라이프사이클 전이 | `PUT /api/lifecycles` (`contentType=PROSPECTIVE_PROJECT`) |
| Keep Project 이동 | `PATCH /api/prospective/keep/{id}/project` |

## 6. Keep 연결

- `prospective_keep.project_id`는 nullable FK이며 기존 Keep과 신규 Keep의 기본값은 Project 미지정이다.
- Keep은 Project를 선택하지 않거나 하나만 선택한다.
- Keep 등록·수정 요청의 `projectId`는 현재 로그인 사용자가 소유한 활성 Project만 허용한다.
- Project 선택과 해제는 Keep의 본문·말머리·태그·상태·라이프사이클·유효 기한을 변경하지 않는다.
- Project 삭제 서비스는 연결된 같은 사용자 Keep의 `project_id`를 `NULL`로 바꾼 뒤 Project를 soft-delete한다.
- Project 삭제는 Keep 삭제로 전파되지 않는다.
- Project 라이프사이클 전이는 연결된 Keep의 `project_id`·본문·상태·라이프사이클을 변경하지 않는다.
- Keep 목록은 `projectId`로 특정 Project를, `projectUnassigned=true`로 Project 미지정 항목을 조회할 수 있다.
- `projectId`와 `projectUnassigned=true`를 동시에 요청하면 모순된 범위로 거부한다.

## 7. Keep 화면 통합

- `/keep`의 기본 탭은 `Project 미지정`이며 `projectId == null`인 Keep을 표시한다.
- `전체` 탭은 Project 연결 여부와 무관하게 전 범위 Keep을 표시한다.
- `OPEN` Project 하나는 상단 탭 하나로 표시되고, 선택하면 해당 `projectId`의 Keep만 남긴다.
- `PENDING`·`RESOLVED`·`DROPPED` Project는 상단 탭에서 제외하고 관리 모달에 보존한다. 관리 모달에서 `OPEN`으로 전이하면 기존 전역 순번 위치에 따라 탭으로 복귀한다.
- Project 범위와 말머리·태그·검색어·기한 지남 필터는 클라이언트에서 AND 조건으로 결합한다.
- Project 탭에서 Keep을 추가하면 선택한 Project를 초기값으로 사용한다. `Project 미지정`과 `전체`에서는 Project 미지정이 초기값이다.
- Keep 등록·수정 모달은 `OPEN` Project를 선택하거나 미지정으로 바꿀 수 있다. 기존 비`OPEN` 소속은 현재값으로 표시하되 새 선택 대상으로 허용하지 않는다.
- Project 관리는 `/keep` 안의 모달에서 등록·수정·삭제·순서 변경을 제공한다.
- Project 관리 모달은 전체 Project의 현재 라이프사이클을 표시하고 네 허용 단계 사이 전이를 제공한다.
- 관리 행은 lifecycle 배지와 위·아래 순서 버튼을 직접 표시한다. 수정·lifecycle 라디오 서브메뉴·삭제는 Keep·저널 목록과 같은 `⋯` 컨텍스트 메뉴에서 수행하며 액션 선택 시 열린 메뉴를 닫는다.
- 추가 폼은 목록 상단에 열고, 수정 폼은 선택 행을 강조하면서 바로 아래에 연다. 두 흐름은 같은 Project 편집 폼을 사용한다.
- Project 삭제 확인은 연결 Keep 수와 삭제 후 Project 미지정으로 남는다는 결과를 표시한다.
- Keep 행의 `프로젝트로 이동` 메뉴는 현재 단일 소속을 체크로 표시한다. Project 미지정 또는 다른 `OPEN` Project로 즉시 이동하며 비`OPEN` Project는 기존 소속 확인만 허용한다.
- Project 이동은 Keep의 본문·말머리·태그·상태·라이프사이클·유효 기한을 변경하지 않는다.
- Project 삭제 뒤 삭제된 탭이 선택돼 있었다면 `Project 미지정` 탭으로 이동한다.
- 선택 중인 Project가 `OPEN` 밖으로 전이되면 `Project 미지정` 탭으로 이동한다.
