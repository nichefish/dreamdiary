package io.nicheblog.dreamdiary.web.mapstruct.vcatn.papr;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.vcatn.papr.VcatnSchdulEntity;
import io.nicheblog.dreamdiary.web.model.schdul.SchdulCalDto;
import io.nicheblog.dreamdiary.web.model.vcatn.dy.VcatnDyDto;
import io.nicheblog.dreamdiary.web.model.vcatn.dy.VcatnDyXlsxDto;
import io.nicheblog.dreamdiary.web.model.vcatn.papr.VcatnSchdulDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * VcatnSchdulMapstruct
 * <pre>
 *  휴가 일정 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class})
public interface VcatnSchdulMapstruct
        extends BaseCrudMapstruct<VcatnSchdulDto, VcatnSchdulDto, VcatnSchdulEntity> {

    VcatnSchdulMapstruct INSTANCE = Mappers.getMapper(VcatnSchdulMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "bgnDt", expression = "java(DateUtils.asStr(entity.getBgnDt(), DatePtn.DATE))")
    @Mapping(target = "endDt", expression = "java(DateUtils.asStr(entity.getEndDt(), DatePtn.DATE))")
    VcatnSchdulDto toDto(final VcatnSchdulEntity entity) throws Exception;

    /**
     * Entity -> DyDto 생성 (1:N, for문 돌면서 개별 생성)
     */
    VcatnDyDto toDyDto(final VcatnSchdulEntity entity) throws Exception;

    /**
     * Dto -> DyXlsxDto
     */
    @Mapping(target = "vcatnExprDy", expression = "java(Double.toString(dto.getVcatnExprDy()))")
    VcatnDyXlsxDto toDyXlsxDto(final VcatnDyDto dto) throws Exception;

    /**
     * Dto -> Entity
     */
    @Mapping(target = "bgnDt", expression = "java(DateUtils.asDate(dto.getBgnDt()))")
    @Mapping(target = "endDt", expression = "java(DateUtils.asDate(dto.getEndDt()))")
    VcatnSchdulEntity toEntity(final VcatnSchdulDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "bgnDt", expression = "java(DateUtils.asDate(dto.getBgnDt()))")
    @Mapping(target = "endDt", expression = "java(DateUtils.asDate(dto.getEndDt()))")
    void updateFromDto(final VcatnSchdulDto dto, final @MappingTarget VcatnSchdulEntity entity) throws Exception;
}
