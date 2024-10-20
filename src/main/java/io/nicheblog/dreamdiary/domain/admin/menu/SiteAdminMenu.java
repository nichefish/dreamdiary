package io.nicheblog.dreamdiary.domain.admin.menu;

import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.model.SiteAcsInfo;

import java.util.List;

/**
 * SiteAdminMenu
 * <pre>
 *  공통 상수 :: 사이트 메뉴 번호, 이름, URL 정의.
 * </pre>
 *
 * @author nichefish
 */
public interface SiteAdminMenu {

    String TOP_MENU_IDX = "00";

    // 공통화면 :: 메인 (관리자)
    SiteAcsInfo ADMIN_MAIN = new SiteAcsInfo(
            SiteTopMenu.ADMIN_MAIN,
            TOP_MENU_IDX,
            "메인",
            Url.ADMIN_MAIN
    );

    // 대메뉴 :: 사이트 관리
    SiteAcsInfo ADMIN = new SiteAcsInfo(
            SiteTopMenu.ADMIN,
            TOP_MENU_IDX,
            "사이트 관리",
            Url.AUTH_LGN_FORM,
            List.of(SubMenu.ADMIN_PAGE, SubMenu.MENU, SubMenu.TMPLAT, SubMenu.POPUP, SubMenu.CD)
    );
    SiteAcsInfo ADMIN_PAGE = SubMenu.ADMIN_PAGE;
    SiteAcsInfo LGN_POLICY = SubMenu.LGN_POLICY;
    SiteAcsInfo MENU = SubMenu.MENU;
    SiteAcsInfo TMPLAT = SubMenu.TMPLAT;
    SiteAcsInfo POPUP = SubMenu.POPUP;
    SiteAcsInfo CD = SubMenu.CD;

    // 대메뉴 :: 컨텐츠 관리
    SiteAcsInfo CONTENT = new SiteAcsInfo(
            SiteTopMenu.CONTENT,
            TOP_MENU_IDX,
            "컨텐츠 관리",
            Url.AUTH_LGN_FORM,
            List.of(SubMenu.BOARD_DEF, SubMenu.TAG)
    );
    SiteAcsInfo BOARD_DEF = SubMenu.BOARD_DEF;
    SiteAcsInfo TAG = SubMenu.TAG;

    // 대메뉴 :: 사용자 관리
    SiteAcsInfo USER = new SiteAcsInfo(
            SiteTopMenu.USER,
            TOP_MENU_IDX,
            "사용자 관리",
            Url.AUTH_LGN_FORM,
            List.of(SubMenu.USER_INFO, SubMenu.LGN_POLICY)
    );
    SiteAcsInfo USER_INFO = SubMenu.USER_INFO;

    // 대메뉴 :: 휴가 관리
    SiteAcsInfo VCATN_ADMIN = new SiteAcsInfo(
            SiteTopMenu.VCATN_ADMIN,
            TOP_MENU_IDX,
            "일정",
            Url.SCHDUL_CAL,
            List.of(SubMenu.VCATN_STATS, SubMenu.VCATN_SCHDUL)
    );
    SiteAcsInfo VCATN_STATS = SubMenu.VCATN_STATS;
    SiteAcsInfo VCATN_SCHDUL = SubMenu.VCATN_SCHDUL;

    // 대메뉴 :: 로그 관리
    SiteAcsInfo LOG = new SiteAcsInfo(
            SiteTopMenu.ADMIN,
            TOP_MENU_IDX,
            "로그 관리",
            Url.AUTH_LGN_FORM,
            List.of(SubMenu.LOG_ACTVTY, SubMenu.LOG_SYS)
    );
    SiteAcsInfo LOG_ACTVTY = SubMenu.LOG_ACTVTY;
    SiteAcsInfo LOG_SYS = SubMenu.LOG_SYS;

    /**
     * 서브메뉴 정보
     */
    interface SubMenu {

        /* 사용자 관리 */
        // 소메뉴 :: 사용자 정보
        SiteAcsInfo USER_INFO = new SiteAcsInfo(
                SiteTopMenu.USER,
                "01",
                "계정 관리",
                Url.USER_LIST
        );

        /* 휴가 관리 */
        // 소메뉴 :: 경비지출누적집계
        SiteAcsInfo VCATN_STATS = new SiteAcsInfo(
                SiteTopMenu.VCATN_ADMIN,
                "01",
                "년도별 휴가관리",
                Url.VCATN_STATS_YY
        );
        // 소메뉴 :: 휴가사용일자
        SiteAcsInfo VCATN_SCHDUL = new SiteAcsInfo(
                SiteTopMenu.VCATN_ADMIN,
                "02",
                "휴가사용일자",
                Url.VCATN_SCHDUL_LIST
        );

        /* 사이트 관리 */
        // 소메뉴 :: 사이트 관리
        SiteAcsInfo ADMIN_PAGE = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                "01",
                "사이트 관리",
                Url.ADMIN_PAGE
        );
        // 소메뉴 :: 로그인 정책 관리
        SiteAcsInfo LGN_POLICY = new SiteAcsInfo(
                SiteTopMenu.USER,
                "02",
                "로그인 정책 관리",
                Url.LGN_POLICY_FORM
        );
        // 소메뉴 :: 메뉴 관리
        SiteAcsInfo MENU = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                "02",
                "메뉴 관리",
                Url.MENU_PAGE
        );
        // 소메뉴 :: 템플릿 관리
        SiteAcsInfo TMPLAT = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                "05",
                "템플릿 관리",
                Url.TMPLAT_DEF_LIST
        );
        // 소메뉴 :: 팝업 관리
        SiteAcsInfo POPUP = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                "06",
                "팝업 관리",
                Url.POPUP_LIST
        );
        // 소메뉴 :: 코드 관리
        SiteAcsInfo CD = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                "07",
                "코드 관리",
                Url.CL_CD_LIST
        );

        /* 컨텐츠 관리 */
        // 소메뉴 :: 게시판 관리
        SiteAcsInfo BOARD_DEF = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                "01",
                "게시판 관리",
                Url.BOARD_DEF_LIST
        );
        // 소메뉴 :: 태그 관리
        SiteAcsInfo TAG = new SiteAcsInfo(
                SiteTopMenu.CONTENT,
                "02",
                "태그 관리",
                Url.TAG_LIST
        );

        /* 로그 관리 */
        // 소메뉴 :: 활동 로그
        SiteAcsInfo LOG_ACTVTY = new SiteAcsInfo(
                SiteTopMenu.LOG,
                "01",
                "활동 로그",
                Url.LOG_ACTVTY_LIST
        );
        // 소메뉴 :: 시스템 로그
        SiteAcsInfo LOG_SYS = new SiteAcsInfo(
                SiteTopMenu.LOG,
                "02",
                "시스템 로그",
                Url.LOG_SYS_LIST
        );
    }

}