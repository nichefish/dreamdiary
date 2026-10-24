# Confluence 로컬 적재 소스

실행·요청 구성·응답 해석·저장·판정·회귀 테스트를 이 디렉터리의 Python 소스로 관리한다. Python 표준 라이브러리만 사용한다.

## 실행 경로

```powershell
py -3 -B -m unittest discover -s tools/local_ingestion -p 'test_*.py' -v
py -3 -B tools/local_ingestion/run_confluence.py C:/Dev/projects/dreamdiary/workspace --scope all --apply
```

두 번째 명령은 stdin/stdout의 줄 단위 JSON RPC를 사용한다. stdout의 `request.tool` 및 `request.arguments`를 호스트가 Atlassian 읽기 커넥터에 그대로 전달하고, 전체 도구 응답 JSON을 한 줄로 stdin에 반환한다. 원문을 shell 명령에 삽입하거나 임시 원문 파일로 전달하지 않는다. 요청 생성과 응답 해석은 `connector.py`, 조회 순서는 `run_confluence.py`, 저장·판정은 `confluence.py`가 담당한다. Agent는 요청을 전달하는 역할이며 응답을 요약·보완하거나 완료 플래그를 수동으로 만들지 않는다.

`--scope`는 `all`, `csr`, `les`, `meetings`, `personal`이다. 대상 원격 ID와 미러 위치는 계약에 지정된 네 인덱스에서 읽는다. 원격 변경 도구는 허용 목록에 없으며 댓글 요청은 생성하지 않는다.

`--apply`를 생략하면 저장하지 않고 계획을 검증한다. 이때 통과 결과는 `PLAN_VALIDATED`이며 실제 적재 완료가 아니다. `confluence.py <mirror>`는 같은 판정/저장기를 전체 정규화 snapshot JSON stdin으로 실행하는 검사 인터페이스다. 운영 snapshot은 `run_confluence.py`와 `connector.py`의 관측값에서 생성한다.

## 두 관문

1. CLI는 현재 소스의 회귀 테스트가 통과한 뒤 조회·저장을 시작한다.
2. 실제 실행은 scope, 두 목록 cursor 연속성/명시 종료, 중복 ID, 필수 본문 조회, 본문 ID/버전/바이트 수/잘림, 경로·인덱스 정합, 댓글 미요청 경계에서 판정한다. FALSE는 FAILED, FALSE 없이 UNKNOWN이면 INCOMPLETE다. 모든 명제가 TRUE인 계획만 적용한다.

`index.json.ingestionAudits`에 요청 cursor, 종료 관측, 분기별 ID, 집계, 원자 판정, 모든 Python 실행/테스트 소스의 SHA-256와 저장 파일 해시를 기록한다. 기준선은 이 증거를 재계산하고 소스·파일 해시가 일치할 때만 사용한다. sourceHash는 실행 식별자이며 정확성의 대체 지표가 아니다. 기존 collectedAt·SUCCESS 문자열만 있는 기준선은 본문 재조회 대상이다.

## 저장과 보호

- Markdown 본문은 응답 문자열을 UTF-8 BOM 없이 보존한다. 제목만 바뀌면 파일명을 유지한다.
- 트리 안 이동은 본문/댓글 파일 바이트와 인덱스 경로를 함께 바꾼다. 댓글 미요청 시 댓글 내용은 보존한다.
- 완주한 목록에서 사라진 기존 페이지는 파일·항목을 보존하고 remoteMissing을 기록한다. 낮은 버전은 적용하지 않는다.
- 범위 밖 페이지 ID, 경로 탈출, 대상 충돌, 계획 이후 인덱스/파일 변경을 거절한다.
- 파일 교체 버퍼를 같은 디렉터리에 준비하고 재읽은 뒤 교체한다. I/O 실패 시 메모리에 보존한 원본 바이트를 복구하고 실패를 반환한다. 버퍼는 정형 저장기의 내부 원자 교체용이며 별도 원문 전달 경로가 아니다. 소스는 원격/로컬 문서를 자동 삭제하는 동기화 옵션을 제공하지 않는다.

## 회귀 사례와 현재 커넥터 경계

가상 사례는 신규/동일/높은/낮은 버전, 제목 변경, 본문·댓글 이동, 원격 부재 보존, cursor 단절/종료 부재, 중복 ID, 상세 실패/잘림/ID·버전·길이 불일치, BOM, 댓글 미요청, 다른 범위 보존, 두 번째 교체 실패 복구, 공동 세션 변경, 시각/수동 status의 신뢰 승격 금지, 실행 소스 변경 후 기준선 무효화, 조회 어댑터 오류/손실 요약/계층 부재를 고정 기대 결과로 확인한다. 실제 사람·원문을 fixture로 사용하지 않는다.

CQL 조회는 `content.ancestors,content.version` expansion으로 현재 계층과 버전을 관측한다. getConfluencePage의 본문/버전과 함께 대조한다. 페이지 descendants API에 폴더 ID를 넣는 경로는 사용하지 않는다. `page_record`는 현재 계층이 없으면 중단하며 캐시 계층으로 이동 여부를 추정하지 않는다. 호스트의 허용 읽기 전달 루프도 `relay.js`로 관리한다.


댓글 미요청 실행에서 footerTopLevel·inlineTopLevel·replies·total이 모두 정수 0이고 댓글 파일이 없으면, 기존 commentsFile 참조와 파일 부재를 그대로 보존한다. 이 분기는 ZERO_COUNT_MISSING_COMMENT_PRESERVED로 기록하고 파일 해시는 null(부재)로 남긴다. 실제 댓글 파일은 내용을 보존하며, 양수·누락·상충하는 집계의 파일 부재와 본문 파일 부재는 검증 실패다. 본문 이동 시에도 부재한 댓글 참조는 유지한다. 이 판정은 원격 현재 댓글이 0건이라는 증명이 아니라 요청 범위 밖 로컬 상태의 보존이다.

목록 종료는 커서 연속성, 응답 size, 중복 없는 누적 ID 수와 페이지 간 동일한 totalSize를 함께 검증한다. 각 커서 응답의 start가 0이어도 누적 고유 ID 수가 totalSize와 일치하면 종료를 확인한다. 누락·중복·합계 변동은 UNKNOWN으로 적용을 차단한다. 개인 스페이스 CQL은 기존 인덱스 source.spaceKey를 읽어 인용한 space 조건을 사용하며 키 부재는 조회 전에 중단한다.
