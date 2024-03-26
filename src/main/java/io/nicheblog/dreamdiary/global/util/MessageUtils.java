package io.nicheblog.dreamdiary.global.util;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * MessageUtil
 * <pre>
 *  메세지 처리 유틸리티 모듈
 *  "Spring Boot에서는 src/main/resources/messages.properties를 찾았을 때 자동으로 MessageSource 빈을 등록한다."
 * </pre>
 *
 * @author nichefish
 */
@Component("MessageUtil")
@Log4j2
public class MessageUtils
        extends ReloadableResourceBundleMessageSource
        implements MessageSource {

    @Resource(name = "messageSource")
    private MessageSource source;

    @Resource
    private HttpServletResponse resp;

    private static MessageSource messageSource;
    private static HttpServletResponse response;

    @PostConstruct
    private void init() {
        this.messageSource = source;
        this.response = resp;
    }

    public static final String RSLT_SUCCESS = "common.result.success";
    public static final String RSLT_FAILURE = "common.result.failure";
    public static final String RSLT_EXCEPTION = "common.result.exception";
    public static final String RSLT_EMPTY = "common.result.empty";

    public static final String RSLT_JANDI_SUCCESS = "commons.result.jandi.success";
    public static final String RSLT_JANDI_FAILURE = "commons.result.jandi.failure";

    public static final String RSLT_USER_ID_DUP = "userInfo.ipDupChck.result.idUnusable";
    public static final String RSLT_USER_ID_NOT_DUP = "userInfo.ipDupChck.result.idUsable";
    public static final String RSLT_SUCCESS_PW_RESET = "userInfo.resetPw.result.success";

    public static final String LGN_FAIL_BADCREDENTIALS_CNT = "AbstractUserDetailsAuthenticationProvider.badCredentials.failCnt";
    public static final String LGN_FAIL_BADCREDENTIALS_LOCKED = "AbstractUserDetailsAuthenticationProvider.badCredentials.locked";

    public static final String NOT_DELABLE_OWN_ID = "본인 아이디는 삭제할 수 없습니다.";
    public static final String PW_MISMATCH = "비밀번호가 일치하지 않습니다.";

    /**
     * 코드로 사전 정의된 메세지 조회
     */
    public static String getMessage(final String code) throws NoSuchMessageException {
        String msg = messageSource.getMessage(code, null, Locale.getDefault());
        log.info("code: {}, msg: {}", code, msg);
        return msg;
    }

    /**
     * 코드로 사전 정의된 메세지 조회
     */
    public static String getMessage(
            final String code,
            final @Nullable Object[] args
    ) throws NoSuchMessageException {
        String msg = messageSource.getMessage(code, args, Locale.getDefault());
        log.info("code: {}, msg: {}", code, msg);
        return msg;
    }

    /**
     * Javascript로 alert 처리
     */
    public static void alertMessage(final String msg) throws IOException {
        alertMessage(msg, null);
    }

    /**
     * Response에 Javascript alert 처리 및 리다이렉트
     */
    public static void alertMessage(
            final String msg,
            final String url
    ) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<script language=\"JavaScript\" type=\"text/JavaScript\">");
            out.println("let hasSwal = (typeof Swal !== \"undefined\");");
            if (url != null) {
                out.println("if (hasSwal) { Swal.fire({\"text\": `" + msg + "`}).then(location.replace('" + url + "')); }");
                out.println("else { alert(`" + msg + "`); location.replace('" + url + "'); }");
            } else {
                out.println("if (hasSwal) { Swal.fire({\"text\": `" + msg + "`}); }");
                out.println("else { alert(`" + msg + "`); }");
            }
            out.println("</script>");
        } catch (IOException e) {
            log.info(getExceptionMsg(e));
            response.sendRedirect("/");
        }
    }

    /**
     * 공통 > Exception 클래스를 받아서 해당 message를 세팅해서 반환
     * messageBundle에 exception 클래스명으로 설정시 해당 에러메세지를 반환한다.
     */
    public static String getExceptionMsg(final Exception e) {
        if (StringUtils.isNotEmpty(e.getMessage())) return e.getMessage();
        String exceptionNm = getExceptionNm(e);
        String resultMsg = getMessage(RSLT_EXCEPTION + "." + exceptionNm);
        log.info("exceptionNm: {}, resultMsg: {}", exceptionNm, resultMsg);
        log.error(e);
        return resultMsg;
    }

    public static String getExceptionNm(final Exception e) {
        String exceptionNm = e.getClass()
                              .toString();
        return exceptionNm.substring(exceptionNm.lastIndexOf('.') + 1);
    }
}