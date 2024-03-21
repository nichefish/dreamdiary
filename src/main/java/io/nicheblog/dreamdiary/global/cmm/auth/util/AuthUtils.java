package io.nicheblog.dreamdiary.global.cmm.auth.util;

import io.nicheblog.dreamdiary.global.cmm.auth.model.AuthInfo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * BaseAuthUtils
 * <pre>
 *  Spring Security:: 인증 및 권한 처리 관련 서비스 모듈
 *  Spring Security 컨텍스트 내에서 인증정보 이용시 해당 객체 상속하여 구현
 * </pre>
 *
 * @author nichefish
 */
@Log4j2
public class AuthUtils {

    /**
     * 현재 로그인 중인 사용자 정보 세션에서 조회해서 반환
     */
    public static AuthInfo getAuthenticatedUser() {
        if (RequestContextHolder.getRequestAttributes() == null) return null;
        return (AuthInfo) RequestContextHolder.getRequestAttributes()
                                                  .getAttribute("authInfo", RequestAttributes.SCOPE_SESSION);
    }

    /**
     * 현재 로그인 중인 사용자 이름 반환
     */
    public static String getLgnUserNm() {
        AuthInfo AuthInfo = getAuthenticatedUser();
        assert AuthInfo != null;
        return AuthInfo.getNickNm();
    }

    /**
     * 현재 로그인 중인 사용자 아이디 반환
     */
    public static String getLgnUserId() {
        AuthInfo authInfo = getAuthenticatedUser();
        assert authInfo != null;
        return authInfo.getUserId();
    }

    /**
     * 현재 사용자 인증여부 세션에서 조회해서 반환
     */
    public static Boolean isAuthenticated() {
        if (RequestContextHolder.getRequestAttributes() == null) return false;
        return (RequestContextHolder.getRequestAttributes()
                                    .getAttribute("authInfo", RequestAttributes.SCOPE_SESSION) != null);
    }

    /**
     * 공통 > 내 정보 여부 체크
     */
    public static Boolean isMyInfo(final String paramUserId) {
        if (paramUserId == null) return false;
        AuthInfo authInfo = getAuthenticatedUser();
        assert authInfo != null;
        String myUserId = authInfo.getUserId();
        return paramUserId.equals(myUserId);
    }

    /**
     * 공통 > 등록자 여부 체크
     */
    public static Boolean isRegstr(final String regstrId) {
        if (StringUtils.isEmpty(regstrId)) return false;
        AuthInfo authInfo = getAuthenticatedUser();
        if (authInfo == null) return false;
        String myUserId = authInfo.getUserId();
        return regstrId.equals(myUserId);
    }

    /**
     * 공통 > 수정자 여부 체크
     */
    public static Boolean isMdfusr(final String mdfusrId) {
        return isRegstr(mdfusrId);
    }

    /**
     * 공통 > 특정 권한 보유 여부
     */
    public static Boolean hasAuthority(final String roleStr) {
        AuthInfo authInfo = getAuthenticatedUser();
        assert authInfo != null;
        for (GrantedAuthority grantedAuthority : authInfo.getAuthorities()) {
            if (roleStr.equals(grantedAuthority.getAuthority())) return true;
        }
        return false;
    }
}