package io.nicheblog.dreamdiary.global.cmm.xlsx.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * XlsxCellDefInfo
 * <pre>
 *  (공통) 엑셀 파일 생성 관련 정보
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
public class XlsxCellDefInfo {

    /** 헤더 명칭 */
    private String headerNm;
    /** 셀 데이터 */
    private String cellData;
    /** CellSizes */
    private Integer cellSize;

    /* ----- */

    /** 생성자 */
    public XlsxCellDefInfo(final String headerNm, final Integer cellSize) {
        this.headerNm = headerNm;
        this.cellSize = cellSize;
    }
}
