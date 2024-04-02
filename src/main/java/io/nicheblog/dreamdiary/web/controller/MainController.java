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
import org.springframework.web.bind.annotation.ResponseBody;

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
     * Robots.txt 설정 (검색엔진 수집 거부)
     */
    @RequestMapping(value = {SiteUrl.ROBOT_TXT, SiteUrl.ROBOTS_TXT})
    @ResponseBody
    public String getRobotsTxt(
            final LogActvtyParam logParam
    ) {

        // TODO: 로봇 수집 로그 남길거냐?

        return "User-agent: *\n" + "Disallow: /\n";
    }

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
        
        return "view/main/main_page";
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

        return "view/main/main_page";
    }


}
