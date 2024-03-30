package io.nicheblog.dreamdiary.web.model.dream;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Embedded;
import java.util.List;

/**
 * DreamDayDto
 * <pre>
 *  꿈 일자 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DreamDayDto
        extends BaseClsfDto {

    /** 꿈 일자 고유 번호 (PK) */
    private Integer dreamDayNo;

    /** 컨텐츠 타입 */
    @Builder.Default
    private String contentType = "dream_day";

    /** 꿈 일자 */
    private String dreamtDt;

    /** 날짜미상 여부 (Y/N) */
    @Builder.Default
    private String dtUnknownYn = "N";

    /** 년도 */
    private Integer yy;

    /** 월 */
    private Integer mnth;

    /** 대략일자 (날짜미상시 해당일자 이후에 표기) */
    private String aprxmtDt;

    /** 꿈 조각 목록 */
    private List<DreamPieceDto> dreamPieceList;

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentCmpstn comment;
}
