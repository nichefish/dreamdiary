package io.nicheblog.dreamdiary.web.controller;

import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ReactController
 * <pre>
 *  React 페이지 컨트롤러
 * </pre>
 * TODO: 보완 예정
 *
 * @author nichefish
 * @extends BaseControllerImpl
 */
@Controller
@Log4j2
public class ReactController {

    /**
     * React 메인
     */
    @GetMapping(value = {Url.REACT_MAIN})
    public String getReactMain(
            final LogActvtyParam logParam
    ) {

        return "redirect:/static/react/index.html";
    }
}
