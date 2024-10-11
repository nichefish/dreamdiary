package io.nicheblog.dreamdiary.domain._core.auth.provider;

import io.nicheblog.dreamdiary.domain._core.auth.exception.*;
import io.nicheblog.dreamdiary.domain._core.auth.service.DupIdLgnManager;
import io.nicheblog.dreamdiary.domain._core.auth.model.AuthInfo;
import io.nicheblog.dreamdiary.domain._core.auth.service.AuthService;
import io.nicheblog.dreamdiary.domain._core.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.domain.admin.lgnPolicy.entity.LgnPolicyEntity;
import io.nicheblog.dreamdiary.domain.admin.lgnPolicy.service.LgnPolicyService;
import io.nicheblog.dreamdiary.domain.user.info.service.UserService;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * DreamdiaryAuthenticationProvider
 * <pre>
 *  Spring Security :: 로그인 및 인증 처리.
 *  비밀번호 체크 + 접속IP + 비밀번호 변경기간 체크기능 추가하여 구현
 * </pre>
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class DreamdiaryAuthenticationProvider
        implements AuthenticationProvider {

    private final AuthService authService;
    private final UserService userService;
    private final LgnPolicyService lgnPolicyService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Spring Security :: 사용자 인증 과정
     *
     * @param authentication 인증 정보를 담고 있는 Authentication 객체
     * @return {@link Authentication} -- 인증된 사용자의 Authentication 객체
     * @throws AuthenticationException 처리 중 발생할 수 있는 예외
     */
    @SneakyThrows
    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = (String) authentication.getCredentials();
        final AuthInfo authInfo = authService.loadUserByUsername(username);

        // 계정 존재여부 체크
        if (authInfo == null) throw new InternalAuthenticationServiceException("internalAuthenticationServiceException");

        // 중복 로그인 '확인'(기존 아이디 끊기) 후 들어왔을 시 바로 패스 :: 메소드 분리
        if (this.isDupLgnConfirmed(username)) return new UsernamePasswordAuthenticationToken(authInfo, null, authInfo.getAuthorities());

        // password 일치여부 체크
        if (!passwordEncoder.matches(password, authInfo.getPassword())) throw new BadCredentialsException("BadCredentialsException");

        // 승인여부 체크
        if (!"Y".equals(authInfo.getCfYn())) throw new AcntNotCfException("AcntNotCfException");

        // 장기간 미로그인여부 체크 :: 시스템계정"system"은 제외
        if (userService.isDormant(username)) throw new AcntDormantException("DormantAcntException");

        // 잠금여부 체크
        if ("Y".equals(authInfo.getLockedYn())) throw new LockedException("LockedException");

        // 접속IP 체크 :: 메소드 분리
        if (!this.isAcsIpValid(authInfo)) throw new AcsIpNotAllowedException("허용되지 않은 IP입니다.");

        // 비밀번호 만료 여부 체크
        if (!this.isPwExpryValid(authInfo)) throw new CredentialsExpiredException("CredentialsExpiredException");

        // 비밀번호 변경 필요 여부 체크
        final boolean needsPwReset = "Y".equals(authInfo.getNeedsPwReset());
        if (needsPwReset) throw new AcntNeedsPwResetException("AcntNeedsPwResetException");

        // 중복 로그인 체크 :: 세션 attribute 훑어서 "lgnId" 비교
        final boolean isDupLgn = DupIdLgnManager.isDupIdLgn(username);
        if (isDupLgn) throw new DupIdLgnException("DupLgnException");

        return new UsernamePasswordAuthenticationToken(authInfo, null, authInfo.getAuthorities());
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

        String remoteAddr = AuthUtils.getAcsIpAddr();
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
    public Boolean isPwExpryValid(AuthInfo authInfo) throws Exception {
        final LgnPolicyEntity lgnPolicy = lgnPolicyService.getDtlEntity();
        final Integer pwChgDy = lgnPolicy.getPwChgDy();
        final Date pwExprDt = DateUtils.getDateAddDay(authInfo.getPwChgDt(), pwChgDy);
        final boolean isPwExprd = (pwExprDt == null || pwExprDt.compareTo(DateUtils.getCurrDate()) < 0);
        return !isPwExprd;
    }

    /**
     * 인증 제공자가 특정 인증 클래스 타입을 지원하는지 여부를 확인합니다.
     * -> UsernamePasswordAuthenticationToken만 처리합니다.
     *
     * @param authentication 검사할 인증 클래스 타입
     * @return {@link Boolean} -- 인증 제공자가 해당 인증 클래스 타입을 지원하는 경우 true
     */
    @Override
    public boolean supports(final Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
