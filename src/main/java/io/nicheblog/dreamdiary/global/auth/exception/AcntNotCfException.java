package io.nicheblog.dreamdiary.global.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * AcntNotCfException
 * <pre>
 *  Spring Security:: 계정 미승인시 던지는 Custom Exception
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
