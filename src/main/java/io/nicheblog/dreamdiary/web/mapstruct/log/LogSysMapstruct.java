package io.nicheblog.dreamdiary.web.mapstruct.log;

import io.nicheblog.dreamdiary.global.cmm.log.entity.LogSysEntity;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.model.log.LogSysDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * LogSysMapstruct
 * <pre>
 *  시스템 로그 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface LogSysMapstruct
        extends BaseCrudMapstruct<LogSysDto, LogSysDto, LogSysEntity> {

    LogSysMapstruct INSTANCE = Mappers.getMapper(LogSysMapstruct.class);

}
