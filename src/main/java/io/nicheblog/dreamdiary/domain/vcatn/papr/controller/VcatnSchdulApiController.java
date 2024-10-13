package io.nicheblog.dreamdiary.domain.vcatn.papr.controller;

import io.nicheblog.dreamdiary.domain.vcatn.papr.model.VcatnSchdulDto;
import io.nicheblog.dreamdiary.domain.vcatn.papr.service.VcatnSchdulService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
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

import javax.validation.Valid;

/**
 * VcatnSchdulApiController
 * <pre>
 *  휴가관리 > 휴가사용일자 API 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@RestController
@RequiredArgsConstructor
@Log4j2
public class VcatnSchdulApiController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.VCATN_SCHDUL_LIST;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.VCATN_SCHDUL;      // 작업 카테고리 (로그 적재용)

    private final VcatnSchdulService vcatnSchdulService;

    /**
     * 휴가관리 > 휴가사용일자 > 휴가사용일자 등록/수정 (Ajax)
     * (관리자MNGR만 접근 가능.)
     *
     * @param vcatnSchdul 등록/수정할 객체
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link String} -- 뷰 이름을 나타내는 문자열
     */
    @PostMapping(value = {Url.VCATN_SCHDUL_REG_AJAX, Url.VCATN_SCHDUL_MDF_AJAX})
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> vcatnSchdulRegAjax(
            final @Valid VcatnSchdulDto vcatnSchdul,
            final Integer key,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 등록/수정 처리
            final boolean isReg = (key == null);
            final VcatnSchdulDto result = isReg ? vcatnSchdulService.regist(vcatnSchdul) : vcatnSchdulService.modify(vcatnSchdul);
            ajaxResponse.setRsltObj(result);

            isSuccess = (result.getVcatnSchdulNo() != null);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn(vcatnSchdul.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 휴가관리 > 휴가사용일자 > 휴가사용일자 단일 조회 (Ajax)
     * (관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link String} -- 뷰 이름을 나타내는 문자열
     */
    @PostMapping(value = Url.VCATN_SCHDUL_DTL_AJAX)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> vcatnSchdulDtlAjax(
            final @RequestParam("vcatnSchdulNo") Integer key,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 응답에 세팅
            final VcatnSchdulDto rsDto = vcatnSchdulService.getDtlDto(key);
            ajaxResponse.setRsltObj(rsDto);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
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
     * 휴가관리 > 휴가사용일자 > 휴가사용일자 삭제 (Ajax)
     * (관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link String} -- 뷰 이름을 나타내는 문자열
     */
    @PostMapping(Url.VCATN_SCHDUL_DEL_AJAX)
    @Secured(Constant.ROLE_MNGR)
    @ResponseBody
    public ResponseEntity<AjaxResponse> vcatnSchdulDelAjax(
            final @RequestParam("vcatnSchdulNo") Integer key,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제
            isSuccess = vcatnSchdulService.delete(key);
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
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