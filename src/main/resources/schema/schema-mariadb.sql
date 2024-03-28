-- 기능 구조 테이블 생성 쿼리 정보를 입력한다.
-- "JPA CASCADE INSERT에서는 먼저 INSERT 후 나중에 FK값을 업데이트하게 되므로 FK가 NOT_NULL이면 에러가 발생한다."
-- (=> JPA에서 다른 테이블과 연관성을 갖는 컬럼은 반드시 NULL을 허용해야 한다!) (NOT NULL이면 안된다)
-- @database : mariadb
-- @author : nichefish

-- -----------------------

-- 공지사항 (notice)
-- @extends: BasePostEntity
CREATE TABLE IF NOT EXISTS notice (
    post_no INT AUTO_INCREMENT PRIMARY KEY COMMENT '글 번호',
    content_type VARCHAR(32) DEFAULT 'notice' COMMENT '게시판 코드',
    popup_yn CHAR(1) DEFAULT 'N' COMMENT '팝업여부',
    -- BOARD
    title VARCHAR(200) COMMENT '제목',
    cn LONGTEXT COMMENT '내용',
    ctgr_cd VARCHAR(50) COMMENT '글분류코드',
    fxd_yn CHAR(1) DEFAULT 'N' COMMENT '상단고정여부',
    hit_cnt INT DEFAULT 0 COMMENT '조회수',
    imprtc_yn CHAR(1) DEFAULT 'N' COMMENT '중요여부',
    mdfable CHAR(50) DEFAULT 'REGSTR' COMMENT '수정권한',
    -- MANAGT
    managtr_id VARCHAR(20) COMMENT '작업자ID',
    managt_dt DATETIME COMMENT '작업일시',
    -- ATCH_FILE
    atch_file_no INT COMMENT '첨부파일 ID',
    -- AUDIT
    regstr_id VARCHAR(20) COMMENT '등록자 ID',
    reg_dt DATETIME DEFAULT NOW() COMMENT '등록일시',
    mdfusr_id VARCHAR(20) COMMENT '수정자 ID',
    mdf_dt DATETIME COMMENT '수정일시',
    del_yn CHAR(1) DEFAULT 'N' COMMENT '삭제 여부'
);