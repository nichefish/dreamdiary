package io.nicheblog.dreamdiary.global.intrfc.mapstruct;

import org.mapstruct.*;

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
    @Named("toDto")
    Dto toDto(final Entity e) throws Exception;

    /**
     * Dto -> Entity
     */
    @Named("toEntity")
    Entity toEntity(final Dto d) throws Exception;

    /**
     * Update Entity from Dto
     * (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Named("updateFromDto")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final Dto d, final @MappingTarget Entity e) throws Exception;

    /**
     * default : State 관련 기본 요소들 매핑
     */
    @AfterMapping
    default void mapBaseListFields(final Dto dto, final @MappingTarget Entity entity) throws Exception {
        MapstructHelper.mapStateFields(dto, entity);
    }
}
