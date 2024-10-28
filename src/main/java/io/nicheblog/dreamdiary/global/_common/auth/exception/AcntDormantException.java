package io.nicheblog.dreamdiary.global._common.auth.exception;

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

    /**
     * 생성자.
     *
     * @param msg 예외 메시지
     * @param cause 이 예외의 원인
     */
    public AcntDormantException(
            final String msg,
            final Throwable cause
    ) {
        super(msg, cause);
    }

    /**
     * 생성자.
     *
     * @param msg 예외 메시지
     */
    public AcntDormantException(final String msg) {
        super(msg);
    }
}
