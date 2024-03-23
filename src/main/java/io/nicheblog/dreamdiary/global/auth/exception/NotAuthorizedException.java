package io.nicheblog.dreamdiary.global.auth.exception;

/**
 * NotAuthorizedException
 * <pre>
 *  자원에 할당된 인가authorization 정보 부재시 던지는 Custom Exception
 * </pre>
 *
 * @author nichefish
 * @since 2022-05-01
 */
public class NotAuthorizedException
        extends RuntimeException {

    public NotAuthorizedException(
            final String msg,
            final Throwable cause
    ) {
        super(msg, cause);
    }

    public NotAuthorizedException(final String msg) {
        super(msg);
    }
}
