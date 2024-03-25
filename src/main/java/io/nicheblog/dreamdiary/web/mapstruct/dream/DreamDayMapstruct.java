package io.nicheblog.dreamdiary.web.mapstruct.dream;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.dream.DreamDayEntity;
import io.nicheblog.dreamdiary.web.model.dream.DreamDayDto;
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
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface DreamDayMapstruct
        extends BaseListMapstruct<DreamDayDto, DreamDayDto, DreamDayEntity> {

    DreamDayMapstruct INSTANCE = Mappers.getMapper(DreamDayMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "regstrNm", expression = "java((entity.getRegstrInfo() != null) ? entity.getRegstrInfo().getNickNm() : null)")
    @Mapping(target = "regDt", expression = "java(DateUtils.asStr(entity.getRegDt(), DateUtils.PTN_DATETIME))")
    @Mapping(target = "mdfDt", expression = "java(DateUtils.asStr(entity.getMdfDt(), DateUtils.PTN_DATETIME))")
    DreamDayDto toDto(final DreamDayEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Mapping(target = "regstrNm", expression = "java((entity.getRegstrInfo() != null) ? entity.getRegstrInfo().getNickNm() : null)")
    @Mapping(target = "regDt", expression = "java(DateUtils.asStr(entity.getRegDt(), DateUtils.PTN_DATETIME))")
    @Mapping(target = "mdfDt", expression = "java(DateUtils.asStr(entity.getMdfDt(), DateUtils.PTN_DATETIME))")
    DreamDayDto toListDto(final DreamDayEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
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
