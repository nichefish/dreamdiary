package io.nicheblog.dreamdiary.global.cmm.auth.handler;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.auth.exception.AcntDormantException;
import io.nicheblog.dreamdiary.global.cmm.auth.exception.AcntNeedsPwResetException;
import io.nicheblog.dreamdiary.global.cmm.auth.exception.DupIdLgnException;
import io.nicheblog.dreamdiary.global.cmm.auth.service.AuthService;
import io.nicheblog.dreamdiary.global.cmm.log.service.LogService;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.entity.admin.LgnPolicyEntity;
import io.nicheblog.dreamdiary.web.service.admin.LgnPolicyService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * LgnFailureHandler
 * <pre>
 *  Spring Security:: 로그인 실패시 처리 Handler
 * </pre>
 *
 * @author nichefish
 */
@Component("lgnFailureHandler")
@Log4j2
public class LgnFailureHandler
        extends SimpleUrlAuthenticationFailureHandler
        implements AuthenticationFailureHandler {

    @Resource(name = "authService")
    private AuthService authService;

    @Resource(name = "lgnPolicyService")
    private LgnPolicyService lgnPolicyService;

    @Resource(name = "logService")
    private LogService logService;

    /**
     * 로그인 실패시 상황별 분기 처리
     */
    @SneakyThrows
    @Override
    public void onAuthenticationFailure(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException exception
    ) throws IOException, ServletException {

        request.removeAttribute("userId");
        request.removeAttribute("needsPwReset");
        String userId = request.getParameter("userId");
        String errorMsg = this.getLgnFailureMsg(exception);
        // 존재하지 않는 계정 제외하고 로그인 실패 로그 저장
        if (!(exception instanceof InternalAuthenticationServiceException) && !(exception instanceof DupIdLgnException)) {
            logService.logLgnFailReg(userId, errorMsg, false);        // 로그인 실패 로그 저장
        }
        // 비밀번호 불일치
        if (exception instanceof BadCredentialsException) {
            LgnPolicyEntity rsLgnPolicyEntity = lgnPolicyService.getLgnPolicyDtlEntity();
            Integer lgnTryLmt = rsLgnPolicyEntity.getLgnTryLmt();
            // 로그인 실패 횟수 처리
            Integer newLgnFailCnt = authService.applyLgnFailCnt(userId);
            if (newLgnFailCnt < lgnTryLmt) {
                errorMsg += "<br>" + MessageUtils.getMessage(MessageUtils.LGN_FAIL_BADCREDENTIALS_CNT, new Object[]{lgnTryLmt, newLgnFailCnt});
            } else {
                authService.lockAccount(userId);
                errorMsg += "<br>" + MessageUtils.getMessage(MessageUtils.LGN_FAIL_BADCREDENTIALS_LOCKED, new Object[]{newLgnFailCnt});
            }
            // 장기간 미로그인 잠금
        } else if (exception instanceof AcntDormantException) {
            authService.lockAccount(userId);        // 계정 잠금 처리
            // 비밀번호 변경기간 만료
        } else if (exception instanceof CredentialsExpiredException) {
            request.setAttribute("userId", userId);
            request.setAttribute("isCredentialExpired", true);
            // 중복 로그인 방지
        } else if (exception instanceof DupIdLgnException) {
            request.setAttribute("userId", userId);
            request.setAttribute("isDupIdLgn", true);
            // 패스워드 초기화 강제
        } else if (exception instanceof AcntNeedsPwResetException) {
            request.setAttribute("userId", userId);
            request.setAttribute("needsPwReset", true);
        }

        log.info("login attempt failed.. userId: {} errorMsg: {}", userId, errorMsg);
        request.setAttribute(Constant.ERROR_MSG, errorMsg);
        request.getRequestDispatcher(SiteUrl.AUTH_LGN_FORM)
               .forward(request, response);
    }

    /**
     * Spring Security Exception 이름으로 message 세팅해서 반환
     * messageBundle에 exception 클래스명으로 설정시 해당 에러메세지 반환
     */
    public String getLgnFailureMsg(final Exception e) {
        String exceptionNm = e.getClass()
                              .toString();
        exceptionNm = exceptionNm.substring(exceptionNm.lastIndexOf('.') + 1);
        return MessageUtils.getMessage("AbstractUserDetailsAuthenticationProvider." + exceptionNm);
    }
}