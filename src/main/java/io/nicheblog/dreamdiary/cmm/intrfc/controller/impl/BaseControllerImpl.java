package io.nicheblog.dreamdiary.cmm.intrfc.controller.impl;

import io.nicheblog.dreamdiary.cmm.intrfc.controller.BaseController;
import org.springframework.context.ApplicationEventPublisher;

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
public class BaseControllerImpl
        implements BaseController {

    @Resource
    protected HttpSession session;
    @Resource
    protected HttpServletRequest request;
    @Resource
    protected HttpServletResponse response;

    @Resource
    protected ApplicationEventPublisher publisher;
}
