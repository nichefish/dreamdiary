package io.nicheblog.dreamdiary.auth.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * AccountNeedsPwResetException
 * <pre>
 *  Spring Security:: 패스워드 리셋 강제시 던지는 Custom Exception
 * </pre>
 *
 * @author nichefish
 */
public class AccountNeedsPwResetException
        extends AuthenticationException {

    /**
     * 생성자.
     *
     * @param msg 예외 메시지
     * @param cause 이 예외의 원인
     */
    public AccountNeedsPwResetException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

    /**
     * 생성자.
     *
     * @param msg 예외 메시지
     */
    public AccountNeedsPwResetException(final String msg) {
        super(msg);
    }
}
