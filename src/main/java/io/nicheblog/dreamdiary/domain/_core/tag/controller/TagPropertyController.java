package io.nicheblog.dreamdiary.domain._core.tag.controller;

import io.nicheblog.dreamdiary.domain._core.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.domain._core.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.domain._core.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.domain._core.tag.model.TagPropertyDto;
import io.nicheblog.dreamdiary.domain._core.tag.service.TagPropertyService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * TagPropertyController
 * <pre>
 *  태그 속성 관리 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class TagPropertyController
        extends BaseControllerImpl {

    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.TAG;           // 작업 카테고리 (로그 적재용)

    private final TagPropertyService tagPropertyService;

    /**
     * 태그 속성 등록/수정 처리 (Ajax)
     * (관리자MNGR만 접근 가능.)
     * @param tagProperty - 등록 또는 수정할 태그 속성 정보를 담은 DTO 객체
     * @param logParam - 로그 활동 기록을 위한 파라미터 객체
     * @param tagNo - 수정하려는 태그 번호
     * @return ResponseEntity<AjaxResponse> - 처리 결과와 메시지
     */
    @GetMapping(value = {Url.TAG_PROPERTY_REG_AJAX, Url.TAG_PROPERTY_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> TagPropertyRegAjax(
            final @Valid TagPropertyDto tagProperty,
            final LogActvtyParam logParam,
            final @RequestParam("tagNo") Integer tagNo
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 태그 상세 조회 (관련글 목록 포함)
            TagPropertyDto rsDto = tagPropertyService.regist(tagProperty);
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
            logParam.setCn("key: " + tagNo);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 태그 속성 상세 조회 (Ajax)
     * (관리자MNGR만 접근 가능.)
     * @param logParam - 로그 활동 기록을 위한 파라미터 객체
     * @param tagNo - 조회하려는 태그 번호
     * @return ResponseEntity<AjaxResponse> - 처리 결과와 메시지
     */
    @GetMapping(Url.TAG_PROPERTY_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> TagDtlAjax(
            final LogActvtyParam logParam,
            final @RequestParam("tagNo") Integer tagNo
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 태그 속성 상세 조회 (관련글 목록 포함)
            TagPropertyDto tagDto = tagPropertyService.getDtlDto(tagNo);
            ajaxResponse.setRsltObj(tagDto);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + tagNo);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

    /**
     * 태그 속성 삭제 처리 (Ajax)
     * (관리자MNGR만 접근 가능.)
     * @param logParam - 로그 활동 기록을 위한 파라미터 객체
     * @param tagNo - 삭제하려는 태그 번호
     * @return ResponseEntity<AjaxResponse> - 처리 결과와 메시지
     */
    @GetMapping(Url.TAG_PROPERTY_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> TagPropertyDelAjax(
            final LogActvtyParam logParam,
            final @RequestParam("tagNo") Integer tagNo
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 태그 상세 조회 (관련글 목록 포함)
            TagPropertyDto tagDto = tagPropertyService.getDtlDto(tagNo);
            ajaxResponse.setRsltObj(tagDto);

            isSuccess = true;
            rsltMsg = MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            ajaxResponse.setAjaxResult(isSuccess, rsltMsg);
            // 로그 관련 처리
            logParam.setCn("key: " + tagNo);
            logParam.setResult(isSuccess, rsltMsg, actvtyCtgr);
            publisher.publishEvent(new LogActvtyEvent(this, logParam));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ajaxResponse);
    }

}
