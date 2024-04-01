package io.nicheblog.dreamdiary.web.mapstruct.dream;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseClsfListMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.dream.DreamDayEntity;
import io.nicheblog.dreamdiary.web.model.dream.day.DreamDayDto;
import io.nicheblog.dreamdiary.web.model.dream.day.DreamDayListDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DreamDayMapstruct
 * <pre>
 *  꿈 일자 MapStruct 기반 Mapper 인터페이스.
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface DreamDayMapstruct
        extends BaseClsfListMapstruct<DreamDayDto, DreamDayListDto, DreamDayEntity> {

    DreamDayMapstruct INSTANCE = Mappers.getMapper(DreamDayMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "dreamtDt", expression = "java(DateUtils.asStr(entity.getDreamtDt(), DateUtils.PTN_DATE))")
    DreamDayDto toDto(final DreamDayEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Mapping(target = "dreamtDt", expression = "java(DateUtils.asStr(entity.getDreamtDt(), DateUtils.PTN_DATE))")
    DreamDayListDto toListDto(final DreamDayEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "dreamtDt", expression = "java(DateUtils.asDate(dto.getDreamtDt()))")
    DreamDayEntity toEntity(final DreamDayDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final DreamDayDto dto,
            final @MappingTarget DreamDayEntity entity
    ) throws Exception;
}
