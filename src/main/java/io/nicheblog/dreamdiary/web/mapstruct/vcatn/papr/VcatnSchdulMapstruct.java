package io.nicheblog.dreamdiary.web.mapstruct.vcatn.papr;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
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
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, DatePtn.class, StringUtils.class})
public interface VcatnSchdulMapstruct
        extends BaseListMapstruct<VcatnSchdulDto, VcatnDyDto, VcatnSchdulEntity> {

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
     * Entity -> CalDto
     */
    // 달력에선 종료일자에 시간 데이터(23:59:59)를 붙여줘야 한다.
    // 하루짜리 이벤트일 때만 allDay=true를 붙여준다.
    // @Mapping(target="title", expression="java((entity.getSchdulCdInfo() != null) ? entity.getSchdulCdInfo().getDtlCdNm() : schdulNm)")
    // @Mapping(target="display", expression="java(\"hldy\".equals(entity.getSchdulCd()) ? \"background\" : null)")
    // @Mapping(target="color", expression="java(\"hldy\".equals(entity.getSchdulCd()) ? \"red\" : null)")
    @Mapping(target = "bgnDt", expression = "java(DateUtils.asStr(entity.getBgnDt(), DatePtn.DATE))")
    @Mapping(target = "endDt", expression = "java(DateUtils.asStr(entity.getEndDt(), DatePtn.ZDATETIME))")
    @Mapping(target = "allDay", expression = "java(entity.getEndDt() == null ? true : DateUtils.isSameDay(entity.getBgnDt(), entity.getEndDt()) ? true : false)")
    SchdulCalDto toCalDto(final VcatnSchdulEntity entity) throws Exception;

    /**
     * Dto -> CalDto (endDt 시간정보를 수동으로 넣어준다.)
     */
    // 달력에선 종료일자에 시간 데이터(23:59:59)를 붙여줘야 한다.
    // 하루짜리 이벤트일 때만 allDay=true를 붙여준다.
    @Mapping(target = "title", expression = "java(StringUtils.isNotEmpty(dto.getUserNm()) ? dto.getUserNm() + \" \" + dto.getVcatnNm() : dto.getVcatnNm())")
    // @Mapping(target="display", expression="java(\"hldy\".equals(entity.getSchdulCd()) ? \"background\" : null)")
    // @Mapping(target="color", expression="java(\"hldy\".equals(entity.getSchdulCd()) ? \"red\" : null)")
    @Mapping(target = "endDt", expression = "java(StringUtils.isNotEmpty(dto.getEndDt()) ? dto.getEndDt() + \"T23:59:59\" : null)")
    @Mapping(target = "allDay", expression = "java(StringUtils.isEmpty(dto.getEndDt()) ? true : DateUtils.isSameDay(dto.getBgnDt(), dto.getEndDt()) ? true : false)")
    SchdulCalDto toCalDto(final VcatnSchdulDto dto) throws Exception;

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
    void updateFromDto(
            final VcatnSchdulDto dto,
            final @MappingTarget VcatnSchdulEntity entity
    ) throws Exception;
}
