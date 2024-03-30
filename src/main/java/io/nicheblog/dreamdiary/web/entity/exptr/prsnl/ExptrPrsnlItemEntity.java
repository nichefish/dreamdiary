package io.nicheblog.dreamdiary.web.entity.exptr.prsnl;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.cmm.file.entity.AtchFileDtlEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.Date;

/**
 * ExptrPrsnlItemEntity
 * 경비관리 > 경비지출서(개인경비 취합) 개별내용 Entity
 * (Serializable 구현)
 * 경비지출항목(ExptrPrsnlItem) = 경비지출 개별 항목. 경비지출서(ExptrPrsnl)에 N:1로 귀속된다.
 *
 * @author nichefish
 */
@Entity
@Table(name = "exptr_prsnl_item")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE exptr_prsnl_item SET del_yn = 'Y' WHERE exptr_prsnl_item_no=?")
public class ExptrPrsnlItemEntity
        extends BaseCrudEntity {

    @PostLoad
    private void onLoad() {
        if (this.exptrCdInfo != null) this.exptrTyNm = this.exptrCdInfo.getDtlCdNm();
    }

    /** 경비지출서(개인경비 취합) 개별내용 번호 */
    @Id
    @Column(name = "exptr_prsnl_item_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("경비지출서(개인경비 취합) 개별내용 번호")
    private Integer exptrPrsnlItemNo;

    /** 경비지출서(개인경비 취합) 개별내용 번호 */
    @Column(name = "ref_post_no")
    @Comment("참조 글 번호")
    private Integer refPostNo;

    /** 개인경비 취합 정보 */
    @ManyToOne
    @JoinColumn(name = "ref_post_no", referencedColumnName = "post_no", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("개인경비 취합 정보")
    private ExptrPrsnlPaprEntity exptrPrsnlInfo;

    /** 지출일자 */
    @Column(name = "exptr_dt")
    @Comment("지출일자")
    private Date exptrDt;

    /** 지출구분코드 */
    @Column(name = "exptr_cd", length = 20)
    @Comment("지출구분코드")
    private String exptrCd;

    /** 지출구분코드 정보 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'" + Constant.EXPTR_CD + "\'", referencedColumnName = "cl_cd")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "exptr_cd", referencedColumnName = "dtl_cd", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("지출구분코드 정보")
    private DtlCdEntity exptrCdInfo;

    /** 지출구분명 */
    @Transient
    private String exptrTyNm;

    /** 지출금액 */
    @Column(name = "exptr_amt")
    @Comment("지출금액")
    private Integer exptrAmt;

    /** 지출내용 */
    @Column(name = "cn")
    @Comment("지출내용")
    private String exptrCn;

    /** 비고 */
    @Column(name = "rm")
    @Comment("비고")
    private String exptrRm;

    /** 첨부파일(상세) 번호 */
    @Column(name = "atch_file_dtl_no")
    @Comment("첨부파일(상세) 번호")
    private Integer atchFileDtlNo;

    /** 첨부파일(상세) 정보 */
    @OneToOne
    @JoinColumn(name = "atch_file_dtl_no", referencedColumnName = "atch_file_dtl_no", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("첨부파일(상세) 정보")
    private AtchFileDtlEntity atchFileDtlInfo;

    /** 영수증 실물제출 여부 (Y/N) */
    @Builder.Default
    @Column(name = "orgnl_rcipt_yn", length = 1, columnDefinition = "CHAR DEFAULT 'Y'")
    @Comment("영수증 실물제출 여부")
    private String orgnlRciptYn = "N";

    /** 반려 여부 (Y/N) */
    @Builder.Default
    @Column(name = "rject_yn", length = 1, columnDefinition = "CHAR DEFAULT 'Y'")
    @Comment("반려 여부")
    private String rjectYn = "N";

    /** 반려사유 */
    @Column(name = "rject_resn")
    @Comment("반려사유")
    private String rjectResn;
}
