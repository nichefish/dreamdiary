package io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed;

import io.nicheblog.dreamdiary.global.intrfc.entity.embed.ViewerEmbed;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.ViewerCmpstn;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ViewerEmbedMapstruct
 * <pre>
 *  열람자 모듈 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface ViewerEmbedMapstruct
        extends BaseMapstruct<ViewerCmpstn, ViewerEmbed> {

    ViewerEmbedMapstruct INSTANCE = Mappers.getMapper(ViewerEmbedMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "list", expression = "java(entity.getDtoList())")
    ViewerCmpstn toDto(final ViewerEmbed entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "list", expression = "java(dto.getEntityList())")
    ViewerEmbed toEntity(final ViewerCmpstn dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final ViewerCmpstn dto,
            final @MappingTarget ViewerEmbed entity
    ) throws Exception;
}
