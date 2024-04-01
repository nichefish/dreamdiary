package io.nicheblog.dreamdiary.web;

/**
 * WebSiteMenu
 * <pre>
 *  공통 상수 :: 사이트 메뉴 번호, 이름, URL 정의
 * </pre>
 *
 * @author nichefish
 */
public interface SiteUrl {

    /** 도메인 */
    String DOMAIN = "dreamdiary.nicheblog.io";

    /** robot.txt */
    String ROBOT_TXT = "/robot.txt";
    String ROBOTS_TXT = "/robots.txt";

    /** 메인 */
    String ROOT = "/";
    String MAIN = "/main.do";
    String REACT_MAIN = "/react/main.do";

    /** 로그인 관련 */
    String AUTH_LGN_FORM = Prefix.AUTH + "/lgnForm.do";
    String AUTH_LGN_PROC = Prefix.AUTH + "/lgnProc.do";
    String AUTH_LGN_PW_CHG_AJAX = Prefix.AUTH + "/lgnPwChgAjax.do";
    String AUTH_LGOUT = Prefix.AUTH + "/lgout.do";

    String ADMIN_MAIN = Prefix.ADMIN + MAIN;
    String ADMIN_TEST_PAGE = Prefix.ADMIN + "/testPage.do";

    /** 꿈 일자 */
    String DREAM_DAY_PAGE = Prefix.DREAM_DAY + "/dreamDayPage.do";
    String DREAM_DAY_REG_AJAX = Prefix.DREAM_DAY + "/dreamDayRegAjax.do";
    String DREAM_DAY_MDF_AJAX = Prefix.DREAM_DAY + "/dreamDayMdfAjax.do";

    /** 꿈 조각 */
    String DREAM_PIECE_REG_AJAX = Prefix.DREAM_PIECE + "/dreamPieceRegAjax.do";
    String DREAM_PIECE_MDF_AJAX = Prefix.DREAM_PIECE + "/dreamPieceMdfAjax.do";

    /** 공지사항 */
    String NOTICE_LIST = Prefix.NOTICE + "/noticeList.do";
    String NOTICE_REG_FORM = Prefix.NOTICE + "/noticeRegForm.do";
    String NOTICE_REG_AJAX = Prefix.NOTICE + "/noticeRegAjax.do";
    String NOTICE_DTL = Prefix.NOTICE + "/noticeDtl.do";
    String NOTICE_DTL_AJAX = Prefix.NOTICE + "/noticeDtlAjax.do";
    String NOTICE_MDF_FORM = Prefix.NOTICE + "/noticeMdfForm.do";
    String NOTICE_MDF_AJAX = Prefix.NOTICE + "/noticeMdfAjax.do";
    String NOTICE_DEL_AJAX = Prefix.NOTICE + "/noticeDelAjax.do";
    String NOTICE_REG_PREVIEW_POP = Prefix.NOTICE + "/noticeRegPreviewPop.do";
    String NOTICE_POPUP_LIST_AJAX = Prefix.NOTICE + "/noticePopupListAjax.do";

    /** 게시판 */
    String BOARD_POST_LIST = Prefix.BOARD_POST + "/boardPostList.do";
    String BOARD_POST_REG_FORM = Prefix.BOARD_POST + "/boardPostRegForm.do";
    String BOARD_POST_REG_AJAX = Prefix.BOARD_POST + "/boardPostRegAjax.do";
    String BOARD_POST_DTL = Prefix.BOARD_POST + "/boardPostDtl.do";
    String BOARD_POST_DTL_AJAX = Prefix.BOARD_POST + "/boardPostDtlAjax.do";
    String BOARD_POST_MDF_FORM = Prefix.BOARD_POST + "/boardPostMdfForm.do";
    String BOARD_POST_MDF_AJAX = Prefix.BOARD_POST + "/boardPostMdfAjax.do";
    String BOARD_POST_DEL_AJAX = Prefix.BOARD_POST + "/boardPostDelAjax.do";
    String BOARD_POST_REG_PREVIEW_POP = Prefix.BOARD_POST + "/boardPostRegPreviewPop.do";

