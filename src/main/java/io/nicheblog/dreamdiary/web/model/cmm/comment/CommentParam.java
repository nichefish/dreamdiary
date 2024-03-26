package io.nicheblog.dreamdiary.web.model.cmm.comment;

import io.nicheblog.dreamdiary.web.model.cmm.BaseParam;
import lombok.*;

/**
 * CommentSearchParam
 * <pre>
 *  게시판 댓글 검색 파라미터 Dto
 *  ※게시판 댓글(board_comment) = 게시판 게시물(board_post)에 1:N으로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BasePostSearchParam
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class CommentParam
        extends BaseParam {

    private String actvtyCtgr;

}
