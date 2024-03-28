package io.nicheblog.dreamdiary.web.model.admin;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseManageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * MenuDto
 * <pre>
 *  메뉴 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseManageDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class MenuDto
        extends BaseManageDto {

    /**
     * 메뉴 ID (PK)
     */
    private Integer menuId;
    /**
     * 상위 메뉴 ID
     */
    private Integer upperMenuId;
    /**
     * 메뉴 구분 코드 (루트"ROOT", 대메뉴"MAIN", 중-소메뉴"SUB")
     */
    private String menuTyCd;
    /**
     * 메뉴 구분 코드 (루트"ROOT", 대메뉴"MAIN", 중-소메뉴"SUB")
     */
    private String menuTyNm;
    /**
     * 메뉴 이름
     */
    private String menuNm;
    /**
     * 메뉴번호
     */
    private String menuNo;
    /**
     * URL
     */
    private String url;
    /**
     * 아이콘 (bootstrap icon 또는 font-awesome) TODO: svg?
     */
    private String icon;
    /**
     * 정렬 순서
     */
    private Integer sortOrdr;
    /**
     * 사용 여부
     */
    private String useYn;

    /**
     * 폴더(중메뉴) 여부
     */
    private String dirYn;

    /**
     * 셀프 참조 :: 상위메뉴 조회
     */
    private MenuDto upperMenu;
    /**
     * 셀프 참조 :: 하위메뉴 목록 조회
     */
    private List<MenuDto> subMenuList;

    /* ----- */

    /**
     * 상위메뉴가 메인메뉴인지 여부 반환
     */
    public boolean getIsUnderMain() {
        MenuDto upperMenu = this.upperMenu;
        if (upperMenu == null) return false;
        return "main".equals(upperMenu.getMenuTyCd());
    }

    /**
     * 폴더(중메뉴) 여부 반환
     */
    public Boolean isDir() {
        return "Y".equals(this.dirYn);
    }
}