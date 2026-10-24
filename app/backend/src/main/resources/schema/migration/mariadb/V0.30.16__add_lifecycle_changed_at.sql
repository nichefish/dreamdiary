-- 라이프사이클 상태가 현재 값으로 바뀐 시각(UTC). 상태가 실제로 바뀔 때만 서비스가 기록한다.
-- (OPEN 은 행 삭제라 해당 없음. 이 마이그레이션 이전 행은 NULL = 미확인.)
ALTER TABLE lifecycle
    ADD COLUMN changed_at DATETIME(6) NULL COMMENT 'lifecycle state changed at UTC' AFTER resolved_at;

-- [선택] 기존 행 백필 — 필요할 때만 주석을 풀어 따로 실행한다. (Flyway 가 실행하지 않도록 주석 처리)
-- 현재 RESOLVED 이면서 완료 시각을 아는 행은 그 완료 시각이 곧 현재 상태로 바뀐 시각이다.
-- 그 밖의 행(PENDING·FAILED·EXPIRED·DROPPED, 시각 미확인 RESOLVED)은 바뀐 시각을 알 수 없어 NULL 로 둔다.
-- UPDATE lifecycle SET changed_at = resolved_at WHERE changed_at IS NULL AND resolved_at IS NOT NULL AND lifecycle_key = 'RESOLVED';
