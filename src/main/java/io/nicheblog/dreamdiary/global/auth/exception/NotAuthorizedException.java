/*
 * Copyright 2000-2022 BixSoft Co., All Rights Reserved.
 *   http://www.bixsoft.net
 *
 * This source code is digitised property of BixSoft Company Limited ("BixSoft") and
 * protected by copyright under the law of Republic of Korea and other foreign laws.
 * Reproduction and/or redistribution of the source code without written permission of
 * BixSoft is not allowed. Also, it is severely prohibited to register whole or specific
 * part of the source code to any sort of information retrieval system.
 */
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
