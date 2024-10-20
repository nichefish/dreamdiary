-- (구) 공통 코드 데이터 쿼리 정보를 입력한다.
-- 쿼리 줄바꿈 안됨. 무조건 한 줄에 한 쿼리 단위로 실행된다.
-- @database : mariadb
-- @author : nichefish

-- -----------------------

-- (구) 필수 :: 휴가 분류 코드 추가
REPLACE INTO cmm_cl_cd (CL_CD, CL_CD_NM, CL_CD_DC) VALUES ('VCATN_CD', '휴가', '휴가');
-- (구) 필수 :: 휴가 상세 코드 추가
REPLACE INTO cmm_dtl_cd (CL_CD, DTL_CD, DTL_CD_NM, DTL_CD_DC, SORT_ORDR) VALUES ('VCATN_CD', 'ANNUAL', '연차', '연차', '1');
REPLACE INTO cmm_dtl_cd (CL_CD, DTL_CD, DTL_CD_NM, DTL_CD_DC, SORT_ORDR) VALUES ('VCATN_CD', 'AM_HALF', '오전반차', '오전반차', '2');
REPLACE INTO cmm_dtl_cd (CL_CD, DTL_CD, DTL_CD_NM, DTL_CD_DC, SORT_ORDR) VALUES ('VCATN_CD', 'PM_HALF', '오후반차', '오후반차', '3');
REPLACE INTO cmm_dtl_cd (CL_CD, DTL_CD, DTL_CD_NM, DTL_CD_DC, SORT_ORDR) VALUES ('VCATN_CD', 'PBLEN', '공가', '공가', '4');
REPLACE INTO cmm_dtl_cd (CL_CD, DTL_CD, DTL_CD_NM, DTL_CD_DC, SORT_ORDR) VALUES ('VCATN_CD', 'CTSNN', '경조휴가', '경조휴가', '5');
REPLACE INTO cmm_dtl_cd (CL_CD, DTL_CD, DTL_CD_NM, DTL_CD_DC, SORT_ORDR) VALUES ('VCATN_CD', 'MNSTR', '생리휴가', '생리휴가', '6');
REPLACE INTO cmm_dtl_cd (CL_CD, DTL_CD, DTL_CD_NM, DTL_CD_DC, SORT_ORDR) VALUES ('VCATN_CD', 'UNPAID', '무급휴가', '무급휴가', '7');

-- -----------------------
