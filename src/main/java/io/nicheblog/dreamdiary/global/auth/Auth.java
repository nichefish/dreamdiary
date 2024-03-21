package io.nicheblog.dreamdiary.global.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 권한 Enum
 */
@AllArgsConstructor
@Getter
public enum Auth {
    MNGR("MNGR", "관리자"),
    USER("USER", "사용자"),
    DEV("DEV", "개발자");

    private final String key;
    private final String desc;
}