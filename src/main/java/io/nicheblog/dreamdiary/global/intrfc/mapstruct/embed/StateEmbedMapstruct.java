package io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed;

import io.nicheblog.dreamdiary.global.intrfc.entity.embed.StateEmbed;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.StateCmpstn;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * StateEmbedMapstruct
 * <pre>
 *  상태 모듈 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface StateEmbedMapstruct
        extends BaseMapstruct<StateCmpstn, StateEmbed> {

    StateEmbedMapstruct INSTANCE = Mappers.getMapper(StateEmbedMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    StateCmpstn toDto(final StateEmbed entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    StateEmbed toEntity(final StateCmpstn dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final StateCmpstn dto, final @MappingTarget StateEmbed entity) throws Exception;
}
