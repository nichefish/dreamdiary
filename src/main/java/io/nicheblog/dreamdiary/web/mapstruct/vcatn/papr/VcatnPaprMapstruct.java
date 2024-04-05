package io.nicheblog.dreamdiary.web.mapstruct.vcatn.papr;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseClsfMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.embed.CommentEmbedMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.vcatn.papr.VcatnPaprEntity;
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
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface VcatnPaprMapstruct
        extends BaseClsfMapstruct<VcatnPaprDto.DTL, VcatnPaprDto.LIST, VcatnPaprEntity> {

    VcatnPaprMapstruct INSTANCE = Mappers.getMapper(VcatnPaprMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Named("toDto")
    @Mapping(target = "schdulList", expression = "java(entity.getVcatnSchdulDtoList())")
    VcatnPaprDto.DTL toDto(final VcatnPaprEntity entity) throws Exception;

    /**
     * Entity -> listDto
     */
    @Named("toListDto")
    @Mapping(target = "schdulList", expression = "java(entity.getVcatnSchdulDtoList())")
    VcatnPaprDto.LIST toListDto(final VcatnPaprEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Mapping(target = "schdulList", expression = "java(dto.getSchdulEntityList())")
    VcatnPaprEntity toEntity(final VcatnPaprDto.DTL dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @Mapping(target = "schdulList", expression = "java(dto.getSchdulEntityList())")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final VcatnPaprDto.DTL dto, final @MappingTarget VcatnPaprEntity entity) throws Exception;
}
