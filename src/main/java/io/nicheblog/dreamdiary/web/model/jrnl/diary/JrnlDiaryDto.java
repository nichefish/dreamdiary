package io.nicheblog.dreamdiary.web.model.jrnl.diary;

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
 * JrnlDiaryDto
 * <pre>
 *  저널 일기 Dto.
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
public class JrnlDiaryDto
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

    /** 저널 일자 번호 */
    private Integer jrnlDayNo;
    /** 순번 */
    private Integer idx;

    /** 편집완료 여부 (Y/N) */
    @Builder.Default
    private String editComptYn = "N";

    /** 저널 일자 */
    private String jrnlDt;

    @Override
    public Integer getKey() {
        return this.postNo;
    }

    /* ----- */

    public static class DTL extends JrnlDiaryDto {

    }

    public static class LIST extends JrnlDiaryDto {
        //
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    public CommentCmpstn comment;
    /** 태그 정보 모듈 (위임) */
    public TagCmpstn tag;
}
