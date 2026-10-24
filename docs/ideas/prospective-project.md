# Prospective Project 설계

> 상태: **계약화·구현 완료** — SP-P1의 독립 Project 관리, SP-P2의 Keep 0..1 연결, SP-P3의 기존 `/keep` 화면 통합이 [Prospective Project 계약](../spec/PROSPECTIVE_PROJECT_SPEC.md)으로 구현됐다.

## 1. 목적

Prospective는 아직 실행을 약속하지 않은 채 미래에 열어 둔 대상을 다루는 기능 경계다. Keep은 그 안에서 미래의 나에게 남겨 둘 가치가 있다고 판단한 개별 항목이다.

Keep의 수와 종류가 늘어나면 평면 목록과 태그만으로는 같은 장기 맥락에 속한 항목을 함께 관리하기 어렵다. 이 설계는 Keep의 의미를 Todo나 계획으로 바꾸지 않으면서, 여러 Keep을 하나의 이름 있는 맥락으로 묶는 `Project`만 추가한다.

```text
Prospective
├─ Keep
└─ Project
   └─ Keep 0..N
```

Project에 연결됐다는 사실은 실행 결정이나 완료 의무를 뜻하지 않는다. Project와 그 안의 Keep은 계속 가능성으로 남을 수 있다.

## 2. 현재 구현에서 확인된 출발점

- `/keep`은 `prospective_keep`의 전 범위 목록을 보여 준다.
- Keep 항목에는 말머리, 태그, 상태, 라이프사이클, 유효 기한과 전역 `sortOrder`가 있다.
- 말머리는 제목 앞의 단일 분류이고, 태그는 여러 항목을 가로지르는 탐색 축이다.
- 여러 Keep을 장기간 하나의 맥락으로 묶는 관계는 없다.
- 현재 `sortOrder`는 사용자 Keep 전체의 순서이므로 프로젝트 내부 순서를 별도로 표현하지 않는다.

이 설계는 현재 Keep의 의미와 영속 모델을 유지하면서 실제로 필요한 프로젝트 조직 기능만 추가한다.

## 3. 핵심 개념

### Prospective

미래에 다시 고려할 가치가 있지만 실행 여부는 아직 열려 있는 대상을 다루는 기능 경계다. 데이터베이스 엔티티나 모든 대상을 담는 단일 테이블의 이름이 아니다.

### Keep

미래에 다시 고려하기 위해 붙잡아 둔 개별 항목이다.

- 실행 약속을 전제하지 않는다.
- 오래 열려 있다는 이유로 실패나 방치가 되지 않는다.
- Project에 연결되지 않고 독립적으로 존재할 수 있다.
- Project가 사라져도 함께 삭제되지 않는다.

### Project

여러 Keep을 하나의 지속적인 맥락으로 조직하는 이름 있는 객체다.

- Project 자체가 실행 약속이나 Todo는 아니다.
- Project는 Keep을 소유하지 않고 조직한다.
- Project 안에 Keep이 하나도 없는 상태도 유효하다.
- Project의 삭제는 연결 해제이며 Keep의 삭제가 아니다.

## 4. 관계와 불변식

초기 카디널리티는 다음으로 제한한다.

```text
Project 1 ─── N Keep
Keep    0 ─── 1 Project
```

- Keep은 Project를 선택하지 않거나 하나만 선택한다.
- Project는 Keep을 여러 개 포함할 수 있다.
- 한 Keep의 다중 Project 소속은 지원하지 않는다.
- 프로젝트 미지정 Keep은 기존과 같은 독립 항목이며 별도 이동이나 복제가 필요 없다.
- Project 연결과 해제는 Keep의 본문, 태그, 말머리, 상태, 라이프사이클 및 유효 기한을 변경하지 않는다.
- Project 삭제 시 연결된 Keep의 `projectId`를 `NULL`로 바꾼 뒤 Project를 soft-delete한다.
- Project 삭제가 Keep 삭제로 전파되는 cascade는 허용하지 않는다.
- 다른 사용자가 소유한 Project에 Keep을 연결할 수 없다.

