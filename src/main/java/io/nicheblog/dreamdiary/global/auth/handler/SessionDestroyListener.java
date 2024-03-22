package io.nicheblog.dreamdiary.global.auth.handler;

import io.nicheblog.dreamdiary.global.auth.model.AuthInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SessionDestroyListener
 * <pre>
 *  중복 로그인 체크 관련 session event(destroy) listener
 * </pre>
 *
 * @author nichefish
 */
@Component("sessionDestroyListener")
@Log4j2
public class SessionDestroyListener
        implements ApplicationListener<SessionDestroyedEvent> {

    @Override
    public void onApplicationEvent(final SessionDestroyedEvent event) {
        List<SecurityContext> lstSecurityContext = event.getSecurityContexts();
        for (SecurityContext securityContext : lstSecurityContext) {
            // 중복 로그인 관리용 arrayList에서 로그인 아이디 제거
            Authentication authentication = securityContext.getAuthentication();
            String userId = ((AuthInfo) authentication.getPrincipal()).getUserId();
            DupIdLgnManager.removeKey(userId);
        }
    }
}