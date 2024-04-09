package io.nicheblog.dreamdiary.global.cmm.mail.model;

import io.nicheblog.dreamdiary.global.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;

/**
 * MailAddress
 * <pre>
 *  메일 (발신자/수신자) 주소
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailAddress {

    /** 이메일 */
    private String email;

    /** 이름 */
    private String name;

    /* ----- */

    public InternetAddress getInternetAddress() throws UnsupportedEncodingException {
        return new InternetAddress(email, name, Constant.CHARSET_UTF_8);
    }
}
