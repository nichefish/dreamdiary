package io.nicheblog.dreamdiary.global.cmm.mail.handler;

import io.nicheblog.dreamdiary.global.cmm.mail.event.MailSendEvent;
import io.nicheblog.dreamdiary.global.cmm.mail.model.MailSendParam;
import io.nicheblog.dreamdiary.global.cmm.mail.service.MailService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * MailWorker
 * <pre>
 *  메일 처리 Worker :: Runnable 구현 (Queue 처리)
 * </pre>
 *
 * @author nichefish
 **/
@Component
@Log4j2
public class MailWorker implements Runnable {

	@Resource(name = "mailService")
	private MailService mailService;

	/** 메일 queue */
	private Queue<MailSendEvent> mailQueue = new ConcurrentLinkedQueue<>();

	@Override
	public void run() {
		while (true) {
			consume();
			try {
				synchronized (mailQueue) {
					mailQueue.wait();
				}
			} catch (InterruptedException ex) {
				log.error(ex);
			}
		}
	}

	/** 메일 발송 처리 */
	private void consume() {
		while (!mailQueue.isEmpty()) {
			MailSendEvent event = mailQueue.poll();
			MailSendParam mailSendParam = event.getMailSendParam();
			
			try {
				mailService.send(mailSendParam);
			} catch (Exception e) {
				throw new RuntimeException("메일 발송 중 에러가 발생했습니다.", e);
			}
		}
	}

	public void offer(MailSendEvent event) {
		this.mailQueue.offer(event);
	}
}
