package io.nicheblog.dreamdiary.feature.prospective.keep.reminder;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 등록 기기 로컬 09:00·19:00 분의 발송과 Expo 전달 결과 조회를 실행한다. SQL 수동 적용이 실행 전제다. */
@Component
@RequiredArgsConstructor
@Log4j2
public class KeepReminderScheduler {
    private final KeepReminderDispatcher dispatcher;

    @Scheduled(fixedDelay = 15000, initialDelay = 30000)
    public void dispatch() {
        try { dispatcher.dispatch(); }
        catch (Exception e) { log.error("Keep reminder scheduler failed. errorType={}", e.getClass().getSimpleName()); }
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 30000)
    public void receipts() {
        try { dispatcher.receipts(); }
        catch (Exception e) { log.error("Keep reminder receipt scheduler failed. errorType={}", e.getClass().getSimpleName()); }
    }
}