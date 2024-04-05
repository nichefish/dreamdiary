package io.nicheblog.dreamdiary.web.mapstruct.schdul;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.schdul.SchdulEntity;
import io.nicheblog.dreamdiary.web.entity.vcatn.papr.VcatnSchdulEntity;
import io.nicheblog.dreamdiary.web.model.schdul.SchdulCalDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * SchdulCalMapstruct
 * <pre>
 *  일정(달력) MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, DatePtn.class})
public interface SchdulCalMapstruct
        extends BaseMapstruct<SchdulCalDto, SchdulEntity> {

    SchdulCalMapstruct INSTANCE = Mappers.getMapper(SchdulCalMapstruct.class);

    /**
     * Entity -> CalDto
     */
    // 달력에선 종료일자에 시간 데이터(23:59:59)를 붙여줘야 한다.
    // 하루짜리 이벤트일 때만 allDay=true를 붙여준다.
    @Mapping(target = "display", expression = "java(\"hldy\".equals(entity.getSchdulCd()) ? \"background\" : null)")
    @Mapping(target = "color", expression = "java(\"hldy\".equals(entity.getSchdulCd()) ? \"red\" : null)")
    // @Mapping(target = "prtcpnt", expression = "java(entity.getPrtcpntStr())")
    @Mapping(target = "bgnDt", expression = "java(DateUtils.asStr(entity.getBgnDt(), DatePtn.DATE))")
    @Mapping(target = "endDt", expression = "java(DateUtils.Parser.eDateParseStr(entity.getEndDt(), DatePtn.ZDATETIME))")
    @Mapping(target = "allDay", expression = "java(entity.getEndDt() == null ? true : DateUtils.isSameDay(entity.getBgnDt(), entity.getEndDt()) ? true : false)")
    SchdulCalDto toCalDto(final SchdulEntity entity) throws Exception;

    /**
     * Entity -> CalDto
     */
    // 달력에선 종료일자에 시간 데이터(23:59:59)를 붙여줘야 한다.
    // 하루짜리 이벤트일 때만 allDay=true를 붙여준다.
    @Mapping(target = "title", expression = "java(entity.getUserNm() + \" \" + entity.getVcatnNm()) ")
    @Mapping(target = "bgnDt", expression = "java(DateUtils.asStr(entity.getBgnDt(), DatePtn.DATE))")
    @Mapping(target = "endDt", expression = "java(DateUtils.asStr(entity.getEndDt(), DatePtn.ZDATETIME))")
    @Mapping(target = "allDay", expression = "java(entity.getEndDt() == null ? true : DateUtils.isSameDay(entity.getBgnDt(), entity.getEndDt()) ? true : false)")
    SchdulCalDto toCalDto(final VcatnSchdulEntity entity) throws Exception;
}
