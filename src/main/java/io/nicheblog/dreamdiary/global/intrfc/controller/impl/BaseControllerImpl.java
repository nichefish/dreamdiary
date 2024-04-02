package io.nicheblog.dreamdiary.global.intrfc.controller.impl;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.intrfc.controller.BaseController;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * BaseControllerImpl
 * <pre>
 *  (공통/상속) 기본 Controller 구현체
 * </pre>
 *
 * @author nichefish
 * @implements BaseController
 */
public abstract class BaseControllerImpl
        implements BaseController {

    @Resource
    protected HttpSession session;
    @Resource
    protected HttpServletRequest request;
    @Resource
    protected HttpServletResponse response;

    @Resource
    protected ApplicationEventPublisher publisher;

    @Resource
    protected CacheManager cacheManager;

    @ModelAttribute("actvtyCtgrCd")
    public String addActvtyCtgrCd() {
        return getActvtyCtgr().name();
    }
    @ModelAttribute(Constant.LIST_URL)
    public String addBaseUrl() {
        return getBaseUrl();
    }
}
