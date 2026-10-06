package io.nicheblog.dreamdiary.auth.security.session;

import io.nicheblog.dreamdiary.auth.security.util.ClientChannelUtils;
import io.nicheblog.dreamdiary.auth.type.ClientChannel;
import org.springframework.security.core.session.AbstractSessionEvent;
import org.springframework.security.core.session.SessionIdChangedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistryImpl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * ClientChannelSessionRegistry
 * <pre>
 *  클라이언트 채널(WEB/MOBILE) 정보를 함께 관리하는 SessionRegistry.
 *  Spring Security 동시 세션 제어(maximumSessions)를 채널별로 적용하기 위해 세션ID별 채널을 기록한다.
 *  채널 정보가 없는 세션은 WEB으로 취급한다.
 * </pre>
 *
 * @author nichefish
 * @see ClientChannelConcurrentSessionControlAuthenticationStrategy
 */
public class ClientChannelSessionRegistry
        extends SessionRegistryImpl {

    /** 세션ID별 클라이언트 채널 */
    private final ConcurrentMap<String, ClientChannel> sessionChannels = new ConcurrentHashMap<>();

    /**
     * 신규 세션을 등록합니다. 채널은 현재 요청(세션 attribute 우선, 없으면 요청 헤더) 기준으로 판별합니다.
     *
     * @param sessionId 세션ID
     * @param principal 인증 주체
     */
    @Override
    public void registerNewSession(final String sessionId, final Object principal) {
        this.registerNewSession(sessionId, principal, ClientChannelUtils.resolveCurrentForSession());
    }

    /**
     * 신규 세션을 클라이언트 채널과 함께 등록합니다.
     *
     * @param sessionId 세션ID
     * @param principal 인증 주체
     * @param channel 클라이언트 채널 (null이면 WEB)
     */
    public void registerNewSession(final String sessionId, final Object principal, final ClientChannel channel) {
        super.registerNewSession(sessionId, principal);
        sessionChannels.put(sessionId, channel == null ? ClientChannel.DEFAULT : channel);
    }

    /**
     * 세션 정보를 제거합니다. (채널 정보 포함)
     *
     * @param sessionId 세션ID
     */
    @Override
    public void removeSessionInformation(final String sessionId) {
        super.removeSessionInformation(sessionId);
        if (sessionId != null) sessionChannels.remove(sessionId);
    }

    /**
     * 세션 이벤트를 처리합니다.
     * 세션ID 변경(세션 고정 보호 등) 시에는 기존 세션의 채널 정보를 새 세션ID로 이관합니다.
     *
     * @param event 세션 이벤트
     */
    @Override
    public void onApplicationEvent(final AbstractSessionEvent event) {
        if (!(event instanceof SessionIdChangedEvent)) {
            super.onApplicationEvent(event);
            return;
        }

        final SessionIdChangedEvent sessionIdChangedEvent = (SessionIdChangedEvent) event;
        final ClientChannel channel = sessionChannels.get(sessionIdChangedEvent.getOldSessionId());
        super.onApplicationEvent(event);
        final String newSessionId = sessionIdChangedEvent.getNewSessionId();
        if (channel != null && this.getSessionInformation(newSessionId) != null) sessionChannels.put(newSessionId, channel);
    }

    /**
     * 세션ID의 클라이언트 채널을 반환합니다.
     *
     * @param sessionId 세션ID
     * @return {@link ClientChannel} -- 세션의 클라이언트 채널, 정보가 없으면 WEB
     */
    public ClientChannel getClientChannel(final String sessionId) {
        if (sessionId == null) return ClientChannel.DEFAULT;
        return sessionChannels.getOrDefault(sessionId, ClientChannel.DEFAULT);
    }

    /**
     * 인증 주체의 세션 중 지정한 클라이언트 채널의 세션 목록을 반환합니다.
     *
     * @param principal 인증 주체
     * @param includeExpiredSessions 만료 세션 포함 여부
     * @param channel 클라이언트 채널 (null이면 WEB)
     * @return {@link List} -- 같은 채널의 세션 목록 (수정 가능한 리스트)
     */
    public List<SessionInformation> getAllSessions(final Object principal, final boolean includeExpiredSessions, final ClientChannel channel) {
        final ClientChannel targetChannel = channel == null ? ClientChannel.DEFAULT : channel;
        return this.getAllSessions(principal, includeExpiredSessions)
                .stream()
                .filter(sessionInformation -> this.getClientChannel(sessionInformation.getSessionId()) == targetChannel)
                .collect(Collectors.toList());
    }
}
