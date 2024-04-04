package io.nicheblog.dreamdiary.web.mapstruct.dream;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseClsfMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.dream.DreamDayEntity;
import io.nicheblog.dreamdiary.web.model.dream.day.DreamDayDto;
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
 * @extends BaseClsfMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface DreamDayMapstruct
        extends BaseClsfMapstruct<DreamDayDto, DreamDayDto, DreamDayEntity> {

    DreamDayMapstruct INSTANCE = Mappers.getMapper(DreamDayMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "dreamtDt", expression = "java(DateUtils.asStr(entity.getDreamtDt(), DatePtn.DATE))")
    DreamDayDto toDto(final DreamDayEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "dreamtDt", expression = "java(DateUtils.asStr(entity.getDreamtDt(), DatePtn.DATE))")
    DreamDayDto toListDto(final DreamDayEntity entity) throws Exception;

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
