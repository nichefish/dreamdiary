package io.nicheblog.dreamdiary.global.cmm.mail.event;

import io.nicheblog.dreamdiary.global.cmm.mail.model.MailSendParam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * MailSendEvent
 * <pre>
 *  메일 발송 이벤트
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
public class MailSendEvent
        extends ApplicationEvent {

    private final MailSendParam mailSendParam;

    /* ----- */

    /**
     * 생성자
     */
    public MailSendEvent(final Object source, final MailSendParam mailSendParam) {
        super(source);
        this.mailSendParam = mailSendParam;
    }
}
