package io.nicheblog.dreamdiary.domain.jrnl.sumry.model;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * JrnlSumryDto
 * <pre>
 *  저널 결산 Dto.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JrnlSumryDto
        extends BasePostDto
        implements Identifiable<Integer>, CommentCmpstnModule, TagCmpstnModule, SectnCmpstnModule {

    /** 필수: 컨텐츠 타입 */
    @Builder.Default
    private static final ContentType CONTENT_TYPE = ContentType.JRNL_SUMRY;
    /** 필수(Override): 글분류 코드 */
    @Builder.Default
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    private String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 결산 년도 */
    private Integer yy;
    /** 꿈 일수 */
    private Integer dreamDayCnt;
    /** 꿈 갯수 */
    private Integer dreamCnt;
    /** 일기 일수 */
    private Integer diaryDayCnt;

    /** 꿈 기록 완료 여부 (Y/N) */
    @Builder.Default
    @Size(min = 1, max = 1)
    @Pattern(regexp = "^[YN]$")
    private String dreamComptYn = "N";

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class DTL extends JrnlSumryDto {
        //
    }

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class LIST extends JrnlSumryDto {
        //
    }

    /* ----- */

    @Override
    public Integer getKey() {
        return this.postNo;
    }
    /** 위임 :: 댓글 정보 모듈 */
    public CommentCmpstn comment;
    /** 단락 정보 모듈 (위임) */
    public SectnCmpstn sectn;
    /** 위임 :: 태그 정보 모듈 */
    public TagCmpstn tag;
}
