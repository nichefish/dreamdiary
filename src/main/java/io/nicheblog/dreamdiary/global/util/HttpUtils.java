package io.nicheblog.dreamdiary.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class HttpUtils {

    private final HttpServletResponse autowiredResponse;

    private static HttpServletResponse response;

    /** static 맥락에서 사용할 수 있도록 bean 주입 */
    @PostConstruct
    private void init() {
        response = autowiredResponse;
    }

    /**
     * 브라우저의 캐시를 초기화합니다.
     *
     * @param response 응답 객체
     */
    public static void setInvalidateBrowserCacheHeader(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
    }
}
