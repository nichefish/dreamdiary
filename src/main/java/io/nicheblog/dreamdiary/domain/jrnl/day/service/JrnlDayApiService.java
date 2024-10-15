package io.nicheblog.dreamdiary.domain.jrnl.day.service;

import io.nicheblog.dreamdiary.domain.jrnl.day.entity.JrnlDayEntity;
import io.nicheblog.dreamdiary.domain.jrnl.day.mapstruct.JrnlDayApiMapstruct;
import io.nicheblog.dreamdiary.domain.jrnl.day.model.JrnlDayApiDto;
import io.nicheblog.dreamdiary.domain.jrnl.day.repository.jpa.JrnlDayRepository;
import io.nicheblog.dreamdiary.domain.jrnl.day.spec.JrnlDaySpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * JrnlDayApiService
 * <pre>
 *  API:: 저널 일자 관리 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service
@RequiredArgsConstructor
public class JrnlDayApiService
        implements BaseMultiCrudService<JrnlDayApiDto, JrnlDayApiDto, Integer, JrnlDayEntity, JrnlDayRepository, JrnlDaySpec, JrnlDayApiMapstruct> {

    @Getter
    private final JrnlDayRepository repository;
    @Getter
    private final JrnlDaySpec spec;
    @Getter
    private final JrnlDayApiMapstruct mapstruct = JrnlDayApiMapstruct.INSTANCE;
}