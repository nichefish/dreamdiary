package io.nicheblog.dreamdiary.web.entity.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.StateEmbed;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.StateEmbedModule;
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
 * @implements StateEmbedModule
 */
@Entity
@Table(name = "menu")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE menu SET del_yn = 'Y' WHERE menu_no = ?")
public class MenuEntity
        extends BaseAuditEntity
        implements StateEmbedModule {

    /** 메뉴 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_no")
    @Comment("메뉴 번호 (PK)")
    private Integer menuNo;

    /** 상위메뉴 ID */
    @Column(name = "upper_menu_no")
    @Comment("상위 메뉴 번호")
    private Integer upperMenuNo;

    /** 메뉴 구분 코드 */
    @Column(name = "menu_ty_cd")
    @Comment("메뉴 구분 코드")
    private String menuTyCd;

    /** 메뉴 구분 코드 정보 (복합키 조인) */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'MENU_TY_CD\'", referencedColumnName = "cl_cd")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "menu_ty_cd", referencedColumnName = "dtl_cd", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("메뉴 구분 코드 정보")
    private DtlCdEntity menuTyCdInfo;

    /** 메뉴명 */
    @Column(name = "menu_nm")
    @Comment("메뉴명")
    private String menuNm;

    /** URL  */
    @Column(name = "url")
    @Comment("URL")
    private String url;

    /** 아이콘 (bootstrap icon 또는 font-awesome) TODO: svg? */
    @Column(name = "icon")
    @Comment("아이콘")
    private String icon;

    /** 셀프 참조 :: 상위메뉴 조회 */
    @ManyToOne
    @JoinColumn(name = "upper_menu_no", referencedColumnName = "menu_no", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("상위메뉴 조회")
    private MenuUpperEntity upperMenu;

    /** 셀프 참조 :: 하위메뉴 목록 조회 */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "upper_menu_no", referencedColumnName = "menu_no", insertable = false, updatable = false)
    @Fetch(FetchMode.SELECT)
    @OrderBy("state.sortOrdr ASC")
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

    /* ----- */

    /** 상태 관리 모듈 (위임) */
    @Embedded
    public StateEmbed state;
}
