package io.nicheblog.dreamdiary.global.intrfc.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * BaseCrudDto
 * <pre>
 *  (공통/상속) 기본 CRUD Dto
 *  ※공통 인터페이스 사용하는 모든 Dto 객체는 해당 객체를 상속한다.
 * </pre>
 *
 * @author nichefish
 */
@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@EqualsAndHashCode
public class BaseCrudDto
        implements Serializable {

    /**
     * 목록 순번
     */
    protected Long rnum;
}

