package io.nicheblog.dreamdiary.web.controller.admin;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.admin.LgnPolicyDto;
import io.nicheblog.dreamdiary.web.model.cmm.SiteAcsInfo;
import io.nicheblog.dreamdiary.web.service.admin.LgnPolicyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.InvalidParameterException;

/**
 * LgnPolicyController
 * <pre>
 *  로그인 정책 관리 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class LgnPolicyController
        extends BaseControllerImpl {

    private final ActvtyCtgr actvtyCtgrCd = ActvtyCtgr.LGN_POLICY;      // 작업 카테고리 (로그 적재용)


    @Resource(name = "lgnPolicyService")
    private LgnPolicyService lgnPolicyService;

    /**
     * 사이트 관리 > 로그인 설정 관리 > 로그인 설정 등록/수정 화면 조회
     * (관리자MNGR만 접근 가능)
     */
    @RequestMapping(SiteUrl.LGN_POLICY_FORM)
    @Secured(Constant.ROLE_MNGR)
    public String lgnPolicyForm(
            final @ModelAttribute(Constant.SITE_MENU) SiteAcsInfo siteAcsInfo,
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.MAIN_PORTAL.setAcsPageInfo("로그인 정책 관리"));

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            // 현재는 항상 고정 ID(1L)로 조회한다.
            LgnPolicyDto rsUserLgnPolicDto = lgnPolicyService.getLgnPolicyDtlDto();
            isSuccess = (rsUserLgnPolicDto != null);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
            model.addAttribute("lgnPolicy", rsUserLgnPolicDto);
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

        return "/view/admin/lgn_policy/lgn_policy_reg_form";
    }

    /**
     * 사이트 관리 > 로그인 설정 관리 > 로그인 설정 등록/수정 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(SiteUrl.LGN_POLICY_REG_AJAX)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> lgnPolicyRegAjax(
            final @Valid LgnPolicyDto lgnPolicyDto,
            final LogActvtyParam logParam,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            isSuccess = lgnPolicyService.lgnPolicyReg(lgnPolicyDto);
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setCn(lgnPolicyDto.toString());
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }
}