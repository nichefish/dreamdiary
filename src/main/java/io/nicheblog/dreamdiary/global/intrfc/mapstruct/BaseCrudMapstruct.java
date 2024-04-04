package io.nicheblog.dreamdiary.global.intrfc.mapstruct;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

/**
 * BaseCrudMapstruct
 * <pre>
 *  (공통/상속) MapStruct 기반 Mapper 인터페이스
 *  (entity -> listDto 추가)
 * <pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
public interface BaseCrudMapstruct<Dto extends BaseCrudDto, ListDto extends BaseCrudDto, Entity extends BaseCrudEntity>
        extends BaseMapstruct<Dto, Entity> {

    /**
     * Entity -> ListDto
     */
    @Named("toListDto")
    ListDto toListDto(final Entity e) throws Exception;

    /* ----- */

    /**
     * default : BaseEntity 기본 요소들 매핑
     */
    @AfterMapping
    @Named("toDto")
    default void mapBaseFields(final Entity entity, final @MappingTarget ListDto dto) throws Exception {
        MapstructHelper.mapBaseFields(entity, dto);
    }

    /**
     * default : BaseEntity 기본 요소들 매핑
     */
    @AfterMapping
    @Named("toListDto")
    default void mapBaseListFields(final Entity entity, final @MappingTarget ListDto dto) throws Exception {
        MapstructHelper.mapBaseFields(entity, dto);
    }
}
