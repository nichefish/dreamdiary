package io.nicheblog.dreamdiary.domain._core.auth.handler;

import io.nicheblog.dreamdiary.domain._core.auth.model.AuthInfo;
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
 *  중복 로그인 체크 관련 session event(destroy) listener.
 * </pre>
 *
 * @author nichefish
 */
@Component
@Log4j2
public class SessionDestroyListener
        implements ApplicationListener<SessionDestroyedEvent> {

    /**
     * 세션 종료 이벤트(SessionDestroyedEvent)가 발생했을 때 호출되는 메서드입니다.
     *
     * @param event 세션이 종료될 때 발생하는 SessionDestroyedEvent 객체
     */
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