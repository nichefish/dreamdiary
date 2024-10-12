package io.nicheblog.dreamdiary.domain.vcatn.stats.model;

import lombok.*;

import java.util.List;

/**
 * VcatnStatsTotalDto
 * <pre>
 *  휴가관리 > 년도별 휴가관리 일괄 집계 Dto.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class VcatnStatsTotalDto {

    /** 년도 */
    private String statsYy;
    /** 시작일 */
    private String bgnDt;
    /** 종료일 */
    private String endDt;

    /** 통계정보 목록 */
    List<VcatnStatsDto> statsList;
}
