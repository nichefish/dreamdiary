package io.nicheblog.dreamdiary.web.model.dream.day;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstnModule;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstnModule;
import io.nicheblog.dreamdiary.web.model.dream.piece.DreamPieceDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * DreamDayDto
 * <pre>
 *  꿈 일자 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseClsfDto
 * @implements CommentCmpstnModule, TagCmpstnModule
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DreamDayDto
        extends BaseClsfDto
        implements Identifiable<Integer>, CommentCmpstnModule, TagCmpstnModule {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.DREAM_DAY;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    protected String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 꿈 일자 */
    private String dreamtDt;

    /** 날짜미상 여부 (Y/N) */
    @Builder.Default
    private String dtUnknownYn = "N";

    /** 년도 */
    private Integer yy;

    /** 월 */
    private Integer mnth;

    /** 대략일자 (날짜미상시 해당일자 이후에 표기) */
    private String aprxmtDt;

    /** 꿈 조각 목록 */
    private List<DreamPieceDto> dreamPieceList;

    @Override
    public Integer getKey() {
        return this.postNo;
    }

    /* ----- */

    public static class DTL extends DreamDayDto {

    }

    public static class LIST extends DreamDayDto {
        //
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    public CommentCmpstn comment;
    /** 태그 정보 모듈 (위임) */
    public TagCmpstn tag;
}
