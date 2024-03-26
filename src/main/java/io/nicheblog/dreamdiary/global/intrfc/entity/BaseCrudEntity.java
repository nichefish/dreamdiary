package io.nicheblog.dreamdiary.global.intrfc.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * BaseCrudEntity
 * <pre>
 *  (공통/상속) 기본 CRUD Entity
 *  "All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 *
 * @author nichefish
 */
@MappedSuperclass
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
public class BaseCrudEntity
        implements Serializable {

    /**
     * 삭제 여부
     */
    @Builder.Default
    @Column(name = "del_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    protected String delYn = "N";
}