    /** 사용자 관리 */
    String USER_LIST = Prefix.USER + "/userList.do";
    String USER_REG_FORM = Prefix.USER + "/userRegForm.do";
    String USER_REG_AJAX = Prefix.USER + "/userRegAjax.do";
    String USER_DTL = Prefix.USER + "/userDtl.do";
    String USER_MDF_FORM = Prefix.USER + "/userMdfForm.do";
    String USER_MDF_AJAX = Prefix.USER + "/userMdfAjax.do";
    String USER_PW_RESET_AJAX = Prefix.USER + "/passwordResetAjax.do";
    String USER_DEL_AJAX = Prefix.USER + "/userDelAjax.do";
    String USER_LIST_XLSX_DOWNLOAD = Prefix.USER + "/userListXlsxDownload.do";
    String USER_ID_DUP_CHK_AJAX = Prefix.USER + "/userIdDupChkAjax.do";

    /** 내 정보 관리 */
    String USER_MY_DTL = Prefix.USER_MY + "/userMyDtl.do";
    String USER_MY_UPLOAD_PROFL_IMG_AJAX = Prefix.USER_MY + "/userMyUploadProflImgAjax.do";
    String USER_MY_REMOVE_PROFL_IMG_AJAX = Prefix.USER_MY + "/userMyRemoveProflImgAjax.do";
    String USER_MY_PW_CF_AJAX = Prefix.USER_MY + "/userMyPwCfAjax.do";
    String USER_MY_PW_CHG_AJAX = Prefix.USER_MY + "/userMyPwChgAjax.do";

    /** 댓글 */
    String COMMENT_LIST_AJAX = Prefix.COMMENT + "/commentListAjax.do";
    String COMMENT_REG_AJAX = Prefix.COMMENT + "/commentRegAjax.do";
    String COMMENT_MDF_AJAX = Prefix.COMMENT + "/commentMdfAjax.do";
    String COMMENT_DEL_AJAX = Prefix.COMMENT + "/commentDelAjax.do";

    /** 태그 */
    String TAG_LIST_AJAX = Prefix.TAG + "/tagListAjax.do";
    String TAG_DTL_AJAX = Prefix.TAG + "/tagDtlAjax.do";

    /** 로그인 정책 관리 */
    String LGN_POLICY_FORM = Prefix.LGN_POLICY + "/lgnPolicyForm.do";
    String LGN_POLICY_REG_AJAX = Prefix.LGN_POLICY + "/lgnPolicyRegAjax.do";

    /** 메뉴 관리 */
    String MENU_LIST = Prefix.MENU + "/menuList.do";
    String MENU_MAIN_LIST_AJAX = Prefix.MENU + "/menuMainListAjax.do";
    String MENU_REG_AJAX = Prefix.MENU + "/menuList.do";
    String MENU_DTL_AJAX = Prefix.MENU + "/menuDtlAjax.do";
    String MENU_MDF_AJAX = Prefix.MENU + "/menuMdfAjax.do";
    String MENU_DEL_AJAX = Prefix.MENU + "/menuDelAjax.do";

    /** 게시판 관리 */
    String BOARD_DEF_LIST = Prefix.BOARD_DEF + "/boardDefList.do";
    String BOARD_DEF_REG_AJAX = Prefix.BOARD_DEF + "/boardDefRegAjax.do";
    String BOARD_DEF_MDF_ITEM_AJAX = Prefix.BOARD_DEF + "/boardDefMdfItemAjax.do";
    String BOARD_DEF_DEL_AJAX = Prefix.BOARD_DEF + "/boardDefDelAjax.do";
    String BOARD_DEF_USE_AJAX = Prefix.BOARD_DEF + "/boardDefUseAjax.do";
    String BOARD_DEF_UNUSE_AJAX = Prefix.BOARD_DEF + "/boardDefUnuseAjax.do";

