package io.nicheblog.dreamdiary.web;

import io.nicheblog.dreamdiary.web.model.cmm.SiteAcsInfo;
import io.nicheblog.dreamdiary.web.service.board.BoardDefService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * SiteMenu
 * <pre>
 *  공통 상수 :: 사이트 메뉴 번호, 이름, URL 정의
 * </pre>
 *
 * @author nichefish
 */
@Component("siteMenu")
public class SiteMenu {

    @Resource(name = "boardDefService")
    private BoardDefService boardDefService;

    public static String TOP_MENU_IDX = "00";

    @PostConstruct
    private void init() throws Exception {
        BOARD.setSubMenuList(boardDefService.boardDefMenuList());
    }

    // 공통화면 :: 로그인
    public static SiteAcsInfo LGN_PAGE = new SiteAcsInfo(
            SiteTopMenu.LGN,
            TOP_MENU_IDX,
            "로그인",
            SiteUrl.AUTH_LGN_FORM
    );

    // 공통화면 :: 신규계정 신청
    public static SiteAcsInfo USER_REQST = new SiteAcsInfo(
            SiteTopMenu.LGN,
            TOP_MENU_IDX,
            "로그인",
            SiteUrl.AUTH_LGN_FORM
    );

    // 공통화면 :: 메인
    public static SiteAcsInfo MAIN_PORTAL = new SiteAcsInfo(
            SiteTopMenu.MAIN,
            TOP_MENU_IDX,
            "메인",
            SiteUrl.MAIN
    );

    // 공통화면 :: 메인 (관리자)
    public static SiteAcsInfo ADMIN_MAIN = new SiteAcsInfo(
            SiteTopMenu.ADMIN_MAIN,
            TOP_MENU_IDX,
            "메인",
            SiteUrl.ADMIN_MAIN
    );

    // 공통화면 :: 에러
    public static SiteAcsInfo ERROR = new SiteAcsInfo(
            SiteTopMenu.ERROR,
            TOP_MENU_IDX,
            "ERROR",
            SiteUrl.ERROR
    );

    // 공통화면 :: 내 정보
    public static SiteAcsInfo USER_MY = new SiteAcsInfo(
            SiteTopMenu.MAIN,
            TOP_MENU_IDX,
            "내 정보",
            SiteUrl.USER_MY_DTL
    );

    /* ----- */

    // 대메뉴 :: 공지사항
    public static SiteAcsInfo NOTICE = new SiteAcsInfo(
            SiteTopMenu.NOTICE,
            TOP_MENU_IDX,
            "공지사항",
            SiteUrl.NOTICE_LIST
    );

    // 대메뉴 :: 꿈
    public static SiteAcsInfo DREAM = new SiteAcsInfo(
            SiteTopMenu.DREAM,
            TOP_MENU_IDX,
            "꿈 관리",
            SiteUrl.DREAM_DAY_PAGE,
            List.of(SubMenu.DREAM_DAY, SubMenu.DREAM_DAY_CAL)
    );
    public static SiteAcsInfo DREAM_DAY = SubMenu.DREAM_DAY;
    public static SiteAcsInfo DREAM_DAY_CAL = SubMenu.DREAM_DAY_CAL;

    // 대메뉴 :: 일반게시판
    public static SiteAcsInfo BOARD = new SiteAcsInfo(
            SiteTopMenu.BOARD,
            TOP_MENU_IDX,
            "일반게시판",
            SiteUrl.BOARD_POST_LIST
    );

    // 대메뉴 :: 일정
    public static SiteAcsInfo SCHDUL = new SiteAcsInfo(
            SiteTopMenu.EXPTR,
            TOP_MENU_IDX,
            "일정",
            SiteUrl.SCHDUL_CAL,
            List.of(SubMenu.SCHDUL_CAL, SubMenu.VCATN_PAPR)
    );
    public static SiteAcsInfo SCHDUL_CAL = SubMenu.SCHDUL_CAL;
    public static SiteAcsInfo VCATN_PAPR = SubMenu.VCATN_PAPR;

    // 대메뉴 :: 경비
    public static SiteAcsInfo EXPTR = new SiteAcsInfo(
            SiteTopMenu.EXPTR,
            TOP_MENU_IDX,
            "경비",
            SiteUrl.EXPTR_PRSNL_PAPR_LIST,
            List.of(SubMenu.EXPTR_PRSNL_PAPR, SubMenu.EXPTR_REQST)
    );
    public static SiteAcsInfo EXPTR_PRSNL_PAPR = SubMenu.EXPTR_PRSNL_PAPR;
    public static SiteAcsInfo EXPTR_REQST = SubMenu.EXPTR_REQST;

