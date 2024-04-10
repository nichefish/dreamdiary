package io.nicheblog.dreamdiary.web.controller;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * MainController
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
public class MainController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = SiteUrl.MAIN;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.DEFAULT;      // 작업 카테고리 (로그 적재용)

    /**
     * 메인 화면 :: 사용자
     */
    @RequestMapping(value = {SiteUrl.ROOT, SiteUrl.MAIN})
    public String mainPage(
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.MAIN_PORTAL.setAcsPageInfo(Constant.PAGE_MAIN));

        // TODO: 접근 권한 통제

        // 메인 화면 꾸밀때까지 공지사항 목록 화면으로 리다이렉트
        return "redirect:" + SiteUrl.NOTICE_LIST;
        // return "view/main/main_page";
    }

    /**
     * 메인 화면 :: 관리자
     */
    @RequestMapping(value = {SiteUrl.ADMIN_MAIN})
    public String adminMainPage(
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.ADMIN_MAIN.setAcsPageInfo(Constant.PAGE_MAIN));

        // TODO: 접근 권한 통제

        // 메인 화면 꾸밀때까지 사용자 목록 화면으로 리다이렉트
        return "redirect:" + SiteUrl.USER_LIST;
        // return "view/main/main_page";
    }


}
