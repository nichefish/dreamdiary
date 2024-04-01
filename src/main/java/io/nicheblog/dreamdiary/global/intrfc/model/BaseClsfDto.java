package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

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
@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BaseClsfDto
        extends BaseAtchDto {

    /** 글 번호 */
    protected Integer postNo;
    /** 컨텐츠 타입 */
    protected String contentType;

    /** (수정시) 조치일자 변경하지 않음 변수 */
    @Builder.Default
    private String managtDtUpdtYn = "N";

    /* ----- */

    /**
     * 복합키 객체 반환
     */
    public BaseClsfKey getClsfKey() {
        return new BaseClsfKey(this.getPostNo(), this.getContentType());
    }

}
