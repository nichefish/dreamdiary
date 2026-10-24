# 태그 클라우드 · 저널 스레드 모바일 포팅 계획

- 작성일: 2026-10-06 (브랜치 `dev_0.30.0`)
- 목적: 웹(`app/frontend-vue`)에서 "기록과 흐름을 한눈에" 보여주는 태그 클라우드·스레드를 모바일로 옮기기 위한 조사와 단계별 계획
- 범위: 조사·설계만. 코드 변경 없음. 일/주/월 뷰의 날짜 선택 달력은 다른 워크스트림이 작업 중이므로 이 계획은 그 컴포넌트와 겹치지 않게 배치한다.
- 갱신: 2026-10-06 — 탭 재편 결정·태그 탭 제거, 기간 태그 클라우드 패널 완료 반영, 단계를 "1단계 읽기 전용 → 2단계 쓰기" 순서로 재배열(§6)
- 갱신: 2026-10-06 저녁 — 기간 스레드 섹션·엔트리 상세 태그·복사/공유·태그별 기록 화면(`TagResultScreen`) 구현 반영, `TagExploreScreen` 삭제. 실기기 미검증(§6.1)
- 관련 문서: `api-contract-audit.md` (선행 계약 수정 항목), `screen-spec.md` (모바일 화면 계약 — 1단계 목표·범위는 §1)

---

## 1. 웹 현황 — 태그 클라우드

### 1.1 화면·컴포넌트

| 컴포넌트 | 위치 | 역할 |
|---|---|---|
| `JournalTagCloudHeader.vue` | `features/journal/day/components/` | 일자·일기·꿈 3행 태그 클라우드 + 하단에 `JournalPeriodThreadSummary`. 일간(`JournalDayDaily.vue`)·주간·월간(`JournalDayMonthly.vue`)·달력(`JournalDayCalendar.vue`) 상단 카드에 `v-if="store.showTagCloud"`로 표시 |
| `TagCloud.vue` | `shared/ui/tag/` | 공용 클라우드. 태그별 `[ctgr]` + 이름 + 건수(`contentSize`), 크기 `tagClass`(`ts-1`~`ts-9`)·색 `textClass`는 **서버가 빈도·태그 프로필로 확정**해 내려줌. 로딩·오류·빈 상태 문구 |
| `JournalAside.vue` | `features/journal/day/components/` | 옆 패널의 `showTagCloud` 토글(체크박스), 미니 달력, 필터(일기/꿈 표시, 키워드, 라이프사이클, 챕터 말머리) |
| `useJournalTagMenu.ts` | `features/journal/shared/composables/` | 태그 클릭 컨텍스트 메뉴 |
| 스토어 `journal.ts` | `fetchTagCloud` / `getTagPeriodParams` / `normalizeTagCloudList` | 기간·섹션별 조회, 중복 요청 공유, 늦게 온 응답 폐기 |

### 1.2 보여주는 것과 상호작용

- 현재 보고 있는 **기간**(일간=그날, 주간=그 주, 월간·달력=그 달) 안에서 쓰인 태그를 일자 태그 / 일기 태그 / 꿈 태그 세 줄로 보여준다. 글자 크기 = 빈도, 숫자 = 해당 기간 사용 건수.
- 기간이 바뀌면(`yy`, `mnth`, `weekStartDt`, `dailyStdrdDt`, `viewType` watch) 자동 재조회.
- 태그 클릭 → 컨텍스트 메뉴:
  - **검색**: 일자 태그면 `openDayFilterModal`(연도 선택 + 그 태그가 붙은 일자 목록), 일기·꿈 태그면 엔트리 검색 팝업(`journal-entry-search`, `tagIds`·`type`).
  - **검색에 추가**: 이미 열린 엔트리 검색에 태그를 AND 조건으로 추가.
  - **태그 프로필**: 태그 설명·색 등 프로필 보기/편집.

### 1.3 API

