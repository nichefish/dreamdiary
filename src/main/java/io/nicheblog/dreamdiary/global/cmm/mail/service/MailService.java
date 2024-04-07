package io.nicheblog.dreamdiary.global.cmm.mail.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogSysParam;
import io.nicheblog.dreamdiary.global.cmm.mail.model.MailAddress;
import io.nicheblog.dreamdiary.global.cmm.mail.model.MailSendParam;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MailService
 * <pre>
 *  이메일 관련 처리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service
@Log4j2
public class MailService {

    @Resource
    private JavaMailSender mailSender;
    @Autowired
    private Configuration freemarkerMailConfiguration;
    @Resource
    private ApplicationEventPublisher publisher;

    private String WEBMASTER_NAME = "webmaster";
    private String WEBMASTER_EMAIL = "webmaster@email.net";

    /** 메일 발송 */
    public Boolean send(MailSendParam mailSendParam) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, Constant.CHARSET_UTF_8);

            // 발신자
            MailAddress sender = mailSendParam.getSender();
            if (sender == null) sender = new MailAddress(WEBMASTER_EMAIL, WEBMASTER_NAME);
            helper.setFrom(sender.getInternetAddress());
            // 제목
            helper.setSubject(mailSendParam.getSubject());
            // 수신자
            List<InternetAddress> list = new ArrayList<>();
            for (MailAddress mailAddress : mailSendParam.getRecipientList()) {
                InternetAddress internetAddress = mailAddress.getInternetAddress();
                list.add(internetAddress);
            }
            helper.setTo(list.toArray(new InternetAddress[0]));
            // 내용 :: Freemarker 템플릿 처리
            Map<String, Object> dataMap = mailSendParam.getDataMap();
            Template tmplat = freemarkerMailConfiguration.getTemplate(mailSendParam.tmplat);
            String compiledContent = FreeMarkerTemplateUtils.processTemplateIntoString(tmplat, dataMap);
            helper.setText(compiledContent, true);
            // 첨부파일 처리
            mailSendParam.getAtchFileList().forEach(attachFile -> {
                try {
                    helper.addAttachment(attachFile.getOrgnFileNm(), new File(attachFile.getFileStrePath()));
                } catch (MessagingException e) {
                    log.error("Error attaching file", e);
                }
            });

            mailSender.send(message);
        } catch (Exception e) {
            publisher.publishEvent(new LogSysParam(false, "메일 발송에 실패했습니다."));
            return false;
        }
        return true;
    }
}
