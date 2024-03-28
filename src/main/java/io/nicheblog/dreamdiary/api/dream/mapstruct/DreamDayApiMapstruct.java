package io.nicheblog.dreamdiary.api.dream.mapstruct;

import io.nicheblog.dreamdiary.api.dream.model.DreamDayApiDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseAuditListMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.dream.DreamDayEntity;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DreamDayMapstruct
 * <pre>
 *  API:: 꿈 일자 MapStruct 기반 Mapper 인터페이스.
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface DreamDayApiMapstruct
        extends BaseAuditListMapstruct<DreamDayApiDto, DreamDayApiDto, DreamDayEntity> {

    DreamDayApiMapstruct INSTANCE = Mappers.getMapper(DreamDayApiMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "dreamtDt", expression = "java(DateUtils.asStr(entity.getDreamtDt(), DateUtils.PTN_DATE))")
    DreamDayApiDto toDto(final DreamDayEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Mapping(target = "dreamtDt", expression = "java(DateUtils.asStr(entity.getDreamtDt(), DateUtils.PTN_DATE))")
    DreamDayApiDto toListDto(final DreamDayEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "dreamtDt", expression = "java(DateUtils.asDate(dto.getDreamtDt()))")
    DreamDayEntity toEntity(final DreamDayApiDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final DreamDayApiDto dto,
            final @MappingTarget DreamDayEntity entity
    ) throws Exception;
}
