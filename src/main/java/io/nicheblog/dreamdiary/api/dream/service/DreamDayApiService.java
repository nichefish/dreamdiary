package io.nicheblog.dreamdiary.api.dream.service;

import io.nicheblog.dreamdiary.api.dream.mapstruct.DreamDayApiMapstruct;
import io.nicheblog.dreamdiary.api.dream.model.DreamDayApiDto;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.web.entity.dream.DreamDayEntity;
import io.nicheblog.dreamdiary.web.repository.dream.DreamDayRepository;
import io.nicheblog.dreamdiary.web.spec.dream.DreamDaySpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * DreamDayApiService
 * <pre>
 *  API:: 꿈 일자 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseMultiCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service
@Log4j2
public class DreamDayApiService
        implements BaseMultiCrudService<DreamDayApiDto, DreamDayApiDto, Integer, DreamDayEntity, DreamDayRepository, DreamDaySpec, DreamDayApiMapstruct> {

    private final DreamDayApiMapstruct dreamDayMapstruct = DreamDayApiMapstruct.INSTANCE;

    @Resource(name = "dreamDayRepository")
    private DreamDayRepository dreamDayRepository;
    @Resource(name = "dreamDaySpec")
    private DreamDaySpec dreamDaySpec;

    @Override
    public DreamDayRepository getRepository() {
        return this.dreamDayRepository;
    }

    @Override
    public DreamDayApiMapstruct getMapstruct() {
        return this.dreamDayMapstruct;
    }

    @Override
    public DreamDaySpec getSpec() {
        return this.dreamDaySpec;
    }
}