package io.nicheblog.dreamdiary.domain._core.log.sys.event;

import io.nicheblog.dreamdiary.domain._core.log.sys.model.LogSysParam;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * LogSysEvent
 * <pre>
 *  시스템 로그 적재 이벤트.
 * </pre>
 *
 * @author nichefish
 */
@Getter
public class LogSysEvent
        extends ApplicationEvent {

    /** 시스템 로그 */
    private final LogSysParam log;

    /* ----- */

    /**
     * 생성자.
     * @param source 이벤트가 발생한 객체
     * @param log 이벤트와 관련된 로그 정보를 담은 `LogSysParam` 객체
     */
    public LogSysEvent(final Object source, final LogSysParam log) {
        super(source);
        this.log = log;
    }
}