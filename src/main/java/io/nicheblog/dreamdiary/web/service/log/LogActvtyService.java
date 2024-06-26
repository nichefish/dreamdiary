package io.nicheblog.dreamdiary.web.service.log;

import io.nicheblog.dreamdiary.global.cmm.log.entity.LogActvtyEntity;
import io.nicheblog.dreamdiary.global.cmm.log.repository.LogActvtyRepository;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseReadonlyService;
import io.nicheblog.dreamdiary.web.mapstruct.log.LogActvtyMapstruct;
import io.nicheblog.dreamdiary.web.model.log.LogActvtyDto;
import io.nicheblog.dreamdiary.web.spec.log.LogActvtySpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * LogActvtyService
 * <pre>
 *  활동 로그 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseReadonlyService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("logActvtyService")
@Log4j2
public class LogActvtyService
        implements BaseReadonlyService<LogActvtyDto.DTL, LogActvtyDto.LIST, Integer, LogActvtyEntity, LogActvtyRepository, LogActvtySpec, LogActvtyMapstruct> {

    private final LogActvtyMapstruct logActvtyMapstruct = LogActvtyMapstruct.INSTANCE;

    @Resource(name = "logActvtyRepository")
    private LogActvtyRepository logActvtyRepository;
    @Resource(name = "logActvtySpec")
    private LogActvtySpec logActvtySpec;

    @Override
    public LogActvtyRepository getRepository() {
        return this.logActvtyRepository;
    }

    @Override
    public LogActvtySpec getSpec() {
        return this.logActvtySpec;
    }

    @Override
    public LogActvtyMapstruct getMapstruct() {
        return this.logActvtyMapstruct;
    }

}
