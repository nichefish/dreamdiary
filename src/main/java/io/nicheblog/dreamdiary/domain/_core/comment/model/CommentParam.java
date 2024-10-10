package io.nicheblog.dreamdiary.domain._core.comment.model;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseParam;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * CommentSearchParam
 * <pre>
 *  댓글 검색 파라미터.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
@ToString(callSuper = true)
public class CommentParam
        extends BaseParam {
    //
}
