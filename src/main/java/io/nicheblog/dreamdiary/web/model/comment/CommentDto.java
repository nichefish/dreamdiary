package io.nicheblog.dreamdiary.web.model.comment;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * CommentDto
 * <pre>
 *  게시판 댓글 Dto
 *  ※게시판 댓글(board_comment) = 게시판 게시물(board_post)에 1:N으로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditDto
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class CommentDto
        extends BaseAtchDto {

    /**
     * 목록 순번
     */
    private Long rnum;

    /**
     * 댓글 ID
     */
    private Integer commentNo;
    /**
     * 글 번호
     */
    private Integer postNo;
    /**
     * 게시판분류코드
     */
    private String boardCd;
    /**
     * 댓글 내용
     */
    private String commentCn;

    /**
     * 성공여부
     */
    @Builder.Default
    Boolean isSuccess = false;

    /* ---- */

    /**
     * 복합키 객체 반환
     */
    public BaseClsfKey getPostKey() {
        return new BaseClsfKey(this.getPostNo(), this.getBoardCd());
    }
}
