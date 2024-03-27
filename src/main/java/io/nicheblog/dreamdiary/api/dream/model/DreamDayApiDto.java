package io.nicheblog.dreamdiary.api.dream.model;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * DreamDayApiDto
 * <pre>
 *  API:: 꿈 일자 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DreamDayApiDto
        extends BaseAtchDto {

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
