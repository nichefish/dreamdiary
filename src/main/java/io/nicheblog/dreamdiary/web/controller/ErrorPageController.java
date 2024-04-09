package io.nicheblog.dreamdiary.web.controller;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.web.AcsPageNm;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

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

    @Getter
    private final String baseUrl = SiteUrl.ERROR_PAGE;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.DEFAULT;      // 작업 카테고리 (로그 적재용)

    /**
     * 기본 에러 화면(/error) :: 에러 코드에 따라 반환 페이지 분기
     */
    @RequestMapping(SiteUrl.ERROR)
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.FORBIDDEN.value()) return "/error/error_access_denied";
            if (statusCode == HttpStatus.NOT_FOUND.value()) return "/error/error_not_found";
        }
        return "/error/error";
    }

    /**
     * 에러 화면 (404 NOT FOUND)
     * 비로그인 사용자도 외부에서 접근 가능
     */
    @GetMapping(SiteUrl.ERROR_NOT_FOUND)
    public String errorNotFound(
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.ERROR.setAcsPageInfo(AcsPageNm.ERROR));

        // 에러 화면 반환
        return "/view/error/error_not_found";
    }

    /**
     * 에러 화면 (비인가 접근)
     * 비로그인 사용자도 외부에서 접근 가능
     */
    @GetMapping(SiteUrl.ERROR_ACCESS_DENIED)
    public String errorAccessDenied(
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.ERROR.setAcsPageInfo(AcsPageNm.ERROR));

        // 에러 화면으로 리다이렉트 리다리렉트
        return "/view/error/error_access_denied";
    }

    /**
     * 에러 화면 (공통)
     * 비로그인 사용자도 외부에서 접근 가능
     */
    @GetMapping({ SiteUrl.ERROR, SiteUrl.ERROR_PAGE })
    public String errorPage(
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.ERROR.setAcsPageInfo(AcsPageNm.ERROR));

        // 에러 화면으로 리다이렉트 리다리렉트
        return "/view/error/error_page";
    }
}
