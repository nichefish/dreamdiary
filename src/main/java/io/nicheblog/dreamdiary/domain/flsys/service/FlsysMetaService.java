package io.nicheblog.dreamdiary.domain.flsys.service;

import io.nicheblog.dreamdiary.domain.flsys.entity.FlsysMetaEntity;
import io.nicheblog.dreamdiary.domain.flsys.mapstruct.FlsysMetaMapstruct;
import io.nicheblog.dreamdiary.domain.flsys.model.FlsysMetaDto;
import io.nicheblog.dreamdiary.domain.flsys.repository.jpa.FlsysMetaRepository;
import io.nicheblog.dreamdiary.domain.flsys.spec.FlsysMetaSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * FlsysMetaService
 * <pre>
 *  파일시스템 메타 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("flsysMetaService")
@RequiredArgsConstructor
public class FlsysMetaService
        implements BaseMultiCrudService<FlsysMetaDto, FlsysMetaDto, Integer, FlsysMetaEntity, FlsysMetaRepository, FlsysMetaSpec, FlsysMetaMapstruct> {

    private final FlsysMetaRepository flsysMetaRepository;
    private final FlsysMetaSpec flsysMetaSpec;
    private final FlsysMetaMapstruct flsysMetaMapstruct = FlsysMetaMapstruct.INSTANCE;

    @Override
    public FlsysMetaRepository getRepository() {
        return this.flsysMetaRepository;
    }

    @Override
    public FlsysMetaSpec getSpec() {
        return this.flsysMetaSpec;
    }

    @Override
    public FlsysMetaMapstruct getMapstruct() {
        return this.flsysMetaMapstruct;
    }
}