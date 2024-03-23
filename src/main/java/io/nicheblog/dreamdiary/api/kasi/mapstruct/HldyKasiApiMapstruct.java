package io.nicheblog.dreamdiary.api.kasi.mapstruct;

import io.nicheblog.dreamdiary.api.kasi.model.HldyKasiApiItemDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.schdul.SchdulEntity;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * HldyKasiApiMapstruct
 * <pre>
 *  API:: 한국천문연구원(KASI):: 특일 정보 API Mapstruct 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = { DateUtils.class, StringUtils.class })
public interface HldyKasiApiMapstruct
        extends BaseMapstruct<HldyKasiApiItemDto, SchdulEntity> {

    HldyKasiApiMapstruct INSTANCE = Mappers.getMapper(HldyKasiApiMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    HldyKasiApiItemDto toDto(final SchdulEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    // @Mapping(target = "boardCd", expression = "java(\"hldyApi\")")
    @Mapping(target = "schdulNm", expression = "java(dto.getDateName())")
    @Mapping(target = "schdulTyCd", expression = "java(\"HLDY\")")
    @Mapping(target = "beginDt", expression = "java(DateUtils.asDate(dto.getLocdate()))")
    @Mapping(target = "endDt", expression = "java(DateUtils.asDate(dto.getLocdate()))")
    SchdulEntity toEntity(final HldyKasiApiItemDto dto) throws Exception;

    /**
     * Entity update from Dto
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // @Mapping(target = "boardCd", expression = "java(\"hldyApi\")")
    @Mapping(target = "schdulNm", expression = "java(dto.getDateName())")
    @Mapping(target = "schdulTyCd", expression = "java(\"HLDY\")")
    @Mapping(target = "beginDt", expression = "java(DateUtils.asDate(dto.getLocdate()))")
    @Mapping(target = "endDt", expression = "java(DateUtils.asDate(dto.getLocdate()))")
    void updateFromDto(
            final HldyKasiApiItemDto dto,
            final @MappingTarget SchdulEntity entity
    ) throws Exception;
}
