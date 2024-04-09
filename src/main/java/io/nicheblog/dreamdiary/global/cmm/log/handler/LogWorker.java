package io.nicheblog.dreamdiary.global.cmm.log.handler;

import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogAnonActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogSysEvent;
import io.nicheblog.dreamdiary.global.cmm.log.service.LogService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
@Log4j2
public class LogWorker
        implements Runnable {

    @Resource(name = "logService")
    private LogService logService;

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
        boolean isSuccess = false;
        try {
            while (true) {
                // Blocks until an element is available
                Object logEvent = logQueue.take();
                log.info("after taking logQueue: {}", logQueue);

                if (logEvent instanceof LogActvtyEvent) {
                    // 활동 로그 (로그인) 로깅 처리

                    isSuccess = logService.regLogActvty(((LogActvtyEvent) logEvent).getLog());
                } else if (logEvent instanceof LogAnonActvtyEvent) {
                    // 활동 로그 (비로그인) 로깅 처리
                    isSuccess = logService.regLogAnonActvty(((LogAnonActvtyEvent) logEvent).getLog());
                } else if (logEvent instanceof LogSysEvent) {
                    // 시스템 로그 로깅 처리
                    isSuccess = logService.regSysActvty(((LogSysEvent) logEvent).getLog());
                }
            }
        } catch (Exception e) {
            log.warn("log regist failed with {}", (Object) e.getStackTrace());
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            log.info("logWorker isSuccess: {}", isSuccess);
        }
    }

    public void offer(Object o) {
        log.info("logQueue: {}", logQueue);
        Boolean isSuccess = logQueue.offer(o);
        log.info("logging offer result: {}", isSuccess);
    }
}