| 용도 | 엔드포인트 | 파라미터 | 응답 |
|---|---|---|---|
| 일자 태그 클라우드 | `GET /api/journal/day/tags` | 기간: `stdrdDt` \| `weekStartDt` \| `yy`+`mnth` (`TagSearchParam`, 좁은 기간 우선) | `rsltList: TagDto[]` |
| 일기/꿈 태그 클라우드 | `GET /api/journal/entry/tags` | 위 기간 + `type=DIARY\|DREAM`(필수) | `rsltList: TagDto[]` |
| 일자 태그 → 연도 | `GET /api/journal/day/tag/{tagId}/years` | — | `rsltList: Integer[]` |
| 일자 태그 → 일자 목록 | `GET /api/journal/days?viewType=SEARCH&tagId&yy` | `yy` 생략 시 전체 연도 | `rsltList: JournalDayDto[]` |
| 엔트리 태그 → 엔트리 | `GET /api/journal/entries?type&tagIds=…` | `type` 필수, `tagIds` 반복 키, `sort`, `searchKeywords`, `states` | `rsltList: JournalEntryDto[]` |
| 태그 프로필 | `GET /api/tags/{tagId}/profile` (`POST` 저장) | `contentType` 등 | `rsltObj` |
| 태그 카테고리 | `GET /api/journal/day/tag/categories`, `GET /api/journal/entry/tag/categories?type` | — | `rsltMap` |

`TagDto`: `id`, `tagCategoryId`, `ctgr`(기본 `""`), `name`, `contentSize`, `tagClass`, `textSemantic`, `textClassCd`, `textClass`.

---

## 2. 웹 현황 — 저널 스레드

스레드 = 여러 날짜의 일기·꿈 엔트리를 하나의 주제(예: "이직 준비", "반복되는 꿈")로 묶는 개인 컬렉션. 말머리(prefix)·라이프사이클(OPEN/PENDING/RESOLVED)·태그 집계·연관 스레드·댓글을 가진다.

### 2.1 화면·컴포넌트

| 컴포넌트 | 라우트/위치 | 역할 |
|---|---|---|
| `JournalPeriodThreadSummary.vue` | 태그 클라우드 카드 하단 | **기간 스레드 요약**: 현재 기간(일/주/월/연)에 엔트리가 등장한 스레드를 칩으로 표시(말머리 배지 + 제목 + "N건"). 월간·연간은 10개 초과 시 펼치기. 칩 클릭 → 스레드 상세 모달. 결과가 없으면 영역 자체를 숨김 |
| `JournalThreadList.vue` | `/thread` (`thread-list`) | 목록: 말머리 탭(전체/각 말머리/말머리 없음), 라이프사이클 필터, 제목 검색, 태그 필터(AND, `[ctgr]` 배지), 페이지네이션 표(번호·말머리·제목·소속 엔트리 수·기간·댓글 수·라이프사이클) |
| `JournalThreadDetailPage.vue` / `JournalThreadDetailModal.vue` + `JournalThreadDetailContent.vue` | `/thread/:id` 또는 전역 모달 | 상세: 말머리·라이프사이클·제목·소속 기간, 작성자/일시, 본문(markdown), **소속 엔트리 태그 집계**, 연관 스레드(추가·삭제·"엔트리 합쳐 보기" 토글), **소속 엔트리를 날짜별 그룹**(빌려온 엔트리는 출처 칩), 댓글. 복사·TXT 내보내기·이력·수정 |
| `JournalThreadEditPage.vue`, `JournalThreadRegistModal.vue`, `JournalThreadEditorForm.vue` | `/thread/:id/edit`, 모달 | 등록·수정(말머리, 제목, 본문, 첨부) |
| `JournalThreadPickerModal.vue` | 상세의 연관 스레드 추가 | 키워드로 스레드 검색 후 선택 |
| 엔트리 메뉴 "스레드에 추가" (`useEntryThreadMembership.ts`, `journalThreadMembership.ts`) | 일간/주간/월간 엔트리 | **스레드 후보(candidates)**: 최근·관련 스레드 후보 목록, 키워드·말머리 필터, 해결된 스레드 포함 토글, 소속 토글, 새 스레드 만들고 바로 소속 |
| 챕터/엔트리 스레드 칩 (`JournalChapterItem.vue`) | 일자 화면 | 챕터가 접혔을 때 소속 스레드 칩(`threadTitle`) 표시, 클릭 → 상세 모달 |

### 2.2 API

