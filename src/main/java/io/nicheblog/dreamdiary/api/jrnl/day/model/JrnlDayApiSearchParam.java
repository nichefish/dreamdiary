package io.nicheblog.dreamdiary.api.jrnl.day.model;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * JrnlDayApiSearchParam
 * <pre>
 *  API:: 저널 일자 검색 파라미터.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JrnlDayApiSearchParam
        extends BaseSearchParam {

    /** 글분류 코드 */
    private String ctgrCd;

    /** 제목 */
    private String title;
}
