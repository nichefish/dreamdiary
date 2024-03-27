package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.Constant;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

/**
 * BasePostListDto
 * <pre>
 *  (공통/상속) 게시판 목록 조회 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseClsfListDto
 */
@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BasePostListDto
        extends BaseClsfListDto {

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

}