    /** 템플릿 관리 (TODO) */
    String TMPLAT_DEF_LIST = Prefix.TMPLAT + "/tmplatDefList.do";
    String TMPLAT_DEF_REG_AJAX = Prefix.TMPLAT + "/tmplatDefRegAjaxdo";
    String TMPLAT_DEF_MDF_AJAX = Prefix.TMPLAT + "/tmplatDefMdfAjax.do";
    String TMPLAT_DEF_DEL_AJAX = Prefix.TMPLAT + "/tmplatDefDelAjax.do";
    String TMPLAT_REG_AJAX = Prefix.TMPLAT + "/tmplatRegAjax.do";

    /** 팝업 관리 (TODO) */
    String POPUP_LIST = "";

    /** 코드 관리 */
    String CL_CD_LIST = Prefix.CD + "/clCdList.do";
    String CL_CD_DTL = Prefix.CD + "/clCdDtl.do";
    String CL_CD_REG_AJAX = Prefix.CD + "/clCdRegAjax.do";
    String CL_CD_DTL_AJAX = Prefix.CD + "/clCdDtlAjax.do";
    String CL_CD_MDF_AJAX = Prefix.CD + "/clCdMdfAjax.do";
    String CL_CD_DEL_AJAX = Prefix.CD + "/clCdDelAjax.do";
    String CL_CD_USE_AJAX = Prefix.CD + "/clCdUseAjax.do";
    String CL_CD_UNUSE_AJAX = Prefix.CD + "/clCdUnuseAjax.do";

    String DTL_CD_REG_AJAX = Prefix.CD + "/dtlCdRegAjax.do";
    String DTL_CD_DTL_AJAX = Prefix.CD + "/dtlCdDtlAjax.do";
    String DTL_CD_MDF_AJAX = Prefix.CD + "/dtlCdMdfAjax.do";
    String DTL_CD_LIST_AJAX = Prefix.CD + "/dtlCdListAjax.do";
    String DTL_CD_USE_AJAX = Prefix.CD + "/dtlCdUseAjax.do";
    String DTL_CD_UNUSE_AJAX = Prefix.CD + "/dtlCdUnuseAjax.do";
    String DTL_CD_DEL_AJAX = Prefix.CD + "/dtlCdDelAjax.do";
    
    /** 활동 로그 조회 */
    String LOG_ACTVTY_LIST = Prefix.LOG_ACTVTY + "/logActvtyList.do";
    String LOG_ACTVTY_DTL_AJAX = Prefix.LOG_ACTVTY + "/logActvtyDtlAjax.do";
    String LOG_ACTVTY_LIST_XLSX_DOWNLOAD = Prefix.LOG_ACTVTY + "";

    /** 시스템 로그 조회 */
    String LOG_SYS_LIST = Prefix.LOG_SYS + "/logSysList.do";
    String LOG_SYS_DTL_AJAX = Prefix.LOG_SYS + "/logSysDtlAjax.do";

    /** 로그 통계 조회 (TODO) */
    String LOG_STATS_USER_LIST = Prefix.LOG_STATS + "/logStatsUserList.do";

    /** 파일시스템 */
    String FLSYS_HOME = Prefix.FLSYS + "/flsysHome.do";
    String FLSYS_LIST_AJAX = Prefix.FLSYS + "/flsysListAjax.do";
    String FLSYS_FILE_DOWNLOAD = Prefix.FLSYS + "/flsysFileDownload.do";
    String FLSYS_OPEN_IN_EXPLORER_AJAX = Prefix.FLSYS + "/flsysOpenInExplorerAjax.do";
    String FLSYS_FILE_EXEC_AJAX = Prefix.FLSYS + "/flsysFileExecAjax.do";

    String FLSYS_META_REG_AJAX = Prefix.FLSYS + "/flsysMetaRegAjax.do";
    String FLSYS_META_DTL_AJAX = Prefix.FLSYS + "/flsysMetaDtlAjax.do";
    String FLSYS_META_MDF_AJAX = Prefix.FLSYS + "/flsysMetaMdfAjax.do";
    String FLSYS_META_DEL_AJAX = Prefix.FLSYS + "/flsysMetaDelAjax.do";

