package io.nicheblog.dreamdiary.web.entity.vcatn.papr;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.Date;

/**
 * VcatnSchdulEntity
 * <pre>
 *  휴가 일정 Entity
 * </pre>
 *
 *
 * @author nichefish
 */
@Entity
@Table(name = "vcatn_schdul")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@ToString(exclude = "vcatnPaprInfo", callSuper = true)
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE vcatn_schdul SET del_yn = 'Y' WHERE vcatn_schdul_no = ?")
public class VcatnSchdulEntity
        extends BaseCrudEntity {

    @PostLoad
    private void onLoad() {
        // 코드 이름 세팅
        if (this.vcatnCdInfo != null) this.vcatnNm = this.vcatnCdInfo.getDtlCdNm();
        this.userNm = this.getVcatnUserNm();        // return 용이성 때문에 메소드 분리
    }
    private String getVcatnUserNm() {
        boolean isPaprSubmitted = this.vcatnPaprInfo != null;
        if (isPaprSubmitted) {
            if (this.vcatnPaprInfo.getRegstrInfo() == null) return null;
            return this.vcatnPaprInfo.getRegstrInfo().getNickNm();
        }
        boolean isRegByMngr = this.userInfo != null;
        if (isRegByMngr) return this.userInfo.getNickNm();
        return null;
    }

    /** 휴가 일정 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vcatn_schdul_no")
    @Comment("휴가 일정 번호 (PK)")
    private Integer vcatnSchdulNo;

    /** 휴가계획서 정보 */
    @ManyToOne
    @JoinColumn(name = "ref_post_no", referencedColumnName = "post_no", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("휴가계획서 정보")
    private VcatnPaprEntity vcatnPaprInfo;

    /** 글 번호 */
    @Column(name = "ref_post_no")
    @Comment("글 번호")
    private Integer refPostNo;

    /** 휴가 분류 코드 */
    @Column(name = "vcatn_cd")
    @Comment("휴가 분류 코드")
    private String vcatnCd;

    /** 휴가 분류 코드 정보 (복합키 조인) */
    @ManyToOne
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'" + Constant.VCATN_CD + "\'", referencedColumnName = "cl_cd")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "vcatn_cd", referencedColumnName = "dtl_cd", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("휴가 분류 코드 정보")
    private DtlCdEntity vcatnCdInfo;

    @Transient
    private String vcatnNm;

    /** 휴가 시작일 */
    @Column(name = "bgn_dt")
    @Comment("휴가 시작일")
    private Date bgnDt;

    /** 휴가 종료일 */
    @Column(name = "end_dt")
    @Comment("휴가 종료일")
    private Date endDt;

    /** 휴가 사유 */
    @Column(name = "resn")
    @Comment("휴가 사유")
    private String resn;

    /** 비고 */
    @Column(name = "rm")
    @Comment("비고")
    private String rm;

    /** 휴가자 ID */
    @Column(name = "user_id")
    @Comment("휴가자 ID")
    private String userId;

    /** 휴가자 정보 (관리자 등록시 적용) */
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("휴가자 정보")
    private AuditorInfo userInfo;

    @Transient
    private String userNm;
}
