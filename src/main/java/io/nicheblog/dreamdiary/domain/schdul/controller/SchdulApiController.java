package io.nicheblog.dreamdiary.domain.schdul.controller;

import io.nicheblog.dreamdiary.adapter.jandi.model.JandiParam;
import io.nicheblog.dreamdiary.domain.schdul.model.SchdulDto;
import io.nicheblog.dreamdiary.domain.schdul.service.SchdulService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global._common._clsf.tag.event.TagProcEvent;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import javax.validation.Valid;

/**
 * SchdulController
 * <pre>
 *  일정 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class SchdulApiController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.SCHDUL_CAL;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.SCHDUL;      // 작업 카테고리 (로그 적재용)

    private final SchdulService schdulService;

    /**
     * 일정 > 전체일정 > 일정 등록 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param schdul 등록 처리할 객체
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param jandiParam 잔디 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(value = {Url.SCHDUL_REG_AJAX, Url.SCHDUL_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> schdulRegAjax(
            final @Valid SchdulDto schdul,
            final @RequestParam("postNo") @Nullable Integer key,
            final LogActvtyParam logParam,
            final JandiParam jandiParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 등록/수정 처리
            final boolean isReg = key == null;
            final SchdulDto result = isReg ? schdulService.regist(schdul) : schdulService.modify(schdul);
            ajaxResponse.setRsltObj(result);

            isSuccess = (result.getPostNo() != null);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
            if (isSuccess) {
                // 태그 처리 :: 메인 로직과 분리
                publisher.publishEvent(new TagProcEvent(this, result.getClsfKey(), schdul.tag));
                // 잔디 메세지 발송 :: 메인 로직과 분리
                // if (isSuccess && "Y".equals(jandiYn)) {
                //     String jandiRsltMsg = notifyService.notifySchdulReg(trgetTopic, result, logParam);
                //     rsltMsg = rsltMsg + "\n" + jandiRsltMsg;
                // }
            }
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn(schdul.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 일정 > 전체일정 > 일정 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     */
    @PostMapping(Url.SCHDUL_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> schdulDtlAjax(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 응답에 추가
            final SchdulDto rsDto = schdulService.getDtlDto(key);
            ajaxResponse.setRsltObj(rsDto);

            isSuccess = (rsDto.getPostNo() != null);
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 일정 > 전체일정 > 일정 삭제 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(Url.SCHDUL_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> schdulDelAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제
            isSuccess = schdulService.delete(key);
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }
}