    // 대메뉴 :: 프로젝트
    public static SiteAcsInfo PRJCT = new SiteAcsInfo(
            SiteTopMenu.PRJCT,
            TOP_MENU_IDX,
            "프로젝트 관리",
            SiteUrl.PRJCT_INFO_LIST,
            List.of(SubMenu.PRJCT_INFO)
    );
    public static SiteAcsInfo PRJCT_INFO = SubMenu.PRJCT_INFO;

    // 대메뉴 :: 사용자 관리
    public static SiteAcsInfo USER = new SiteAcsInfo(
            SiteTopMenu.USER,
            TOP_MENU_IDX,
            "사용자 관리",
            SiteUrl.AUTH_LGN_FORM,
            List.of(SubMenu.USER_INFO)
    );
    public static SiteAcsInfo USER_INFO = SubMenu.USER_INFO;

    // 대메뉴 :: 휴가 관리
    public static SiteAcsInfo VCATN_ADMIN = new SiteAcsInfo(
            SiteTopMenu.VCATN_ADMIN,
            TOP_MENU_IDX,
            "일정",
            SiteUrl.SCHDUL_CAL,
            List.of(SubMenu.VCATN_STATS, SubMenu.VCATN_DY)
    );
    public static SiteAcsInfo VCATN_STATS = SubMenu.VCATN_STATS;
    public static SiteAcsInfo VCATN_DY = SubMenu.VCATN_DY;

    // 대메뉴 :: 경비 관리
    public static SiteAcsInfo EXPTR_ADMIN = new SiteAcsInfo(
            SiteTopMenu.EXPTR_ADMIN,
            TOP_MENU_IDX,
            "경비",
            SiteUrl.EXPTR_PRSNL_PAPR_LIST,
            List.of(SubMenu.EXPTR_PRSNL_RPT, SubMenu.EXPTR_PRSNL_STATS)
    );
    public static SiteAcsInfo EXPTR_PRSNL_RPT = SubMenu.EXPTR_PRSNL_RPT;
    public static SiteAcsInfo EXPTR_PRSNL_STATS = SubMenu.EXPTR_PRSNL_STATS;

    // 대메뉴 :: 사이트 관리
    public static SiteAcsInfo ADMIN = new SiteAcsInfo(
            SiteTopMenu.ADMIN,
            TOP_MENU_IDX,
            "사이트 관리",
            SiteUrl.AUTH_LGN_FORM,
            List.of(SubMenu.LGN_POLICY, SubMenu.MENU, SubMenu.BOARD_DEF, SubMenu.TMPLAT, SubMenu.POPUP, SubMenu.CD)
    );
    public static SiteAcsInfo LGN_POLICY = SubMenu.LGN_POLICY;
    public static SiteAcsInfo MENU = SubMenu.MENU;
    public static SiteAcsInfo BOARD_DEF = SubMenu.BOARD_DEF;
    public static SiteAcsInfo TMPLAT = SubMenu.TMPLAT;
    public static SiteAcsInfo POPUP = SubMenu.POPUP;
    public static SiteAcsInfo CD = SubMenu.CD;

    // 대메뉴 :: 로그 관리
    public static SiteAcsInfo LOG = new SiteAcsInfo(
            SiteTopMenu.ADMIN,
            TOP_MENU_IDX,
            "로그 관리",
            SiteUrl.AUTH_LGN_FORM,
            List.of(SubMenu.LOG_ACTVTY, SubMenu.LOG_SYS)
    );
    public static SiteAcsInfo LOG_ACTVTY = SubMenu.LOG_ACTVTY;
    public static SiteAcsInfo LOG_SYS = SubMenu.LOG_SYS;

    /**
     * 서브메뉴 정보
     */
    public interface SubMenu {

        // 소메뉴 :: 꿈 일자
        SiteAcsInfo DREAM_DAY = new SiteAcsInfo(
                SiteTopMenu.DREAM,
                "01",
                "꿈 일자",
                SiteUrl.DREAM_DAY_PAGE
        );
        // 소메뉴 :: 꿈 달력
        SiteAcsInfo DREAM_DAY_CAL = new SiteAcsInfo(
                SiteTopMenu.DREAM,
                "02",
                "꿈 달력",
                SiteUrl.DREAM_DAY_PAGE
        );

