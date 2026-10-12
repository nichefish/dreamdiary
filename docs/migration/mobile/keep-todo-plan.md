# Keep 전수관리 → 모바일 할일(Keep) 앱 계획

- 작성일: 2026-10-06 (브랜치 `dev_0.30.0`, 기준 커밋 `8330dae45`)
- 목적: 웹 `Keep 전수관리`(feature `prospective`, 경로 `/keep`)를 모바일 앱 안의 할일형 화면으로 옮기기 위한 조사와 단계별 계획
- 범위: 조사·설계만 다룬다. 앱·백엔드 코드는 바꾸지 않는다. 백엔드 부족분은 "갭"으로만 기록하고, 모바일은 **현재 백엔드 계약 그대로** 쓰는 것을 전제로 한다.
- 갱신: 2026-10-06 밤 — **2-0(읽기 전용 목록·상세) 구현**, 코드 확인 중 찾은 계약 사항(상세 응답에 lifecycle 없음, 태그 `contentSize` 집계 범위) 반영(§3.1, §3.5, §6.1)
- 갱신: 2026-10-06 밤(2) — **백엔드 상세(R2)도 lifecycle 부착**(사용자 승인, Keep 상세 읽기 경로만). 모바일 상세는 응답 값을 그대로 쓰고 목록 병합·목록 재조회를 없앴다(§3.1, §3.5, §6.1)
- 위치: 모바일 2단계(쓰기)의 첫 대상이다. 1단계(조회 전용) 범위는 `screen-spec.md` §1을 따른다.
- 관련 문서: `screen-spec.md` (모바일 화면 계약), `tagcloud-thread-port-plan.md` (같은 형식의 선행 계획), `docs/migration/prospective/screen-spec.md`·`interaction-spec.md`·`component-spec.md` (웹 Keep 계약), `docs/spec/PROSPECTIVE_PROJECT_SPEC.md`, `docs/ideas/prospective-keep-evolution.md`

---

## 1. 웹 Keep 현황 분석

### 1.1 Keep이란

`docs/ideas/prospective-keep-evolution.md`의 정의: **"Keep은 실행을 약속한 Todo가 아니라 미래에 다시 고려할 가치가 있어 붙잡아 둔 가능성"**이다. 그래서 웹은 "완료" 하나가 아니라 끝난 이유를 네 가지(완료·실패·만료·내려놓음)로 나눈다. 모바일에서 할일처럼 다루더라도 이 어휘는 그대로 둔다(§6 질문 2).

- 시간 축은 `created_at`(목록 행 오른쪽 `yyyy.MM`), 기한 축은 `expire_dt`다.
- 기한은 자유 텍스트 `date_text`(예: `10/17, 24`)에서 **클라이언트가** 마지막 날짜를 뽑아 채운다(`utils/expireDt.ts`). 서버는 계산하지 않는다.
- 기한이 지나도 자동으로 만료(EXPIRED)되지 않는다. 사용자가 직접 종결한다.
- `전수관리` = 진행 중인 것과 끝난 것을 **전 범위(scope=all)** 로 한 화면에 모아 보는 관리 화면이라는 뜻이다. 저널 옆 패널(`JournalAsideKeepCard.vue`)은 진행 중 항목만 보여 준다.

### 1.2 화면·컴포넌트 (웹)

| 컴포넌트 | 위치 (`app/frontend-vue/src/features/prospective/`) | 역할 |
|---|---|---|
| `KeepPage.vue` | `keep/` (909줄) | `/keep` 전수관리 화면. Project 탭, 검색·필터, 진행/종결 두 그룹, 행 컨텍스트 메뉴, 태그 클라우드 |
| `ProspectiveKeepRegistModal.vue` | `keep/modals/` (573줄) | 등록·수정 모달. 말머리, 제목(maxlength 100), 일시 텍스트, 기한(flatpickr + "추출" 버튼), 위치, 본문(RichEditor 마크다운), 태그(Tagify), Project |
| `prospectiveKeepModal.ts` | `keep/stores/` | 모달 상태. 기본값 `prefixId:null, title:"", content:"", location:"", dateText:"", expireDt:"", projectId:null, tag:{tagListStrWithCtgr:""}` |
| `prospectiveKeepCategoryMap.ts` | `keep/stores/` | Keep 태그 카테고리 맵 (`GET /api/prospective/keep/tag/categories`) |
| `expireDt.ts` (+ `expireDt.spec.ts`) | `keep/utils/` (198줄) | `parseExpireDt`, `scanLastDate`, `resolveNearestYear`, `formatExpireDt`, `daysUntilExpire` |
| `prospectiveProject.ts`, `ProspectiveProjectManageModal.vue`, `ProspectiveProjectEditorForm.vue`, `ProspectiveKeepProjectMoveMenu.vue` | `project/` | Project 목록·관리(생성·수정·정렬·삭제)·Keep 이동 메뉴 |
| `JournalAsideKeepCard.vue` | `features/journal/components/` | 저널 옆 패널의 진행 중 Keep 카드 (`journal.ts` `fetchKeeps` → `GET /api/prospective/keeps`, scope 없음) |
| `personalPrefixOptions.ts` | `features/attachable/stores/` | 개인 말머리 선택지 (`GET /api/my/prefixes/options?contentType=PROSPECTIVE_KEEP`) |

### 1.3 전수관리 화면이 하는 일

