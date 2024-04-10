package io.nicheblog.dreamdiary.web.mapstruct.log;

import io.nicheblog.dreamdiary.global.cmm.log.entity.LogSysEntity;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.model.log.LogSysDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * LogSysMapstruct
 * <pre>
 *  시스템 로그 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, DatePtn.class}, builder = @Builder(disableBuilder = true))
public interface LogSysMapstruct
        extends BaseCrudMapstruct<LogSysDto.DTL, LogSysDto.LIST, LogSysEntity> {

    LogSysMapstruct INSTANCE = Mappers.getMapper(LogSysMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "logDt", expression = "java(DateUtils.asStr(entity.getLogDt(), DatePtn.DATETIME))")
    LogSysDto.DTL toDto(final LogSysEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "logDt", expression = "java(DateUtils.asStr(entity.getLogDt(), DatePtn.DATETIME))")
    LogSysDto.LIST toListDto(final LogSysEntity entity) throws Exception;
}
