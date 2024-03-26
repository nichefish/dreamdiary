package io.nicheblog.dreamdiary.global.intrfc.model;

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
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class BasePostListDto
        extends BaseClsfListDto {

    /**
     * 조회수
     */
    @Builder.Default
    protected Integer hitCnt = 0;

}
