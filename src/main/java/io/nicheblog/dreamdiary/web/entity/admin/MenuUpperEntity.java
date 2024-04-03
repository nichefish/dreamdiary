package io.nicheblog.dreamdiary.web.entity.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * MenuUpperEntity
 * <pre>
 *  메뉴 관리 간소화 Entity
 *  순환참조 방지 위해 ㅎ상위메뉴 entity에 대해서 하위메뉴 목록 참조 삭제
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditEntity
 */
@Entity
@Table(name = "menu")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE menu SET del_yn = 'Y' WHERE menu_id = ?")
public class MenuUpperEntity
        extends BaseAuditEntity {

    /**
     * 메뉴 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    @Comment("메뉴 ID")
    private Integer menuId;

    /**
     * 상위메뉴 ID
     */
    @Column(name = "upper_menu_id")
    @Comment("상위메뉴 ID")
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
     * 아이콘 (bootstrap icon 또는 font-awesome)
     */
    @Column(name = "icon")
    @Comment("아이콘")
    private String icon;

    /**
     * 정렬 순서
     */
    @Column(name = "sort_ordr")
    @Comment("정렬 순서")
    private Integer sortOrdr;

    /**
     * 사용여부
     */
    @Column(name = "use_yn", length = 1, columnDefinition = "CHAR DEFAULT 'Y'")
    @Comment("사용여부")
    private String useYn;

    /**
     * 셀프 참조 :: 상위메뉴 조회
     */
    @ManyToOne
    @JoinColumn(name = "upper_menu_id", referencedColumnName = "menu_id", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("상위메뉴 조회")
    private MenuUpperEntity upperMenu;
}
