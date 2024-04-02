package io.nicheblog.dreamdiary.global.handler;

import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * GlobalControllerAdvice
 * <pre>
 *  전체 Controller 공통 처리 클래스
 * </pre>
 *
 * @author nichefish
 */
@ControllerAdvice
@Log4j2
public class GlobalControllerAdvice {

    /** static 자원 releaseDate 설정 */
    @ModelAttribute("releaseDate")
    public String setCurrDate() throws Exception {
        return DateUtils.getCurrDateStr(DatePtn.PDATE);
    }
}
