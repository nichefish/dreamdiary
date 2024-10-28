package io.nicheblog.dreamdiary.global._common.file.service;

import io.nicheblog.dreamdiary.global._common.file.entity.AtchFileEntity;
import io.nicheblog.dreamdiary.global._common.file.mapstruct.AtchFileMapstruct;
import io.nicheblog.dreamdiary.global._common.file.model.AtchFileDto;
import io.nicheblog.dreamdiary.global._common.file.repository.jpa.AtchFileRepository;
import io.nicheblog.dreamdiary.global._common.file.spec.AtchFileSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * AtchFileService
 * <pre>
 *  공통 > 파일 처리 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service
@RequiredArgsConstructor
public class AtchFileService
        implements BaseCrudService<AtchFileDto, AtchFileDto, Integer, AtchFileEntity, AtchFileRepository, AtchFileSpec, AtchFileMapstruct> {

    @Getter
    private final AtchFileRepository repository;
    @Getter
    private final AtchFileSpec spec;
    @Getter
    private final AtchFileMapstruct mapstruct = AtchFileMapstruct.INSTANCE;
}
