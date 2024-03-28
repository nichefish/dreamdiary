package io.nicheblog.dreamdiary.web.mapstruct.log;

import io.nicheblog.dreamdiary.global.cmm.log.entity.LogActvtyEntity;
import io.nicheblog.dreamdiary.global.cmm.log.entity.LogSysEntity;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogSysParam;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.board.BoardDefEntity;
import io.nicheblog.dreamdiary.web.model.board.BoardDefDto;
import io.nicheblog.dreamdiary.web.model.log.LogSysDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * LogActvtyMapstruct
 * <pre>
 *  활동 로그 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseListMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface LogSysMapstruct
        extends BaseListMapstruct<LogSysDto, LogSysDto, LogSysEntity> {

    LogSysMapstruct INSTANCE = Mappers.getMapper(LogSysMapstruct.class);

}
