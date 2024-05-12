package io.nicheblog.dreamdiary.global.cmm.cd.entity;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.StateEmbed;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.StateEmbedModule;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * DtlCdEntity
 * <pre>
 *  상세코드(dtlCd) Entity
 *  ※상세코드(dtl_cd) = 분류코드 하위의 상세코드. 분류코드(cl_cd)에 N:1로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditEntity
 * @implements StateEmbedModule
 */
@Entity
@Table(name = "cmm_dtl_cd")
@IdClass(DtlCdKey.class)      // 분류코드+상세코드 복합키 적용
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@Where(clause = "del_yn='N'")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DtlCdEntity
        extends BaseAuditEntity
        implements StateEmbedModule {

    /** 상세코드 */
    @Id
    @Column(name = "dtl_cd", length=50)
    private String dtlCd;

    /** 공통코드 */
    @Id
    @Column(name = "cl_cd", length=50)
    private String clCd;

    /** 분류코드 정보 */
    @ManyToOne
    @JoinColumn(name = "cl_cd", referencedColumnName = "cl_cd", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private ClCdEntity clCdInfo;

    /** 상세코드이름 */
    @Column(name = "dtl_cd_nm", length=50)
    private String dtlCdNm;

    /** 상세코드설명 */
    @Column(name = "dc", length=2000)
    private String dc;

    /* ---- */

    /**
     * 생성자
     */
    public DtlCdEntity(final String clCd, final String dtlCd) {
        this.clCd = clCd;
        this.dtlCd = dtlCd;
    }
    public DtlCdEntity(final DtlCdKey key) {
        this(key.getClCd(), key.getDtlCd());
    }

    /* ----- */

    /** 상태 관리 모듈 (위임) */
    @Embedded
    public StateEmbed state;
}
