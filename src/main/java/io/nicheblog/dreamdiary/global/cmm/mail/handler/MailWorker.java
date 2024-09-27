package io.nicheblog.dreamdiary.global.cmm.mail.handler;

import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogSysEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogSysParam;
import io.nicheblog.dreamdiary.global.cmm.mail.event.MailSendEvent;
import io.nicheblog.dreamdiary.global.cmm.mail.model.MailSendParam;
import io.nicheblog.dreamdiary.global.cmm.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * MailWorker
 * <pre>
 *  메일 처리 Worker :: Runnable 구현 (Queue 처리)
 * </pre>
 *
 * @author nichefish
 **/
@Component
@RequiredArgsConstructor
@Log4j2
public class MailWorker implements Runnable {

	private final MailService mailService;
	private final ApplicationEventPublisher publisher;

	/** 메일 queue */
	private static final BlockingQueue<MailSendEvent> mailQueue = new LinkedBlockingQueue<>();

	@PostConstruct
	public void init() {
		Thread workerThread = new Thread(this);
		workerThread.start();
	}

	@Override
	public void run() {
		while (true) {
			// Blocks until an element is available
			try {
				// 메일 발송 처리
				MailSendEvent mailEvent = mailQueue.take();
				MailSendParam mailSendParam = mailEvent.getMailSendParam();
				mailService.send(mailSendParam);
			} catch (InterruptedException e) {
				log.warn("mail send failed", e);
				Thread.currentThread().interrupt();
				LogSysParam logParam = new LogSysParam(true, "메일 발송에 실패했습니다.", ActvtyCtgr.SYSTEM);
				logParam.setExceptionInfo(e);
				publisher.publishEvent(new LogSysEvent(this, logParam));
			} catch (Exception e) {
				log.warn("mail send failed", e);
				LogSysParam logParam = new LogSysParam(true, "메일 발송에 실패했습니다.", ActvtyCtgr.SYSTEM);
				logParam.setExceptionInfo(e);
				publisher.publishEvent(new LogSysEvent(this, logParam));
			}
		}
	}

	public void offer(MailSendEvent event) {
		boolean isSuccess = mailQueue.offer(event);
		if (!isSuccess) log.warn("queue offer failed... {}", event.toString());
	}
}
