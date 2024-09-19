package io.nicheblog.dreamdiary.web.controller.user;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteMenu;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.model.vcatn.papr.VcatnPaprDto;
import io.nicheblog.dreamdiary.web.model.vcatn.stats.VcatnStatsYyDto;
import io.nicheblog.dreamdiary.web.service.user.UserMyService;
import io.nicheblog.dreamdiary.web.service.user.UserService;
import io.nicheblog.dreamdiary.web.service.vcatn.papr.VcatnPaprService;
import io.nicheblog.dreamdiary.web.service.vcatn.stats.VcatnStatsService;
import io.nicheblog.dreamdiary.web.service.vcatn.stats.VcatnStatsYyService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserMyController
 * <pre>
 *  내 정보 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class UserMyController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.USER_MY_DTL;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.USER_MY;     // 작업 카테고리 (로그 적재용)

    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "userMyService")
    private UserMyService userMyService;
    @Resource(name = "vcatnStatsService")
    private VcatnStatsService vcatnStatsService;
    @Resource(name = "vcatnStatsYyService")
    private VcatnStatsYyService vcatnStatsYyService;
    @Resource(name = "vcatnPaprService")
    private VcatnPaprService vcatnPaprService;

    /**
     * 내 정보 (상세) 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @GetMapping(Url.USER_MY_DTL)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    public String myInfoDtl(
            final LogActvtyParam logParam,
            final ModelMap model
    ) throws Exception {

        /* 사이트 메뉴 설정 */
        model.addAttribute(Constant.SITE_MENU, SiteMenu.MAIN_PORTAL.setAcsPageInfo("내 정보"));

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 내 정보 조회 및 모델에 추가
            String lgnUserId = AuthUtils.getLgnUserId();
            UserDto lgnUserDto = userService.getDtlDto(lgnUserId);
            model.addAttribute("user", lgnUserDto);

            // 휴가계획서 년도 정보 조회 (시작일자~종료일자)
            try {
                //if (AuthService.hasEcnyDt()) {
                VcatnStatsYyDto statsYy = vcatnStatsYyService.getCurrVcatnYyDt();
                model.addAttribute("vcatnYy", statsYy);
                String userId = AuthUtils.getLgnUserId();
                // VcatnStatsDto vcatnStatsDtl = vcatnStatsService.getVcatnStatsDtl(statsYy, userId);
               //  model.addAttribute("vcatnStats", vcatnStatsDtl);
                // 올해 사용 휴가 목록 조회
                Map<String, Object> searchParamMap = new HashMap<>() {{
                    put("searchStartDt", statsYy.getBgnDt());
                    put("searchEndDt", statsYy.getEndDt());
                    put("regstrId", lgnUserId);
                }};
                List<VcatnPaprDto.LIST> vcatnPaprList = vcatnPaprService.getListDto(searchParamMap);
                model.addAttribute("vcatnPaprList", vcatnPaprList);
                // }
            } catch (Exception e) {
                log.info("휴가계획서 정보를 조회하지 못했습니다.");
            }

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
            MessageUtils.alertMessage(rsltMsg, Url.MAIN);
        } finally {
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return "/view/user/my/user_my_dtl";
    }

    /**
     * 프로필 이미지 변경
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(Url.USER_MY_REMOVE_PROFL_IMG_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> removeProflImgAjax(
            final LogActvtyParam logParam
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 이미지 파일 삭제 처리
            isSuccess = userMyService.removeProflImg();
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 프로필 이미지 삭제
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(Url.USER_MY_UPLOAD_PROFL_IMG_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> uploadProflImgAjax(
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 이미지 파일 업로드 처리
            isSuccess = userMyService.uploadProflImg(request);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }


    /**
     * 내 비밀번호 확인 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(Url.USER_MY_PW_CF_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> myPwChkAjax(
            final LogActvtyParam logParam,
            final @RequestParam("currPw") @Nullable String currPw
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 확인 처리
            String lgnUserId = AuthUtils.getLgnUserId();
            isSuccess = userMyService.myPwCf(lgnUserId, currPw);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 내 비밀번호 변경 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(Url.USER_MY_PW_CHG_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> myPwChgAjax(
            final LogActvtyParam logParam,
            final @RequestParam("newPw") @Nullable String newPw,
            final @RequestParam("currPw") @Nullable String currPw
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 비밀번호 변경 처리
            isSuccess = userMyService.myPwChg(AuthUtils.getLgnUserId(), currPw, newPw);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }
        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }
}
