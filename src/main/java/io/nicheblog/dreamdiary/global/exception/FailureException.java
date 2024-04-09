package io.nicheblog.dreamdiary.global.exception;

/**
 * FailureException
 * <pre>
 *  실패시 분기에서 빼버리기 위해 던지는 Custom Exception
 * </pre>
 *
 * @author nichefish
 */
public class FailureException
        extends RuntimeException {

    public FailureException(
            final String msg,
            final Throwable cause
    ) {
        super(msg, cause);
    }

    public FailureException(final String msg) {
        super(msg);
    }
}
