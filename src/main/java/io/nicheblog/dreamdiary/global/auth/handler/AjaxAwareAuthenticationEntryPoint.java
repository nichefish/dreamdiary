package io.nicheblog.dreamdiary.global.auth.handler;

import io.nicheblog.dreamdiary.global.Url;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AjaxAwareAuthenticationEntryPoint
 * <pre>
 *  AJAX 요청에는 JSON 응답을 반환하도록 설정
 * </pre>
 *
 * @author nichefish
 */
@Component
@Log4j2
public class AjaxAwareAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        boolean isAjaxRequest = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        if (isAjaxRequest) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthenticated\"}");
        } else {
            // 현재 요청 URL을 추출
            String currentUrl = request.getRequestURI();
            // 현재 URL이 로그인 페이지 URL과 다른 경우 리디렉션
            if (!currentUrl.equals(Url.AUTH_LGN_FORM)) {
                response.sendRedirect(Url.AUTH_LGN_FORM);
            }
        }
    }
}