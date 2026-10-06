package io.nicheblog.dreamdiary.auth.security.service.manager;

import io.nicheblog.dreamdiary.auth.security.handler.LoginSuccessHandler;
import io.nicheblog.dreamdiary.auth.type.ClientChannel;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DupIdLoginManager
 * <pre>
 *  중복 로그인 체크 관련 메모리 캐시 매니저
 *  로그인시 array에 username 저장, 로그아웃 및 세션만료시 username 제거
 *  클라이언트 채널(WEB/MOBILE)별로 분리 관리한다. (같은 계정의 웹 로그인 1개 + 모바일 로그인 1개 공존 허용)
 * </pre>
 *
 * @author nichefish
 * @see LoginSuccessHandler ,LogoutHandler,SessionDestroyListener
 */
@Log4j2
public class DupIdLoginManager
        implements HttpSessionListener {

    /** 로그인 아이디 목록을 담을 Map. (key: "채널:아이디") */
    private static final ConcurrentHashMap<String, Boolean> loginIdMap = new ConcurrentHashMap<>();

    /* ----- */

    /**
     * 같은 채널 내 중복 로그인 여부를 체크합니다.
     *
     * @param compareId 중복 여부를 확인할 사용자 ID (String)
     * @param channel 클라이언트 채널 (null이면 WEB)
     * @return {@link Boolean} -- 같은 채널에 이미 로그인된 경우 true, 그렇지 않으면 false
     */
    public synchronized static boolean isDupIdLogin(final String compareId, final ClientChannel channel) {
        if (compareId == null) return false;
        return loginIdMap.containsKey(toKey(compareId, channel));
    }

    /**
     * 사용자 ID를 채널별 중복 로그인 리스트(loginIdMap)에 추가합니다.
     *
     * @param loginId 추가할 사용자 ID (String)
     * @param channel 클라이언트 채널 (null이면 WEB)
     */
    public synchronized static void addKey(final String loginId, final ClientChannel channel) {
        if (loginId == null) return;
        loginIdMap.put(toKey(loginId, channel), true);
        log.info("username {} added for dupIdLoginMap. channel={}", loginId, toChannel(channel));
    }

    /**
     * 사용자 ID를 채널별 중복 로그인 리스트(loginIdMap)에서 제거합니다.
     *
     * @param compareId 제거할 사용자 ID (String)
     * @param channel 클라이언트 채널 (null이면 WEB)
     */
    public synchronized static void removeKey(final String compareId, final ClientChannel channel) {
        if (compareId == null) return;
        loginIdMap.remove(toKey(compareId, channel));
        log.info("username {} removed from dupIdLoginMap. channel={}", compareId, toChannel(channel));
    }

    /**
     * 채널별 Map key 생성 :: 메소드 분리
     *
     * @param loginId 사용자 ID
     * @param channel 클라이언트 채널
     * @return {@link String} -- "채널:아이디" 형식의 key
     */
    private static String toKey(final String loginId, final ClientChannel channel) {
        return toChannel(channel).name() + ":" + loginId;
    }

    /**
     * null 채널은 기본 채널(WEB)로 처리 :: 메소드 분리
     *
     * @param channel 클라이언트 채널
     * @return {@link ClientChannel} -- null이 아닌 클라이언트 채널
     */
    private static ClientChannel toChannel(final ClientChannel channel) {
        return channel == null ? ClientChannel.DEFAULT : channel;
    }
}
