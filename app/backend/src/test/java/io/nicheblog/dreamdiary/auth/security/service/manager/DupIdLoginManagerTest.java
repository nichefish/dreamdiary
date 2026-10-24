package io.nicheblog.dreamdiary.auth.security.service.manager;

import io.nicheblog.dreamdiary.auth.type.ClientChannel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DupIdLoginManagerTest
 * <pre>
 *  채널(WEB/MOBILE)별 중복 로그인 체크 테스트
 * </pre>
 *
 * @author nichefish
 */
class DupIdLoginManagerTest {

    private static final String USERNAME = "dup-login-test-user";

    @AfterEach
    void tearDown() {
        DupIdLoginManager.removeKey(USERNAME, ClientChannel.WEB);
        DupIdLoginManager.removeKey(USERNAME, ClientChannel.MOBILE);
    }

    @Test
    void webAndMobileLoginsCoexist() {
        DupIdLoginManager.addKey(USERNAME, ClientChannel.WEB);

        // 웹 로그인 상태에서 모바일 로그인은 중복이 아님
        assertThat(DupIdLoginManager.isDupIdLogin(USERNAME, ClientChannel.MOBILE)).isFalse();

        DupIdLoginManager.addKey(USERNAME, ClientChannel.MOBILE);

        // 두 채널 모두 로그인 상태 유지
        assertThat(DupIdLoginManager.isDupIdLogin(USERNAME, ClientChannel.WEB)).isTrue();
        assertThat(DupIdLoginManager.isDupIdLogin(USERNAME, ClientChannel.MOBILE)).isTrue();
    }

    @Test
    void secondWebLoginIsDuplicate() {
        DupIdLoginManager.addKey(USERNAME, ClientChannel.WEB);

        assertThat(DupIdLoginManager.isDupIdLogin(USERNAME, ClientChannel.WEB)).isTrue();
    }

    @Test
    void secondMobileLoginIsDuplicate() {
        DupIdLoginManager.addKey(USERNAME, ClientChannel.MOBILE);

        assertThat(DupIdLoginManager.isDupIdLogin(USERNAME, ClientChannel.MOBILE)).isTrue();
        assertThat(DupIdLoginManager.isDupIdLogin(USERNAME, ClientChannel.WEB)).isFalse();
    }

    @Test
    void nullChannelIsTreatedAsWeb() {
        DupIdLoginManager.addKey(USERNAME, null);

        assertThat(DupIdLoginManager.isDupIdLogin(USERNAME, ClientChannel.WEB)).isTrue();
        assertThat(DupIdLoginManager.isDupIdLogin(USERNAME, ClientChannel.MOBILE)).isFalse();
    }

    @Test
    void removeKeyOnlyAffectsSameChannel() {
        DupIdLoginManager.addKey(USERNAME, ClientChannel.WEB);
        DupIdLoginManager.addKey(USERNAME, ClientChannel.MOBILE);

        // 모바일 로그아웃/세션 만료는 웹 로그인 상태에 영향 없음
        DupIdLoginManager.removeKey(USERNAME, ClientChannel.MOBILE);

        assertThat(DupIdLoginManager.isDupIdLogin(USERNAME, ClientChannel.WEB)).isTrue();
        assertThat(DupIdLoginManager.isDupIdLogin(USERNAME, ClientChannel.MOBILE)).isFalse();
    }
}
