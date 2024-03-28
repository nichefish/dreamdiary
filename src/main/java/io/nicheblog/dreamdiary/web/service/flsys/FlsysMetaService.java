package io.nicheblog.dreamdiary.web.service.flsys;

import io.nicheblog.dreamdiary.global.cmm.file.service.CmmFileService;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.web.entity.flsys.FlsysMetaEntity;
import io.nicheblog.dreamdiary.web.mapstruct.flsys.FlsysMetaMapstruct;
import io.nicheblog.dreamdiary.web.model.flsys.FlsysMetaDto;
import io.nicheblog.dreamdiary.web.repository.flsys.FlsysMetaRepository;
import io.nicheblog.dreamdiary.web.spec.flsys.FlsysMetaSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
@Log4j2
public class FlsysMetaService
        implements BaseMultiCrudService<FlsysMetaDto, FlsysMetaDto, BaseClsfKey, FlsysMetaEntity, FlsysMetaRepository, FlsysMetaSpec, FlsysMetaMapstruct, CmmFileService> {

    @Resource(name = "flsysMetaRepository")
    private FlsysMetaRepository flsysMetaRepository;
    @Resource(name = "flsysMetaSpec")
    private FlsysMetaSpec flsysMetaSpec;
    @Resource(name = "cmmFileService")
    private CmmFileService cmmFileService;

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

    @Override
    public CmmFileService getFileService() {
        return this.cmmFileService;
    }

    // @Resource(name = "boardTagService")
    // private BoardTagService boardTagService;

    /**
     * 파일시스템 메타 등록 전처리
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