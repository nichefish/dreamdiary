package io.nicheblog.dreamdiary.web.model.vcatn;

import io.nicheblog.dreamdiary.global.intrfc.model.BasePostListDto;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * VcatnPaprListDto
 * <pre>
 *  휴가계획서 목록 조회 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BasePostListDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VcatnPaprListDto
        extends BasePostListDto {

    /* 확인 여부*/
    private String cfYn;

    /* 휴가 추가 리스트*/
    private List<VcatnSchdulDto> vcatnSchdulList;

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