- **조회**: `GET /api/prospective/keeps?scope=all`로 전 범위를 한 번에 받는다(페이징 없음). 이후 모든 필터는 클라이언트에서 거른다. 재조회하지 않는다.
- **Project 탭**: `Project 미지정`(기본) · `전체` · OPEN Project들(정렬 순). Project 관리 모달에서 생성·수정·정렬·삭제를 한다.
- **필터(AND)**: 검색어(제목·본문 부분 일치), 말머리, 태그(태그 클라우드 또는 행 태그 칩), `기한 지남`(종결 안 된 채 기한이 지난 항목만, 켜면 종결 그룹이 비게 됨), `중요`(IMPRTC).
- **두 그룹**
  - 진행 = OPEN·PENDING. `expireDt` 오름차순(지난 기한이 맨 위), 기한 없는 항목은 아래쪽에 서버 순서(`sortOrder`→`id`)대로. 안정 정렬이라 수동 순번은 기한 없는 항목 사이에서만 의미가 있다.
  - 종결 = RESOLVED(초록 체크, `완료`) · FAILED(빨강 X, `실패`) · EXPIRED(노랑 모래시계, `만료`) · DROPPED(회색 슬래시, `내려놓음`). 최근 종결 순(상태 변경 시각 `changedAt` → 완료 시각 `resolvedAt` → `updatedAt` → `createdAt` 내림차순, 같으면 `id` 내림차순; 웹은 서버 순서 — 2026-10-11 변경), 끝난 이유를 배지로 보여 준다.
- **행 표시**: 별(IMPRTC, 굵은 제목) · `보류` 배지(PENDING) · `요구` 배지(DEMANDED) · `[말머리]` · 제목 · 태그 칩 · 기한(진행 행에서 지난 기한은 경고색 + `N일 지남`) · 등록 년월.
- **조작**
  - 진행 행 체크박스: 즉시 `RESOLVED` (확인 없음). 종결 행에는 체크박스가 없고 라이프사이클 메뉴로 되돌린다.
  - 제목 클릭: 상세를 다시 받아(`GET /api/prospective/keep/{id}`) 수정 모달을 연다.
  - 행 컨텍스트 메뉴: 수정, 라이프사이클(진행 2 + 종결 4, EXPIRED는 기한이 있을 때만), Project 이동, 상태(요구·중요 스위치), 삭제.
  - 태그 클릭: 이 태그로 거르기 / 태그 프로필.
- **저장 후**: 목록 재조회, 저널 일자 새로고침, `journalStore.fetchKeeps()` (옆 패널 동기화).

### 1.4 백엔드

| 구분 | 파일 (`app/backend/src/main/java/io/nicheblog/dreamdiary/feature/`) | 요점 |
|---|---|---|
| 컨트롤러 | `prospective/keep/controller/ProspectiveKeepRestController.java` | 목록·상세·등록/수정·Project 이동·삭제·태그 카테고리 |
| | `prospective/project/controller/ProspectiveProjectRestController.java` | Project 목록·상세·등록·수정·삭제·정렬 |
| | `attachable/lifecycle/controller/LifecycleRestController.java`, `attachable/state/controller/StateRestController.java` | 공용 라이프사이클 설정·상태 토글 |
| DTO | `ProspectiveKeepDto` (`BaseAttachableDto` + Tag/Lifecycle/State 복합 모듈) | 아래 §2 |
| 엔티티 | `ProspectiveKeepEntity` → `prospective_keep` | soft delete(`deleted_at`) |
| 서비스 | `ProspectiveKeepService` (`BaseAttachableService`) | regist/modify 트랜잭션, `resolveOwnedProject`(Project 소유 확인), `applyPrefixSelection`, `preRegist`에서 `sortOrder = 사용자 마지막+1`, `preModify`/`preDelete` 소유 확인, 커밋 후 상세·태그 캐시 비움 |
| 매퍼 | `ProspectiveKeepMapstruct.updateFromDto` | `NullValuePropertyMappingStrategy.IGNORE`이지만 `content`(`MarkdownUtils.normalize`)·`expireDt`(`asLocalDate`) 식 매핑은 null이어도 적용됨 → §3.3 |
| 정책 | `AttachableContentLifecyclePolicy` / `AttachableContentStatePolicy` | KEEP 라이프사이클 6종 전부, 상태는 `DEMANDED`·`IMPRTC`만 |
| 가드 | `AttachableContentOwnershipGuard.assertOwned`, `JournalDayResolvedGuard.assertWritableForRef` | Keep은 소유자만 쓰기 가능. 일자 완결 잠금은 저널 타입에만 적용되고 Keep에는 영향 없음 |
| 보안 | `WebSecurityAdapter` | 모든 Keep API `@Secured(ROLE_USER, ROLE_MNGR)`. `http.csrf().disable()`. `CsrfInterceptor`(Referer 검사)는 `/`·`/**/*.do` 페이지에만 붙음 → `/api/**` 쓰기에 CSRF 토큰 불필요 |

### 1.5 DB 실데이터 (2026-10-06, 읽기 전용 조회)

| 항목 | 값 |
|---|---|
| `prospective_keep` | 전체 62, 살아 있음 60 (soft delete 2), 사용자 1명, 등록 2025-03 ~ 2026-10-04 (2025년 6 / 2026년 54) |
| `content_type` | 전부 `PROSPECTIVE_KEEP` |
| 라이프사이클 (`lifecycle`, row 없음 = OPEN) | OPEN 32 · PENDING 0 · RESOLVED 16 · DROPPED 8 · EXPIRED 3 · FAILED 1 (항목당 row 최대 1) |
| 상태 (`state`) | IMPRTC 0 · DEMANDED 0 · **`COLLAPSED` 16** (정책에 없는 레거시 키, 웹에서 토글 불가) |
| 기한 | 있음 21 / 없음 39. 진행(OPEN) 중 기한 있음 10, 그중 지난 것 0. 기한 오늘 1, 7일 이내 3 (라이프사이클 무관) |
| 기타 필드 채움 | `date_text` 21, `location` 19, `content` 20 (평균 126자), 제목 최대 53자 |
| Project | 살아 있는 4개 모두 OPEN (+삭제 1). Keep 26건 소속. 진행 중 기준: 미지정 22, Project별 6·2·1·1 |
| 말머리 | `PROSPECTIVE_KEEP` 활성 개인 말머리 3개, 말머리 붙은 Keep 40건 (20·11·9) |
| 태그 | 연결 82건, 서로 다른 태그 5개, 태그 붙은 Keep 51건 |
| `sort_order` | 최대 56, 서로 다른 값 56 / 60 → `sort_order=1`이 5건 중복 (순번 도입 이전 데이터) |

