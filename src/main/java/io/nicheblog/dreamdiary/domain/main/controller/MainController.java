package io.nicheblog.dreamdiary.domain.main.controller;

import io.nicheblog.dreamdiary.domain._core.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.domain._core.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.SiteMenu;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MainController
 * <pre>
 *  메인 화면 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@Controller
public class MainController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.MAIN;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.DEFAULT;      // 작업 카테고리 (로그 적재용)

    /**
     * 메인 화면 :: 사용자
     *
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 전달할 데이터를 저장하는 ModelMap 객체
     * @return {@link String} -- 뷰 이름을 나타내는 문자열
     */
    @GetMapping(value = {Url.ROOT, Url.MAIN})
    public String mainPage(
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.MAIN_PORTAL.setAcsPageInfo(Constant.PAGE_MAIN));

        // TODO: 접근 권한 통제

        // 메인 화면 꾸밀때까지 공지사항 목록 화면으로 리다이렉트
        return "redirect:" + Url.NOTICE_LIST;
        // return "/view/main/main_page";
    }

    /**
     * 메인 화면 :: 관리자
     *
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 전달할 데이터를 저장하는 ModelMap 객체
     * @return {@link String} -- 뷰 이름을 나타내는 문자열
     */
    @GetMapping(value = {Url.ADMIN_MAIN})
    public String adminMainPage(
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.ADMIN_MAIN.setAcsPageInfo(Constant.PAGE_MAIN));

        // TODO: 접근 권한 통제

        // 메인 화면 꾸밀때까지 사이트 관리 화면으로 리다이렉트
        return "redirect:" + Url.ADMIN_PAGE;
        // return "/view/main/main_page";
    }
}
