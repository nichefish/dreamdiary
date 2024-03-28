package io.nicheblog.dreamdiary.web.model.log;

import io.nicheblog.dreamdiary.web.model.cmm.BaseSearchParam;
import lombok.*;

/**
 * LogStatsSearchParam
 * <pre>
 *  로그 목록 검색 파라미터 Dto
 * </pre>
 *
 * @author nichefish
 * @implements BaseSearchParam
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class LogStatsSearchParam
        extends BaseSearchParam {

    /**
     * 글분류 코드
     */
    private String ctgrCd;
    /**
     * 제목
     */
    private String postSj;


}
