package io.nicheblog.dreamdiary.global.intrfc.mapstruct;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfEntity;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

/**
 * BasePostMapstruct
 * <pre>
 *  (공통/상속) MapStruct 기반 Mapper 인터페이스
 *  (clsf 기반 요소들 변환 로직 추가)
 * <pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
public interface BasePostMapstruct<Dto extends BaseClsfDto, ListDto extends BaseClsfDto, Entity extends BaseClsfEntity>
        extends BaseClsfMapstruct<Dto, ListDto, Entity> {

    /**
     * default : ClsfEntity 요소들 매핑
     */
    @AfterMapping
    default void mapPostFields(final Entity entity, final @MappingTarget Dto dto) throws Exception {
        MapstructHelper.mapPostFields(entity, dto);
    }

    /**
     * default : ClsfEntity 요소들 매핑
     */
    @AfterMapping
    default void mapPostListFields(final Entity entity, final @MappingTarget ListDto dto) throws Exception {
        MapstructHelper.mapPostFields(entity, dto);
    }
}
