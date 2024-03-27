package io.nicheblog.dreamdiary.web.controller.admin;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ErrorPageController
 * <pre>
 *  에러 화면 조회 컨트롤러
 *  (스프링 기본 제공 ErrorController 구현 : WhiteLabel 에러페이지 override)
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class ErrorPageController
        extends BaseControllerImpl
        implements ErrorController {

    /**
     * 에러 화면 (404 NOT FOUND)
     * 비로그인 사용자도 외부에서 접근 가능
     */
    @GetMapping(SiteUrl.ERROR_NOT_FOUND)
    public String errorNotFound(
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.MAIN_PORTAL.setAcsPageInfo("ERROR"));

        // 에러 화면으로 리다이렉트 리다리렉트
        return "/view/error/error_not_found";
    }

    /**
     * 에러 화면 (비인가 접근)
     * 비로그인 사용자도 외부에서 접근 가능
     */
    @GetMapping(SiteUrl.ERROR_ACCESS_DENIED)
    public String errorAccessDenied(
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.MAIN_PORTAL.setAcsPageInfo("ERROR"));

        // 에러 화면으로 리다이렉트 리다리렉트
        return "/view/error/error_access_denied";
    }

    /**
     * 에러 화면 (공통)
     * 비로그인 사용자도 외부에서 접근 가능
     */
    @GetMapping({ SiteUrl.ERROR, SiteUrl.ERROR_PAGE })
    public String errorPage(
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.MAIN_PORTAL.setAcsPageInfo("ERROR"));

        // 에러 화면으로 리다이렉트 리다리렉트
        return "/view/error/error_page";
    }
}
