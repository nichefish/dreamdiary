package io.nicheblog.dreamdiary.web.model.jrnl.dream;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.*;

/**
 * JrnlDreamSearchParam
 * <pre>
 *  저널 꿈 목록 검색 파라미터 Dto.
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
public class JrnlDreamSearchParam
        extends BaseSearchParam {

    /** 년도 */
    private Integer yy;
    /** 월 */
    private Integer mnth;

    /** 컨텐츠 타입 */
    private String contentType;

    /** 꿈 키워드 */
    private String dreamKeyword;

    /** 태그 번호 */
    Integer tagNo;
}
