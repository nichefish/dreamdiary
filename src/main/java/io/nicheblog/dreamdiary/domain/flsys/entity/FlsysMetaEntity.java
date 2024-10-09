package io.nicheblog.dreamdiary.domain.flsys.entity;

import io.nicheblog.dreamdiary.domain._core.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * FlsysMetaEntity
 * <pre>
 *  파일시스템 메타정보 Entity
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "flsys_meta")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE flsys_meta SET del_yn = 'Y' WHERE post_no = ?")
public class FlsysMetaEntity
        extends BasePostEntity
        implements CommentEmbedModule, TagEmbedModule, ManagtEmbedModule, ViewerEmbedModule {

    /** 필수: 컨텐츠 타입 */
    @Builder.Default
    private static final ContentType CONTENT_TYPE = ContentType.FLSYS_META;

    /** 글 번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    @Comment("글 번호")
    private Integer postNo;

    /** 컨텐츠 타입 */
    @Builder.Default
    @Column(name = "content_type", columnDefinition = "VARCHAR(50) DEFAULT 'FLSYS_META'")
    @Comment("컨텐츠 타입")
    private String contentType = CONTENT_TYPE.key;

    /** 글분류 코드 정보 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'FLSYS_META_CTGR_CD'", referencedColumnName = "cl_cd")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "ctgr_cd", referencedColumnName = "dtl_cd", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("파일시스템 메타 글분류 코드 정보")
    private DtlCdEntity ctgrCdInfo;

    /* ----- */

    /** 파일절대경로 */
    @Column(name = "file_path", length = 500)
    @Comment("파일 경로")
    private String filePath;

    /** 상위파일절대경로 */
    @Column(name = "upper_file_path", length = 500)
    @Comment("상위 파일 경로")
    private String upperFilePath;

    /* ----- */

    /** 위임 :: 댓글 정보 모듈 */
    @Embedded
    public CommentEmbed comment;
    /** 위임 :: 태그 정보 모듈 */
    @Embedded
    public TagEmbed tag;
    /** 위임 :: 조치 정보 모듈 */
    @Embedded
    public ManagtEmbed managt;
    /** 위임 :: 열람 정보 모듈 */
    @Embedded
    public ViewerEmbed viewer;
}
