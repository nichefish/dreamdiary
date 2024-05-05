package io.nicheblog.dreamdiary.web.model.jrnl.day;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.*;

/**
 * JrnlDaySearchParam
 * <pre>
 *  저널 일자 목록 검색 파라미터 Dto.
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
public class JrnlDaySearchParam
        extends BaseSearchParam {

    /** 년도 */
    private Integer yy;
    /** 제목 */
    private Integer mnth;

    /** 컨텐츠 타입 */
    private String contentType;
}
