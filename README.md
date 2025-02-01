# 개인 프로젝트: dreamdiary

## 기간:
- 2023.05.01~상시

## 개요:
- 일기, 꿈, 기타 개인 기록을 체계적으로 정리할 수 있는 개인 웹사이트입니다. 사용자는 게시물을 태그와 참조 기능을 활용하여 유연하게 분류하고 동적으로 관리할 수 있습니다.

## 기능 특징: 
#### 1. 사용자 관리, 코드 관리 등 기본 시스템 관리 기능.
#### 2. 게시판 및 태그 기능을 활용한 컨텐츠 분류 기능.
- 기존의 블로그, 메모 앱 등에서는 게시물 작성시 그 내용이 한 카테고리에 국한되지 않는 경우가 많음.
- 일반적인 글분류 기능에 더하여 태그 기능, 참조 추가 기능을 활용하여 내용을 유연하게 분류하고 각 정보들 사이의 연결을 동적으로 관리.
#### 3. 년도/월별, 태그별 집계 및 연관 데이터 조회를 통한 인사이트 제공.

## 사용 기술: 
* Java (v11 -> v17)
  * Spring Boot (v2.7.18) 
  * Spring Security, JWT, OAuth2
  * Spring AOP, Lombok, Mapstruct, EhCache, Redis
* Gradle v8.10.2
* MariaDB
* Spring Data JPA / MyBatis (both), JPA Criteria, QueryDSL, Flyway 
* Javascript(ES6), jQuery, Apache FreeMarker, Handlebars.js, TypeScript, Node.js(v20.11.1), Vue.js
* WebSocket(Stomp.js)
* Bootstrap 5 (Metronic 8)
* CSS/SCSS

## 주요 작업: (개발 포인트)
#### 1. 시스템 구조 설계 및 DB 설계
#### 2. 백엔드 개발
- Spring Boot, Spring Data JPA를 이용한 백엔드 로직 개발
- 상속 구조와 인터페이스를 활용한 공통 코드 분리 (관리 포인트 최소화)
- 기본 CRUD 인터페이스화를 통한 코드 간소화
- 태그, 단락, 열람자 등의 공통 기능 모듈화 및 인터페이스, 합성(composition)을 통한 손쉬운 기능 확장
- AOP를 이용한 비즈니스 로직과 공통 처리 로직 분리
- 즉각 응답이 필요하지 않은 요소들에 대한 비동기 처리 (이벤트 기반, Queue 기반 순차적 처리)
- 메모리 캐시(ehCache) 적용을 통한 데이터 조회 성능 향상
#### 3. 프론트엔드 개발
- Javascript, FreeMarker, Vue.js, Handlebars.js를 이용한 사용자 인터페이스 구현 (서버사이드 렌더링)
- 모듈식 노출 패턴을 활용한 Javascript 함수 모듈화
- Freemarker 매크로 기능을 활용한 기본 디자인 요소(버튼, 모달 등) 컴포넌트화
- Bootstrap 5를 사용한 반응형 디자인 구현
- WebSocket(Stomp)을 이용한 실시간 채팅 기능 구현
#### 4. junit, mockito를 이용한 테스트 코드 작성, jacoco를 이용한 커버리지 레포트
- 현재 테스트 커버리지 30%↑... (보완 중)

## 문제 해결:
1. 공통코드 분리 및 인터페이스화 과정에서 코드 복잡성 증가
- chatGPT와의 대화를 통해 베스트 프랙티스에 대한 방향성 잡음
- 테스트 코드 작성을 통해 시행착오의 부담을 줄임
- 코드 분리가 난해한 일부의 경우 리플렉션을 활용하여 공통 로직 분리
2. 외부 배포시 (오라클 클라우드) 시스템 속도 이슈
- 데이터베이스 테이블 인덱스 추가
- 캐시를 활용하여 데이터베이스 접근 회수 경감

---

##  **[전체 변경 기록 보기 (@CHANGELOG.md)](./CHANGELOG.md)**