package io.nicheblog.dreamdiary.web.model.cmm;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

/**
 * AjaxResponse
 * <pre>
 *  (공통) Ajax Response Body
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AjaxResponse {

    /** message */
    private String message;
    /** status */
    private Integer status;
    /** TODO: url to replace? */
    private String url;

    /** 결과 (성공여부) */
    private Boolean rslt;
    /** 결과 (목록) */
    private List<?> rsltList;
    /** 결과 (맵) */
    private HashMap<String, Object> rsltMap;
    /** 결과 (숫자) */
    private Integer rsltVal;
    /** 결과 (객체) */
    private Object rsltObj;
    /** 결과 (문자열) */
    private String rsltStr;

    /* ----- */

    /**
     * 생성자
     */
    public AjaxResponse(final Boolean rslt, final String message) {
        this.rslt = rslt;
        this.message = message;
        if (this.status == null) this.status = rslt ? 200 : 400;
    }

    /**
     * Ajax 결과 세팅
     */
    public void setAjaxResult(final Boolean rslt, final String message) {
        this.rslt = rslt;
        this.message = message;
        if (this.status == null) this.status = rslt ? 200 : 400;
    }
}
