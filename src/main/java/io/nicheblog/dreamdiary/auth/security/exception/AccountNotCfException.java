package io.nicheblog.dreamdiary.auth.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * AccountNotCfException
 * <pre>
 *  Spring Security:: 계정 미승인시 던지는 Custom Exception
 * </pre>
 *
 * @author nichefish
 */
public class AccountNotCfException
        extends AuthenticationException {

    /**
     * 생성자.
     *
     * @param msg 예외 메시지
     * @param cause 이 예외의 원인
     */
    public AccountNotCfException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

    /**
     * 생성자.
     *
     * @param msg 예외 메시지
     */
    public AccountNotCfException(final String msg) {
        super(msg);
    }
}
