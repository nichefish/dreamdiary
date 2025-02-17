package io.nicheblog.dreamdiary.extension.clsf.comment.entity;

import io.nicheblog.dreamdiary.extension.clsf.ContentType;
import io.nicheblog.dreamdiary.extension.clsf.comment.entity.embed.CommentEmbed;
import io.nicheblog.dreamdiary.extension.clsf.comment.entity.embed.CommentEmbedModule;
import io.nicheblog.dreamdiary.extension.clsf.tag.entity.embed.TagEmbed;
import io.nicheblog.dreamdiary.extension.clsf.tag.entity.embed.TagEmbedModule;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * CommentEntity
 * <pre>
 *  댓글 Entity.
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "comment")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE comment SET del_yn = 'Y' WHERE post_no = ?")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CommentEntity
        extends BasePostEntity
        implements CommentEmbedModule, TagEmbedModule {

    /** 필수: 컨텐츠 타입 */
    @Builder.Default
    private static final ContentType CONTENT_TYPE = ContentType.COMMENT;

    /** 댓글 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    @Comment("댓글 번호 (key)")
    private Integer postNo;

    /** 컨텐츠 타입 */
    @Builder.Default
    @Column(name = "content_type", columnDefinition = "VARCHAR(50) DEFAULT 'COMMENT'")
    @Comment("컨텐츠 타입")
    private String contentType = CONTENT_TYPE.key;

    /** 글분류 코드 :: join을 제거하고 메모리 캐시 처리 */
    @Column(name = "ctgr_cd", length = 50)
    @Comment("댓글 글분류 코드 정보")
    private String ctgrCd;

    /** 글분류 코드 이름 :: join을 제거하고 메모리 캐시 처리 */
    @Transient
    private String ctgrNm;

    /* ----- */

    /** 원글 번호 */
    @Column(name = "ref_post_no")
    @Comment("원글 번호")
    private Integer refPostNo;

    /** 원글 컨텐츠 타입 */
    @Column(name = "ref_content_type")
    @Comment("원글 컨텐츠 타입")
    private String refContentType;

    /* ----- */

    /** 위임 :: 댓글 정보 모듈 */
    @Embedded
    public CommentEmbed comment;
    /** 위임 :: 태그 정보 모듈 */
    @Embedded
    public TagEmbed tag;
}
