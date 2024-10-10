package io.nicheblog.dreamdiary.domain._core.log.sys.service;

import io.nicheblog.dreamdiary.domain._core.log.actvty.spec.LogSysSpec;
import io.nicheblog.dreamdiary.domain._core.log.sys.entity.LogSysEntity;
import io.nicheblog.dreamdiary.domain._core.log.sys.mapstruct.LogSysMapstruct;
import io.nicheblog.dreamdiary.domain._core.log.sys.model.LogSysDto;
import io.nicheblog.dreamdiary.domain._core.log.sys.repository.jpa.LogSysRepository;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseReadonlyService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * LogSysService
 * <pre>
 *  시스템 로그 관리 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("logSysService")
@RequiredArgsConstructor
public class LogSysService
        implements BaseReadonlyService<LogSysDto.DTL, LogSysDto.LIST, Integer, LogSysEntity, LogSysRepository, LogSysSpec, LogSysMapstruct> {

    @Getter
    private final LogSysRepository repository;
    @Getter
    private final LogSysSpec spec;
    @Getter
    private final LogSysMapstruct mapstruct = LogSysMapstruct.INSTANCE;
}
