package io.nicheblog.dreamdiary.web.mapstruct.admin;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.admin.TmplatTxtEntity;
import io.nicheblog.dreamdiary.web.model.admin.TmplatTxtDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * TmplatTxtMapstruct
 * <pre>
 *  템플릿 문구 정보 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class})
public interface TmplatTxtMapstruct
        extends BaseCrudMapstruct<TmplatTxtDto, TmplatTxtDto, TmplatTxtEntity> {

    /**
     * getMapper
     */
    TmplatTxtMapstruct INSTANCE = Mappers.getMapper(TmplatTxtMapstruct.class);

    /**
     * Dto -> Entity
     */
    @Override
    TmplatTxtEntity toEntity(final TmplatTxtDto dto) throws Exception;

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "regDt", expression = "java(DateUtils.asStr(entity.getRegDt(), DatePtn.DATETIME))")
    TmplatTxtDto toDto(final TmplatTxtEntity entity) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final TmplatTxtDto dto, final @MappingTarget TmplatTxtEntity entity) throws Exception;
}
