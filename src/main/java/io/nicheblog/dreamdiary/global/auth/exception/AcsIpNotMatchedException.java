package io.nicheblog.dreamdiary.global.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * AcsIpNotMatchedException
 * <pre>
 *  Spring Security:: 접속IP 불일치시 던지는 Custom Exception
 * </pre>
 */
public class AcsIpNotMatchedException
        extends AuthenticationException {

    public AcsIpNotMatchedException(
            final String msg,
            final Throwable cause
    ) {
        super(msg, cause);
    }

    public AcsIpNotMatchedException(final String msg) {
        super(msg);
    }
}
