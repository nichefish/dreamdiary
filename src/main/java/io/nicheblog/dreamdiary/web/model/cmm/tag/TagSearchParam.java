package io.nicheblog.dreamdiary.web.model.cmm.tag;

import lombok.*;

/**
 * BoardTagSearchParam
 * <pre>
 *  게시판 태그 검색 파라미터 Dto
 *  ※게시판 태그(board_tag) = 게시판별 태그 정보. 게시판 게시물 태그(board_post_tag)와 1:N으로 연관된다.
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
public class TagSearchParam {

    //

}
