package io.nicheblog.dreamdiary.web.controller.prjct;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.flsys.FlsysSearchParam;
import io.nicheblog.dreamdiary.web.model.prjct.PrjctDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * PrjctInfoController
 * <pre>
 *  프로젝트 관리 컨트롤러
 * </pre>
 * TODO: 신규개발 예정
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class PrjctController
        extends BaseControllerImpl {

    // 작업 카테고리 (로그 적재용)
    private final String baseUrl = SiteUrl.PRJCT_INFO_LIST;
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.PRJCT;      // 작업 카테고리 (로그 적재용)

    @ModelAttribute("actvtyCtgrCd")
    public String addActvtyCtgrCd() {
        return actvtyCtgr.name();
    }

    /**
     * 프로젝트 관리 목록 화면 조회
     * 관리자MNGR만 접근 가능
     */
    @GetMapping(SiteUrl.PRJCT_INFO_LIST)
    @Secured({Constant.ROLE_MNGR})
    public String prjctInfoList(
            final LogActvtyParam logParam,
            final @ModelAttribute("searchParam") FlsysSearchParam searchParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.PRJCT_INFO.setAcsPageInfo(Constant.PAGE_LIST));

        // 활동 로그 목록 조회
        boolean isSuccess = false;
        String resultMsg = "";
        try {
            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 관리자페이지 화면 모드 세팅
            session.setAttribute("userMode", Constant.AUTH_MNGR);
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

        return "/view/prjct/info/prjct_info_list";
    }

    /**
     * 프로젝트 정보 등록 화면 조회
     * 관리자MNGR만 접근 가능
     */
    @RequestMapping(SiteUrl.PRJCT_REG_FORM)
    @Secured({Constant.ROLE_MNGR})
    public String prjctRegForm(
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.PRJCT_INFO.setAcsPageInfo(Constant.PAGE_REG));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            model.addAttribute("post", new PrjctDto());      // 빈 객체 주입 (freemarker error prevention)
            model.addAttribute(Constant.IS_REG, true);           // 등록/수정 화면 플래그 세팅
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            MessageUtils.alertMessage(resultMsg, baseUrl);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/prjct/info/prjct_info_reg_form";
    }
}