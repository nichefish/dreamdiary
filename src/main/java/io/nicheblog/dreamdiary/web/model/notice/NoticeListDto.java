package io.nicheblog.dreamdiary.web.model.notice;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostListDto;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * NoticeListDto
 * <pre>
 *  공지사항 목록 조회 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BasePostListDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NoticeListDto
        extends BasePostListDto {

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

    /* ----- */

    /**
     * 태그 목록 문자열 반환
     */
    // @JsonIgnore
    // public String getTagListStr() {
    //     if (CollectionUtils.isEmpty(this.getTagList())) return null;
    //     StringBuilder a = new StringBuilder();
    //     for (BoardPostTagDto tag : this.getTagList()) {
    //         if (a.length() > 0) a.append(",");
    //         a.append(tag.getBoardTag());
    //     }
    //     return a.toString();
    // }

    /* ----- 댓글 모듈 ----- */

    /** 댓글 목록 */
    private List<CommentDto> commentList;
    /** 댓글 갯수 */
    @Builder.Default
    private Integer commentCnt = 0;
    /** 댓글 존재 여부 */
    @Builder.Default
    private Boolean hasComment = false;
}
