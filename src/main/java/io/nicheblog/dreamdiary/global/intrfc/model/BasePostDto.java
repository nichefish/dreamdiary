package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDto;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostKey;
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
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BasePostDto
        extends BaseClsfDto {

    /**
     * 상단고정여부
     */
    @Builder.Default
    private String fxdYn = "N";

    /**
     * 조회수
     */
    @Builder.Default
    protected Integer hitCnt = 0;

    /* ----- */
}
