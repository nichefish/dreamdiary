package io.nicheblog.dreamdiary.global.cmm.log;

import lombok.AllArgsConstructor;

/**
 * ActvtyCtgr
 * <pre>
 *  공통으로 사용하는 코드성 데이터 정의
 * </pre>
 * TODO: enum으로 점진적 변환쓰
 * TODO: Freemarker 단에서 enum을 어떻게 처리할 것인지?
 *
 * @author nichefish
 */
@AllArgsConstructor
public enum ActvtyCtgr {

    // DEFAULT
    DEFAULT("DEFAULT"),

    // API
    API_JANDI("API_잔디"),
    API_KASI("API_특일"),
    API_DREAM("API_꿈 관리"),
    JANDI("잔디"),

    // ADMIN
    LGN("로그인"),
    ADMIN("관리"),
    CACHE("캐시"),

    NOTICE("공지사항"),
    DREAM("꿈 관리"),
    BOARD_POST("일반게시판"),

    VCATN_PAPR("휴가계획서"),
    VCATN_SCHDUL("휴가사용일자"),
    VCATN_STATS("년도별 휴가관리"),

    EXPTR_PRSNL_PAPR("경비지출서"),
    EXPTR_PRSNL_RPT("월간지출내역"),
    EXPTR_PRSNL_STATS("경비지출누적집계"),
    EXPTR_REQST("물품구매/경조사비 신청"),
    
    TAG("태그"),

    USER("사용자 관리"),
    USER_MY("내 정보 관리"),
    USER_REQST("신규계정 신청"),

    // MANAGE
    LGN_POLICY("로그인 정책 관리"),
    MENU("메뉴 관리"),
    BOARD_DEF("일반게시판 관리"),
    TMPLAT("템플릿 관리"),
    CD("코드 관리"),
    // LOG
    LOG_ACTVTY("활동 로그"),
    LOG_SYS("시스템 로그"),
    LOG_STATS("로그 통계"),
    // CMM
    FILE("파일"),
    FLSYS("파일 시스템"),

    // DEPRECATED
    SCHDUL("일정 관리"),
    NOTION("노션");

    public final String desc;
}
