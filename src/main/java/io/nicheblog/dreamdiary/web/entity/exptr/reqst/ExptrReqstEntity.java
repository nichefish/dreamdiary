package io.nicheblog.dreamdiary.web.entity.exptr.reqst;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.CommentEmbed;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.ManagtEmbed;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.ViewerEmbed;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * ExptrReqstEntity
 * <pre>
 *  물품구매/경조사비 신청 Entity
 * </pre>
 *
 * @author nichefish
 * @extends BasePostEntity
 */
@Entity
@Table(name = "exptr_reqst")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE exptr_reqst SET del_yn = 'Y' WHERE post_no = ?")
public class ExptrReqstEntity
        extends BasePostEntity {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.EXPTR_REQST;
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

    /** 처리 여부 (Y/N) */
    @Builder.Default
    @Column(name = "cf_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("처리 여부")
    private String cfYn = "N";

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentEmbed comment;

    /** 조치 정보 모듈 (위임) */
    @Embedded
    public ManagtEmbed managt;

    /** 열람자 정보 모듈 (위임) */
    @Embedded
    public ViewerEmbed viewer;
}
