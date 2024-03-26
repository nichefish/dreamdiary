/*
package io.nicheblog.dreamdiary.web.controller.user;

import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.web.SiteUrl;
import lombok.extern.log4j.Log4j2;
import dreamdiary.nicheblog.io.cmm.Constant;
import dreamdiary.nicheblog.io.cmm.event.LogActvtyEvent;
import dreamdiary.nicheblog.io.cmm.intrfc.controller.impl.BaseControllerImpl;
import dreamdiary.nicheblog.io.cmm.util.MessageUtils;
import dreamdiary.nicheblog.io.web.model.user.UserDto;
import dreamdiary.nicheblog.io.web.model.user.UserInfoDto;
import dreamdiary.nicheblog.io.web.service.CmmService;
import dreamdiary.nicheblog.io.web.service.log.LogActvtyService;
import dreamdiary.nicheblog.io.web.service.user.UserReqstService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.InvalidParameterException;

*/
/**
 * UserReqstController
 * <pre>
 *  사용자 계정 신청 컨트롤러
 * </pre>
 * TODO: 기능추가 예정
 *
 * @author nichefish
 * @extends BaseControllerImpl
 *//*

@Controller
@Log4j2
public class UserReqstController
        extends BaseControllerImpl {

    private final String baseUrl = SiteUrl.LGN_FORM;                    // 기본 URL
    private final String actvtyCtgrCd = Constant.ACTVTY_USER_REQST;     // 작업 카테고리 (로그 적재용)

    @Resource(name = "userReqstService")
    private UserReqstService userReqstService;

    @Resource(name = "logActvtyService")
    private LogActvtyService logActvtyService;

    */
/**
     * 계정 정보 신청 화면 조회
     * 비로그인 사용자도 외부에서 접근 가능
     *//*

    @RequestMapping(value = SiteUrl.USER_REQST_REG_FORM)
    public String userReqstRegForm(
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        */
/* 사이트 메뉴 설정 *//*

        siteMenuAcsInfo.setAcsInfo(SiteMenu.MENU_USER_REQST, SiteMenu.PAGE_REG, request.getRequestURI());

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            model.addAttribute("user", new UserDto());      // 빈 객체 주입 (freemarker error prevention)
            model.addAttribute(Constant.IS_REG, true);           // 등록/수정 화면 플래그 세팅
            cdService.setModelCdData(Constant.AUTH_CD, model);
            cdService.setModelCdData(Constant.CMPY_CD, model);
            cdService.setModelCdData(Constant.TEAM_CD, model);
            cdService.setModelCdData(Constant.EMPLYM_CD, model);
            cdService.setModelCdData(Constant.JOB_TITLE_CD, model);
            model.addAttribute("isNotAuthenticated", true);

            isSuccess = true;
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
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


        model.addAttribute("userInfo", new UserInfoDto());      // 빈 객체 주입 (muatache error prevention)

        return "view/user/reqst/user_reqst_reg_form";
    }

    */
/**
     * 계정 정보 신청 (Ajax)
     * 비로그인 사용자도 외부에서 접근 가능
     *//*

    @PostMapping(value = {SiteUrl.USER_REQST_REG_AJAX})
    @ResponseBody
    public ResponseEntity<AjaxResponse> userReqstRegAjax(
            final @Valid UserDto userDto,
            final LogActvtyParam logParam,
            final BindingResult bindingResult,
            final MultipartHttpServletRequest request
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            UserDto rsDto = userReqstService.userReqstReg(userDto, request);
            isSuccess = (rsDto.getUserNo() != null);
            resultMsg = isSuccess ? "신규계정이 성공적으로 신청되었습니다." : "신규계정 신청에 실패했습니다.";
        } catch (Exception e) {
            resultMsg = MessageUtils.getExceptionMsg(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            logActvtyService.logLgnFailReg(userDto.getUserId(), resultMsg, isSuccess);
            log.info("{} / isSuccess: {}, resultMsg: {}", request.getRequestURI(), isSuccess, resultMsg);
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    */
/**
     * 사용자 관리 > 계정 및 권한 관리 > 사용자 승인 (Ajax)
     * (관리자MNGR만 접근 가능)
     *//*

    @PostMapping(SiteUrl.USER_REQST_CF_AJAX)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> userCfAjax(
            final LogActvtyParam logParam,
            final @RequestParam("userNo") String userNoStr
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            Integer userNo = Integer.parseInt(userNoStr);
            isSuccess = userReqstService.cfReqst(userNo);
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    */
/**
     * 사용자 관리 > 계정 및 권한 관리 > 사용자 승인취소 (Ajax)
     * (관리자MNGR만 접근 가능)
     *//*

    @PostMapping(SiteUrl.USER_REQST_UNCF_AJAX)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> userUncfAjax(
            final LogActvtyParam logParam,
            final @RequestParam("userNo") String userNoStr
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            Integer userNo = Integer.parseInt(userNoStr);
            isSuccess = userReqstService.uncfReqst(userNo);
            resultMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, resultMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, resultMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }
}
*/
