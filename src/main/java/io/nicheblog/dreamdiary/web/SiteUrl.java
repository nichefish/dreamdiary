package io.nicheblog.dreamdiary.web;

/**
 * WebSiteMenu
 * <pre>
 *  공통 상수 :: 사이트 메뉴 번호, 이름, URL 정의
 * </pre>
 *
 * @author nichefish
 */
public interface SiteUrl {

    /**
     * 메인 도메인
     */
    String SITE_DOMAIN = "dreamdiary.nicheblog.io";

    String ROBOT_TXT = "/robot.txt";
    String ROBOTS_TXT = "/robots.txt";

    String ROOT = "/";
    String MAIN = "/main.do";

    /**
     * 메인 > 로그인 화면
     * NO_ASIDE
     */
    // URL
    String PREFIX_AUTH = "/auth";
    String AUTH_LGN_FORM = PREFIX_AUTH + "/lgnForm.do";
    String AUTH_LGN_PROC = PREFIX_AUTH + "/lgnProc.do";
    String AUTH_LGN_PW_CHG_AJAX = PREFIX_AUTH + "/lgnPwChgAjax.do";
    String AUTH_LGOUT = PREFIX_AUTH + "/lgout.do";

    String PREFIX_ADMIN = "/admin";
    String ADMIN_MAIN = PREFIX_ADMIN + MAIN;
    String ADMIN_TEST_PAGE = PREFIX_ADMIN + "/testPage.do";




    /**
     * ERROR
     */
    // URL
    String PREFIX_ERROR = "/error";
    String ERROR = "/error";
    String ERROR_PAGE = PREFIX_ERROR + "/errorPage.do";
    String ERROR_NOT_FOUND = PREFIX_ERROR + "/notFound.do";
    String ERROR_ACCESS_DENIED = PREFIX_ERROR + "/accessDenied.do";
}