이 데이터에서 알 수 있는 것:
- 규모가 작다(60건). 전 범위를 한 번에 받고 클라이언트에서 거르는 웹 방식이 모바일에서도 충분하다.
- 진행 중 32건 중 22건이 기한이 없다. "오늘 할 일" 화면만으로는 대부분이 보이지 않으므로 기본 화면은 진행 목록이어야 한다.
- 중요·요구 상태는 아직 쓰지 않는다. PENDING도 0건이다.

---

## 2. 도메인 모델

```
ProspectiveKeep (prospective_keep, soft delete, 소유자 = created_by)
├─ id, contentType="PROSPECTIVE_KEEP"
├─ title (DB 200, 웹 UI 100자·필수, 서버 @NotBlank 없음)
├─ content (마크다운 원문) / markdownContent (서버 렌더 HTML, 비면 "-")
├─ location (≤200), dateText (≤200, 자유 텍스트 원문)
├─ expireDt (yyyy-MM-dd 또는 빈 값, 패턴 ^$|^\d{4}-\d{2}-\d{2}$)
├─ sortOrder (사용자 전역 순번, 등록 시 마지막+1)
├─ projectId → ProspectiveProject (0..1)
├─ prefix {id, name, textClass} / prefixId → prefix_content (0..1, 개인 말머리)
├─ lifecycle.lifecycleKey → lifecycle (0..1 row, 없으면 OPEN)
├─ state.list[].stateKey → state (0..n, 다중 속성 토글)
├─ tag.list[] {tagId, name, ctgr} / tag.tagListStr → tag_content (0..n)
└─ createdAt, updatedAt, createdBy …

ProspectiveProject (prospective_project, soft delete)
├─ id, title, description, sortOrder
└─ lifecycle: OPEN · PENDING · RESOLVED · DROPPED (OPEN만 웹 탭에 표시)
```

### 2.1 라이프사이클 (`LifecycleKey`)

| 키 | 표시 | 그룹 | 비고 |
|---|---|---|---|
| OPEN | 열림 | 진행 | 저장 row 없음. OPEN으로 설정하면 row 삭제 |
| PENDING | 보류 | 진행 | `보류` 배지 |
| RESOLVED | 완료 | 종결 | 체크박스 = 이 값 |
| FAILED | 실패 | 종결 | |
| EXPIRED | 만료 | 종결 | 기한(`expireDt`)이 있을 때만 고를 수 있음 (UI 규칙, 서버는 검사 안 함) |
| DROPPED | 내려놓음 | 종결 | |

`isTerminal` = RESOLVED·FAILED·EXPIRED·DROPPED. 활성 목록(`scope` 없음)은 종결 4종을 뺀다.

### 2.2 상태 (`StateKey`) — 라이프사이클과 별개인 다중 속성

| 키 | 표시 | 의미 |
|---|---|---|
| IMPRTC | 중요 | 별 아이콘 + 굵은 제목. 정렬은 바꾸지 않음 |
| DEMANDED | 요구 | 내가 담은 것과 외부에서 요구받은 것을 구분 |

레거시 `COLLAPSED` 16건이 남아 있다. 모바일은 모르는 상태 키를 무시한다.

---

## 3. API 목록

모든 응답은 `AjaxResponse { rslt, message, rsltObj, rsltList, rsltMap }`, 권한은 `@Secured(ROLE_USER, ROLE_MNGR)`이다.

### 3.1 읽기 (7)

| # | 엔드포인트 | 파라미터 | 응답 | 모바일 사용 |
|---|---|---|---|---|
| R1 | `GET /api/prospective/keeps` | `scope=all`(전 범위) \| 생략(활성 = OPEN·PENDING, 종결은 메모리에서 제외). `ProspectiveKeepSearchParam`: `tagId`, `projectId`, `projectUnassigned` (`projectId`와 `projectUnassigned`를 같이 보내면 `IllegalArgumentException`). 페이징·서버 검색 없음, 정렬 `sortOrder`→`id` | `rsltList: ProspectiveKeepDto[]` (lifecycle·state·tag·prefix 포함, Project는 `projectId`만) | 2-0 핵심. `scope=all` 한 번 받고 클라이언트 투영 |
| R2 | `GET /api/prospective/keep/{id}` | — | `rsltObj: ProspectiveKeepDto` (본문은 캐시, 소유 확인). **lifecycle 포함** — `getDetailDtoWithLifecycleByUser`가 캐시된 본문의 복사본에 목록과 같은 `applyKeepLifecycles`로 매 조회 부착(행이 없으면 `OPEN`, 2026-10-06 백엔드 수정. 그 전에는 없었음). 없는 ID면 `rslt=false` | 상세·편집 직전 재조회. 라이프사이클도 이 응답 값을 쓴다 |
| R3 | `GET /api/prospective/projects` | — | `rsltList` (Project + lifecycle) | Project 칩·이름 매핑 |
| R4 | `GET /api/prospective/project/{id}` | — | `rsltObj` | (미사용) |
| R5 | `GET /api/prospective/keep/tag/categories` | — | `rsltMap` (태그 카테고리 맵) | 태그 편집(2-5) |
| R6 | `GET /api/tags?contentType=PROSPECTIVE_KEEP` | `TagSearchParam` | `rsltList: TagDto[]` (빈도 크기 포함). `TagService.getOverallSizedTagList`("컨텐츠 타입과 무관하게")라 `contentSize`가 Keep 전용 건수라는 보장이 없다 | 태그 필터 선택지의 순서. 건수는 R1에서 직접 센다 |
| R7 | `GET /api/my/prefixes/options?contentType=PROSPECTIVE_KEEP` | — | 말머리 선택지 | 말머리 필터·편집 |

### 3.2 쓰기 (10)

