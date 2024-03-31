package io.nicheblog.dreamdiary.global.cmm.file.service;

import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileDtlEntity;
import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileEntity;
import io.nicheblog.dreamdiary.global.cmm.file.mapstruct.AtchFileMapstruct;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDto;
import io.nicheblog.dreamdiary.global.cmm.file.repository.AtchFileRepository;
import io.nicheblog.dreamdiary.global.cmm.file.spec.AtchFileSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * AtchFileService
 * <pre>
 *  공통 > 파일 처리 서비스 모듈
 *  TODO: DB단 관리하는 서비스와 실제 파일을 처리하는 유틸리티 분리
 * </pre>
 *
 * @author nichefish
 */
@Service("atchFileService")
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

    /**
     * 메소드 분리 :: 삭제된 파일에 대하여 DB 삭제 플래그 세팅
     */
    public List<AtchFileDtlEntity> delFile(
            final MultipartHttpServletRequest multiRequest,
            final List<AtchFileDtlEntity> atchFileList
    ) {
        if (CollectionUtils.isEmpty(atchFileList)) return null;
        return atchFileList.stream()
                .peek(atchFileDtl -> {
                    String atchCtrl = multiRequest.getParameter("atchCtrl" + atchFileDtl.getAtchFileDtlNo());
                    if ("D".equals(atchCtrl)) atchFileDtl.setDelYn("Y");
                    // TODO: 실제 파일 삭제?
                })
                .collect(Collectors.toList());
    }
}
