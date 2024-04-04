package io.nicheblog.dreamdiary.api.cmm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * BaseApiResponse
 * <pre>
 *  API:: 공통:: 응답 반환 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseApiRespns {

    /** result */
    @JsonProperty("result")
    private Boolean result;

    /** code */
    @JsonProperty("code")
    private Integer code;

    /** status */
    @JsonProperty("status")
    private Integer status;

    /** message */
    @JsonProperty("message")
    private String message;

    /* ----- */

    /**
     * 결과 세팅
     */
    public void setApiResult(
            final Boolean result,
            final String message
    ) {
        this.result = result;
        this.message = message;
        if (this.status == null) {
            this.status = result ? 200 : 100;
        }
    }
}