| 용도 | 엔드포인트 | 파라미터 | 응답 |
|---|---|---|---|
| 기간 스레드 요약 | `GET /api/journal/threads/period-summary` | `viewType=DAILY&stdrdDt` \| `WEEKLY&weekStartDt` \| `LIST&yy&mnth` \| `ANNUAL&yy` | `rsltList: JournalThreadPeriodSummaryDto[]` (`threadId`, `title`, `prefix`, `entryCount`, `firstEntryDate`). 월간은 `entryCount` 내림차순 |
| 스레드 목록 | `GET /api/journal/threads` | `page`(0-base), `size`(기본 10), `searchType=title&searchKeyword`, `prefixId` \| `noPrefixYn=Y`, `lifecycleKey`, `tagIds`(반복 키) | `rsltObj: Page<JournalThreadDto>` (`content`, `totalElements`, `totalPages`, `number`, `size`) |
| 스레드 상세 | `GET /api/journal/threads/{id}` | — | `rsltObj: JournalThreadDto` |
| 소속 엔트리 | `GET /api/journal/threads/{id}/entries` | `relatedThreadIds`(반복, 선택) | `rsltList: JournalEntryDto[]` (`stdrdDt`, `sourceThreadId` 등). 서버 정렬 일자 → 챕터 `sortOrder` → 엔트리 `sortOrder` → ID 오름차순, `reflectionList`·라이프사이클 채움, `threadList`는 비어 옴 |
| 연관 스레드 | `GET /api/related/JOURNAL_THREAD/{id}` | — | 연관 콘텐츠 목록 |
| 엔트리의 소속 스레드 | `GET /api/journal/entries/{entryId}/threads` | — | `rsltList: JournalThreadEntryDto[]` |
| 스레드 후보 | `GET /api/journal/threads/candidates` | `entryId`(필수), `keyword`, `prefixId`, `includeResolved`(기본 false), `limit`(기본 7) | `rsltList: JournalThreadCandidateDto[]` (`id`, `title`, `prefix`, `lifecycleKey`, `membershipCount`, `lastMembershipAt`, `member`) |
| 소속 추가/제거 | `POST /api/journal/threads/{id}/entries` (`entryId`, `sortOrder`) / `DELETE /api/journal/threads/{id}/entries/{entryId}` | form | `rsltObj` |
| 등록·수정·삭제 | `POST /api/journal/threads`, `POST /api/journal/threads/{id}`, `DELETE /api/journal/threads/{id}` | multipart | `rsltObj` |
| 말머리 옵션 | `GET /api/my/prefixes/options` | 콘텐츠 타입 | `rsltList` |
| 내보내기 | `GET /api/journal/threads/{id}/export?includeReflection` | — | text/plain |

`JournalThreadDto`: `id`, `title`, `content`, `markdownContent`, `prefix`, `prefixId`, `membershipCount`, `firstEntryDate`, `lastEntryDate`, `lifecycle`, `tag`(소속 엔트리 태그 집계), `comment`, `file`, `history`.

**추가 API 없이 쓸 수 있는 데이터**: 일자 조회 응답(`/api/journal/days`)의 각 엔트리에 이미 `threadList: JournalThreadEntryDto[]`(`threadId`, `threadTitle`, `entryContentType`)가 채워져 온다(`JournalDayQueryService` 293행). 모바일 타입에만 없다.

---

## 3. 모바일 현황

> 아래는 조사 시점(2026-10-06, 구현 전) 스냅샷이다. 이후 태그 탭 제거·기간 태그 클라우드 패널·태그/스레드 칩·계약 정리가 반영되었으며, 현재 상태는 §6.1 진행 현황을 따른다.

- 하단 탭(`src/navigation/AppNavigator.tsx`): **오늘**(📖, `TodayScreen` — 일/주/월 토글: `JournalDailyView`·`JournalWeeklyView`·`JournalMonthlyView`) · **태그**(🏷️, `TagExploreScreen`) · **검색**(🔍, `SearchScreen`) · **나**(👤, `ProfileScreen`). 스택: `EntryDetail`, `Login`.
- `TagExploreScreen`: 월 단위(‹ 월 ›)로 일자/일기/꿈 태그를 **칩 목록**으로 표시(크기 차등 없음, `[ctgr] 이름 건수`). 일자 태그 선택 → 연도 범위 + `SEARCH` 일자 목록("오늘 탭에서 보기 →"), 일기·꿈 태그 선택 → 해당 타입 엔트리 목록(전체 기간) → `EntryDetail`.
  - 한계: 월 단위만(일·주 기간 없음), `tagClass`/`textClass` 버려서 클라우드 표현 불가, 저널 화면과 분리.
- 스레드 관련 코드: **없음**(`src/` 전체에 thread 문자열 없음). `JournalEntry` 타입에 `threadList`도 없음.
- 선행 계약 이슈(`api-contract-audit.md`): 일자 조회에 `showDiaries/showDreams` 미전송(웹 기본 true), `TagCloudItem`이 `tagClass/textClass` 누락.

---

## 4. 정보 구조(IA) 재설계

### 4.1 문제

