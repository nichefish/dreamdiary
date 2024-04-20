package io.nicheblog.dreamdiary.web.entity.jrnl.diary;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.CommentEmbed;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.CommentEmbedModule;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.TagEmbed;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.TagEmbedModule;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * JrnlDiaryEntity
 * <pre>
 *  저널 일기 Entity.
 *  Entity that contains each distinct diary.
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudEntity
 */
@Entity
@Table(name = "jrnl_diary")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE jrnl_diary SET del_yn = 'Y' WHERE post_no = ?")
public class JrnlDiaryEntity
        extends BasePostEntity
        implements CommentEmbedModule, TagEmbedModule {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.JRNL_DIARY;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 저널 꿈 고유 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    @Comment("저널 일기 고유 번호")
    private Integer postNo;

    /** 게시판 분류 코드 */
    @Builder.Default
    @Column(name = "content_type", columnDefinition = "VARCHAR(50) DEFAULT 'JRNL_DIARY'")
    @Comment("컨텐츠 타입")
    private String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 저널 일자 번호  */
    @Column(name = "jrnl_day_no")
    @Comment("저널 일자 번호")
    private Integer jrnlDayNo;

    /** 저널 일자 정보 */
    @ManyToOne
    @JoinColumn(name = "jrnl_day_no", referencedColumnName = "post_no", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("저널 일자 정보")
    private JrnlDayEntity jrnlDay;

    /** 순번 */
    @Column(name = "idx", columnDefinition = "INT DEFAULT 1")
    private Integer idx;

    /** 편집완료 여부 (Y/N) */
    @Builder.Default
    @Column(name = "edit_compt_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("편집완료 여부")
    private String editComptYn = "N";

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentEmbed comment;
    /** 태그 정보 모듈 (위임) */
    @Embedded
    public TagEmbed tag;
}
