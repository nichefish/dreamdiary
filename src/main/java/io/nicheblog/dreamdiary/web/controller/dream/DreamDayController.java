package io.nicheblog.dreamdiary.web.controller.dream;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.service.dream.DreamDayService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

/**
 * DreamDayController
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
public class DreamDayController
        extends BaseControllerImpl {

    private final String baseUrl = SiteUrl.DREAM_DAY_LIST;               // 기본 URL
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.DREAM;        // 작업 카테고리 (로그 적재용)

    @Resource(name = "dreamDayService")
    private DreamDayService dreamDayService;

    /**
     * 꿈 일자 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(SiteUrl.DREAM_DAY_PAGE)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String dreamDayPage(
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.MAIN_PORTAL.setAcsPageInfo("꿈 목록 조회"));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, SiteUrl.ADMIN_MAIN);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/dream/day/dream_day_page";
    }

}
