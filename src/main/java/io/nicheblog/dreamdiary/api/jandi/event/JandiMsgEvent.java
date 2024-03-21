package io.nicheblog.dreamdiary.api.jandi.event;

import io.nicheblog.dreamdiary.api.jandi.model.JandiParam;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * JandiMsgEvent
 * <pre>
 *  잔디 메세지 발송 이벤트
 * </pre>
 *
 * @author nichefish
 */
@Getter
public class JandiMsgEvent
        extends ApplicationEvent {

    private final JandiParam jandiMsg;

    /* ----- */

    /**
     * 생성자
     */
    public JandiMsgEvent(
            Object source,
            JandiParam jandiMsg
    ) {
        super(source);
        this.jandiMsg = jandiMsg;
    }
}
