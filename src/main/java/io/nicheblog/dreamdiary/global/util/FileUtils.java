package io.nicheblog.dreamdiary.global.util;

import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileDtlEntity;
import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileEntity;
import io.nicheblog.dreamdiary.global.cmm.file.model.AtchFileDtlDto;
import io.nicheblog.dreamdiary.global.cmm.file.service.AtchFileDtlService;
import io.nicheblog.dreamdiary.global.cmm.file.service.AtchFileService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * FileUtils
 * <pre>
 *  파일 서비스에서 (서비스 인터페이스에서 쓰는) 파일 업로드 부분만 유틸리티 클래스로 분리
 * </pre>
 *
 * @author nichefish
 */
@Component("fileUtils")
@Log4j2
public class FileUtils {

    @Resource(name = "atchFileService")
    private AtchFileService fileService;
    @Resource(name = "atchFileDtlService")
    private AtchFileDtlService fileDtlService;
    @Resource
    private HttpServletResponse resp;

    private static AtchFileService atchFileService;
    private static AtchFileDtlService atchFileDtlService;
    private static HttpServletResponse response;

    /** static 맥락에서 사용할 수 있도록 bean 주입 */
    @PostConstruct
    private void init() {
        atchFileService = fileService;
        atchFileDtlService = fileDtlService;
        response = resp;
    }

    /**
     * 파일 유무 체크
     */
    public static Boolean fileChck(final String fileId) {
        // 1. 파일ID일 경우로 상정 ::
        try {
            Integer atchFileDtlNo = Integer.parseInt(fileId);
            AtchFileDtlEntity fileDtl = atchFileDtlService.getDtlEntity(atchFileDtlNo);
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

    public static AtchFileEntity getUploadedFile(
            final MultipartHttpServletRequest multiRequest
    ) throws Exception {
        return getUploadedFile(multiRequest, null);
    }
    public static AtchFileEntity getUploadedFile(
            final MultipartHttpServletRequest multiRequest,
            final Integer atchFileNo
    ) throws Exception {
        // 첨부파일ID 세팅
        AtchFileEntity atchFile = null;
        if (atchFileNo != null) atchFile = atchFileService.getDtlEntity(atchFileNo);
        if (atchFile == null) {
            atchFile = new AtchFileEntity();
            atchFile.setAtchFileList(new ArrayList<>());
        }
        List<AtchFileDtlEntity> atchFileList = atchFile.getAtchFileList();
        boolean isAtchFileListEmpty = CollectionUtils.isEmpty(atchFileList);

        // 파일 처리
        // input file이 안 넘어오는 경우
        Map<String, MultipartFile> fileMap = multiRequest.getFileMap();
        boolean isMultipartFileEmpty = MapUtils.isEmpty(fileMap);
        if (isMultipartFileEmpty) {
            // 추가된(multipart로 요청된) 파일도 없고 기존 파일도 없으면 리턴
            if (isAtchFileListEmpty) return null;
            // 삭제된(del 플래그가 전달된) 파일에 대하여 DB삭제플래그 세팅(atchCtrl="D") (메소드 분리)
            atchFileDtlService.delFile(multiRequest, atchFileList);
        }
        // 추가된(multipart로 요청된) 파일에 대하여 업로드+DB추가
        atchFileDtlService.addFiles(multiRequest, atchFileList);
        atchFile.cascade();
        return atchFileService.updt(atchFile);
    }

    /**
     * 파일 업로드
     */
    public static Integer uploadFile(final MultipartHttpServletRequest multiRequest) throws Exception {
        return uploadFile(multiRequest, null);
    }
    public static Integer uploadFile(
            final MultipartHttpServletRequest multiRequest,
            final Integer atchFileNo
    ) throws Exception {

        try {
            AtchFileEntity rslt = getUploadedFile(multiRequest, atchFileNo);
            assert rslt != null;
            return rslt.getAtchFileNo();
        } catch (Exception e) {
            MessageUtils.alertMessage("파일 업로드에 실패했습니다.");
        }
        return atchFileNo;
    }

    /**
     * 파일 업로드
     */
    public static AtchFileDtlDto uploadDtlFile(final MultipartHttpServletRequest multiRequest) throws Exception {
        AtchFileEntity atchFile = getUploadedFile(multiRequest);
        AtchFileDtlEntity dtlFile = atchFile.getAtchFileList().get(0);
        return dtlFile.asDto();
    }

    /**
     * 메소드 분리 :: 삭제된 파일에 대하여 DB 삭제 플래그 세팅
     */
    private List<AtchFileDtlEntity> delFile(
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

    /**
     * 메소드 분리 :: 파일 다운로드
     */
    public static void downloadFile(final File file) throws Exception {
        String fileName = file.getName();
        downloadFile(file, fileName);
    }

    public static void downloadFile(
            final File file,
            final String fileNm
    ) throws Exception {
        OutputStream os;
        InputStream is = new FileInputStream(file);

        FileUtils.setRespnsHeader(fileNm);       // 응답 헤더 설정 및 한글 파일명 처리 (메소드 분리)
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
     * 응답 헤더 설정 및 한글 파일명 처리 (메소드 분리)
     */
    public static void setRespnsHeader(final String fileNm) throws Exception {
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
}