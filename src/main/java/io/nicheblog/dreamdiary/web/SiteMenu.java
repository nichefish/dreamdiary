package io.nicheblog.dreamdiary.web;

import io.nicheblog.dreamdiary.web.model.cmm.SiteAcsInfo;

/**
 * SiteMenu
 * <pre>
 *  공통 상수 :: 사이트 메뉴 번호, 이름, URL 정의
 * </pre>
 *
 * @author nichefish
 */
public class SiteMenu {


    public static String TOP_MENU_NO_MAIN = SiteTopMenu.MAIN.menuNo;
    public static String TOP_MENU_NO_NOTICE = SiteTopMenu.NOTICE.menuNo;
    public static String TOP_MENU_NO_BOARD = SiteTopMenu.BOARD.menuNo;
    public static String TOP_MENU_NO_SCHDUL = SiteTopMenu.SCHDUL.menuNo;
    public static String TOP_MENU_NO_DREAM = SiteTopMenu.DREAM.menuNo;


    // 상위 메뉴번호 :: 주기능
    public static String MENU_NO_MAIN = "01000000";
    // 메뉴 :: 로그인
    public static SiteAcsInfo LGN_PAGE = new SiteAcsInfo(
            SiteTopMenu.NO_ASIDE,
            MENU_NO_MAIN,
            "로그인",
            SiteUrl.AUTH_LGN_FORM
    );

    // 메뉴 :: 메인
    public static SiteAcsInfo MAIN_PORTAL = new SiteAcsInfo(
            SiteTopMenu.MAIN,
            MENU_NO_MAIN,
            "메인",
            SiteUrl.MAIN
    );

    // 메뉴 :: 꿈
    public static String MENU_NO_DREAM = "01000000";
    public static SiteAcsInfo MAIN_DREAM = new SiteAcsInfo(
            SiteTopMenu.DREAM,
            MENU_NO_MAIN,
            "메인",
            SiteUrl.MAIN
    );

    // 메뉴 :: 메인(관리자)
    public static SiteAcsInfo MAIN_ADMIN = new SiteAcsInfo(
            SiteTopMenu.MAIN,
            MENU_NO_MAIN,
            "메인(관리자)",
            SiteUrl.ADMIN_MAIN
    );

}