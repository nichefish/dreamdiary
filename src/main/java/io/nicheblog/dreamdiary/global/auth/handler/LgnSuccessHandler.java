package io.nicheblog.dreamdiary.global.auth.handler;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.model.AuthInfo;
import io.nicheblog.dreamdiary.global.auth.service.AuthService;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * LgnSuccessHandler
 * <pre>
 *  Spring Security:: 로그인 성공시 처리 Handler
 *  "로그인 후 이전 페이지 이동" 기능 구현 위해 SavedRequestAwareAuthenticationSuccessHandler 상속
 * </pre>
 *
 * @author nichefish
 */
@Component
@Log4j2
public class LgnSuccessHandler
        extends SavedRequestAwareAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    @Resource(name = "authService")
    private AuthService authService;
    @Resource
    protected ApplicationEventPublisher publisher;
    @Resource
    private HttpSession session;

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication
    ) throws IOException, ServletException {

        request.removeAttribute(Constant.ERROR_MSG);
        request.removeAttribute("isCredentialExpired");
        request.removeAttribute("isDupIdLgn");
        request.removeAttribute("needsPwReset");

        // 사용자 정보 세션에 추가
        AuthInfo authInfo = (AuthInfo) authentication.getPrincipal();
        session.setAttribute("authInfo", authentication.getPrincipal());
        session.setAttribute("acsIp", AuthUtils.getAcsIpAddr());

        // 최종 로그인 날짜 세팅 및 패스워드오류 카운트 초기화
        String userId = authInfo.getUserId();
        authService.setLstLgnDt(userId);
        // session에 lgnId attribute 추가 :: 중복 로그인 방지 비교용
        DupIdLgnManager.addKey(userId);

        // 로그인 로그 남기기
        LogActvtyParam logParam = new LogActvtyParam(true, MessageUtils.getMessage(MessageUtils.RSLT_SUCCESS), ActvtyCtgr.LGN);
        publisher.publishEvent(new LogActvtyEvent(this, logParam));

        // 이전 페이지 :: 부재시 메인 페이지로 리다이렉트
        // 상속받은 상위 SavedRequestAwareAuthenticationSuccessHandler의 메소드 call
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