웹은 저널 본문 바로 위(또는 옆)에 그 기간의 태그 클라우드와 스레드 요약을 함께 둔다. 사용자는 "이번 주에 뭘 썼나"와 "이번 주에 어떤 흐름이 있었나"를 한 화면에서 본다. 모바일은 저널(오늘 탭)과 태그(태그 탭)가 다른 탭이고, 태그 탭은 오늘 탭의 기간과 연동되지 않으며, 스레드는 아예 없다.

### 4.2 대안

**A. 기간 요약 패널(접이식) — 오늘 탭 안에 통합**

일/주/월 뷰 상단(날짜 헤더·달력 아래, 엔트리 목록 위)에 접이식 "이번 기간 요약" 카드. 접힘: 한 줄 요약(상위 태그 3~5개 + "스레드 N개"). 펼침: 일자/일기/꿈 태그 클라우드(가로 줄바꿈, 크기·색 차등) + 기간 스레드 칩. 태그 탭(🏷️)은 "탐색"(전체 기간·연도별 검색)으로 역할 축소 또는 제거, 대신 **스레드 탭** 신설.

- 장점: 웹과 같은 "기록+흐름" 동시 노출. 기간이 뷰와 자동 연동(같은 `stdrdDt`/`weekStartDt`/`yy·mnth`). 기존 API 그대로 재사용. 접힘 상태 기억으로 공간 부담 조절.
- 단점: 오늘 탭 세로 길이 증가(특히 일간은 엔트리가 적어 요약이 더 커 보임). 날짜 선택 달력 워크스트림과 같은 화면 상단을 공유 → 배치 조율 필요. 요청 수 증가(기간당 태그 3 + 스레드 1).

**B. 저널 뷰 위 세그먼트 "기록 | 흐름" 전환**

오늘 탭 상단 일/주/월 토글 아래에 `기록 / 흐름` 세그먼트. "흐름"을 고르면 같은 기간의 태그 클라우드 + 스레드 요약 + (월간이면) 스레드 상위 목록을 전체 화면으로.

- 장점: 각 모드가 화면 전체를 써서 폰에서 읽기 좋음. 기간 공유는 A와 동일. 기록 모드 레이아웃은 그대로라 달력 워크스트림과 충돌이 적음.
- 단점: 엔트리와 흐름을 "동시에" 보지는 못함(한 번 탭해야 함). 세그먼트가 두 단(일/주/월 + 기록/흐름)으로 겹쳐 조작이 복잡해질 수 있음.

**C. 하단 시트(bottom sheet) 요약**

오늘 탭 우하단 플로팅 버튼 또는 헤더 아이콘 → 기간 요약을 하단 시트(반쯤/전체 높이)로 띄움. 시트 안에서 태그·스레드 탭 시 결과로 이동.

- 장점: 저널 화면을 전혀 밀어내지 않음. 엔트리를 보면서 시트를 반쯤 올려 "곁눈질" 가능(웹의 옆 패널과 가장 비슷한 느낌). 달력 워크스트림과 충돌 최소.
- 단점: 발견성이 낮음(숨겨진 기능). 제스처 라이브러리(`@gorhom/bottom-sheet` 등) 추가 의존성. 시트 안 스크롤과 화면 스크롤 충돌 처리 필요.

### 4.3 권장

**A(접이식 기간 요약 패널)를 기본으로, 탭 구성은 `오늘 · 스레드 · 검색 · 나`로 재편**한다.

> **결정 (2026-10-06)**: 이 권장안을 채택했다. 판단 근거는 엔트리와 그 기간의 태그를 한 화면에서 보는 것이다. 태그 탭은 제거했고(`TagExploreScreen.tsx`는 삭제, 결과 로직은 스택 화면 `TagResultScreen`으로 이전), 현재 탭은 `오늘 · 검색 · 나`다. 비운 자리의 스레드 탭은 예정이다.
>
> **변경 (2026-10-06)**: 스레드 목록은 탭 대신 **햄버거 메뉴**(웹 사용자 메뉴 전체를 담는 왼쪽 드로어)의 `저널 > 저널 스레드`로 연다. 하단 탭은 `오늘 · 검색 · 나` 세 개를 유지한다(`screen-spec.md` §4.10·§4.11).
>
> 구현은 아래 권장과 다음이 다르다.
>
> - 패널은 기간 태그만 담는 `JournalPeriodTagCloudPanel`이다. 기간 스레드는 패널 안이 아니라 **패널 아래 별도 접이식 섹션** `JournalPeriodThreadPanel`(`이 날/주/달의 스레드`, 웹 `JournalPeriodThreadSummary.vue` 대응)로 두었다. 결과가 0개여도 숨기지 않고 `스레드 없음`을 표시한다.
> - 패널은 **기본 펼침**이다. 펼침/접힘은 앱 실행 중 메모리에만 기억하고(일·주·월 공유), 앱을 다시 시작하면 펼침으로 돌아간다. 아래 (1)의 "기본 접힘"과 다르다.
> - 태그가 0개여도 패널을 숨기지 않고 헤더에 `태그 없음`을 표시한다(본문만 그리지 않음). 아래 (2)와 다르다.
> - 펼침 시 섹션(일자/일기/꿈 태그)별로 처음 18개까지 보여주고 `+N 더보기`로 그 행을 펼친다. 아래 (3)의 "2~3줄"을 개수 기준으로 정했다.
> - 태그를 누르면 액션 시트 없이 바로 태그별 기록 화면(전체 기간 + 연도 선택)을 연다.
> - 상세 동작은 `screen-spec.md` §4.4.2(태그별 기록)·§5.7(패널)·§5.8(칩)·§5.9(기간 스레드 섹션)을 따른다.

