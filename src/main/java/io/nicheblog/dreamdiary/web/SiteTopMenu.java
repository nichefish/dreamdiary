package io.nicheblog.dreamdiary.web;

import lombok.RequiredArgsConstructor;

/**
 * SiteTopMenu
 * <pre>
 *  공통 상수 :: 사이트 상위메뉴 정보 Enum
 *  메뉴번호 :: 2depth를 상정하고 두자리씩 할당. depth 늘어날시 자리수를 늘려야 한다.
 * </pre>
 * TODO: 메뉴 DB관리로 완전히 바꾸기?
 *
 * @author nichefish
 */
@RequiredArgsConstructor
public enum SiteTopMenu {

    // COMMON
    NO_ASIDE("00", "NO_ASIDE", "사이드바 미노출", false),
    LGN("00", "0000", "로그인", false),
    ERROR("00", "0000", "ERROR", false),
    // PORTAL
    MAIN("00", "0000", "메인", false),
    NOTICE("01", "0100", "공지사항", false),
    DREAM("02", "0200", "꿈 관리", false),
    BOARD("03", "0300", "게시판", false),
    SCHDUL("04", "0400", "일정", false),
    // ADMIN
    ADMIN_MAIN("00", "0000", "메인", true),
    USER("01", "0100", "사용자 관리", true),
    ADMIN("02", "0200", "사이트 관리", true),
    LOG("03", "0300", "로그 관리", true);

    public final String idx;
    public final String menuNo;
    public final String menuNm;
    public final Boolean isMngrMenu;
}