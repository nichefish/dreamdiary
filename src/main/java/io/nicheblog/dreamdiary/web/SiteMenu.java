package io.nicheblog.dreamdiary.web;

import io.nicheblog.dreamdiary.web.model.cmm.SiteMenuAcsInfo;

/**
 * SiteMenu
 * <pre>
 *  공통 상수 :: 사이트 메뉴 번호, 이름, URL 정의
 * </pre>
 *
 * @author nichefish
 */
public class SiteMenu {

    // 상위 메뉴번호 :: 주기능
    public static String MENU_NO_MAIN = "01000000";
    // 메뉴 :: 로그인
    public static SiteMenuAcsInfo LGN_PAGE = new SiteMenuAcsInfo(
            SiteTopMenu.NO_ASIDE,
            MENU_NO_MAIN,
            "로그인",
            SiteUrl.AUTH_LGN_FORM
    );

    // 메뉴 :: 메인
    public static SiteMenuAcsInfo MAIN_PORTAL = new SiteMenuAcsInfo(
            SiteTopMenu.MAIN,
            MENU_NO_MAIN,
            "메인",
            SiteUrl.MAIN
    );

    // 메뉴 :: 메인(관리자)
    public static SiteMenuAcsInfo MAIN_ADMIN = new SiteMenuAcsInfo(
            SiteTopMenu.MAIN,
            MENU_NO_MAIN,
            "메인(관리자)",
            SiteUrl.ADMIN_MAIN
    );

}