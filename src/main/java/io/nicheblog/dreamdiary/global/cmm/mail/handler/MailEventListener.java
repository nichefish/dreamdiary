package io.nicheblog.dreamdiary.global.cmm.mail.handler;

import io.nicheblog.dreamdiary.global.cmm.mail.event.MailSendEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * MailEventListener
 * <pre>
 *  메일 이벤트 처리 핸들러
 * </pre>
 *
 * @author nichefish
 */
@Component
public class MailEventListener {

    @Resource(name = "mailWorker")
    MailWorker MailWorker;

    /**
     * 메일 발송
     */
    @EventListener
    @Async
    public void handleMailEvent(MailSendEvent event) throws Exception {
        // 큐에 전달하기 전에 request 관련 속성들을 미리 바인딩해야 한다. (권장)
        MailWorker.offer(event);
    }
}
