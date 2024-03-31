package io.nicheblog.dreamdiary.global.cmm.log.event;

import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * LogAnonmssActvtyEvent
 * <pre>
 *  활동 로그(비로그인) 적재 이벤트
 * </pre>
 *
 * @author nichefish
 */
@Getter
public class LogAnonActvtyEvent
        extends ApplicationEvent {

    private final LogActvtyParam log;

    /* ----- */

    /**
     * 생성자
     */
    public LogAnonActvtyEvent(
            final Object source,
            final LogActvtyParam log
    ) {
        super(source);
        this.log = log;
    }
}
