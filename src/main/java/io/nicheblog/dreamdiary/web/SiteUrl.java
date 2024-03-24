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

    /**
     * 메인 도메인
     */
    String DOMAIN = "dreamdiary.nicheblog.io";

    String ROBOT_TXT = "/robot.txt";
    String ROBOTS_TXT = "/robots.txt";

    String ROOT = "/";
    String MAIN = "/main.do";

    String PREFIX_INFO = "/info";

    /**
     * 메인 > 로그인 화면
     * NO_ASIDE
     */
    // URL
    String PREFIX_AUTH = "/auth";
    String AUTH_LGN_FORM = PREFIX_AUTH + "/lgnForm.do";
    String AUTH_LGN_PROC = PREFIX_AUTH + "/lgnProc.do";
    String AUTH_LGN_PW_CHG_AJAX = PREFIX_AUTH + "/lgnPwChgAjax.do";
    String AUTH_LGOUT = PREFIX_AUTH + "/lgout.do";

    String PREFIX_ADMIN = "/admin";
    String ADMIN_MAIN = PREFIX_ADMIN + MAIN;
    String ADMIN_TEST_PAGE = PREFIX_ADMIN + "/testPage.do";

    /** 꿈 관리 */
    String PREFIX_DREAM = "/dream";
    String PREFIX_DAY = "/day";
    String PREFIX_DREAM_DAY = PREFIX_DREAM + PREFIX_DAY;
    String DREAM_DAY_LIST = PREFIX_DREAM_DAY + "/dreamDayList.do";
    String DREAM_DAY_REG_AJAX = PREFIX_DREAM_DAY + "/dreamDayRegAjax.do";
    String DREAM_DAY_DTL_AJAX = PREFIX_DREAM_DAY + "/dreamDayDtlAjax.do";
    String DREAM_DAY_MDF_AJAX = PREFIX_DREAM_DAY + "/dreamDayMdfAjax.do";


    String PREFIX_PIECE = "/piece";
    String PREFIX_DREAM_PIECE = PREFIX_DREAM + PREFIX_PIECE;

    /**
     * 사용자 관리
     */
    String PREFIX_USER = "/user";
    String USER_LIST = PREFIX_USER + "/userList.do";
    String USER_REG_FORM = PREFIX_USER + "/userRegForm.do";
    String USER_REG_AJAX = PREFIX_USER + "/userRegAjax.do";
    String USER_DTL = PREFIX_USER + "/userDtl.do";
    String USER_MDF_FORM = PREFIX_USER + "/userMdfForm.do";
    String USER_MDF_AJAX = PREFIX_USER + "/userMdfAjax.do";
    String USER_PW_RESET_AJAX = PREFIX_USER + "/userPwResetAjax.do";

    String USER_DEL_AJAX = PREFIX_USER + "/userDelAjax.do";


    String USER_ID_DUP_CHK_AJAX = PREFIX_USER + "/userIdDupChkAjax.do";

    /**
     * 내 정보 관리
     */
    String PREFIX_MY = "/my";
    String PREFIX_USER_MY = PREFIX_USER + PREFIX_MY;
    String USER_MY_DTL = PREFIX_USER_MY + "/userMyDtl.do";

    String USER_MY_UPLOAD_PROFL_IMG_AJAX = PREFIX_USER_MY + "/userMyUploadProflImgAjax.do";
    String USER_MY_REMOVE_PROFL_IMG_AJAX = PREFIX_USER_MY + "/userMyRemoveProflImgAjax.do";
    String USER_MY_PW_CF_AJAX = PREFIX_USER_MY + "/userMyPwCfAjax.do";
    String USER_MY_PW_CHG_AJAX = PREFIX_USER_MY + "/userMyPwChgAjax.do";

    /** 댓글 */
    String PREFIX_COMMENT = "/comment";
    String COMMENT_LIST_AJAX = PREFIX_COMMENT + "/commentListAjax.do";
    String COMMENT_REG_AJAX = PREFIX_COMMENT + "/commentRegAjax.do";
    String COMMENT_MDF_AJAX = PREFIX_COMMENT + "/commentMdfAjax.do";
    String COMMENT_DEL_AJAX = PREFIX_COMMENT + "/commentDelAjax.do";

    /**
     * 로그인 정책 관리
     */
    String PREFIX_LGN_POLICY = "/lgnPolicy";
    String LGN_POLICY_FORM = PREFIX_LGN_POLICY + "/lgnPolicyForm.do";
    String LGN_POLICY_REG_AJAX = PREFIX_LGN_POLICY + "/lgnPolicyRegAjax.do";

    /** 코드 관리 */
    String PREFIX_CD = "/cd";
    String CL_CD_LIST = "clCdList.do";
    String CL_CD_DTL = "clCdDtl.do";
    String CL_CD_REG_AJAX = "clCdRegAjax.do";
    String CL_CD_DTL_AJAX = "clCdDtlAjax.do";
    String CL_CD_MDF_AJAX = "clCdMdfAjax.do";
    String CL_CD_DEL_AJAX = "clCdDelAjax.do";
    String CL_CD_USE_AJAX = "clCdUseAjax.do";
    String CL_CD_UNUSE_AJAX = "clCdUnuseAjax.do";

    String DTL_CD_REG_AJAX = "dtlCdRegAjax.do";
    String DTL_CD_DTL_AJAX = "dtlCdDtlAjax.do";
    String DTL_CD_MDF_AJAX = "dtlCdMdfAjax.do";
    String DTL_CD_LIST_AJAX = "dtlCdListAjax.do";
    String DTL_CD_USE_AJAX = "dtlCdUseAjax.do";
    String DTL_CD_UNUSE_AJAX = "dtlCdUnuseAjax.do";
    String DTL_CD_DEL_AJAX = "dtlCdDelAjax.do";

    /**
     * (공통) 파일
     */
    String PREFIX_FILE = "/file";
    String FILE_DOWNLOAD_CHK_AJAX = PREFIX_FILE + "/fileDownloadChkAjax.do";
    String FILE_INFO_LIST_AJAX = PREFIX_FILE + "/fileInfoListAjax.do";
    String FILE_DOWNLOAD = PREFIX_FILE + "/fileDownload.do";
    String FILE_UPLOAD_AJAX = PREFIX_FILE + "/fileUploadAjax.do";

    /**
     * (공통) 캐시 관리
     */
    String PREFIX_CACHE = "/cache";
    String CACHE_CHCK_AJAX = PREFIX_CACHE + "/chckActiveCachesAjax.do";
    String CACHE_CLEAR_AJAX = PREFIX_CACHE + "/clearCacheAjax.do";

    /**
     * ERROR
     */
    // URL
    String PREFIX_ERROR = "/error";
    String ERROR = "/error";
    String ERROR_PAGE = PREFIX_ERROR + "/errorPage.do";
    String ERROR_NOT_FOUND = PREFIX_ERROR + "/notFound.do";
    String ERROR_ACCESS_DENIED = PREFIX_ERROR + "/accessDenied.do";

    /* ---------- */

    String USER_REQST_REG_FORM = "";

    String PREFIX_NOTICE = "/notice";
    String NOTICE_LIST = PREFIX_NOTICE + "/noticeList.do";

    String NOTICE_REG_FORM = PREFIX_NOTICE + "/noticeRegForm.do";
    String NOTICE_REG_AJAX = PREFIX_NOTICE + "/noticeRegAjax.do";
    String NOTICE_DTL = PREFIX_NOTICE + "/noticeDtl.do";
    String NOTICE_DTL_AJAX = PREFIX_NOTICE + "/noticeDtlAjax.do";
    String NOTICE_MDF_FORM = PREFIX_NOTICE + "/noticeMdfForm.do";
    String NOTICE_MDF_AJAX = PREFIX_NOTICE + "/noticeMdfAjax.do";

    String NOTICE_DEL_AJAX = PREFIX_NOTICE + "/noticeDelAjax.do";
    String NOTICE_REG_PREVIEW_POP = PREFIX_NOTICE + "/noticeRegPreviewPop.do";

    String NOTICE_POPUP_LIST_AJAX = PREFIX_NOTICE + "/noticePopupListAjax.do";








    String NOTION_HOME = "";
    String FLSYS_HOME = "";
}