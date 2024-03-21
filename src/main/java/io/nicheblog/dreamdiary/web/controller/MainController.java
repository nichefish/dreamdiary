package io.nicheblog.dreamdiary.web.controller;

import io.nicheblog.dreamdiary.cmm.Constant;
import io.nicheblog.dreamdiary.cmm.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.admin.SiteMenuAcsInfo;
import io.nicheblog.dreamdiary.web.model.log.actrvy.LogActvtyParamDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    /**
     * Robots.txt 설정 (검색엔진 수집 거부)
     */
    @RequestMapping(value = {SiteUrl.URL_ROBOT_TXT, SiteUrl.URL_ROBOTS_TXT})
    @ResponseBody
    public String getRobotsTxt(
            final LogActvtyParamDto logParam
    ) {

        // TODO: 로봇 수집 로그 남길거냐?

        return "User-agent: *\n" + "Disallow: /\n";
    }

    /**
     * 메인 화면 :: 사용자
     */
    @RequestMapping(value = {SiteUrl.URL_ROOT, SiteUrl.URL_MAIN})
    public String mainPage(
            final LogActvtyParamDto logParam,
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.MAIN_PORTAL.setAcsPageInfo(""));

        // TODO: 접근 권한 통제
        // TODO: 페이지 생성
        
        return "/view/main_page";
    }


}
