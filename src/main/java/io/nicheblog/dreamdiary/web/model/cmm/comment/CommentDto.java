package io.nicheblog.dreamdiary.web.model.cmm.comment;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.web.model.board.BoardPostDto;
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
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class CommentDto
        extends BaseClsfDto {

    /** 필수: 컨텐츠 타입 */
    protected static final String CONTENT_TYPE = ContentType.COMMENT.key;
    /** 필수(Override): 글분류 코드 */
    protected static final String CTGR_CL_CD = CONTENT_TYPE + "_CTGR_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    protected String contentType = CONTENT_TYPE;

    /* ----- */

    /** 원글 번호 */
    protected Integer refPostNo;
    /** 원글 컨텐츠 타입 */
    protected String refContentType;

    /** 내용 */
    protected String cn;

    /* ----- */

    /**
     *
     */
    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ToString(callSuper = true)
    public static class DTL extends CommentDto {

    }

    /**
     *
     */
    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ToString(callSuper = true)
    public static class LIST extends CommentDto {
        //
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    public CommentCmpstn comment;
}
