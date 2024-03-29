package io.nicheblog.dreamdiary.web.model.cmm.viewer;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditRegDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ViewerDto
 * <pre>
 *  열람자 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditRegDto
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ViewerDto
        extends BaseAuditRegDto {

    /** 조치자 번호 (PK) */
    private Integer managtrNo;

    /** 글 번호 */
    private Integer refPostNo;

    /** 게시판 코드 */
    private String refContentType;

    /* ----- */

    /**
     * 생성자
     */
    public ViewerDto(final BaseClsfKey key) {
        this.refPostNo = key.getPostNo();
        this.refContentType = key.getContentType();
    }
}
