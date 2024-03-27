package io.nicheblog.dreamdiary.web;

import io.nicheblog.dreamdiary.web.model.board.BoardDefDto;
import io.nicheblog.dreamdiary.web.model.cmm.SiteAcsInfo;
import io.nicheblog.dreamdiary.web.service.board.BoardDefService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static io.nicheblog.dreamdiary.web.SiteMenu.SubMenu.user_profl;

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

    @PostConstruct
    private void init() throws Exception {
        BOARD.setSubMenuList(boardDefService.boardDefMenuList());
    }

    /** 게시판 메뉴 갱신 로직 */
    public void refreshBoardMenu(List<SiteAcsInfo> boardDefMenuList) {
        BOARD.setSubMenuList(boardDefMenuList);
    }


    // 공통화면 :: 로그인
    public static SiteAcsInfo LGN_PAGE = new SiteAcsInfo(
            SiteTopMenu.NO_ASIDE,
            SiteTopMenu.NO_ASIDE.menuNo,
            "로그인",
            SiteUrl.AUTH_LGN_FORM
    );

    // 공통화면 :: 메인
    public static SiteAcsInfo MAIN_PORTAL = new SiteAcsInfo(
            SiteTopMenu.MAIN,
            SiteTopMenu.MAIN.menuNo,
            "메인",
            SiteUrl.MAIN
    );

    // 공통화면 :: 메인 (관리자)
    public static SiteAcsInfo MAIN_ADMIN = new SiteAcsInfo(
            SiteTopMenu.MAIN,
            SiteTopMenu.MAIN.menuNo,
            "메인",
            SiteUrl.ADMIN_MAIN
    );

    // 공통화면 :: 내 정보
    public static SiteAcsInfo USER_MY = new SiteAcsInfo(
            SiteTopMenu.MAIN,
            SiteTopMenu.MAIN.menuNo,
            "내 정보",
            SiteUrl.USER_MY_DTL
    );

    /* ----- */

    // 대메뉴 :: 꿈
    public static SiteAcsInfo DREAM = new SiteAcsInfo(
            SiteTopMenu.DREAM,
            SiteTopMenu.DREAM.menuNo,
            "꿈 관리",
            SiteUrl.DREAM_DAY_PAGE,
            List.of(SubMenu.DREAM_DAY)
    );

    // 대메뉴 :: 게시판
    public static SiteAcsInfo BOARD = new SiteAcsInfo(
            SiteTopMenu.BOARD,
            SiteTopMenu.BOARD.menuNo,
            "게시판",
            SiteUrl.BOARD_POST_LIST
    );


    // 대메뉴 :: 일정
    public static SiteAcsInfo SCHDUL = new SiteAcsInfo(
            SiteTopMenu.SCHDUL,
            SiteTopMenu.SCHDUL.menuNo,
            "메인",
            SiteUrl.MAIN
    );

    // 대메뉴 :: 공지사항
    public static SiteAcsInfo NOTICE = new SiteAcsInfo(
            SiteTopMenu.NOTICE,
            SiteTopMenu.NOTICE.menuNo,
            "공지사항",
            SiteUrl.NOTICE_LIST
    );

    // 대메뉴 :: 사용자 관리
    public static SiteAcsInfo USER = new SiteAcsInfo(
            SiteTopMenu.USER,
            SiteTopMenu.USER.menuNo,
            "사용자 관리",
            SiteUrl.AUTH_LGN_FORM,
            List.of(user_profl)
    );

    // 대메뉴 :: 사이트 관리
    public static SiteAcsInfo ADMIN = new SiteAcsInfo(
            SiteTopMenu.ADMIN,
            SiteTopMenu.ADMIN.menuNo,
            "사이트 관리",
            SiteUrl.AUTH_LGN_FORM,
            List.of(SubMenu.LGN_POLICY, SubMenu.CD, SubMenu.BOARD_DEF)
    );

    // 대메뉴 :: 로그 관리
    public static SiteAcsInfo LOG = new SiteAcsInfo(
            SiteTopMenu.ADMIN,
            SiteTopMenu.ADMIN.menuNo,
            "로그 관리",
            SiteUrl.AUTH_LGN_FORM,
            List.of(SubMenu.LOG_ACTVTY, SubMenu.LOG_SYS)
    );

    /**
     * 서브메뉴 정보
     */
    public interface SubMenu {

        // 소메뉴 :: 꿈 일자
        String MENU_NO_DREAM = "01000000";
        SiteAcsInfo DREAM_DAY = new SiteAcsInfo(
                SiteTopMenu.DREAM,
                MENU_NO_DREAM,
                "꿈",
                SiteUrl.DREAM_DAY_PAGE
        );

        /* 사용자 관리 */

        // 소메뉴 :: 사용자 정보
        String MENU_NO_USER = "01000000";
        SiteAcsInfo user_profl = new SiteAcsInfo(
                SiteTopMenu.USER,
                MENU_NO_USER,
                "사용자 관리",
                SiteUrl.USER_LIST
        );

        /* 사이트 관리 */

        // 소메뉴 :: 로그인 정책 관리
        String MENU_NO_LGN_POLICY = "01000000";
        SiteAcsInfo LGN_POLICY = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                MENU_NO_LGN_POLICY,
                "로그인 정책 관리",
                SiteUrl.LGN_POLICY_FORM
        );

        // 소메뉴 :: 코드 관리
        String MENU_NO_CD = "01000000";
        SiteAcsInfo CD = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                MENU_NO_CD,
                "코드 관리",
                SiteUrl.CL_CD_LIST
        );

        // 소메뉴 :: 게시판 관리
        String MENU_NO_BOARD_DEF = "01000000";
        SiteAcsInfo BOARD_DEF = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                MENU_NO_BOARD_DEF,
                "게시판 관리",
                SiteUrl.BOARD_DEF_LIST
        );

        /* 로그 관리 */

        // 소메뉴 :: 활동 로그
        String MENU_NO_LOG_ACTVTY = "01000000";
        SiteAcsInfo LOG_ACTVTY = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                MENU_NO_LOG_ACTVTY,
                "활동 로그",
                SiteUrl.AUTH_LGN_FORM
        );

        // 소메뉴 :: 시스템 로그
        String MENU_NO_LOG_SYS = "01000000";
        SiteAcsInfo LOG_SYS = new SiteAcsInfo(
                SiteTopMenu.ADMIN,
                MENU_NO_LOG_SYS,
                "시스템 로그",
                SiteUrl.AUTH_LGN_FORM
        );
    }

}