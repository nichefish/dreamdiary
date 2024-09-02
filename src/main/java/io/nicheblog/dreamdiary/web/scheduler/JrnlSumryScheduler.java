package io.nicheblog.dreamdiary.web.scheduler;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogSysEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogSysParam;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.service.jrnl.day.JrnlDayTagCtgrSynchronizer;
import io.nicheblog.dreamdiary.web.service.jrnl.diary.JrnlDiaryTagCtgrSynchronizer;
import io.nicheblog.dreamdiary.web.service.jrnl.dream.JrnlDreamTagCtgrSynchronizer;
import io.nicheblog.dreamdiary.web.service.jrnl.sumry.JrnlSumryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * JrnlSumryScheduler
 * <pre>
 *  저널 집계 Scheduler
 * </pre>
 *
 * @author nichefish
 */
@Component
@Log4j2
public class JrnlSumryScheduler {

    @Resource(name = "jrnlSumryService")
    public JrnlSumryService jrnlSumryService;

    @Resource(name = "jrnlDreamTagCtgrSynchronizer")
    private JrnlDreamTagCtgrSynchronizer jrnlDreamTagCtgrSynchronizer;
    @Resource(name = "jrnlDiaryTagCtgrSynchronizer")
    private JrnlDiaryTagCtgrSynchronizer jrnlDiaryTagCtgrSynchronizer;
    @Resource(name = "jrnlDayTagCtgrSynchronizer")
    private JrnlDayTagCtgrSynchronizer jrnlDayTagCtgrSynchronizer;

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
            // 결산 생성
            jrnlSumryService.makeTotalYySumry();
            // 캐시 재생성 위해 조회
            jrnlSumryService.getTotalSumry();
        } catch (Exception e) {
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            // 로그 관련 처리
            logParam.setResult(false, rsltMsg, ActvtyCtgr.JRNL);
            publisher.publishEvent(new LogSysEvent(this, logParam));
        }
    }

    /**
     * 하루에 한 번 태그 카테고리 동기화
     */
    @Scheduled(cron = "0 25 0 * * *", zone = Constant.LOC_SEOUL)         // second min hour day month weekday
    public void jrnlTagCtgrSyncSchedule() {

        log.info("jrnlTagCtgrSyncSchedule...");

        LogSysParam logParam = new LogSysParam();
        String rsltMsg = "";
        try {
            // 저널 일자 태그 동기화
            jrnlDayTagCtgrSynchronizer.tagSync();
            // 저널 일기 태그 동기화
            jrnlDiaryTagCtgrSynchronizer.tagSync();
            // 저널 꿈 태그 동기화
            jrnlDreamTagCtgrSynchronizer.tagSync();
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
