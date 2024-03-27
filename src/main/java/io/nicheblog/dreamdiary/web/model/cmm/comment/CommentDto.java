package io.nicheblog.dreamdiary.web.model.cmm.comment;

import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostKey;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * CommentDto
 * <pre>
 *  댓글 Dto
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
        extends BaseClsfDto {

    /**
     * 목록 순번
     */
    private Long rnum;

    /**
     * 댓글 ID
     */
    private Integer postNo;
    /**
     * 참조 글 번호
     */
    private Integer refPostNo;
    /**
     * 참조 게시판 코드
     */
    private String refContentType;

    /**
     * 성공여부
     */
    @Builder.Default
    Boolean isSuccess = false;

    /* ---- */

    /**
     * 복합키 객체 반환
     */
    public BasePostKey getPostKey() {
        return new BasePostKey(this.getPostNo(), this.getBoardCd());
    }
}
