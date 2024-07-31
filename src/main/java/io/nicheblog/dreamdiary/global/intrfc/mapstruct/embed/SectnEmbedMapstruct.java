package io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed;

import io.nicheblog.dreamdiary.global.intrfc.entity.embed.SectnEmbed;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.SectnCmpstn;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.sectn.SectnMapstruct;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * CnEmbedMapstruct
 * <pre>
 *  내용 모듈 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {SectnMapstruct.class}, builder = @Builder(disableBuilder = true))
public interface SectnEmbedMapstruct
        extends BaseMapstruct<SectnCmpstn, SectnEmbed> {

    SectnEmbedMapstruct INSTANCE = Mappers.getMapper(SectnEmbedMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "list", expression = "java(SectnMapstruct.INSTANCE.toDtoList(entity.getList()))")
    SectnCmpstn toDto(final SectnEmbed entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    SectnEmbed toEntity(final SectnCmpstn dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final SectnCmpstn dto, final @MappingTarget SectnEmbed entity) throws Exception;
}
