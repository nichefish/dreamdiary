package io.nicheblog.dreamdiary.global.cmm.xlsx.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * XlsxStyledCell
 * <pre>
 *  (공통) 엑셀 셀(+스타일) 정보
 * </pre>
 *
 * @author nichefish
 * @extends XlsxCell
 */
@Getter
@Setter
public class XlsxStyledCell
        extends XlsxCell {

    /** 셀 스타일 */
    private CellStyle style;

    /* ----- */

    /**
     * 생성자
     */
    public XlsxStyledCell(final String header, final Integer length) {
        super(header, length);
    }
}
