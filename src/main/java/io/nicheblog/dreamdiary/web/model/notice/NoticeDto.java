package io.nicheblog.dreamdiary.web.model.notice;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.auth.model.AuditorDto;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * NoticeDto
 * <pre>
 *  공지사항 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BasePostDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class NoticeDto
        extends BasePostDto {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.NOTICE;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    protected String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 팝업공지 여부 */
    @Builder.Default
    private String popupYn = "N";

    /** 파일시스템 참조 목록 */
    // private List<FlsysRefDto> flsysRefList;

    /* ----- */

    /**
     * 내부 값들 합쳐서 풀 타이틀 반환
     */
    public String getFullTitle() {
        String title = this.title;
        if (StringUtils.isNotEmpty(this.ctgrNm)) title = "[" + this.ctgrNm + "] " + title;
        if ("Y".equals(this.imprtcYn)) title = "[중요] " + title;
        return title;
    }

    /* ----- 댓글 모듈 ----- */

    /** 댓글 목록 */
    private List<CommentDto> commentList;
    /** 댓글 갯수 */
    @Builder.Default
    private Integer commentCnt = 0;
    /** 댓글 존재 여부 */
    @Builder.Default
    private Boolean hasComment = false;

    /* ----- 조치 모듈 ----- */

    /** 수정권한 */
    @Builder.Default
    protected String mdfable = Constant.MDFABLE_REGSTR;
    /** 조치자(작업자)ID */
    private String managtrId;
    /** 조치자(작업자)이름 */
    private String managtrNm;
    /** 조치자 정보 */
    private AuditorDto managtrInfo;
    /** 게시물 조치자 목록 */
    // private List<BoardPostManagtrDto> managtrList;
    /** 열람자 목록 */
    // private List<BoardPostViewerDto> viewerList;
    /** (수정시) 조치일자 변경하지 않음 변수 */
    private String managtDtUpdtYn;
}