    /** (공통) 파일 */
    String FILE_DOWNLOAD_CHK_AJAX = Prefix.FILE + "/fileDownloadChkAjax.do";
    String FILE_INFO_LIST_AJAX = Prefix.FILE + "/fileInfoListAjax.do";
    String FILE_DOWNLOAD = Prefix.FILE + "/fileDownload.do";
    String FILE_UPLOAD_AJAX = Prefix.FILE + "/fileUploadAjax.do";

    /** (공통) 캐시 관리 */
    String CACHE_CHCK_AJAX = Prefix.CACHE + "/chckActiveCachesAjax.do";
    String CACHE_CLEAR_AJAX = Prefix.CACHE + "/clearCacheAjax.do";

    /** ERROR */
    // URL
    String ERROR = "/error";
    String ERROR_PAGE = Prefix.ERROR + "/errorPage.do";
    String ERROR_NOT_FOUND = Prefix.ERROR + "/notFound.do";
    String ERROR_ACCESS_DENIED = Prefix.ERROR + "/accessDenied.do";

    /* ---------- */

    /** 경비지출서 */
    String EXPTR_PRSNL_PAPR_LIST = Prefix.EXPTR_PRSNL_PAPR + "/exptrPrsnlPaprList.do";
    String EXPTR_PRSNL_PAPR_EXISTING_CHCK_AJAX = Prefix.EXPTR_PRSNL_PAPR + "/exptrPrsnlPaprExistingChckAjax.do";
    String EXPTR_PRSNL_PAPR_REG_FORM = Prefix.EXPTR_PRSNL_PAPR + "/exptrPrsnlPaprRegForm.do";
    String EXPTR_PRSNL_PAPR_YY_MNTH_CHCK_AJAX = Prefix.EXPTR_PRSNL_PAPR + "/exptrPrsnlPaprYyMnthChckAjax.do";
    String EXPTR_PRSNL_PAPR_REG_AJAX = Prefix.EXPTR_PRSNL_PAPR + "/exptrPrsnlPaprRegAjax.do";
    String EXPTR_PRSNL_PAPR_DTL = Prefix.EXPTR_PRSNL_PAPR + "/exptrPrsnlPaprDtl.do";
    String EXPTR_PRSNL_PAPR_PDF_POP = Prefix.EXPTR_PRSNL_PAPR + "/exptrPrsnlPaprPdfPop.do";
    String EXPTR_PRSNL_PAPR_MDF_FORM = Prefix.EXPTR_PRSNL_PAPR + "/exptrPrsnlPaprMdfForm.do";
    String EXPTR_PRSNL_PAPR_MDF_AJAX = Prefix.EXPTR_PRSNL_PAPR + "/exptrPrsnlPaprMdfAjax.do";
    String EXPTR_PRSNL_PAPR_DEL_AJAX = Prefix.EXPTR_PRSNL_PAPR + "/exptrPrsnlPaprDelAjax.do";
    String EXPTR_PRSNL_PAPR_RCIPT_PDF_DOWNLOAD = Prefix.EXPTR_PRSNL_PAPR + "/exptrPrsnlPaprRciptPdfDownload.do";

    String EXPTR_PRSNL_ITEM_LIST_AJAX = Prefix.EXPTR_PRSNL_ITEM + "/exptrPrsnlItemListAjax.do";
    String EXPTR_PRSNL_ITEM_RCIPT_UPLOAD_AJAX = Prefix.EXPTR_PRSNL_ITEM + "/exptrPrsnlItemRciptUploadAjax.do";
    String EXPTR_PRSNL_ITEM_ORGNL_RCIPT_AJAX = Prefix.EXPTR_PRSNL_ITEM + "/exptrPrsnlItemOrgnlRciptAjax.do";
    String EXPTR_PRSNL_ITEM_RJECT_AJAX = Prefix.EXPTR_PRSNL_ITEM + "/exptrPrsnlItemRejectAjax.do";

    String EXPTR_PRSNL_RPT_ITEMS = Prefix.EXPTR_PRSNL_RPT + "/exptrPrsnlRptItems.do";
    String EXPTR_PRSNL_RPT_XLSX_DOWNLOAD = Prefix.EXPTR_PRSNL_RPT + "/exptrPrsnlRptXlsxDownload.do";

