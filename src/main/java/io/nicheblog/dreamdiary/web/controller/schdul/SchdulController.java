package io.nicheblog.dreamdiary.web.controller.schdul;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.intrfc.controller.impl.BaseControllerImpl;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.event.TagProcEvent;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import io.nicheblog.dreamdiary.web.model.schdul.SchdulDto;
import io.nicheblog.dreamdiary.web.service.schdul.SchdulService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.InvalidParameterException;

/**
 * SchdulController
 * <pre>
 *  일정 컨트롤러
 * </pre>
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class SchdulController
        extends BaseControllerImpl {

    @Getter
    private final String baseUrl = Url.SCHDUL_CAL;
    @Getter
    private final ActvtyCtgr actvtyCtgr = ActvtyCtgr.SCHDUL;      // 작업 카테고리 (로그 적재용)

    @Resource(name = "schdulService")
    private SchdulService schdulService;

    /**
     * 일정 > 전체일정 > 일정 등록(ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(value = {Url.SCHDUL_REG_AJAX, Url.SCHDUL_MDF_AJAX})
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> schdulRegAjax(
            final @Valid SchdulDto schdul,
            final LogActvtyParam logParam,
            final @RequestParam("postNo") @Nullable Integer postNo,
            final @RequestParam("jandiYn") @Nullable String jandiYn,
            final @RequestParam("trgetTopic") @Nullable String trgetTopic,
            final BindingResult bindingResult
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // Validation
            if (bindingResult.hasErrors()) throw new InvalidParameterException();
            // 등록/수정 처리
            boolean isReg = postNo == null;
            SchdulDto result = isReg ? schdulService.regist(schdul) : schdulService.modify(schdul);
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

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 일정 > 전체일정 > 일정 조회 (ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(Url.SCHDUL_DTL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> schdulDtlAjax(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 객체 조회 및 응답에 추가
            SchdulDto rsDto = schdulService.getDtlDto(key);
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

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 일정 > 전체일정 > 일정 삭제 (ajax)
     * (사용자USER, 관리자MNGR만 접근 가능)
     */
    @PostMapping(Url.SCHDUL_DEL_AJAX)
    @Secured({Constant.ROLE_USER, Constant.ROLE_MNGR})
    @ResponseBody
    public ResponseEntity<AjaxResponse> schdulDelAjax(
            final LogActvtyParam logParam,
            final @RequestParam("postNo") Integer key
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();

        boolean isSuccess = false;
        String rsltMsg = "";
        try {
            // 삭제 처리
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

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }
}
