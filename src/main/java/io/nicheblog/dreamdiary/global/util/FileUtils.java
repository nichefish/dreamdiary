package io.nicheblog.dreamdiary.global.util;

import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileDtlEntity;
import io.nicheblog.dreamdiary.global.cmm.file.service.AtchFileDtlService;
import io.nicheblog.dreamdiary.global.cmm.file.service.AtchFileService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
import java.util.Objects;

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