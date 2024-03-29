package io.nicheblog.dreamdiary.global.auth;

import lombok.AllArgsConstructor;

/**
 * 권한 Enum
 */
@AllArgsConstructor
public enum Auth {

    MNGR("MNGR", "관리자"),
    USER("USER", "사용자"),
    DEV("DEV", "개발자");

    public final String key;
    public final String desc;
}