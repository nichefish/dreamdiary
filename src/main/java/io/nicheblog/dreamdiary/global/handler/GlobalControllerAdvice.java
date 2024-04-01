package io.nicheblog.dreamdiary.global.handler;

import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.model.cmm.AjaxResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

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
