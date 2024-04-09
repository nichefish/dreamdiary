package io.nicheblog.dreamdiary.global.cmm.log.handler;

import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogAnonActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogSysEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * LogEventListener
 * <pre>
 *  로그 이벤트 처리 핸들러
 *  내부에 LogWorkerRunnable 구현 (Queue 처리)
 * </pre>
 *
 * @author nichefish
 */
@Component
public class LogEventListener {

    @Resource
    private HttpServletRequest request;
    @Resource(name = "logWorker")
    private LogWorker logWorker;

    /**
     * 현재 인증(로그인) 상태인 등록/수정자 반환
     */
    @EventListener
    @Async
    public void handleLogActvtyEvent(LogActvtyEvent event) {
        // 큐에 전달하기 전에 request 관련 속성들을 미리 바인딩해야 한다. (권장)
        logWorker.offer(event);
    }

    /**
     * 현재 인증(로그인) 상태인 등록/수정자 반환
     */
    @EventListener
    @Async
    public void handleLogAnonActvtyEvent(LogAnonActvtyEvent event) {
        // 큐에 전달하기 전에 request 관련 속성들을 미리 바인딩해야 한다. (권장)
        logWorker.offer(event);
    }

    /**
     * 현재 인증(로그인) 상태인 등록/수정자 반환
     */
    @EventListener
    @Async
    public void handleLogSysEvent(LogSysEvent event) {
        // 큐에 전달하기 전에 request 관련 속성들을 미리 바인딩해야 한다. (권장)
        logWorker.offer(event);
    }
}
