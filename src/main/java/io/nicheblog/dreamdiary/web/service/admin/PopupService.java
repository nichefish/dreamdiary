package io.nicheblog.dreamdiary.web.service.admin;

import io.nicheblog.dreamdiary.global.cmm.file.service.FileService;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.web.entity.admin.PopupEntity;
import io.nicheblog.dreamdiary.web.mapstruct.admin.PopupMapstruct;
import io.nicheblog.dreamdiary.web.model.admin.PopupDto;
import io.nicheblog.dreamdiary.web.repository.admin.PopupRepository;
import io.nicheblog.dreamdiary.web.spec.admin.PopupSpec;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * PopupService
 * <pre>
 *  팝업 정보 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseMultiCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("popupService")
public class PopupService
        implements BaseMultiCrudService<PopupDto, PopupDto, Integer, PopupEntity, PopupRepository, PopupSpec, PopupMapstruct, FileService> {

    @Resource(name = "popupRepository")
    private PopupRepository popupRepository;
    @Resource(name = "popupSpec")
    private PopupSpec popupSpec;
    @Resource(name = "fileService")
    private FileService fileService;

    private final PopupMapstruct popupMapstruct = PopupMapstruct.INSTANCE;

    @Override
    public PopupRepository getRepository() {
        return this.popupRepository;
    }

    @Override
    public PopupSpec getSpec() {
        return this.popupSpec;
    }

    @Override
    public FileService getFileService() {
        return this.fileService;
    }

    @Override
    public PopupMapstruct getMapstruct() {
        return this.popupMapstruct;
    }

    /**
     * 활성화 중인 팝업 모음
     */
    // public Page<PopupDto> getActivePopupList() throws Exception {
    //     Page<PopupEntity> entityPage = popupRepository.findAll(popupSpec.getActives(), Pageable.unpaged());
//
    //     // Page<Entity> -> Page<Dto>
    //     return this.pageEntityToDto(entityPage);
    // }
}
