package io.nicheblog.dreamdiary.global.cmm.file.service;

import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileDtlEntity;
import io.nicheblog.dreamdiary.global.cmm.file.mapstruct.AtchFileDtlMapstruct;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDtlDto;
import io.nicheblog.dreamdiary.global.cmm.file.repository.AtchFileDtlRepository;
import io.nicheblog.dreamdiary.global.cmm.file.spec.AtchFileDtlSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * AtchFileDtlService
 * <pre>
 *  공통 > 상세 파일 처리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service("atchFileDtlService")
@Log4j2
public class AtchFileDtlService
        implements BaseCrudService<AtchFileDtlDto, AtchFileDtlDto, Integer, AtchFileDtlEntity, AtchFileDtlRepository, AtchFileDtlSpec, AtchFileDtlMapstruct> {

    AtchFileDtlMapstruct atchFileDtlMapstruct = AtchFileDtlMapstruct.INSTANCE;

    @Resource(name = "atchFileDtlRepository")
    private AtchFileDtlRepository atchFileDtlRepository;
    @Resource(name = "atchFileDtlSpec")
    private AtchFileDtlSpec atchFileDtlSpec;

    @Override
    public AtchFileDtlRepository getRepository() {
        return this.atchFileDtlRepository;
    }
    @Override
    public AtchFileDtlSpec getSpec() {
        return this.atchFileDtlSpec;
    }
    @Override
    public AtchFileDtlMapstruct getMapstruct() {
        return this.atchFileDtlMapstruct;
    }

    /**
     * 첨부파일 상세 목록 조회 (dto level)
     */
    public List<AtchFileDtlDto> getListDto(final Integer atchFileNo) throws Exception {
        Map<String, Object> paramMap = new HashMap<>() {{
            put("atchFileNo", atchFileNo);
        }};
        return this.getListDto(paramMap, Pageable.unpaged()).getContent();
    }

    /**
     * 메소드 분리 :: 추가된 파일에 대하여 업로드 및 정보 DB에 등록
     */
    public void addFiles(
            final MultipartHttpServletRequest multiRequest,
            final List<AtchFileDtlEntity> atchFileList
    ) throws Exception {

        String path = "upfile/";
        File file = new File(path);
        if (!file.exists()) if (!file.mkdirs()) throw new Exception("폴더 생성에 실패했습니다.");
        path = "upfile/" + DateUtils.getCurrDateStr(DateUtils.PTN_DATE) + "/";
        file = new File(path);
        if (!file.exists()) if (!file.mkdirs()) throw new Exception("폴더 생성에 실패했습니다.");

        Iterator<String> fileNmIterator = multiRequest.getFileNames();
        String fileInputNm, orgnFileNm, orgnFileExtn, replaceFileNm;
        while (fileNmIterator.hasNext()) {
            fileInputNm = fileNmIterator.next();
            MultipartFile multipartFile = multiRequest.getFile(fileInputNm);
            if (multipartFile == null || multipartFile.isEmpty()) {
                log.debug("file is Empty...");
                continue;
            }
            // String fileIdx = fileInputNm.replace("atchFile", "");        // TODO: 파일 순번이 중요한 시점이 올수도 있다.
            orgnFileNm = Optional.ofNullable(multipartFile.getOriginalFilename())
                    .orElse(DateUtils.getCurrDateStr(DateUtils.PTN_DATE));
            orgnFileExtn = orgnFileNm.substring(orgnFileNm.lastIndexOf('.') + 1);
            replaceFileNm = System.nanoTime() + "." + orgnFileExtn;
            // 실제 파일 저장
            //multipartfile.transferto(file)를 사용시 절대경로를 사용하지 않으면 문제 발생!
            Path abPath = Paths.get(path + replaceFileNm)
                    .toAbsolutePath();
            String abPathStr = Paths.get(path)
                    .toAbsolutePath()
                    .toString();
            log.debug("absolute path: {} pathStr: {}", abPath, abPathStr);
            multipartFile.transferTo(abPath.toFile());
            /* 파일정보 DB 저장 준비 (entity에 할당) */
            AtchFileDtlEntity fileEntity = new AtchFileDtlEntity();
            fileEntity.setFileSize(multipartFile.getSize());
            // TODO: 파일명 특수문자 등 있으면 처리
            fileEntity.setOrgnFileNm(orgnFileNm);
            fileEntity.setStreFileNm(replaceFileNm);
            fileEntity.setFileExtn(orgnFileExtn);
            fileEntity.setFileStrePath(abPathStr);
            fileEntity.setUrl("/" + path + replaceFileNm);
            atchFileList.add(fileEntity);
        }
    }

    /**
     * 메소드 분리 :: 삭제된 파일에 대하여 DB 삭제 플래그 세팅
     */
    public void delFile(
            final MultipartHttpServletRequest multiRequest,
            final List<AtchFileDtlEntity> atchFileList
    ) {
        if (CollectionUtils.isEmpty(atchFileList)) return;
        atchFileList.stream()
                .peek(atchFileDtl -> {
                    String atchCtrl = multiRequest.getParameter("atchCtrl" + atchFileDtl.getAtchFileDtlNo());
                    if ("D".equals(atchCtrl)) atchFileDtl.setDelYn("Y");
                    // TODO: 실제 파일 삭제?
                });
    }
}