| # | 엔드포인트 | 본문 | 응답 | 모바일 단계 |
|---|---|---|---|---|
| W1 | `PUT /api/lifecycles` | JSON `{id, contentType:"PROSPECTIVE_KEEP", lifecycleKey}` | `rslt`, 정책 위반은 HTTP 200 + `rslt=false` + `message` | 2-1 완료·되돌리기 |
| W2 | `POST /api/states` | JSON `{id, contentType:"PROSPECTIVE_KEEP", stateKey}` | `rslt`, `rsltSts: "ON"\|"OFF"` | 2-1 중요·요구 |
| W3 | `POST /api/prospective/keeps` | **multipart/form-data** (ModelAttribute, `@Valid`) — 필드는 §3.3 | `rsltObj` (`fromResponseWithObj`) | 2-2 빠른 추가 |
| W4 | `POST /api/prospective/keep/{id}` | W3과 같음 (**전체 객체**) | 같음 | 2-3 편집 |
| W5 | `PATCH /api/prospective/keep/{id}/project` | JSON `{projectId}` (null = 미지정) | `rslt` | 2-4 |
| W6 | `DELETE /api/prospective/keep/{id}` | — | soft delete. 복구 API 없음 | 2-4 |
| W7 | `POST /api/prospective/projects` | multipart (ModelAttribute) | | 웹 전용 유지 |
| W8 | `POST /api/prospective/project/{id}` | multipart | | 웹 전용 유지 |
| W9 | `DELETE /api/prospective/project/{id}` | — | soft delete, 소속 Keep은 미지정이 됨 | 웹 전용 유지 |
| W10 | `PUT /api/prospective/projects/sort-orders` | JSON `[id…]` (전체 순서) | | 웹 전용 유지 |

### 3.3 등록·수정 폼 필드 (W3·W4)

웹이 보내는 `FormData` 그대로:

| 필드 | 값 | 빈 값/누락 시 서버 동작 |
|---|---|---|
| `id` | 수정 시 ID | |
| `title` | 문자열 | IGNORE (기존 유지) — 단 UI는 필수 |
| `sortOrder` | 숫자 | IGNORE |
| `prefixId` | 숫자 또는 빈 값 | **말머리 해제** (`prefixContentService.applySelection`) |
| `projectId` | 숫자 또는 빈 값 | **Project 미지정으로 이동** |
| `location`, `dateText` | 문자열 | IGNORE (빈 문자열을 보내면 빈 값으로 덮음) |
| `expireDt` | `yyyy-MM-dd` 또는 빈 값 | **식 매핑이라 null이어도 덮음 → 기한 삭제** |
| `content` | 마크다운 | **식 매핑(`MarkdownUtils.normalize`)이라 null이어도 덮음 → 본문 삭제 위험** |
| `tag.tagListStr` | Tagify JSON `[{"value":"이름","data":{"ctgr":"…"}}]` | **빈 값이면 태그 전부 제거** (`TagProcService.procTags`가 기존/신규를 비교. 태그 복합 객체 자체가 null일 때만 건너뜀) |

**결론: 모바일 편집은 항상 상세를 다시 받아 전체 필드를 채워 보내야 한다(전체 재전송).** 부분 수정 API(PATCH)는 Project 이동(W5)뿐이다.

### 3.4 쓰기 위험과 대응

| 위험 | 내용 | 모바일 대응 |
|---|---|---|
| 부분 수정 데이터 손실 | §3.3 — 필드를 빠뜨리면 본문·기한·말머리·Project·태그가 지워짐 | 편집 화면은 R2로 받은 값으로 폼 전체를 채우고 W4는 항상 9개 필드를 모두 보냄. 태그는 `tag.list`로 Tagify JSON을 다시 만듦(상세의 `tagListStrWithCtgr` 형식은 구현 때 확인). 단위 테스트로 직렬화 고정 |
| `rslt=false` + HTTP 200 | 라이프사이클·상태 정책 위반, 등록/수정 실패가 200으로 옴. 현재 모바일 `request()`는 2xx면 성공으로 처리 | `requestAjax()` 래퍼: `rslt !== true`면 `message`로 `ApiError` 던짐. 공용 `AjaxResponse` 타입에 `rsltList`·`rsltMap`·`rsltSts` 추가 |
| 소유권 위반·검증 실패 | 예외 → 전역 예외 처리기의 HTTP 오류 응답 | 기존 `ApiError` 경로(서버 `message` 우선 표시) |
| 상태 토글 비멱등 | `POST /api/states`는 토글이라 두 번 누르면 원상태 | 요청 중 버튼 잠금, 성공·오류 모두 Keep 목록을 조회해 서버 현재 값 반영. 조회 실패 시 쓰기를 잠그고 GET만 재시도. POST 자동 재전송 금지 |
| 라이프사이클 | `PUT`은 설정(멱등). OPEN = row 삭제 | 완료 취소(실행 취소) = 직전 키로 다시 PUT. 재시도 안전 |
| 동시 편집 | 버전 필드·낙관적 잠금 없음 → 웹과 동시에 고치면 나중 저장이 이김 | 편집 진입 시 R2 재조회, 저장 직전 R2를 한 번 더 받아 `updatedAt`이 다르면 경고 후 선택 |
| multipart | ModelAttribute 바인딩이라 JSON 본문은 받지 않음 | 모바일 `request()`는 이미 `FormData`를 지원(Content-Type 자동). `tag.tagListStr` 같은 점 표기 키 그대로 사용 |
| CSRF·세션 | CSRF 비활성, 쓰기도 `Authorization: Bearer` + 쿠키 + `X-Client-Channel: MOBILE`로 동작. 채널별 동시 세션 제어는 로그인 시점 규칙 | 추가 작업 없음. 401은 기존 전역 핸들러(자동 로그아웃) |
| 캐시 | 상세 캐시는 커밋 후 비움 | 쓰기 후 R1 재조회(60건이라 비용 작음). 웹도 같은 방식 |
| 삭제 복구 불가 | soft delete지만 복구 API 없음 | 확인 대화상자 + "내려놓음" 권장 문구 |
| EXPIRED 규칙 | 서버는 기한 없는 EXPIRED를 막지 않음 | 클라이언트에서 기한 없으면 메뉴 비활성 (웹과 같음) |

