package io.nicheblog.dreamdiary.global.exception;

import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogAnonActvtyEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
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
@Component
public class AjaxSessionTimeoutFilter
        implements Filter {

    public FilterConfig filterConfig;

    @Resource
    protected ApplicationEventPublisher publisher;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    /**
     * Ajax 요청에 대하여 응답 설정 및 로깅 처리
     */
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
                // 로그 관련 처리
                LogActvtyParam logParam = new LogActvtyParam(false, MessageUtils.getExceptionMsg(e), ActvtyCtgr.DEFAULT);
                publisher.publishEvent(new LogAnonActvtyEvent(this, logParam));
            } catch (AccessDeniedException e) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN);        // 403
                // 로그 관련 처리
                LogActvtyParam logParam = new LogActvtyParam(false, MessageUtils.getExceptionMsg(e), ActvtyCtgr.DEFAULT);
                publisher.publishEvent(new LogAnonActvtyEvent(this, logParam));
            }
        } else {
            chain.doFilter(req, res);
        }
    }

    /**
     * AJAX 요청들에 대하여 헤더에 "AJAX" 수동 설정
     * (commons.js)
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