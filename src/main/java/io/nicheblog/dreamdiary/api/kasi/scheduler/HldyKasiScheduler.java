package io.nicheblog.dreamdiary.api.kasi.scheduler;

import io.nicheblog.dreamdiary.api.kasi.model.HldyKasiApiItemDto;
import io.nicheblog.dreamdiary.api.kasi.service.HldyKasiApiService;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogSysEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogSysParam;
import io.nicheblog.dreamdiary.global.util.DateUtil;
import io.nicheblog.dreamdiary.global.util.MessageUtil;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * HldyKasiScheduler
 * <pre>
 *  API:: 한국천문연구원(KASI):: 특일 정보 등록 Scheduler
 * </pre>
 *
 * @author nichefish
 */
@Component("hldyKasiScheduler")
@Log4j2
public class HldyKasiScheduler {

    @Resource(name = "hldyKasiApiService")
    public HldyKasiApiService hldyKasiApiService;

    @Resource
    private ApplicationEventPublisher publisher;

    /**
     * 1년에 한 번씩 휴일/특일 정보 API 조회하여 등록
     * 매년 1월 1일 00시 30분 실행
     */
    @Scheduled(cron = "0 30 0 1 1 *", zone = "Asia/Seoul")         // second min hour day month weekday
    public void hldyKasiScheduler(
            final LogSysParam logParam
    ) {

        log.info("hldyKasiSchedule...");

        boolean isSuccess = false;
        String resultMsg = "";
        try {
            String yyStr = DateUtil.getCurrYearStr();
            // 기존 정보 (API로 받아온 휴일) 삭제 후 재등록
            hldyKasiApiService.delHldyList(yyStr);
            List<HldyKasiApiItemDto> hldyApiList = hldyKasiApiService.getHldyList(yyStr);
            isSuccess = hldyKasiApiService.regHldyList(hldyApiList);
            resultMsg = MessageUtil.getMessage(isSuccess ? MessageUtil.RSLT_SUCCESS : MessageUtil.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtil.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtil.getExceptionNm(e), e.getMessage());
        } finally {
            logParam.setResult(isSuccess, resultMsg, ActvtyCtgr.API_KASI);
            publisher.publishEvent(new LogSysEvent(this, logParam));
        }
    }
}
