-- Prospective Keep을 사용자 소유 Project에 선택적으로 연결한다.
-- @database : mariadb
-- @author : nichefish
--
-- 기존 Keep은 project_id NULL을 유지하며 Project 미지정 상태가 기본이다.
-- Project 물리 삭제가 발생해도 Keep은 보존되고 연결만 해제된다.

ALTER TABLE prospective_keep
    ADD COLUMN project_id INT NULL COMMENT '선택한 Prospective Project ID' AFTER sort_order,
    ADD INDEX idx_prospective_keep_project (project_id, created_by, sort_order, id),
    ADD CONSTRAINT fk_prospective_keep_project
        FOREIGN KEY (project_id) REFERENCES prospective_project(id) ON DELETE SET NULL;
