package io.nicheblog.dreamdiary.global.handler;

import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.annotation.Resource;

/**
 * GlobalExceptionlHandler
 * <pre>
 *  Controller에서 catch되지 않는 exception 공통 처리 클래스
 *  (각 exception별로 처리 메소드 생성 가능)
 *  "컨트롤러 메소드의 실행이 완료된 후 뷰를 렌더링하는 과정에서 발생하는 예외는 @ExceptionHandler로 처리되지 않습니다."
 * </pre>
 *
 * @author nichefish
 */
@ControllerAdvice
@Log4j2
public class BaseExceptionlHandler {

    @Resource
    protected ApplicationEventPublisher publisher;

    /**
     * 요청이 AJAX 요청인지 확인
     */
    private boolean isAjaxRequest(WebRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    /**
     * 예외 처리 공통 로직
     * Ajax 요청과 페이지뷰 요청을 구분하여 응답을 내려준다.
     */
    private Object handleException(Exception e, WebRequest request, HttpStatus status) {
        return handleException(e, request, status, "/view/error/error_page");
    }
    private Object handleException(Exception e, WebRequest request, HttpStatus status, String view) {
        String errorMsg = MessageUtils.getExceptionMsg(e);
        log.warn("Exception handled: ", e);

        // 로그 처리
        LogActvtyParam logParam = new LogActvtyParam(false, errorMsg);
        publisher.publishEvent(new LogActvtyEvent(this, logParam));

        if (this.isAjaxRequest(request)) {
            AjaxResponse ajaxResponse = new AjaxResponse(false, errorMsg);
            return new ResponseEntity<>(ajaxResponse, status);
        }
        ModelAndView modelAndView = new ModelAndView(view);
        modelAndView.addObject("errorMsg", errorMsg);
        return modelAndView;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNoHandlerFoundException(
            NoHandlerFoundException e,
            WebRequest request
    ) {

        return handleException(e, request, HttpStatus.NOT_FOUND, "/view/error/error_not_found");
    }

    /**
     * 권한 관련 (접근불가) 처리
     * 요청에 따라 responseEntity 또는 페이지뷰 반환
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Object accessDenied(
            AccessDeniedException e,
            WebRequest request
    ) {

        return handleException(e, request, HttpStatus.FORBIDDEN, "/view/error/error_access_denied");
    }

    /**
     * 공통 Exception 처리
     * 요청에 따라 responseEntity 또는 페이지뷰 반환
     */
    @ExceptionHandler(Exception.class)
    public Object generalException(
            Exception e,
            WebRequest request
    ) {

        return handleException(e, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
