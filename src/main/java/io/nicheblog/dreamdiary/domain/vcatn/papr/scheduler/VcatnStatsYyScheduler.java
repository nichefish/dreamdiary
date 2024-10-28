package io.nicheblog.dreamdiary.domain.vcatn.papr.scheduler;

import io.nicheblog.dreamdiary.domain.vcatn.stats.service.VcatnStatsYyService;
import io.nicheblog.dreamdiary.global._common.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.global._common.log.sys.event.LogSysEvent;
import io.nicheblog.dreamdiary.global._common.log.sys.model.LogSysParam;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * VcatnStatsYyScheduler
 * <pre>
 *  휴가관리정보 추가 Scheduler.
 *  1년에 한번 자동으로 추가 (해당 정보가 없으면 휴가 산정시 에러가 발생하므로)
 * </pre>
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class VcatnStatsYyScheduler {

    private final VcatnStatsYyService vcatnStatsYyService;
    private final ApplicationEventPublisher publisher;

    /**
     * 당해 휴가집계년도 관리 정보 추가
     * 매년 초 = 1월 1일 00시 10분 실행
     */
    @Scheduled(cron = "0 10 0 1 1 *", zone = "Asia/Seoul")         // second min hour day month weekday
    public void vcatnStatsYySchedule() {

        log.info("vcatnStatsYySchedule...");

        LogSysParam logParam = new LogSysParam();
        boolean isSuccess;
        String rsltMsg = "";
        try {
            isSuccess = vcatnStatsYyService.regVcatnYyDt();
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            // 로그 관련 처리
            logParam.setResult(false, rsltMsg, ActvtyCtgr.CACHE);
            publisher.publishEvent(new LogSysEvent(this, logParam));
        }
    }
}
