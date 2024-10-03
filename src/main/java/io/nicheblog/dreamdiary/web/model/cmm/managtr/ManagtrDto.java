package io.nicheblog.dreamdiary.web.model.cmm.managtr;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditRegDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * ManagtrDto
 * <pre>
 *  조치자 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditRegDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ManagtrDto
        extends BaseAuditRegDto {

    /** 조치자 번호 (PK) */
    @Positive
    private Integer managtrNo;

    /** 참조 글 번호 */
    @Positive
    private Integer refPostNo;

    /** 참조 컨텐츠 타입 (게시판 코드) */
    @Size(max = 50)
    private String refContentType;

    /* ----- */

    /**
     * 생성자
     */
    public ManagtrDto(final BaseClsfKey key) {
        this.refPostNo = key.getPostNo();
        this.refContentType = key.getContentType();
    }
}