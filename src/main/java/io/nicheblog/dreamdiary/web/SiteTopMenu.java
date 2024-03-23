package io.nicheblog.dreamdiary.web;

import lombok.RequiredArgsConstructor;

/**
 * SiteTopMenu
 * <pre>
 *  공통 상수 :: 사이트 상위메뉴 정보
 * </pre>
 * TODO: 메뉴 DB관리로 완전히 바꾸기
 *
 * @author nichefish
 */
@RequiredArgsConstructor
public enum SiteTopMenu {

    NO_ASIDE("NO_ASIDE", "사이드바 미노출"),
    MAIN("00000000", "메인"),
    NOTICE("10000000", "공지사항"),
    BOARD("50000000", "게시판"),
    SCHDUL("60000000", "일정");


    public final String menuNo;
    public final String desc;
}