-- 게시판 관련 테이블 생성 쿼리 정보를 입력한다.
-- "JPA CASCADE INSERT에서는 먼저 INSERT 후 나중에 FK값을 업데이트하게 되므로 FK가 NOT_NULL이면 에러가 발생한다."
-- (=> JPA에서 다른 테이블과 연관성을 갖는 컬럼은 반드시 NULL을 허용해야 한다!) (NOT NULL이면 안된다)
-- @database : mariadb
-- @author : nichefish

-- ---------- --

-- 게시판 정의 정보 :: BaseManagtEntity 상속
CREATE TABLE IF NOT EXISTS board_def (
    board_cd VARCHAR(30) PRIMARY KEY COMMENT '게시판코드',
    board_nm VARCHAR(120) COMMENT '게시판 이름',
    ctgr_cl_cd VARCHAR(30) COMMENT '분류 코드',
    menu_no VARCHAR(10) COMMENT '메뉴 번호',
    -- MANAGE
    sort_ordr INT DEFAULT 0 COMMENT '정렬 순서',
    use_yn CHAR(1) DEFAULT 'Y' COMMENT '사용 여부',
    -- AUDIT
    regstr_id VARCHAR(20) COMMENT '등록자 ID',
    reg_dt DATETIME DEFAULT NOW() COMMENT '등록일시',
    mdfusr_id VARCHAR(20) COMMENT '수정자 ID',
    mdf_dt DATETIME COMMENT '수정일시',
    del_yn CHAR(1) DEFAULT 'N' COMMENT '삭제 여부'
) COMMENT = '게시판 정의';

-- ---------- --

-- 게시판 게시물 :: BaseClsfEntity 상속
CREATE TABLE IF NOT EXISTS board_post(
    post_no INT COMMENT '글 번호',
    content_type VARCHAR(30) COMMENT '게시판 코드',
    -- POST
    title VARCHAR(200) COMMENT '제목',
    cn VARCHAR(500) COMMENT '내용',
    ctgr_cd VARCHAR(50) COMMENT '글분류코드',
    fxd_yn CHAR(1) DEFAULT 'N' COMMENT '상단고정여부',
    hit_cnt INT DEFAULT 0 COMMENT '조회수',
    imprtc_yn CHAR(1) DEFAULT 'N' COMMENT '중요여부',
    mdfable CHAR(50) DEFAULT 'REGSTR' COMMENT '수정권한',
    -- MANAGT
    managtr_id VARCHAR(20) COMMENT '작업자ID',
    managt_dt DATETIME COMMENT '작업일시',
    -- ATCH_FILE
    atch_file_no INT COMMENT '첨부파일 번호',
    -- AUDIT
    regstr_id VARCHAR(20) COMMENT '등록자 ID',
    reg_dt DATETIME DEFAULT NOW() COMMENT '등록일시',
    mdfusr_id VARCHAR(20) COMMENT '수정자 ID',
    mdf_dt DATETIME COMMENT '수정일시',
    del_yn CHAR(1) DEFAULT 'N' COMMENT '삭제 여부',
    -- CONSTRAINT
    PRIMARY KEY (post_no, content_type)
) COMMENT = '게시판 게시물';

-- ---------- --

-- (공통) 댓글 :: BaseClsfEntity 상속
CREATE TABLE IF NOT EXISTS comment (
    post_no INT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '댓글 번호',
    content_type VARCHAR(32) DEFAULT 'comment' COMMENT '게시판 코드',
    ref_post_no INT COMMENT '참초 글번호',
    ref_content_type VARCHAR(30) COMMENT '참조 게시판 코드',
    cn LONGTEXT COMMENT '내용',
    -- ATCH_FILE
    atch_file_no INT COMMENT '첨부파일 번호',
    -- AUDIT
    regstr_id VARCHAR(20) COMMENT '등록자 ID',
    reg_dt DATETIME DEFAULT NOW() COMMENT '등록일시',
    mdfusr_id VARCHAR(20) COMMENT '수정자 ID',
    mdf_dt DATETIME COMMENT '수정일시',
    del_yn CHAR(1) DEFAULT 'N' COMMENT '삭제 여부'
) COMMENT = '게시판 댓글';




