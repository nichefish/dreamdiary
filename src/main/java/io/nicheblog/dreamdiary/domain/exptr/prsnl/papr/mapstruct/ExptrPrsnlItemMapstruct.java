package io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.mapstruct;

import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.entity.ExptrPrsnlItemEntity;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.model.ExptrPrsnlItemDto;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.rpt.model.ExptrPrsnlRptItemDto;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.rpt.model.ExptrPrsnlRptItemXlsxDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ExptrPrsnlItemMapstruct
 * <pre>
 *  경비 관리 > MapStruct 기반 Mapper 인터페이스.
 *  ※경비지출항목(exptr_prsnl_item) = 경비지출서(exptr_prsnl_papr)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class})
public interface ExptrPrsnlItemMapstruct
        extends BaseMapstruct<ExptrPrsnlItemDto, ExptrPrsnlItemEntity> {

    ExptrPrsnlItemMapstruct INSTANCE = Mappers.getMapper(ExptrPrsnlItemMapstruct.class);

    /**
     * Entity -> Dto 변환
     *
     * @param entity 변환할 Entity 객체
     * @return Dto -- 변환된 Dto 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Override
    @Mapping(target = "exptrDt", expression = "java(DateUtils.asStr(entity.getExptrDt(), DatePtn.DATE))")
    ExptrPrsnlItemDto toDto(final ExptrPrsnlItemEntity entity) throws Exception;

    /**
     * Entity -> RptItemDto 변환
     *
     * @param entity 변환할 Entity 객체
     * @return Dto -- 변환된 RptItemDto 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Mapping(target = "exptrDt", expression = "java(DateUtils.asStr(entity.getExptrDt(), DatePtn.DATE))")
    ExptrPrsnlRptItemDto toRptItemDto(final ExptrPrsnlItemEntity entity) throws Exception;

    /**
     * Dto -> RptItemXlsxDto 변환
     *
     * @param dto 변환할 Entity 객체
     * @return Dto -- 변환된 RptItemXlsxDto 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Mapping(target = "atchFileAt", expression = "java(StringUtils.isNotEmpty(dto.getAtchFileDtlNo()) ? \"Y\" : \"N\")")
    @Mapping(target = "orgnlRciptAt", source = "orgnlRciptYn")
    ExptrPrsnlRptItemXlsxDto toRptItemXlsxDto(final ExptrPrsnlRptItemDto dto) throws Exception;

    /**
     * EntityList to DtoList
     *
     * @param entityList 변환할 Entity 목록
     * @return {@link List} -- 변환된 Dto 목록
     */
    default List<ExptrPrsnlItemDto> toDtoList(final List<ExptrPrsnlItemEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) return null;
        return entityList.stream()
                .map(entity -> {
                    try {
                        return this.toDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Dto -> Entity 변환
     *
     * @param dto 변환할 Dto 객체
     * @return Entity -- 변환된 Entity 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Override
    @Mapping(target = "exptrDt", expression = "java(DateUtils.asDate(dto.getExptrDt()))")
    ExptrPrsnlItemEntity toEntity(final ExptrPrsnlItemDto dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     *
     * @param dto 업데이트할 DTO 객체
     * @param entity 업데이트할 대상 엔티티 객체
     * @throws Exception 매핑 중 발생할 수 있는 예외
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final ExptrPrsnlItemDto dto, final @MappingTarget ExptrPrsnlItemEntity entity) throws Exception;
}
