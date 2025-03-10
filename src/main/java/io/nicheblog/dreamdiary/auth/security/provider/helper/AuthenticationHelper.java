package io.nicheblog.dreamdiary.auth.security.provider.helper;

import io.nicheblog.dreamdiary.auth.security.exception.*;
import io.nicheblog.dreamdiary.auth.security.model.AuthInfo;
import io.nicheblog.dreamdiary.auth.security.service.manager.DupIdLgnManager;
import io.nicheblog.dreamdiary.auth.security.util.AuthUtils;
import io.nicheblog.dreamdiary.domain.admin.lgnPolicy.entity.LgnPolicyEntity;
import io.nicheblog.dreamdiary.domain.admin.lgnPolicy.service.LgnPolicyService;
import io.nicheblog.dreamdiary.domain.user.info.service.UserService;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * AuthenticationHelper
 * <pre>
 *  Spring Security :: 사용자 인증 로직 분리
 *  비밀번호 체크 + 접속IP + 비밀번호 변경기간 체크기능 추가하여 구현
 * </pre>
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class AuthenticationHelper {

    private final UserService userService;
    private final LgnPolicyService lgnPolicyService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 주어진 인증 정보를 기반으로 사용자를 인증합니다.
     * 계정 기본 정보 유효성 검사
     *
     * @param authentication 인증 정보를 담고 있는 {@link Authentication} 객체
     * @param authInfo 인증된 사용자 정보
     * @return {@link Boolean} -- 인증 체크 성공 여부
     * @throws Exception 인증 과정 중 발생할 수 있는 예외
     */
    public Boolean validateAuth(final Authentication authentication, final AuthInfo authInfo) throws Exception {

        // 계정 존재여부 체크
        if (authentication == null || authInfo == null) throw new InternalAuthenticationServiceException(MessageUtils.getExceptionMsg("Exception"));

        // 중복 로그인 '확인'(기존 아이디 끊기) 후 들어왔을 시 바로 패스 :: 메소드 분리
        final String username = authentication.getName();
        if (this.isDupLgnConfirmed(username)) return true;

        // password 일치여부 체크
        final String password = (String) authentication.getCredentials();
        if (!passwordEncoder.matches(password, authInfo.getPassword())) throw new BadCredentialsException(MessageUtils.getExceptionMsg("BadCredentialsException"));

        authInfo.nullifyPasswordInfo();
        return this.validateAuth(authInfo);
    }

    /**
     * 주어진 인증 정보를 기반으로 사용자를 인증합니다. (중복 로그인, 패스워드 비교 제외)
     * 계정 상세 정보 유효성 검사
     *
     * @param authInfo 인증된 사용자 정보
     * @return {@link Boolean} -- 인증 체크 성공 여부
     * @throws Exception 인증 과정 중 발생할 수 있는 예외
     */
    public Boolean validateAuth(final AuthInfo authInfo) throws Exception {
        if (authInfo == null) throw new UsernameNotFoundException(MessageUtils.getExceptionMsg("UsernameNotFoundException"));

        final String username = authInfo.getUsername();

        // 승인여부 체크
        if (!"Y".equals(authInfo.getCfYn())) throw new AccountNotCfException(MessageUtils.getMessage("AbstractUserDetailsAuthenticationProvider.AccountNotCfException"));

        // 장기간 미로그인여부 체크 :: 시스템계정"system"은 제외
        if (userService.isDormant(username)) throw new AccountDormantException(MessageUtils.getMessage("AbstractUserDetailsAuthenticationProvider.AccountDormantException"));

        // 잠금여부 체크
        if ("Y".equals(authInfo.getLockedYn())) throw new LockedException(MessageUtils.getMessage("AbstractUserDetailsAuthenticationProvider.LockedException"));

        // 접속IP 체크 :: 메소드 분리
        if (!this.isAcsIpValid(authInfo)) throw new AcsIpNotAllowedException(MessageUtils.getMessage("AbstractUserDetailsAuthenticationProvider.AcsIpNotAllowedException"));

        // 비밀번호 만료 여부 체크
        if (!this.isPwExpryValid(authInfo)) throw new CredentialsExpiredException(MessageUtils.getMessage("AbstractUserDetailsAuthenticationProvider.CredentialsExpiredException"));

        // 비밀번호 변경 필요 여부 체크
        final boolean needsPwReset = "Y".equals(authInfo.getNeedsPwReset());
        if (needsPwReset) throw new AccountNeedsPwResetException(MessageUtils.getMessage("AbstractUserDetailsAuthenticationProvider.AccountNeedsPwResetException"));

        // 중복 로그인 체크 :: 세션 attribute 훑어서 "lgnId" 비교
        final boolean isDupLgn = DupIdLgnManager.isDupIdLgn(username);
        if (isDupLgn) throw new DupIdLgnException(MessageUtils.getMessage("AbstractUserDetailsAuthenticationProvider.DupIdLgnException"));

        return true;
    }

    /**
     * 중복 로그인 후 confirm 눌러 재접속 여부 :: 메소드 분리
     *
     * @param username 확인할 사용자 이름 (String)
     * @return {@link Boolean} -- 중복 로그인 후 재접근이 확인된 경우 true, 그렇지 않으면 false
     */
    public Boolean isDupLgnConfirmed(final String username) {
        if (StringUtils.isEmpty(username)) return false;

        final ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpSession session = servletRequestAttribute.getRequest().getSession(false);
        if (session == null) return false;

        final Object isDupIdLgn = session.getAttribute("isDupIdLgn");
        session.removeAttribute("isDupIdLgn");
        return isDupIdLgn instanceof String && username.equals(isDupIdLgn);
    }

    /**
     * 접속 IP 체크 :: 메소드 분리
     *
     * @param authInfo 사용자 인증 정보 (AuthInfo)
     * @return {@link Boolean} -- 접속 IP가 유효한 경우 true
     */
    public Boolean isAcsIpValid(final AuthInfo authInfo) {
        if (!"Y".equals(authInfo.getUseAcsIpYn())) return true;

        final List<String> acsIpStrList = authInfo.getAcsIpStrList();
        if (CollectionUtils.isEmpty(acsIpStrList)) return true;

        final String remoteAddr = AuthUtils.getAcsIpAddr();
        log.info("logged in remoteAddr: {}", remoteAddr);

        // 순회하며 IP 체크
        for (final String acsIp : acsIpStrList) {
            log.info("comparing remoteIP {} to access-allowed-IP {}...", remoteAddr, acsIp);
            final boolean isCidr = acsIp.contains("/");
            if (!isCidr) {
                // 단순 IP일 경우: 정확히 일치여부 확인
                if (acsIp.equals(remoteAddr)) return true;
            } else {
                // CIDR일 경우: 범위 체크
                final SubnetUtils subnetUtils = new SubnetUtils(acsIp);
                final boolean isIpAddrWithinValid = subnetUtils.getInfo().isInRange(remoteAddr);
                if (isIpAddrWithinValid) return true;
            }
        }
        return false;
    }

    /**
     * 비밀번호 만료 여부 체크 :: 메소드 분리
     *
     * @param authInfo 사용자 인증 정보 (AuthInfo)
     * @return {@link Boolean} -- 비밀번호가 만료되지 않은 경우 true
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    public Boolean isPwExpryValid(final AuthInfo authInfo) throws Exception {
        final LgnPolicyEntity lgnPolicy = lgnPolicyService.getDtlEntity();
        final Integer pwChgDy = lgnPolicy.getPwChgDy();
        final Date pwExprDt = DateUtils.getDateAddDay(authInfo.getPwChgDt(), pwChgDy);
        final boolean isPwExprd = (pwExprDt == null || pwExprDt.compareTo(DateUtils.getCurrDate()) < 0);
        return !isPwExprd;
    }
}
