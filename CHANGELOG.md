
<div style="border: 1px solid #ccc; padding: 10px; margin-top:20px; border-radius: 5px;">

### 2025-01-12 | v0.9.5
- 개선 사항
  - 모달 호출 방식 내부 개선. (handlebars.js 호출 함수 조정 및 로깅 추가)
  - 저널 관련하여 사용자별로 자기 정보만 조회할 수 있게끔 변경.
    - 캐시 로직 보강. (사용자 ID별 캐시 관리 및 동기화 전략)
    - 컨텐츠 태그를 사용자별로 처리하도록 변경.

### 2025-01-09 | v0.9.4
- 개선 사항
  - 타입스크립트 마이그레이션 1차 완료. 패키지 구조 재조정. fetch api 추가.
  - 스크립트 도메인 모듈에 'initialized" 속성 추가. 각 모듈을 사용 페이지에서 명시적으로 초기화하도록 조치.
  - 메인 로직에서 파생된 비동기 이벤트 핸들링의 경우 별도 트랜잭션으로 분리.
  - 태그 카테고리 자동완성 관련하여 DB 동기화 방식을 파일 동기화 -> API 호출로 변경.
  - 일반게시판(BoardPost) 엔티티를 복합키 -> 단일키로 복원.

### 2025-01-05 | v0.9.3
- 개선 사항
  - 점진적인 TypeScript 마이그레이션. 기존 Vue.js 컴포넌트를 TypeScript로 전환.
  - 템플릿 패키지 구조 변경. modal 관련 컴포넌트들을 각 도메인별 _modal 폴더 아래로 이동.
  - jQuery ajax 공통함수를 fetch 로 교체.
  - TypeScript 전체 도메인 모듈에 "dF" 네임스페이스 추가.
  - Redis 연결 상태 체크하여 연결 불가시 Redis 캐시 사용하지 않도록 처리.
  - 컴파일 결과로 생성되는 파일은 형상관리에서 제거. (ts -> js, scss -> css, map)

### 2025-01-02 | v0.9.2
- 개선 사항
  - 점진적인 TypeScript 마이그레이션.
  - TypeScript 기존 "commons" 네임스페이스를 "cF"로 축약. 지나치게 비대한 util, validation 모듈 분리.
  - Ajax, 정규식 관련 메소드를 별도 모듈로 분리. ajax 요청 메소드를 get과 post로 분리.
  
### 2025-01-01 | v0.9.1
- 개선 사항:
  - 캐싱 처리 패턴을 기본 인터페이스에 통합.
  - TypeScript 도입 및 기존 JavaScript 점진적인 마이그레이션.
  - Url, MessageBundle을 TypeScript에서 사용하기 위한 exporter 추가.
  - 인터셉터 적용 로직 정리 및 단순화.
  - 주석 보강. (단방향 연관 클래스에 @see 추가)

### 2024-12-29 | v0.9.0
- 기능 추가:
  - 메뉴 관리 기능 추가.
    - 계층형 트리 구조. 
    - Draggable 컴포넌트를 이용한 정렬 로직 적용.
    - 기존의 정적 메뉴 관리를 동적 메뉴 관리로 전면 교체.
    - 메뉴 정보 캐시 처리. (EhCache)
- 개선 사항:
  - 코드 정보 및 사용자 정보 조회를 Entity JOIN -> EhCache, Redis 조회로 교체.
  - 서비스 클래스를 인터페이스/구현체로 분리.

### 2024-12-25 | v.0.8.0
- 기능 추가:
  - 웹소켓을 이용한 채팅 기능 구현. Vue.js 및 Stomp.js 도입.
    - JWT를 이용한 인증.
    - 채팅 윈도우 오픈 및 새 메세지 작성시 채팅창 스크롤 최하단으로 이동.
- 개선 사항:
  - MimeType enum 추가.

### 2024-12-18 | v.0.7.1
- 개선 사항:
  - 헤더 UI 개선. 사용자 정보 aside toggle → tooltip 으로 변경.
  - 읽지 않은 공지사항 알림 로직 추가.

### 2024-11-23 | v.0.7.0
- 개선 사항:
  - Java 11 -> 17로 버전 업.
  - Gradle 7.4 -> 8.10.2로 버전 업. (latest stable)
  - auth 패키지 구조 변경. 사용자 계정 잠금/해제 처리시 로그인 실패 횟수 리셋하도록 함.

</div>

---

<div style="border: 1px solid #ccc; padding: 10px; margin-top:20px; border-radius: 5px;">

### 2024-10-28 | v0.6.4
- 개선 사항:
  - Jacoco 커버리지 리포트 설정 추가.

### 2024-10-01 | v0.6.3
- 개선 사항:
  - 글 목록 화면에 태그 필터링 로직 추가.
    - 태그 클릭시 해당 태그에 해당하는 글만 목록에 보여짐. + 필터 클리어시 다시 전체 글 보임.
  - 저널 일기에 검색 로직 추가.
  - 내부 구조 대거 개선. 서버사이드 유효성 검사 추가. jQuery 의존성 낮추고 ES6 문법으로 전환.
  - JavaDoc, JSDoc 주석 대거 추가.

