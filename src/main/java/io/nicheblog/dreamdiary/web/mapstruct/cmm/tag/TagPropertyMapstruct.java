package io.nicheblog.dreamdiary.web.mapstruct.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagPropertyEntity;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagSmpEntity;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagPropertyDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * TagPropertyMapstruct
 * <pre>
 *  태그 속성 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {})
public interface TagPropertyMapstruct
        extends BaseCrudMapstruct<TagPropertyDto, TagPropertyDto, TagPropertyEntity> {

    TagPropertyMapstruct INSTANCE = Mappers.getMapper(TagPropertyMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    TagPropertyDto toDto(final TagPropertyEntity entity) throws Exception;

    /**
     * SmpEntity -> Dto
     */
    TagPropertyDto toDto(final TagSmpEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    TagPropertyDto toListDto(final TagPropertyEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    TagPropertyEntity toEntity(final TagPropertyDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final TagPropertyDto dto, final @MappingTarget TagPropertyEntity entity) throws Exception;
}
