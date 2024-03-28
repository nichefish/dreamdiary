package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.Constant;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

/**
 * BasePostDto
 * <pre>
 *  (공통/상속) 게시판 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseClsfDto
 */
@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BasePostDto
        extends BaseClsfDto {

    /**
     * 조회수
     */
    @Builder.Default
    protected Integer hitCnt = 0;

    /**
     * 중요여부
     */
    @Builder.Default
    protected String imprtcYn = "N";

    /**
     * 수정권한
     */
    @Builder.Default
    protected String mdfable = Constant.MDFABLE_REGSTR;

    /* ----- */
}
