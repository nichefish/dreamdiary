package io.nicheblog.dreamdiary.domain.vcatn.stats.model;

import lombok.*;

/**
 * VcatnStatsYyDto
 * <pre>
 *  휴가관리 > 년도별 휴가관리 년도 Dto.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class VcatnStatsYyDto {

    /** 년도 */
    private String statsYy;

    /** 시작일 */
    private String bgnDt;

    /** 종료일 */
    private String endDt;
}
