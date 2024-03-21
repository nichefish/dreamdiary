package io.nicheblog.dreamdiary.global.cmm.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * AcntNeedsPwResetException
 * <pre>
 *  Spring Security:: 패스워드 리셋 강제시 던지는 Custom Exception
 * </pre>
 *
 * @author nichefish
 */
public class AcntNeedsPwResetException
        extends AuthenticationException {

    public AcntNeedsPwResetException(
            final String msg,
            final Throwable cause
    ) {
        super(msg, cause);
    }

    public AcntNeedsPwResetException(final String msg) {
        super(msg);
    }
}
