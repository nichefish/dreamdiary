package io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed;

import io.nicheblog.dreamdiary.global.intrfc.entity.embed.ManagtEmbed;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.ManagtCmpstn;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ManagtEmbedMapstruct
 * <pre>
 *  조치 모듈 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface ManagtEmbedMapstruct
        extends BaseMapstruct<ManagtCmpstn, ManagtEmbed> {

    ManagtEmbedMapstruct INSTANCE = Mappers.getMapper(ManagtEmbedMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "list", expression = "java(entity.getDtoList())")
    @Mapping(target = "managtDt", expression = "java(DateUtils.asStr(entity.getManagtDt(), DatePtn.DATETIME))")
    ManagtCmpstn toDto(final ManagtEmbed entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "list", expression = "java(dto.getEntityList())")
    ManagtEmbed toEntity(final ManagtCmpstn dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final ManagtCmpstn dto, final @MappingTarget ManagtEmbed entity) throws Exception;
}
