-- Keep 서버 리마인드 기기와 발송 기록. 시각은 UTC로 저장한다.
CREATE TABLE keep_reminder_device (
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

CREATE TABLE keep_reminder_delivery (
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