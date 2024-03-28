package io.nicheblog.dreamdiary.global.cmm.mail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;

/**
 * MailAddress
 * TODO: 봐야한다.
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailAddress {
    private String email;
    private String name;

    /**
     * 생성자
     */
    public MailAddress(final String email) {
        super();
        this.email = email;
        this.name = "";
    }

    public InternetAddress getInternetAddress() throws UnsupportedEncodingException {
        return new InternetAddress(email, name, "UTF-8");
    }
}
