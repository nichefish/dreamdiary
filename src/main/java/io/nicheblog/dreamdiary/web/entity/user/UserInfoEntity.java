package io.nicheblog.dreamdiary.web.entity.user;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * UserInfoEntity
 * 사용자 정보 Entity :: 계정(User) 정보에 귀속됨
 * (BaseAuditEntity 상속, Serializable 구현)
 *
 * @author nichefish
 */
@Entity
@Table(name = "USER_INFO")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user"}, callSuper = true)
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE USER_INFO SET DEL_YN = 'Y' WHERE USER_INFO_NO = ?")
public class UserInfoEntity
        extends BaseCrudEntity {

    /**
     * 사용자 정보 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_INFO_NO")
    @Comment("사용자 정보 번호")
    private Integer userInfoNo;

    /**
     * 계정 정보
     */
    @OneToOne(mappedBy = "userInfo")
    @JoinColumn(name = "USER_INFO_NO", referencedColumnName = "USER_INFO_NO", insertable = false, updatable = false)
    @Comment("계정 정보")
    private UserEntity user;

    /**
     * 이름
     */
    @Column(name = "USER_NM", length = 20)
    @Comment("이름")
    private String userNm;

    /**
     * 연락처
     */
    @Column(name = "CTTPC", length = 20)
    @Comment("연락처")
    private String cttpc;

    /**
     * Email 주소 (사내메일)
     */
    @Column(name = "EMAIL", length = 40)
    @Comment("Email 주소 (사내메일)")
    private String email;

/*    *//**
     * 소속회사코드 : ...
     *//*
    @Column(name = "CMPY_CD", length = 20)
    @Comment("소속회사코드")
    private String cmpyCd;

    *//**
     * 소속회사코드 정보 (복합키 조인)
     *//*
    @ManyToOne
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'" + Constant.CMPY_CD + "\'", referencedColumnName = "CL_CD")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "CMPY_CD", referencedColumnName = "DTL_CD", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("소속회사코드 정보")
    private DtlCdEntity cmpyCdInfo;

    *//**
     * 팀 코드
     *//*
    @Column(name = "TEAM_CD", length = 20)
    @Comment("팀 코드")
    private String teamCd;

    *//**
     * 팀 정보 (복합키 조인)
     *//*
    @ManyToOne
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'" + Constant.TEAM_CD + "\'", referencedColumnName = "CL_CD")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "TEAM_CD", referencedColumnName = "DTL_CD", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("팀 정보")
    private DtlCdEntity teamCdInfo;*/
/*
    *//**
     * 재직구분 코드 : 재직중/프리랜서
     *//*
    @Column(name = "EMPLYM_CD", length = 20)
    @Comment("재직구분 코드")
    private String emplymCd;

    *//**
     * 재직구분 정보 (복합키 조인)
     *//*
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'" + Constant.EMPLYM_CD + "\'", referencedColumnName = "CL_CD")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "EMPLYM_CD", referencedColumnName = "DTL_CD", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("재직구분 정보")
    private DtlCdEntity emplymCdInfo;*/
/*
    *//**
     * 직급 코드
     *//*
    @Column(name = "JOB_TITLE_CD", length = 20)
    @Comment("직급 코드")
    private String jobTitleCd;

    *//**
     * 직급 정보 (복합키 조인)
     *//*
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'" + Constant.JOB_TITLE_CD + "\'", referencedColumnName = "CL_CD")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "JOB_TITLE_CD", referencedColumnName = "DTL_CD", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("직급 정보")
    private DtlCdEntity jobTitleCdInfo;*/

    /**
     * 수습여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "APNTC_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("수습여부")
    private String apntcYn = "N";

    /** 소속 팀 정보 목록 */
    //    @OneToMany(fetch=FetchType.EAGER)
    //    @JoinColumn(name="USER_INFO_NO")
    //    private List<UserInfoTeamEntity> teamInfoList;

    /**
     * 입사일
     */
    @DateTimeFormat(pattern = DateUtils.PTN_DATE)
    @Column(name = "ECNY_DT")
    @Comment("입사일")
    private Date ecnyDt;

    /**
     * 퇴사여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "RETIRE_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("퇴사여부")
    private String retireYn = "N";

    /**
     * 퇴사일
     */
    @DateTimeFormat(pattern = DateUtils.PTN_DATE)
    @Column(name = "RETIRE_DT")
    @Comment("퇴사일")
    private Date retireDt;

    /**
     * 생년월일
     */
    @DateTimeFormat(pattern = DateUtils.PTN_DATE)
    @Column(name = "BRTHDY")
    @Comment("생년월일")
    private Date brthdy;

    /**
     * 음력여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "LUNAR_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("음력여부")
    private String lunarYn = "N";

    /**
     * (급여)은행
     */
    @Column(name = "ACNT_BANK")
    @Comment("(급여)은행")
    private String acntBank;

    /**
     * (급여)계좌번호
     */
    @Column(name = "ACNT_NO")
    @Comment("(급여)계좌번호")
    private String acntNo;

    /**
     * 사용자 정보 추가추가 목록
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "USER_INFO_NO")
    @Fetch(FetchMode.SELECT)
    @OrderBy("sortOrdr ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("사용자 정보 추가추가 목록")
    private List<UserInfoItemEntity> itemList;

    /**
     * 사용자 설명 (관리자용)
     */
    @Column(name = "USER_INFO_DC", length = 1000)
    @Comment("사용자 설명")
    private String userInfoDc;

    /* ----- */

    /**
     * 서브엔티티 List 처리를 위한 Setter (override)
     * 한 번 Entity가 생성된 이후부터는 new List를 할당하면 안 되고 계속 JPA 이력이 추적되어야 한다.
     */
    public void setItemList(final List<UserInfoItemEntity> itemList) {
        if (CollectionUtils.isEmpty(itemList)) return;
        if (this.itemList == null) {
            this.itemList = itemList;
        } else {
            this.itemList.clear();
            this.itemList.addAll(itemList);
        }
    }
}