    String EXPTR_PRSNL_STATS_PAGE = Prefix.EXPTR_PRSNL_STATS + "/exptrPrsnlStatsPage.do";
    String EXPTR_PRSNL_STATS_DTL = Prefix.EXPTR_PRSNL_STATS + "/exptrPrsnlStatsDtl.do";
    String EXPTR_PRSNL_STATS_CF_AJAX = Prefix.EXPTR_PRSNL_STATS + "/exptrPrsnlStatsCfAjax.do";
    String EXPTR_PRSNL_STATS_XLSX_DOWNLOAD = Prefix.EXPTR_PRSNL_STATS + "/exptrPrsnlStatsXlsxDownload.do";

    String EXPTR_REQST_LIST = Prefix.EXPTR_REQST + "/exptrReqstList.do";
    String EXPTR_REQST_REG_FORM = Prefix.EXPTR_REQST + "/exptrReqstRegForm.do";
    String EXPTR_REQST_REG_PREVIEW_POP = Prefix.EXPTR_REQST + "/exptrReqstRegPreviewPop.do";
    String EXPTR_REQST_REG_AJAX = Prefix.EXPTR_REQST + "/exptrReqstRegAjax.do";
    String EXPTR_REQST_DTL = Prefix.EXPTR_REQST + "/exptrReqstDtl.do";
    String EXPTR_REQST_DTL_AJAX = Prefix.EXPTR_REQST + "/exptrReqstDtlAjax.do";
    String EXPTR_REQST_MDF_FORM = Prefix.EXPTR_REQST + "/exptrReqstMdfForm.do";
    String EXPTR_REQST_MDF_AJAX = Prefix.EXPTR_REQST + "/exptrReqstMdfAjax.do";
    String EXPTR_REQST_DEL_AJAX = Prefix.EXPTR_REQST + "/exptrReqstDelAjax.do";
    String EXPTR_REQST_CF_AJAX = Prefix.EXPTR_REQST + "/exptrReqstCfAjax.do";
    String EXPTR_REQST_DISMISS_AJAX = Prefix.EXPTR_REQST + "/exptrReqstDismissAjax.do";

    String SCHDUL_CAL = Prefix.SCHDUL + "/schdulCal.do";
    String SCHDUL_CAL_LIST_AJAX = Prefix.SCHDUL + "/schdulCalListAjax.do";
    String SCHDUL_REG_AJAX = Prefix.SCHDUL + "/schdulCalRegAjax.do";
    String SCHDUL_DTL_AJAX = Prefix.SCHDUL + "/schdulCalDtlAjax.do";
    String SCHDUL_MDF_AJAX = Prefix.SCHDUL + "/schdulCalMdfAjax.do";
    String SCHDUL_DEL_AJAX = Prefix.SCHDUL + "/schdulCalDelAjax.do";

    String VCATN_PAPR_LIST = Prefix.VCATN_PAPR + "/vcatnPaprList.do";
    String VCATN_PAPR_REG_FORM = Prefix.VCATN_PAPR + "/vcatnPaprRegForm.do";
    String VCATN_PAPR_REG_AJAX = Prefix.VCATN_PAPR + "/vcatnPaprRegAjax.do";
    String VCATN_PAPR_DTL = Prefix.VCATN_PAPR + "/vcatnPaprDtl.do";
    String VCATN_PAPR_DTL_AJAX = Prefix.VCATN_PAPR + "/vcatnPaprDtlAjax.do";
    String VCATN_PAPR_MDF_FORM = Prefix.VCATN_PAPR + "/vcatnPaprMdfForm.do";
    String VCATN_PAPR_MDF_AJAX = Prefix.VCATN_PAPR + "/vcatnPaprMdfAjax.do";
    String VCATN_PAPR_CF_AJAX = Prefix.VCATN_PAPR + "/vcatnPaprCfAjax.do";
    String VCATN_PAPR_DEL_AJAX = Prefix.VCATN_PAPR + "/vcatnPaprDelAjax.do";

