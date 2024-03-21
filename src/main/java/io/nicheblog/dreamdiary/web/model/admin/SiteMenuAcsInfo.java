package io.nicheblog.dreamdiary.web.model.admin;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

/**
 * SiteMenuAcsInfo
 * <pre>
 *  메뉴 및 화면 정보 Dto
 * </pre>
 *
 * @author nichefish
 */
@Log4j2
@Getter
@Setter
public class SiteMenuAcsInfo {

    /**
     * 사이드바 메뉴 번호
     */
    private String siteMenuNo;

    /**
     * 사이드바 상위 메뉴 번호 (메뉴 활성화용)
     */
    private String topMenuNo;

    /**
     * 사이드바 메뉴 이름 (로깅 및 사이트 헤더 표시용)
     */
    private String siteMenuNm;

    /**
     * 접근 화면 이름 (로깅 및 사이트 헤더 표시용)
     */
    private String acsPageNm;

    /**
     * 접근(처리) url
     */
    private String url;

    /**
     * 사이드메뉴 표시 여부
     */
    private Boolean asideAt = true;

    /* ----- */

    /** constructor */
    public SiteMenuAcsInfo(String topMenuNo, String siteMenuNo, String siteMenuNm, String url) {
        this.topMenuNo = topMenuNo;
        this.siteMenuNo = siteMenuNo;
        this.siteMenuNm = siteMenuNm;
        this.url = url;
        // TODO:: 로그인/메인/에러페이지는 사이드바 미노출
        this.asideAt = false;
    }

    /** 페이지명 세팅 */
    public SiteMenuAcsInfo setAcsPageInfo(
            final String acsPageNm
    ) {
        this.acsPageNm = acsPageNm;
        return this;
    }
}
