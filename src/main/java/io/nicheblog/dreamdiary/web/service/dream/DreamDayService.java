package io.nicheblog.dreamdiary.web.service.dream;

import io.nicheblog.dreamdiary.global.cmm.file.service.FileService;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.web.entity.dream.DreamDayEntity;
import io.nicheblog.dreamdiary.web.mapstruct.dream.DreamDayMapstruct;
import io.nicheblog.dreamdiary.web.model.dream.DreamDayDto;
import io.nicheblog.dreamdiary.web.repository.dream.DreamDayRepository;
import io.nicheblog.dreamdiary.web.spec.dream.DreamDaySpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * DreamDayService
 * <pre>
 *  꿈 일자 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseMultiCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("dreamDayService")
@Log4j2
public class DreamDayService
        implements BaseMultiCrudService<DreamDayDto, DreamDayDto, Integer, DreamDayEntity, DreamDayRepository, DreamDaySpec, DreamDayMapstruct, FileService> {

    private final DreamDayMapstruct dreamDayMapstruct = DreamDayMapstruct.INSTANCE;

    @Resource(name = "dreamDayRepository")
    private DreamDayRepository dreamDayRepository;
    @Resource(name = "dreamDaySpec")
    private DreamDaySpec dreamDaySpec;
    @Resource(name = "fileService")
    private FileService fileService;

    @Override
    public DreamDayRepository getRepository() {
        return this.dreamDayRepository;
    }

    @Override
    public DreamDayMapstruct getMapstruct() {
        return this.dreamDayMapstruct;
    }

    @Override
    public DreamDaySpec getSpec() {
        return this.dreamDaySpec;
    }

    @Override
    public FileService getFileService() {
        return this.fileService;
    }
}