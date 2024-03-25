package io.nicheblog.dreamdiary.web.model.board;

import io.nicheblog.dreamdiary.web.model.cmm.BaseSearchParam;
import lombok.*;

/**
 * BoardDefSearchParam
 * <pre>
 *  게시판 정의 검색 파라미터 Dto
 *  ※게시판 정의(board_def) = 게시판 분류. 게시판 게시물(board_post)을 1:N으로 관리한다.
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
public class BoardDefSearchParam extends BaseSearchParam {

    //

}
