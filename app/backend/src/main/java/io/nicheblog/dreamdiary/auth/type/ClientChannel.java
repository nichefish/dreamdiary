package io.nicheblog.dreamdiary.auth.type;

import org.apache.commons.lang3.StringUtils;

/**
 * ClientChannel
 * <pre>
 *  로그인 클라이언트 채널 Enum.
 *  웹(Vue SPA)과 모바일 앱 세션을 분리해서 중복 로그인 정책을 채널별로 적용하기 위해 사용한다.
 *  요청 헤더 "X-Client-Channel: MOBILE"(대소문자 무시)이면 MOBILE, 그 외(헤더 없음 포함)는 WEB으로 판단한다.
 * </pre>
 *
 * @author nichefish
 */
public enum ClientChannel {

    /** 웹 (기본값) */
    WEB,
    /** 모바일 앱 */
    MOBILE;

    /** 클라이언트 채널 판별용 요청 헤더명 */
    public static final String HEADER_NAME = "X-Client-Channel";

    /** 로그인 세션에 저장되는 클라이언트 채널 attribute 이름 */
    public static final String SESSION_ATTR = "clientChannel";

    /** 중복 로그인 확인 대기 시 해당 시도의 클라이언트 채널을 담는 세션 attribute 이름 ("isDupIdLogin"과 함께 사용) */
    public static final String DUP_LOGIN_SESSION_ATTR = "isDupIdLoginChannel";

    /** 기본 채널 */
    public static final ClientChannel DEFAULT = WEB;

    /**
     * 헤더(또는 세션 attribute) 값으로 클라이언트 채널을 반환한다.
     * "MOBILE"(대소문자 무시)만 MOBILE로 판단하고, 그 외 값은 모두 WEB으로 처리한다.
     *
     * @param value 헤더 또는 세션 attribute 값
     * @return {@link ClientChannel} -- 판별된 클라이언트 채널
     */
    public static ClientChannel from(final String value) {
        if (StringUtils.isBlank(value)) return DEFAULT;
        return MOBILE.name().equalsIgnoreCase(value.trim()) ? MOBILE : DEFAULT;
    }

    /**
     * 세션 attribute 값(Enum 또는 문자열)으로 클라이언트 채널을 반환한다.
     *
     * @param value 세션 attribute 값
     * @return {@link ClientChannel} -- 판별된 클라이언트 채널, 값이 없으면 null
     */
    public static ClientChannel fromAttribute(final Object value) {
        if (value instanceof ClientChannel) return (ClientChannel) value;
        if (value instanceof String && StringUtils.isNotBlank((String) value)) return from((String) value);
        return null;
    }
}
