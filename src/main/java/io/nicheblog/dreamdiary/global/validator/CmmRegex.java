package io.nicheblog.dreamdiary.global.validator;

import lombok.experimental.UtilityClass;

/**
 * CmmRegex
 * <pre>
 *  공통 사용 정규식 정의
 * </pre>
 *
 * @author nichefish
 */
@UtilityClass
public final class CmmRegex {

    /**
     * 한글 정규식
     */
    public static final String KOR_REGEX = "(^[가-힣]+)$";

    /**
     * 특수문자 정규식
     */
    public static final String SPECIAL_CHAR_ALL_REGEX = "[^\\p{Alnum}\\p{IsHangul}]+";

    /**
     * ID 정규식
     */
    public static final String ID_REGEX = "^(?=.*[a-z])[a-z\\d]{5,16}$";

    /**
     * 패스워드 정규식
     */
    public static final String PW_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[$@$!%*#?&_])[a-zA-Z\\d$~@$!%*#?&_!]{9,20}$";
    /**
     * 패스워드 정규식 (대문자 포함)
     */
    public static final String PW_UPPER_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*#?&])[a-zA-Z\\d$~@$!%*#?&!]{9,20}$";
    /**
     * 2차 패스워드 정규식
     */
    public static final String SCSC_PW_REGEX = "^(?=.*[a-zA-Z])(?=.*[$~@!%*#?&])[a-zA-Z$~@!%*#?&]{5}$";

    /**
     * ipv4 cidr 정규식
     */
    public static final String CIDR_REGEX = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(\\/(\\d|[1-2]\\d|3[0-2]))?$";

    /**
     * HTML 태그 (<>...</>로 묶인 영역) 정규식
     */
    public static final String HTML_TAG_REGEX = "<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>";
}
