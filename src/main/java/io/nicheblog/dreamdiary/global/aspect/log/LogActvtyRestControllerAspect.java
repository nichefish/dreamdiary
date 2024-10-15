package io.nicheblog.dreamdiary.global.aspect.log;

import io.nicheblog.dreamdiary.global._common.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.global._common.log.actvty.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global._common.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * LogActvtyRestControllerAspect
 * <pre>
 *  RestController에서의 로그 공통 처리 Aspect.
 *  TODO: 페이지 컨트롤러 / 서비스 로깅 추가하기.
 * </pre>
 *
 * @author nichefish
 */
@Aspect
@Component
@RequiredArgsConstructor
@Log4j2
public class LogActvtyRestControllerAspect {

    private final ApplicationEventPublisher publisher;

    /**
     * Pointcut :: RestController(API)를 대상으로 지정합니다.
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void apiControllerMethods() { }

    /**
     * RestController (API) 메소드 성공시 응답 객체를 기반으로 로그를 기록합니다.
     *
     * @param joinPoint 메소드 이름, 파라미터, 호출된 클래스, 타겟 객체 등의 메타 정보를 담은 객체
     * @param result 사용자에게 반환된 {@link ResponseEntity} 객체
     * TODO: 추가적인 인자들을 전달받아야 한다.
     */
    // @Around("apiControllerMethods()")
    @AfterReturning(pointcut = "apiControllerMethods()", returning = "result")
    public void logApiAfterReturning(JoinPoint joinPoint, Object result) {
        // API 요청에 대한 결과값 로그 남기기
        log.info("API Method {} completed. Result: {}", joinPoint.getSignature().getName(), result);

        final LogActvtyParam logParam = this.extractLogParam(joinPoint);

        publisher.publishEvent(new LogActvtyEvent(this, logParam));
    }

    /**
     * RestController (API) 메소드 실행 중 예외 발생시 에러를 기반으로 로그를 기록합니다.
     *
     * @param joinPoint 메소드 이름, 파라미터, 호출된 클래스, 타겟 객체 등의 메타 정보를 담은 객체
     * @param ex 익셉션 또는 에러
     * TODO: 추가적인 인자들을 전달받아야 한다.
     */
    @AfterThrowing(pointcut = "apiControllerMethods()", throwing = "ex")
    public void logApiAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("API Method {} threw an exception: {}", joinPoint.getSignature().getName(), ex.getMessage());

        LogActvtyParam logParam = extractLogParam(joinPoint);
        // 실패 여부, 에러 메시지, 활동 카테고리 설정
        logParam.setResult(false, MessageUtils.getExceptionMsg(ex), this.getActvtyCtgr(joinPoint.getTarget()));
        logParam.setExceptionInfo(ex);
        // logParam.setMethodName(joinPoint.getSignature().getName());

        // 로그 이벤트 발행
        publisher.publishEvent(new LogActvtyEvent(this, logParam));
    }

    /**
     * 컨트롤러 메소드에서 LogActvtyParam를 추출합니다.
     *
     * @param joinPoint 메소드 이름, 파라미터, 호출된 클래스, 타겟 객체 등의 메타 정보를 담은 객체
     */
    private LogActvtyParam extractLogParam(final JoinPoint joinPoint) {
        // JoinPoint에서 LogActvtyParam을 추출하는 로직
        final Object[] args = joinPoint.getArgs();
        for (final Object arg : args) {
            if (arg instanceof LogActvtyParam) return (LogActvtyParam) arg;
        }
        // TODO: 댓글, 단락 등에서 활동유형 세팅
        // ActvtyCtgr actvty = Optional.ofNullable(searchParam.getActvtyCtgrCd()).map(ActvtyCtgr::valueOf).orElse(null);
        return new LogActvtyParam(); // 기본값 반환 (필요에 따라 처리)
    }

    /**
     * 컨트롤러 필드로 선언된 ActvtyCtgr 값을 가져옵니다.
     *
     * @param target 컨트롤러 객체
     * @return {@link ActvtyCtgr} -- 컨트롤러 필드로 선언된 ActvtyCtgr 값
     */
    private ActvtyCtgr getActvtyCtgr(final Object target) {
        try {
            final Field field = target.getClass().getDeclaredField("actvtyCtgr");
            field.setAccessible(true);
            return (ActvtyCtgr) field.get(target);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.warn("Failed to get ActvtyCtgr from controller: {}", e.getMessage());
            return ActvtyCtgr.DEFAULT;  // 기본 값 반환
        }
    }
}
