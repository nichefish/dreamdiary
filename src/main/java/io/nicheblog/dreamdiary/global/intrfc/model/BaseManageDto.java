package io.nicheblog.dreamdiary.global.intrfc.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

/**
 * BaseManageDto
 * <pre>
 *  (공통/상속) 관리용 정보 Dto (정렬 순서, 사용 여부 추가)
 *  "All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditDto
 */
@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BaseManageDto
        extends BaseAuditDto {

    /** 정렬 순서 */
    protected Integer sortOrdr;

    /** 사용 여부 (Y/N) */
    @Builder.Default
    protected String useYn = "Y";
}
