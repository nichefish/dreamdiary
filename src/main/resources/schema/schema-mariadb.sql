-- 기능 구조 테이블 생성 쿼리 정보를 입력한다.
-- "JPA CASCADE INSERT에서는 먼저 INSERT 후 나중에 FK값을 업데이트하게 되므로 FK가 NOT_NULL이면 에러가 발생한다."
-- (=> JPA에서 다른 테이블과 연관성을 갖는 컬럼은 반드시 NULL을 허용해야 한다!) (NOT NULL이면 안된다)
-- @database : mariadb
-- @author : nichefish

-- 공지사항
CREATE TABLE IF NOT EXISTS NOTICE (
    POST_NO INT AUTO_INCREMENT PRIMARY KEY COMMENT '글번호',
    CTGR_CD VARCHAR(30) COMMENT '글분류 코드',
    TITLE VARCHAR(200) COMMENT '제목',
    CN LONGTEXT COMMENT '내용',
    HIT_CNT INT DEFAULT 0 COMMENT '조회수',
    IMPRTC_YN CHAR(1) DEFAULT 'N' COMMENT '중요여부',
    FXD_YN CHAR(1) DEFAULT 'N' COMMENT '상단고정여부',
    MDFABLE CHAR(50) DEFAULT 'REGSTR' COMMENT '수정권한',
    -- NOTICE 고유
    POPUP_YN CHAR(1) DEFAULT 'N' COMMENT '팝업여부',
    -- MANAGT
    MANAGTR_ID VARCHAR(20) COMMENT '작업자ID',
    MANAGT_DT DATETIME COMMENT '작업일시',
    -- 첨부파일
    ATCH_FILE_ID BIGINT COMMENT '첨부파일 ID',
    -- AUDIT
    REGSTR_ID VARCHAR(20) COMMENT '등록자ID',
    REG_DT DATETIME DEFAULT NOW() COMMENT '등록일시',
    MDFUSR_ID VARCHAR(20) COMMENT '수정자ID',
    MDF_DT DATETIME COMMENT '수정일시',
    DEL_YN CHAR(1) DEFAULT 'N' COMMENT '삭제여부'
);