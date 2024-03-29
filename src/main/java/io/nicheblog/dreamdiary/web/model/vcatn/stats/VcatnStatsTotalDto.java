package io.nicheblog.dreamdiary.web.model.vcatn.stats;

import lombok.*;

import java.util.List;

/**
 * VcatnStatsTotalDto
 * <pre>
 *  휴가계획서 통계 일괄 집계 Dto
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
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
