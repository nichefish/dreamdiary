package io.nicheblog.dreamdiary.global.intrfc.mapstruct;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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

    /**
     * EntityList to DtoList
     */
    default List<ListDto> toDtoList(List<Entity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) return null;
        AtomicLong i = new AtomicLong(1);
        return entityList.stream()
                .map(entity -> {
                    try {
                        ListDto dto = this.toListDto(entity);
                        dto.setRnum(i.getAndIncrement());
                        return dto;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    /* ----- */

    /**
     * default : BaseEntity 기본 요소들 매핑 (toDto)
     */
    @AfterMapping
    default void mapBaseFields(final Entity entity, final @MappingTarget Dto dto) throws Exception {
        MapstructHelper.mapBaseFields(entity, dto);
    }

    /**
     * default : BaseEntity 기본 요소들 매핑 (toListDto)
     */
    @AfterMapping
    default void mapBaseListFields(final Entity entity, final @MappingTarget ListDto dto) throws Exception {
        MapstructHelper.mapBaseFields(entity, dto);
    }
}
