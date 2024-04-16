package io.nicheblog.dreamdiary.api.jrnl.day.model;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.web.model.jrnl.dream.JrnlDreamDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * JrnlDayApiDto
 * <pre>
 *  API:: 저널 일자 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseClsfDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class JrnlDayApiDto
        extends BaseClsfDto
        implements Identifiable<Integer> {

    /** 저널 일자 고유 번호 (PK) */
    private Integer jrnlDayNo;
    /** 저널 일자 */
    private String jrnlDt;
    /** 날짜미상여부 (Y/N) */
    @Builder.Default
    private String dtUnknownYn = "N";
    /** 년도 */
    private Integer yy;
    /** 월 */
    private Integer mnth;
    /** 대략일자 (날짜미상시 해당일자 이후에 표기) */
    private String aprxmtDt;
    /** 저널 꿈 목록 */
    private List<JrnlDreamDto> jrnlDreamList;

    @Override
    public Integer getKey() {
        return this.jrnlDayNo;
    }
}
