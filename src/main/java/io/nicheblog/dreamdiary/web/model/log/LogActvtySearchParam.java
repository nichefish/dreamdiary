package io.nicheblog.dreamdiary.web.model.log;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * LogActvtySearchParam
 * <pre>
 *  활동 로그 목록 검색 파라미터 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseSearchParam
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class LogActvtySearchParam
        extends BaseSearchParam {

    /** 글분류 코드 */
    private String ctgrCd;
    /** 제목 */
    private String title;

}
