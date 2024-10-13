package io.nicheblog.dreamdiary.domain.jrnl.sbjct.controller;

import io.nicheblog.dreamdiary.domain.jrnl.sbjct.model.JrnlSbjctDto;
import io.nicheblog.dreamdiary.domain.jrnl.sbjct.service.JrnlSbjctService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global._common._clsf.ContentType;
import io.nicheblog.dreamdiary.global._common._clsf.managt.event.ManagtrAddEvent;
import io.nicheblog.dreamdiary.global._common._clsf.tag.event.TagProcEvent;
import io.nicheblog.dreamdiary.global._common._clsf.viewer.event.ViewerAddEvent;
import io.nicheblog.dreamdiary.global._common.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.global._common.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global._common.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.model.AjaxResponse;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Nullable;
import javax.validation.Valid;

/**
 * JrnlSbjctApiController
 * <pre>
 *  저널 주제 API 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@RestController
@RequiredArgsConstructor
@Log4j2
public class JrnlSbjctApiController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.JRNL_SBJCT_LIST;             // 기본 URL
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.NOTICE;      // 작업 카테고리 (로그 적재용)

    private final JrnlSbjctService jrnlSbjctService;

    /**
     * 저널 주제 등록/수정 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param jrnlSbjct 등록/수정 처리할 객체
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @param request - Multipart 요청
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(value = {Url.JRNL_SBJCT_REG_AJAX, Url.JRNL_SBJCT_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlSbjctRegAjax(
            final @Valid JrnlSbjctDto.DTL jrnlSbjct,
            final @RequestParam("postNo") @Nullable Integer key,
            final LogActvtyParam logParam,
            final MultipartHttpServletRequest request
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 등록/수정 처리
            final boolean isReg = (key == null);
            final JrnlSbjctDto result = isReg ? jrnlSbjctService.regist(jrnlSbjct, request) : jrnlSbjctService.modify(jrnlSbjct, request);
            ajaxResponse.setRsltObj(result);

            isSuccess = (result.getPostNo() != null);
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
            if (isSuccess) {
                // 태그 처리 :: 메인 로직과 분리
                publisher.publishEvent(new TagProcEvent(this, result.getClsfKey(), jrnlSbjct.tag));
                // 조치자 추가 :: 메인 로직과 분리
                publisher.publishEvent(new ManagtrAddEvent(this, result.getClsfKey()));
                // 잔디 메세지 발송 :: 메인 로직과 분리
                // if ("Y".equals(jandiYn)) {
                //     String jandiRsltMsg = notifyService.notifyJrnlSbjctReg(trgetTopic, result, logParam);
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
            logParam.setCn(jrnlSbjct.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 저널 주제 상세 조회 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @GetMapping(Url.JRNL_SBJCT_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlSbjctDtlAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 응답에 추가
            final JrnlSbjctDto rsDto = jrnlSbjctService.getDtlDto(key);
            ajaxResponse.setRsltObj(rsDto);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            // 조회수 카운트 추가
            jrnlSbjctService.hitCntUp(key);
            // 열람자 추가 :: 메인 로직과 분리
            publisher.publishEvent(new ViewerAddEvent(this, rsDto.getClsfKey()));
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 저널 주제 삭제 (Ajax)
     * (사용자USER, 관리자MNGR만 접근 가능.)
     *
     * @param key 식별자
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link ResponseEntity} -- 처리 결과와 메시지
     */
    @PostMapping(Url.JRNL_SBJCT_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> jrnlSbjctDelAjax(
            final @RequestParam("postNo") Integer key,
            final LogActvtyParam logParam
    ) {

        final AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제
            isSuccess = jrnlSbjctService.delete(key);
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
            if (isSuccess) {
                // 태그 처리 :: 메인 로직과 분리
                publisher.publishEvent(new TagProcEvent(this, new BaseClsfKey(key, ContentType.JRNL_SBJCT)));
            }
        } catch (Exception e) {
            logParam.setExceptionInfo(e);

            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + key.toString());
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }
}