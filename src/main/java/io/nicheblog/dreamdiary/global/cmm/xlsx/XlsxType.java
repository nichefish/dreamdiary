package io.nicheblog.dreamdiary.global.cmm.xlsx;

import io.nicheblog.dreamdiary.global.cmm.xlsx.model.XlsxCell;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * ContentType
 * <pre>
 *  ClsfEntity를 상속받은 클래스들이 사용하는 컨텐츠 타입 정보
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