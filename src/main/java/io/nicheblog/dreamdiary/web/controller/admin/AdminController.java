package io.nicheblog.dreamdiary.web.controller.admin;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.model.AuthRoleDto;
import io.nicheblog.dreamdiary.global.auth.service.AuthRoleService;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

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

    @Getter
    private final String baseUrl = SiteUrl.ADMIN_PAGE;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.ADMIN;      // 작업 카테고리 (로그 적재용)

    @Resource
    private AuthRoleService authRoleService;

    /**
     * 사이트 관리 > 사이트 관리 화면 조회
     * (관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.ADMIN_PAGE)
    @Secured(Constant.ROLE_MNGR)
    public String adminPage(
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.ADMIN_PAGE.setAcsPageInfo("사이트 관리"));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 권한 정보 조회
            List<AuthRoleDto> authRoleList = authRoleService.getListDto(new HashMap<>());
            model.addAttribute("authRoleList", authRoleList);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/admin/admin_page";
    }

    /**
     * 사이트 관리 > 테스트 페이지
     * (관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.ADMIN_TEST)
    @Secured(Constant.ROLE_MNGR)
    public String testPage(
            final LogActvtyParam logParam,
            final ModelMap model
    ) {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.ADMIN.setAcsPageInfo("테스트 화면"));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/admin/test_page";
    }
}