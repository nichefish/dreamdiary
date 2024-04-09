package io.nicheblog.dreamdiary.global.cmm.log.mapstruct;

import io.nicheblog.dreamdiary.global.cmm.log.entity.LogActvtyEntity;
import io.nicheblog.dreamdiary.global.cmm.log.entity.LogSysEntity;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogSysParam;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * LogMapstruct
 * <pre>
 *  로그 적재용 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface LogMapstruct {

    LogMapstruct INSTANCE = Mappers.getMapper(LogMapstruct.class);

    /**
     * logActvtyParam -> logActvtyEntity
     */
    @Mapping(target = "actvtyCtgrCd", expression = "java(param.getActvtyCtgr() != null ? param.getActvtyCtgr().name() : null)")
    LogActvtyEntity toEntity(final LogActvtyParam param) throws Exception;

    /**
     * logSysParam -> logSysEntity
     */
    @Mapping(target = "actvtyCtgrCd", expression = "java(param.getActvtyCtgr() != null ? param.getActvtyCtgr().name() : null)")
    LogSysEntity toEntity(final LogSysParam param) throws Exception;
}
