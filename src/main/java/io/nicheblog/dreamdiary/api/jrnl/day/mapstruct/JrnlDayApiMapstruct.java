package io.nicheblog.dreamdiary.api.jrnl.day.mapstruct;

import io.nicheblog.dreamdiary.api.jrnl.day.model.JrnlDayApiDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * JrnlDayMapstruct
 * <pre>
 *  API:: 저널 일자 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface JrnlDayApiMapstruct
        extends BaseCrudMapstruct<JrnlDayApiDto, JrnlDayApiDto, JrnlDayEntity> {

    JrnlDayApiMapstruct INSTANCE = Mappers.getMapper(JrnlDayApiMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "dreamtDt", expression = "java(DateUtils.asStr(entity.getDreamtDt(), DatePtn.DATE))")
    JrnlDayApiDto toDto(final JrnlDayEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "dreamtDt", expression = "java(DateUtils.asStr(entity.getDreamtDt(), DatePtn.DATE))")
    JrnlDayApiDto toListDto(final JrnlDayEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "dreamtDt", expression = "java(DateUtils.asDate(dto.getDreamtDt()))")
    JrnlDayEntity toEntity(final JrnlDayApiDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final JrnlDayApiDto dto, final @MappingTarget JrnlDayEntity entity) throws Exception;
}
