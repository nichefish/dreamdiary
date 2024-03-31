package io.nicheblog.dreamdiary.web.entity.flsys;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * FlsysMetaEntity
 * 파일시스템 메타정보 Entity
 * (BasePostEntity 상속)
 *
 * @author nichefish
 */
@Entity
@Table(name = "flsys_meta")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE flsys_meta SET del_yn = 'Y' WHERE content_type = ? AND post_no = ?")
public class FlsysMetaEntity
        extends BasePostEntity
        implements CommentEmbedModule, TagEmbedModule, ManagtEmbedModule, ViewerEmbedModule {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.FLSYS_META;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 글 번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    @Comment("글 번호")
    private Integer postNo;

    /** 컨텐츠 타입 */
    @Builder.Default
    @Column(name = "content_type")
    @Comment("컨텐츠 타입")
    private String contentType = CONTENT_TYPE.key;

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
