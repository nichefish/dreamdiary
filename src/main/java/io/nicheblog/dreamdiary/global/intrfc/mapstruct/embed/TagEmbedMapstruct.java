package io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed;

import io.nicheblog.dreamdiary.global.intrfc.entity.embed.TagEmbed;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.tag.ContentTagMapstruct;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * TagEmbedMapstruct
 * <pre>
 *  태그 모듈 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, ContentTagMapstruct.class}, builder = @Builder(disableBuilder = true))
public interface TagEmbedMapstruct
        extends BaseMapstruct<TagCmpstn, TagEmbed> {

    TagEmbedMapstruct INSTANCE = Mappers.getMapper(TagEmbedMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "list", expression = "java(ContentTagMapstruct.INSTANCE.toDtoList(entity.getList()))")
    TagCmpstn toDto(final TagEmbed entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "list", expression = "java(dto.getEntityList())")
    @Mapping(target = "tagStrList", expression = "java(dto.getParsedTagStrList())")
    TagEmbed toEntity(final TagCmpstn dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final TagCmpstn dto, final @MappingTarget TagEmbed entity) throws Exception;
}
