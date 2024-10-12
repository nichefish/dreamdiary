package io.nicheblog.dreamdiary.global.model;

import io.nicheblog.dreamdiary.domain.admin.menu.SiteTopMenu;
import io.nicheblog.dreamdiary.domain.admin.menu.model.AcsPageNm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import static io.nicheblog.dreamdiary.domain.admin.menu.SiteTopMenu.NO_ASIDE;

/**
 * SiteAcsInfo
 * <pre>
 *  메뉴 및 화면 정보 Dto.
 * </pre>
 *
 * @author nichefish
 */
@Log4j2
@Getter
@Setter
@NoArgsConstructor
public class SiteAcsInfo {

    /** 사이드바 메뉴 번호 */
    private String menuNo;

    /** 사이드바 상위 메뉴 */
    private SiteTopMenu topMenu;
    /** 사이드바 상위 메뉴 번호 (메뉴 활성화용) */
    private String topMenuNo;
    /** 사이드바 상위 메뉴 이름 (툴바 표시용) */
    private String topMenuNm;
    /** 사이드바 상위 메뉴 코드 (메뉴 표시용) */
    private String topMenuLabel;

    /** 사이드바 메뉴 이름 (로깅 및 사이트 헤더 표시용) */
    private String menuNm;

    /** 접근 화면 이름 (로깅 및 사이트 헤더 표시용) */
    private String acsPageNm;

    /** 접근(처리) url */
    private String url;

    /** 관리자메뉴 여부 */
    private Boolean isMngrMenu = false;
    /** 사이드메뉴 표시 여부 */
    private Boolean asideAt = true;

    /** 하위 메뉴 리스트 */
    private List<SiteAcsInfo> subMenuList;

    /* ----- */

    /** constructor */
    public SiteAcsInfo(SiteTopMenu topMenu, String menuIdx, String menuNm, String url) {
        this(topMenu, menuIdx, menuNm, url, null);
    }
    public SiteAcsInfo(SiteTopMenu topMenu, String menuIdx, String menuNm, String url, List<SiteAcsInfo> subMenuList) {
        this.topMenu = topMenu;
        this.topMenuNo = topMenu.menuNo;
        this.topMenuNm = topMenu.menuNm;
        this.topMenuLabel = topMenu.label;
        this.isMngrMenu = topMenu.isMngrMenu;
        this.menuNo = topMenu.idx + menuIdx;
        this.menuNm = menuNm;
        this.url = url;
        this.subMenuList = subMenuList;
        // 로그인/메인/에러페이지는 사이드바 미노출
        if (NO_ASIDE.equals(topMenu)) this.asideAt = false;
    }

    /** 페이지명 세팅 (for breadcrumb) */
    public SiteAcsInfo setAcsPageInfo(final String acsPageNm) {
        this.acsPageNm = acsPageNm;
        return this;
    }
    public SiteAcsInfo setAcsPageInfo(final AcsPageNm acsPageNm) {
        this.acsPageNm = acsPageNm.pageNm;
        return this;
    }

    /** 메인메뉴 여부 체크 */
    public Boolean getIsTopMenu() {
        return this.menuNo.equals(this.topMenuNo);
    }
}
