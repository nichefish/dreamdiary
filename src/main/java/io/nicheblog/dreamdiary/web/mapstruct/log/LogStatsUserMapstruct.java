package io.nicheblog.dreamdiary.web.mapstruct.log;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.log.LogStatsUserEntity;
import io.nicheblog.dreamdiary.web.model.log.LogStatsUserDto;
import io.nicheblog.dreamdiary.web.model.log.LogStatsUserIntrfc;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * LogActvtyMapstruct
 * <pre>
 *  사용자별 로그 통계 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseMapstruct
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class})
public interface LogStatsUserMapstruct
        extends BaseMapstruct<LogStatsUserDto, LogStatsUserEntity> {

    LogStatsUserMapstruct INSTANCE = Mappers.getMapper(LogStatsUserMapstruct.class);

    /**
     * Dto -> Entity
     */
    @Override
    LogStatsUserEntity toEntity(final LogStatsUserDto dto) throws Exception;

    /**
     * Entity -> Dto
     */
    @Override
    LogStatsUserDto toDto(final LogStatsUserEntity entity) throws Exception;

    /**
     * Entity -> Dto
     */
    LogStatsUserDto toDto(final LogStatsUserIntrfc entity) throws Exception;

    /**
     * Entity -> ListXlsxDto
     */
    // LogActvtyListXlsxDto toListXlsxDto(final LogStatsUserEntity entity) throws Exception;
}
