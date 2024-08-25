package io.nicheblog.dreamdiary.global.cmm.file.service;

import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileEntity;
import io.nicheblog.dreamdiary.global.cmm.file.mapstruct.AtchFileMapstruct;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDto;
import io.nicheblog.dreamdiary.global.cmm.file.repository.jpa.AtchFileRepository;
import io.nicheblog.dreamdiary.global.cmm.file.spec.AtchFileSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * AtchFileService
 * <pre>
 *  공통 > 파일 처리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service
@Log4j2
public class AtchFileService
        implements BaseCrudService<AtchFileDto, AtchFileDto, Integer, AtchFileEntity, AtchFileRepository, AtchFileSpec, AtchFileMapstruct> {

    AtchFileMapstruct atchFileMapstruct = AtchFileMapstruct.INSTANCE;

    @Resource(name = "atchFileRepository")
    private AtchFileRepository atchFileRepository;
    @Resource(name = "atchFileSpec")
    private AtchFileSpec atchFileSpec;

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
