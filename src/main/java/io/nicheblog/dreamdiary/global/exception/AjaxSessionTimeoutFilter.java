package io.nicheblog.dreamdiary.global.exception;

import io.nicheblog.dreamdiary.global.cmm.log.service.LogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AjaxSessionTimeoutFilter
 * <pre>
 *  Ajax 인증만료시 클라이언트에서 식별 후 로그인 페이지 이동 처리
 * </pre>
 *
 * @author nichefish
 */
@Component("ajaxSessionTimeoutFilter")
public class AjaxSessionTimeoutFilter
        implements Filter {

    @Resource(name = "logService")
    private LogService logActvtyService;

    public FilterConfig filterConfig;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(
            final ServletRequest request,
            final ServletResponse response,
            final FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if (isAjaxRequest(req)) {
            try {
                chain.doFilter(req, res);
            } catch (AuthenticationException e) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED);     // 401
                logActvtyService.logLgnFailReg("", "", false);        // 접근 실패 로그 저장
            } catch (AccessDeniedException e) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN);        // 403
                logActvtyService.logLgnFailReg("", "", false);        // 접근 실패 로그 저장
            }
        } else {
            chain.doFilter(req, res);
        }
    }

    /**
     * 헤더에 "AJAX"
     */
    private boolean isAjaxRequest(final HttpServletRequest req) {
        String header = req.getHeader("AJAX");
        if (StringUtils.isEmpty(header)) return false;
        return header.equals(Boolean.TRUE.toString());
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }
}