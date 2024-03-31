package io.nicheblog.dreamdiary.web.model.vcatn.papr;

import io.nicheblog.dreamdiary.global.intrfc.model.BasePostListDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.ManagtCmpstn;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Embedded;
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

    /** 확인 여부 (Y/N) */
    private String cfYn;

    /** 휴가 일정 리스트 */
    private List<VcatnSchdulDto> schdulList;

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentCmpstn comment;

    /** 조치 정보 모듈 (위임) */
    @Embedded
    public ManagtCmpstn managt;
}
