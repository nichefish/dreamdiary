package io.nicheblog.dreamdiary.web.controller.admin;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.model.admin.LgnPolicyDto;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.service.admin.LgnPolicyService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Getter
    private final String baseUrl = Url.LGN_POLICY_FORM;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.LGN_POLICY;        // 작업 카테고리 (로그 적재용)

    @Resource(name = "lgnPolicyService")
    private LgnPolicyService lgnPolicyService;

    /**
     * 사이트 관리 > 로그인 설정 관리 > 로그인 설정 등록/수정 화면 조회
     * (관리자MNGR만 접근 가능)
     */
    @GetMapping(Url.LGN_POLICY_FORM)
    @Secured(Constant.ROLE_MNGR)
    public String lgnPolicyForm(
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.LGN_POLICY.setAcsPageInfo("로그인 정책 관리"));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 항목 조회 및 모델에 추가 :: 현재는 항상 고정 ID(1L)로 조회한다.
            LgnPolicyDto rsUserLgnPolicDto = lgnPolicyService.getDtlDto();
            model.addAttribute("lgnPolicy", rsUserLgnPolicDto);

            isSuccess = (rsUserLgnPolicDto != null);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
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

        return "/view/admin/lgn_policy/lgn_policy_reg_form";
    }

    /**
     * 사이트 관리 > 로그인 설정 관리 > 로그인 설정 등록/수정 (Ajax)
     * (관리자MNGR만 접근 가능)
     */
    @PostMapping(Url.LGN_POLICY_REG_AJAX)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> lgnPolicyRegAjax(
            final @Valid LgnPolicyDto lgnPolicy,
            final LogActvtyParam logParam,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // Validation
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            // 등록/수정 처리
            isSuccess = lgnPolicyService.regist(lgnPolicy);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn(lgnPolicy.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }
}