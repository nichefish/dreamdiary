package io.nicheblog.dreamdiary.global.cmm.xlsx.model;

import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.XlsxConstant;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * CmmXlsxDefInfo
 * <pre>
 *  (공통) 엑셀 파일 생성 관련 정보
 * </pre>
 * TODO: 나중에 대대적인 개편이 필요하다. (사용방식이 비효율적)
 *
 * @author nichefish
 */
@Getter
@Setter
public class XlsxDefInfo {

    /** 엑셀 파일 이름 */
    private String fileNm;
    /** 엑셀 시트 이름 */
    private String sheetNm;
    /** 엑셀 시트 상단 제목 */
    private String title;
    /** 헤더 */
    private Object[][] header;
    /** 상세 헤더 */
    private String[][] dtlHeader;

    /* ----- */

    /**
     * 엑셀 파일 제목 세팅
     */
    public String getDownloadFileNm() throws Exception {
        String downloadFileNm = StringUtils.isEmpty(this.getFileNm()) ? "defaultNm" : this.getFileNm();
        return downloadFileNm + "_" + DateUtils.getCurrDateStr(DatePtn.DATETIME) + ".xlsx";
    }

    /**
     * 헤더 길이 반환
     */
    public Integer getHeaderLength() {
        boolean isHeaderNmEmpty = this.header == null || this.header.length == 0;
        return isHeaderNmEmpty ? 1 : this.header.length;
    }

    /**
     * 생성자:: 각 정보 유형별로 기본 세팅된 생성 정보 사용
     */
    public XlsxDefInfo(final String xlsxListType) {
        Map<String, Object> xlsxHeader = XlsxConstant.excelMngInfoMap;
        if (StringUtils.isNotEmpty(xlsxListType)) {
            this.fileNm = xlsxHeader.get(xlsxListType + "FileNm") != null ? (String) xlsxHeader.get(xlsxListType + "FileNm") : null;
            this.sheetNm = xlsxHeader.get(xlsxListType + "SheetNm") != null ? (String) xlsxHeader.get(xlsxListType + "SheetNm") : null;
            this.title = xlsxHeader.get(xlsxListType + "Title") != null ? (String) xlsxHeader.get(xlsxListType + "Title") : null;
            this.header = xlsxHeader.get(xlsxListType + "Header") != null ? (Object[][]) xlsxHeader.get(xlsxListType + "Header") : null;
            this.dtlHeader = xlsxHeader.get(xlsxListType + "DtlHeader") != null ? (String[][]) xlsxHeader.get(xlsxListType + "DtlHeader") : null;
        }
    }

}
