package io.nicheblog.dreamdiary.web.scheduler;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogSysEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogSysParam;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.service.jrnl.sumry.JrnlSumryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * JrnlSumryScheduler
 * 저널 집계 Scheduler
 *
 * @author nichefish
 */
@Component
@Log4j2
public class JrnlSumryScheduler {

    @Resource(name = "jrnlSumryService")
    public JrnlSumryService jrnlSumryService;
    @Resource
    private ApplicationEventPublisher publisher;

    /**
     * 하루에 한 번 전체 집계 갱신
     * 매일 00시 15분 실행
     */
    @Scheduled(cron = "0 15 0 * * *", zone = Constant.LOC_SEOUL)         // second min hour day month weekday
    public void jrnlSumrySchedule() {

        log.info("jrnlSumrySchedule...");

        LogSysParam logParam = new LogSysParam();
        String rsltMsg = "";
        try {
            jrnlSumryService.makeTotalYySumry();
        } catch (Exception e) {
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            // 로그 관련 처리
            logParam.setResult(false, rsltMsg, ActvtyCtgr.JRNL);
            publisher.publishEvent(new LogSysEvent(this, logParam));
        }
    }
}
