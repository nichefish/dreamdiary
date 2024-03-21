package io.nicheblog.dreamdiary.global.intrfc.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * BaseCrudEntity
 * <pre>
 *  (공통/상속) 기본 CRUD Entity
 *  ※공통 인터페이스 사용하는 모든 Dto 객체는 해당 객체를 상속한다.
 *  ※"All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 *
 * @author nichefish
 */
@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseCrudEntity {

    /**
     * 삭제여부
     */
    @Builder.Default
    @Column(name = "DEL_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    protected String delYn = "N";
}