- 사용자 요구("한눈에 추세")에 가장 직접적이다. 접힘 상태에서도 상위 태그와 스레드 수가 항상 보인다.
- 태그 탭의 "기간 태그 보기" 기능은 패널이 흡수한다. "전체 기간 태그 탐색"(연도별 일자 검색, 엔트리 태그 검색)은 패널의 태그 탭 결과 화면(스택)과 검색 탭으로 옮긴다. 따라서 태그 탭 자리를 스레드 탭이 차지한다.
- 공간 부담은 (1) 기본 접힘 + 마지막 상태 기억, (2) 일간은 태그가 0이면 패널 숨김(웹 스레드 요약과 같은 규칙), (3) 펼침 시 섹션별 최대 2~3줄 + "더보기"로 완화한다.
- 달력 워크스트림과는 "날짜 헤더/달력 → 기간 요약 패널 → 엔트리 목록" 순서만 합의하면 된다. 패널은 `JournalDailyView`/`JournalWeeklyView`/`JournalMonthlyView`가 각자 렌더하는 공용 컴포넌트(`PeriodSummaryPanel`)로 두어 뷰 내부 수정을 최소화한다.

권장 탭 구조:

| 탭 | 내용 |
|---|---|
| 오늘 | 일/주/월 + 날짜 달력(타 워크스트림) + **기간 요약 패널**(태그 클라우드 + 기간 스레드) + 엔트리 목록(엔트리별 스레드 칩) |
| 스레드 | 스레드 목록(말머리 탭·라이프사이클 필터·제목 검색, 무한 스크롤) → 스레드 상세 |
| 검색 | 키워드 검색 + 태그 검색(기존 태그 탭의 전체 기간 탐색 흡수) |
| 나 | 프로필·이번 달 통계·로그아웃 |

---

## 5. 폰 레이아웃 스케치

**기간 요약 패널 (오늘 탭)**
```
┌ 이번 주 요약 ───────────── ▾ ┐   ← 접힘: 상위 태그 3~5개 · "스레드 3"
│ #야근 #운동 #엄마 … · 🧵 3    │
└──────────────────────────────┘
펼침:
  일자   야근(4) 운동(2) 비(1) …        ← 크기=tagClass, 색=textClass, 2~3줄 + 더보기
  일기   회의(5) 이직(3) …
  꿈     바다(2) 추락(1) …
  스레드 [업무] 이직 준비 3건  [꿈] 반복되는 바다 2건 …   ← 가로 스크롤 칩
```
- 태그 탭 → 하단 액션 시트: "이 기간에서 보기"(같은 기간 엔트리 목록 필터) / "전체 기간 검색"(검색 탭으로 `tagIds`·`type` 전달) / (일자 태그) "연도별 일자 보기".
- 스레드 칩 탭 → 스레드 상세(스택 화면).

**스레드 상세 (스택)**
- 헤더: 말머리 배지 · 라이프사이클 배지 · 제목 · 기간(`firstEntryDate ~ lastEntryDate`) · 소속 N건
- 본문(접이식, HTML→텍스트 또는 경량 렌더)
- 태그 집계 칩(`tag.list`)
- 소속 엔트리: 날짜 헤더(요일 포함)별 그룹 → 엔트리 카드(기존 `EntryCard` 재사용) → `EntryDetail`
- (후순위) 연관 스레드, 댓글

