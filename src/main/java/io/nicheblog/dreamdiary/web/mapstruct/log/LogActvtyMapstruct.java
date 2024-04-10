package io.nicheblog.dreamdiary.web.mapstruct.log;

import io.nicheblog.dreamdiary.global.cmm.log.entity.LogActvtyEntity;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.model.log.LogActvtyDto;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * LogActvtyMapstruct
 * <pre>
 *  활동 로그 MapStruct 기반 Mapper 인터페이스
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudMapstruct:: 기본 변환 매핑 로직 상속
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {DateUtils.class, StringUtils.class, DatePtn.class}, builder = @Builder(disableBuilder = true))
public interface LogActvtyMapstruct
        extends BaseCrudMapstruct<LogActvtyDto.DTL, LogActvtyDto.LIST, LogActvtyEntity> {

    LogActvtyMapstruct INSTANCE = Mappers.getMapper(LogActvtyMapstruct.class);

    /**
     * Entity -> Dto
     */
    @Override
    @Named("toDto")
    @Mapping(target = "logDt", expression = "java(DateUtils.asStr(entity.getLogDt(), DatePtn.DATETIME))")
    LogActvtyDto.DTL toDto(final LogActvtyEntity entity) throws Exception;

    /**
     * Entity -> ListDto
     */
    @Override
    @Named("toListDto")
    @Mapping(target = "logDt", expression = "java(DateUtils.asStr(entity.getLogDt(), DatePtn.DATETIME))")
    LogActvtyDto.LIST toListDto(final LogActvtyEntity entity) throws Exception;
}
