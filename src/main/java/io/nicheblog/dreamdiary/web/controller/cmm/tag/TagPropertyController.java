package io.nicheblog.dreamdiary.web.controller.cmm.tag;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagPropertyDto;
import io.nicheblog.dreamdiary.web.service.cmm.tag.TagPropertyService;
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
 *  태그 속성 관리 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
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
     * 태그 속성 등록/수정 처리 (ajax)
     * (관리자MNGR만 접근 가능)
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
     * 태그 속성 상세 조회 (ajax)
     * (관리자MNGR만 접근 가능)
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
     * 태그 속성 삭제 처리 (ajax)
     * (관리자MNGR만 접근 가능)
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
