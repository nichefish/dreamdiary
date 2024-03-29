package io.nicheblog.dreamdiary.web.mapstruct.exptr.prsnl;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.exptr.prsnl.ExptrPrsnlItemEntity;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr.ExptrPrsnlItemDto;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.rpt.ExptrPrsnlRptItemDto;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.rpt.ExptrPrsnlRptItemXlsxDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ExptrPrsnlItemMapstruct
 * <pre>
 *  경비 관리 > MapStruct 기반 Mapper 인터페이스
 *  ※경비지출항목(exptr_prsnl_item) = 경비지출서(exptr_prsnl_papr)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface ExptrPrsnlItemMapstruct
        extends BaseMapstruct<ExptrPrsnlItemDto, ExptrPrsnlItemEntity> {

    ExptrPrsnlItemMapstruct INSTANCE = Mappers.getMapper(ExptrPrsnlItemMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Mapping(target = "exptrDt", expression = "java(DateUtils.asStr(entity.getExptrDt(), DateUtils.PTN_DATE))")
    @Mapping(target = "atchFileDtlNo", expression = "java(entity.getAtchFileDtlNo() != null ? Integer.toString(entity.getAtchFileDtlNo()) : null)")
    ExptrPrsnlItemDto toDto(final ExptrPrsnlItemEntity entity) throws Exception;

    /**
     * Entity -> RptItemDto
     */
    @Mapping(target = "exptrDt", expression = "java(DateUtils.asStr(entity.getExptrDt(), DateUtils.PTN_DATE))")
    @Mapping(target = "atchFileDtlNo", expression = "java(entity.getAtchFileDtlNo() != null ? Integer.toString(entity.getAtchFileDtlNo()) : null)")
    ExptrPrsnlRptItemDto toRptItemDto(final ExptrPrsnlItemEntity entity) throws Exception;

    /**
     * RptItemDto -> RptItemXlsxDto
     */
    @Mapping(target = "atchFileAt", expression = "java(StringUtils.isNotEmpty(dto.getAtchFileDtlNo()) ? \"Y\" : \"N\")")
    @Mapping(target = "orgnlRciptAt", source = "orgnlRciptYn")
    ExptrPrsnlRptItemXlsxDto toRptItemXlsxDto(final ExptrPrsnlRptItemDto dto) throws Exception;

    /**
     * Dto -> Entity
     */
    @Override
    @Mapping(target = "exptrDt", expression = "java(DateUtils.asDate(dto.getExptrDt()))")
    @Mapping(target = "atchFileDtlNo", expression = "java(StringUtils.isNotEmpty(dto.getAtchFileDtlNo()) ? Integer.valueOf(dto.getAtchFileDtlNo()) : null)")
    ExptrPrsnlItemEntity toEntity(final ExptrPrsnlItemDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(
            final ExptrPrsnlItemDto dto,
            final @MappingTarget ExptrPrsnlItemEntity entity
    ) throws Exception;
}
