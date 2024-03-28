package io.nicheblog.dreamdiary.web.entity.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseManageEntity;
import io.nicheblog.dreamdiary.web.mapstruct.admin.MenuMapstruct;
import io.nicheblog.dreamdiary.web.model.admin.MenuDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MenuEntity
 * <pre>
 *  메뉴 관리 Entity
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditEntity
 */
@Entity
@Table(name = "menu")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE menu SET del_yn = 'Y' WHERE menu_id = ?")
public class MenuEntity
        extends BaseManageEntity {

    /**
     * 메뉴 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    @Comment("메뉴 ID")
    private Integer menuId;

    /**
     * 상위메뉴번호
     */
    @Column(name = "upper_menu_id")
    @Comment("상위메뉴번호")
    private Integer upperMenuId;

    /**
     * 메뉴 구분 코드
     */
    @Column(name = "menu_ty_cd")
    @Comment("메뉴 구분 코드")
    private String menuTyCd;

    /**
     * 메뉴 구분 코드 정보 (복합키 조인)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'MENU_TY_CD\'", referencedColumnName = "cl_cd")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "menu_ty_cd", referencedColumnName = "dtl_cd", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("메뉴 구분 코드 정보")
    private DtlCdEntity menuTyInfo;

    /**
     * 메뉴명
     */
    @Column(name = "menu_nm")
    @Comment("메뉴명")
    private String menuNm;

    /**
     * 메뉴번호
     */
    @Column(name = "menu_no")
    @Comment("메뉴번호")
    private String menuNo;

    /**
     * URL
     */
    @Column(name = "url")
    @Comment("URL")
    private String url;

    /**
     * 아이콘 (bootstrap icon 또는 font-awesome) TODO: svg?
     */
    @Column(name = "icon")
    @Comment("아이콘")
    private String icon;

    /**
     * 셀프 참조 :: 상위메뉴 조회
     */
    @ManyToOne
    @JoinColumn(name = "upper_menu_id", referencedColumnName = "menu_id", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("상위메뉴 조회")
    private MenuUpperEntity upperMenu;

    /**
     * 셀프 참조 :: 하위메뉴 목록 조회
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "upper_menu_id", referencedColumnName = "menu_id", insertable = false, updatable = false)
    @Fetch(FetchMode.SELECT)
    @OrderBy("menuNo ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("하위메뉴 목록 조회")
    private List<MenuEntity> subMenuList;

    /* ----- */

    /**
     * 하위메뉴 DTO 목록 반환
     */
    public List<MenuDto> getSubMenuDtoList() throws Exception {
        if (CollectionUtils.isEmpty(this.subMenuList)) return new ArrayList<>();
        List<MenuDto> subMenuDtoList = new ArrayList<>();
        for (MenuEntity e : this.subMenuList) {
            subMenuDtoList.add(MenuMapstruct.INSTANCE.toDto(e));
        }
        return subMenuDtoList;
    }
}
