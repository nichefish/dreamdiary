package io.nicheblog.dreamdiary.global.cmm.cd.entity;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseManageEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;

/**
 * DtlCdEntity
 * <pre>
 *  상세코드(dtlCd) Entity
 *  ※상세코드(dtl_cd) = 분류코드 하위의 상세코드. 분류코드(cl_cd)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseManageEntity
 */
@Entity
@Table(name = "CMM_DTL_CD")
@IdClass(DtlCdKey.class)      // 분류코드+상세코드 복합키 적용
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "DEL_YN='N'")
public class DtlCdEntity
        extends BaseManageEntity
        implements Serializable {

    /**
     * 상세코드
     */
    @Id
    @Column(name = "DTL_CD")
    private String dtlCd;

    /**
     * 공통코드
     */
    @Id
    @Column(name = "CL_CD")
    private String clCd;

    /**
     * 분류코드 정보
     */
    @ManyToOne
    @JoinColumn(name = "CL_CD", referencedColumnName = "CL_CD", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private ClCdEntity clCdInfo;

    /**
     * 상세코드이름
     */
    @Column(name = "DTL_CD_NM")
    private String dtlCdNm;

    /**
     * 상세코드설명
     */
    @Column(name = "DTL_CD_DC")
    private String dtlCdDc;

    /* ---- */

    /**
     * 생성자
     */
    public DtlCdEntity(
            final String clCd,
            final String dtlCd
    ) {
        this.clCd = clCd;
        this.dtlCd = dtlCd;
    }

    public DtlCdEntity(final DtlCdKey key) {
        this(key.getClCd(), key.getDtlCd());
    }
}
