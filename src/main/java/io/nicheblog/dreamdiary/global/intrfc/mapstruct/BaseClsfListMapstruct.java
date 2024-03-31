package io.nicheblog.dreamdiary.global.intrfc.mapstruct;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfEntity;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfDto;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfListDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

/**
 * BaseClsfListMapstruct
 * <pre>
 *  (공통/상속) MapStruct 기반 Mapper 인터페이스
 *  (entity -> listDto 추가)
 * <pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
public interface BaseClsfListMapstruct<Dto extends BaseClsfDto, ListDto extends BaseClsfListDto, Entity extends BaseClsfEntity>
        extends BaseListMapstruct<Dto, ListDto, Entity> {

    @AfterMapping
    default void mapClsfFields(final Entity entity, final @MappingTarget Dto dto) throws Exception {
        MapstructHelper.mapClsfFields(entity, dto);
    }

    @AfterMapping
    default void mapClsfListFields(final Entity entity, final @MappingTarget ListDto dto) throws Exception {
        MapstructHelper.mapClsfFields(entity, dto);
    }
}
