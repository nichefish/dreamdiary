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

    // 메뉴 :: 로그인
    public static String MENU_NO_MAIN = "01000000";
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
    public static SiteAcsInfo DREAM = new SiteAcsInfo(
            SiteTopMenu.DREAM,
            MENU_NO_DREAM,
            "메인",
            SiteUrl.MAIN
    );

    // 일정
    public static SiteAcsInfo SCHDUL = new SiteAcsInfo(
            SiteTopMenu.SCHDUL,
            MENU_NO_DREAM,
            "메인",
            SiteUrl.MAIN
    );

    // 메뉴 :: 공지사항
    public static String MENU_NO_NOTICE = "01000000";
    public static SiteAcsInfo NOTICE = new SiteAcsInfo(
            SiteTopMenu.NOTICE,
            MENU_NO_NOTICE,
            "공지사항",
            SiteUrl.MAIN
    );

    // 메뉴 :: 사용자 관리
    public static String MENU_NO_USER = "01000000";
    public static SiteAcsInfo USER = new SiteAcsInfo(
            SiteTopMenu.USER,
            MENU_NO_USER,
            "사용자 관리",
            SiteUrl.AUTH_LGN_FORM
    );

    // 메뉴 :: 게시판 관리
    public static String MENU_NO_BOARD_DEF = "01000000";
    public static SiteAcsInfo BOARD_DEF = new SiteAcsInfo(
            SiteTopMenu.ADMIN,
            MENU_NO_BOARD_DEF,
            "게시판 관리",
            SiteUrl.AUTH_LGN_FORM
    );

    // 메뉴 :: 사이트 관리
    public static String MENU_NO_ADNIN = "01000000";
    public static SiteAcsInfo ADMIN = new SiteAcsInfo(
            SiteTopMenu.ADMIN,
            MENU_NO_ADNIN,
            "로그인",
            SiteUrl.AUTH_LGN_FORM
    );

    // 메뉴 :: 로그인 정책
    public static String MENU_NO_LGN_POLICY = "01000000";
    public static SiteAcsInfo LGN_POLICY = new SiteAcsInfo(
            SiteTopMenu.ADMIN,
            MENU_NO_LGN_POLICY,
            "로그인 정책 관리",
            SiteUrl.AUTH_LGN_FORM
    );


    // 메뉴 :: 활동 로그
    public static String MENU_NO_LOG_ACTVTY = "01000000";
    public static SiteAcsInfo LOG_ACTVTY = new SiteAcsInfo(
            SiteTopMenu.ADMIN,
            MENU_NO_LOG_ACTVTY,
            "활동 로그 관리",
            SiteUrl.AUTH_LGN_FORM
    );

    // 메뉴 :: 시스템 로그
    public static String MENU_NO_LOG_SYS = "01000000";
    public static SiteAcsInfo LOG_SYS = new SiteAcsInfo(
            SiteTopMenu.ADMIN,
            MENU_NO_LOG_SYS,
            "시스템 로그 관리",
            SiteUrl.AUTH_LGN_FORM
    );
}