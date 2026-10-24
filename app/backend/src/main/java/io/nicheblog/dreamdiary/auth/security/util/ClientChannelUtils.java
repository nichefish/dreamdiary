package io.nicheblog.dreamdiary.auth.security.util;

import io.nicheblog.dreamdiary.auth.type.ClientChannel;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * ClientChannelUtils
 * <pre>
 *  로그인 클라이언트 채널(WEB/MOBILE) 판별 및 세션 저장 유틸리티.
 *  - 로그인 시점: 요청 헤더(X-Client-Channel) 기준으로 판별하여 세션에 저장
 *  - 로그아웃/세션 만료/동시 세션 제어 시점: 세션에 저장된 채널 우선, 없으면 요청 헤더 기준
 * </pre>
 *
 * @author nichefish
 */
@UtilityClass
@Log4j2
public class ClientChannelUtils {

    /**
     * 요청 헤더로 클라이언트 채널을 판별한다. (헤더 없음 = WEB)
     *
     * @param request HTTP 요청 객체
     * @return {@link ClientChannel} -- 판별된 클라이언트 채널
     */
    public static ClientChannel resolveFromHeader(final HttpServletRequest request) {
        if (request == null) return ClientChannel.DEFAULT;
        return ClientChannel.from(request.getHeader(ClientChannel.HEADER_NAME));
    }

    /**
     * 세션에 저장된 클라이언트 채널을 반환한다.
     *
     * @param session HTTP 세션
     * @return {@link ClientChannel} -- 세션에 저장된 채널, 없으면 null
     */
    public static ClientChannel getFromSession(final HttpSession session) {
        if (session == null) return null;
        try {
            return ClientChannel.fromAttribute(session.getAttribute(ClientChannel.SESSION_ATTR));
        } catch (final IllegalStateException e) {
            // 이미 무효화된 세션
            log.debug("Failed to read client channel from invalidated session.");
            return null;
        }
    }

    /**
     * 세션에 저장된 채널을 우선으로, 없으면 요청 헤더로 클라이언트 채널을 판별한다.
     *
     * @param request HTTP 요청 객체
     * @return {@link ClientChannel} -- 판별된 클라이언트 채널
     */
    public static ClientChannel resolveForSession(final HttpServletRequest request) {
        if (request == null) return ClientChannel.DEFAULT;
        final ClientChannel sessionChannel = getFromSession(request.getSession(false));
        return sessionChannel != null ? sessionChannel : resolveFromHeader(request);
    }

    /**
     * 현재 요청(RequestContextHolder) 헤더 기준으로 클라이언트 채널을 판별한다.
     * 요청 컨텍스트가 없으면 WEB을 반환한다.
     *
     * @return {@link ClientChannel} -- 판별된 클라이언트 채널
     */
    public static ClientChannel resolveCurrentFromHeader() {
        return resolveFromHeader(getCurrentRequest());
    }

    /**
     * 현재 요청(RequestContextHolder) 기준으로 세션 채널 우선, 없으면 헤더로 클라이언트 채널을 판별한다.
     * 요청 컨텍스트가 없으면 WEB을 반환한다.
     *
     * @return {@link ClientChannel} -- 판별된 클라이언트 채널
     */
    public static ClientChannel resolveCurrentForSession() {
        return resolveForSession(getCurrentRequest());
    }

    /**
     * 세션에 클라이언트 채널을 저장한다. (로그인 시점, 기존 값 덮어씀)
     *
     * @param session HTTP 세션
     * @param channel 저장할 클라이언트 채널
     */
    public static void setToSession(final HttpSession session, final ClientChannel channel) {
        if (session == null) return;
        session.setAttribute(ClientChannel.SESSION_ATTR, channel == null ? ClientChannel.DEFAULT.name() : channel.name());
    }

    /**
     * 세션에 클라이언트 채널이 없을 때만 요청 헤더 기준 채널을 저장한다.
     *
     * @param session HTTP 세션
     * @param request HTTP 요청 객체
     */
    public static void setToSessionIfAbsent(final HttpSession session, final HttpServletRequest request) {
        if (session == null || getFromSession(session) != null) return;
        setToSession(session, resolveFromHeader(request));
    }

    /**
     * 현재 요청 객체를 반환한다.
     *
     * @return {@link HttpServletRequest} -- 현재 요청 객체, 요청 컨텍스트가 없으면 null
     */
    private static HttpServletRequest getCurrentRequest() {
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) return null;
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }
}
