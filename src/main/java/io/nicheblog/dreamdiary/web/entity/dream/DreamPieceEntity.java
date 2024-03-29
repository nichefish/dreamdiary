package io.nicheblog.dreamdiary.web.entity.dream;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.CommentEmbed;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * DreamPieceEntity
 * <pre>
 *  꿈 조각 Entity.
 *  Entity that contains each distinct dream.
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudEntity
 */
@Entity
@Table(name = "dream_piece")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE dream_piece SET del_yn = 'Y' WHERE dream_piece_no = ?")
public class DreamPieceEntity
        extends BasePostEntity {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.DREAM_PIECE;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 꿈 조각 고유 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    @Comment("꿈 조각 고유 번호")
    private Integer postNo;

    /** 게시판 분류 코드 */
    @Builder.Default
    @Column(name = "content_type")
    private String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 꿈 일자 번호  */
    @Column(name = "dream_day_no")
    @Comment("꿈 일자 번호")
    private Integer dreamDayNo;

    /** 순번 */
    @Column(name = "idx", columnDefinition = "INT DEFAULT 1")
    private Integer idx;

    /** 편집완료 여부 */
    @Builder.Default
    @Column(name = "edit_compt_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("편집완료 여부")
    private String editComptYn = "N";

    /** 타인 꿈 여부 */
    @Builder.Default
    @Column(name = "else_dream_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("타인 꿈 여부")
    private String elseDreamYn = "N";

    /** 꿈꾼이(타인) 이름 */
    @Column(name = "else_dreamer_nm", length = 64)
    private String elseDreamerNm;

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentEmbed comment;
}
