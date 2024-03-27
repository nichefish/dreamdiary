package io.nicheblog.dreamdiary.web.model.dream;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DreamDayDto
 * <pre>
 *  꿈 일자 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditDto
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DreamDayDto
        extends BaseClsfDto {

    /** 꿈 일자 고유 번호 (PK) */
    private Integer dreamDayNo;

    /** 꿈 일자 */
    private String dreamtDt;

    /** 날짜미상여부 */
    @Builder.Default
    private String dtUnknownYn = "N";

    /** 년도 */
    private Integer yy;

    /** 월 */
    private Integer mnth;

    /** 대략일자 (날짜미상시 해당일자 이후에 표기) */
    private String aprxmtDt;
}
