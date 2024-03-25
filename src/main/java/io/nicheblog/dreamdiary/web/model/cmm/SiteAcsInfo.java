package io.nicheblog.dreamdiary.web.model.cmm;

import io.nicheblog.dreamdiary.web.SiteTopMenu;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import static io.nicheblog.dreamdiary.web.SiteTopMenu.NO_ASIDE;


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
public class SiteAcsInfo {

    /**
     * 사이드바 메뉴 번호
     */
    private String menuNo;

    /**
     * 사이드바 상위 메뉴
     */
    private SiteTopMenu topMenu;
    /**
     * 사이드바 상위 메뉴 번호 (메뉴 활성화용)
     */
    private String topMenuNo;
    /**
     * 사이드바 상위 메뉴 이름 (메뉴 표시용)
     */
    private String topMenuNm;

    /**
     * 사이드바 메뉴 이름 (로깅 및 사이트 헤더 표시용)
     */
    private String menuNm;

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
    public SiteAcsInfo(SiteTopMenu siteTopMenu, String siteMenuNo, String siteMenuNm, String url) {
        this.topMenu = siteTopMenu;
        this.topMenuNo = siteTopMenu.menuNo;
        this.topMenuNm = siteTopMenu.name();
        this.menuNo = siteMenuNo;
        this.menuNm = siteMenuNm;
        this.url = url;
        // 로그인/메인/에러페이지는 사이드바 미노출
        if (NO_ASIDE.equals(siteTopMenu)) this.asideAt = false;
    }

    /** 페이지명 세팅 */
    public SiteAcsInfo setAcsPageInfo(
            final String acsPageNm
    ) {
        this.acsPageNm = acsPageNm;
        return this;
    }
}
