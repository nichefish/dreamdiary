package io.nicheblog.dreamdiary.global.util;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * UrlUtils
 * <pre>
 *  URL 생성 및 관리 유틸리티 클래스
 * </pre>
 * TODO:
 *
 * @author nichefish
 */
@Component
public class UrlUtils {

    @Resource
    private HttpServletRequest req;
    @Resource
    private HttpServletResponse resp;

    private static HttpServletRequest request;
    private static HttpServletResponse response;

    /** static 맥락에서 사용할 수 있도록 bean 주입 */
    @PostConstruct
    private void init() {
        request = req;
        response = resp;
    }

    //
}
