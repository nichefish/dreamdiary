package io.nicheblog.dreamdiary.web.model.cmm.comment;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * CommentSearchParam
 * <pre>
 *  댓글 검색 파라미터 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BasePostSearchParam
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class CommentSearchParam
        extends BaseSearchParam {

    /** 참조 글 번호 */
    private Integer refPostNo;
    /** 참조 컨텐츠 타입 */
    private String refContentType;
}
