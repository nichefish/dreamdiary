package io.nicheblog.dreamdiary.cmm;

import io.nicheblog.dreamdiary.api.ApiConstant;
import io.nicheblog.dreamdiary.cmm.auth.Auth;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Constant
 * <pre>
 *  공통으로 사용하는 코드성 데이터 정의
 * </pre>
 * TODO: enum으로 점진적 변환쓰
 * TODO: Freemarker 단에서 enum을 어떻게 처리할 것인지?
 *
 * @author nichefish
 */
public final class Constant
        implements ApiConstant {

    /**
     * 사용자 권한 코드
     */
    public static final String AUTH_CD = "AUTH_CD";

    public static final String AUTH_MNGR = Auth.MNGR.name();
    public static final String AUTH_USER = Auth.USER.name();
    public static final String AUTH_DEV = Auth.DEV.name();

    public static final String ROLE_MNGR = "ROLE_MNGR";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_DEV = "ROLE_DEV";

    /**
     * RememberMeParam
     */
    public static final String REMEMBER_ME_PARAM = "remember-me";


    public static final String SITE_MENU = "siteAcsInfo";

    /** URL_ENCODING */
    public static final Boolean URL_ENC_FALSE = false;

    /** 디바이스 정보 */
    public static String DVC_PC = "PC";
    public static String DVC_MOBILE = "MOBILE";
    public static String DVC_TABLET = "TABLET";

    /** IP 헤더 목록 */
    public static final String[] IP_HEADERS = { "X-FORWARDED-FOR", "Proxy-Client-IP",  "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR" };
    public static final String REMOTE_ADDR = "remoteAddr";
    public static final String REFERER = "referer";

    /**
     * 쿠키
     */
    public static final String ASIDE_MENU = "aside_menu";
    public static final String ASIDE_MENU_FIXED = "fixed";
    public static final String ASIDE_MENU_MINIMIZED = "minimized";

}
