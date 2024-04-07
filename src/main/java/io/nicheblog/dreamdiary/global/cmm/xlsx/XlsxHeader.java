package io.nicheblog.dreamdiary.global.cmm.xlsx;

import io.nicheblog.dreamdiary.global.cmm.xlsx.model.XlsxCell;

import java.util.List;

/**
 * XlsxConstant
 * <pre>
 *  엑셀 다운로드 관련 데이터(상수) 정의
 *  ×256(한글 1글자)으로 세팅됨. 대략적인 글자수로 세팅하면 된다.
 * </pre>
 *
 * @author nichefish
 */
public interface XlsxHeader {

    /** 공지사항 */
    List<XlsxCell> NOTICE = List.of(
        new XlsxCell("제목", 10),
        new XlsxCell("등록자", 10),
        new XlsxCell("등록일자", 10)
    );

    /** 게시판 */
    List<XlsxCell> BOARD = List.of(
            new XlsxCell("제목", 10),
            new XlsxCell("등록자", 10),
            new XlsxCell("등록일자", 10)
    );
    
    /**
     * 경비지출서
     */
    List<XlsxCell> EXPTR_PRSNL_STATS = List.of(
            new XlsxCell("구분", 10),
            new XlsxCell("소속", 10),
            new XlsxCell("이름", 10),
            new XlsxCell("1월", 10),
            new XlsxCell("2월", 10),
            new XlsxCell("3월", 10),
            new XlsxCell("4월", 10),
            new XlsxCell("5월", 10),
            new XlsxCell("6월", 10),
            new XlsxCell("7월", 10),
            new XlsxCell("8월", 10),
            new XlsxCell("9월", 10),
            new XlsxCell("10월", 10),
            new XlsxCell("11월", 10),
            new XlsxCell("이름", 10),
            new XlsxCell("12월", 10)
    );

    /**
     * 경비지출서
     */
    List<XlsxCell> EXPTR_PRSNL_RPT = List.of(
            new XlsxCell("지출일", 10),
            new XlsxCell("소속", 10),
            new XlsxCell("이름", 10),
            new XlsxCell("항목", 10),
            new XlsxCell("상세", 10),
            new XlsxCell("금액", 10),
            new XlsxCell("비고", 10),
            new XlsxCell("스캔본", 10),
            new XlsxCell("원본", 10)
    );

    /**
     * 휴가
     */
    List<XlsxCell> VCATN_STATS = List.of(
            new XlsxCell("직원명", 10),
            new XlsxCell("입사일", 10),
            new XlsxCell("산정기준일", 10),
            new XlsxCell("근속년수", 10),
            new XlsxCell("기본연차", 10),
            new XlsxCell("근속추가연차", 10),
            new XlsxCell("프로젝트추가연차", 10),
            new XlsxCell("안식주(2주연차)", 10),
            new XlsxCell("총연차", 10),
            new XlsxCell("소진", 10),
            new XlsxCell("잔여연차", 10)
    );

    /**
     * 휴가
     */
    List<XlsxCell> VCATN_SCHDUL = List.of(
            new XlsxCell("직원명", 10),
            new XlsxCell("날짜", 10),
            new XlsxCell("종류", 10),
            new XlsxCell("소진일", 10),
            new XlsxCell("비고", 10)
    );

    /**
     * 사용자
     */
    List<XlsxCell> USER = List.of(
            new XlsxCell("아이디", 10),
            new XlsxCell("이름", 10),
            new XlsxCell("권한", 10),
            new XlsxCell("전화번호", 10),
            new XlsxCell("이메일", 10),
            new XlsxCell("잠금여부", 10),
            new XlsxCell("접속IP", 10),
            new XlsxCell("설명", 10),
            new XlsxCell("등록자", 10),
            new XlsxCell("최종접속일시", 10)
    );

    /**
     * 사용자
     */
    List<XlsxCell> LOG_ACTVTY = List.of(
            new XlsxCell("작업일시", 10),
            new XlsxCell("작업자ID", 10),
            new XlsxCell("작업자이름", 10),
            new XlsxCell("작업자IP", 10),
            new XlsxCell("작업내용", 10),
            new XlsxCell("작업결과", 10)
    );
}
