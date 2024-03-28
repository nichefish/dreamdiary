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

    /**
     * 게시판 메뉴 갱신 로직
     * TODO: 좀더 고쳐야한다...
     */
    public void refreshBoardMenu(List<SiteAcsInfo> boardDefMenuList) {
        BOARD.setSubMenuList(boardDefMenuList);
    }


    // 공통화면 :: 로그인
    public static SiteAcsInfo LGN_PAGE = new SiteAcsInfo(
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

    // 대메뉴 :: 게시판
    public static SiteAcsInfo BOARD = new SiteAcsInfo(
            SiteTopMenu.BOARD,
            TOP_MENU_IDX,
            "일반게시판",
            SiteUrl.BOARD_POST_LIST
    );

    // 대메뉴 :: 사용자 관리
    public static SiteAcsInfo USER = new SiteAcsInfo(
            SiteTopMenu.USER,
            TOP_MENU_IDX,
            "사용자 관리",
            SiteUrl.AUTH_LGN_FORM,
            List.of(SubMenu.USER_INFO)
    );
    public static SiteAcsInfo USER_INFO = SubMenu.USER_INFO;

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

        String MENU_IDX_DREAM = "01";
        String MENU_IDX_DREAM_CAL = "02";

        // 소메뉴 :: 꿈 일자
        SiteAcsInfo DREAM_DAY = new SiteAcsInfo(
                SiteTopMenu.DREAM,
                MENU_IDX_DREAM,
                "꿈 일자",
                SiteUrl.DREAM_DAY_PAGE
        );
        // 소메뉴 :: 꿈 달력
        SiteAcsInfo DREAM_DAY_CAL = new SiteAcsInfo(
                SiteTopMenu.DREAM,
                MENU_IDX_DREAM_CAL,
                "꿈 달력",
                SiteUrl.DREAM_DAY_PAGE
        );

        /* 사용자 관리 */
        String MENU_IDX_USER = "01";

        // 소메뉴 :: 사용자 정보
        SiteAcsInfo USER_INFO = new SiteAcsInfo(
                SiteTopMenu.USER,
                MENU_IDX_USER,
                "계정 관리",
                SiteUrl.USER_LIST
        );

        /* 사이트 관리 */
        String MENU_IDX_LGN_POLICY = "01";
        String MENU_IDX_MENU = "02";
        String MENU_IDX_BOARD_DEF = "03";
        String MENU_IDX_TMPLAT = "04";
        String MENU_IDX_POPUP = "05";
        String MENU_IDX_CD = "06";

        // 소메뉴 :: 로그인 정책 관리
        SiteAcsInfo LGN_POLICY = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                MENU_IDX_LGN_POLICY,
                "로그인 정책 관리",
                SiteUrl.LGN_POLICY_FORM
        );
        // 소메뉴 :: 메뉴 관리
        SiteAcsInfo MENU = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                MENU_IDX_MENU,
                "메뉴 관리",
                SiteUrl.MENU_LIST
        );
        // 소메뉴 :: 게시판 관리
        SiteAcsInfo BOARD_DEF = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                MENU_IDX_BOARD_DEF,
                "게시판 관리",
                SiteUrl.BOARD_DEF_LIST
        );
        // 소메뉴 :: 템플릿 관리
        SiteAcsInfo TMPLAT = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                MENU_IDX_TMPLAT,
                "템플릿 관리",
                SiteUrl.TMPLAT_DEF_LIST
        );
        // 소메뉴 :: 팝업 관리
        SiteAcsInfo POPUP = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                MENU_IDX_POPUP,
                "팝업 관리",
                SiteUrl.POPUP_LIST
        );
        // 소메뉴 :: 코드 관리
        SiteAcsInfo CD = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                MENU_IDX_CD,
                "코드 관리",
                SiteUrl.CL_CD_LIST
        );

        /* 로그 관리 */
        String MENU_IDX_LOG_ACTVTY = "01";
        String MENU_IDX_LOG_SYS = "02";

        // 소메뉴 :: 활동 로그
        SiteAcsInfo LOG_ACTVTY = new SiteAcsInfo(
                SiteTopMenu.LOG,
                MENU_IDX_LOG_ACTVTY,
                "활동 로그",
                SiteUrl.LOG_ACTVTY_LIST
        );
        // 소메뉴 :: 시스템 로그
        SiteAcsInfo LOG_SYS = new SiteAcsInfo(
                SiteTopMenu.LOG,
                MENU_IDX_LOG_SYS,
                "시스템 로그",
                SiteUrl.LOG_SYS_LIST
        );
    }

}