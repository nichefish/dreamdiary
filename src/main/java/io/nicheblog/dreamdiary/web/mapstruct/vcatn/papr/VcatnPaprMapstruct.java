package io.nicheblog.dreamdiary.web.mapstruct.vcatn.papr;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BasePostMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.vcatn.papr.VcatnPaprEntity;
import io.nicheblog.dreamdiary.web.mapstruct.vcatn.schdul.VcatnSchdulMapstruct;
import io.nicheblog.dreamdiary.web.model.vcatn.papr.VcatnPaprDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * VcatnPaprMapstruct
 * <pre>
 *  휴가계획서 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseClsfMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, VcatnSchdulMapstruct.class}, builder = @Builder(disableBuilder = true))
public interface VcatnPaprMapstruct
        extends BasePostMapstruct<VcatnPaprDto.DTL, VcatnPaprDto.LIST, VcatnPaprEntity> {

    VcatnPaprMapstruct INSTANCE = Mappers.getMapper(VcatnPaprMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Named("toDto")
    @Mapping(target = "schdulList", expression = "java(VcatnSchdulMapstruct.INSTANCE.toDtoList(entity.getSchdulList()))")
    VcatnPaprDto.DTL toDto(final VcatnPaprEntity entity) throws Exception;

    /**
     * Entity -> listDto
     */
    @Named("toListDto")
    @Mapping(target = "schdulList", expression = "java(VcatnSchdulMapstruct.INSTANCE.toDtoList(entity.getSchdulList()))")
    VcatnPaprDto.LIST toListDto(final VcatnPaprEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Mapping(target = "schdulList", expression = "java(VcatnSchdulMapstruct.INSTANCE.toEntityList(dto.getSchdulList()))")
    VcatnPaprEntity toEntity(final VcatnPaprDto.DTL dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @Mapping(target = "schdulList", expression = "java(VcatnSchdulMapstruct.INSTANCE.toEntityList(dto.getSchdulList()))")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final VcatnPaprDto.DTL dto, final @MappingTarget VcatnPaprEntity entity) throws Exception;
}
