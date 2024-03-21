package io.nicheblog.dreamdiary.global.cmm.auth.handler;

import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * DupIdLgnManager
 * <pre>
 *  중복 로그인 체크 관련 메모리 캐시 매니저
 *  로그인시 array에 userId 저장, 로그아웃 및 세션만료시 userId 제거
 * </pre>
 *
 * @author nichefish
 */
@Log4j2
public class DupIdLgnManager
        implements HttpSessionListener {

    /**
     * lgnIdList
     */
    private static final List<String> lgnIdList = new ArrayList<>();

    /**
     * 중복 로그인 여부 체크
     */
    public synchronized static boolean isDupIdLgn(final String compareId) {
        return lgnIdList.contains(compareId);
    }

    /**
     * lgnIdList에 id 추가
     */
    public synchronized static void addKey(final String lgnId) {
        lgnIdList.add(lgnId);
        log.info("userId {} added for dupIdLgnArray.", lgnId);
    }

    /**
     * lgnIdList에서 id 제거
     */
    public synchronized static void removeKey(final String compareId) {
        lgnIdList.remove(compareId);
        log.info("userId {} removed from dupIdLgnArray.", compareId);
    }
}