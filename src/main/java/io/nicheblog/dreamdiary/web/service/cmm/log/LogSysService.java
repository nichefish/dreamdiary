package io.nicheblog.dreamdiary.web.service.cmm.log;

import io.nicheblog.dreamdiary.global.cmm.log.entity.LogSysEntity;
import io.nicheblog.dreamdiary.global.cmm.log.repository.LogSysRepository;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseReadonlyService;
import io.nicheblog.dreamdiary.web.mapstruct.log.LogSysMapstruct;
import io.nicheblog.dreamdiary.web.model.log.LogSysDto;
import io.nicheblog.dreamdiary.web.spec.log.LogSysSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * LogSysService
 * <pre>
 *  시스템 로그 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseReadonlyService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("logSysService")
@Log4j2
public class LogSysService
        implements BaseReadonlyService<LogSysDto, LogSysDto, Integer, LogSysEntity, LogSysRepository, LogSysSpec, LogSysMapstruct> {

    private final LogSysMapstruct logSysMapstruct = LogSysMapstruct.INSTANCE;

    @Resource(name = "logSysRepository")
    private LogSysRepository logSysRepository;
    @Resource(name = "logSysSpec")
    private LogSysSpec logSysSpec;

    @Override
    public LogSysRepository getRepository() {
        return this.logSysRepository;
    }

    @Override
    public LogSysSpec getSpec() {
        return this.logSysSpec;
    }

    @Override
    public LogSysMapstruct getMapstruct() {
        return this.logSysMapstruct;
    }

}
