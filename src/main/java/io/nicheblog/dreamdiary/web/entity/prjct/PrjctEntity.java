package io.nicheblog.dreamdiary.web.entity.prjct;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.Date;

/**
 * PrjctEntity
 * <pre>
 *  프로젝트 Entity
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditEntity
 */
@Entity
@Table(name = "prjct")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE prjct SET del_yn = 'Y' WHERE prjct_no = ?")
public class PrjctEntity
        extends BaseAuditEntity {

    /**
     * 프로젝트 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prjct_no")
    @Comment("프로젝트 번호")
    private Integer prjctNo;

    /**
     * 회사 코드
     */
    @Column(name = "cmpy_cd")
    @Comment("회사 코드")
    private String cmpyCd;

    /**
     * 회사 코드 정보 (복합키 조인)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'" + Constant.CMPY_CD + "\'", referencedColumnName = "cl_cd")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "cmpy_cd", referencedColumnName = "dtl_cd", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("회사 코드 정보")
    private DtlCdEntity cmpyInfo;

    /**
     * 계약명
     */
    @Column(name = "cntrct_nm")
    @Comment("계약명")
    private String cntrctNm;

    /**
     * 고객사명
     */
    @Column(name = "cstmr_cmpy_nm")
    @Comment("고객사명")
    private String cstmrCmpyNm;

    /**
     * 원청사
     */
    @Column(name = "orgnl_cntrct_cmpy_nm")
    @Comment("원청사")
    private String orgnlCntrctCmpyNm;

    /**
     * 계약금액 (VAT 별도)
     */
    @Column(name = "cmtrct_amt")
    @Comment("계약금액 (VAT 별도)")
    private Integer cntrtcAmt;

    /**
     * 계약금액 (VAT 포함)
     */
    @Column(name = "cmtrct_amt_with_vat")
    @Comment("계약금액 (VAT 포함)")
    private Integer cntrtcAmtWithVat;

    /**
     * 시작일자
     */
    @Column(name = "bgn_dt")
    @Comment("시작일자")
    private Date bgnDt;

    /**
     * 종료일자
     */
    @Column(name = "end_dt")
    @Comment("종료일자")
    private Date endDt;

    /**
     * 계약기간 시작일 (yyyyMM)
     */
    @Column(name = "cntrct_bgn_dt")
    @Comment("계약기간 시작일 (yyyyMM)")
    private Date cntrctBgnDt;

    /**
     * 계약기간 종료일 (yyyyMM)
     */
    @Column(name = "cntrct_end_dt")
    @Comment("계약기간 종료일 (yyyyMM)")
    private Date cntrctEndDt;

    /**
     * 이행보증
     */
    @Column(name = "prfm_warant")
    @Comment("이행보증")
    private String prfmWarant;

    /**
     * 하자보증
     */
    @Column(name = "flaw_warant")
    @Comment("하자보증")
    private String flawWarant;

    // TODO: SWIT 등록
    // TODO: 투입인원

    /**
     * 잔금여부
     */
    @Builder.Default
    @Column(name = "surplus_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("잔금여부")
    private String surplusYn = "N";
}