다중 Project 소속은 실제 사용 사례가 확인되기 전까지 도입하지 않는다. 태그가 여러 맥락을 가로지르는 탐색을 이미 담당하므로, 최초 설계에서는 한 항목의 주된 프로젝트 맥락만 표현한다.

## 5. 데이터 모델

### `prospective_project`

```text
id            INT PK
title         VARCHAR(200) NOT NULL
description   LONGTEXT NULL
sort_order    INT NOT NULL
created_by    VARCHAR(20) NOT NULL
created_at    DATETIME NOT NULL
updated_by    VARCHAR(20) NULL
updated_at    DATETIME NULL
deleted_at    DATETIME NULL
```

계약:

- `title`은 사용자에게 보이는 프로젝트 이름이다.
- `description`은 프로젝트의 맥락을 보존하는 설명이며 선택값이다.
- `sort_order`는 사용자별 Project 목록 순서다.
- 같은 사용자가 같은 제목의 Project를 만들 수 있다. 제목은 식별자가 아니다.
- 사용자 소유 경계는 `created_by`로 제한한다.
- 일반 삭제는 soft-delete다.

### 현재 Keep 연결

현재 구현 단계에서는 `prospective_keep`에 다음 nullable FK를 추가하는 것이 최소 구조다.

```text
project_id    INT NULL FK prospective_project(id)
```

DB와 서비스는 다음을 함께 보장한다.

- FK의 삭제 동작은 cascade delete가 아니다.
- Project 삭제 서비스가 같은 트랜잭션에서 연결을 먼저 해제한다.
- 등록·수정 시 서버가 Project와 Keep의 소유자가 같은지 검증한다.
- 존재하지 않거나 삭제된 Project ID는 거부한다.

Project 내부 별도 정렬 컬럼은 추가하지 않는다. 실제 사용에서 프로젝트별 수동 정렬 요구가 확인되기 전까지 기존 Keep 정렬 계약을 유지한다.

## 6. 사용자 동작

### Project 관리

- Project를 제목과 선택적 설명으로 등록한다.
- Project 제목과 설명을 수정한다.
- Project 목록 순서를 변경한다.
- Project를 삭제하면 연결된 Keep은 프로젝트 미지정 상태가 된다.

### Keep 연결

- Keep 등록·수정 시 Project를 선택하거나 비워 둘 수 있다.
- 기존 Keep에 Project를 지정할 수 있다.
- 지정된 Project를 다른 Project로 변경할 수 있다.
- Project 연결을 해제할 수 있다.
- 연결 변경은 Keep의 실행 여부나 라이프사이클을 바꾸지 않는다.

### Keep 화면

기존 `/keep` 화면에 다음 범위 선택만 추가한다.

```text
프로젝트 미지정
전체
Project A
Project B
```

- `프로젝트 미지정`은 기본 탭이며 `projectId == null`인 Keep만 보여 준다.
- `전체`는 Project 연결 여부와 무관하게 현재 전 범위 Keep을 보여 준다.
- `프로젝트 미지정`은 `projectId == null`인 Keep만 보여 준다.
- 특정 Project를 선택하면 해당 Project의 Keep만 보여 준다.
- 기존 말머리·태그·검색어·기한 필터는 선택한 범위 안에서 그대로 적용한다.
- Project 화면을 별도 대시보드로 만들지 않는다.

## 7. API 경계

구체 URL과 DTO 이름은 구현 진입 시 기존 URL 규약과 대조해 확정한다. 필요한 행위의 경계는 다음과 같다.

### Project

- 내 Project 목록 조회
- 내 Project 상세 조회
- Project 등록
- Project 수정
- Project 삭제
- Project 순서 변경

### Keep

- Keep 등록·수정 요청에서 nullable `projectId` 수신
- Keep 목록에서 Project ID 조건 조회
- 프로젝트 미지정 조건 조회

