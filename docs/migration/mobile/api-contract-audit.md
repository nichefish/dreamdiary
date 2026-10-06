# 모바일 API 계약 감사 (mobile-react-native ↔ backend ↔ frontend-vue)

- 작성일: 2026-10-06 (브랜치 `dev_0.30.0`, 미커밋 작업 트리 기준)
- 대상: `app/mobile-react-native/src/api/*.ts` 전 함수, `src/types/*.ts` 전 타입
- 기준: `app/backend` 컨트롤러·파라미터·DTO(`global/ApiUrl.java`, `*RestController`, `*SearchParam`, `*Dto`), `app/frontend-vue` 동일 엔드포인트 호출부
- 공통 응답 래퍼: 백엔드 `AjaxResponse extends ServiceResponse` → `rslt`, `rsltSts`, `message`, `rsltList`, `rsltPage`, `rsltMap`, `rsltVal`, `rsltObj`, `rsltStr` (`@JsonInclude(NON_NULL)`), 오류 시 `BaseExceptionHandler` (`IllegalArgumentException`/`BusinessException` → 400)

## 고영향(High) 요약

1. **[미해결] 검색 탭 "전체" 필터가 항상 400** — `SearchScreen`이 `filterType === "ALL"`이면 `type`을 빼고 `GET /api/journal/entries`를 호출한다. 백엔드 `JournalEntryRestController.journalEntryListAjax` → `JournalEntryTypeResolver.resolveByRawTypeOrFallback(type, contentType)` → 둘 다 비어 `IllegalArgumentException("type is required.")` → 400. 웹은 항상 `type=DIARY|DREAM`을 보낸다(`entrySearchQuery.ts`). 기본 필터가 "전체"이므로 검색 탭 첫 검색이 실패한다.
2. **[해결됨] 일간/주간/월간 챕터 엔트리 미표시** — 모바일이 `journalDiaryList/journalDreamList/journalNoteList`를 읽었으나 백엔드 `JournalChapterDto`는 `journalEntryList` 하나(엔트리별 `contentType`)만 보낸다. 오늘 화면이 빈 화면이 되던 원인. `chapterEntries()`·`dayHasEntries()`로 수정.
3. **[해결됨] 월간 `viewType=MONTHLY`** — `JournalDayViewType`에 없음 → `LIST&yy&mnth`로 수정(별도 작업).
4. **[해결됨] 프로필 통계 0 / 챕터 라벨 "챕터 0"** — 같은 필드 불일치. 통계는 `dayHasEntries` + `contentType` 집계, 라벨은 웹 규칙(`일기: 요약`, `일기: {말머리} · {제목}`)으로 수정.

## 영향도별 건수

| 영향 | 건수 | 비고 |
|---|---|---|
| High | 3 | 표 기준. 미해결 1 (검색 "전체"), 해결 2 (챕터 엔트리 필드, 챕터 말머리/라벨 필드). 월간 MONTHLY는 별도 작업에서 해결 |
| Med | 6 | 모두 제안(미수정) |
| Low | 10 | 정보성·잠재 이슈 포함 |
| OK | 14 | 일치 확인 항목 |

## API 함수 감사 (`src/api/*.ts`)

