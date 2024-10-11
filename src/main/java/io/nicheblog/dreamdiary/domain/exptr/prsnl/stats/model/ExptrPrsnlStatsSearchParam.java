package io.nicheblog.dreamdiary.domain.exptr.prsnl.stats.model;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.*;

/**
 * ExptrPrsnlStatsSearchParam
 * <pre>
 *  경비 관리 > 경비지출서 > 개인 경비 취합 검색 파라미터.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ExptrPrsnlStatsSearchParam
        extends BaseSearchParam {

    /** 년도 */
    private String yy;

    /** 등록자 ID */
    private String regstrId;
}
