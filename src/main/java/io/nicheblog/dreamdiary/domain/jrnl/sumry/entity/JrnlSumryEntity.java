package io.nicheblog.dreamdiary.domain.jrnl.sumry.entity;

import io.nicheblog.dreamdiary.global._common._clsf.ContentType;
import io.nicheblog.dreamdiary.global._common._clsf.comment.entity.embed.CommentEmbed;
import io.nicheblog.dreamdiary.global._common._clsf.comment.entity.embed.CommentEmbedModule;
import io.nicheblog.dreamdiary.global._common._clsf.sectn.entity.embed.SectnEmbed;
import io.nicheblog.dreamdiary.global._common._clsf.sectn.entity.embed.SectnEmbedModule;
import io.nicheblog.dreamdiary.global._common._clsf.tag.entity.embed.TagEmbed;
import io.nicheblog.dreamdiary.global._common._clsf.tag.entity.embed.TagEmbedModule;
import io.nicheblog.dreamdiary.global._common.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * JrnlSumryEntity
 * <pre>
 *  저널 결산 Entity.
 * </pre>
 *
 * @author nichefish
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
        implements CommentEmbedModule, TagEmbedModule, SectnEmbedModule {

    /** 필수: 컨텐츠 타입 */
    @Builder.Default
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
    private DtlCdEntity ctgrCdInfo;

    /* ----- */

    /** 꿈 기록 완료 여부 (Y/N) */
    @Builder.Default
    @Column(name = "dream_compt_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("꿈 기록 완료 여부 (Y/N)")
    private String dreamComptYn = "N";

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

    /**
     * 생성자.
     *
     * @param yy - 생성할 엔티티에 설정할 연도 값
     */
    public JrnlSumryEntity(final Integer yy) {
        this.yy = yy;
    }

    /* ----- */

    /** 위임 :: 댓글 정보 모듈 */
    @Embedded
    public CommentEmbed comment;
    /** 위임 :: 태그 정보 모듈 */
    @Embedded
    public TagEmbed tag;
    /** 위임 :: 단락 정보 모듈 */
    @Embedded
    public SectnEmbed sectn;
}
