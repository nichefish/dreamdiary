package io.nicheblog.dreamdiary.global.cmm.log.handler;

import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogAnonActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogSysEvent;
import io.nicheblog.dreamdiary.global.cmm.log.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * LogWorker
 * <pre>
 *  로그 처리 Worker :: Runnable 구현 (Queue 처리)
 * </pre>
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class LogWorker
        implements Runnable {

    private final LogService logService;

    /** 로그 queue */
    private static final BlockingQueue<Object> logQueue = new LinkedBlockingQueue<>();

    @PostConstruct
        public void init() {
        Thread workerThread = new Thread(this);
        workerThread.start();
    }

    /** 
     * 로깅 수행 로직
     */
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
            log.warn("log regist failed", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.warn("log regist failed", e);
        }
    }

    public void offer(Object o) {
        logQueue.offer(o);
    }
}
