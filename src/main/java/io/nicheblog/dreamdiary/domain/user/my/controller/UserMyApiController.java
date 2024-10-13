package io.nicheblog.dreamdiary.domain.user.my.controller;

import io.nicheblog.dreamdiary.domain.user.info.service.UserService;
import io.nicheblog.dreamdiary.domain.user.my.service.UserMyService;
import io.nicheblog.dreamdiary.domain.vcatn.papr.service.VcatnPaprService;
import io.nicheblog.dreamdiary.domain.vcatn.stats.service.VcatnStatsService;
import io.nicheblog.dreamdiary.domain.vcatn.stats.service.VcatnStatsYyService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global._common.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global._common.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.global._common.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global._common.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Nullable;

/**
 * UserMyApiController
 * <pre>
 *  내 정보 관리 API 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@RestController
@RequiredArgsConstructor
@Log4j2
public class UserMyApiController
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
