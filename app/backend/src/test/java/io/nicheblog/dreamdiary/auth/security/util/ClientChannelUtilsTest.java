package io.nicheblog.dreamdiary.auth.security.util;

import io.nicheblog.dreamdiary.auth.type.ClientChannel;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ClientChannelUtilsTest
 * <pre>
 *  클라이언트 채널(WEB/MOBILE) 판별 테스트
 * </pre>
 *
 * @author nichefish
 */
class ClientChannelUtilsTest {

    @Test
    void mobileHeaderIsCaseInsensitive() {
        assertThat(ClientChannelUtils.resolveFromHeader(requestWithHeader("MOBILE"))).isEqualTo(ClientChannel.MOBILE);
        assertThat(ClientChannelUtils.resolveFromHeader(requestWithHeader("mobile"))).isEqualTo(ClientChannel.MOBILE);
        assertThat(ClientChannelUtils.resolveFromHeader(requestWithHeader(" Mobile "))).isEqualTo(ClientChannel.MOBILE);
    }

    @Test
    void missingOrUnknownHeaderIsWeb() {
        assertThat(ClientChannelUtils.resolveFromHeader(new MockHttpServletRequest())).isEqualTo(ClientChannel.WEB);
        assertThat(ClientChannelUtils.resolveFromHeader(requestWithHeader(""))).isEqualTo(ClientChannel.WEB);
        assertThat(ClientChannelUtils.resolveFromHeader(requestWithHeader("WEB"))).isEqualTo(ClientChannel.WEB);
        assertThat(ClientChannelUtils.resolveFromHeader(requestWithHeader("ios"))).isEqualTo(ClientChannel.WEB);
        assertThat(ClientChannelUtils.resolveFromHeader(null)).isEqualTo(ClientChannel.WEB);
    }

    @Test
    void sessionChannelTakesPrecedenceOverHeader() {
        final MockHttpServletRequest request = requestWithHeader("WEB");
        final MockHttpSession session = new MockHttpSession();
        ClientChannelUtils.setToSession(session, ClientChannel.MOBILE);
        request.setSession(session);

        assertThat(ClientChannelUtils.resolveForSession(request)).isEqualTo(ClientChannel.MOBILE);
    }

    @Test
    void setToSessionIfAbsentKeepsExistingChannel() {
        final MockHttpSession session = new MockHttpSession();
        ClientChannelUtils.setToSessionIfAbsent(session, requestWithHeader("MOBILE"));
        assertThat(ClientChannelUtils.getFromSession(session)).isEqualTo(ClientChannel.MOBILE);

        // 이미 채널이 저장된 세션은 덮어쓰지 않음
        ClientChannelUtils.setToSessionIfAbsent(session, new MockHttpServletRequest());
        assertThat(ClientChannelUtils.getFromSession(session)).isEqualTo(ClientChannel.MOBILE);
    }

    private static MockHttpServletRequest requestWithHeader(final String value) {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(ClientChannel.HEADER_NAME, value);
        return request;
    }
}
