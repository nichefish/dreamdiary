package io.nicheblog.dreamdiary.global._common.flsys.service;

import io.nicheblog.dreamdiary.global._common.flsys.entity.FlsysMetaEntity;
import io.nicheblog.dreamdiary.global._common.flsys.mapstruct.FlsysMetaMapstruct;
import io.nicheblog.dreamdiary.global._common.flsys.model.FlsysMetaDto;
import io.nicheblog.dreamdiary.global._common.flsys.repository.jpa.FlsysMetaRepository;
import io.nicheblog.dreamdiary.global._common.flsys.spec.FlsysMetaSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import lombok.Getter;
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

    @Getter
    private final FlsysMetaRepository repository;
    @Getter
    private final FlsysMetaSpec spec;
    @Getter
    private final FlsysMetaMapstruct mapstruct = FlsysMetaMapstruct.INSTANCE;
}