### 2024-09-30 | v0.6.2
- 개선 사항:
  - 글 단락 정렬 순서 조정 기능 추가.
  - 글 단락 접기(accordian) 기능 추가 및 UI 개선.
    - 만료 체크된 단락은 자동 접기 처리.
  - 공지사항, 저널 결산에 글 단락 기능 적용.
  - 저널 결산에 '내용' 항목 추가 및 해당 항목 목록에 표출.
    - 각 결산별 요약내용을 목록 화면에 표출함으로써 시간에 따른 주제의 흐름을 한 눈에 파악할 수 있도록 함.

### 2024-09-25 | v0.6.1
- 개선 사항:
  - 글 단락에 만료 여부 기능 추가.
    - 만료 체크시 해당 단락은 취소선 및 흐림 처리.

### 2024-09-24 | v.0.6.0
- 신규 기능:
  - 저널 주제 기능 추가.
    - 특정 날짜에 묶이지 않지만 인생 시기에 반복해서 나타나는 주요 주제들에 대해서 별도의 글 모음을 작성 가능하도록 함.
  - 글 단락(section) 기능 추가.
    - 게시물에 덧붙이는 토막글 정보로서, 게시물 내용을 주제별로 분절하여 유연하게 관리 가능하도록 함.
    - 태그 및 댓글 기능 적용.
- 개선 사항:
  - 저널 주제에 글 단락 기능 적용.
  - 중복 로그인 처리 로직 개선 및 보안성 강화.
  - 세션 만료 상태에서 AJAX 호출시, 로그인 페이지로 이동하는 대신 현재 페이지에 남는 선택지 추가.
    - 페이지 이동으로 작성 중인 내용이 날아가는 사태를 방지하기 위해, 현재 페이지에 머물러 작성 중인 내용을 복사 가능하도록 함.
  - 등록/수정 모달 팝업에서 닫기 버튼에 안전 닫기 기능 적용.
    - 클릭 실수로 작성 중인 내용이 날아가는 사태를 방지하기 위해, 닫기 버튼을 명시적으로 2번 클릭해야 닫히도록 함.

### 2024-08-24 | v.0.5.2
- 개선 사항:
  - 저널 일자 저장 및 저장한 날짜로 되돌아가기, 오늘 일자로 가기 기능 추가.
  - 로그인 페이지 접근시 브라우저 캐시 클리어 처리 추가.

### 2024-08-03 | v.0.5.1
- 신규 기능:
  - 태그 카테고리 기능 및 카테고리별 태그 필터 기능 추가.
  - 태그 자동완성 기능 추가.

### 2024-05-05 | v.0.5.0
- 신규 기능:
  - 저널 태그 기능 및 태그별 글 조회 기능 추가.
    - 저널 날짜, 꿈, 일기에 각각 태그를 부여하여 주제별로 분류하고, 해당 태그를 한 번에 조회함으로써 관련 주제의 흐름을 한 눈에 파악 가능하도록 함.
  - 태그클라우드 기능 추가.
    - 기간 내 태그 사용 빈도별로 시각적인 가중치를 부여하여 해당 기간의 메인 주제를 확인 가능하도록 함.
- 개선 사항:
  - 메모리 캐시(EhCache, L2Cache) 적용을 통한 반응 속도 개선.

### 2024-04-21 | v.0.4.0
- 신규 기능:
  - 저널 결산 기능 추가.
    - 년간 일기/꿈 기록 수 집계 및 중요 글들을 별도로 조회하여 해당 년도의 주요 테마를 한 눈에 파악 가능하도록 함.
    - 댓글 기능 적용.

### 2024-04-16 | v.0.3.0
- 신규 기능:
  - 기존 꿈 일기 기능을 저널 꿈 및 일기(JrnlDay, Dream&Diary) 기능으로 확장.
    - 날짜를 중심으로 --현실--일기와 --공상--꿈을 '저널'이라는 큰 틀에서 같이 관리하여 둘 사이의 맥락 공유.

### 2024-03-25 | v.0.2.0
- 신규 기능:
  - 꿈 일기(DreamDay&Piece) 기능 추가.
    - 꿈 날짜와 그에 귀속된 꿈 조각 목록으로 구성.
    - 댓글 기능 적용.
- 개선 사항:
  - 자바 인터페이스 및 상속, 합성을 활용한 공통 구조를 중심으로 소스코드 개선 및 리팩토링.

</div>

---

<div style="border: 1px solid #ccc; padding: 10px; margin-top:20px; border-radius: 5px;">

### 2024-03-20 | v.0.1.0
- 주요 변경 사항:
  - 기존 소스코드 구조(인트라넷 및 솔루션) 차용하여 프로젝트 세팅 및 Github Repository 구성.
    - (https://github.com/nichefish/dreamdiary)
  - 메트로닉 버전 업(v8.0.23 → v8.2.5) 및 관련 변경사항 적용하여 UI 재구성.
- 신규 기능:
  - 공지사항, 일반게시판, 휴가계획서 및 휴가 관리, 일정 관리, 경비지출서 및 경비 관리 기능 마이그레이션.

</div>
