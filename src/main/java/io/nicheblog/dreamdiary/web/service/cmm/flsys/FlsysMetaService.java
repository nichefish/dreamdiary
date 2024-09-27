package io.nicheblog.dreamdiary.web.service.cmm.flsys;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.web.entity.cmm.flsys.FlsysMetaEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.flsys.FlsysMetaMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.flsys.FlsysMetaDto;
import io.nicheblog.dreamdiary.web.repository.cmm.flsys.jpa.FlsysMetaRepository;
import io.nicheblog.dreamdiary.web.spec.cmm.flsys.FlsysMetaSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * FlsysMetaService
 * <pre>
 *  파일시스템 메타 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseMultiCrudInterface:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("flsysMetaService")
@RequiredArgsConstructor
@Log4j2
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

    /**
     * 등록 전처리 :: override
     */
    @Override
    public void preRegist(final FlsysMetaDto flsysMetaDto) throws Exception {
        // 태그를 먼저 처리해준다. :: 메소드 분리
        // boardTagService.processTagList(flsysMetaDto);
    }

    /**
     * 파일시스템 메타 수정 전처리
     */
    @Override
    public void preModify(final FlsysMetaDto flsysMetaDto) throws Exception {
        // 태그를 먼저 처리해준다. :: 메소드 분리
        // boardTagService.processTagList(flsysMetaDto);
    }
}