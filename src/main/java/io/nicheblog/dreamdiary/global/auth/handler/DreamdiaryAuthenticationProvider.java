package io.nicheblog.dreamdiary.global.auth.handler;

import io.nicheblog.dreamdiary.global.auth.exception.*;
import io.nicheblog.dreamdiary.global.auth.model.AuthInfo;
import io.nicheblog.dreamdiary.global.auth.service.AuthService;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.admin.LgnPolicyEntity;
import io.nicheblog.dreamdiary.web.service.admin.LgnPolicyService;
import io.nicheblog.dreamdiary.web.service.user.UserService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * IntranetAuthenticationProvider
 * <pre>
 *  로그인 및 인증 처리
 *  비밀번호 체크 + 접속IP + 비밀번호 변경기간 체크기능 추가하여 구현
 * </pre>
 *
 * @author nichefish
 */
@Log4j2
public class DreamdiaryAuthenticationProvider
        implements AuthenticationProvider {

    @Resource(name = "authService")
    private AuthService authService;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "lgnPolicyService")
    private LgnPolicyService lgnPolicyService;

    @Resource(name = "passwordEncoder")
    private PasswordEncoder passwordEncoder;

    /**
     * 사용자 인증 과정
     */
    @SneakyThrows
    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        AuthInfo authInfo = authService.loadUserByUsername(username);

        // 계정 존재여부 체크
        if (authInfo == null) throw new InternalAuthenticationServiceException("internalAuthenticationServiceException");

        // 중복 로그인 확인 후 들어왔을 시 바로 패스
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String isDupIdLgnProc = request.getParameter("isDupIdLgnProc");
        if ("Y".equals(isDupIdLgnProc)) {
            log.info("isDupIdLgnProc!!");
            return new UsernamePasswordAuthenticationToken(authInfo, null, authInfo.getAuthorities());
        }

        // password 일치여부 체크
        if (!passwordEncoder.matches(password, authInfo.getPassword())) throw new BadCredentialsException("BadCredentialsException");

        // 승인여부 체크
        if (!"Y".equals(authInfo.getCfYn())) throw new AcntNotCfException("AcntNotCfException");

        // 장기간 미로그인여부 체크 :: 시스템계정"system"은 제외
        if (userService.isDormant(username)) throw new AcntDormantException("DormantAcntException");

        // 잠금여부 체크
        if ("Y".equals(authInfo.getLockedYn())) throw new LockedException("LockedException");

        // 접속IP 체크
        if ("Y".equals(authInfo.getUseAcsIpYn())) {
            List<String> acsIpList = authInfo.getAcsIpList();
            if (CollectionUtils.isNotEmpty(acsIpList)) {
                String remoteAddr = authService.getUserIpAddr();
                log.info("logged in remoteAddr: {}", remoteAddr);

                boolean isAllowedIp = false;
                // CIDR 체크
                for (String acsIp : acsIpList) {
                    log.info("comparing remoteIP {} to access-allowed-IP {}...", remoteAddr, acsIp);
                    boolean isCidr = acsIp.contains("/");
                    if (!isCidr) {
                        if (acsIp.equals(remoteAddr)) {
                            isAllowedIp = true;
                            break;
                        }
                        continue;
                    }
                    SubnetUtils subnetUtils = new SubnetUtils(acsIp);
                    boolean isIpAddrValid = subnetUtils.getInfo().isInRange(remoteAddr);
                    if (isIpAddrValid) isAllowedIp = true;
                    break;
                }
                if (!isAllowedIp) throw new AcsIpNotAllowedException("허용되지 않은 IP입니다.");
            }
        }

        // 비밀번호 변경기간 만료여부 체크
        LgnPolicyEntity lgnPolicy = lgnPolicyService.getDtlEntity();
        Integer pwChgDy = lgnPolicy.getPwChgDy();
        Date pwExprDt = DateUtils.getDateAddDay(authInfo.getPwChgDt(), pwChgDy);
        boolean isPwExprd = (pwExprDt == null || pwExprDt.compareTo(DateUtils.getCurrDate()) < 0);
        if (isPwExprd) throw new CredentialsExpiredException("CredentialsExpiredException");

        // 비밀번호 / 2차 비밀번호 변경 필요 여부 체크
        boolean needsPwReset = "Y".equals(authInfo.getNeedsPwReset());
        if (needsPwReset) throw new AcntNeedsPwResetException("AcntNeedsPwResetException");

        // 중복 로그인 체크 :: 세션 attribute 훑어서 "lgnId" 비교
        boolean isDupLgn = DupIdLgnManager.isDupIdLgn(username);
        if (isDupLgn) throw new DupIdLgnException("DupLgnException");

        return new UsernamePasswordAuthenticationToken(authInfo, null, authInfo.getAuthorities());
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return true;
    }
}
