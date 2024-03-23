package io.nicheblog.dreamdiary.web;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * SiteTopMenu
 * <pre>
 *  공통 상수 :: 사이트 상위메뉴 정보
 * </pre>
 *
 * @author nichefish
 */
@RequiredArgsConstructor
@Getter
public enum SiteTopMenu {

    NO_ASIDE("NO_ASIDE", "사이드바 미노출"),
    MAIN("00000000", "메인");

    private final String menuNo;
    private final String desc;
}