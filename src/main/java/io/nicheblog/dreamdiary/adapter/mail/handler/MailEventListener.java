package io.nicheblog.dreamdiary.adapter.mail.handler;

import io.nicheblog.dreamdiary.adapter.mail.event.MailSendEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * MailEventListener
 * <pre>
 *  메일 이벤트 처리 핸들러
 * </pre>
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
public class MailEventListener {

    private final MailSendWorker MailSendWorker;

    /**
     * 메일 발송 이벤트를 처리한다.
     *
     * @param event 처리할 이벤트 객체
     */
    @EventListener
    @Async
    public void handleMailEvent(MailSendEvent event) {
        // 큐에 전달하기 전에 request 관련 속성들을 미리 바인딩해야 한다. (권장)
        MailSendWorker.offer(event);
    }
}
