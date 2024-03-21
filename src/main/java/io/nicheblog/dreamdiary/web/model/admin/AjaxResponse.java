package io.nicheblog.dreamdiary.web.model.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AjaxResponse {

    /**
     * message
     */
    private String message;
    /**
     * status
     */
    private Integer status;
    /**
     * TODO: url to replace
     */
    private String url;
    /**
     * result
     */
    private Boolean result;
    /**
     * resultList (목록)
     */
    private List<?> resultList;
    /**
     * resultMap (맵)
     */
    private HashMap<String, Object> resultMap;
    /**
     * resultVal (숫자)
     */
    private Integer resultVal;
    /**
     * resultObj (객체)
     */
    private Object resultObj;
    /**
     * resultStr (문자열)
     */
    private String resultStr;

    /* ----- */

    /**
     * Ajax 결과 세팅
     */
    public void setAjaxResult(
            final Boolean result,
            final String message
    ) {
        this.result = result;
        this.message = message;
        if (this.status == null) this.status = result ? 200 : 400;
    }
}
