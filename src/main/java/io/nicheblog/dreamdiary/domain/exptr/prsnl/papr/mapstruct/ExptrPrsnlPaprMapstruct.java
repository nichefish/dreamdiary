package io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.mapstruct;

import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.entity.ExptrPrsnlPaprEntity;
import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.model.ExptrPrsnlPaprDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BasePostMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ExptrPrsnlPaprMapstruct
 * <pre>
 *  경비 관리 > 경비지출서 관리 MapStruct 기반 Mapper 인터페이스.
 *  ※ 경비지출서(exptr_prsnl_papr) = 경비지출서. 경비지출항목(exptr_prsnl_item)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class}, builder = @Builder(disableBuilder = true))
public interface ExptrPrsnlPaprMapstruct
        extends BasePostMapstruct<ExptrPrsnlPaprDto.DTL, ExptrPrsnlPaprDto.LIST, ExptrPrsnlPaprEntity> {

    ExptrPrsnlPaprMapstruct INSTANCE = Mappers.getMapper(ExptrPrsnlPaprMapstruct.class);

    /**
     * Entity -> Dto 변환
     *
     * @param entity 변환할 Entity 객체
     * @return Dto -- 변환된 Dto 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Override
    @Named("toDto")
    @Mapping(target = "itemList", expression = "java(ExptrPrsnlItemMapstruct.INSTANCE.toDtoList(entity.getItemList()))")
    ExptrPrsnlPaprDto.DTL toDto(final ExptrPrsnlPaprEntity entity) throws Exception;

    /**
     * Entity -> ListDto 변환
     *
     * @param entity 변환할 Entity 객체
     * @return ListDto -- 변환된 ListDto 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "itemList", expression = "java(ExptrPrsnlItemMapstruct.INSTANCE.toDtoList(entity.getItemList()))")
    ExptrPrsnlPaprDto.LIST toListDto(final ExptrPrsnlPaprEntity entity) throws Exception;

    /**
     * StatsDto -> StatsXlsxDto
     */
    // @Mapping(target = "emplymNm", expression = "java(dto.getUser() != null ? dto.getUser().getEmplymNm() : \"\")")
    // @Mapping(target = "cmpyNm", expression = "java(dto.getUser() != null ? dto.getUser().getCmpyNm() : \"\")")
    // @Mapping(target = "userNm", expression = "java(dto.getUser() != null ? dto.getUser().getUserNm() : \"\")")
    // @Mapping(target = "jan", expression = "java(dto.getExptrPrsnlList().get(0) != null ? dto.getExptrPrsnlList().get(0).getTotAmt() : 0)")
    // @Mapping(target = "feb", expression = "java(dto.getExptrPrsnlList().get(1) != null ? dto.getExptrPrsnlList().get(1).getTotAmt() : 0)")
    // @Mapping(target = "mar", expression = "java(dto.getExptrPrsnlList().get(2) != null ? dto.getExptrPrsnlList().get(2).getTotAmt() : 0)")
    // @Mapping(target = "apr", expression = "java(dto.getExptrPrsnlList().get(3) != null ? dto.getExptrPrsnlList().get(3).getTotAmt() : 0)")
    // @Mapping(target = "may", expression = "java(dto.getExptrPrsnlList().get(4) != null ? dto.getExptrPrsnlList().get(4).getTotAmt() : 0)")
    // @Mapping(target = "jun", expression = "java(dto.getExptrPrsnlList().get(5) != null ? dto.getExptrPrsnlList().get(5).getTotAmt() : 0)")
    // @Mapping(target = "jul", expression = "java(dto.getExptrPrsnlList().get(6) != null ? dto.getExptrPrsnlList().get(6).getTotAmt() : 0)")
    // @Mapping(target = "aug", expression = "java(dto.getExptrPrsnlList().get(7) != null ? dto.getExptrPrsnlList().get(7).getTotAmt() : 0)")
    // @Mapping(target = "sep", expression = "java(dto.getExptrPrsnlList().get(8) != null ? dto.getExptrPrsnlList().get(8).getTotAmt() : 0)")
    // @Mapping(target = "oct", expression = "java(dto.getExptrPrsnlList().get(9) != null ? dto.getExptrPrsnlList().get(9).getTotAmt() : 0)")
    // @Mapping(target = "nov", expression = "java(dto.getExptrPrsnlList().get(10) != null ? dto.getExptrPrsnlList().get(10).getTotAmt() : 0)")
    // @Mapping(target = "dec", expression = "java(dto.getExptrPrsnlList().get(11) != null ? dto.getExptrPrsnlList().get(11).getTotAmt() : 0)")
    // ExptrPrsnlStatsListXlsxDto toListXlsxDto(final ExptrPrsnlStatsDto dto) throws Exception;

    /**
     * Dto -> Entity 변환
     *
     * @param dto 변환할 Dto 객체
     * @return Entity -- 변환된 Entity 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Override
    @Mapping(target = "itemList", expression = "java(ExptrPrsnlItemMapstruct.INSTANCE.toEntityList(dto.getItemList()))")
    ExptrPrsnlPaprEntity toEntity(final ExptrPrsnlPaprDto.DTL dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     *
     * @param dto 업데이트할 DTO 객체
     * @param entity 업데이트할 대상 엔티티 객체
     * @throws Exception 매핑 중 발생할 수 있는 예외
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "itemList", expression = "java(ExptrPrsnlItemMapstruct.INSTANCE.toEntityList(dto.getItemList()))")
    void updateFromDto(final ExptrPrsnlPaprDto.DTL dto, final @MappingTarget ExptrPrsnlPaprEntity entity) throws Exception;
}
