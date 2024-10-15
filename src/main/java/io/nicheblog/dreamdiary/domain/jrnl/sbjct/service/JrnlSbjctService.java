package io.nicheblog.dreamdiary.domain.jrnl.sbjct.service;

import io.nicheblog.dreamdiary.domain.jrnl.sbjct.entity.JrnlSbjctEntity;
import io.nicheblog.dreamdiary.domain.jrnl.sbjct.mapstruct.JrnlSbjctMapstruct;
import io.nicheblog.dreamdiary.domain.jrnl.sbjct.model.JrnlSbjctDto;
import io.nicheblog.dreamdiary.domain.jrnl.sbjct.repository.jpa.JrnlSbjctRepository;
import io.nicheblog.dreamdiary.domain.jrnl.sbjct.spec.JrnlSbjctSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BasePostService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * JrnlSbjctService
 * <pre>
 *  저널 주제 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("jrnlSbjctService")
@RequiredArgsConstructor
public class JrnlSbjctService
        implements BasePostService<JrnlSbjctDto.DTL, JrnlSbjctDto.LIST, Integer, JrnlSbjctEntity, JrnlSbjctRepository, JrnlSbjctSpec, JrnlSbjctMapstruct> {

    @Getter
    private final JrnlSbjctRepository repository;
    @Getter
    private final JrnlSbjctSpec spec;
    @Getter
    private final JrnlSbjctMapstruct mapstruct = JrnlSbjctMapstruct.INSTANCE;
}