-- Prospective Project (prospective_project)
CREATE TABLE IF NOT EXISTS prospective_project (
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

-- Prospective Keep (prospective_keep)
-- @extends: BaseAttachableEntity
-- @uses: PrefixEmbed, StateEmbed, TagEmbed, LifecycleCmpstn
CREATE TABLE IF NOT EXISTS prospective_keep (
    -- ATTACHABLE
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Prospective Keep ID',
    content_type VARCHAR(32) NOT NULL DEFAULT 'PROSPECTIVE_KEEP' COMMENT '컨텐츠 타입',
    --
    sort_order INT NOT NULL DEFAULT 1 COMMENT '사용자 전역 순번',
    project_id INT NULL COMMENT '선택한 Prospective Project ID',
    -- CONTENT
    title VARCHAR(200) COMMENT '제목',
    content LONGTEXT COMMENT '내용',
    location VARCHAR(200) COMMENT '위치',
    date_text VARCHAR(200) COMMENT '일시(자유 텍스트 원문)',
    expire_dt DATE COMMENT '유효 기한',
    -- 말머리 선택은 prefix_content(ref_id, ref_content_type) 연결로 보유한다.
    -- FILE_GROUP
    file_group_id INT COMMENT '첨부파일 번호',
    -- AUDIT
    created_by VARCHAR(20) COMMENT '등록자 ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    updated_by VARCHAR(20) COMMENT '수정자 ID',
    updated_at DATETIME COMMENT '수정일시',
    deleted_at DATETIME COMMENT '삭제일시',
    INDEX idx_prospective_keep_owner_order (created_by, sort_order, id),
    INDEX idx_prospective_keep_project (project_id, created_by, sort_order, id),
    CONSTRAINT fk_prospective_keep_project
        FOREIGN KEY (project_id) REFERENCES prospective_project(id) ON DELETE SET NULL
) COMMENT = '미래에 다시 고려하기 위해 보존한 Keep 항목';
