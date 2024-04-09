package io.nicheblog.dreamdiary.web.model.dream.piece;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstnModule;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstnModule;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;

/**
 * DreamPieceDto
 * <pre>
 *  꿈 일자 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BasePostDto
 * @implements CommentCmpstnModule, TagCmpstnModule
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DreamPieceDto
        extends BasePostDto
        implements Identifiable<Integer>, CommentCmpstnModule, TagCmpstnModule {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.DREAM_PIECE;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    protected String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 꿈 일자 번호 */
    private Integer dreamDayNo;
    /** 순번 */
    private Integer idx;

    /** 편집완료 여부 (Y/N) */
    @Builder.Default
    private String editComptYn = "N";
    /** 타인 꿈 여부 (Y/N) */
    @Builder.Default
    private String elseDreamYn = "N";
    /** 꿈꾼이(타인) 이름 */
    @Column(name = "ELSE_DREAMER_NM", length = 64)
    private String elseDreamerNm;

    @Override
    public Integer getKey() {
        return this.postNo;
    }

    /* ----- */

    public static class DTL extends DreamPieceDto {

    }

    public static class LIST extends DreamPieceDto {
        //
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    public CommentCmpstn comment;
    /** 태그 정보 모듈 (위임) */
    public TagCmpstn tag;
}
