package io.nicheblog.dreamdiary.web.scheduler;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogSysEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogSysParam;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
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
@Component
@Log4j2
public class EhCacheScheduler {

    @Resource
    private ApplicationEventPublisher publisher;

    /**
     * 1시간에 한 번씩 전체 캐시 클리어
     * 매시간 00분 실행
     */
    @Scheduled(cron = "0 0 * * * *", zone = Constant.LOC_SEOUL)         // second min hour day month weekday
    public void cacheAllClearSchedule() {

        log.info("cacheAllClearSchedule...");

        LogSysParam logParam = new LogSysParam();
        try {
            EhCacheUtils.clearAllCaches();
        } catch (Exception e) {
            String resultMsg = MessageUtils.getExceptionMsg(e);
            // 수시로 이루어지므로 실패시에만 로깅한다.
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            logParam.setResult(false, resultMsg, ActvtyCtgr.CACHE);
            publisher.publishEvent(new LogSysEvent(this, logParam));
        }
    }
}
