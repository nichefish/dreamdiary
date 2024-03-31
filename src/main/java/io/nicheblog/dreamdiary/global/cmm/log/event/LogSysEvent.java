package io.nicheblog.dreamdiary.global.cmm.log.event;

import io.nicheblog.dreamdiary.global.cmm.log.model.LogSysParam;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * LogSysEvent
 * <pre>
 *  시스템 로그 적재 이벤트
 * </pre>
 *
 * @author nichefish
 */
@Getter
public class LogSysEvent
        extends ApplicationEvent {

    private final LogSysParam log;

    /* ----- */

    /**
     * 생성자
     */
    public LogSysEvent(
            final Object source,
            final LogSysParam log
    ) {
        super(source);
        this.log = log;
    }
}
