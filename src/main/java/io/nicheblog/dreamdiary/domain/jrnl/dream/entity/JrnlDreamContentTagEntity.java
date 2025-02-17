package io.nicheblog.dreamdiary.domain.jrnl.dream.entity;

import io.nicheblog.dreamdiary.extension.clsf.tag.entity.TagSmpEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditRegEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * JrnlDreamTagEntity
 * <pre>
 *  저널 꿈 태그 Entity.
 *  (사용 용이성을 위해 엔티티 분리)
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "content_tag")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "ref_content_type='JRNL_DREAM' AND del_yn='N'")
@SQLDelete(sql = "UPDATE content_tag SET del_yn = 'Y' WHERE content_tag_no = ?")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class JrnlDreamContentTagEntity
        extends BaseAuditRegEntity {

    /** 컨텐츠 태그 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_tag_no")
    @Comment("컨텐츠 태그 번호 (PK)")
    private Integer contentTagNo;

    /** 참조 태그 번호 */
    @Column(name = "ref_tag_no")
    @Comment("참조 태그 번호")
    private Integer refTagNo;

    /** 참조 글 번호 */
    @Column(name = "ref_post_no")
    @Comment("참조 글 번호")
    private Integer refPostNo;

    /** 참조 컨텐츠 타입 */
    @Column(name = "ref_content_type")
    @Comment("참조 컨텐츠 타입")
    private String refContentType;

    /** 태그 정보 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ref_tag_no", referencedColumnName = "tag_no", updatable = false, insertable = false)
    @Fetch(FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    private TagSmpEntity tag;

    /** 태그 이름 */
    @Transient
    private String tagNm;

    /** 태그 카테고리 */
    @Transient
    private String ctgr;

    /** 참조 컨텐츠 (저널 꿈)  */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ref_post_no", referencedColumnName = "post_no", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("저널 꿈 정보")
    private JrnlDreamSmpEntity jrnlDream;
}
