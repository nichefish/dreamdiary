package io.nicheblog.dreamdiary.web;

import lombok.RequiredArgsConstructor;

/**
 * SiteTopMenu
 * <pre>
 *  공통 상수 :: 사이트 상위메뉴 정보 Enum
 *  메뉴 번호 :: 2depth를 상정하고 두자리씩 할당. depth 늘어날시 자리수를 늘려야 한다.
 * </pre>
 * TODO: 메뉴 DB관리로 완전히 바꾸기?
 *
 * @author nichefish
 */
@RequiredArgsConstructor
public enum SiteTopMenu {

    // COMMON
    NO_ASIDE("00", "NO_ASIDE", "-", "", false),
    //
    LGN("00", "0000", "로그인", "", false),
    USER_REQST("00", "0000", "신규계정 신청", "", false),
    ERROR("00", "0000", "ERROR", "", false),
    // PORTAL
    MAIN("00", "0000", "메인", "", false),
    NOTICE("01", "0100", "공지사항", "notice", false),
    JRNL("02", "0200", "일지", "journal", false),
    BOARD("03", "0300", "일반게시판", "board", false),
    SCHDUL("04", "0400", "일정", "schedule", false),
    EXPTR("05", "0500", "경비", "expenditure", false),

    // ADMIN
    ADMIN_MAIN("00", "0000", "메인", "", true),
    USER("01", "0100", "사용자 관리", "user", true),
    VCATN_ADMIN("01", "0100", "휴가 관리", "vacation", true),
    EXPTR_ADMIN("03", "0300", "경비 관리", "expenditure", true),
    ADMIN("04", "0400", "사이트 관리", "admin", true),
    LOG("09", "0900", "로그 관리", "log", true);

    public final String idx;
    public final String menuNo;
    public final String menuNm;
    public final String label;
    public final Boolean isMngrMenu;
}