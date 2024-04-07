package io.nicheblog.dreamdiary.global.cmm.xlsx.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nicheblog.dreamdiary.global.cmm.file.utils.FileUtils;
import io.nicheblog.dreamdiary.global.cmm.xlsx.XlsxCellStyle;
import io.nicheblog.dreamdiary.global.cmm.xlsx.XlsxType;
import io.nicheblog.dreamdiary.global.util.CookieUtils;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * XlsxUtils
 * <pre>
 *  엑셀 POI 라이브러리 관련 처리 유틸리티 모듈
 *  (리플렉션 사용하여 일반화한 공통 로직, 가변적인 소규모 데이터에 적합, 대량(수십만건 이상) 처리에는 부적합.)
 *  (대용량 처리시 xlsxService에 특화된 로직 구현하기)
 * </pre>
 *
 * @author nichefish
 */
@Component
@Log4j2
public class XlsxUtils {

    @Resource
    private HttpServletResponse resp;

    private static HttpServletResponse response;

    /** static 맥락에서 사용할 수 있도록 bean 주입 */
    @PostConstruct
    private void init() {
        response = resp;
    }

    /**
     * 목록 엑셀 파일 다운로드
     */
    public static void listXlxsDownload(final XlsxType xlsxType, final Stream<?> dataList) throws Exception {

        // 엑셀 파일 생성 (메소드 분리)
        SXSSFWorkbook workbook = makeListXlsxFile(xlsxType, dataList);

        // 생성된 엑셀 파일 다운로드 (메소드 분리)
        xslxFileDownload(xlsxType, workbook);
    }

    /**
     * 목록 엑셀 파일 생성 (메소드 분리)
     */
    public static SXSSFWorkbook makeListXlsxFile(final XlsxType xlsxType, final Stream<?> dataList) throws Exception {

        // 1. 파일 및 시트 생성
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SXSSFSheet sheet = workbook.createSheet(xlsxType.title);

        // 2. 기본 Cell Style 생성
        Map<XlsxCellStyle, CellStyle> styleMap = XlsxCellStyle.createStyleMap(workbook);

        // 3-1. Title Row 생성 (메소드 분리)
        createTitleRow(xlsxType, sheet, styleMap);

        // 3-2. Header Row 생성 (메소드 분리)
        createListHeaderRow(xlsxType, sheet, styleMap);

        // 3-3. dataList Rows 생성 (if row is not empty) (메소드 분리)
        createListDataRows(dataList, sheet, styleMap);

        return workbook;
    }

    /**
     * 엑셀 파일 Title Row 생성 (메소드 분리)
     */
    public static void createTitleRow(final XlsxType xlsxType, final SXSSFSheet sheet, final Map<XlsxCellStyle, CellStyle> styleMap) {
        SXSSFRow titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(45);
        SXSSFCell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(xlsxType.title);
        titleCell.setCellStyle(styleMap.get(XlsxCellStyle.TITLE));
        int headerLength = xlsxType.headers.size();
        if (headerLength > 1) {
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headerLength - 1));
        }
    }

    /**
     * 엑셀 파일 목록 Header Row 생성 (메소드 분리)
     */
    public static void createListHeaderRow(final XlsxType xlsxType, final SXSSFSheet sheet,  final Map<XlsxCellStyle, CellStyle> styleMap) {
        SXSSFRow headerRow = sheet.createRow(sheet.getLastRowNum() + 1);
        headerRow.setHeightInPoints(40);
        // IntStream을 사용하여 헤더 리스트를 반복
        IntStream.range(0, xlsxType.headers.size()).forEach(i -> {
            SXSSFCell headerCell = headerRow.createCell(i);
            sheet.setColumnWidth(i, xlsxType.headers.get(i).getLength() * 256);
            headerCell.setCellStyle(styleMap.get(XlsxCellStyle.HEADER));
            headerCell.setCellValue(xlsxType.headers.get(i).getHeader());
        });
    }

    /**
     * 엑셀 파일 목록 dataList Rows 생성 (메소드 분리)
     * merged cell에 대하여 자동 열 높이 기능 작동안함. (poi가 아니라 엑셀 자체 문제)
     * TODO:: 필드 캐싱 처리?
     */
    private static void createListDataRows(final Stream<?> dataList, final SXSSFSheet sheet,  final Map<XlsxCellStyle, CellStyle> styleMap) {
        final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper

        // row iteration
        final AtomicInteger rowIndex = new AtomicInteger(sheet.getLastRowNum() + 1);
        dataList.forEach(item -> {
            try {
                SXSSFRow dataRow = sheet.createRow(rowIndex.getAndIncrement());
                dataRow.setHeight((short) -1);

                LinkedHashMap<String, Object> rowDataMap = mapper.convertValue(item, LinkedHashMap.class);

                // if column is not empty
                if (MapUtils.isNotEmpty(rowDataMap)) {
                    String[] dataKeyset = rowDataMap.keySet().toArray(new String[0]);
                    log.debug("dataKeyset: {}", Arrays.toString(dataKeyset));
                    IntStream.range(0, dataKeyset.length).forEach(colIndex -> {
                        SXSSFCell dataCell = dataRow.createCell(colIndex);
                        Object valueObj = rowDataMap.get(dataKeyset[colIndex]);
                        // 숫자는 숫자 형식 유지하도록
                        if (valueObj instanceof Integer) {
                            dataCell.setCellValue((Integer) valueObj);
                            CellStyle cellStyle = styleMap.get(XlsxCellStyle.DATA);
                            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
                            dataCell.setCellStyle(cellStyle);
                        } else {
                            String cellValue = valueObj != null ? valueObj.toString() : "";
                            dataCell.setCellValue(cellValue);
                        }
                    });
                }
            } catch (Exception e) {
                throw new RuntimeException("엑셀 파일 생성 중 오류가 발생했습니다.", e);
            }
        });
    }

    /* ----- */

    /**
     * 셀 생성과 동시에 스타일 적용
     */
    public static Cell createBlankCellWithStyle(final Row row, final int index, final CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellStyle(style);
        return cell;
    }

    /**
     * 셀 생성과 동시에 스타일 적용, 값 세팅
     */
    public static Cell createCellWithStyleInit(final Row row, final int index, final CellStyle style, final String value) {
        Cell cell = createBlankCellWithStyle(row, index, style);
        cell.setCellValue(value);
        return cell;
    }

    /**
     * 범위로 빈 셀 생성
     */
    public static void createBlankCellRangeWithStyle(final Row row, final int startIndex, final int endIndex, final CellStyle style) {
        for (int i = startIndex; i <= endIndex; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
        }
    }

    /**
     * 생성된 엑셀 파일 다운로드 (메소드 분리)
     */
    private static void xslxFileDownload(final XlsxType xlsxType, final SXSSFWorkbook workbook) throws IOException {

        BufferedOutputStream outs;
        try {
            String fileNm = xlsxType.fileNm;
            // 응답 헤더 설정 및 한글 파일명 처리 (메소드 분리)
            FileUtils.setRespnsHeader(fileNm);
            CookieUtils.setFileDownloadSuccessCookie();

            // Write.xlsx에 대한 출력 스트림 생성
            outs = new BufferedOutputStream(response.getOutputStream());
            // Write.xlsx에 위에서 입력된 데이터를 씀
            workbook.write(outs);
            outs.close();
        } catch (Exception e) {
            log.info(MessageUtils.getExceptionMsg(e));
        } finally {
            response.getOutputStream().flush();
            response.getOutputStream().close();
        }

    }
}