### 3.5 백엔드 갭 (이번에는 바꾸지 않음)

| 갭 | 영향 | 모바일 우회 |
|---|---|---|
| Keep 순서 변경 API 없음 (`sortOrder`는 수정 폼으로만) | 드래그 정렬 = 항목마다 전체 재전송 → 비현실적 | 모바일 수동 정렬 미지원. 필요하면 Project처럼 `PUT /api/prospective/keeps/sort-orders` 추가 (별도 결정) |
| 서버 기한 계산 없음 | 빠른 추가에서 일시 텍스트 → 기한을 앱이 계산해야 함 | `expireDt.ts`를 그대로 포팅하고 `expireDt.spec.ts`도 함께 옮김 |
| 부분 수정(PATCH) 없음 | §3.3 위험 | 전체 재전송 |
| 낙관적 잠금 없음 | 마지막 저장이 이김 | `updatedAt` 비교 |
| 상태 "설정" API 없음 (토글만) | 재시도 위험 | 잠금 + `rsltSts` |
| 서버 측 검색·기한·라이프사이클 필터, 페이징 없음 | 데이터가 커지면 부담 | 현재 60건이라 클라이언트 투영으로 충분 |
| 반복(recurrence)·하위 할일·우선순위(중요 외) 없음 | 일반 할일 앱 기능 부족 | 도메인 철학상 의도된 범위로 보고 다루지 않음 |
| 삭제 복구 API 없음 | 실수 삭제 복구 불가 | 확인 대화상자 |
| ~~상세(R2)에 lifecycle 없음~~ **해결 (2026-10-06)** | (이전) 상세만 받으면 종결 항목도 `진행 중`으로 보임 | 백엔드 상세가 목록과 같은 방식으로 lifecycle 을 붙이도록 고쳤다(§3.1 R2). 모바일의 목록 병합·재조회는 없앴다. 바뀌기 전 백엔드에 붙으면 상세가 `진행 중`으로 보이므로 백엔드를 다시 띄운다 |

---

## 4. 모바일 현황

- 진입: 햄버거 드로어(`src/components/navigation/AppMenu.tsx`)의 `Keep 전수관리` → `src/navigation/appMenu.ts` `resolveMenuTarget`이 `/keep`을 `MenuPlaceholderScreen`(`준비 중`)으로 보냄.
- 하단 탭: `오늘 · 검색 · 나`. 드로어 메뉴 화면은 루트 스택(`RootStackParamList`)에 쌓인다(`navigateToMenu`, `pop: true`).
- API: `src/api/client.ts` `request()` — `X-Client-Channel: MOBILE` 강제, Bearer 토큰, `FormData` 지원, 401 전역 핸들러. `rslt=false` 검사는 없음. 공용 `AjaxResponse` 타입은 `rslt`·`rsltObj`만 있음(`tagcloud-thread-port-plan.md` §6.1 1-0의 남은 항목).
- 쓰기 화면은 아직 없다(1단계 조회 전용).

---

## 5. 모바일 할일 앱 화면 구성안

### 5.1 진입 위치: 드로어 vs 하단 탭

| 안 | 장점 | 단점 |
|---|---|---|
| A. 드로어 유지 (`준비 중` → `KeepHomeScreen`) | 탭 구조를 건드리지 않음. 다른 메뉴 화면(스레드·결산)과 같은 방식. 2-0에서 바로 가능 | 빠른 추가까지 두 번 눌러야 함 |
| B. 하단 탭 추가 (`오늘 · 검색 · Keep · 나`) | 할일 앱다운 접근성. 탭 아이콘에 진행 건수 배지 가능 | 탭 재편 필요, 1단계 리더 성격과 섞임 |

**권장: 2-0~2-1은 A, 빠른 추가(2-2)가 들어가면 B로 승격**(§7 질문 1). 화면 컴포넌트는 같게 만들어 두고 진입만 바꾼다.

### 5.2 화면 목록

| 화면 | 내용 |
|---|---|
| `KeepHomeScreen` | 헤더(`☰` · `Keep` · 검색 아이콘 · 필터 아이콘), Project 칩 줄, 보기 전환, 목록, 오른쪽 아래 `+` 버튼(2-2부터) |
| `KeepDetailScreen` | 읽기 전용 상세 + 하단 액션(완료, 라이프사이클, 중요/요구, 편집, Project 이동, 삭제) |
| `KeepEditScreen` | 전체 편집 폼(2-3) |
| `KeepQuickAddSheet` | 하단 시트(2-2) |
| `KeepActionSheet` | 행 길게 누르기·왼쪽 스와이프로 여는 액션 시트 |

### 5.3 `KeepHomeScreen` 레이아웃

```
┌────────────────────────────────┐
│ ☰  Keep                 🔍  ⚲ │
├────────────────────────────────┤
│ [전체] [미지정] [P1] [P2] …  → │  Project 칩 (가로 스크롤, OPEN만)
│ ( 진행 32 | 기한 | 종결 28 )    │  보기 전환 (세그먼트)
├────────────────────────────────┤
│ ☐ ★ [말머리] 제목       10.17  │  지난 기한: 경고색 + "N일 지남"
│     #태그 #태그  · P1   2026.09 │
│ ☐ 보류 제목                     │
│ …                               │
│                            (+)  │  빠른 추가 (2-2)
└────────────────────────────────┘
```