        // 소메뉴 :: 경비지출서
        SiteAcsInfo EXPTR_PRSNL_PAPR = new SiteAcsInfo(
                SiteTopMenu.EXPTR,
                "01",
                "경비지출서",
                SiteUrl.EXPTR_PRSNL_PAPR_LIST
        );
        // 소메뉴 :: 물품구매/경조사비 신청
        SiteAcsInfo EXPTR_REQST = new SiteAcsInfo(
                SiteTopMenu.EXPTR,
                "02",
                "물품구매/경조사비 신청",
                SiteUrl.EXPTR_REQST_LIST
        );

        // 소메뉴 :: 일정
        SiteAcsInfo SCHDUL_CAL = new SiteAcsInfo(
                SiteTopMenu.SCHDUL,
                "01",
                "일정 달력",
                SiteUrl.SCHDUL_CAL
        );
        // 소메뉴 :: 휴가계획서
        SiteAcsInfo VCATN_PAPR = new SiteAcsInfo(
                SiteTopMenu.SCHDUL,
                "02",
                "휴가계획서",
                SiteUrl.VCATN_PAPR_LIST
        );

        // 소메뉴 :: 프로젝트 관리
        SiteAcsInfo PRJCT_INFO = new SiteAcsInfo(
                SiteTopMenu.PRJCT,
                "01",
                "프로젝트 관리",
                SiteUrl.PRJCT_INFO_LIST
        );

        /* 사용자 관리 */
        // 소메뉴 :: 사용자 정보
        SiteAcsInfo USER_INFO = new SiteAcsInfo(
                SiteTopMenu.USER,
                "01",
                "계정 관리",
                SiteUrl.USER_LIST
        );

        /* 휴가 관리 */
        // 소메뉴 :: 경비지출누적집계
        SiteAcsInfo VCATN_STATS = new SiteAcsInfo(
                SiteTopMenu.VCATN_ADMIN,
                "01",
                "년도별 휴가관리",
                SiteUrl.VCATN_STATS_YY
        );
        // 소메뉴 :: 휴가사용일자
        SiteAcsInfo VCATN_DY = new SiteAcsInfo(
                SiteTopMenu.VCATN_ADMIN,
                "02",
                "휴가사용일자",
                SiteUrl.VCATN_DY_LIST
        );

        /* 경비 관리 */
        // 소메뉴 :: 월간지출내역
        SiteAcsInfo EXPTR_PRSNL_RPT = new SiteAcsInfo(
                SiteTopMenu.EXPTR_ADMIN,
                "01",
                "월간지출내역",
                SiteUrl.EXPTR_PRSNL_RPT_ITEMS
        );
        // 소메뉴 :: 경비지출누적집계
        SiteAcsInfo EXPTR_PRSNL_STATS = new SiteAcsInfo(
                SiteTopMenu.EXPTR_ADMIN,
                "02",
                "경비지출누적집계",
                SiteUrl.EXPTR_PRSNL_STATS_PAGE
        );

        /* 사이트 관리 */
        // 소메뉴 :: 로그인 정책 관리
        SiteAcsInfo LGN_POLICY = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                "01",
                "로그인 정책 관리",
                SiteUrl.LGN_POLICY_FORM
        );
        // 소메뉴 :: 메뉴 관리
        SiteAcsInfo MENU = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                "02",
                "메뉴 관리",
                SiteUrl.MENU_LIST
        );
        // 소메뉴 :: 게시판 관리
        SiteAcsInfo BOARD_DEF = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                "03",
                "게시판 관리",
                SiteUrl.BOARD_DEF_LIST
        );
        // 소메뉴 :: 템플릿 관리
        SiteAcsInfo TMPLAT = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                "05",
                "템플릿 관리",
                SiteUrl.TMPLAT_DEF_LIST
        );
        // 소메뉴 :: 팝업 관리
        SiteAcsInfo POPUP = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                "06",
                "팝업 관리",
                SiteUrl.POPUP_LIST
        );
        // 소메뉴 :: 코드 관리
        SiteAcsInfo CD = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                "07",
                "코드 관리",
                SiteUrl.CL_CD_LIST
        );

        /* 로그 관리 */
        // 소메뉴 :: 활동 로그
        SiteAcsInfo LOG_ACTVTY = new SiteAcsInfo(
                SiteTopMenu.LOG,
                "01",
                "활동 로그",
                SiteUrl.LOG_ACTVTY_LIST
        );
        // 소메뉴 :: 시스템 로그
        SiteAcsInfo LOG_SYS = new SiteAcsInfo(
                SiteTopMenu.LOG,
                "02",
                "시스템 로그",
                SiteUrl.LOG_SYS_LIST
        );
    }

}