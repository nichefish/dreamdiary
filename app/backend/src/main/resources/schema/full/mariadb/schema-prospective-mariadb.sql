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

-- Keep 서버 리마인드 기기와 발송 기록. 시각은 UTC로 저장한다.
CREATE TABLE IF NOT EXISTS keep_reminder_device (
    device_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL PRIMARY KEY COMMENT '앱 설치 UUID',
    username VARCHAR(20) NOT NULL COMMENT '인증 계정 소유자',
    expo_token VARCHAR(255) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT 'Expo 푸시 토큰',
    time_zone VARCHAR(80) NOT NULL COMMENT '기기가 등록한 IANA 시간대',
    active TINYINT NOT NULL DEFAULT 1 COMMENT '알림 기기 활성 여부',
    created_at DATETIME(6) NOT NULL DEFAULT (UTC_TIMESTAMP(6)),
    updated_at DATETIME(6) NOT NULL DEFAULT (UTC_TIMESTAMP(6)),
    UNIQUE KEY uk_keep_reminder_token (expo_token),
    INDEX idx_keep_reminder_device_owner (username, active),
    CONSTRAINT ck_keep_reminder_device_active CHECK (active IN (0,1))
) COMMENT='Keep 서버 리마인드 수신 기기';

CREATE TABLE IF NOT EXISTS keep_reminder_delivery (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    username VARCHAR(20) NOT NULL,
    local_date DATE NOT NULL COMMENT '발송 기기의 로컬 날짜',
    slot VARCHAR(10) NOT NULL COMMENT 'MORNING 또는 EVENING',
    token_hash CHAR(64) NOT NULL COMMENT '발송 토큰 SHA-256',
    status VARCHAR(20) NOT NULL COMMENT '발송 시도 및 전달 확인 상태',
    ticket_id VARCHAR(100) NULL COMMENT 'Expo 접수 ID',
    error_code VARCHAR(80) NULL COMMENT '개인 정보 없는 오류 코드',
    created_at DATETIME(6) NOT NULL DEFAULT (UTC_TIMESTAMP(6)),
    updated_at DATETIME(6) NOT NULL DEFAULT (UTC_TIMESTAMP(6)),
    UNIQUE KEY uk_keep_reminder_delivery (username, device_id, local_date, slot),
    INDEX idx_keep_reminder_receipt (status, created_at),
    CONSTRAINT fk_keep_reminder_delivery_device FOREIGN KEY (device_id) REFERENCES keep_reminder_device(device_id),
    CONSTRAINT ck_keep_reminder_slot CHECK (slot IN ('MORNING','EVENING')),
    CONSTRAINT ck_keep_reminder_status CHECK (status IN ('CLAIMED','ACCEPTED','HANDED_OFF','REJECTED','UNKNOWN','SKIPPED'))
) COMMENT='Keep 슬롯별 발송 시도 및 Expo 확인 기록';