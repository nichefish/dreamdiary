package io.nicheblog.dreamdiary.infrastructure.web.handler;

import io.nicheblog.dreamdiary.auth.security.exception.NotAuthorizedException;
import io.nicheblog.dreamdiary.global.handler.ApplicationEventPublisherWrapper;
import io.nicheblog.dreamdiary.global.exception.BaseException;
import io.nicheblog.dreamdiary.global.exception.BusinessException;
import io.nicheblog.dreamdiary.global.exception.DuplicateException;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.infrastructure.log.event.LogEvent;
import io.nicheblog.dreamdiary.infrastructure.log.handler.LogEventListener;
import io.nicheblog.dreamdiary.infrastructure.log.model.LogParam;
import io.nicheblog.dreamdiary.infrastructure.web.model.AjaxResponse;
import io.nicheblog.dreamdiary.infrastructure.web.util.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * BaseExceptionlHandler
 * <pre>
 *  Controller에서 catch되지 않는 exception 공통 처리 클래스.
 *  (각 exception별로 처리 메소드 생성 가능)
 *  "컨트롤러 메소드의 실행이 완료된 후 뷰를 렌더링하는 과정에서 발생하는 예외는 @ExceptionHandler로 처리되지 않습니다."
 * </pre>
 *
 * @author nichefish
 */
@ControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class BaseExceptionHandler {

    private final ApplicationEventPublisherWrapper publisher;

    /** 로그에 원문 값을 남기지 않을 민감 필드명 조각 (소문자 비교). */
    private static final List<String> SENSITIVE_FIELD_TOKENS = List.of("password", "passwd", "pw", "secret", "token", "credential", "authorization");
    /** 로그에 남길 거절 값 최대 길이 */
    private static final int REJECTED_VALUE_MAX_LENGTH = 50;
    /** 사용자 응답 메시지에 이어 붙일 최대 검증 메시지 수 */
    private static final int RESPONSE_MAX_MESSAGES = 3;
    /** 활동 로그 exception_message 에 남길 최대 길이 */
    private static final int LOG_DETAIL_MAX_LENGTH = 4000;

    /**
     * 예외 처리 공통 로직
     * Ajax 요청과 페이지뷰 요청을 구분하여 응답을 내려준다.
     *
     * @param e 처리할 예외
     * @param request 요청 정보를 포함한 WebRequest 객체
     * @param status 반환할 HTTP 상태 코드
     * @return Ajax 요청의 경우 {@link ResponseEntity}, 페이지 요청의 경우 {@link ModelAndView} 객체
     */
    private Object handleException(final Exception e, final WebRequest request, final HttpStatus status) {
        return handleException(e, request, status, "general");
    }

    /**
     * 예외 처리 공통 로직
     * Ajax 요청과 페이지뷰 요청을 구분하여 응답을 내려준다.
     *
     * @param e 처리할 예외
     * @param request 발생한 웹 요청 정보
     * @param status 반환할 HTTP 상태 코드
     * @param errorType Vue 에러 페이지에서 사용할 에러 타입
     * @return Ajax 요청의 경우 {@link ResponseEntity}, 페이지 요청의 경우 {@link ModelAndView} 객체
     * @see LogEventListener
     */
    private Object handleException(final Exception e, final WebRequest request, final HttpStatus status, final String errorType) {
        return handleException(e, request, status, errorType, MessageUtils.getExceptionMsg(e));
    }

    /**
     * 예외 상세 로그와 사용자 응답 메시지를 분리하여 처리한다.
     *
     * @param e 처리할 예외
     * @param request 발생한 웹 요청 정보
     * @param status 반환할 HTTP 상태 코드
     * @param errorType Vue 에러 페이지에서 사용할 에러 타입
     * @param responseMsg 사용자 응답에 포함할 안전한 메시지
     * @return Ajax 요청의 경우 {@link ResponseEntity}, 페이지 요청의 경우 {@link ModelAndView} 객체
     */
    private Object handleException(
            final Exception e,
            final WebRequest request,
            final HttpStatus status,
            final String errorType,
            final String responseMsg
    ) {
        final String errorMsg = MessageUtils.getExceptionMsg(e);
        if (isExpectedException(e)) {
            log.warn(
                    "EXCEPTION_HANDLED type={} status={} message={}",
                    e.getClass().getSimpleName(),
                    status.value(),
                    errorMsg
            );
        } else {
            log.warn(
                    "EXCEPTION_HANDLED type={} status={} message={}",
                    e.getClass().getSimpleName(),
                    status.value(),
                    errorMsg,
                    e
            );
        }

        // 로그 처리
        final LogParam logParam = new LogParam(false, errorMsg);
        publisher.publishAsyncEvent(new LogEvent(this, logParam));

        return buildResponse(request, status, errorType, responseMsg, null);
    }

    /**
     * Ajax 요청과 페이지뷰 요청을 구분하여 오류 응답을 만든다.
     *
     * @param request 발생한 웹 요청 정보
     * @param status 반환할 HTTP 상태 코드
     * @param errorType Vue 에러 페이지에서 사용할 에러 타입
     * @param responseMsg 사용자 응답에 포함할 안전한 메시지
     * @param rsltList Ajax 응답 rsltList 에 담을 부가 정보 (예: 필드 오류 목록, 없으면 null)
     * @return Ajax 요청의 경우 {@link ResponseEntity}, 페이지 요청의 경우 {@link ModelAndView} 객체
     */
    private Object buildResponse(
            final WebRequest request,
            final HttpStatus status,
            final String errorType,
            final String responseMsg,
            final List<?> rsltList
    ) {
        // Ajax 요청인 경우
        if (HttpUtils.isAjaxRequest(request)) {
            final AjaxResponse ajaxResponse = new AjaxResponse(false, responseMsg);
            ajaxResponse.setStatus(status.value());
            if (rsltList != null && !rsltList.isEmpty()) ajaxResponse.withList(rsltList);
            return ResponseEntity
                    .status(status)
                    .body(ajaxResponse);
        }
        // 페이지 요청인 경우
        final String redirectUrl = UriComponentsBuilder
                .fromPath("/vue-app/error")
                .queryParam("type", errorType)
                .queryParam("message", responseMsg)
                .build()
                .encode()
                .toUriString();
        return new ModelAndView("redirect:" + redirectUrl);
    }

    /**
     * 검증 오류 하나의 사용자 메시지를 해석한다.
     * 오류 코드(예: {@code typeMismatch}) → 기본 메시지(이미 보간된 문구, {@code {key}} placeholder, 또는 메시지 키) 순으로 찾고,
     * 해석되지 않은 {@code {...}} placeholder 는 사용자에게 노출하지 않는다.
     *
     * @param error 검증 오류
     * @return 해석된 메시지, 없으면 null
     */
    private String resolveErrorMessage(final ObjectError error) {
        final String[] codes = error.getCodes();
        if (codes != null) {
            for (final String code : codes) {
                final String resolved = MessageUtils.getMessage(code);
                if (StringUtils.isNotBlank(resolved) && !resolved.equals(code)) return resolved;
            }
        }
        final String defaultMessage = StringUtils.trimToNull(error.getDefaultMessage());
        if (defaultMessage == null) return null;
        final boolean isPlaceholder = defaultMessage.startsWith("{") && defaultMessage.endsWith("}");
        final String key = isPlaceholder ? defaultMessage.substring(1, defaultMessage.length() - 1) : defaultMessage;
        final String resolved = MessageUtils.getMessage(key);
        if (StringUtils.isNotBlank(resolved) && !resolved.equals(key)) return resolved;
        return isPlaceholder ? null : defaultMessage;
    }

    /**
     * 로그용 거절 값 문자열. 민감 필드는 마스킹하고 길이를 제한한다.
     *
     * @param fieldError 필드 오류
     * @return 로그에 남길 값
     */
    private String formatRejectedValue(final FieldError fieldError) {
        if (isSensitiveField(fieldError.getField())) return "****";
        final Object value = fieldError.getRejectedValue();
        if (value == null) return "null";
        final String str = value instanceof Object[] array ? Arrays.toString(array) : String.valueOf(value);
        final String oneLine = str.replaceAll("[\\r\\n\\t]", " ");
        return "\"" + StringUtils.abbreviate(oneLine, REJECTED_VALUE_MAX_LENGTH) + "\"";
    }

    /**
     * 비밀번호 등 로그에 원문을 남기면 안 되는 필드인지 판단한다.
     *
     * @param field 필드 경로 (예: {@code newPw}, {@code user.password})
     * @return 민감 필드 여부
     */
    private boolean isSensitiveField(final String field) {
        if (field == null) return false;
        final String lower = field.toLowerCase(Locale.ROOT);
        return SENSITIVE_FIELD_TOKENS.stream().anyMatch(lower::contains);
    }

    private boolean isExpectedException(final Exception e) {
        return e instanceof BaseException
                || e instanceof BindException
                || e instanceof AccessDeniedException
                || e instanceof NotAuthorizedException
                || e instanceof NoHandlerFoundException;
    }

    /**
     * {@link NotAuthorizedException} — 리소스 소유·조회 권한 부재 시 처리 (Ajax 403).
     *
     * @param e 처리할 {@link NotAuthorizedException}
     * @param request 발생한 웹 요청 정보
     * @return Ajax 요청의 경우 {@link ResponseEntity}, 페이지 요청의 경우 {@link ModelAndView} 객체
     */
    @ExceptionHandler(NotAuthorizedException.class)
    public Object handleNotAuthorizedException(
            final NotAuthorizedException e,
            final WebRequest request
    ) {
        return handleException(e, request, HttpStatus.FORBIDDEN, "access_denied");
    }

    /**
     * {@link NoHandlerFoundException} - 요청한 핸들러를 찾을 수 없을 때 발생하는 예외를 처리합니다.
     *
     * @param e 처리할 {@link NoHandlerFoundException}
     * @param request 발생한 웹 요청 정보
     * @return Ajax 요청의 경우 {@link ResponseEntity}, 페이지 요청의 경우 {@link ModelAndView} 객체
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNoHandlerFoundException(
            final NoHandlerFoundException e,
            final WebRequest request
    ) {
        return handleException(e, request, HttpStatus.NOT_FOUND, "not_found");
    }

    /**
     * {@link AccessDeniedException} - 요청한 리소스에 접근할 수 없을 때 발생하는 예외를 처리합니다.
     *
     * @param e 처리할 {@link AccessDeniedException}
     * @param request 발생한 웹 요청 정보
     * @return Ajax 요청의 경우 {@link ResponseEntity}, 페이지 요청의 경우 {@link ModelAndView} 객체
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Object accessDenied(
            final AccessDeniedException e,
            final WebRequest request
    ) {
        return handleException(e, request, HttpStatus.FORBIDDEN, "access_denied");
    }

    /**
     * {@link BindException} - Spring Validation을 통과하지 못했을 때 발생하는 예외를 처리합니다.
     * ({@code @RequestBody} 검증 실패인 {@link org.springframework.web.bind.MethodArgumentNotValidException} 도 하위 타입이라 여기서 처리된다.)
     * 필드별 메시지를 해석해 응답 message·rsltList({@code field}, {@code message})와 활동 로그에 담고,
     * WARN 로그에는 필드·거절 값(민감 필드 마스킹, 길이 제한)·코드를 남긴다.
     * 변경 전: 응답에 {@code {msg...}} placeholder 원문, 활동 로그에는 공통 문구만 남아 원인 필드를 알 수 없었다.
     *
     * @param e 처리할 {@link BindException}
     * @param request 발생한 웹 요청 정보
     * @return Ajax 요청의 경우 {@link ResponseEntity}, 페이지 요청의 경우 {@link ModelAndView} 객체
     */
    @ExceptionHandler(BindException.class)
    public Object handleBingdingException(
            final BindException e,
            final WebRequest request
    ) {
        final List<Map<String, String>> fieldErrors = new ArrayList<>();
        final List<String> logDetails = new ArrayList<>();
        final Set<String> messages = new LinkedHashSet<>();
        for (final ObjectError error : e.getBindingResult().getAllErrors()) {
            final String message = resolveErrorMessage(error);
            if (StringUtils.isNotBlank(message)) messages.add(message);
            if (error instanceof FieldError fieldError) {
                final Map<String, String> item = new LinkedHashMap<>();
                item.put("field", fieldError.getField());
                item.put("message", message);
                fieldErrors.add(item);
                logDetails.add(String.format("field=%s rejected=%s code=%s message=%s",
                        fieldError.getField(), formatRejectedValue(fieldError), fieldError.getCode(), message));
            } else {
                logDetails.add(String.format("object=%s code=%s message=%s", error.getObjectName(), error.getCode(), message));
            }
        }

        // 사용자 응답: 해석된 검증 메시지 (없으면 exception.bind 공통 문구)
        final String responseMsg;
        if (messages.isEmpty()) {
            final String fallback = MessageUtils.getMessage("exception.bind");
            responseMsg = StringUtils.isNotBlank(fallback) ? fallback : MessageUtils.getMessage("common.result.exception");
        } else {
            responseMsg = String.join(" / ", messages.stream().limit(RESPONSE_MAX_MESSAGES).toList());
        }
        final String logDetail = String.join("; ", logDetails);
        log.warn(
                "EXCEPTION_HANDLED type={} status={} object={} errorCount={} errors=[{}]",
                e.getClass().getSimpleName(),
                HttpStatus.BAD_REQUEST.value(),
                e.getObjectName(),
                e.getErrorCount(),
                logDetail
        );

        // 활동 로그: 공통 문구 대신 읽을 수 있는 메시지와 필드 상세를 남긴다.
        final LogParam logParam = new LogParam(false, responseMsg);
        logParam.setExceptionNm(MessageUtils.getExceptionNm(e));
        logParam.setExceptionMsg(StringUtils.abbreviate(logDetail, LOG_DETAIL_MAX_LENGTH));
        publisher.publishAsyncEvent(new LogEvent(this, logParam));

        return buildResponse(request, HttpStatus.BAD_REQUEST, "bad_request", responseMsg, fieldErrors);
    }

    /** 클라이언트 요청 값 또는 비즈니스 규칙 위반을 400으로 응답한다. */
    @ExceptionHandler({BusinessException.class, IllegalArgumentException.class})
    public Object handleBadRequestException(final Exception e, final WebRequest request) {
        return handleException(e, request, HttpStatus.BAD_REQUEST, "bad_request");
    }

    /** 존재하지 않는 리소스 조회·수정·삭제를 404로 응답한다. */
    @ExceptionHandler(EntityNotFoundException.class)
    public Object handleEntityNotFoundException(final EntityNotFoundException e, final WebRequest request) {
        return handleException(e, request, HttpStatus.NOT_FOUND, "not_found");
    }

    /** 중복 등록과 데이터 무결성 충돌을 409로 응답한다. */
    @ExceptionHandler({DuplicateException.class, DataIntegrityViolationException.class})
    public Object handleConflictException(final Exception e, final WebRequest request) {
        return handleException(e, request, HttpStatus.CONFLICT, "conflict");
    }

    /**
     * {@link Exception} - 공통 예외를 처리합니다.
     *
     * @param e 처리할 {@link Exception}
     * @param request 발생한 웹 요청 정보
     * @return Ajax 요청의 경우 {@link ResponseEntity}, 페이지 요청의 경우 {@link ModelAndView} 객체
     */
    @ExceptionHandler(Exception.class)
    public Object generalException(
            final Exception e,
            final WebRequest request
    ) {
        return handleException(
                e,
                request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "general",
                MessageUtils.getMessage("common.result.exception")
        );
    }
}
