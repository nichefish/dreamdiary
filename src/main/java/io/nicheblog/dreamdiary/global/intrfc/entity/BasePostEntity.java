package io.nicheblog.dreamdiary.global.intrfc.entity;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.*;

/**
 * BasePostEntity
 * <pre>
 *  (공통/상속) 게시판 속성 Entity
 *  "All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 *
 * @author nichefish
 * @implements BaseClsfEntity
 */
@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
public class BasePostEntity
        extends BaseClsfEntity {

    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = "DEFAULT_CTGR_CL_CD";

    /**
     * 제목
     */
    @Column(name = "title")
    protected String title;

    /**
     * 내용
     */
    @Column(name = "cn")
    protected String cn;

    /**
     * 글분류 코드
     */
    @Column(name = "ctgr_cd", length = 20)
    @Comment("글분류 코드")
    protected String ctgrCd;

    /** 글분류 코드 정보 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'"+CTGR_CL_CD+"'", referencedColumnName = "cl_cd")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "ctgr_cd", referencedColumnName = "dtl_cd", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("공지사항 글분류 코드 정보")
    protected DtlCdEntity ctgrCdInfo;

    /**
     * 상단고정여부
     */
    @Builder.Default
    @Column(name = "fxd_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("상단고정여부")
    protected String fxdYn = "N";

    /**
     * 조회수
     */
    @Builder.Default
    @Column(name = "HIT_CNT")
    protected Integer hitCnt = 0;

    /**
     * 중요여부
     */
    @Builder.Default
    @Column(name = "IMPRTC_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("중요여부")
    protected String imprtcYn = "N";

    /**
     * 수정권한
     */
    @Builder.Default
    @Column(name = "MDFABLE")
    @Comment("수정권한")
    protected String mdfable = Constant.MDFABLE_REGSTR;
}
