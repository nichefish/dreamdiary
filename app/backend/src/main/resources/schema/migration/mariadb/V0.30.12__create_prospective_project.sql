-- Prospective Project 독립 aggregate를 생성한다.
-- @database : mariadb
-- @author : nichefish
--
-- SP-P1은 Project 자체의 CRUD와 사용자별 순서만 도입한다.
-- Keep 연결 FK는 후속 SAVEPOINT에서 별도 마이그레이션으로 추가한다.

CREATE TABLE prospective_project (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Prospective Project ID',
    title VARCHAR(200) NOT NULL COMMENT 'Project 이름',
    description LONGTEXT COMMENT 'Project 설명',
    sort_order INT NOT NULL COMMENT '사용자별 Project 순번',
    created_by VARCHAR(20) NOT NULL COMMENT '등록자 ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    updated_by VARCHAR(20) COMMENT '수정자 ID',
    updated_at DATETIME COMMENT '수정일시',
    deleted_at DATETIME COMMENT '삭제일시',
    INDEX idx_prospective_project_owner_order (created_by, sort_order, id)
) COMMENT = 'Keep을 지속적인 맥락으로 조직하는 Project';
