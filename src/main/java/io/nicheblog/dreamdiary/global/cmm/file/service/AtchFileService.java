package io.nicheblog.dreamdiary.global.cmm.file.service;

import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileDtlEntity;
import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileEntity;
import io.nicheblog.dreamdiary.global.cmm.file.mapstruct.AtchFileDtlMapstruct;
import io.nicheblog.dreamdiary.global.cmm.file.mapstruct.AtchFileMapstruct;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDtlDto;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDto;
import io.nicheblog.dreamdiary.global.cmm.file.repository.AtchFileDtlRepository;
import io.nicheblog.dreamdiary.global.cmm.file.repository.AtchFileRepository;
import io.nicheblog.dreamdiary.global.cmm.file.spec.AtchFileSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
    AtchFileDtlMapstruct atchFileDtlMapstruct = AtchFileDtlMapstruct.INSTANCE;

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

    @Resource(name = "atchFileRepository")
    private AtchFileRepository atchFileRepository;
    @Resource(name = "atchFileDtlRepository")
    private AtchFileDtlRepository atchFileDtlRepository;
    @Resource(name = "atchFileSpec")
    private AtchFileSpec atchFileSpec;
    @Resource
    private HttpServletResponse response;

    /**
     * 파일 업로드
     */
    public AtchFileDtlDto uploadDtlFile(final MultipartHttpServletRequest multiRequest) throws Exception {
        Integer atchFileNo = this.uploadFile(multiRequest);
        AtchFileDtlEntity atchFileDtlEntity = this.getFileEntity(atchFileNo)
                                                  .getAtchFileList()
                                                  .get(0);
        atchFileDtlEntity.setAtchFileNo(atchFileNo);
        return atchFileDtlMapstruct.toDto(atchFileDtlEntity);
    }

    /**
     * 파일 업로드
     */
    public Integer uploadFile(final MultipartHttpServletRequest multiRequest) throws Exception {
        return this.uploadFile(multiRequest, null);
    }

    public Integer uploadFile(
            final MultipartHttpServletRequest multiRequest,
            final Integer atchFileNo
    ) throws Exception {

        // 첨부파일ID 세팅
        AtchFileEntity atchFile;
        List<AtchFileDtlEntity> atchFileList;
        if (atchFileNo != null) {
            atchFile = atchFileRepository.findById(atchFileNo)
                                         .orElseGet(AtchFileEntity::new);
        } else {
            atchFile = new AtchFileEntity();
            atchFile.setAtchFileList(new ArrayList<>());
        }
        atchFileList = atchFile.getAtchFileList();
        boolean isAtchFileListEmpty = CollectionUtils.isEmpty(atchFileList);

        // 파일 처리
        // input file이 안 넘어오는 경우
        Map<String, MultipartFile> fileMap = multiRequest.getFileMap();
        boolean isMultipartFileEmpty = MapUtils.isEmpty(fileMap);
        if (isMultipartFileEmpty) {
            // 추가된(multipart로 요청된) 파일도 없고 기존 파일도 없으면 리턴
            if (isAtchFileListEmpty) return null;
            // 삭제된(del 플래그가 전달된) 파일에 대하여 DB삭제플래그 세팅(atchCtrl="D") (메소드 분리)
            this.delFile(multiRequest, atchFileList);
        }
        // 추가된(multipart로 요청된) 파일에 대하여 업로드+DB추가
        this.addFile(multiRequest, atchFileList);
        AtchFileEntity rsltEntity = atchFileRepository.save(atchFile);
        return rsltEntity.getAtchFileNo();
    }

    /**
     * 메소드 분리 :: 추가된 파일에 대하여 업로드 및 정보 DB에 등록
     */
    private void addFile(
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
            log.debug("File name tag : " + fileInputNm);
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
            // 파일정보 DB 저장
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

    /** TODO: MultipartRequest에서 파일 정보만 추출? */

    /**
     * 메소드 분리 :: 삭제된 파일에 대하여 DB 삭제 플래그 세팅
     */
    private void delFile(
            final MultipartHttpServletRequest multiRequest,
            final List<AtchFileDtlEntity> atchFileList
    ) {
        Integer atchFileDtlNo;
        String atchCtrl;
        for (AtchFileDtlEntity atchFileDtl : atchFileList) {
            atchFileDtlNo = atchFileDtl.getAtchFileDtlNo();
            atchCtrl = multiRequest.getParameter("atchCtrl" + atchFileDtlNo);
            if ("D".equals(atchCtrl)) {
                atchFileDtl.setDelYn("Y");
            }
        }
    }

    /**
     * 메소드 분리 :: 파일 다운로드
     */
    public void downloadFile(final File file) throws Exception {
        String fileName = file.getName();
        downloadFile(file, fileName);
    }

    public void downloadFile(
            final File file,
            final String fileNm
    ) throws Exception {
        OutputStream os;
        InputStream is = new FileInputStream(file);

        this.setRespnsHeader(fileNm);       // 응답 헤더 설정 및 한글 파일명 처리 (메소드 분리)
        response.setHeader("Content-Length", String.valueOf(file.length()));        // 파일 크기 설정

        // 응답으로 파일 전송
        os = response.getOutputStream();
        byte[] buffer = new byte[2048];
        int bytesRead, bytesBufferd = 0;
        while ((bytesRead = is.read(buffer)) > -1) {
            os.write(buffer, 0, bytesRead);
            bytesBufferd += bytesRead;
            if (bytesBufferd > 2048 * 1024) {       // 2MB마다 flush
                bytesBufferd = 0;
                os.flush();
            }
        }
        os.flush();
        // 전송 끝 + 스트림 닫음
        is.close();
        os.close();
    }

    /**
     * 첨부파일 정보 조회 (atchFileNo) (entity level)
     */
    public AtchFileEntity getFileEntity(final Integer atchFileNo) throws Exception {
        return atchFileRepository.findById(atchFileNo)
                                 .orElse(null);
    }

    /**
     * 첨부파일 정보 조회 (atchFileNo) (dto level)
     */
    public AtchFileDto getFileDto(final Integer atchFileNo) throws Exception {
        AtchFileEntity fileEntity = this.getFileEntity(atchFileNo);
        if (fileEntity == null) return null;

        return atchFileMapstruct.toDto(fileEntity);
    }

    /**
     * 첨부파일 상세 목록 조회 (dto level)
     */
    public List<AtchFileDtlDto> getFileDtlDtoList(final Integer atchFileNo) throws Exception {
        AtchFileDto fileDto = this.getFileDto(atchFileNo);
        if (fileDto == null) return null;
        return fileDto.getAtchFileList();
    }

    /**
     * 첨부파일 상세 단일 조회 (entity level)
     */
    public AtchFileDtlEntity getFileDtlEntity(final Integer atchFileDtlNo) {
        return atchFileDtlRepository.findById(atchFileDtlNo)
                                    .orElse(null);
    }

    /**
     * 첨부파일 상세 단일 조회 (dto level)
     */
    public AtchFileDtlDto getFileDtlDto(final Integer atchFileDtlNo) throws Exception {
        AtchFileDtlEntity fileDtlEntity = this.getFileDtlEntity(atchFileDtlNo);
        return atchFileDtlMapstruct.toDto(fileDtlEntity);
    }

    /** 첨부파일 저장 */
    public AtchFileEntity regist(AtchFileEntity atchFile) {
        return atchFileRepository.save(atchFile);
    }

    /**
     * 응답 헤더 설정 및 한글 파일명 처리 (메소드 분리)
     */
    public void setRespnsHeader(final String fileNm) throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        String client = request.getHeader("User-Agent");
        // 브라우저가 IE일 경우 별도 처리
        if (client.contains("MSIE")) {
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileNm, StandardCharsets.UTF_8));
        } else if (client.contains("rv:11.0")) {
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(fileNm, StandardCharsets.UTF_8)
                                                        .replaceAll("\\+", "\\ ") + ";"
            );
        } else {
            // 한글 파일명 처리
            String korFileNm = new String(fileNm.getBytes("euc-kr"), StandardCharsets.ISO_8859_1);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + korFileNm + "\"");
            response.setHeader("Content-type", "application/octet-stream; charset=euc-kr");
        }
    }

    /**
     * 파일 유무 체크
     */
    public Boolean fileChck(final String fileId) {
        // 1. 파일ID일 경우로 상정 ::
        try {
            Integer atchFileDtlNo = Integer.parseInt(fileId);
            AtchFileDtlEntity fileDtl = this.getFileDtlEntity(atchFileDtlNo);
            new File(fileDtl.getFileStrePath(), fileDtl.getStreFileNm());
        } catch (NumberFormatException e) {
            // 2. 에러시 Integer형 ID가 아닌 것으로 판단, 파일명으로 처리
            log.info(MessageUtils.getExceptionMsg(e));
            new File("content/" + fileId);
            return true;
        } catch (Exception e) {
            log.info(MessageUtils.getExceptionMsg(e));
            return false;
        }
        return true;
    }


}
