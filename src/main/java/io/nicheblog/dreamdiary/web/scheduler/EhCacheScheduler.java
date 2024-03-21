package io.nicheblog.dreamdiary.web.scheduler;

import io.nicheblog.dreamdiary.cmm.log.event.LogSysEvent;
import io.nicheblog.dreamdiary.cmm.log.model.LogSysParam;
import io.nicheblog.dreamdiary.cmm.util.EhCacheUtil;
import io.nicheblog.dreamdiary.cmm.util.MessageUtil;
import io.nicheblog.dreamdiary.cmm.log.ActvtyCtgr;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * EhCacheClearScheduler
 * 캐시 클리어 Scheduler
 *
 * @author nichefish
 */
@Component("ehCacheClearScheduler")
@Log4j2
public class EhCacheScheduler {

    @Resource
    private ApplicationEventPublisher publisher;

    /**
     * 1시간에 한 번씩 전체 캐시 클리어
     * 매시간 00분 실행
     */
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")         // second min hour day month weekday
    public void cacheAllClearSchedule(
            final LogSysParam logParam
        ) {

        log.info("cacheAllClearSchedule...");

        try {
            EhCacheUtil.clearAllCaches();
        } catch (Exception e) {
            String resultMsg = MessageUtil.getExceptionMsg(e);
            // 수시로 이루어지므로 실패시에만 로깅한다.
            logParam.setExceptionInfo(MessageUtil.getExceptionNm(e), e.getMessage());
            logParam.setResult(false, resultMsg, ActvtyCtgr.CACHE);
            publisher.publishEvent(new LogSysEvent(this, logParam));
        }
    }
}
