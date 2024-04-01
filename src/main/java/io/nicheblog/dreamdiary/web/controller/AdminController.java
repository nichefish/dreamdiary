package io.nicheblog.dreamdiary.web.controller;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * AdminController
 * <pre>
 *  사이트 관리 > 사이트 관리 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class AdminController
        extends BaseControllerImpl {

    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.ADMIN;      // 작업 카테고리 (로그 적재용)

    /**
     * 사이트 관리 > 사이트 관리 화면 조회
     * 관리자MNGR만 접근 가능
     */
    @RequestMapping(SiteUrl.ADMIN_PAGE)
    @Secured(Constant.ROLE_MNGR)
    public String adminPage(
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.ADMIN.setAcsPageInfo("사이트 관리"));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/admin/admin_page";
    }

    /**
     * 테스트 페이지
     * 관리자MNGR만 접근 가능
     */
    @RequestMapping(SiteUrl.ADMIN_TEST)
    @Secured(Constant.ROLE_MNGR)
    public String testPage(
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.ADMIN.setAcsPageInfo("테스트 화면"));

        return "/view/admin/test_page";
    }
}