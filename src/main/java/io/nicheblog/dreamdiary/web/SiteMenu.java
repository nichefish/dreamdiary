package io.nicheblog.dreamdiary.web;

import io.nicheblog.dreamdiary.web.model.admin.SiteMenuAcsInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * WebSiteMenu
 * <pre>
 *  공통 상수 :: 사이트 메뉴 번호, 이름, URL 정의
 * </pre>
 *
 * @author nichefish
 */
public class SiteMenu {

    @RequiredArgsConstructor
    @Getter
    public enum TopMenuNo {
        WITHOUT_ASIDE("00000000", "사이드바 미노출"),
        MAIN("10000000", "메인");

        private final String menuNo;
        private final String desc;
    }

    // 상위 메뉴번호 :: 00000000인 경우 사이드바 미노출

    // 상위 메뉴번호 :: 주기능
    public static String MENU_NO_MAIN = "01000000";
    // 메뉴 :: 로그인
    public static SiteMenuAcsInfo LGN_PAGE = new SiteMenuAcsInfo(
            TopMenuNo.WITHOUT_ASIDE.menuNo,
            MENU_NO_MAIN,
            "로그인",
            SiteUrl.AUTH_LGN_FORM
    );

    // 메뉴 :: 메인
    public static SiteMenuAcsInfo MAIN_PORTAL = new SiteMenuAcsInfo(
            TopMenuNo.MAIN.menuNo,
            MENU_NO_MAIN,
            "메인",
            SiteUrl.MAIN
    );

    // 메뉴 :: 메인(관리자)
    public static SiteMenuAcsInfo MAIN_ADMIN = new SiteMenuAcsInfo(
            TopMenuNo.MAIN.menuNo,
            MENU_NO_MAIN,
            "메인(관리자)",
            SiteUrl.ADMIN_MAIN
    );

}