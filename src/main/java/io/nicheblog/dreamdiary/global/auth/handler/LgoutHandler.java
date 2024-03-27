package io.nicheblog.dreamdiary.global.auth.handler;

import io.nicheblog.dreamdiary.global.auth.model.AuthInfo;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * LgoutHandler
 * <pre>
 *  Spring Security:: 로그아웃시 처리 Handler
 * </pre>
 *
 * @author nichefish
 */
@Component("lgoutHandler")
public class LgoutHandler
        implements LogoutHandler {

    @Resource
    protected ApplicationEventPublisher publisher;

    @Override
    public void logout(
            final HttpServletRequest request,
            final HttpServletResponse httpServletResponse,
            final Authentication authentication
    ) {

        // 로그 적재
        LogActvtyParam logParam = new LogActvtyParam(true);
        publisher.publishEvent(new LogActvtyEvent(this, logParam));
        // 중복 로그인 관리용 arrayList에서 로그인 아이디 제거
        if (authentication == null || authentication.getPrincipal() == null) return;
        DupIdLgnManager.removeKey(((AuthInfo) authentication.getPrincipal()).getUserId());
    }
}
