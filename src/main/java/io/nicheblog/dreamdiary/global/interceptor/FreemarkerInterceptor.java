package io.nicheblog.dreamdiary.global.interceptor;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.model.AuthInfo;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.web.SiteUrl;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
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
@Component("freemarkerInterceptor")
@Log4j2
public class FreemarkerInterceptor
        implements HandlerInterceptor {

    //@Resource(name = "boardDefService")
    //private BoardDefService boardDefService;
//
    //@Resource(name = "menuService")
    //private MenuService menuService;

    @Resource
    private HttpSession session;

    @Value("${release-date:20000101}")
    private String releaseDate;

    /**
     * postHandle : controller 요청 처리 후 view를 렌더링하기 전에 동작한다.
     */
    @Override
    public void postHandle(
            final @NotNull HttpServletRequest request,
            final @NotNull HttpServletResponse response,
            final @NotNull Object handler,
            final ModelAndView mav
    ) {

        /* model 정보 없을시 처리하지 않음 */
        if (mav == null) return;

        // static 자원들에 releaseDate 세팅
        mav.addObject("releaseDate", releaseDate);

        /* 모바일 여부 체크 추가 (현재 미사용중) */
        // Boolean isMobile = DeviceUtils.getCurrentDevice(request).isMobile();
        // request.setAttribute(Constant.IS_MBL, isMobile);

        /* 권한 정보 모델에 추가 */
        if (AuthUtils.isAuthenticated()) {
            AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
            Boolean isMngr = authInfo.getIsMngr();
            mav.addObject("isMngr", isMngr);
            mav.addObject("isDev", authInfo.getIsDev());

            /* 사용자모드(사용자/관리자) 부재시 기본 사용자로 세팅 */
            String userMode = (String) session.getAttribute("userMode");
            if (StringUtils.isEmpty(userMode)) userMode = Constant.AUTH_USER;
            session.setAttribute("userMode", userMode);
            Boolean isMngrMode = Constant.AUTH_MNGR.equals(userMode);
            mav.addObject("isMngrMode", isMngrMode);
        }
        /* 게시판 목록 조회 */
        // Map<String, Object> searchParamMap = new HashMap<>() {{
        //     put("useYn", "Y");
        // }};
        // Page<BoardDefDto> boardDefList = boardDefService.getListDto(searchParamMap, Pageable.unpaged());
        // request.setAttribute("boardDefList", boardDefList.getContent());
//
        // // TODO: (사용자/관리자) 메뉴 정보 조회
        // Page<MenuListDto> menuList = isMngrMode ? menuService.getMngrMenuList() : menuService.getUserMenuList();
    }
}
