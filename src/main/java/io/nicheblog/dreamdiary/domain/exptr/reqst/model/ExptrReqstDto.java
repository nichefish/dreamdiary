package io.nicheblog.dreamdiary.domain.exptr.reqst.model;

import io.nicheblog.dreamdiary.domain._clsf.comment.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.domain._clsf.comment.model.cmpstn.CommentCmpstnModule;
import io.nicheblog.dreamdiary.domain._clsf.managt.model.cmpstn.ManagtCmpstn;
import io.nicheblog.dreamdiary.domain._clsf.managt.model.cmpstn.ManagtCmpstnModule;
import io.nicheblog.dreamdiary.domain._clsf.tag.model.cmpstn.TagCmpstn;
import io.nicheblog.dreamdiary.domain._clsf.tag.model.cmpstn.TagCmpstnModule;
import io.nicheblog.dreamdiary.domain._clsf.viewer.model.cmpstn.ViewerCmpstn;
import io.nicheblog.dreamdiary.domain._clsf.viewer.model.cmpstn.ViewerCmpstnModule;
import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * ExptrReqstDto
 * <pre>
 *  경비 관리 > 물품구매/경조사비 신청 Dto.
 * </pre>
 *
 * @author nichefish
 * @extends BasePostDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode
public class ExptrReqstDto
        extends BasePostDto
        implements Identifiable<Integer>, CommentCmpstnModule, TagCmpstnModule, ManagtCmpstnModule, ViewerCmpstnModule {

    /** 필수: 컨텐츠 타입 */
    @Builder.Default
    private static final ContentType CONTENT_TYPE = ContentType.EXPTR_REQST;
    /** 필수(Override): 글분류 코드 */
    @Builder.Default
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    private String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 처리 여부 (Y/N/X) */
    @Builder.Default
    @Size(min = 1, max = 1)
    @Pattern(regexp = "^[YNX]$")
    private String cfYn = "N";

    /* ---- */

    /**
     * 내부 값들 합쳐서 풀 타이틀 반환
     */
    public String getFullTitle() {
        String title = this.title;
        if (StringUtils.isNotEmpty(this.ctgrNm)) title = "[" + this.ctgrNm + "] " + title;
        return title;
    }

    /* ----- */

    /**
     * 경비 관리 > 물품구매/경조사비 신청 상세 (DTL) Dto.
     */
    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class DTL
            extends ExptrReqstDto {
        //
    }

    /**
     * 경비 관리 > 물품구매/경조사비 신청 목록 조회 (LIST) Dto.
     */
    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class LIST
            extends ExptrReqstDto {
        //
    }

    /* ----- */

    @Override
    public Integer getKey() {
        return this.postNo;
    }

    /** 위임 :: 댓글 정보 모듈 */
    public CommentCmpstn comment;
    /** 위임 :: 태그 정보 모듈 */
    public TagCmpstn tag;
    /** 위임 :: 조치 정보 모듈 */
    public ManagtCmpstn managt;
    /** 위임 :: 열람 정보 모듈 */
    public ViewerCmpstn viewer;
}
