package io.nicheblog.dreamdiary.web.mapstruct.admin;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.admin.TmplatDefEntity;
import io.nicheblog.dreamdiary.web.model.admin.TmplatDefDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * TmplatDefMapstruct
 * <pre>
 *  템플릿 정의 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class})
public interface TmplatDefMapstruct
        extends BaseCrudMapstruct<TmplatDefDto, TmplatDefDto, TmplatDefEntity> {

    /**
     * getMapper
     */
    TmplatDefMapstruct INSTANCE = Mappers.getMapper(TmplatDefMapstruct.class);

    /**
     * Dto -> Entity
     */
    @Override
    TmplatDefEntity toEntity(final TmplatDefDto dto) throws Exception;

    /**
     * Entity -> Dto
     */
    @Override
    TmplatDefDto toDto(final TmplatDefEntity entity) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final TmplatDefDto dto, final @MappingTarget TmplatDefEntity entity) throws Exception;
}