| 영역 | 모바일 호출/타입 | 백엔드·웹 실제 | 영향 | 조치 |
|---|---|---|---|---|
| 검색 | `searchEntries({keyword, type?})` — SearchScreen "전체"는 `type` 생략 | `GET /api/journal/entries` `@RequestParam("type", required=false)` 이지만 `JournalEntryTypeResolver.normalizeRawType`이 blank면 `IllegalArgumentException("type is required.")` → 400. 웹은 항상 `type` 전송 | **High** | 제안: "전체"는 `DIARY`·`DREAM` 2회 병렬 호출 후 `stdrdDt` 내림차순 병합(웹 정렬 기본 DESC), 또는 "전체" 필터 제거. `EntrySearchParams.type`을 필수로 바꿔 컴파일 단계에서 막기 |
| 일자 | `getDailyJournalDay` — `viewType=DAILY&stdrdDt` | `JournalDayRestController` → `getMyStdrdDaysDtoEnriched`; `stdrdDt`는 `journal_date` 동등 비교, ownerId=`AuthUtils.requireLoginUserId()`. 웹은 `yy/mnth/showDiaries/showDreams/showTagCloud/sort`도 전송 | OK | 응답 `rsltList[0]` 사용 정상 |
| 일자 | `getDailyJournalDay` / `getWeeklyJournalDays` / `getMonthlyJournalDays` 가 `showDiaries`·`showDreams` 미전송 | `JournalDaySearchParam.showDiaries/showDreams` 기본 `false` → `JournalDayViewHelper.applyStates`(엔트리 state·lifecycle 병합)와 `applyChapterTagSummary`(챕터 태그 요약)가 **건너뜀**. 웹 기본값 `true` | Med | 제안: 세 호출에 `showDiaries: true, showDreams: true` 추가(웹과 동일). 현재 모바일 UI가 state/lifecycle/챕터 태그를 쓰지 않아 증상은 없음. 라이프사이클 배지·태그 칩 포팅 전 필수 |
| 일자 | `getMonthlyJournalDays` — `viewType=LIST&yy&mnth` | `getMyYyMnthListDtoEnriched` (yy/mnth 캐시), 웹 월간과 동일 | OK | (MONTHLY→LIST 수정 완료) |
| 일자 | `getWeeklyJournalDays` — `viewType=WEEKLY&weekStartDt` (월요일) | `getMyWeeklyListDtoEnriched`; DB `week_start_date`도 월요일. 웹은 `weekStartDt`+`stdrdDt` | OK | — |
| 태그 | `searchJournalDaysByTag(tagId, yy)` — `viewType=SEARCH&tagId&yy` | 컨트롤러 SEARCH는 `getMyListDtoByMetaIdEnriched`로 가지만 `JournalDaySpec`이 `tagId`를 처리. 웹 `journalModalDay.ts`도 동일하게 `SEARCH+tagId+yy` | OK | 전용 `GET /api/journal/day/tag/{tagId}`도 존재(참고) |
| 태그 | `getJournalDayTagYears(tagId)` — `GET /api/journal/day/tag/{tagId}/years` | `JOURNAL_DAY_TAG_YYS` → `rsltList: Integer[]`. 웹 동일 | OK | — |
| 태그 | `getJournalDayTags(yy, mnth)` — `GET /api/journal/day/tags` | `TagSearchParam{yy,mnth,stdrdDt,weekStartDt,...}` → `rsltList: TagDto[]`. 웹은 기간에 따라 `stdrdDt`/`weekStartDt`/`yy+mnth` 사용 | Low | 포팅 시 일·주 기간 파라미터 지원 추가(제안) |
| 태그 | `getJournalEntryTags(yy, mnth, type)` — `GET /api/journal/entry/tags` | `@RequestParam("type")` 필수, 모바일은 항상 전달. 웹 태그 클라우드는 기간 파라미터+type, 검색 카탈로그는 type만 | OK | — |
| 태그 | `normalizeTagList` → `TagCloudItem{id,name,ctgr,contentSize}` | `TagDto`는 `tagClass`(빈도 크기 `ts-1`~`ts-9`), `textClass`/`textClassCd`(색), `tagCategoryId`도 보냄. 웹 `normalizeTagCloudList`는 `tagClass`·`textClass` 보존 | Med | 제안: `tagClass`·`textClass` 보존(모바일 클라우드 크기·색 표현에 필요). 포팅 1단계에 포함 |
| 검색 | 배열 직렬화 `searchKeywords[0]=…`, `tagIds[0]=…` | Spring `@ModelAttribute` 인덱스 바인딩 지원. 웹은 반복 키 `tagIds=1&tagIds=2` | Low | 동작함. 통일하려면 반복 키 방식 권장(제안) |
| 검색 | 응답 `rsltList: JournalEntry[]` | `withList(List<JournalEntryDto>)`, `stdrdDt` 포함 | OK | — |
| 인증 | `getAuthInfo` — `GET /api/auth/get-auth-info` | `rsltObj: AuthUserDto`, 비로그인 401 | OK | — |
| 인증 | `login` — `POST /api/auth/login` JSON `{username,password}` | `LoginRequest{username,password}` 일치. 실패는 모두 **401** + `message`(+ 일부 `rsltMap`) | OK | — |
| 인증 | 로그인 실패 메시지 | `BadCredentialsException` 메시지에 `"<br>"` + 남은 시도 횟수 HTML 포함 → 모바일 `Text`에 `<br>` 그대로 노출 | Med | 제안: 로그인 오류 표시 시 `<br>`→개행 치환(`stripHtml` 유틸 재사용). 인증 워크스트림 영역이라 보류 |
| 인증 | 로그인 401 `rsltMap.isDupIdLogin` / `needsPasswordReset` / `isCredentialExpired` | 웹 `SignIn.vue`는 중복 로그인 확인(`POST /api/auth/expire-session`) · 비밀번호 변경(`POST /api/auth/login-pw-chg`) 흐름 보유. 모바일은 메시지만 표시, `AjaxResponse` 타입에 `rsltMap` 없음 | Med | 제안: 인증 워크스트림(X-Client-Channel·DupIdLogin 작업)과 함께 처리. 동일 채널 중복 로그인 시 모바일은 진행 불가 |
| 인증 | 로그인 실패 401 → `client.ts` 전역 401 핸들러(토큰 삭제·`setUser(null)`) 호출 | 로그인 화면에서는 무해 | Low | 필요 시 `login`에 `expectedErrorStatuses: [401]` + 전역 핸들러 제외 옵션(제안) |
| 인증 | `refreshAccessToken` — `POST /api/auth/refresh` `{}` | 쿠키 refresh 토큰 기반, 응답 `Authorization` 헤더. 바디 무시 | OK | — |
| 인증 | `logout` — `POST /api/auth/logout-json` | 일치, 웹 동일 | OK | — |
| 경로 | 모바일 전 경로 | `ApiUrl.java`에 전부 존재. 제거·이전된 엔드포인트 없음 | OK | — |
| 웹 차이 | 모바일 없음: 메타(`/api/journal/day/metas/{id}`, `/years`), 연간(`/api/journal/annual*`), 스레드(`/api/journal/threads*`), 태그 프로필(`/api/tags/{tagId}/profile`) | 웹 전용 기능 | Low | 포팅 계획 문서 참조(`tagcloud-thread-port-plan.md`). 이후 스레드 읽기 API(목록 포함), 연간 결산 읽기(목록·집계·상세), 태그 프로필 읽기, 사용자 메뉴를 모바일에 반영(아래 "후속 반영"). 일기/꿈 결산·결산 쓰기는 여전히 없음 |

