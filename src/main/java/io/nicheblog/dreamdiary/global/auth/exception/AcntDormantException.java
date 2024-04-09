package io.nicheblog.dreamdiary.global.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * AcntDormantException
 * <pre>
 *  Spring Security:: 장기간 미로그인시 던지는 Custom Exception
 * </pre>
 *
 * @author nichefish
 */
public class AcntDormantException
        extends AuthenticationException {

    public AcntDormantException(
            final String msg,
            final Throwable cause
    ) {
        super(msg, cause);
    }

    public AcntDormantException(final String msg) {
        super(msg);
    }
}
