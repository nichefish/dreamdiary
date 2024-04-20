package io.nicheblog.dreamdiary.global;

import io.nicheblog.dreamdiary.api.ApiConstant;
import io.nicheblog.dreamdiary.global.auth.AuthConstant;
import lombok.AllArgsConstructor;

import java.util.Locale;
import java.util.TimeZone;

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
        implements AuthConstant, ApiConstant {

    /**
     * TimeZone
     */
    public static final String LOC_SEOUL = "Asia/Seoul";
    public static final TimeZone TZ_SEOUL = TimeZone.getTimeZone(Constant.LOC_SEOUL);
    public static final Locale LC_KO = Locale.KOREA;

    public static final String CHARSET_UTF_8 = "UTF-8";

    public static final String SITE_NM = "dreamdiary";
    public static final String SITE_DESC = "'tis my site!";
    public static final String SITE_KEYWORDS = "dream, diary, journal";

    public static final String PAGE_LGN = "로그인";
    public static final String PAGE_MAIN = "메인";
    public static final String PAGE_LIST = "목록 조회";
    public static final String PAGE_REG = "등록";
    public static final String PAGE_DTL = "상세 조회";
    public static final String PAGE_MDF = "수정";
    public static final String PAGE_POP = "팝업";
    public static final String PAGE_STATS = "통계 조회";
    public static final String PAGE_CAL = "달력 조회";

    /** blank avatar image url */
    public static final String BLANK_AVATAR_URL = "/metronic/assets/media/avatars/avatar_blank.png";

    public static final String PREFIX = "PREFIX";
    public static final String SUFFIX = "SUFFIX";
    public static final String SITE_MENU = "siteAcsInfo";

    /** URL_ENCODING */
    public static final Boolean URL_ENC_FALSE = false;

    /** 디바이스 정보 */
    @AllArgsConstructor
    public enum Device {
        PC,
        MOBILE,
        TABLET;
    }
    public static final String DVC_PC = Device.PC.name();
    public static final String DVC_MOBILE = Device.MOBILE.name();
    public static final String DVC_TABLET = Device.TABLET.name();
    public static final String IS_MBL = "IS_MBL";

    /** IP 헤더 목록 */
    public static final String[] IP_HEADERS = { "X-FORWARDED-FOR", "Proxy-Client-IP",  "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR" };
    public static final String REMOTE_ADDR = "remoteAddr";
    public static final String REFERER = "referer";

    /** 쿠키 */
    public static final String KT_SIDEBAR_MINIMIZE = "data-kt-app-sidebar-minimize";
    public static final String KT_SIDEBAR_MINIMIZE_STATE = "sidebar_minimize_state";
    public static final String KT_ASIDE_MINIMIZE = "data-kt-aside-minimize";

    /** Page Static Strings */
    public static final String ERROR_MSG = "errorMsg";
    public static final String MESSAGE = "message";
    public static final String PAGINATION_INFO = "paginationInfo";
    public static final String IS_REG = "isReg";
    public static final String IS_MDF = "isMdf";
    public static final String LIST_URL = "listUrl";

    /* 소속(팀) 코드 */
    public static final String TEAM_CD = "TEAM_CD";

    /** 재직 구분 코드 */
    @AllArgsConstructor
    public enum Emplym {
        EMPLYM("재직"),
        FREE("프리랜서");

        public final String desc;
    }
    public static final String EMPLYM_CD = "EMPLYM_CD";
    public static final String EMPLYM_FREE = Emplym.FREE.name();

    @AllArgsConstructor
    public enum Rank {
        INTN("인턴"),
        STAFF("사원"),
        DAERI("대리"),
        GWJANG("과장"),
        CHJANG("차장"),
        BJANG("부장"),
        SLJANG("실장"),
        DRCTR("이사"),
        PRSDNT("사장");

        public final String desc;
    }
    public static final String RANK_CD = "JOB_TITLE_CD";       // 직급 코드
    public static final String RANK_STAFF = Rank.STAFF.name();           // 직급:사원

    public static final String NOTICE_CTGR_CD = "NOTICE_CTGR_CD";   // 공지사항 글분류 코드
    public static final String POST_CTGR_CD = "POST_CTGR_CD";       // 게시판
    public static final String EXPTR_REQST_CTGR_CD = "EXPTR_REQST_CTGR_CD";   // 물품구매 및 경조사비 신청 글분류 코드
    public static final String JANDI_TOPIC_CD = "JANDI_TOPIC_CD";   // 잔디 토픽 코드

    public static final String POST_CD = "POST_CD";                 // 게시판 코드 (예약어)

    public static final String YY_CD = "YY_CD";                 // 사용자 권한 코드
    public static final String MNTH_CD = "MNTH_CD";                 // 사용자 권한 코드

    public static final String EXPTR_CD = "EXPTR_CD";         // 지출구분 코드

    public static final String ACTVTY_CTGR_CD = "ACTVTY_CTGR_CD";     // 작업 카테고리 코드
    public static final String ACTION_TY_CD = "ACTION_TY_CD";         // 액션 유형 코드

    /* 메뉴 분류 코드 */
    public static final String MENU_TY_CD = "MENU_TY_CD";
    public static final String MENU_TY_MAIN = "MAIN";
    public static final String MENU_TY_SUB = "SUB";


    /** 일정 분류 코드 */
    @AllArgsConstructor
    public enum Schdul {
        HLDY("공휴일"),
        CEREMONY("행사"),
        TLCMMT("재택근무"),
        OUTDT("외근"),
        INDT("내부일정"),
        VCATN("휴가"),
        BRTHDY("생일"),
        ETC("기타");

        public final String desc;
    }
    public static final String SCHDUL_CD = "SCHDUL_CD";       // 일정 구분 코드
    public static final String SCHDUL_HLDY = Schdul.HLDY.name();
    public static final String SCHDUL_CEREMONY = Schdul.CEREMONY.name();
    public static final String SCHDUL_TLCMMT = Schdul.TLCMMT.name();
    public static final String SCHDUL_OUTDT = Schdul.OUTDT.name();
    public static final String SCHDUL_INDT = Schdul.INDT.name();
    public static final String SCHDUL_VCATN = Schdul.VCATN.name();
    public static final String SCHDUL_BRTHDY = Schdul.BRTHDY.name();
    public static final String SCHDUL_ETC = Schdul.ETC.name();


    /** 휴가 분류 코드 */
    @AllArgsConstructor
    public enum Vcatn {
        ANNUAL("연차"),
        AM_HALF("오전반차"),
        PM_HALF("오후반차"),
        PBLEN("공가"),
        CTSNN("경조휴가"),
        UNPAID("무급휴가"),
        MNSTR("생리휴가");

        public final String desc;
    }
    public static final String VCATN_CD = "VCATN_CD";
    public static final String VCATN_ANNUAL = Vcatn.ANNUAL.name();
    public static final String VCATN_AM_HALF = Vcatn.AM_HALF.name();
    public static final String VCATN_PM_HALF = Vcatn.PM_HALF.name();
    public static final String VCATN_PBLEN = Vcatn.PBLEN.name();
    public static final String VCATN_CTSNN = Vcatn.CTSNN.name();
    public static final String VCATN_UNPAID = Vcatn.UNPAID.name();
    public static final String VCATN_MNSTR = Vcatn.MNSTR.name();

    /** 활동 구분 코드 (로그) */
    public static final String ACTION_TY_SEARCH = "SEARCH";
    public static final String ACTION_TY_MY_PAPR = "MY_PAPR";
    public static final String ACTION_TY_VIEW = "VIEW";
    public static final String ACTION_TY_SUBMIT = "SUBMIT";
    public static final String ACTION_TY_DOWNLOAD = "DOWNLOAD";

    /** 각 메뉴 번호 */
    public static final String USER = "user";              // 계정 관리
    public static final String LGN_POLICY = "lgnPolicy";       // 로그인 관리
    public static final String CRDT_USER_CTTPC = "crdtUserCttpc";   // 직원 연락처
    public static final String LOG_ACTVTY = "logActvty";            // 활동 로그 관리
    public static final String LOG_SYS = "logSys";                  // 활동 로그 관리
    public static final String EXPTR_PRSNL_PAPR = "exptrPrsnl";          // 개인경비지출서
    public static final String EXPTR_PRSNL_RPT = "exptrPrsnlRpt";
    public static final String EXPTR_PRSNL_RPT_ITEM = "exptrPrsnlRptItem";
    public static final String VCATN_STATS = "vcatnStats";          // 휴가계획서
    public static final String VCATN_SCHDUL = "vcatnSchdul";                // 휴가사용일자

    public static final String HOME_FLSYS = "flsys";

    /** UTM 파라미터 */
    @AllArgsConstructor
    public enum UtmParam {

        UTM_SOURCE("utm_source", "등록자"),
        UTM_MEDIUM("utm_medium", "관리자"),
        UTM_CAMPAIGN("utm_campaign", "사용자"),
        UTM_TERM("utm_term", "사용자"),
        UTM_CONTENT( "utm_content", "전체");

        public final String key;
        public final String desc;
    }
    public static final String UTM_SOURCE = UtmParam.UTM_SOURCE.key;
    public static final String UTM_MEDIUM = UtmParam.UTM_MEDIUM.key;
    public static final String UTM_CAMPAIGN = UtmParam.UTM_CAMPAIGN.key;
    public static final String UTM_TERM = UtmParam.UTM_TERM.key;
    public static final String UTM_CONTENT = UtmParam.UTM_CONTENT.key;

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

    /* 코드 정보 */

    /* 소속(회사) 코드 */
    public static final String CMPY_CD = "CMPY_CD";

    @AllArgsConstructor
    public enum CMPY {
        AA("aa"),
        BB("bb");

        public final String desc;
    }
}
