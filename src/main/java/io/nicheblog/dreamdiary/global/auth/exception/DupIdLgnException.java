package io.nicheblog.dreamdiary.global.auth.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * DupIdLgnException
 * <pre>
 *  Spring Security:: 중복 로그인시 던지는 Custom Exception
 * </pre>
 */
public class DupIdLgnException
        extends AuthenticationException {

    public DupIdLgnException(
            final String msg,
            final Throwable cause
    ) {
        super(msg, cause);
    }

    public DupIdLgnException(final String msg) {
        super(msg);
    }
}
