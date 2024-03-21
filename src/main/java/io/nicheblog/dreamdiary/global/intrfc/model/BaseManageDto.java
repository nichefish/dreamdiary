package io.nicheblog.dreamdiary.global.intrfc.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

/**
 * BaseManageDto
 * <pre>
 *  (공통/상속) 관리용 정보 Dto (정렬 순서, 사용여부 추가)
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditDto
 */
@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BaseManageDto
        extends BaseAuditDto {

    /**
     * 정렬 순서
     */
    private Integer sortOrdr;

    /**
     * 사용여부
     */
    @Builder.Default
    private String useYn = "Y";
}
