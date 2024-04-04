package io.nicheblog.dreamdiary.global.intrfc.mapstruct;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfEntity;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

/**
 * BaseClsfListMapstruct
 * <pre>
 *  (공통/상속) MapStruct 기반 Mapper 인터페이스
 *  (entity -> listDto 추가)
 * <pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
public interface BaseClsfMapstruct<Dto extends BaseClsfDto, ListDto extends BaseClsfDto, Entity extends BaseClsfEntity>
        extends BaseCrudMapstruct<Dto, ListDto, Entity> {

    /**
     * default : ClsfEntity 요소들 매핑
     */
    @AfterMapping
    @Named("toDto")
    default void mapClsfFields(final Entity entity, final @MappingTarget Dto dto) throws Exception {
        MapstructHelper.mapClsfFields(entity, dto);
    }

    /**
     * default : ClsfEntity 요소들 매핑
     */
    @AfterMapping
    @Named("toListDto")
    default void mapClsfListFields(final Entity entity, final @MappingTarget ListDto dto) throws Exception {
        MapstructHelper.mapClsfFields(entity, dto);
    }
}
