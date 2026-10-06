package io.nicheblog.dreamdiary.auth.security.handler;

import io.nicheblog.dreamdiary.auth.security.model.AuthInfo;
import io.nicheblog.dreamdiary.auth.security.service.manager.DupIdLoginManager;
import io.nicheblog.dreamdiary.auth.security.util.ClientChannelUtils;
import io.nicheblog.dreamdiary.auth.type.ClientChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
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
@RequiredArgsConstructor
@Log4j2
public class SessionDestroyListener
        implements ApplicationListener<SessionDestroyedEvent> {

    private final SimpMessagingTemplate messagingTemplate; // 메시지 전송을 위한 템플릿

    /**
     * 세션 종료 이벤트(SessionDestroyedEvent)가 발생했을 때 호출되는 메서드입니다.
     *
     * @param event 세션이 종료될 때 발생하는 SessionDestroyedEvent 객체
     */
    @Override
    public void onApplicationEvent(final SessionDestroyedEvent event) {
        final List<SecurityContext> lstSecurityContext = event.getSecurityContexts();
        // 종료되는 세션의 클라이언트 채널(WEB/MOBILE) :: 채널 정보가 없으면 WEB
        final ClientChannel channel = this.resolveChannel(event);
        for (final SecurityContext securityContext : lstSecurityContext) {
            // 중복 로그인 관리용 arrayList에서 로그인 아이디 제거 (같은 채널 키만)
            final Authentication authentication = securityContext.getAuthentication();
            final String username = ((AuthInfo) authentication.getPrincipal()).getUsername();
            DupIdLoginManager.removeKey(username, channel);

            // WebSocket 메시지로 세션 종료 알림 전송
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/session-invalid",
                    "Your session has expired, please log in again."
            );
            log.info("Session invalid notification sent. username={}", username);
        }
    }

    /**
     * 종료되는 세션에 저장된 클라이언트 채널을 반환한다. :: 메소드 분리
     *
     * @param event 세션 종료 이벤트
     * @return {@link ClientChannel} -- 세션의 클라이언트 채널, 확인할 수 없으면 WEB
     */
    private ClientChannel resolveChannel(final SessionDestroyedEvent event) {
        if (!(event instanceof HttpSessionDestroyedEvent)) return ClientChannel.DEFAULT;

        final ClientChannel channel = ClientChannelUtils.getFromSession(((HttpSessionDestroyedEvent) event).getSession());
        return channel != null ? channel : ClientChannel.DEFAULT;
    }
}
