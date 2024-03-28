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

    ADMIN("관리"),
    CACHE("캐시"),

    API_JANDI("API_잔디"),
    API_KASI("API_특일"),
    API_DREAM("API_꿈 관리"),

    LGN("로그인"),

    DREAM("꿈 관리"),

    BOARD_POST("게시판"),

    TAG("태그"),

    NOTICE("공지사항"),

    USER("사용자 관리"),
    USER_MY("내 정보 관리"),

    LGN_POLICY("로그인 정책 관리"),
    MENU("메뉴 관리"),
    BOARD_DEF("게시판 관리"),
    TMPLAT("템플릿 관리"),
    CD("코드 관리"),

    LOG_ACTVTY("활동 로그"),
    LOG_SYS("시스템 로그"),

    FILE("파일"),
    FLSYS("파일 시스템");

    public final String desc;
}
