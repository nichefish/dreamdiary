package io.nicheblog.dreamdiary.web.entity.vcatn.papr;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.List;

/**
 * VcatnPaprEntity
 * <pre>
 *  휴가계획서 Entity
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "vcatn_papr")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
@ToString(callSuper = true)
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE vcatn_papr SET del_yn = 'Y' WHERE post_no = ?")
public class VcatnPaprEntity
        extends BasePostEntity
        implements CommentEmbedModule, TagEmbedModule, ManagtEmbedModule, ViewerEmbedModule {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.VCATN_PAPR;

    /** 글 번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    @Comment("글 번호")
    private Integer postNo;

    /** 컨텐츠 타입 */
    @Builder.Default
    @Column(name = "content_type", columnDefinition = "VARCHAR(50) DEFAULT 'VCATN_PAPR'")
    @Comment("컨텐츠 타입")
    private String contentType = CONTENT_TYPE.key;

    /** 글분류 코드 정보 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'VCATN_PAPR_CTGR_CD'", referencedColumnName = "cl_cd")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "ctgr_cd", referencedColumnName = "dtl_cd", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("휴가계획서 글분류 코드 정보")
    protected DtlCdEntity ctgrCdInfo;
    
    /* ----- */

    /** 휴가 일정 목록 */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumnOrFormula(column = @JoinColumn(name = "ref_post_no", referencedColumnName = "post_no"))
    @Fetch(FetchMode.SELECT)
    @OrderBy("bgnDt ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("휴가 일정 목록")
    private List<VcatnSchdulEntity> schdulList;

    /** 확인 여부 (Y/N) */
    @Builder.Default
    @Column(name = "cf_yn")
    @Comment("휴가계획서 확인 여부")
    private String cfYn = "N";

    /* ----- */

    /**
     * 서브엔티티 List 처리를 위한 Setter Override
     * 한 번 Entity가 생성된 이후부터는 new List를 할당하면 안 되고 계속 JPA 이력이 추적되어야 한다.
     */
    public void setSchdulList(final List<VcatnSchdulEntity> itemList) {
        if (CollectionUtils.isEmpty(itemList)) return;
        if (this.schdulList == null) {
            this.schdulList = itemList;
        } else {
            this.schdulList.clear();
            this.schdulList.addAll(itemList);
        }
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentEmbed comment;

    /** 태그 정보 모듈 (위임) */
    @Embedded
    public TagEmbed tag;

    /** 조치 정보 모듈 (위임) */
    @Embedded
    public ManagtEmbed managt;

    /** 열람자 정보 모듈 (위임) */
    @Embedded
    public ViewerEmbed viewer;
}
