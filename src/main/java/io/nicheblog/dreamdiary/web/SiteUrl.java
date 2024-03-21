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

    String URL_ROBOT_TXT = "/robot.txt";
    String URL_ROBOTS_TXT = "/robots.txt";

    String URL_ROOT = "/";
    String URL_MAIN = "/main.do";


    String PREFIX_ADMIN = "/admin";
    String URL_ADMIN_MAIN = PREFIX_ADMIN + URL_MAIN;

    String URL_ADMIN_TEST_PAGE = PREFIX_ADMIN + "/testPage.do";

    String MENU_NM_ADMIN_MAIN = "메인 (관리자)";

}