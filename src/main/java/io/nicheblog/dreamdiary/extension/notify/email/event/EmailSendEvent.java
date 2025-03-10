package io.nicheblog.dreamdiary.extension.notify.email.event;

import io.nicheblog.dreamdiary.extension.notify.email.handler.EmailEventListener;
import io.nicheblog.dreamdiary.extension.notify.email.model.EmailSendParam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * MailSendEvent
 * <pre>
 *  메일 발송 이벤트.
 * </pre>
 *
 * @author nichefish
 * @see EmailEventListener
 */
@Getter
@Setter
public class EmailSendEvent
        extends ApplicationEvent {

    /** 보안 컨텍스트 */
    private final SecurityContext securityContext;
    /** 메일 팔송 파라미터 */
    private final EmailSendParam emailSendParam;

    /* ----- */

    /**
     * 생성자.
     *
     * @param source 이벤트의 출처를 나타내는 객체
     * @param emailSendParam 발송할 메일 정보를 담은 객체
     */
    public EmailSendEvent(final Object source, final EmailSendParam emailSendParam) {
        super(source);
        this.securityContext = SecurityContextHolder.getContext();
        this.emailSendParam = emailSendParam;
    }
}
