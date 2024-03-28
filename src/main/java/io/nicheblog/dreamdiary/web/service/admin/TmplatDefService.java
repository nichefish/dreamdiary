package io.nicheblog.dreamdiary.web.service.admin;

import io.nicheblog.dreamdiary.global.cmm.file.service.CmmFileService;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.web.entity.admin.TmplatDefEntity;
import io.nicheblog.dreamdiary.web.mapstruct.admin.TmplatDefMapstruct;
import io.nicheblog.dreamdiary.web.model.admin.TmplatDefDto;
import io.nicheblog.dreamdiary.web.repository.admin.TmplatDefRepository;
import io.nicheblog.dreamdiary.web.spec.admin.TmplatDefSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * TmplatDefService
 * <pre>
 *  템플릿 정의 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseMultiCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("tmplatDefService")
@Log4j2
public class TmplatDefService
        implements BaseMultiCrudService<TmplatDefDto, TmplatDefDto, Integer, TmplatDefEntity, TmplatDefRepository, TmplatDefSpec, TmplatDefMapstruct, CmmFileService> {

    @Resource(name = "tmplatDefRepository")
    private TmplatDefRepository tmplatDefRepository;
    @Resource(name = "tmplatDefSpec")
    private TmplatDefSpec tmplatDefSpec;
    @Resource(name = "cmmFileService")
    private CmmFileService cmmFileService;

    private final TmplatDefMapstruct tmplatDefMapstruct = TmplatDefMapstruct.INSTANCE;

    @Override
    public TmplatDefRepository getRepository() {
        return this.tmplatDefRepository;
    }

    @Override
    public TmplatDefSpec getSpec() {
        return this.tmplatDefSpec;
    }

    @Override
    public TmplatDefMapstruct getMapstruct() {
        return this.tmplatDefMapstruct;
    }

    @Override
    public CmmFileService getFileService() {
        return this.cmmFileService;
    }

}