## 타입 감사 (`src/types/*.ts` + API 파일 내 인터페이스)

| 영역 | 모바일 호출/타입 | 백엔드·웹 실제 | 영향 | 조치 |
|---|---|---|---|---|
| 일자 | `JournalChapter.journalEntryList` (구 `journalDiaryList/DreamList/NoteList`) | `JournalChapterDto.journalEntryList: JournalEntryDto[]`, 타입은 엔트리 `contentType`(`JOURNAL_DIARY`/`JOURNAL_DREAM`; NOTE 챕터 엔트리도 `JOURNAL_DIARY`) | **High** | 해결됨(구 필드 삭제) |
| 일자 | `JournalChapter.categoryCode/categoryName` | 백엔드에 없음. 실제는 `prefix: PrefixDto{id,name,textClassCd,textClass,sortOrder,activeYn}`, `prefixId`, `chapterType`, `summaryYn` | **High** | 해결됨(`prefix`·`chapterType`·`summaryYn` 추가, `chapterHeaderLabel`) |
| 일자 | `JournalChapter` 미포함 필드 | `tag`(챕터 태그 요약, showDiaries 필요), `state`, `journalDayId`, `stdrdDt`, `markdownContent` | Low | 필요 시 추가 |
| 일자 | `JournalDay{id, stdrdDt, journalChapterList, journalDreamSectionList}` | `JournalDayDto` 추가 필드: `journalDate`, `journalDateWeekDay`, `isHolyday`, `holydayNm`, `vacationDayStatus`, `vacationReasonList`, `weather`, `diaryResolvedYn`, `dreamResolvedYn`, `chapterList`, `hiddenChapterPrefixList`, `yy`, `mnth`, `weekStartDt` | Low | 제안: 공휴일·휴가 표시가 필요하면 `isHolyday/holydayNm/vacationDayStatus` 추가 |
| 일자 | `JournalDreamSection{sectionKey,title,dreamerName,entries}` | `JournalDreamSectionDto` 동일 | OK | — |
| 엔트리 | `JournalEntry{id, contentType, title?, content?, stdrdDt?}` | `JournalEntryDto` 필드명 일치. 추가: `threadList: JournalThreadEntryDto[]`(일자 응답에 이미 채워짐, `JournalDayQueryService:293`), `tag`, `state`, `lifecycle`, `reflectionList`, `dreamerName`, `sortOrder`, `markdownContent`, `journalChapterId`, `relatedContentList`, `diaryResolvedYn`/`dreamResolvedYn` | Med | 제안: 스레드 포팅 시 `threadList`·`tag` 추가(추가 API 없이 엔트리별 스레드 칩 가능) |
| 엔트리 | `JournalEntry.title?: string` | 백엔드 null 가능 | Low | `string \| null` 로 표기(제안) |
| 태그 | `TagCloudItem{id: number\|string, name, ctgr?, contentSize}` | `TagDto.id: Integer`, `ctgr` 기본 `""`, `tagClass`, `textClass` 누락 | Med | 위 API 표 항목과 동일(제안) |
| 인증 | `AuthUser{username,nickname,email,profileImageUrl?,roles:{roleKey}[],isMngr,isDev}` | `AuthUserDto` + `permissions: string[]`, `activeProfile`; `isMngr/isDev`는 `Boolean`(null 가능); `RoleDto`는 `roleKey` 외 다수 | Low | 필요 시 optional 필드 추가(제안) |
| 공통 | `AjaxResponse<T>{rslt, message?, rsltObj?}` | `ServiceResponse` + `rsltSts`, `rsltList`, `rsltPage`, `rsltMap`, `rsltVal`, `rsltStr` | Low | 제안: `rsltList?`·`rsltMap?` 추가(로그인 `rsltMap` 처리, 스레드 목록 `rsltObj` Page 처리에 필요) |
| 공통 | `DailyListResponse{rslt, rsltList?}`, `TagListResponse`, `EntrySearchResponse` | `message` 미포함 | Low | 소프트 실패(`rslt=false`) 메시지 표시가 필요하면 `message?` 추가 |
| env | `env.d.ts` `EXPO_PUBLIC_API_BASE_URL` | 백엔드 무관 | OK | — |

