package io.nicheblog.dreamdiary.web.model.vcatn.stats;

import lombok.*;

/**
 * VcatnStatsYyDto
 * <pre>
 *  휴가계획서 통계 년도 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class VcatnStatsYyDto {

    /**
     * 년도
     */
    private String statsYy;
    /**
     * 시작일
     */
    private String bgnDt;
    /**
     * 종료일
     */
    private String endDt;
}
