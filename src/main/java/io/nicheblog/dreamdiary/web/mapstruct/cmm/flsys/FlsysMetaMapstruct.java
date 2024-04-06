package io.nicheblog.dreamdiary.web.mapstruct.cmm.flsys;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseClsfMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.flsys.FlsysMetaEntity;
import io.nicheblog.dreamdiary.web.model.cmm.flsys.FlsysMetaDto;
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
 * @extends BaseClsfMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface FlsysMetaMapstruct
        extends BaseClsfMapstruct<FlsysMetaDto, FlsysMetaDto, FlsysMetaEntity> {

    FlsysMetaMapstruct INSTANCE = Mappers.getMapper(FlsysMetaMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    FlsysMetaDto toDto(final FlsysMetaEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Named("toListDto")
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
