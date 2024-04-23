package io.nicheblog.dreamdiary.web.controller.cmm.notion;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.model.cmm.flsys.FlsysSearchParam;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Nullable;

/**
 * NotionPageController
 * <pre>
 *  노션 페이지 관련 컨트롤러
 * </pre>
 * TODO: 보완 예정
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class NotionController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.NOTION_HOME;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.NOTION;      // 작업 카테고리 (로그 적재용)

    private final String NOTION_PUBLIC = "";

    /**
     * 파일시스템 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @RequestMapping(Url.NOTION_HOME)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String flsysList(
            @ModelAttribute("searchParam") FlsysSearchParam searchParam,
            final @RequestParam("notionPageId") @Nullable String notionPageIdParam,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.LGN_POLICY.setAcsPageInfo("노션 연동"));

        // 활동 로그 목록 조회
        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            String notionPageId = !StringUtils.isEmpty(notionPageIdParam) ? notionPageIdParam : NOTION_PUBLIC;
            // NotionRetriever notionRetriever = new NotionRetriever(notionPageId);
            // String notionPage = notionRetriever.render();
            // model.addAttribute("notionPage", notionPage);
            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, Url.ADMIN_MAIN);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/notion/notion_home";
    }
}
