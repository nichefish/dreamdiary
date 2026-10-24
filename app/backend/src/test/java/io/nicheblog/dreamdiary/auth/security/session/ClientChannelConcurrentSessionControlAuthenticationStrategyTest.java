package io.nicheblog.dreamdiary.auth.security.session;

import io.nicheblog.dreamdiary.auth.security.util.ClientChannelUtils;
import io.nicheblog.dreamdiary.auth.type.ClientChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionIdChangedEvent;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ClientChannelConcurrentSessionControlAuthenticationStrategyTest
 * <pre>
 *  채널(WEB/MOBILE)별 동시 세션 제어 테스트 (maximumSessions = 1)
 * </pre>
 *
 * @author nichefish
 */
class ClientChannelConcurrentSessionControlAuthenticationStrategyTest {

    private static final String PRINCIPAL = "concurrent-session-test-user";

    private ClientChannelSessionRegistry sessionRegistry;
    private ClientChannelConcurrentSessionControlAuthenticationStrategy strategy;
    private final Authentication authentication = new TestingAuthenticationToken(PRINCIPAL, null);

    @BeforeEach
    void setUp() {
        sessionRegistry = new ClientChannelSessionRegistry();
        strategy = new ClientChannelConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
        strategy.setMaximumSessions(1);
        strategy.setExceptionIfMaximumExceeded(false);
    }

    @Test
    void mobileLoginDoesNotExpireWebSession() {
        sessionRegistry.registerNewSession("web-1", PRINCIPAL, ClientChannel.WEB);

        strategy.onAuthentication(authentication, request("mobile-1", ClientChannel.MOBILE), new MockHttpServletResponse());

        assertThat(sessionRegistry.getSessionInformation("web-1").isExpired()).isFalse();
    }

    @Test
    void webLoginDoesNotExpireMobileSession() {
        sessionRegistry.registerNewSession("mobile-1", PRINCIPAL, ClientChannel.MOBILE);

        strategy.onAuthentication(authentication, request("web-1", ClientChannel.WEB), new MockHttpServletResponse());

        assertThat(sessionRegistry.getSessionInformation("mobile-1").isExpired()).isFalse();
    }

    @Test
    void secondWebLoginExpiresExistingWebSessionOnly() {
        sessionRegistry.registerNewSession("web-1", PRINCIPAL, ClientChannel.WEB);
        sessionRegistry.registerNewSession("mobile-1", PRINCIPAL, ClientChannel.MOBILE);

        strategy.onAuthentication(authentication, request("web-2", ClientChannel.WEB), new MockHttpServletResponse());

        assertThat(sessionRegistry.getSessionInformation("web-1").isExpired()).isTrue();
        assertThat(sessionRegistry.getSessionInformation("mobile-1").isExpired()).isFalse();
    }

    @Test
    void secondMobileLoginExpiresExistingMobileSessionOnly() {
        sessionRegistry.registerNewSession("web-1", PRINCIPAL, ClientChannel.WEB);
        sessionRegistry.registerNewSession("mobile-1", PRINCIPAL, ClientChannel.MOBILE);

        strategy.onAuthentication(authentication, request("mobile-2", ClientChannel.MOBILE), new MockHttpServletResponse());

        assertThat(sessionRegistry.getSessionInformation("mobile-1").isExpired()).isTrue();
        assertThat(sessionRegistry.getSessionInformation("web-1").isExpired()).isFalse();
    }

    @Test
    void alreadyRegisteredSessionIsNotExpired() {
        sessionRegistry.registerNewSession("web-1", PRINCIPAL, ClientChannel.WEB);

        strategy.onAuthentication(authentication, request("web-1", ClientChannel.WEB), new MockHttpServletResponse());

        assertThat(sessionRegistry.getSessionInformation("web-1").isExpired()).isFalse();
    }

    @Test
    void sessionIdChangeKeepsChannel() {
        sessionRegistry.registerNewSession("mobile-old", PRINCIPAL, ClientChannel.MOBILE);

        sessionRegistry.onApplicationEvent(new SessionIdChangedEvent(new Object()) {
            @Override
            public String getOldSessionId() {
                return "mobile-old";
            }

            @Override
            public String getNewSessionId() {
                return "mobile-new";
            }
        });

        assertThat(sessionRegistry.getSessionInformation("mobile-old")).isNull();
        assertThat(sessionRegistry.getClientChannel("mobile-new")).isEqualTo(ClientChannel.MOBILE);
    }

    @Test
    void removedSessionFallsBackToWeb() {
        sessionRegistry.registerNewSession("mobile-1", PRINCIPAL, ClientChannel.MOBILE);
        sessionRegistry.removeSessionInformation("mobile-1");

        assertThat(sessionRegistry.getClientChannel("mobile-1")).isEqualTo(ClientChannel.WEB);
    }

    private static MockHttpServletRequest request(final String sessionId, final ClientChannel channel) {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        if (channel == ClientChannel.MOBILE) request.addHeader(ClientChannel.HEADER_NAME, "MOBILE");
        final MockHttpSession session = new MockHttpSession(null, sessionId);
        ClientChannelUtils.setToSession(session, channel);
        request.setSession(session);
        return request;
    }
}
