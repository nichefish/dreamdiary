package io.nicheblog.dreamdiary.global.auth.util;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.model.AuthInfo;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * AuthUtils
 * <pre>
 *  Spring Security:: 인증 및 권한 처리 관련 유틸리티 모듈
 * </pre>
 *
 * @author nichefish
 */
@Component
@Log4j2
public class AuthUtils {

    @Resource
    private HttpServletRequest reqst;

    private static HttpServletRequest request;

    /** static 맥락에서 사용할 수 있도록 bean 주입 */
    @PostConstruct
    private void init() {
        request = reqst;
    }

    /**
     * 현재 로그인 중인 사용자 정보 세션에서 조회해서 반환
     */
    public static AuthInfo getAuthenticatedUser() {
        if (!isAuthenticated()) return null;
        return (AuthInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
     * 익명 사용자(anynymousUser) 일 경우 false
     */
    public static Boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
        if (!isAuthenticated) log.info("isAuthenticated: {}", isAuthenticated);
        return isAuthenticated;
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

    /**
     * 사용자 IP주소 조회 (헤더 조회)
     */
    public static String getAcsIpAddr() {
        // request 맥락 하에서만 실행
        if (request == null) return null;
        String ipType = "";
        String ipAddr = "";
        for (String s : Constant.IP_HEADERS) {
            ipType = s;
            ipAddr = request.getHeader(ipType);
            if (ipAddr != null) break;
        }
        if (ipAddr == null) {
            ipType = Constant.REMOTE_ADDR;
            ipAddr = request.getRemoteAddr();
        }
        log.info("ipAddr > {}: {}", ipType, ipAddr);
        return ipAddr;
    }
}