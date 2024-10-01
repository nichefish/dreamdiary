package io.nicheblog.dreamdiary.global.cmm.xlsx.util;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * XlsxTemplateLoader
 * <pre>
 *  엑셀 POI 라이브러리 관련 처리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@UtilityClass
@Log4j2
public class XlsxTemplateLoader {

    /**
     * 엑셀 XSSF 템플릿 조회
     */
    public static XSSFWorkbook loadTemplateAsXssf(final String templatePath) throws IOException {
        FileInputStream fis = new FileInputStream(templatePath);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fis);
        // XSSFWorkbook을 SXSSFWorkbook으로 변환
        fis.close();
        return xssfWorkbook;
    }

    /**
     * 엑셀 SXSSF 템플릿 조회 (스트림 처리)
     */
    public static SXSSFWorkbook loadTemplateAsSxssf(final String templatePath) throws IOException {
        XSSFWorkbook xssfWorkbook = loadTemplateAsXssf(templatePath);
        // XSSFWorkbook을 SXSSFWorkbook으로 변환
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(xssfWorkbook);
        return sxssfWorkbook;
    }
}