Project 접근과 Keep 연결 요청은 모두 로그인 사용자 소유 범위에서 검증한다. 소유 불일치, 삭제된 Project 선택, 존재하지 않는 Project 선택은 구조화 로그를 남기고 거부한다.

## 8. 삭제와 보존

Project는 Keep의 보존 경계가 아니다.

```text
Project 삭제
→ 연결된 Keep의 projectId 해제
→ Project soft-delete
→ Keep 본문과 모든 부착 정보 보존
```

Project 삭제 전에 연결된 Keep의 개수를 사용자에게 보여 주고, 삭제하면 항목들이 프로젝트 미지정 상태로 남는다는 결과를 알린다. Project 삭제와 Keep 영구삭제를 결합하는 선택지는 제공하지 않는다.

## 9. 구현 분할안

구현에 진입하면 다음 SAVEPOINT로 나눈다.

### SP-P1. Project 도메인과 영속 구조

- `prospective_project` 테이블과 마스터 스키마
- Project entity, DTO, repository, service, controller
- 사용자 소유 검증과 삭제 시 Keep 연결 해제
- Project CRUD 테스트

### SP-P2. Keep 연결

- `prospective_keep.project_id` nullable FK
- Keep 등록·수정의 Project 소유 검증
- Project 및 프로젝트 미지정 목록 조건
- 기존 Keep 데이터는 전부 `project_id = NULL` 유지

### SP-P3. Keep 화면 통합

- Project 목록과 범위 선택
- Keep 등록·수정의 Project 선택
- Project 등록·수정·삭제 조작
- 기존 필터와 Project 범위의 결합
- 관련 화면·인터랙션·컴포넌트 spec 동기화

각 SAVEPOINT는 DB 마이그레이션, 마스터 스키마, 코드, 주석, 테스트와 관련 spec이 같은 현재 계약을 설명하는 상태로 끝낸다.

## 10. 비목표

다음은 이 설계에 포함하지 않는다.

- Milestone
- Stage
- Roadmap
- Project 목표일과 기한
- Project 완료율
- Project 라이프사이클 또는 자동 완료
- Project별 별도 Keep 정렬
- 하나의 Keep을 여러 Project에 연결
- Project 중첩
- Project 템플릿
- 협업, 담당자 및 권한 공유
- 시간·장소·맥락에 따른 자동 재노출

이 항목들은 향후 기능 목록으로 예약하지 않는다. 실제 사용에서 반복되는 요구가 관찰되고 독립적인 계약이 확인될 때 새 설계 대상으로 다룬다.

## 11. 검증 기준

- Project가 없는 기존 Keep 사용 흐름이 그대로 성립한다.
- 사용자는 Keep을 Project에 연결하거나 다시 미지정 상태로 돌릴 수 있다.
- Project 선택은 Keep의 라이프사이클과 상태를 변경하지 않는다.
- Project 삭제 후 연결됐던 Keep을 `전체`와 `프로젝트 미지정`에서 다시 찾을 수 있다.
- 다른 사용자의 Project ID를 이용한 조회·연결·수정·삭제가 거부된다.
- Project가 비어 있어도 조회·수정·삭제할 수 있다.
- 동일 제목의 Project가 서로 다른 식별자로 독립적으로 존재할 수 있다.

## 12. 재검토 조건

다음 사용 패턴이 실제로 반복될 때만 모델 확장을 검토한다.

- 하나의 Keep을 여러 Project에서 함께 관리해야 한다.
- 같은 Project 안에서 Keep의 수동 순서가 반드시 필요하다.
- Project와 무관하게 독립적으로 이름·목표일·달성 조건을 갖는 목표점이 반복된다.
- Project 자체의 진행·보류·종결 상태가 필요하다.

재검토는 관찰된 사례를 먼저 기록하고, 현재 모델로 표현할 수 없는 정보가 무엇인지 확인한 뒤 진행한다.