- **보기 전환** (같은 R1 데이터의 클라이언트 투영)
  - `진행` (기본): OPEN·PENDING, 웹과 같은 정렬(기한 오름차순, 기한 없음은 아래에 서버 순서).
  - `기한`: 진행 항목 중 기한 있는 것만 `지남` / `오늘` / `7일 이내` / `이후`로 묶음. 일반 할일 앱의 "오늘"에 해당. 이름을 `오늘`이 아닌 `기한`으로 하는 이유: 진행 32건 중 22건은 기한이 없어 "오늘"만으로는 할 일이 안 보인다.
  - `종결`: 종결 4종, 최근 종결 순(2026-10-11, §4.14), 끝난 이유 배지/아이콘(웹 색 그대로).
- **Project 칩**: `전체` · `미지정` · OPEN Project(정렬 순). 권장 기본 `전체`(§7 질문 3). `전체`에서는 행에 Project 이름을 작게 표시.
- **필터 시트**(`⚲`): 말머리(R7), 태그(R6), `중요`, `기한 지남`. 웹과 같은 AND. 켜진 필터는 칩 줄 아래 요약 칩으로 보이고 탭으로 해제.
- **검색**(`🔍`): 헤더 검색창, 제목·본문 부분 일치, 입력하는 동안 거름(클라이언트 투영이라 서버 호출 없음).
- **행 조작**
  - 체크박스 탭 또는 **오른쪽 스와이프 = 완료(RESOLVED)**. 확인 없이 바로 처리하고 하단 스낵바 `완료함 · 실행 취소`(5초, 직전 라이프사이클로 되돌림).
  - **왼쪽 스와이프 / 길게 누르기** = 액션 시트: 보류·실패·만료(기한 있을 때만)·내려놓음·열림, 중요/요구 토글, Project 이동, 편집, 삭제.
  - 행 탭 = 상세.
- 당겨서 새로고침 = R1 + R3 재조회. 화면 포커스 때도 재조회(웹에서 바뀐 내용 반영).

### 5.4 `KeepDetailScreen`

- 머리: `[말머리]` 제목, 라이프사이클 배지, 별(중요)·`요구` 배지.
- 필드: 일시(`dateText` 원문), 기한(`expireDt` + `D-3` / `N일 지남`), 위치, Project, 태그 칩, 등록/수정 일시.
- 본문: 2-0은 `content` 마크다운 원문을 텍스트로 표시(스레드 본문과 같은 수준, HTML 렌더링 여부는 `tagcloud-thread-port-plan.md` §7 질문과 함께 결정).
- 하단 액션 바: `완료`(진행일 때) / `다시 열기`(종결일 때), `⋯`(액션 시트), `편집`(2-3부터).

### 5.5 `KeepQuickAddSheet` (2-2)

```
┌────────────────────────────────┐
│ 새 Keep                         │
│ [제목 ______________________]  │  필수, 100자
│ [일시  10/17, 24 ___________]  │  선택. 입력하면 아래 기한 미리보기
│  기한 2026-10-24 (D-18)  ✎      │  scanLastDate 결과, 탭하면 날짜 선택기
│ Project: [현재 칩]  ★ 중요       │
│                       [저장]   │
└────────────────────────────────┘
```

- 키보드가 바로 뜨고, 제목만 입력해도 엔터로 저장.
- Project 기본값 = 현재 선택된 칩(`전체`면 미지정).
- `★ 중요`를 켜고 저장하면 등록 응답의 ID로 `POST /api/states`(IMPRTC)를 이어서 보냄(등록 API에는 상태 필드가 없음).
- 저장 후 시트를 닫지 않고 비워서 연속 입력, 목록은 재조회.
- 말머리·위치·본문·태그는 편집 화면에서.

### 5.6 `KeepEditScreen` (2-3)

- 진입 시 R2로 최신 상세를 받아 폼 전체를 채움.
- 필드: 말머리(선택), 제목, 일시 텍스트 + 기한(자동 추출 버튼 + 날짜 선택기 + 지우기), 위치, Project, 본문(마크다운 원문 여러 줄 입력), 태그(2-3은 읽기 전용 표시, 기존 값 그대로 재전송).
- 저장: R2 재조회로 `updatedAt` 비교 → W4에 9개 필드 전부 → `rslt` 확인 → 상세·목록 갱신.
- 뒤로 가기 때 바뀐 내용이 있으면 버림 확인.

---

## 6. 단계별 진행안

1단계(조회 전용) 이후 모바일 첫 쓰기 기능이다. 단계마다 독립적으로 쓸 수 있는 가치가 생기도록 나눴다.

| 단계 | 내용 | API | 선행/주의 |
|---|---|---|---|
| **2-0. 읽기 전용 목록·상세** | 드로어 `Keep 전수관리` → `KeepHomeScreen`(`준비 중` 교체). Project 칩, `진행 / 기한 / 종결` 보기, 검색, 필터 시트(말머리·태그·중요·기한 지남), 상세 화면. `formatExpireDt`·`daysUntilExpire` 포팅. 타입 `KeepItem`, `KeepProject`, 공용 `AjaxResponse`에 `rsltList`·`rsltMap` | R1(`scope=all`), R3, R6, R7, R2 | 서버 데이터를 바꾸지 않으므로 1단계 계약(`screen-spec.md` §8) 안. 레거시 `COLLAPSED` 무시 |
| **2-1. 완료·라이프사이클·상태** | 왼쪽 드래그로 완료 버튼 노출, 상태 메뉴 라이프사이클 6종(EXPIRED는 기한 있을 때만). 서버 설정 응답 확인 후 Keep 목록만 재조회, 요청 잠금, 저장 실패와 조회 실패 구분. 중요·요구 스위치는 POST 한 번 후 Keep 목록을 조회하며 오류에도 서버 현재 값을 확인 | W1, W2 | 모바일 첫 쓰기. `screen-spec.md` §4.14·§8의 Keep 한정 계약 |
| **2-2. 빠른 추가** | `+` 버튼 → `KeepQuickAddSheet`. `parseExpireDt`·`scanLastDate`·`resolveNearestYear` 포팅 + `expireDt.spec.ts` 테스트 포팅. multipart 직렬화 함수 `toKeepFormData()`(단위 테스트). 이 시점에 하단 탭 승격 검토(§7 질문 1) | W3 (+ W2) | 제목 필수·100자 클라이언트 검증 |
| **2-3. 편집** | `KeepEditScreen` 전체 편집. 전체 재전송, `updatedAt` 비교, 말머리 선택(R7), 기한 날짜 선택기. 태그는 재전송만 | R2, W4 | §3.3 데이터 손실 위험 → 직렬화 테스트에 "모든 필드 포함" 케이스 필수 |
| **2-4. Project 이동·삭제** | 액션 시트 `Project 이동`(OPEN Project 목록 + 미지정), `삭제`(확인 대화상자, 복구 불가 안내) | W5, W6 | |
| **2-5. (선택) 확장** | 태그 편집(R5 카테고리 + 자동완성), 오늘 탭 연동(저널 옆 패널처럼 진행·기한 임박 Keep 요약), 하단 탭 배지, 로컬 알림(기한 당일) | R5, R6, W4 | 필요할 때 결정. Project 관리(W7~W10)는 웹 전용 유지 |

