package io.nicheblog.dreamdiary.domain._core.tag.mapstruct;

import io.nicheblog.dreamdiary.domain._core.tag.entity.TagPropertyEntity;
import io.nicheblog.dreamdiary.domain._core.tag.model.TagPropertyDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * TagPropertyMapstruct
 * <pre>
 *  태그 속성 MapStruct 기반 Mapper 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {})
public interface TagPropertyMapstruct
        extends BaseCrudMapstruct<TagPropertyDto, TagPropertyDto, TagPropertyEntity> {

    TagPropertyMapstruct INSTANCE = Mappers.getMapper(TagPropertyMapstruct.class);

    /**
     * Entity -> Dto
     * @param entity - 변환할 Entity 객체
     * @return Dto - 변환된 Dto 객체
     * @throws Exception - 변환 중 발생할 수 있는 예외
     */
    @Override
    @Named("toDto")
    TagPropertyDto toDto(final TagPropertyEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     * @param entity - 변환할 Entity 객체
     * @return ListDto - 변환된 ListDto 객체
     * @throws Exception - 변환 중 발생할 수 있는 예외
     */
    @Override
    @Named("toListDto")
    TagPropertyDto toListDto(final TagPropertyEntity entity) throws Exception;

    /**
     * Dto -> Entity
     * @param dto - 변환할 AtchFileDtlDto 객체
     * @return Entity - 변환된 AtchFileDtlEntity 객체
     * @throws Exception - 변환 중 발생할 수 있는 예외
     */
    @Override
    TagPropertyEntity toEntity(final TagPropertyDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     * @param dto - 업데이트할 DTO 객체
     * @param entity - 업데이트할 대상 엔티티 객체
     * @throws Exception - 매핑 중 발생할 수 있는 예외
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final TagPropertyDto dto, final @MappingTarget TagPropertyEntity entity) throws Exception;
}
