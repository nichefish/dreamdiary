package io.nicheblog.dreamdiary.global.cmm.xlsx;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * XlsxStyleUtils
 * <pre>
 *  엑셀 셀 스타일 관련 처리 유틸리티 클래스
 * </pre>
 *
 * @author nichefish
 */
@UtilityClass
@Log4j2
public class XlsxStyleUtils {

    /**
     * 기본
     */
    public static CellStyle getDefaultStyle(final SXSSFWorkbook workbook) {
        return workbook.createCellStyle();
    }

    /**
     * 기본 + 테두리
     */
    public static CellStyle getTitleStyle(final SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 폰트
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        style.setFont(titleFont);
        style.setAlignment(HorizontalAlignment.CENTER);
        // 테두리
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        // 자동줄바꿈
        style.setWrapText(true);
        return style;
    }

    /**
     * 기본 + 테두리
     */
    public static CellStyle getHeaderStyle(final SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 폰트
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        // 테두리
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        // 자동줄바꿈
        style.setWrapText(true);
        return style;
    }

    /**
     * 기본 + 테두리
     */
    public static CellStyle getDataStyle(final SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        // 자동줄바꿈
        style.setWrapText(true);
        return style;
    }

    /* ----- */

    /**
     * 테두리
     */
    public static CellStyle getBorderStyle(final SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 테두리
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        // 자동줄바꿈
        style.setWrapText(true);
        return style;
    }

    /**
     * LABEL:: 가운데 정렬 및 테두리
     */
    public static CellStyle getLabelStyle(final SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 가운데 정렬
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        // 테두리
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        // 배경색
        style.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * RED_LABEL:: 가운데 정렬 및 테두리 + 빨간글씨
     */
    public static CellStyle getRedLabelStyle(final SXSSFWorkbook workbook) {
        CellStyle style = getLabelStyle(workbook);
        // 빨간글씨
        Font font = workbook.createFont();
        font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        style.setFont(font);
        return style;
    }

    /**
     * COVER_TITLE:: 가운데 정렬 및 테두리, 크고 굵은 글씨
     */
    public static CellStyle getCoverStyle(final SXSSFWorkbook workbook) {
        CellStyle style = getLabelStyle(workbook);
        // 크고굵은글씨
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 24);
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