## 후속 반영 (감사 이후, 2026-10-06)

감사 뒤 모바일에 추가한 호출. 모두 읽기 전용이며 백엔드 변경은 없다. 필드명은 백엔드 DTO에서 확인했다.

| 영역 | 모바일 호출/타입 | 백엔드 실제 | 상태 |
|---|---|---|---|
| 스레드 | `getJournalThread(threadId)` — `GET /api/journal/threads/{id}` → `JournalThread` | `JournalThreadRestController.journalThreadDetailAjax` → `JournalThreadService.viewDetailPage`(상세 DTO + 소속 엔트리 태그 집계·`membershipCount`·`firstEntryDate`/`lastEntryDate` + 라이프사이클) → `rsltObj: JournalThreadDto`. 웹 `journalThreadDetail` 스토어와 같은 요청 | 구현·미검증 (스레드 글 목록) |
| 스레드 | `getJournalThreadEntries(threadId)` — `GET /api/journal/threads/{id}/entries` → `JournalEntry[]` | `journalThreadEntryListAjax` → `JournalThreadEntryService.getEntriesByThread` → `rsltList: JournalEntryDto[]`(소유권 검증, 일자 → 챕터 → 엔트리 sortOrder → ID 정렬, `reflectionList`·lifecycle 채움). `threadList`는 기본값(빈 목록). `relatedThreadIds`는 보내지 않음 | 구현·미검증 |
| 스레드 | `getJournalEntryThreads(entryId)` — `GET /api/journal/entries/{entryId}/threads` → `JournalThreadEntry[]` | `journalEntryThreadListAjax` → `getListByEntry` → `rsltList: JournalThreadEntryDto[]`(`id`, `threadId`, `entryId`, `sortOrder`, `threadTitle`, `entryContentType`) | 구현·미검증 (스레드 글 목록 → 상세) |
| 태그 | `getJournalDayTagCatalog()` — `GET /api/journal/day/tags` (파라미터 없음) | 기간 조건 없이 `getMySizedTagList` → 사용자 일자 태그 전체. 웹 `JournalDayMetaModal` 태그 카탈로그와 같은 요청. 서버에 태그 이름 검색 파라미터 없음(`TagSearchParam`) | 구현, 에뮬레이터 확인 2026-10-06 (검색 태그 매칭 — 일기 필터에서만 사용, 화면 부분 일치) |
| 태그 | `getJournalEntryTagCatalog(type)` — `GET /api/journal/entry/tags?type=DIARY\|DREAM` (기간 없음) | 기간 조건이 없으면 `getMyTagList(contentType)`. 웹 `useSearchTagCatalog`와 같은 요청 | 구현, 에뮬레이터 확인 2026-10-06 (DIARY) · DREAM 미확인 |
| 타입 | `JournalThread{id, title, content, markdownContent, prefix, prefixId, membershipCount, firstEntryDate, lastEntryDate, lifecycle{lifecycleKey, lifecycleDesc}, tag}` | `JournalThreadDto`·`LifecycleCmpstn`과 필드명 일치 | OK |
| 스레드 | `getJournalThreadPage({page, size, keyword?})` — `GET /api/journal/threads?page=&size=(&searchType=title&searchKeyword=)` → `JournalThreadPage{content, totalElements, totalPages, page}` | `JournalThreadRestController` `JOURNAL_THREAD_API_LIST` → `journalThreadService.getPageDto(searchParam, pageRequest)` → `rsltObj: Page<JournalThreadDto>`. 웹 `journalThreadList.fetchList`와 같은 요청. 모바일은 Spring Page 기본 직렬화(`content`·`totalElements`·`totalPages`·`number`)와 VIA_DTO 모양(`page{}`)을 모두 받는다. 정렬은 서버 기본 | 구현, 에뮬레이터 확인 2026-10-06 (첫 페이지 60개 · 제목 검색·다음 페이지 미확인) — 햄버거 메뉴 `저널 스레드` |
| 결산 | `getJournalAnnualList()` — `GET /api/journal/annuals` → `JournalAnnual[]` | `JournalAnnualRestController` `JOURNAL_ANNUALS` → `myJournalAnnualService.getMyListDto` → `rsltList: JournalAnnualDto[]`. 웹 `journalAnnual` 스토어 목록과 같은 요청 | 구현, 에뮬레이터 확인 2026-10-06 — 햄버거 메뉴 `저널 결산` |
| 결산 | `getJournalAnnualTotal()` — `GET /api/journal/annual/total` → `JournalAnnual \| null` | `JOURNAL_ANNUAL_TOTAL` → `getMyTotalAnnual` → `rsltObj: JournalAnnualDto`(`dreamDayCnt`·`dreamCnt`) | 구현, 에뮬레이터 확인 2026-10-06 |
| 결산 | `getJournalAnnual(yy)` — `GET /api/journal/annual/{yy}` → `JournalAnnual \| null` | `JOURNAL_ANNUAL` → `getMyDetailDtoByYy(yy)` → `rsltObj: JournalAnnualDto`(`journalAnnualReviewList` 포함) | 구현, 에뮬레이터 확인 2026-10-06 (2026년) |
| 타입 | `JournalAnnual{id, yy, title, content, markdownContent, dreamDayCnt, dreamCnt, dreamComptYn, tag, journalAnnualReviewList}`, `JournalAnnualReview{id, title, content, markdownContent, categoryCode, yy}` (`src/types/journalAnnual.ts`) | `JournalAnnualDto`·리뷰 DTO에서 쓰는 필드만 옮김(모두 optional). 결산 쓰기·일기/꿈 결산(`/annual/diaries`·`/dreams`·`/tags`)은 쓰지 않음 | OK (읽기 필드만) |
| 메뉴 | `getUserMenus()` — `GET /api/menus?mode=USER` → `MenuDto[]` (`src/types/menu.ts`: `id, parentMenuId, menuType, menuName, menuLabel, url, submenuExpandType, useYn, sidebarVisibleYn, subMenuList`) | `MenuRestController` `MENUS`: `mode` 기본 `USER` → `menuService.getUserMenuList()` → `rsltList: MenuDto[]`(트리). `MNGR`은 `menu.view.admin` 권한이 있을 때만 관리자 메뉴 — 모바일은 요청하지 않음. 웹 사이드바(`shared/menu/stores/menu.ts`)와 같은 요청. `rslt=false`/빈 목록이면 모바일은 내장 기본 메뉴로 대체 | 구현, 에뮬레이터 확인 2026-10-06 (서버 메뉴 · 게시판 주입) — 햄버거 메뉴 |
| Keep 목록 (2-0) | `getKeepList()` — `GET /api/prospective/keeps?scope=all` → `KeepItem[]` (`src/types/prospectiveKeep.ts`) | `ProspectiveKeepRestController`: `getListDtoWithLifecycleByUser` → `rsltList: ProspectiveKeepDto[]`, 페이지 없음, `sortOrder`→`id` 정렬, lifecycle `{lifecycleKey, lifecycleDesc}` 부착(`applyKeepLifecycles`). `projectId`와 `projectUnassigned`를 같이 주면 `IllegalArgumentException`(400) — 모바일은 둘 다 안 보내고 클라이언트에서 거른다. 웹 `KeepPage.vue`와 같은 요청 | 구현·미검증 (에뮬레이터 미확인 — 백엔드 미기동) — Keep 목록 |
| Keep 상세 (2-0) | `getKeepDetail(id)` — `GET /api/prospective/keep/{id}` → `KeepItem` (rsltObj `id`가 숫자가 아니면 오류) | `getDetailDtoWithCacheByUser`: 소유자가 아니면 `NotAuthorizedException`, ID가 null이면 `rslt=false`. **lifecycle 미부착**(`mapstruct.toDto`만) — 모바일은 목록 항목의 lifecycle을 합친다(`screen-spec.md` §4.15) | 구현·미검증 (에뮬레이터 미확인 — 백엔드 미기동) — Keep 상세 |
| Keep Project (2-0) | `getKeepProjects()` — `GET /api/prospective/projects` → `KeepProject[]` (`id, title, description?, sortOrder?, lifecycle?`) | `ProspectiveProjectRestController` → `ProspectiveProjectService.getMyList` → `rsltList: ProspectiveProjectDto[]`(모든 lifecycle). 모바일은 OPEN만 칩으로, 이름 조회는 전체로 | 구현·미검증 (에뮬레이터 미확인 — 백엔드 미기동) — Keep 목록 Project 칩·상세 Project 이름 |
| Keep 태그 (2-0) | `getKeepTags()` — `GET /api/tags?contentType=PROSPECTIVE_KEEP` → `KeepTagOption[]` (`id, name, ctgr?, contentSize`; 정규화) | `TagRestController` → `TagService.getOverallSizedTagList` → `rsltList: TagDto[]` (`id, name, ctgr, contentSize, tagClass, textClass`). 메서드 주석상 "컨텐츠 타입과 무관하게" 조회라 `contentSize`가 Keep 전용 건수라는 보장이 없어, 모바일은 순서만 쓰고 건수는 목록에서 센다. 웹 Keep 태그 클라우드와 같은 요청 | 구현·미검증 (에뮬레이터 미확인 — 백엔드 미기동) — Keep 필터 시트 |
| Keep 말머리 (2-0) | `getKeepPrefixOptions()` — `GET /api/my/prefixes/options?contentType=PROSPECTIVE_KEEP` → `KeepPrefixOption[]` (`id, name, sortOrder?`) | `PrefixRestController` `/options` → `getActiveMine` → `rsltList: PrefixDto[]` (`id, name, textClassCd, textClass, sortOrder, activeYn`), 사용 중인 내 말머리만. 웹 등록 모달 말머리 선택지와 같은 요청 | 구현·미검증 (에뮬레이터 미확인 — 백엔드 미기동) — Keep 필터 시트 |
| Keep 타입·읽기 오류 (2-0) | `src/types/prospectiveKeep.ts`: `KeepItem`(`id, contentType?, title?, content?, markdownContent?, location?, dateText?, expireDt?, prefix?: KeepPrefix, prefixId?, sortOrder?, projectId?, lifecycle?, state?{list: KeepStateItem[]}, tag?, createdAt?, createdByNm?, updatedAt?, updatedByNm?`), `KeepProject`, `KeepTagOption`, `KeepPrefixOption`. Keep 읽기 5개는 HTTP 200 + `rslt=false`를 `ApiError(message, 200)`로 바꾼다(`assertReadOk`) | `ProspectiveKeepDto`(+`BaseAuditRegDto`·`BaseAuditDto`: `createdAt`·`updatedAt`은 `yyyy-MM-dd HH:mm:ss`), `expireDt`는 `yyyy-MM-dd` 문자열, `markdownContent`는 비면 `-`, `state.list[]`는 `StateDto{id, stateKey, refId, refContentType}`(`IMPRTC`·`DEMANDED`, 레거시 `COLLAPSED`는 모바일이 무시), `tag`는 `TagCmpstn`(`TagContentDto`). Java DTO와 필드명 대조 완료 | 구현·미검증 — 쓰기 호출·쓰기용 `rslt` 래퍼는 아직 없음(2-1) |
| 공통 `AjaxResponse` | `AjaxResponse<T, L>{rslt, message?, rsltObj?, rsltList?, rsltMap?}` (`src/types/auth.ts`) | 위 타입 감사의 "공통" 제안(`rsltList?`·`rsltMap?` 추가)을 Keep 2-0에서 반영 | 반영 (타입만, 기존 호출 동작 변화 없음) |

