package io.nicheblog.dreamdiary.web.mapstruct.schdul;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BasePostMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.schdul.SchdulEntity;
import io.nicheblog.dreamdiary.web.model.schdul.SchdulDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * SchdulMapstruct
 * <pre>
 *  일정 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, DatePtn.class})
public interface SchdulMapstruct
        extends BasePostMapstruct<SchdulDto, SchdulDto, SchdulEntity> {

    SchdulMapstruct INSTANCE = Mappers.getMapper(SchdulMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    // @Mapping(target = "prtcpnt", expression = "java(entity.getPrtcpntStr())")
    @Mapping(target = "bgnDt", expression = "java(DateUtils.asStr(entity.getBgnDt(), DatePtn.DATE))")
    @Mapping(target = "endDt", expression = "java(DateUtils.asStr(entity.getEndDt(), DatePtn.DATE))")
    // @Mapping(target = "prtcpntList", expression = "java(entity.getPrtcpntDtoList())")
    SchdulDto toDto(final SchdulEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Mapping(target = "bgnDt", expression = "java(DateUtils.asDate(dto.getBgnDt()))")
    @Mapping(target = "endDt", expression = "java(DateUtils.asDate(dto.getEndDt()))")
    SchdulEntity toEntity(final SchdulDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "bgnDt", expression = "java(DateUtils.asDate(dto.getBgnDt()))")
    @Mapping(target = "endDt", expression = "java(DateUtils.asDate(dto.getEndDt()))")
    void updateFromDto(final SchdulDto dto, final @MappingTarget SchdulEntity entity) throws Exception;
}
