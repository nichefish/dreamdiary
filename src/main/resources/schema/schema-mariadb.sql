-- 기능 구조 테이블 생성 쿼리 정보를 입력한다.
-- "JPA CASCADE INSERT에서는 먼저 INSERT 후 나중에 FK값을 업데이트하게 되므로 FK가 NOT_NULL이면 에러가 발생한다."
-- (=> JPA에서 다른 테이블과 연관성을 갖는 컬럼은 반드시 NULL을 허용해야 한다!) (NOT NULL이면 안된다)
-- @database : mariadb
-- @author : nichefish

-- -----------------------

-- 공지사항 (notice)
-- @extends: BaseManagtEntity
-- @Uses: CommentEmbed
CREATE TABLE IF NOT EXISTS notice (
    -- CLSF
    post_no INT AUTO_INCREMENT PRIMARY KEY COMMENT '글 번호',
    content_type VARCHAR(32) DEFAULT 'notice' COMMENT '게시판 코드',
    --
    popup_yn CHAR(1) DEFAULT 'N' COMMENT '팝업여부',
    -- BOARD
    title VARCHAR(200) COMMENT '제목',
    cn LONGTEXT COMMENT '내용',
    ctgr_cd VARCHAR(50) COMMENT '글분류코드',
    imprtc_yn CHAR(1) DEFAULT 'N' COMMENT '중요 여부',
    fxd_yn CHAR(1) DEFAULT 'N' COMMENT '상단고정 여부',
    hit_cnt INT DEFAULT 0 COMMENT '조회수',
    -- MANAGT (module)
    mdfable CHAR(50) DEFAULT 'REGSTR' COMMENT '수정권한',
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

-- ---------- --

-- 경비지출서 (exptr_prsnl_papr)
-- @extends: BaseManagtEntity
-- @Uses: CommentEmbed
CREATE TABLE IF NOT EXISTS exptr_prsnl_papr (
    -- CLSF
    post_no INT AUTO_INCREMENT PRIMARY KEY COMMENT '글 번호',
    content_type VARCHAR(32) DEFAULT 'notice' COMMENT '게시판 코드',
    --
    yy VARCHAR(4) COMMENT '년도',
    mnth VARCHAR(2) COMMENT '월',
    cf_yn CHAR(1) DEFAULT 'N' COMMENT '취합완료 여부',
    -- BOARD
    title VARCHAR(200) COMMENT '제목',
    cn LONGTEXT COMMENT '내용',
    ctgr_cd VARCHAR(50) COMMENT '글분류코드',
    imprtc_yn CHAR(1) DEFAULT 'N' COMMENT '중요 여부',
    fxd_yn CHAR(1) DEFAULT 'N' COMMENT '상단고정 여부',
    hit_cnt INT DEFAULT 0 COMMENT '조회수',
    -- MANAGT (module)
    mdfable CHAR(50) DEFAULT 'REGSTR' COMMENT '수정권한',
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
) COMMENT = '경비지출서';






-- 물품구매 및 경조사비 신청
CREATE TABLE IF NOT EXISTS exptr_reqst(
    -- CLSF
    post_no INT AUTO_INCREMENT PRIMARY KEY COMMENT '글 번호',
    content_type VARCHAR(32) DEFAULT 'notice' COMMENT '게시판 코드',
    --
    cf_yn CHAR(1) DEFAULT 'N' COMMENT '처리여부'
    -- BOARD
    title VARCHAR(200) COMMENT '제목',
    cn LONGTEXT COMMENT '내용',
    ctgr_cd VARCHAR(50) COMMENT '글분류코드',
    imprtc_yn CHAR(1) DEFAULT 'N' COMMENT '중요 여부',
    fxd_yn CHAR(1) DEFAULT 'N' COMMENT '상단고정 여부',
    hit_cnt INT DEFAULT 0 COMMENT '조회수',
    -- MANAGT (module)
    mdfable CHAR(50) DEFAULT 'REGSTR' COMMENT '수정권한',
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
) COMMENT = '물품구매/경조사비 신청';




-- 경비지출 항목
CREATE TABLE IF NOT EXISTS EXPTR_PRSNL_ITEM (
    exptr_prsnl_item_no INT AUTO_INCREMENT PRIMARY KEY,
    ref_post_no INT,
    exptr_dt DATE,
    exptr_ty_cd VARCHAR(30),
    exptr_amt INT,
    cn varchar(500),
    rm VARCHAR(500),
    rject_yn CHAR(1) DEFAULT 'N',
    rject_resn VARCHAR(500),
    -- ATCH_FILE
    atch_file_dtl_no INT COMMENT '영수증 파일 ID',
    orgnl_rcipt_yn CHAR(1) DEFAULT 'N',
    -- AUDIT
    del_yn CHAR(1) DEFAULT 'N' COMMENT '삭제여부'
) COMMENT = '경비지출항목';