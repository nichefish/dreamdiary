package io.nicheblog.dreamdiary.global.cmm.log;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ActvtyCtgr
 * <pre>
 *  공통으로 사용하는 코드성 데이터 정의
 * </pre>
 * TODO: enum으로 점진적 변환쓰
 * TODO: Freemarker 단에서 enum을 어떻게 처리할 것인지?
 *
 * @author nichefish
 */
@AllArgsConstructor
@Getter
public enum ActvtyCtgr {

    // API
    API_JANDI("API_잔디"),
    API_KASI("API_특일"),
    API_DREAM("API_꿈 관리"),
    JANDI("잔디"),

    // ADMIN
    LGN("로그인"),
    ADMIN("관리"),
    CACHE("캐시"),

    NOTICE("공지사항"),
    DREAM("꿈 관리"),
    BOARD_POST("게시판"),

    TAG("태그"),

    USER("사용자 관리"),
    USER_MY("내 정보 관리"),

    // MANAGE
    LGN_POLICY("로그인 정책 관리"),
    MENU("메뉴 관리"),
    BOARD_DEF("게시판 관리"),
    TMPLAT("템플릿 관리"),
    CD("코드 관리"),
    // LOG
    LOG_ACTVTY("활동 로그"),
    LOG_SYS("시스템 로그"),
    LOG_STATS("로그 통계"),
    // CMM
    FILE("파일"),
    FLSYS("파일 시스템"),

    // DEPRECATED


    CODEMIRROR("코드미러"),
    SCHDUL("일정 관리"),
    NOTION("노션"),
    EXPTR_INDVD_PAPR("경비지출서");

    public final String desc;
}
