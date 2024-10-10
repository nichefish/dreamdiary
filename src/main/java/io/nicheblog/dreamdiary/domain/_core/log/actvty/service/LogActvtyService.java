package io.nicheblog.dreamdiary.domain._core.log.actvty.service;

import io.nicheblog.dreamdiary.domain._core.log.actvty.entity.LogActvtyEntity;
import io.nicheblog.dreamdiary.domain._core.log.actvty.mapstruct.LogActvtyMapstruct;
import io.nicheblog.dreamdiary.domain._core.log.actvty.model.LogActvtyDto;
import io.nicheblog.dreamdiary.domain._core.log.actvty.spec.LogActvtySpec;
import io.nicheblog.dreamdiary.domain._core.log.actvty.repository.jpa.LogActvtyRepository;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseReadonlyService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * LogActvtyService
 * <pre>
 *  활동 로그 관리 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("logActvtyService")
@RequiredArgsConstructor
@Log4j2
public class LogActvtyService
        implements BaseReadonlyService<LogActvtyDto.DTL, LogActvtyDto.LIST, Integer, LogActvtyEntity, LogActvtyRepository, LogActvtySpec, LogActvtyMapstruct> {

    @Getter
    private final LogActvtyRepository repository;
    @Getter
    private final LogActvtySpec spec;
    @Getter
    private final LogActvtyMapstruct mapstruct = LogActvtyMapstruct.INSTANCE;
}
