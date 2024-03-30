package io.nicheblog.dreamdiary.global.cmm.log.handler;

import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogAnonActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogSysEvent;
import io.nicheblog.dreamdiary.global.cmm.log.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * LogActvtyEventListener
 * <pre>
 *  로그 이벤트 처리 핸들러
 *  내부에 LogWorkerRunnable 구현 (Queue 처리)
 * </pre>
 *
 * @author nichefish
 */
@Component("logEventListener")
public class LogEventListener {

    @Resource
    private LogService logService;

    private final BlockingQueue<Object> logQueue = new LinkedBlockingQueue<>();

    public LogEventListener() {
        LogWorker logWorker = new LogWorker(logQueue, logService);
        Thread workerThread = new Thread(logWorker);
        workerThread.start();
    }

    /**
     * 현재 인증(로그인) 상태인 등록/수정자 반환
     */
    @EventListener
    public void handleLogActvtyEvent(LogActvtyEvent logActvtyEvent) {
        logQueue.offer(logActvtyEvent);
    }

    /**
     * 현재 인증(로그인) 상태인 등록/수정자 반환
     */
    @EventListener
    public void handleLogAnonActvtyEvent(LogAnonActvtyEvent logAnonActvtyEvent) {
        logQueue.offer(logAnonActvtyEvent);
    }

    /**
     * 현재 인증(로그인) 상태인 등록/수정자 반환
     */
    @EventListener
    public void handleLogSysEvent(LogSysEvent logSysEvent) {
        logQueue.offer(logSysEvent);
    }

    /**
     * LogWorker
     * 로그 처리 Worker
     */
    @RequiredArgsConstructor
    private static class LogWorker 
            implements Runnable {

        private final BlockingQueue<Object> logQueue;
        private final LogService logService;

        @Override
        public void run() {
            try {
                while (true) {
                    // Blocks until an element is available
                    Object logEvent = logQueue.take();

                    if (logEvent instanceof LogActvtyEvent) {
                        // 활동 로그 (로그인) 로깅 처리
                        logService.regLogActvty(((LogActvtyEvent) logEvent).getLog());
                    } else if (logEvent instanceof LogAnonActvtyEvent) {
                        // 활동 로그 (비로그인) 로깅 처리
                        logService.regLogAnonActvty(((LogAnonActvtyEvent) logEvent).getLog());
                    } else if (logEvent instanceof LogSysEvent) {
                        // 시스템 로그 로깅 처리
                        logService.regSysActvty(((LogSysEvent) logEvent).getLog());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