**스레드 목록 (탭)**
- 상단: 말머리 가로 탭(전체/각 말머리/없음) + 라이프사이클 칩(전체/진행/보류/완료) + 검색창
- 카드: 말머리 · 제목 · N건 · 기간 · 라이프사이클 · 댓글 수, 무한 스크롤(`page` 증가)

---

## 6. 단계별 계획

모바일 1단계의 목표는 **읽기 전용**이다(`screen-spec.md` §1). 성공 기준은 읽기가 완벽하게 동작하고 웹과 같은 복사·내보내기를 제공하는 것이며, 쓰기 기능은 1단계 범위 밖이다. 그래서 단계를 **1단계(읽기 전용) → 2단계(쓰기)** 로 나누고, 모든 쓰기 작업을 1단계 뒤에 둔다. 1단계 안의 세부 단계 번호는 기존 0~4단계를 그대로 `1-0`~`1-4`로 옮기고, 새로 정한 읽기 항목을 `1-5`·`1-6`으로 붙였다.

### 1단계 — 읽기 전용

| 단계 | 내용 | API | 선행/주의 |
|---|---|---|---|
| **1-0. 계약 정리** (작음) | `TagCloudItem`에 `tagClass`/`textClass` 보존, `JournalEntry`에 `threadList` 추가, 일자 조회에 `showDiaries/showDreams=true`, `AjaxResponse`에 `rsltList`/`rsltMap` 추가, 스레드 타입(`JournalThreadPeriodSummary`, `JournalThread`, `JournalThreadEntry`) 정의 | 기존 | 감사 문서 Med 항목. UI 변경 없음 |
| **1-1. 기간 스레드 섹션 + 엔트리 스레드 칩** (가장 작은 가치 단위) | 일/주/월 뷰의 기간 태그 패널 아래에 기간 스레드 섹션(칩, 결과 0이면 숨김). 엔트리 카드에 소속 스레드 칩(`entry.threadList`). 칩 탭 시 아직 상세가 없으므로 간단한 "스레드 엔트리" 화면 = `GET /threads/{id}` + `/threads/{id}/entries`를 날짜별 그룹으로 표시 | `period-summary`, `threads/{id}`, `threads/{id}/entries` | 신규 API 3개뿐, 읽기 전용. 모바일에서도 "흐름"이 즉시 보임. 달력 워크스트림과 상단 배치만 합의 |
| **1-2. 기간 태그 클라우드 패널** | `PeriodSummaryPanel`(접이식)에 일자/일기/꿈 클라우드 추가, 기간 파라미터(`stdrdDt`/`weekStartDt`/`yy+mnth`) 연동, 크기·색은 `tagClass`(`ts-1..9` → 폰트 크기 매핑)·`textClass`(→ 색 매핑 테이블). (당초 1-1 스레드 칩을 패널 안으로 옮길 계획이었으나 패널 아래 별도 섹션으로 변경) | `day/tags`, `entry/tags` | 매핑 테이블은 웹 CSS의 `ts-*`, `text-*` 값에서 추출 |
| **1-3. 태그 액션 + 탭 재편** | 태그 탭 → 액션 시트(이 기간/전체 기간/연도별 일자). 기존 `TagExploreScreen` 결과 로직을 스택 화면으로 이동, 하단 탭을 `오늘·스레드·검색·나`로 변경, 검색 탭에 태그 필터 추가 | `days?viewType=SEARCH`, `day/tag/{id}/years`, `entries?type&tagIds` | 검색 "전체" 400 버그(감사 High) 먼저 수정 |
| **1-4. 스레드 탭(목록) + 상세 강화** | 목록(말머리 탭·라이프사이클·제목 검색·무한 스크롤), 상세에 태그 집계·본문·라이프사이클 배지 | `threads?page&size&prefixId&lifecycleKey&searchType=title&searchKeyword&tagIds`, `my/prefixes/options` | 페이지 응답은 `rsltObj.content` |
| **1-5. 엔트리 상세 태그** | 엔트리 상세 본문 아래에 엔트리 태그(`태그`/`꿈 태그`), 소속 스레드(`스레드`), 일자 태그(`일자 태그`, 일·주·월 목록·일자 태그 결과에서 진입할 때만 전달) 칩 표시. 태그 칩은 태그별 기록 화면을, 스레드 칩은 스레드 글 목록을 연다 | 내비게이션 파라미터 재사용. 스레드 글 목록에서 진입하면 `entries/{entryId}/threads` | 스레드 소속 엔트리 응답에 `threadList`가 없어 재조회 |
| **1-6. 복사·내보내기** | 웹과 같은 기록 복사·텍스트 내보내기(엔트리·챕터·꿈 섹션·일자·스레드). 모바일은 `expo-clipboard` 복사 + RN `Share` 공유 시트, 텍스트는 클라이언트에서 웹 규칙으로 조립(`screen-spec.md` §4.8) | 없음(클라이언트 조립). 서버 TXT API `chapter/{id}/export`, `entries/export`, `threads/{id}/export?includeReflection`는 미사용 | 서버 데이터를 바꾸지 않으므로 읽기 범위 |

