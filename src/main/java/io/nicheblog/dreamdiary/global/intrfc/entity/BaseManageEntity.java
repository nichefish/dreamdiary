package io.nicheblog.dreamdiary.global.intrfc.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

/**
 * BaseManageEntity
 * <pre>
 *  (공통/상속) 관리용 정보 Entity (정렬 순서, 사용 여부)
 *  "All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditEntity
 */
@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BaseManageEntity
        extends BaseAuditEntity {

    /**
     * 정렬 순서
     */
    @Column(name = "sort_ordr", columnDefinition = "INT DEFAULT 0")
    protected Integer sortOrdr;

    /**
     * 사용 여부
     */
    @Builder.Default
    @Column(name = "use_yn", length = 1, columnDefinition = "CHAR DEFAULT 'Y'")
    protected String useYn = "Y";
}

