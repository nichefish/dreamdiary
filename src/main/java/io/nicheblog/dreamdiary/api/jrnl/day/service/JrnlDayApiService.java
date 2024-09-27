package io.nicheblog.dreamdiary.api.jrnl.day.service;

import io.nicheblog.dreamdiary.api.jrnl.day.mapstruct.JrnlDayApiMapstruct;
import io.nicheblog.dreamdiary.api.jrnl.day.model.JrnlDayApiDto;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import io.nicheblog.dreamdiary.web.repository.jrnl.day.jpa.JrnlDayRepository;
import io.nicheblog.dreamdiary.web.spec.jrnl.day.JrnlDaySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * JrnlDayApiService
 * <pre>
 *  API:: 저널 일자 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseMultiCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class JrnlDayApiService
        implements BaseMultiCrudService<JrnlDayApiDto, JrnlDayApiDto, Integer, JrnlDayEntity, JrnlDayRepository, JrnlDaySpec, JrnlDayApiMapstruct> {

    private final JrnlDayRepository jrnlDayRepository;
    private final JrnlDaySpec jrnlDaySpec;
    private final JrnlDayApiMapstruct jrnlDayMapstruct = JrnlDayApiMapstruct.INSTANCE;

    @Override
    public JrnlDayRepository getRepository() {
        return this.jrnlDayRepository;
    }

    @Override
    public JrnlDayApiMapstruct getMapstruct() {
        return this.jrnlDayMapstruct;
    }

    @Override
    public JrnlDaySpec getSpec() {
        return this.jrnlDaySpec;
    }
}