**가장 작은 가치 있는 첫 단계 = 2-0 + 2-1**: 읽기 API 5개와 쓰기 API 2개로, 폰에서 Keep 목록을 보고 바로 완료 처리할 수 있다. 쓰기 위험이 가장 낮은 두 API(멱등 설정 + 단순 토글)만 쓴다.

### 6.1 진행 현황 (2026-10-06)

상태 표기는 `tagcloud-thread-port-plan.md` §6.1과 같다: **완료**(실기기 확인까지), `구현·미검증`(코드 반영, 실기기 미확인), 예정.

| 단계 | 상태 | 내용 |
|---|---|---|
| 2-0 읽기 전용 목록·상세 | `구현·미검증` | 아래 2-0 구현 내용. 타입 검사(`tsc --noEmit`) 0, `expo export --platform android` 번들 생성 확인. 에뮬레이터 확인은 남음(PC 백엔드가 꺼져 있었음, `screen-spec.md` §11) |
| 2-1 완료·라이프사이클·속성 | `구현·실기기 미검증` | 왼쪽 드래그 완료 버튼·여섯 단계 상태 메뉴·멱등 설정 후 Keep 목록 재조회. 중요·요구 스위치·요청 잠금·응답 유실 시 GET 확인·조회 실패 시 GET만 재시도 |
| 2-2 ~ 2-5 | 예정 | 현재 앱 쓰기 범위는 완료·라이프사이클·중요/요구 토글이다 |

**최근 완료 진행(2026-10-07, 구현·실기기 미검증)**

서버는 현재 완료 전이 시각을 저장·조회하고 웹·앱은 완료 후 1시간 미만을 최근 완료로 표시한다. 앱의 진행·기한 보기 하단 그룹은 기본으로 접히며 종결 보기에서는 최근 완료를 먼저 표시한다. 첫 5건·전체 펼침, 필터 적용, 화면 순서에 따른 복사와 API 호출 없는 만료 전환을 제공한다. 표시 계약은 `screen-spec.md` §4.14와 Prospective screen/interaction/component spec을 따른다. 완료 드래그 버튼과 여섯 단계 메뉴는 구현·실기기 미검증이며 저장 계약은 `screen-spec.md` §4.14를 따른다.

**2-0 구현 내용 (2026-10-06)**

- 진입: 드로어 `Keep 전수관리`(`/keep`) → `KeepHomeScreen`(루트 스택, `준비 중` 대체). `appMenu.ts` `MENU_URL.KEEP` + `resolveMenuTarget`. 하단 탭은 그대로(`오늘 · 검색 · 나`).
- 조회: R1(`scope=all`) + R3 + R6 + R7을 병렬로 받는다. R1만 실패하면 오류 + `다시 시도`, 보조 목록(R3·R6·R7) 실패는 목록 표시를 막지 않는다(Project 칩 안내 문구, 태그·말머리 선택지는 목록에서 모은 값). 읽기 응답이 HTTP 200 + `rslt=false`면 `message`로 오류 처리(`assertReadOk`, 쓰기용 래퍼는 2-1). 화면 복귀 시 조용히 갱신, 당겨서 새로고침.
- 투영(`src/utils/keepProjection.ts`): Project 범위(기본 `전체` · `미지정` · OPEN Project) → 말머리·태그·기한 지남·중요·검색어 AND(웹 `filtered`와 같은 규칙) → 보기 `진행`(웹 정렬: 기한 오름차순, 기한 없음은 아래에 서버 순서) / `기한`(진행 중 기한 있는 것만 `지남 / 오늘 / 7일 이내 / 그 이후`) / `종결`(최근 종결 순, `keepTerminalSort.ts`). 보기 탭에 필터 적용 후 건수.
- 검색: 제목·본문 부분 일치, `검색` 버튼·키보드 검색 키로만 적용(검색 탭과 같은 원칙). 필터는 하단 시트(`KeepFilterSheet`: 기한 지남 · 중요 · 말머리 · 태그, 같은 칩을 다시 누르면 해제). 걸린 조건은 목록 위 칩(눌러서 해제) + `초기화`. 행의 태그 칩을 누르면 태그 필터 토글.
- 행: 중요 `★` + 굵은 제목, `[말머리]`(웹과 같은 `#287D94`), 보류·종결 배지(완료 초록 · 실패 빨강 · 만료 노랑 · 내려놓음 회색 + 아이콘), 요구 배지, `기한 ~10/24 · D-3`(진행 중 지난 기한은 경고색 `N일 지남`, 종결은 날짜만), `일시 …`, `📁 Project`(전체 범위에서만), 등록 년월, 태그 칩. 레거시 `COLLAPSED` 상태는 무시.
- 상세 `KeepDetailScreen`: R2로 다시 읽고(로드 전에는 목록 항목을 임시 표시), 라이프사이클은 R2 응답 값(백엔드 수정 후). Project 이름은 목록에서 넘겨받고, 목록 항목 없이 열거나 당겨서 새로고침하면 R3을 다시 받아 찾는다. 배지(라이프사이클·중요·요구), 제목, 기한(D-day)·일시·위치·Project·말머리·태그·등록·수정, 본문(마크다운 → 평문, `src/utils/markdownText.ts`, 새 의존성 없음).
- 복사·공유: 상세 헤더 `복사`·`공유`(1건 텍스트), 목록 위 `JournalCopyShareBar`(현재 보기·필터 결과, 기한 보기는 묶음 머리 포함). 웹에 Keep 복사가 없어 1단계 저널 복사와 같은 CRLF·빈 줄 규칙으로 새로 정했다(`src/utils/keepCopy.ts`, `screen-spec.md` §4.8).
- 파일: `src/types/prospectiveKeep.ts`, `src/utils/keepExpire.ts`(웹 `formatExpireDt`·`daysUntilExpire` 포트), `src/utils/keepProjection.ts`, `src/utils/keepCopy.ts`, `src/utils/markdownText.ts`, `src/components/keep/KeepBadges.tsx`, `src/components/keep/KeepFilterSheet.tsx`, `src/screens/KeepHomeScreen.tsx`, `src/screens/KeepDetailScreen.tsx`, 수정 `src/api/dreamDiaryApi.ts`(읽기 함수 5개), `src/types/auth.ts`(`AjaxResponse`에 `rsltList`·`rsltMap`), `src/navigation/AppNavigator.tsx`, `src/navigation/appMenu.ts`.
- 계획(§5.3)과 다른 점: 헤더 `🔍`·`⚲` 아이콘 대신 목록 위 검색줄 + `필터` 버튼, 검색은 입력 중 즉시가 아니라 제출 시에만(사용자 결정), 오른쪽 아래 `+`·체크박스·스와이프 없음(2-0은 읽기 전용).

