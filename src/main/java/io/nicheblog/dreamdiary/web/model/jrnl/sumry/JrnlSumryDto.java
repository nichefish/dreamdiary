package io.nicheblog.dreamdiary.web.model.jrnl.sumry;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstnModule;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstnModule;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * JrnlSumryDto
 * <pre>
 *  저널 결산 Dto.
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
public class JrnlSumryDto
        extends BasePostDto
        implements Identifiable<Integer>, CommentCmpstnModule, TagCmpstnModule {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.JRNL_DIARY;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    protected String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 결산 년도 */
    private Integer yy;
    /** 꿈 일수 */
    private Integer dreamDayCnt;
    /** 꿈 갯수 */
    private Integer dreamCnt;
    /** 일기 일수 */
    private Integer diaryDayCnt;

    @Override
    public Integer getKey() {
        return this.postNo;
    }

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ToString(callSuper = true)
    public static class DTL extends JrnlSumryDto {

        /** 저널 결산 내용 (일기) 목록 */
        private List<JrnlSumryCnDto> sumryCnDiaryList;
        /** 저널 결산 내용 (꿈) 목록 */
        private List<JrnlSumryCnDto> sumryCnDreamList;
    }

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ToString(callSuper = true)
    public static class LIST extends JrnlSumryDto {
        //
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    public CommentCmpstn comment;
    /** 태그 정보 모듈 (위임) */
    public TagCmpstn tag;
}
