package io.nicheblog.dreamdiary.web.model.schdul;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.model.cmm.BaseCalDto;
import lombok.*;
import lombok.extern.log4j.Log4j2;

/**
 * SchdulCalDto
 * <pre>
 *  전체 일정 Full-Calendar Dto
 *  (휴가 + 일정 등 통합)
 * </pre>
 *
 * @author nichefish
 * @see "https://fullcalendar.io/docs/event-object"
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Log4j2
public class SchdulCalDto
        extends BaseCalDto {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.SCHDUL;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    protected String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 일정 고유 번호 (PK) */
    @JsonProperty("id")
    private String postNo;
    /** 일정 그룹 ID */
    @JsonProperty("groupId")
    private String schdulCd;
    /** 일정 이름 */
    private String title;
    /** 일정 내용 */
    @JsonProperty("description")
    private String cn;

    /** 일정 시작일 */
    @JsonProperty("start")
    private String bgnDt;
    /** 일정 종료일 */
    @JsonProperty("end")
    private String endDt;

    /** 참여자 문자열 */
    private String prtcpnt;

    /** 개인일정 여부 */
    private String prvtYn;

    /* ----- */

    /**
     * 날짜 지났는지 여부 체크 (항목 override)
     */
    @JsonIgnore
    public Boolean hasPassed() throws Exception {
        return DateUtils.getCurrDate()
                .compareTo(DateUtils.asDate(this.endDt)) > 0;
    }
}
