package io.nicheblog.dreamdiary.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileDtlEntity;
import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileEntity;
import io.nicheblog.dreamdiary.global.cmm.file.service.AtchFileService;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import io.nicheblog.dreamdiary.global.validator.CmmRegex;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
    @Resource
    private HttpServletResponse resp;

    private static AtchFileService atchFileService;
    private static HttpServletResponse response;

    /** static 맥락에서 사용할 수 있도록 bean 주입 */
    @PostConstruct
    private void init() {
        atchFileService = fileService;
        response = resp;
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