package io.nicheblog.dreamdiary.global.cmm.mail.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDtlDto;
import io.nicheblog.dreamdiary.global.cmm.mail.event.MailSendEvent;
import io.nicheblog.dreamdiary.global.cmm.mail.handler.SMTPAuthenticator;
import io.nicheblog.dreamdiary.global.cmm.mail.model.MailAddress;
import io.nicheblog.dreamdiary.global.cmm.mail.model.MailSendModel;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteUrl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.util.Properties;

/**
 * MailSendService
 * TODO: 파봐야 함
 *
 * @author cslim
 * @version 1.0, 2016.12.14
 */
@Service("mainSendService")
@Log4j2
public class MailSendService
        implements ApplicationListener<MailSendEvent> {

    @Value("${mail.enabled:false}")
    private boolean enabled;

    @Value("${mail.imageUrl:/}")
    private String imageUrl;

    @Value("${mail.smtp.auth.userName:}")
    private String authUserName;

    @Value("${mail.smtp.auth.password:}")
    private String authPassword;

    private String webMasterName = "intranet";
    private String webMasterEmail = "sinziman@sinzi.net";

    @Autowired
    private Configuration freemarkerMailConfiguration;

    @Override
    public void onApplicationEvent(final MailSendEvent event) {
        MailSendModel mailSendModel = event.getMailSendModel();
        mailSendModel.data.put("subject", mailSendModel.getSubject());
        mailSendModel.data.put("siteName", SiteUrl.DOMAIN);
        mailSendModel.data.put("siteUrl", SiteUrl.DOMAIN);
        mailSendModel.data.put("imageUrl", imageUrl);
        mailSendModel.data.put("authUrl", imageUrl);

        if (!enabled) {
            log.info("mail service disabled !!");
            return;
        }

        try {
            Template template = freemarkerMailConfiguration.getTemplate(mailSendModel.tpl);
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, mailSendModel.data);

            if (mailSendModel.getSender() == null) {
                mailSendModel.setSender(new MailAddress(webMasterEmail, webMasterName));
            }

            String charset = "UTF-8";
            try {
                Properties props = new Properties();

                Authenticator authenticator = new SMTPAuthenticator(authUserName, authPassword);

                Session session = Session.getInstance(props, authenticator);
                MimeMessage message = new MimeMessage(session);
                message.setFrom(mailSendModel.getSender()
                                             .getInternetAddress());
                String subject = mailSendModel.getSubject();

                message.setSubject(subject, charset);

                Multipart multipart = new MimeMultipart();

                // 메세지 본문
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(content, "text/html; charset=" + charset);
                multipart.addBodyPart(messageBodyPart);

                // 메세지 첨부
                for (AtchFileDtlDto atchFile : mailSendModel.getAtchFileList()) {
                    MimeBodyPart fileBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(atchFile.getFileStrePath());
                    fileBodyPart.setDataHandler(new DataHandler(source));
                    fileBodyPart.setFileName(MimeUtility.encodeText(atchFile.getStreFileNm(), charset, "B"));
                    multipart.addBodyPart(fileBodyPart);
                }
                message.setContent(multipart);

                for (MailAddress recipient : mailSendModel.getRecipientList()) {
                    message.setRecipient(Message.RecipientType.TO, recipient.getInternetAddress());
                    //message.addRecipient(Message.RecipientType.TO, recipient);
                    Transport.send(message);
                }

            } catch (MessagingException e) {
                MessageUtils.getExceptionMsg(e);
            }

        } catch (Exception e) {
            MessageUtils.getExceptionMsg(e);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getWebMasterEmail() {
        return webMasterEmail;
    }
}
