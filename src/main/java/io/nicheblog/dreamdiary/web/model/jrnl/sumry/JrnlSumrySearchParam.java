package io.nicheblog.dreamdiary.web.model.jrnl.sumry;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.*;

/**
 * JrnlSumrySearchParam
 * <pre>
 *  저널 결산 목록 검색 파라미터 Dto.
 * </pre>
 *
 * @author nichefish
 * @extends BaseSearchParam
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class JrnlSumrySearchParam
        extends BaseSearchParam {

    /** 년도 */
    private Integer yy;
    /** 월 */
    private Integer mnth;
}
