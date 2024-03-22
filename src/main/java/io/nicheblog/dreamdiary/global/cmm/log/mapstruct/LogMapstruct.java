package io.nicheblog.dreamdiary.global.cmm.log.mapstruct;

import io.nicheblog.dreamdiary.global.cmm.log.entity.LogActvtyEntity;
import io.nicheblog.dreamdiary.global.cmm.log.entity.LogSysEntity;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogSysParam;
import io.nicheblog.dreamdiary.global.util.DateUtils;
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
public interface LogMapstruct {

    LogMapstruct INSTANCE = Mappers.getMapper(LogMapstruct.class);

    /**
     * Param -> Entity
     */
    @Mapping(target = "rslt", source = "isSuccess")
    LogActvtyEntity toEntity(final LogActvtyParam dto) throws Exception;

    @Mapping(target = "rslt", source = "isSuccess")
    LogSysEntity toEntity(final LogSysParam dto) throws Exception;
}
