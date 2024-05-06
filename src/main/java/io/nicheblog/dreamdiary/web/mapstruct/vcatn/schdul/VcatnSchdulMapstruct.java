package io.nicheblog.dreamdiary.web.mapstruct.vcatn.schdul;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.vcatn.papr.VcatnSchdulEntity;
import io.nicheblog.dreamdiary.web.model.vcatn.schdul.VcatnSchdulDto;
import io.nicheblog.dreamdiary.web.model.vcatn.schdul.VcatnSchdulXlsxDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

/**
 * VcatnSchdulMapstruct
 * <pre>
 *  휴가 일정 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class})
public interface VcatnSchdulMapstruct
        extends BaseCrudMapstruct<VcatnSchdulDto, VcatnSchdulDto, VcatnSchdulEntity> {

    VcatnSchdulMapstruct INSTANCE = Mappers.getMapper(VcatnSchdulMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "bgnDt", expression = "java(DateUtils.asStr(entity.getBgnDt(), DatePtn.DATE))")
    @Mapping(target = "endDt", expression = "java(DateUtils.asStr(entity.getEndDt(), DatePtn.DATE))")
    VcatnSchdulDto toDto(final VcatnSchdulEntity entity) throws Exception;

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "bgnDt", expression = "java(DateUtils.asStr(entity.getBgnDt(), DatePtn.DATE))")
    @Mapping(target = "endDt", expression = "java(DateUtils.asStr(entity.getEndDt(), DatePtn.DATE))")
    VcatnSchdulDto toListDto(final VcatnSchdulEntity entity) throws Exception;

    /**
     * EntityList to DtoList
     */
    default List<VcatnSchdulDto> toDtoList(List<VcatnSchdulEntity> entityList) {
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
     * Dto -> DyXlsxDto
     */
    // @Mapping(target = "vcatnExprDy", expression = "java(Double.toString(dto.getVcatnExprDy()))")
    VcatnSchdulXlsxDto toDyXlsxDto(final VcatnSchdulDto dto) throws Exception;

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

    /**
     * DtoList to EntityList
     */
    default List<VcatnSchdulEntity> toEntityList(List<VcatnSchdulDto> dtoList) {
        return dtoList.stream()
                .map(dto -> {
                    String vcatnCd = dto.getVcatnCd();
                    boolean isHalf = Constant.VCATN_AM_HALF.equals(vcatnCd) || Constant.VCATN_PM_HALF.equals(vcatnCd);
                    if (isHalf) {
                        // 반차일 경우 시작일 = 종료일
                        dto.setBgnDt(dto.getBgnDt() + " 09:00:00");
                        dto.setEndDt(dto.getBgnDt() + " 14:00:00");
                    } else {
                        dto.setBgnDt(dto.getBgnDt() + " 01:00:00");
                        dto.setEndDt(dto.getEndDt() + " 23:59:59");
                    }
                    try {
                        return this.toEntity(dto);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
