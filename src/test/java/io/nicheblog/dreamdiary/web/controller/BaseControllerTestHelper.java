package io.nicheblog.dreamdiary.web.controller;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;

/**
 * BaseControllerTestHelper
 * <pre>
 *  테스트 컨트롤러 기본속성 세팅 모듈
 * </pre>
 *
 * @author nichefish 
 */
@UtilityClass
@ActiveProfiles("test")
@Log4j2
public class BaseControllerTestHelper {

    /** 뷰 템플릿 파일 존재여부 체크 */
    public boolean viewExists(String viewName) {
        log.info("viewName: {}", viewName);
        String viewPath = "templates" + viewName + ".ftlh";
        File file = new File(viewPath);
        return file.exists();
    }
}