    String VCATN_DY_LIST = "/vcatnDyList.do";
    String VCATN_DY_REG_AJAX = "/vcatnDyRegAjax.do";
    String VCATN_DY_DTL_AJAX = "/vcatnDyDtlAjax.do";
    String VCATN_DY_MDF_AJAX = "/vcatnDyMdfAjax.do";
    String VCATN_DY_DEL_AJAX = "/vcatnDyDelAjax.do";
    String VCATN_DY_XLSX_DOWNLOAD = "/vcatnDyXlsxDownload.do";

    String VCATN_STATS_YY = "/vcatnStatsYy.do";
    String VCATN_STATS_YY_UPDT_AJAX = "/vcatnStatsYyUpdtAjax.do";
    String VCATN_STATS_YY_XLSX_DOWNLOAD = "/vcatnStatsXlsxDownload.do";

    String ADMIN_PAGE = "/admin";
    String ADMIN_TEST = "/admin/test";
    String NOTION_HOME = "/notionHome.do";

    String USER_REQST_REG_FORM = Prefix.USER_REQST + "/userReqstRegForm.do";
    String USER_REQST_REG_AJAX = Prefix.USER_REQST + "/userReqstRegAjax.do";
    String USER_REQST_CF_AJAX = Prefix.USER_REQST + "/userReqstCfAjax.do";
    String USER_REQST_UNCF_AJAX = Prefix.USER_REQST + "/userReqstUncfAjax.do";

    /** 프로젝트 */
    String PRJCT_INFO_LIST = Prefix.PRJCT + "/prjctInfoList.do";
    String PRJCT_REG_FORM = Prefix.PRJCT + "/prjctRegForm.do";

    /**
     * 서브메뉴 정보
     */
    interface Prefix {
        String AUTH = "/auth";
        String ADMIN = "/admin";

        String NOTICE = "/notice";

        String DREAM = "/dream";
        String DAY = "/day";
        String DREAM_DAY = DREAM + DAY;
        String PIECE = "/piece";
        String DREAM_PIECE = DREAM + PIECE;

        /* 일반게시판 (board) */
        String BOARD = "/board";
        String POST = "/board";
        String BOARD_POST = BOARD + POST;

        /* 일정 (schdul) */
        String SCHDUL = "/schdul";

        /* 휴가 (vcatn) */
        String VCATN = "/vcatn";
        String PAPR = "/papr";
        String VCATN_PAPR = VCATN + PAPR;

        /* 경비 (exptr) */
        String EXPTR = "/exptr";
        String PRSNL = "/prsnl";
        String EXPTR_PRSNL = EXPTR + PRSNL;
        String EXPTR_PRSNL_PAPR = EXPTR_PRSNL + PAPR;
        String ITEM = "/item";
        String EXPTR_PRSNL_ITEM = EXPTR_PRSNL + ITEM;
        String RPT = "/rpt";
        String EXPTR_PRSNL_RPT = EXPTR_PRSNL + RPT;
        String STATS = "/stats";
        String EXPTR_PRSNL_STATS = EXPTR_PRSNL + STATS;
        String REQST = "/reqst";
        String EXPTR_REQST = EXPTR + REQST;

        /* 프로젝트 (prjct) */
        String PRJCT = "/project";

        String COMMENT = "/comment";
        String TAG = "/tag";

        String LGN_POLICY = "/lgnPolicy";

        String MENU = "/menu";
        String BOARD_DEF = "/boardDef";
        String TMPLAT = "/templat";
        String CD = "/cd";

        String LOG = "/log";
        String ACTVTY = "/actvty";
        String LOG_ACTVTY = LOG + ACTVTY;
        String SYS = "/sys";
        String LOG_SYS = LOG + SYS;
        String LOG_STATS = LOG + STATS;

        String USER = "/user";
        String MY = "/my";
        String USER_MY = USER + MY;
        String USER_REQST = USER + REQST;

        String FILE = "/file";
        String FLSYS = "/flsys";

        String ERROR = "/error";
        String CACHE = "/cache";
    }
}