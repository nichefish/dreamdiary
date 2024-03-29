package io.nicheblog.dreamdiary.global.cmm.log.handler;

import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogAnonActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogSysEvent;
import io.nicheblog.dreamdiary.global.cmm.log.service.LogService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * LogActvtyEventListener
 * <pre>
 *  로그 이벤트 처리 핸들러
 * </pre>
 *
 * @author nichefish
 */
@Component("logEventListener")
public class LogEventListener {

    @Resource
    private LogService logService;

    /**
     * 현재 인증(로그인) 상태인 등록/수정자 반환
     */
    @EventListener
    public void handleLogActvtyEvent(LogActvtyEvent logActvtyEvent) {
        logService.regLogActvty(logActvtyEvent.getLog());
    }

    /**
     * 현재 인증(로그인) 상태인 등록/수정자 반환
     */
    @EventListener
    public void handleLogAnonActvtyEvent(LogAnonActvtyEvent logActvtyEvent) {
        logService.regLogAnonActvty(logActvtyEvent.getLog());
    }

    /**
     * 현재 인증(로그인) 상태인 등록/수정자 반환
     */
    @EventListener
    public void handleLogSysEvent(LogSysEvent logSysEvent) {
        logService.regSysActvty(logSysEvent.getLog());
    }

}
