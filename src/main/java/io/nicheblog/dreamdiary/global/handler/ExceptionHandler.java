package io.nicheblog.dreamdiary.global.handler;

import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * CmmExceptionAdvise
 * <pre>
 *  Controller에서 catch되지 않는 exception 공통 처리 클래스
 *  (각 exception별로 처리 메소드 생성 가능)
 * </pre>
 *
 * @author nichefish
 */
@ControllerAdvice
@Log4j2
public class ExceptionHandler {

    /**
     * 권한 관련 (접근불가) 처리
     *
     * @return JSON message
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public ResponseEntity<AjaxResponse> accessDenied(
            final AccessDeniedException e
    ) {

        AjaxResponse ajaxResponse = new AjaxResponse();
        String resultMsg = MessageUtils.getExceptionMsg(e);
        ajaxResponse.setAjaxResult(false, resultMsg);

        return new ResponseEntity<>(ajaxResponse, HttpStatus.OK);
    }

    /**
     * 공통 Exception 처리
     *
     * @return errorPage
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public String exception(
            final Exception e,
            final Model model
    ) {

        String resultMsg = MessageUtils.getExceptionMsg(e);
        model.addAttribute("errorMsg", resultMsg);

        return "/view/error_page";
    }
}
