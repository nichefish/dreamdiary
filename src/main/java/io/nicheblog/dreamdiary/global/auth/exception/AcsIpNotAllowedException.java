package io.nicheblog.dreamdiary.global.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * AcsIpNotAllowedException
 * <pre>
 *  Spring Security:: 접속IP 불일치시 던지는 Custom Exception
 * </pre>
 */
public class AcsIpNotAllowedException
        extends AuthenticationException {

    public AcsIpNotAllowedException(
            final String msg,
            final Throwable cause
    ) {
        super(msg, cause);
    }

    public AcsIpNotAllowedException(final String msg) {
        super(msg);
    }
}
