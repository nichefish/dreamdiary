package io.nicheblog.dreamdiary.web.model.exptr.reqst;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.ManagtCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.ViewerCmpstn;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Embedded;

/**
 * ExptrReqstDto
 * <pre>
 *  경비 관리 > 물품구매/경조사비 신청 Dto
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
public class ExptrReqstDto
        extends BasePostDto {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.EXPTR_REQST;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    protected String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 처리 여부 (Y/N) */
    @Builder.Default
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

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentCmpstn comment;

    /** 조치 정보 모듈 (위임) */
    @Embedded
    public ManagtCmpstn managt;

    /** 열람자 정보 모듈 (위임) */
    @Embedded
    public ViewerCmpstn viewer;
}
