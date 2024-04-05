package io.nicheblog.dreamdiary.web.model.schdul;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.web.model.cmm.BaseCalDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;

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
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Log4j2
public class SchdulCalDto
        extends BaseCalDto {

    @PostConstruct
    public void init() throws Exception {
        this.className = this.initClassName();
        this.color = this.initColor();
        this.display = this.initDisplay();
        this.allDay = this.initAllDay();
    }
    public String initClassName() throws Exception {
        if (StringUtils.isNotEmpty(this.className)) return this.className;
        Constant.SchdulTy schdulTy = Constant.SchdulTy.valueOf(this.schdulCd);
        switch (schdulTy) {
            case HLDY:
            case CEREMONY:
                return "text-light";
            case TLCMMT:
                return "text-dark";
            case OUTDT:
            case INDT:
            case ETC:
                return "text-dark" + (!this.hasPassed() ? " blink" : "");
            default:
                return "fc-event-danger fc-event-solid-warning text-light";
        }
    }
    public String initColor() {
        Constant.SchdulTy schdulTy = Constant.SchdulTy.valueOf(this.schdulCd);
        switch (schdulTy) {
            case HLDY:
                return "red";
            case CEREMONY:
                return "#e8a8ff";
            case BRTHDY:
                return "purple";
            case TLCMMT:
                return "#d6edff";
            case OUTDT:
            case INDT:
            case ETC:
                // return "#ffed91";
                return "lightgray";
            case VCATN:
            default:
                return null;
        }
    }
    public String initDisplay() {
        Constant.SchdulTy schdulTy = Constant.SchdulTy.valueOf(this.schdulCd);
        if (schdulTy == Constant.SchdulTy.HLDY) return "background";
        return null;
    }
    public Boolean initAllDay() {
        Constant.SchdulTy schdulTy = Constant.SchdulTy.valueOf(this.schdulCd);
        switch (schdulTy) {
            case INDT:
            case OUTDT:
            case ETC:
                return false;
            case CEREMONY:
            case HLDY:
            case TLCMMT:
            default:
                return this.allDay;
        }
    }

    /** 일정 고유 번호 (PK) */
    @JsonProperty("id")
    private String schdulNo;
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
     * 생성자
     */
    public SchdulCalDto(
            final String title,
            final String datestr,
            final String schdulCd
    ) {
        this.title = title;
        this.bgnDt = datestr;
        this.endDt = datestr;
        this.schdulCd = schdulCd;
    }

    /**
     * schdulCd에 따른 Getter Override
     */
    public String getSchdulNm() throws Exception {
        String title = this.title;
        Constant.SchdulTy schdulTy = Constant.SchdulTy.valueOf(this.schdulCd);
        switch (schdulTy) {
            case TLCMMT:
                return this.prtcpnt + " 재택";
            case CEREMONY:
                return "\uD83D\uDC4F" + title;
            case HLDY:
            case VCATN:
            case BRTHDY:
                return title;
            default:
                boolean isPrvt = "Y".equals(this.prvtYn);
                if (isPrvt) title = "\uD83D\uDD07" + title;
                title += this.hasPassed() ? " \uD83D\uDDF8" : " ⋯";
                return title;
        }
    }


}
