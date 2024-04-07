package io.nicheblog.dreamdiary.global.cmm.xlsx;

import io.nicheblog.dreamdiary.global.cmm.xlsx.model.XlsxCell;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * XlsxType
 * <pre>
 *  사전 정의된 엑셀 규격 정보
 * </pre>
 *
 * @author nichefish
 */
@RequiredArgsConstructor
public enum XlsxType {

    NOTICE(
            "공지사항",
            "공지사항 #1",
            "공지사항",
            XlsxConstant.NOTICE
    );

    public final String fileNm;
    public final String sheetNm;
    public final String title;
    public final List<XlsxCell> headers;
}