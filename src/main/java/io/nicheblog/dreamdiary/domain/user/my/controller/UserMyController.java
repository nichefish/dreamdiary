package io.nicheblog.dreamdiary.domain.user.my.controller;

import io.nicheblog.dreamdiary.domain._core.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.domain._core.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.domain._core.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.domain._core.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.domain.user.info.model.UserDto;
import io.nicheblog.dreamdiary.domain.user.info.service.UserService;
import io.nicheblog.dreamdiary.domain.user.my.service.UserMyService;
import io.nicheblog.dreamdiary.domain.vcatn.papr.model.VcatnPaprDto;
import io.nicheblog.dreamdiary.domain.vcatn.papr.service.VcatnPaprService;
import io.nicheblog.dreamdiary.domain.vcatn.stats.model.VcatnStatsYyDto;
import io.nicheblog.dreamdiary.domain.vcatn.stats.service.VcatnStatsService;
import io.nicheblog.dreamdiary.domain.vcatn.stats.service.VcatnStatsYyService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.SiteMenu;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserMyController
 * <pre>
 *  내 정보 관리 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class UserMyController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.USER_MY_DTL;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.USER_MY;     // 작업 카테고리 (로그 적재용)

    private final UserService userService;
    private final UserMyService userMyService;
    private final VcatnStatsService vcatnStatsService;
    private final VcatnStatsYyService vcatnStatsYyService;
    private final VcatnPaprService vcatnPaprService;

    /**
     * 내 정보 (상세) 화면 조회
     * (사용자USER, 관리자MNGR만 접근 가능.)
     * 
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param model 뷰에 데이터를 전달하기 위한 ModelMap 객체
     * @return {@link String} -- 화면 뷰 경로
     * @throws Exception 처리 중 발생할 수 있는 예외
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
            final String lgnUserId = AuthUtils.getLgnUserId();
            final UserDto lgnUserDto = userService.getDtlDto(lgnUserId);
            model.addAttribute("user", lgnUserDto);

            // 휴가계획서 년도 정보 조회 (시작일자~종료일자)
            try {
                //if (AuthService.hasEcnyDt()) {
                final VcatnStatsYyDto statsYy = vcatnStatsYyService.getCurrVcatnYyDt();
                model.addAttribute("vcatnYy", statsYy);
                final String userId = AuthUtils.getLgnUserId();
                // VcatnStatsDto vcatnStatsDtl = vcatnStatsService.getVcatnStatsDtl(statsYy, userId);
               //  model.addAttribute("vcatnStats", vcatnStatsDtl);
                // 올해 사용 휴가 목록 조회
                final BaseSearchParam param = BaseSearchParam.builder()
                        .regstrId(lgnUserId)
                        .searchStartDt(statsYy.getBgnDt())
                        .searchEndDt(statsYy.getEndDt())
                        .build();
                final List<VcatnPaprDto.LIST> vcatnPaprList = vcatnPaprService.getListDto(param);
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
     * 프로필 이미지 등록 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     * 
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param request - Multipart 요청
     */
    @PostMapping(Url.USER_MY_UPLOAD_PROFL_IMG_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> uploadProflImgAjax(
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

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

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 프로필 이미지 제거 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     * 
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(Url.USER_MY_REMOVE_PROFL_IMG_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> removeProflImgAjax(
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 이미지 파일 삭제
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

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 내 비밀번호 확인 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param currPw 현재 비밀번호
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(Url.USER_MY_PW_CF_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> myPwChkAjax(
            final @RequestParam("currPw") @Nullable String currPw,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 확인 처리
            final String lgnUserId = AuthUtils.getLgnUserId();
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

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 내 비밀번호 변경 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param currPw 현재 비밀번호
     * @param newPw 새 비밀번호
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(Url.USER_MY_PW_CHG_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> myPwChgAjax(
            final @RequestParam("currPw") @Nullable String currPw,
            final @RequestParam("newPw") @Nullable String newPw,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

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

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }
}
