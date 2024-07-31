package io.nicheblog.dreamdiary.web.model.cmm.cn;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstnModule;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstnModule;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * SectnDto
 * <pre>
 *  단락 Dto
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
public class SectnDto
        extends BasePostDto
        implements Identifiable<Integer>, CommentCmpstnModule, TagCmpstnModule {

    /** 필수: 컨텐츠 타입 */
    private static final String CONTENT_TYPE = ContentType.SECTN.key;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE + "_CTGR_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    private String contentType = CONTENT_TYPE;

    /* ----- */

    /** 원글 번호 */
    private Integer refPostNo;
    /** 원글 컨텐츠 타입 */
    private String refContentType;

    @Override
    public Integer getKey() {
        return this.postNo;
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    public CommentCmpstn comment;
    /** 태그 정보 모듈 (위임) */
    public TagCmpstn tag;
}
