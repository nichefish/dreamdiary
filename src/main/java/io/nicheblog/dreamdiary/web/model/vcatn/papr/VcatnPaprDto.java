package io.nicheblog.dreamdiary.web.model.vcatn.papr;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.*;
import io.nicheblog.dreamdiary.web.model.vcatn.schdul.VcatnSchdulDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * VcatnPaprDto
 * <pre>
 *  휴가계획서 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BasePostDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class VcatnPaprDto
        extends BasePostDto
        implements Identifiable<Integer>, CommentCmpstnModule, TagCmpstnModule, ManagtCmpstnModule, ViewerCmpstnModule {

    /** 필수: 컨텐츠 타입 */
    protected static final ContentType CONTENT_TYPE = ContentType.VCATN_PAPR;
    /** 필수(Override): 글분류 코드 */
    protected static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    protected String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 확인 여부 (Y/N) */
    protected String cfYn;

    /** 휴가 일정 리스트 */
    protected List<VcatnSchdulDto> schdulList;

    /* ----- */

    @Override
    public Integer getKey() {
        return this.postNo;
    }

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ToString
    public static class DTL extends VcatnPaprDto {
        //
    }

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ToString
    public static class LIST extends VcatnPaprDto {
        //
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    public CommentCmpstn comment;
    /** 태그 정보 모듈 (위임) */
    public TagCmpstn tag;
    /** 조치 정보 모듈 (위임) */
    public ManagtCmpstn managt;
    /** 열람자 정보 모듈 (위임) */
    public ViewerCmpstn viewer;
}
