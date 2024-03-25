-- dreamdiary 기능 관련 구조 테이블 생성 쿼리 정보를 입력한다.
-- "JPA CASCADE INSERT에서는 먼저 INSERT 후 나중에 FK값을 업데이트하게 되므로 FK가 NOT_NULL이면 에러가 발생한다."
-- (=> JPA에서 다른 테이블과 연관성을 갖는 컬럼은 반드시 NULL을 허용해야 한다!) (NOT NULL이면 안된다)
-- @database : mariadb
-- @author : nichefish

-- 꿈 일자
CREATE TABLE DREAM_DAY (
    DREAM_DAY_NO BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '꿈 일자 고유 번호',
    DREAMT_DT DATE COMMENT '꿈 일자',
    -- 날짜미상시 처리용
    DT_UNKNOWN_YN CHAR(1) DEFAULT 'N' COMMENT '날짜미상여부',
    YY INT COMMENT '년도',
    MNTH INT COMMENT '월',
    APRXMT_DT DATE COMMENT '대략일자 (날짜미상시 해당일자 이후에 표기)',
    -- ATCH_FILE
    ATCH_FILE_NO INT COMMENT '첨부파일 번호',
    -- AUDIT
    REGSTR_ID VARCHAR(20) COMMENT '등록자ID',
    REG_DT DATETIME DEFAULT NOW() COMMENT '등록일시',
    MDFUSR_ID VARCHAR(20) COMMENT '수정자ID',
    MDF_DT DATETIME COMMENT '수정일시',
    DEL_YN CHAR(1) DEFAULT 'N',
    -- CONSTRAINT
    INDEX (DREAMT_DT)
) COMMENT = '꿈 일자';

-- 꿈 조각
CREATE TABLE DREAM_PIECE (
    DREAM_PIECE_NO BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '꿈조각 고유 번호',
    DREAM_DAY_NO BIGINT COMMENT '꿈일자 번호',
    SORT_ORDR INT DEFAULT 1 COMMENT '꿈조각 순번',
    CN LONGTEXT COMMENT '내용',
    IMPRTC_YN CHAR(1) DEFAULT "N" COMMENT '중요여부',
    EDIT_COMPT_YN CHAR(1) DEFAULT 'N' COMMENT '편집완료여부',
    ELSE_DREAM_YN CHAR(1) DEFAULT 'N' COMMENT '타인 꿈 여부',
    ELSE_DREAMER_NM VARCHAR(64) COMMENT '꿈꾼이 이름',
    -- ATCH_FILE
    ATCH_FILE_NO INT COMMENT '첨부파일 번호',
    -- AUDIT
    REGSTR_ID VARCHAR(20) COMMENT '등록자ID',
    REG_DT DATETIME DEFAULT NOW() COMMENT '등록일시',
    MDFUSR_ID VARCHAR(20) COMMENT '수정자ID',
    MDF_DT DATETIME COMMENT '수정일시',
    DEL_YN CHAR(1) DEFAULT 'N'
) COMMENT = '꿈 조각';