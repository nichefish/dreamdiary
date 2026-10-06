package io.nicheblog.dreamdiary.auth.security.session;

import io.nicheblog.dreamdiary.auth.security.util.ClientChannelUtils;
import io.nicheblog.dreamdiary.auth.type.ClientChannel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * ClientChannelConcurrentSessionControlAuthenticationStrategy
 * <pre>
 *  클라이언트 채널(WEB/MOBILE)별 동시 세션 제어 전략.
 *  기본 {@link ConcurrentSessionControlAuthenticationStrategy}와 동일하게 동작하되,
 *  최대 세션 수 비교/만료 대상을 "같은 채널의 세션"으로 한정한다.
 *  → 웹 로그인 1개 + 모바일 로그인 1개는 공존하고, 같은 채널의 두 번째 로그인만 기존 세션을 만료시킨다.
 * </pre>
 *
 * @author nichefish
 * @see ClientChannelSessionRegistry
 */
public class ClientChannelConcurrentSessionControlAuthenticationStrategy
        extends ConcurrentSessionControlAuthenticationStrategy {

    private final ClientChannelSessionRegistry sessionRegistry;

    /**
     * 생성자.
     *
     * @param sessionRegistry 채널 정보를 관리하는 SessionRegistry
     */
    public ClientChannelConcurrentSessionControlAuthenticationStrategy(final ClientChannelSessionRegistry sessionRegistry) {
        super(sessionRegistry);
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * 인증 시 같은 클라이언트 채널의 세션 수를 기준으로 최대 세션 수를 검사합니다.
     *
     * @param authentication 인증 정보
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     */
    @Override
    public void onAuthentication(
            final Authentication authentication,
            final HttpServletRequest request,
            final HttpServletResponse response
    ) {
        final int allowedSessions = this.getMaximumSessionsForThisUser(authentication);
        if (allowedSessions == -1) return;      // 무제한

        final ClientChannel channel = ClientChannelUtils.resolveForSession(request);
        final List<SessionInformation> sessions = sessionRegistry.getAllSessions(authentication.getPrincipal(), false, channel);
        final int sessionCount = sessions.size();
        if (sessionCount < allowedSessions) return;

        if (sessionCount == allowedSessions) {
            // 현재 세션이 이미 등록된 세션이면 통과
            final HttpSession session = request.getSession(false);
            if (session != null) {
                for (final SessionInformation sessionInformation : sessions) {
                    if (sessionInformation.getSessionId().equals(session.getId())) return;
                }
            }
        }

        // 같은 채널에서 가장 오래 사용되지 않은 세션 만료 처리 (maxSessionsPreventsLogin=true면 예외)
        this.allowableSessionsExceeded(sessions, allowedSessions, sessionRegistry);
    }
}
