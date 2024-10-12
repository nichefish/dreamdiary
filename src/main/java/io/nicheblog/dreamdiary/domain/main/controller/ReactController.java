package io.nicheblog.dreamdiary.domain.main.controller;

import io.nicheblog.dreamdiary.domain._core.log.actvty.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.Url;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ReactController
 * <pre>
 *  React 페이지 컨트롤러.
 * </pre>
 *
 * @author nichefish
 */
@Controller
@Log4j2
public class ReactController {

    /**
     * 리액트React 메인
     *
     * @param logParam 로그 기록을 위한 파라미터 객체
     * @return {@link String} -- 화면 뷰 경로
     */
    @GetMapping(value = {Url.REACT_MAIN})
    public String getReactMain(
            final LogActvtyParam logParam
    ) {

        // TODO:

        return "redirect:/static/react/index.html";
    }
}
