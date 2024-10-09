package io.nicheblog.dreamdiary.domain.board.def.model;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseParam;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * BoardDefParam
 * <pre>
 *  일반게시판 정의 파라미터.
 *  ※일반게시판 정의(board_def) = 일반게시판 분류. 일반게시판 게시물(board_post)을 1:N으로 관리한다.
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
public class BoardDefParam extends BaseParam {

    /** 정렬순서 목록 */
    List<BoardDefDto> sortOrdr;
}
