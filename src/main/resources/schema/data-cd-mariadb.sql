-- 공통 코드 데이터 쿼리 정보를 입력한다.
-- 쿼리 줄바꿈 안됨. 무조건 한 줄에 한 쿼리 단위로 실행된다.
-- @database : mariadb
-- @author : nichefish

-- -------------------

-- 옵션 :: 분류 코드 분류 코드 추가
REPLACE INTO cmm_cl_cd (cl_cd, cl_cd_nm, cl_ctgr_cd, dc) VALUES ('CL_CTGR_CD', '분류 코드 분류 코드', 'SYS', '분류');
-- 필수 :: 분류 코드 분류 상세 코드 추가
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('CL_CTGR_CD', 'SYS', '시스템', '시스템에서 필수적으로 사용되는 코드입니다.', '1');

-- -------------------

-- 필수 :: 잔디 토픽 분류 코드 추가
REPLACE INTO cmm_cl_cd (cl_cd, cl_cd_nm, cl_ctgr_cd, cl_cd_dc) VALUES ('JANDI_TOPIC_CD', '잔디 토픽', '', '잔디 토픽 코드');
-- 필수 :: 잔디 토픽 상세 코드 추가
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('JANDI_TOPIC_CD', 'NOTICE', '공지사항', '공지사항', '1');
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('JANDI_TOPIC_CD', 'SCHDUL', '일정공유', '일정공유', '2');
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('JANDI_TOPIC_CD', 'TEST', '테스트', '테스트', '99');

-- -----------------------

-- 필수 :: 수정권한 분류 코드 추가
REPLACE INTO cmm_cl_cd (cl_cd, cl_cd_nm, cl_ctgr_cd, dc) VALUES ('MDFABLE_CD', '수정권한', 'SYSTEM', '수정권한 코드');
-- 필수 :: 수정권한 상세 코드 추가
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('MDFABLE_CD', 'REGSTR', '등록자', '등록자만 수정 가능', '1');
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('MDFABLE_CD', 'MNGR', '관리자', '관리자만 수정 가능', '2');
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('MDFABLE_CD', 'USER', '사용자', '사용자만 수정 가능', '3');
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('MDFABLE_CD', 'ALL', '전체', '전체 수정 가능', '99');

-- -----------------------

-- 옵션 :: 일정 분류 코드 추가
REPLACE INTO CMM_CL_CD (cl_cd, cl_cd_nm, cl_ctgr_cd, dc) VALUES ('SCHDUL_CD', '일정', '', '일정 코드');
-- 필수 :: 수정권한 상세 코드 추가
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('SCHDUL_CD', 'HLDY', '공휴일', '공휴일', '1');
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('SCHDUL_CD', 'CEREMONY', '행사', '행사', '2');
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('SCHDUL_CD', 'BRTHDY', '생일', '생일', '3');
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('SCHDUL_CD', 'INDT', '내부일정', '내부일정', '11');
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('SCHDUL_CD', 'TLCMMT', '재택', '재택', '21');
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('SCHDUL_CD', 'OUTDT', '외근', '외근', '22');
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('SCHDUL_CD', 'VCATN', '휴가', '휴가', '23');
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('SCHDUL_CD', 'ETC', '기타', '기타', '99');

-- -----------------------

-- 필수 :: 저널 결산 구분 분류 코드 추가
REPLACE INTO cmm_cl_cd (cl_cd, cl_cd_nm, cl_ctgr_cd, dc) VALUES ('JRNL_SUMRY_TY_CD', '저널 결산 구분', 'SYSTEM', '저널 결산 구분 코드');
-- 필수 :: 저널 결산 구분 상세 코드 추가
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('JRNL_SUMRY_TY_CD', 'DIARY', '일기', '일기 결산', '1');
REPLACE INTO cmm_dtl_cd (cl_cd, dtl_cd, dtl_cd_nm, dc, sort_ordr) VALUES ('JRNL_SUMRY_TY_CD', 'DREAM', '꿈', '꿈 결산', '2');
