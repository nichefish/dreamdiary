package io.nicheblog.dreamdiary.global.cmm.log.event;

import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * LogActvtyEvent
 * <pre>
 *  활동 로그 적재 이벤트
 * </pre>
 *
 * @author nichefish
 */
@Getter
public class LogActvtyEvent
        extends ApplicationEvent {

    private final LogActvtyParam log;

    /* ----- */

    /**
     * 생성자
     */
    public LogActvtyEvent(final Object source, final LogActvtyParam log) {
        super(source);
        this.log = log;
    }
}
