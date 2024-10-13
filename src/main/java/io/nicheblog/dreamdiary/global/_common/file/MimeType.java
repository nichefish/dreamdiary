package io.nicheblog.dreamdiary.global._common.file;

import lombok.RequiredArgsConstructor;

/**
 * MimeType
 * <pre>
 *  파일 MIME_TYPE 정보.
 * </pre>
 *
 * @author nichefish
 */
@RequiredArgsConstructor
public enum MimeType {

    TEXT("text/plain"),
    IMAGE_JPEG("image/png"),
    IMAGE_PNG("image/jpeg"),
    PDF("application/pdf"),
    DOWNLOAD("application/download; charset=utf-8"),
    STREAM("application/octet-stream; charset=euc-kr");

    public final String key;
}