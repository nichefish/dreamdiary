package io.nicheblog.dreamdiary.web.model.board;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.*;

/**
 * BoardDefSearchParam
 * <pre>
 *  일반게시판 정의 검색 파라미터 Dto
 *  ※일반게시판 정의(board_def) = 일반게시판 분류. 일반게시판 게시물(board_post)을 1:N으로 관리한다.
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
