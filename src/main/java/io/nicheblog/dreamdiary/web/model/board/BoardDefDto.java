package io.nicheblog.dreamdiary.web.model.board;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseManageDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * BoardDefDto
 * <pre>
 *  게시판 정의 정보 Dto
 *  ※게시판 정의(board_def) = 게시판 분류. 게시판 게시물(board_post)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BasePostDto
 */
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class BoardDefDto
        extends BaseManageDto {

    /**
     * 목록 순번
     */
    private Long rnum;

    /**
     * 게시판 코드
     */
    private String boardCd;

    /**
     * 게시판 이름
     */
    private String boardNm;

    /**
     * 메뉴번호
     */
    private String menuNo;

    /**
     * 글분류 분류 코드
     */
    private String ctgrClCd;
}