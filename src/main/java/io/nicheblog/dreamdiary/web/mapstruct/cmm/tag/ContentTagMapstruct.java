package io.nicheblog.dreamdiary.web.mapstruct.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import io.nicheblog.dreamdiary.web.model.cmm.tag.ContentTagDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ContentTagMapstruct
 * <pre>
 *  컨텐츠-태그 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, TagMapstruct.class})
public interface ContentTagMapstruct
        extends BaseCrudMapstruct<ContentTagDto, ContentTagDto, ContentTagEntity> {

    ContentTagMapstruct INSTANCE = Mappers.getMapper(ContentTagMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "tag", expression = "java(TagMapstruct.INSTANCE.toDto(entity.getTag()))")
    ContentTagDto toDto(final ContentTagEntity entity) throws Exception;

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "tag", expression = "java(TagMapstruct.INSTANCE.toDto(entity.getTag()))")
    ContentTagDto toListDto(final ContentTagEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    ContentTagEntity toEntity(final ContentTagDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final ContentTagDto dto, final @MappingTarget ContentTagEntity entity) throws Exception;
}
