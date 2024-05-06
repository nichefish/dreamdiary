package io.nicheblog.dreamdiary.web.entity.jrnl.sumry;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.CommentEmbed;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.CommentEmbedModule;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.TagEmbed;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.TagEmbedModule;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.List;

/**
 * JrnlSumryEntity
 * <pre>
 *  저널 결산 Entity.
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudEntity
 */
@Entity
@Table(name = "jrnl_sumry")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE jrnl_summary SET del_yn = 'Y' WHERE post_no = ?")
public class JrnlSumryEntity
        extends BasePostEntity
        implements CommentEmbedModule, TagEmbedModule {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.JRNL_SUMRY;

    /** 저널 꿈 고유 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    @Comment("저널 결산 고유 번호")
    private Integer postNo;

    /** 컨텐츠 타입 */
    @Builder.Default
    @Column(name = "content_type", columnDefinition = "VARCHAR(50) DEFAULT 'JRNL_SUMRY'")
    @Comment("컨텐츠 타입")
    private String contentType = CONTENT_TYPE.key;

    /** 글분류 코드 정보 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'JRNL_SUMRY_CTGR_CD'", referencedColumnName = "cl_cd")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "ctgr_cd", referencedColumnName = "dtl_cd", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("저널 결산 글분류 코드 정보")
    protected DtlCdEntity ctgrCdInfo;

    /* ----- */

    /** 저널 결산 내용 (일기) 목록 */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "jrnl_sumry_no", referencedColumnName = "post_no", insertable = false, updatable = false)
    @Fetch(FetchMode.SELECT)
    @Where(clause = "ctgr_cd = 'DIARY'")
    @OrderBy("idx ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("저널 결산 내용 목록")
    private List<JrnlSumryCnEntity> sumryCnDiaryList;

    /** 저널 결산 내용 (꿈) 목록 */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "jrnl_sumry_no", referencedColumnName = "post_no", insertable = false, updatable = false)
    @Fetch(FetchMode.SELECT)
    @Where(clause = "ctgr_cd = 'DREAM'")
    @OrderBy("idx ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("저널 결산 내용 목록")
    private List<JrnlSumryCnEntity> sumryCnDreamList;

    /* ----- */

    /** 결산 년도 */
    @Column(name = "yy", unique = true)
    private Integer yy;

    /** 꿈 일수 */
    @Column(name = "dream_day_cnt")
    private Integer dreamDayCnt;

    /** 꿈 갯수 */
    @Column(name = "dream_cnt")
    private Integer dreamCnt;

    /** 일기 일수 */
    @Column(name = "diary_day_cnt")
    private Integer diaryDayCnt;

    /* ----- */

    /** 생성자 */
    public JrnlSumryEntity(Integer yy) {
        this.yy = yy;
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentEmbed comment;
    /** 태그 정보 모듈 (위임) */
    @Embedded
    public TagEmbed tag;
}
