package io.nicheblog.dreamdiary.web.model.notice;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.ManagtEmbed;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostListDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Embedded;

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

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentCmpstn comment;

    /** 조치 정보 모듈 (위임) */
    @Embedded
    public ManagtEmbed managt;
}
