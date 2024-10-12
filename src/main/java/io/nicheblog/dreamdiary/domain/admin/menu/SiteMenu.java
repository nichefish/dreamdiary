package io.nicheblog.dreamdiary.domain.admin.menu;

import io.nicheblog.dreamdiary.domain.board.def.service.BoardDefService;
import io.nicheblog.dreamdiary.global.Url;
import io.nicheblog.dreamdiary.global.model.SiteAcsInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * SiteMenu
 * <pre>
 *  공통 상수 :: 사이트 메뉴 번호, 이름, URL 정의.
 * </pre>
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
public class SiteMenu
    implements SiteAdminMenu {

    private final BoardDefService boardDefService;

    public static final String TOP_MENU_IDX = "00";

    @PostConstruct
    private void init() throws Exception {
        BOARD.setSubMenuList(boardDefService.boardDefMenuList());
    }

    // 공통화면 :: 로그인
    public static SiteAcsInfo LGN_PAGE = new SiteAcsInfo(
            SiteTopMenu.LGN,
            TOP_MENU_IDX,
            "로그인",
            Url.AUTH_LGN_FORM
    );

    // 공통화면 :: 신규계정 신청
    public static SiteAcsInfo USER_REQST = new SiteAcsInfo(
            SiteTopMenu.USER_REQST,
            TOP_MENU_IDX,
            "신규계정 신청",
            Url.AUTH_LGN_FORM
    );

    // 공통화면 :: 메인
    public static SiteAcsInfo MAIN_PORTAL = new SiteAcsInfo(
            SiteTopMenu.MAIN,
            TOP_MENU_IDX,
            "메인",
            Url.MAIN
    );

    // 공통화면 :: 에러
    public static SiteAcsInfo ERROR = new SiteAcsInfo(
            SiteTopMenu.ERROR,
            TOP_MENU_IDX,
            "ERROR",
            Url.ERROR
    );

    // 공통화면 :: 내 정보
    public static SiteAcsInfo USER_MY = new SiteAcsInfo(
            SiteTopMenu.MAIN,
            TOP_MENU_IDX,
            "내 정보",
            Url.USER_MY_DTL
    );

    /* ----- */

    // 대메뉴 :: 공지사항
    public static SiteAcsInfo NOTICE = new SiteAcsInfo(
            SiteTopMenu.NOTICE,
            TOP_MENU_IDX,
            "공지사항",
            Url.NOTICE_LIST
    );

    // 대메뉴 :: 저널
    public static SiteAcsInfo JRNL = new SiteAcsInfo(
            SiteTopMenu.JRNL,
            TOP_MENU_IDX,
            "저널",
            Url.JRNL_DAY_PAGE,
            List.of(SubMenu.JRNL_DAY, SubMenu.JRNL_SBJCT, SubMenu.JRNL_SUMRY)
    );
    public static SiteAcsInfo JRNL_DAY = SubMenu.JRNL_DAY;
    public static SiteAcsInfo JRNL_SBJCT = SubMenu.JRNL_SBJCT;
    public static SiteAcsInfo JRNL_SUMRY = SubMenu.JRNL_SUMRY;

    // 대메뉴 :: 게시판
    public static SiteAcsInfo BOARD = new SiteAcsInfo(
            SiteTopMenu.BOARD,
            TOP_MENU_IDX,
            "게시판",
            Url.BOARD_POST_LIST
    );

    // 대메뉴 :: 일정
    public static SiteAcsInfo SCHDUL = new SiteAcsInfo(
            SiteTopMenu.SCHDUL,
            TOP_MENU_IDX,
            "일정",
            Url.SCHDUL_CAL,
            List.of(SubMenu.SCHDUL_CAL, SubMenu.VCATN_PAPR)
    );
    public static SiteAcsInfo SCHDUL_CAL = SubMenu.SCHDUL_CAL;
    public static SiteAcsInfo VCATN_PAPR = SubMenu.VCATN_PAPR;

    /**
     * 서브메뉴 정보
     */
    public interface SubMenu {

        // 소메뉴 :: 저널 일자
        SiteAcsInfo JRNL_DAY = new SiteAcsInfo(
                SiteTopMenu.JRNL,
                "01",
                "저널 일자",
                Url.JRNL_DAY_PAGE
        );
        // 소메뉴 :: 저널 주제
        SiteAcsInfo JRNL_SBJCT = new SiteAcsInfo(
                SiteTopMenu.JRNL,
                "02",
                "저널 주제",
                Url.JRNL_SBJCT_LIST
        );
        // 소메뉴 :: 저널 결산
        SiteAcsInfo JRNL_SUMRY = new SiteAcsInfo(
                SiteTopMenu.JRNL,
                "03",
                "저널 결산",
                Url.JRNL_SUMRY_LIST
        );

        // 소메뉴 :: 태그
        SiteAcsInfo TAG = new SiteAcsInfo(
                SiteTopMenu.JRNL,
                "03",
                "저널 결산",
                Url.JRNL_SUMRY_LIST
        );

        // 소메뉴 :: 일정
        SiteAcsInfo SCHDUL_CAL = new SiteAcsInfo(
                SiteTopMenu.SCHDUL,
                "01",
                "일정 달력",
                Url.SCHDUL_CAL
        );
        // 소메뉴 :: 휴가계획서
        SiteAcsInfo VCATN_PAPR = new SiteAcsInfo(
                SiteTopMenu.SCHDUL,
                "02",
                "휴가계획서",
                Url.VCATN_PAPR_LIST
        );
    }

}