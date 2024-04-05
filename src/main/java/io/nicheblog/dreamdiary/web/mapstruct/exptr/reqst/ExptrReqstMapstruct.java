package io.nicheblog.dreamdiary.web.mapstruct.exptr.reqst;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseClsfMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.exptr.reqst.ExptrReqstEntity;
import io.nicheblog.dreamdiary.web.model.exptr.reqst.ExptrReqstDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ExptrReqstMapstruct
 * <pre>
 *  물품구매/경조사비 신청 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseClsfMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface ExptrReqstMapstruct
        extends BaseClsfMapstruct<ExptrReqstDto.DTL, ExptrReqstDto.LIST, ExptrReqstEntity> {

    ExptrReqstMapstruct INSTANCE = Mappers.getMapper(ExptrReqstMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    ExptrReqstDto.DTL toDto(final ExptrReqstEntity entity) throws Exception;

    /**
     * Entity -> listDto
     */
    @Named("toListDto")
    ExptrReqstDto.LIST toListDto(final ExptrReqstEntity entity) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    ExptrReqstEntity toEntity(final ExptrReqstDto.DTL dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final ExptrReqstDto.DTL dto, final @MappingTarget ExptrReqstEntity entity) throws Exception;
}
