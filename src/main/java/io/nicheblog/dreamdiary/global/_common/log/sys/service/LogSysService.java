package io.nicheblog.dreamdiary.global._common.log.sys.service;

import io.nicheblog.dreamdiary.global.ActiveProfile;
import io.nicheblog.dreamdiary.global._common.log.actvty.spec.LogSysSpec;
import io.nicheblog.dreamdiary.global._common.log.sys.entity.LogSysEntity;
import io.nicheblog.dreamdiary.global._common.log.sys.mapstruct.LogSysMapstruct;
import io.nicheblog.dreamdiary.global._common.log.sys.model.LogSysDto;
import io.nicheblog.dreamdiary.global._common.log.sys.model.LogSysParam;
import io.nicheblog.dreamdiary.global._common.log.sys.repository.jpa.LogSysRepository;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseReadonlyService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class LogSysService
        implements BaseReadonlyService<LogSysDto.DTL, LogSysDto.LIST, Integer, LogSysEntity, LogSysRepository, LogSysSpec, LogSysMapstruct> {

    @Getter
    private final LogSysRepository repository;
    @Getter
    private final LogSysSpec spec;
    @Getter
    private final LogSysMapstruct mapstruct = LogSysMapstruct.INSTANCE;

    private final ActiveProfile activeProfile;

    /**
     * 시스템 로그 등록
     *
     * @param logParam 시스템 로그 파라미터
     * @return {@link Boolean} -- 로그 등록 성공 여부
     * @throws Exception 로그 등록 중 발생할 수 있는 예외
     */
    public Boolean regSysActvty(final LogSysParam logParam) throws Exception {
        // 로컬 또는 개발 접속은 시스템 로그 남기지 않음
        if (!activeProfile.isProd()) return true;

        final LogSysEntity logActvty = mapstruct.paramToEntity(logParam);

        log.info("isSuccess: {}, rsltMsg: {}", logParam.getRslt(), logParam.getRsltMsg());
        final LogSysEntity rslt = repository.save(logActvty);

        return rslt.getLogSysNo() != null;
    }
}
