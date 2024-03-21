package io.nicheblog.dreamdiary.global.cmm.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * DormantAcntException
 * <pre>
 *  Spring Security:: 장기간 미로그인시 던지는 Custom Exception
 * </pre>
 *
 * @author nichefish
 */
public class AcntNotCfException
        extends AuthenticationException {

    public AcntNotCfException(
            final String msg,
            final Throwable cause
    ) {
        super(msg, cause);
    }

    public AcntNotCfException(final String msg) {
        super(msg);
    }
}
