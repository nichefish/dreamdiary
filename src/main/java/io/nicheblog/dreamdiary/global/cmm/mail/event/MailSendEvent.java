package io.nicheblog.dreamdiary.global.cmm.mail.event;

import io.nicheblog.dreamdiary.global.cmm.mail.model.MailSendModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * MailSendEvent
 * TODO: 봐야한다.
 *
 * @author nichefish
 */
@Getter
@Setter
public class MailSendEvent
        extends ApplicationEvent {

    private static final long serialVersionUID = 1L;
    private MailSendModel mailSendModel;

    public MailSendEvent(
            final Object source,
            final MailSendModel mailSendModel
    ) {
        super(source);
        this.mailSendModel = mailSendModel;
    }
}
