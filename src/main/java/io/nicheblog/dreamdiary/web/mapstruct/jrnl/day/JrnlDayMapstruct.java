package io.nicheblog.dreamdiary.web.mapstruct.jrnl.day;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseClsfMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import io.nicheblog.dreamdiary.web.model.jrnl.day.JrnlDayDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * JrnlDayMapstruct
 * <pre>
 *  저널 일자 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseClsfMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface JrnlDayMapstruct
        extends BaseClsfMapstruct<JrnlDayDto, JrnlDayDto, JrnlDayEntity> {

    JrnlDayMapstruct INSTANCE = Mappers.getMapper(JrnlDayMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "jrnlDt", expression = "java(DateUtils.asStr(entity.getJrnlDt(), DatePtn.DATE))")
    JrnlDayDto.DTL toDto(final JrnlDayEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "jrnlDt", expression = "java(DateUtils.asStr(entity.getJrnlDt(), DatePtn.DATE))")
    JrnlDayDto toListDto(final JrnlDayEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "jrnlDt", expression = "java(DateUtils.asDate(dto.getJrnlDt()))")
    JrnlDayEntity toEntity(final JrnlDayDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final JrnlDayDto dto, final @MappingTarget JrnlDayEntity entity) throws Exception;
}
