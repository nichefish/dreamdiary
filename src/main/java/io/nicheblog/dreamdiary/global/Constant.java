package io.nicheblog.dreamdiary.global;

import io.nicheblog.dreamdiary.api.ApiConstant;
import io.nicheblog.dreamdiary.global.cmm.auth.Auth;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Constant
 * <pre>
 *  공통으로 사용하는 코드성 데이터 정의
 * </pre>
 * TODO: enum으로 점진적 변환쓰
 * TODO: Freemarker 단에서 enum을 어떻게 처리할 것인지?
 *
 * @author nichefish
 */
public final class Constant
        implements ApiConstant {

    /**
     * TimeZone
     */
    public static final String LOC_SEOUL = "Asia/Seoul";

    /**
     * RememberMeParam
     */
    public static final String REMEMBER_ME_PARAM = "remember-me";

    @AllArgsConstructor
    @Getter
    public enum LogTy {

        ACTVTY("활동 로그"),
        SYS("시스템 로그");

        private final String desc;
    }
    public static final String LOG_TY_ACTVTY = LogTy.ACTVTY.name();
    public static final String LOG_TY_SYS = LogTy.SYS.name();

    /**
     * 권한
     */
    public static final String SYSTEM_ACNT = "system";
    public static final String SYSTEM_ACNT_NM = "시스템관리자";
    public static final String DEV_ACNT = "nichefish";

    /**
     * 사용자 권한 코드
     */
    public static final String AUTH_CD = "AUTH_CD";

    public static final String AUTH_MNGR = Auth.MNGR.name();
    public static final String AUTH_USER = Auth.USER.name();
    public static final String AUTH_DEV = Auth.DEV.name();

    public static final String ROLE_MNGR = "ROLE_MNGR";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_DEV = "ROLE_DEV";

    public static final String SITE_MENU = "siteAcsInfo";

    /** URL_ENCODING */
    public static final Boolean URL_ENC_FALSE = false;

    /** 디바이스 정보 */
    public static String DVC_PC = "PC";
    public static String DVC_MOBILE = "MOBILE";
    public static String DVC_TABLET = "TABLET";

    /** IP 헤더 목록 */
    public static final String[] IP_HEADERS = { "X-FORWARDED-FOR", "Proxy-Client-IP",  "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR" };
    public static final String REMOTE_ADDR = "remoteAddr";
    public static final String REFERER = "referer";

    /**
     * 쿠키
     */
    public static final String ASIDE_MENU = "aside_menu";
    public static final String ASIDE_MENU_FIXED = "aside_fixed";
    public static final String ASIDE_MENU_MINIMIZED = "aside_minimized";








    /**
     * Page Static Strings
     */
    public static final String ERROR_MSG = "errorMsg";
    public static final String MESSAGE = "message";
    public static final String PAGINATION_INFO = "paginationInfo";
    public static final String IS_REG = "isReg";
    public static final String IS_MDF = "isMdf";
    public static final String LIST_URL = "listUrl";

    /* 코드 정보 */

    /* 소속(회사) 코드 */
    public static final String CMPY_CD = "CMPY_CD";

    @AllArgsConstructor
    @Getter
    public enum CMPY {
        SINZI("신지넷"),
        BIX("빅스소프트");
        private final String desc;
    }

    public static final String CMPY_SINZI = CMPY.SINZI.name();                // 회사:신지넷
    public static final String CMPY_BIX = CMPY.BIX.name();                    // 회사:빅스소프트

    /* 소속(팀) 코드 */
    public static final String TEAM_CD = "TEAM_CD";

    /* 재직구분 코드 */
    public static final String EMPLYM_CD = "EMPLYM_CD";

    @AllArgsConstructor
    @Getter
    public enum EMPLYM {
        FREE("프리랜서");
        private final String desc;
    }

    public static final String EMPLYM_FREE = EMPLYM.FREE.name();


    public static final String JOB_TITLE_CD = "JOB_TITLE_CD";       // 직급 코드
    public static final String JOB_TITLE_STAFF = "STAFF";           // 직급:사원

    public static final String NOTICE_CTGR_CD = "NOTICE_CTGR_CD";   // 공지사항 글분류 코드
    public static final String POST_CTGR_CD = "POST_CTGR_CD";       // 게시판
    public static final String EXPTR_REQST_CTGR_CD = "EXPTR_REQST_CTGR_CD";   // 물품구매 및 경조사비 신청 글분류 코드
    public static final String JANDI_TOPIC_CD = "JANDI_TOPIC_CD";   // 잔디 토픽 코드

    public static final String POST_CD = "POST_CD";                 // 게시판 코드 (예약어)

    public static final String YY_CD = "YY_CD";                 // 사용자 권한 코드
    public static final String MNTH_CD = "MNTH_CD";                 // 사용자 권한 코드

    public static final String EXPTR_TY_CD = "EXPTR_TY_CD";         // 지출구분 코드

    public static final String ACTVTY_CTGR_CD = "ACTVTY_CTGR_CD";     // 작업 카테고리 코드
    public static final String ACTION_TY_CD = "ACTION_TY_CD";         // 액션 유형 코드

    /* 일정 분류 코드 */
    public static final String SCHDUL_TY_CD = "SCHDUL_TY_CD";       // 일정 구분 코드

    @AllArgsConstructor
    @Getter
    public enum SCHDUL_TY {
        HLDY("공휴일"),
        CEREMONY("행사"),
        TLCMMT("재택근무"),
        OUTDT("외근"),
        INDT("내부일정"),
        VCATN("휴가"),
        BRTHDY("생일"),
        ETC("기타");
        private final String desc;
    }

    public static final String SCHDUL_TY_HLDY = SCHDUL_TY.HLDY.name();
    public static final String SCHDUL_TY_CEREMONY = SCHDUL_TY.CEREMONY.name();
    public static final String SCHDUL_TY_TLCMMT = SCHDUL_TY.TLCMMT.name();
    public static final String SCHDUL_TY_OUTDT = SCHDUL_TY.OUTDT.name();
    public static final String SCHDUL_TY_INDT = SCHDUL_TY.INDT.name();
    public static final String SCHDUL_TY_VCATN = SCHDUL_TY.VCATN.name();
    public static final String SCHDUL_TY_BRTHDY = SCHDUL_TY.BRTHDY.name();
    public static final String SCHDUL_TY_ETC = SCHDUL_TY.ETC.name();

    /* 휴가 분류 코드 */
    public static final String VCATN_CD = "VCATN_CD";

    @AllArgsConstructor
    @Getter
    public enum VCATN {
        ANNUAL("연차"),
        AM_HALF("오전반차"),
        PM_HALF("오후반차"),
        PBLEN("공가"),
        CTSNN("경조휴가"),
        UNPAID("무급휴가"),
        MNSTR("생리휴가");
        private final String desc;
    }

    public static final String VCATN_ANNUAL = VCATN.ANNUAL.name();
    public static final String VCATN_AM_HALF = VCATN.AM_HALF.name();
    public static final String VCATN_PM_HALF = VCATN.PM_HALF.name();
    public static final String VCATN_PBLEN = VCATN.PBLEN.name();
    public static final String VCATN_CTSNN = VCATN.CTSNN.name();
    public static final String VCATN_UNPAID = VCATN.UNPAID.name();
    public static final String VCATN_MNSTR = VCATN.MNSTR.name();

    /**
     * 활동 구분 코드 (로그)
     */
    public static final String ACTION_TY_SEARCH = "SEARCH";
    public static final String ACTION_TY_MY_PAPR = "MY_PAPR";
    public static final String ACTION_TY_VIEW = "VIEW";
    public static final String ACTION_TY_SUBMIT = "SUBMIT";
    public static final String ACTION_TY_DOWNLOAD = "DOWNLOAD";

    /**
     * 각 메뉴 번호
     */
    public static final String USER_INFO = "userInfo";              // 계정 관리
    public static final String LGN_POLICY = "lgnPolicy";       // 로그인 관리
    public static final String CRDT_USER_CTTPC = "crdtUserCttpc";   // 직원 연락처
    public static final String LOG_ACTVTY = "logActvty";            // 활동 로그 관리
    public static final String LOG_SYS = "logSys";                  // 활동 로그 관리
    public static final String EXPTR_INDVD_PAPR = "exptrIndvd";          // 개인경비지출서
    public static final String EXPTR_INDVD_RPT = "exptrIndvdRpt";
    public static final String EXPTR_INDVD_RPT_ITEM = "exptrIndvdRptItem";
    public static final String VCATN_STATS = "vcatnStats";          // 휴가계획서
    public static final String VCATN_DY = "vcatnDy";                // 휴가사용일자

    public static final String HOME_WORKSPACE = "workspace";
    public static final String HOME_WORKSPACE_PROJECT = "workspace/프로젝트용";
    public static final String HOME_STORAGE = "storage";
    public static final String HOME_VOD_STORAGE = "vod-storage";

    public static final String FILE_SVR_IP_ADDR = "192.168.10.21";

    /* 수정 권한 코드 */
    public static final String MDFABLE_CD = "MDFABLE_CD";

    @AllArgsConstructor
    @Getter
    public enum MDFABLE {
        REGSTR("등록자"),
        MNGR("관리자"),
        USER("사용자"),
        ALL("전체");
        private final String desc;
    }

    public static final String MDFABLE_REGSTR = MDFABLE.REGSTR.name();
    public static final String MDFABLE_MNGR = MDFABLE.MNGR.name();
    public static final String MDFABLE_USER = MDFABLE.USER.name();
    public static final String MDFABLE_ALL = MDFABLE.ALL.name();

    /**
     * PREFIX / SUFFIX
     */
    public static final String PREFIX = "PREFIX";
    public static final String SUFFIX = "SUFFIX";

    /**
     * UTM 파라미터
     */
    public static final String UTM_SOURCE = "utm_source";
    public static final String UTM_MEDIUM = "utm_medium";
    public static final String UTM_CAMPAIGN = "utm_campaign";
    public static final String UTM_TERM = "utm_term";
    public static final String UTM_CONTENT = "utm_content";

    public static final String BS_PRIMARY = "primary";
    public static final String BS_PRIMARY_700 = "primary-700";
    public static final String BS_SECONDARY = "secondary";
    public static final String BS_SUCCESS = "success";
    public static final String BS_INFO = "info";
    public static final String BS_INFO_700 = "info-700";
    public static final String BS_DANGER = "danger";
    public static final String BS_WARNING = "warning";
    public static final String BS_DARK = "dark";
    public static final String BS_MUTED = "muted";
}
