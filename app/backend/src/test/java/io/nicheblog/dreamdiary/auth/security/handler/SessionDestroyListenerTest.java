package io.nicheblog.dreamdiary.auth.security.handler;

import io.nicheblog.dreamdiary.auth.security.model.AuthInfo;
import io.nicheblog.dreamdiary.auth.security.service.manager.DupIdLoginManager;
import io.nicheblog.dreamdiary.auth.security.util.ClientChannelUtils;
import io.nicheblog.dreamdiary.auth.type.ClientChannel;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SessionDestroyListenerTest {

    @Test
    void sendsSessionInvalidNotificationOnlyToDestroyedSessionUser() {
        final SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        final SessionDestroyListener listener = new SessionDestroyListener(messagingTemplate);
        final SessionDestroyedEvent event = mock(SessionDestroyedEvent.class);
        final SecurityContext securityContext = mock(SecurityContext.class);
        final Authentication authentication = mock(Authentication.class);
        final AuthInfo authInfo = mock(AuthInfo.class);

        when(event.getSecurityContexts()).thenReturn(List.of(securityContext));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(authInfo);
        when(authInfo.getUsername()).thenReturn("target-user");

        listener.onApplicationEvent(event);

        verify(messagingTemplate).convertAndSendToUser(
                "target-user",
                "/queue/session-invalid",
                "Your session has expired, please log in again."
        );
    }

    @Test
    void removesDupLoginKeyOnlyForDestroyedSessionChannel() {
        final String username = "channel-destroy-user";
        final SessionDestroyListener listener = new SessionDestroyListener(mock(SimpMessagingTemplate.class));
        final AuthInfo authInfo = AuthInfo.builder().username(username).build();
        final MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                new SecurityContextImpl(new UsernamePasswordAuthenticationToken(authInfo, null))
        );
        ClientChannelUtils.setToSession(session, ClientChannel.MOBILE);

        DupIdLoginManager.addKey(username, ClientChannel.WEB);
        DupIdLoginManager.addKey(username, ClientChannel.MOBILE);
        try {
            listener.onApplicationEvent(new HttpSessionDestroyedEvent(session));

            // 모바일 세션 종료는 웹 로그인 키에 영향 없음
            assertThat(DupIdLoginManager.isDupIdLogin(username, ClientChannel.MOBILE)).isFalse();
            assertThat(DupIdLoginManager.isDupIdLogin(username, ClientChannel.WEB)).isTrue();
        } finally {
            DupIdLoginManager.removeKey(username, ClientChannel.WEB);
            DupIdLoginManager.removeKey(username, ClientChannel.MOBILE);
        }
    }
}