**가장 작은 가치 있는 첫 단계 = 1-0 + 1-1**(당초 계획): 타입 몇 개와 읽기 API 3개로, 일/주/월 화면에서 "이 기간에 이어지고 있는 이야기(스레드)"와 엔트리별 소속 스레드를 바로 볼 수 있다. 실제 구현은 기간 태그 클라우드 패널(1-2)을 먼저 끝냈다(§6.1).

### 2단계 — 쓰기 (1단계 이후)

1단계(읽기 전용)를 마친 뒤에 시작한다. 모바일에 아직 쓰기 화면이 없다.

| 단계 | 내용 | API | 선행/주의 |
|---|---|---|---|
| **2-1. 스레드 쓰기** | 엔트리 → "스레드에 추가"(후보 목록·소속 토글·새 스레드 생성), 라이프사이클 변경, 연관 스레드 | `threads/candidates`, `POST/DELETE threads/{id}/entries`, `POST threads` | 축 잠금(resolved) 규칙 확인 필요 |
| **2-2. 댓글** | 댓글 작성·수정·삭제 | (웹 댓글 API 조사 필요) | |

엔트리·일자 등록·수정 화면은 이 문서 범위 밖이지만, 마찬가지로 1단계 이후에 둔다(`screen-spec.md` §1.2).

### 6.1 진행 현황 (2026-10-06)

상태 표기: **완료**(실기기 확인까지), `구현·미검증`(코드 반영, 실기기 미확인), 일부 완료, 예정. 실기기 확인 항목은 `screen-spec.md` §11 체크리스트를 따른다. 지금까지 실기기에서 확인한 항목은 없으므로, 1-2의 "완료"도 크기·색 클래스는 §11에서 확인이 남아 있다.