---

## 7. 결정이 필요한 질문 (권장 기본값)

1. **진입 위치** — 드로어만 쓸지, 하단 탭으로 올릴지.
   → 권장: 2-0·2-1은 드로어(`준비 중` 교체), 2-2(빠른 추가) 이후 하단 탭 `오늘 · 검색 · Keep · 나`로 승격.
2. **이름과 어휘** — "할일"로 바꿀지, `Keep`을 유지할지. 종결 4종을 "완료" 하나로 줄일지.
   → 권장: 메뉴는 웹과 같은 `Keep 전수관리`, 화면 제목 `Keep`. 종결 4종과 색은 웹 그대로 두고, 빠른 조작(체크·스와이프)만 `완료`에 연결.
3. **기본 Project 범위** — 웹 기본은 `Project 미지정`.
   → 권장: 모바일 기본 `전체`(행에 Project 이름 표시). 폰에서는 한 목록에서 다 보는 편이 낫고, 진행 중 32건이라 길지 않다. 마지막 선택은 앱 실행 중 기억.
4. **완료 동작** — 확인 대화상자를 띄울지.
   → 권장: 확인 없이 즉시 RESOLVED + 5초 `실행 취소` 스낵바(웹 체크박스도 확인 없음).
5. **`기한` 보기 정의** — 무엇을 포함할지.
   → 권장: 진행 항목 중 기한 있는 것만, `지남 / 오늘 / 7일 이내 / 이후` 묶음. 기한 없는 항목은 `진행` 보기에서만.
6. **기한 지난 항목 자동 만료** — 자동으로 EXPIRED 처리할지.
   → 권장: 하지 않음(웹·도메인 규칙과 같음). `기한 지남` 필터와 경고색으로만 알림.
7. **빠른 추가 필드** — 어디까지 받을지.
   → 권장: 제목(필수) + 일시 텍스트(기한 자동 추출, 수정 가능) + Project(현재 칩) + 중요 토글. 말머리·위치·본문·태그는 편집에서.
8. **본문 편집·표시** — 마크다운 에디터를 넣을지.
   → 권장: 원문 여러 줄 텍스트 입력·표시. 렌더링 라이브러리 도입은 스레드 본문 렌더링 결정과 함께.
9. **태그 편집 시점** — 2-3에 넣을지.
   → 권장: 2-3은 태그 읽기 전용(기존 값 재전송), 편집은 2-5. 현재 태그 5종뿐이라 급하지 않음.
10. **수동 정렬** — 드래그로 순서 바꾸기를 지원할지.
    → 권장: 지원하지 않음. 백엔드 정렬 API가 없고, 웹도 진행 그룹은 기한 순이라 수동 순번의 의미가 작다. 필요해지면 백엔드에 Keep 정렬 API를 추가하는 별도 작업으로.
11. **동시 편집 보호** — 버전 필드가 없는데 어떻게 할지.
    → 권장: 저장 직전 상세 재조회 후 `updatedAt`이 다르면 "웹에서 바뀌었습니다 — 덮어쓰기 / 취소" 확인. 백엔드 변경 없음.
12. **모바일 삭제 허용** — 복구 API가 없다.
    → 권장: 허용(2-4)하되 확인 대화상자에 "복구할 수 없습니다. 끝난 항목이면 `내려놓음`을 권장합니다" 문구.
13. **Project 관리** — 모바일에서 생성·수정·정렬·삭제할지.
    → 권장: 웹 전용 유지. 모바일은 칩 표시와 Keep 이동(W5)만.
14. **오프라인** — 네트워크 없이 추가·완료를 쌓아 둘지.
    → 권장: 지원하지 않음. 실패하면 낙관적 갱신을 되돌리고 오류 토스트.
15. **레거시 `COLLAPSED` 상태 16건** — 정리할지.
    → 권장: 모바일은 무시. DB 정리는 이 작업 범위 밖(별도 결정, 백엔드/데이터 작업).
16. **저널 연동** — 오늘 탭에 Keep을 보여 줄지.
    → 권장: 2-5로 미룸. 웹 옆 패널처럼 진행·기한 임박 요약 카드 정도로.
