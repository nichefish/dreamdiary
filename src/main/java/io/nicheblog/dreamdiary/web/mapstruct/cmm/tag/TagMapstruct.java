package io.nicheblog.dreamdiary.web.mapstruct.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagSmpEntity;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * TagMapstruct
 * <pre>
 *  태그 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {})
public interface TagMapstruct
        extends BaseCrudMapstruct<TagDto, TagDto, TagEntity> {

    TagMapstruct INSTANCE = Mappers.getMapper(TagMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "contentTagList", expression = "java(ContentTagMapstruct.INSTANCE.toDtoList(entity.getContentTagList()))")
    TagDto toDto(final TagEntity entity) throws Exception;

    /**
     * SmpEntity -> Dto
     */
    TagDto toDto(final TagSmpEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "contentTagList", expression = "java(ContentTagMapstruct.INSTANCE.toDtoList(entity.getContentTagList()))")
    TagDto toListDto(final TagEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    TagEntity toEntity(final TagDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final TagDto dto, final @MappingTarget TagEntity entity) throws Exception;
}
