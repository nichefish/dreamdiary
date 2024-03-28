package io.nicheblog.dreamdiary.web.mapstruct.flsys;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.flsys.FlsysMetaEntity;
import io.nicheblog.dreamdiary.web.model.flsys.FlsysMetaDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * FlsysMetaMapstruct
 * <pre>
 *  파일시스템 메타 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface FlsysMetaMapstruct
        extends BaseListMapstruct<FlsysMetaDto, FlsysMetaDto, FlsysMetaEntity> {

    FlsysMetaMapstruct INSTANCE = Mappers.getMapper(FlsysMetaMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    FlsysMetaDto toDto(final FlsysMetaEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    FlsysMetaEntity toEntity(final FlsysMetaDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final FlsysMetaDto dto,
            final @MappingTarget FlsysMetaEntity entity
    ) throws Exception;
}
