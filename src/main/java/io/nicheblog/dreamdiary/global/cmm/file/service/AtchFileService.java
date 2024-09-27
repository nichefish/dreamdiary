package io.nicheblog.dreamdiary.global.cmm.file.service;

import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileEntity;
import io.nicheblog.dreamdiary.global.cmm.file.mapstruct.AtchFileMapstruct;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDto;
import io.nicheblog.dreamdiary.global.cmm.file.repository.jpa.AtchFileRepository;
import io.nicheblog.dreamdiary.global.cmm.file.spec.AtchFileSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * AtchFileService
 * <pre>
 *  공통 > 파일 처리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AtchFileService
        implements BaseCrudService<AtchFileDto, AtchFileDto, Integer, AtchFileEntity, AtchFileRepository, AtchFileSpec, AtchFileMapstruct> {

    private final AtchFileRepository atchFileRepository;
    private final AtchFileSpec atchFileSpec;
    private final AtchFileMapstruct atchFileMapstruct = AtchFileMapstruct.INSTANCE;

    @Override
    public AtchFileRepository getRepository() {
        return this.atchFileRepository;
    }
    @Override
    public AtchFileSpec getSpec() {
        return this.atchFileSpec;
    }
    @Override
    public AtchFileMapstruct getMapstruct() {
        return this.atchFileMapstruct;
    }
}
