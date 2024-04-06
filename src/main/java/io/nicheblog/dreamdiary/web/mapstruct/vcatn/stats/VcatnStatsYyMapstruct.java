package io.nicheblog.dreamdiary.web.mapstruct.vcatn.stats;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.vcatn.stats.VcatnStatsYyEntity;
import io.nicheblog.dreamdiary.web.model.vcatn.stats.VcatnStatsYyDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * VcatnStatsYyMapstruct
 * <pre>
 *  휴가 통계 년도 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class})
public interface VcatnStatsYyMapstruct
        extends BaseMapstruct<VcatnStatsYyDto, VcatnStatsYyEntity> {

    VcatnStatsYyMapstruct INSTANCE = Mappers.getMapper(VcatnStatsYyMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Mapping(target = "bgnDt", expression = "java(entity.getBgnDt() != null ? DateUtils.asStr(entity.getBgnDt(), DatePtn.DATE) : null)")
    @Mapping(target = "endDt", expression = "java(entity.getEndDt() != null ? DateUtils.asStr(entity.getEndDt(), DatePtn.DATE) : null)")
    VcatnStatsYyDto toDto(final VcatnStatsYyEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    VcatnStatsYyEntity toEntity(final VcatnStatsYyDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final VcatnStatsYyDto dto, final @MappingTarget VcatnStatsYyEntity entity) throws Exception;
}
