package io.nicheblog.dreamdiary.web.entity.cmm.comment;

import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAtchEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.Date;

/**
 * CommentEntity
 * <pre>
 *  게시판 댓글 Entity
 *  ※게시판 댓글(board_comment) = 게시판 게시물(board_post)에 1:N으로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditEntity
 */
@Entity
@Table(name = "comment")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE comment SET del_yn = 'Y' WHERE post_no = ?")
public class CommentEntity
        extends BasePostEntity {

    /** 필수: 게시물 코드 */
    private static final String CONTENT_TYPE = "comment";
    /** 필수: 글분류 코드 */
    private static final String CTGR_CL_CD = "COMMENT_CL_CD";

    /**
     * 댓글 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    @Comment("댓글 번호 (key)")
    private Integer postNo;

    /**
     * 게시판 분류 코드
     */
    @Builder.Default
    @Column(name = "content_type")
    private String contentType = CONTENT_TYPE;

    /**
     * 원글 번호
     */
    @Column(name = "ref_post_no")
    @Comment("원글 번호")
    private Integer refPostNo;

    /**
     * 원글 게시판 코드
     */
    @Column(name = "ref_content_type")
    @Comment("원글 게시판 코드")
    private String refBoardCd;
}
