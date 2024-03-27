package io.nicheblog.dreamdiary.web;

import lombok.RequiredArgsConstructor;

/**
 * SiteTopMenu
 * <pre>
 *  공통 상수 :: 사이트 상위메뉴 정보 Enum
 * </pre>
 * TODO: 메뉴 DB관리로 완전히 바꾸기
 *
 * @author nichefish
 */
@RequiredArgsConstructor
public enum SiteTopMenu {

    NO_ASIDE("NO_ASIDE", "사이드바 미노출"),
    LGN("00000000", "로그인"),
    MAIN("00000000", "메인"),
    NOTICE("10000000", "공지사항"),
    BOARD("50000000", "게시판"),
    DREAM("20000000", "꿈 관리"),
    SCHDUL("60000000", "일정"),
    USER("60000000", "사용자 관리"),
    ADMIN("90000000", "사이트 관리");

    public final String menuNo;
    public final String menuNm;
}