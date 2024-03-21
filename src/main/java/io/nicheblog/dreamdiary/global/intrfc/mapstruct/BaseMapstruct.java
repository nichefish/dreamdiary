package io.nicheblog.dreamdiary.global.intrfc.mapstruct;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * BaseMapstruct
 * <pre>
 *  (공통/상속) MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 */
public interface BaseMapstruct<Dto, Entity> {

    /**
     * Entity -> Dto
     */
    Dto toDto(final Entity e) throws Exception;

    /**
     * Dto -> Entity
     */
    Entity toEntity(final Dto d) throws Exception;

    /**
     * update Entity from Dto
     * (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final Dto d,
            final @MappingTarget Entity e
    ) throws Exception;
}
