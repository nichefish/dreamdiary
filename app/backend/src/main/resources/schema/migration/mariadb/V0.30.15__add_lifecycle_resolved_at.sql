-- Keep의 현재 완료 전이 시각은 UTC로 저장한다. 시각 미확인 완료 행은 NULL을 유지한다.
ALTER TABLE lifecycle
    ADD COLUMN resolved_at DATETIME(6) NULL COMMENT 'Keep resolved transition at UTC' AFTER lifecycle_key;
