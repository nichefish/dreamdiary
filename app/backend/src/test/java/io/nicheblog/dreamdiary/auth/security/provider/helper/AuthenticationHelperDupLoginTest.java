package io.nicheblog.dreamdiary.auth.security.provider.helper;

import io.nicheblog.dreamdiary.auth.policy.entity.AuthPolicyEntityTestFactory;
import io.nicheblog.dreamdiary.auth.policy.service.AuthPolicyQueryService;
import io.nicheblog.dreamdiary.auth.security.exception.DupIdLoginException;
import io.nicheblog.dreamdiary.auth.security.model.AuthInfo;
import io.nicheblog.dreamdiary.auth.security.service.manager.DupIdLoginManager;
import io.nicheblog.dreamdiary.auth.type.ClientChannel;
import io.nicheblog.dreamdiary.feature.user.account.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * AuthenticationHelperDupLoginTest
 * <pre>
 *  AuthenticationHelper 채널(WEB/MOBILE)별 중복 로그인 체크 테스트
 * </pre>
 *
 * @author nichefish
 */
class AuthenticationHelperDupLoginTest {

    private static final String USERNAME = "helper-dup-login-test-user";

    private AuthenticationHelper authenticationHelper;

    @BeforeEach
    void setUp() throws Exception {
        final UserService userService = mock(UserService.class);
        final AuthPolicyQueryService authPolicyQueryService = mock(AuthPolicyQueryService.class);
        when(userService.isDormant(USERNAME)).thenReturn(false);
        when(authPolicyQueryService.getDtlEntity()).thenReturn(AuthPolicyEntityTestFactory.create());
        authenticationHelper = new AuthenticationHelper(userService, authPolicyQueryService, mock(PasswordEncoder.class));
    }

    @AfterEach
    void tearDown() {
        DupIdLoginManager.removeKey(USERNAME, ClientChannel.WEB);
        DupIdLoginManager.removeKey(USERNAME, ClientChannel.MOBILE);
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void mobileLoginPassesWhileWebSessionExists() throws Exception {
        DupIdLoginManager.addKey(USERNAME, ClientChannel.WEB);
        bindRequest("MOBILE");

        assertThat(authenticationHelper.validateAuth(createAuthInfo())).isTrue();
    }

    @Test
    void webLoginPassesWhileMobileSessionExists() throws Exception {
        DupIdLoginManager.addKey(USERNAME, ClientChannel.MOBILE);
        bindRequest(null);

        assertThat(authenticationHelper.validateAuth(createAuthInfo())).isTrue();
    }

    @Test
    void secondWebLoginIsRejectedAsDuplicate() {
        DupIdLoginManager.addKey(USERNAME, ClientChannel.WEB);
        bindRequest(null);

        assertThatThrownBy(() -> authenticationHelper.validateAuth(createAuthInfo()))
                .isInstanceOf(DupIdLoginException.class);
    }

    @Test
    void secondMobileLoginIsRejectedAsDuplicate() {
        DupIdLoginManager.addKey(USERNAME, ClientChannel.MOBILE);
        bindRequest("mobile");

        assertThatThrownBy(() -> authenticationHelper.validateAuth(createAuthInfo()))
                .isInstanceOf(DupIdLoginException.class);
    }

    @Test
    void dupLoginConfirmIsAcceptedOnlyForSameChannel() {
        // 모바일에서 중복 로그인 확인 대기 상태
        final MockHttpSession session = new MockHttpSession();
        session.setAttribute("isDupIdLogin", USERNAME);
        session.setAttribute(ClientChannel.DUP_LOGIN_SESSION_ATTR, ClientChannel.MOBILE.name());

        // 다른 채널(WEB) 요청으로는 확인 처리되지 않음
        bindRequest(null, session);
        assertThat(authenticationHelper.isDupLoginConfirmed(USERNAME)).isFalse();

        // 같은 채널(MOBILE) 요청은 확인 처리
        session.setAttribute("isDupIdLogin", USERNAME);
        session.setAttribute(ClientChannel.DUP_LOGIN_SESSION_ATTR, ClientChannel.MOBILE.name());
        bindRequest("MOBILE", session);
        assertThat(authenticationHelper.isDupLoginConfirmed(USERNAME)).isTrue();
        assertThat(session.getAttribute("isDupIdLogin")).isNull();
    }

    @Test
    void dupLoginConfirmWithoutChannelKeepsLegacyBehavior() {
        final MockHttpSession session = new MockHttpSession();
        session.setAttribute("isDupIdLogin", USERNAME);
        bindRequest(null, session);

        assertThat(authenticationHelper.isDupLoginConfirmed(USERNAME)).isTrue();
    }

    private static void bindRequest(final String channelHeader) {
        bindRequest(channelHeader, null);
    }

    private static void bindRequest(final String channelHeader, final MockHttpSession session) {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        if (channelHeader != null) request.addHeader(ClientChannel.HEADER_NAME, channelHeader);
        if (session != null) request.setSession(session);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private static AuthInfo createAuthInfo() {
        return AuthInfo.builder()
                .username(USERNAME)
                .lockedYn("N")
                .useAllowedIpYn("N")
                .needsPasswordReset("N")
                .passwordChangedAt(LocalDateTime.now())
                .build();
    }
}
