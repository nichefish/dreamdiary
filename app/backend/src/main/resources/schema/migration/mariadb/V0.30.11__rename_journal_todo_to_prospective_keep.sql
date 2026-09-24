-- journal_todo를 Prospective Keep 도메인으로 이관한다.
-- @database : mariadb
-- @author : nichefish
--
-- Keep은 저널 월에 소속된 할 일이 아니라 미래에 다시 고려하기 위해 보존한 독립 항목이다.
-- 원본 ID를 유지한 채 테이블과 content type을 함께 바꾸며, attachable 연결도 같은 키로 수렴시킨다.

RENAME TABLE journal_todo TO prospective_keep;

UPDATE prospective_keep
SET content_type = 'PROSPECTIVE_KEEP'
WHERE content_type IS NULL OR content_type = 'JOURNAL_TODO';

ALTER TABLE prospective_keep
    MODIFY COLUMN content_type VARCHAR(32) NOT NULL DEFAULT 'PROSPECTIVE_KEEP' COMMENT '컨텐츠 타입',
    MODIFY COLUMN sort_order INT NOT NULL DEFAULT 1 COMMENT '사용자 전역 순번',
    ADD INDEX idx_prospective_keep_owner_order (created_by, sort_order, id);

UPDATE prefix_scope SET content_type = 'PROSPECTIVE_KEEP' WHERE content_type = 'JOURNAL_TODO';
UPDATE prefix_content SET ref_content_type = 'PROSPECTIVE_KEEP' WHERE ref_content_type = 'JOURNAL_TODO';
UPDATE `comment` SET ref_content_type = 'PROSPECTIVE_KEEP' WHERE ref_content_type = 'JOURNAL_TODO';
UPDATE tag_content SET ref_content_type = 'PROSPECTIVE_KEEP' WHERE ref_content_type = 'JOURNAL_TODO';
UPDATE tag_profile SET content_type = 'PROSPECTIVE_KEEP' WHERE content_type = 'JOURNAL_TODO';
UPDATE tag_category_profile SET content_type = 'PROSPECTIVE_KEEP' WHERE content_type = 'JOURNAL_TODO';
UPDATE meta_content SET ref_content_type = 'PROSPECTIVE_KEEP' WHERE ref_content_type = 'JOURNAL_TODO';
UPDATE state SET ref_content_type = 'PROSPECTIVE_KEEP' WHERE ref_content_type = 'JOURNAL_TODO';
UPDATE lifecycle SET ref_content_type = 'PROSPECTIVE_KEEP' WHERE ref_content_type = 'JOURNAL_TODO';
UPDATE managtr SET ref_content_type = 'PROSPECTIVE_KEEP' WHERE ref_content_type = 'JOURNAL_TODO';
UPDATE history SET ref_content_type = 'PROSPECTIVE_KEEP' WHERE ref_content_type = 'JOURNAL_TODO';
UPDATE viewer SET ref_content_type = 'PROSPECTIVE_KEEP' WHERE ref_content_type = 'JOURNAL_TODO';
UPDATE related_content SET left_content_type = 'PROSPECTIVE_KEEP' WHERE left_content_type = 'JOURNAL_TODO';
UPDATE related_content SET right_content_type = 'PROSPECTIVE_KEEP' WHERE right_content_type = 'JOURNAL_TODO';