| 단계 | 상태 | 내용 |
|---|---|---|
| 1-0 계약 정리 | 일부 완료 | 완료: `TagCloudItem`의 `tagClass`/`textClass` 보존, `JournalEntry.threadList`, 일자 조회(DAILY/WEEKLY/LIST)의 `showDiaries=true&showDreams=true`, `JournalThreadPeriodSummary`·`JournalThreadPrefix`·`JournalThread`(백엔드 `JournalThreadDto` 읽기 필드) 타입. 남음: 공용 `AjaxResponse`의 `rsltList`/`rsltMap`(현재 `rslt`·`rsltObj`만, 기간 스레드·스레드 소속 엔트리·검색 API는 함수별 응답 타입으로 우회). |
| 1-1 기간 스레드 섹션 + 엔트리 스레드 칩 | `구현·미검증` | 구현: 엔트리 카드의 소속 스레드 칩(`🧵 제목`), 기간 스레드 섹션 `JournalPeriodThreadPanel`(`period-summary` DAILY·WEEKLY·LIST, `🧵` + 말머리 배지 + 제목 + `N건`, 월간 10개 후 `펼치기`, 접이식). 기간 스레드 칩·엔트리/상세 스레드 칩을 누르면 "스레드 엔트리" 화면 = 스레드 글 목록 `ThreadResultScreen`(`openThreadResult`, 파라미터 `{ threadId, title }`)이 열린다. 사용 API: `GET /api/journal/threads/{id}`(스레드 정보) + `GET /api/journal/threads/{id}/entries`(소속 엔트리 full DTO, 웹 스레드 상세와 같은 요청 — 스레드의 글 전체를 한 번에 주는 유일한 API). 날짜별 묶음 기본 최신순 + `오래된순` 토글, 엔트리 → 상세(`loadThreads`로 `entries/{id}/threads` 재조회), 복사·공유 바. `screen-spec.md` §4.9. |
| 1-2 기간 태그 클라우드 패널 | **완료** (크기·색 실기기 확인 남음) | `JournalPeriodTagCloudPanel`·`journalTagStyle`. 일·주·월 공용, 기본 펼침, 섹션별 18개 + `+N 더보기`. 엔트리·일자 태그 칩(`JournalTagChips`). 태그를 누르면 태그별 기록 화면(1-3). |
| 1-3 태그 액션 + 탭 재편 | 일부 완료 (`구현·미검증`) | 완료: 태그 탭 제거(현재 `오늘 · 검색 · 나`), 검색 `전체` 400 수정(`일기 \| 꿈`, 기본 일기). 구현: 태그별 기록 스택 화면 `TagResultScreen`(파라미터 tagId·name·ctgr·kind `day`/`diary`/`dream`, 연도 선택, 날짜별 결과, 태그 설명 읽기, 날짜 헤더 → 오늘 탭 그 날짜) — 패널·목록 칩·상세 칩에서 열림, `TagExploreScreen` 삭제. 태그 검색 복구로 본다. 검색 탭 태그 매칭(구현·미검증): 키워드 검색 시 이름이 맞는 태그 칩을 결과 위에 표시하고 누르면 태그별 기록(서버 이름 검색 API가 없어 기간 없는 `day/tags`·`entry/tags?type=` 카탈로그를 화면에서 부분 일치, 일기 필터 = 일기+일자 태그, 꿈 필터 = 꿈 태그만(사용자 결정), 검색은 제출 시에만, 에뮬레이터 확인 2026-10-06, `screen-spec.md` §4.4.1). 남음: 액션 시트(이 기간에서 보기 등, 현재는 바로 태그별 기록), 검색 탭 안 태그로 엔트리 거르기 필터, 스레드 탭 추가(1-4). |
| 1-4 스레드 탭 + 상세 | 일부 (`구현·미검증`) | 상세에 해당하는 스레드 글 목록 스택 화면은 구현(1-1 참고): 말머리·라이프사이클 배지·소속 기간·소속 엔트리 수·본문(접기)·소속 엔트리 태그 집계(표시 전용). 스레드 목록은 탭 대신 햄버거 메뉴 `저널 스레드` → `ThreadListScreen`으로 구현(`GET /api/journal/threads?page&size=20`, 제목 검색 `searchType=title`(제출 시에만), 무한 스크롤, 말머리·라이프사이클 배지·소속 기간·건수·태그 6개, 누르면 스레드 글 목록. 에뮬레이터에서 첫 페이지 확인 2026-10-06, `screen-spec.md` §4.11). 남음: 말머리 탭(`my/prefixes/options`)·라이프사이클 필터·태그 필터, 연관 스레드 표시. |
| 1-5 엔트리 상세 태그 | `구현·미검증` | `EntryDetailScreen` 본문 아래 `태그`/`꿈 태그`·`스레드`·`일자 태그` 행(내비게이션 파라미터). 태그 칩 → 태그별 기록, 스레드 칩 → 스레드 글 목록. 스레드 글 목록에서 들어오면 `GET /api/journal/entries/{entryId}/threads`로 소속 스레드 재조회. |
| 1-6 복사·내보내기 | 일부 완료 (`구현·미검증`) | 구현: `expo-clipboard` 복사 + RN `Share` 공유. 엔트리 상세 `복사`·`공유`, 일·주·월 보기 `JournalCopyShareBar`(이 날/주/달 기록), 검색 결과·태그별 기록·스레드 글 목록의 결과 목록 바(머리줄 + 화면 순서 엔트리, `buildResultListCopyText`). 텍스트는 `src/utils/journalCopy.ts`가 웹 규칙(CRLF, `날짜 (요일)`, `#순번`, 해석 포함)으로 조립하며 전체(full) 모드만. 남음(웹 대비): 챕터 단위 복사, 해석 포함 범위 선택, TXT 파일 내보내기(스레드 서버 내보내기 `threads/{id}/export` 포함). |
| 2-1 · 2-2 쓰기 | 1단계 이후 | |

---

## 7. 열린 질문

- ~~일간 화면에서 기간 요약 패널을 기본 펼침/접힘 중 무엇으로 할지~~ → 결정: 일·주·월 모두 기본 펼침, 앱 실행 중 메모리에 상태 기억(§4.3 결정).
- ~~태그 탭 제거 후 진입 경로(딥링크 `{ date }` 진입 등) 유지 방법~~ → 태그별 기록 화면의 날짜 헤더가 `navigateToDailyHub`(`popTo("Main")` + `{ date }`)로 오늘 탭에 날짜를 넘긴다. 외부 딥링크는 아직 없다.
- `ts-*`/`text-*` 클래스 → RN 스타일 매핑을 앱 상수로 둘지, 백엔드가 `textClassCd` 같은 의미 코드만 쓰도록 할지.
- 스레드 본문(HTML/markdown) 렌더링 수준(텍스트 변환 vs `react-native-render-html` 도입).