## 이번 감사에서 수정한 항목

- 없음(감사 단계). 이전 단계에서 같은 계약 불일치로 수정한 파일: `src/types/journalDay.ts`, `src/components/journal/JournalDayList.tsx`, `src/hooks/useJournalDay.ts`, `src/screens/ProfileScreen.tsx`.
- 이유: 남은 항목은 동작 변경(검색 병합 정책), 인증 워크스트림 영역(로그인), 또는 현재 소비자가 없는 응답 보강(showDiaries/showDreams)이라 "자명·저위험" 기준에 맞지 않거나, 다른 워크스트림(날짜 선택 달력)이 일·주·월 뷰를 편집 중이라 충돌 위험이 있다.

## 다음 수정 권장 순서

1. 검색 "전체" 400 (High) — `SearchScreen` + `searchEntries` 2회 호출 병합 또는 "전체" 제거.
2. 일자 조회에 `showDiaries: true, showDreams: true` (Med) — 웹과 동일한 enrich, 태그·라이프사이클 포팅 전제.
3. `TagCloudItem`에 `tagClass`·`textClass` 보존 (Med) — 태그 클라우드 포팅 1단계 전제.
4. 로그인 오류 `<br>` 정리 + `rsltMap`(중복 로그인·비밀번호 재설정) 흐름 (Med) — 인증 워크스트림과 조율.
5. `JournalEntry.threadList`·`tag` 타입 추가 (Med) — 스레드 칩 포팅 전제.