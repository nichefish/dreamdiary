package io.nicheblog.dreamdiary.web.model.cmm.comment;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
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
@SuperBuilder(toBuilder=true)
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

    /** 원글 번호 */
    protected Integer refPostNo;
    /** 원글 컨텐츠 타입 */
    protected String refContentType;

    /** 내용 */
    protected String cn;

    /** 성공여부 */
    @Builder.Default
    protected Boolean isSuccess = false;

    /* ---- */

    /**
     * 복합키 객체 반환
     */
    public BaseClsfKey getClsfKey() {
        return new BaseClsfKey(this.postNo, this.contentType);
    }
}
