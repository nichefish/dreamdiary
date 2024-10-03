package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * BaseClsfDto
 * <pre>
 *  (공통/상속) 게시판 Dto
 *  "All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 *
 * @author nichefish
 * @extends BaseAtchDto
 */
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BaseClsfDto
        extends BaseAtchDto {

    /** 글 번호 */
    @Positive
    protected Integer postNo;

    /** 컨텐츠 타입 */
    @Size(max = 50)
    protected String contentType;

    /** (수정시) 조치일자 변경하지 않음 여부 (Y/N) */
    @Builder.Default
    @Size(min = 1, max = 1)
    @Pattern(regexp = "^[YN]$")
    protected String managtDtUpdtYn = "N";

    /* ----- */

    /** 새 글 여부 */
    @Builder.Default
    protected Boolean isNew = false;

    /**
     * 복합키 객체 반환
     */
    public BaseClsfKey getClsfKey() {
        return new BaseClsfKey(this.getPostNo(), this.getContentType());
    }
}
