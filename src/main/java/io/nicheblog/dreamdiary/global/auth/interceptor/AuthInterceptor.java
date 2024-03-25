package io.nicheblog.dreamdiary.global.auth.interceptor;

import io.nicheblog.dreamdiary.global.auth.model.AuthInfo;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * FreemarkerInterceptor
 * <pre>
 *  controller -> view로 가는 중간에 작용하는 인터셉터
 *  (프리마커 관련 외에도 분류가 애매한 기타 로직 뭉뚱그려 수행)
 * </pre>
 *
 * @author nichefish
 */
@Component("authInterceptor")
@Log4j2
public class AuthInterceptor
        implements HandlerInterceptor {

    @Resource
    private HttpSession session;

    /**
     * postHandle : controller 요청 처리 후 view를 렌더링하기 전에 동작한다.
     * true = 통과, false = 미통과
     */
    @Override
    public void postHandle(
            final @NotNull HttpServletRequest request,
            final @NotNull HttpServletResponse response,
            final @NotNull Object handler,
            final ModelAndView mav
    ) {

        /* 권한 정보 모델에 추가 */
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        if (AuthUtils.isAuthenticated() && mav != null) {
            mav.addObject("isMngr", authInfo.getIsMngr());
            mav.addObject("isDev", authInfo.getIsDev());
        }
    }
}
