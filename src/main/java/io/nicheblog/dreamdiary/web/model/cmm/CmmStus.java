package io.nicheblog.dreamdiary.web.model.cmm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CmmStus
 * <pre>
 *  템플릿엔진 표출시 코드에 따라 바뀌는 정보 위임용 정의
 * </pre>
 * TODO: 아직 미적용
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
public class CmmStus {

    /**
     * 상태명
     */
    private String nm;
    /**
     * 상태 클래스명(색상)
     */
    private String classNm;
    /**
     * 상태 아이콘(클래스)
     */
    private String icon;

    /* ----- */

    /**
     * 생성자
     */
    public CmmStus(final String nm) {
        this.nm = nm;
    }

    public CmmStus(
            final String nm,
            final String classNm
    ) {
        this(nm);
        this.classNm = classNm;
    }

    public CmmStus(
            final String nm,
            final String classNm,
            final String icon
    ) {
        this(nm, classNm);
        this.icon = icon;
    }
}
