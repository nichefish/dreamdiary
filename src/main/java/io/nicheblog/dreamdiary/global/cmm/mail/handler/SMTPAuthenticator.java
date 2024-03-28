package io.nicheblog.dreamdiary.global.cmm.mail.handler;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * SMTPAuthenticator
 * TODO: 파봐야함
 *
 * @author nichefish
 */
public class SMTPAuthenticator
        extends Authenticator {

    /**
     * SMTP 인증
     */
    PasswordAuthentication passwordAuthentication;

    public SMTPAuthenticator(
            final String userName,
            final String password
    ) {
        passwordAuthentication = new PasswordAuthentication(userName, password);
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return passwordAuthentication;
    }
}
