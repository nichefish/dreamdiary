package io.nicheblog.dreamdiary.domain.exptr.reqst.mapstruct;

import io.nicheblog.dreamdiary.domain.exptr.reqst.entity.ExptrReqstEntity;
import io.nicheblog.dreamdiary.domain.exptr.reqst.model.ExptrReqstDto;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BasePostMapstruct;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * ExptrReqstMapstruct
 * <pre>
 *  물품구매/경조사비 신청 MapStruct 기반 Mapper 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, CmmUtils.class}, builder = @Builder(disableBuilder = true))
public interface ExptrReqstMapstruct
        extends BasePostMapstruct<ExptrReqstDto.DTL, ExptrReqstDto.LIST, ExptrReqstEntity> {

    ExptrReqstMapstruct INSTANCE = Mappers.getMapper(ExptrReqstMapstruct.class);

    /**
     * Entity -> Dto 변환
     *
     * @param entity 변환할 Entity 객체
     * @return Dto -- 변환된 Dto 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Override
    @Named("toDto")
    @Mapping(target = "markdownCn", expression = "java(StringUtils.isEmpty(entity.getCn()) ? \"-\" : CmmUtils.markdown(entity.getCn()))")
    ExptrReqstDto.DTL toDto(final ExptrReqstEntity entity) throws Exception;

    /**
     * Entity -> ListDto 변환
     *
     * @param entity 변환할 Entity 객체
     * @return ListDto -- 변환된 ListDto 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Named("toListDto")
    @Mapping(target = "markdownCn", expression = "java(StringUtils.isEmpty(entity.getCn()) ? \"-\" : CmmUtils.markdown(entity.getCn()))")
    ExptrReqstDto.LIST toListDto(final ExptrReqstEntity entity) throws Exception;

    /**
     * Dto -> Entity 변환
     *
     * @param dto 변환할 Dto 객체
     * @return Entity -- 변환된 Entity 객체
     * @throws Exception 변환 중 발생할 수 있는 예외
     */
    @Override
    ExptrReqstEntity toEntity(final ExptrReqstDto.DTL dto) throws Exception;

    /**
     * update Entity from Dto (Dto에서 null이 아닌 값만 Entity로 매핑)
     *
     * @param dto 업데이트할 DTO 객체
     * @param entity 업데이트할 대상 엔티티 객체
     * @throws Exception 매핑 중 발생할 수 있는 예외
     */
    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(final ExptrReqstDto.DTL dto, final @MappingTarget ExptrReqstEntity entity) throws Exception;
}
