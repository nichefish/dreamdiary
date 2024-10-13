package io.nicheblog.dreamdiary.global._common.error.exception;

/**
 * DupRegException
 * <pre>
 *  항목 중복(이중)등록시 던지는 Custom Exception
 * </pre>
 *
 * @author nichefish
 */
public class DupRegException
        extends RuntimeException {

    public DupRegException(
            final String msg,
            final Throwable cause
    ) {
        super(msg, cause);
    }

    public DupRegException(final String msg) {
        super(msg);
    }
}
