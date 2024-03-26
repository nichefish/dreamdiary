package io.nicheblog.dreamdiary.web.entity.cmm.comment;

import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAtchEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfEntity;
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
@Table(name = "COMMENT")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE COMMENT SET DEL_YN = 'Y' WHERE POST_NO = ?")
public class CommentEntity
        extends BaseClsfEntity {

    /** 필수: 게시물 코드 */
    private static final String BOARD_CD = "comment";
    /** 필수: 글분류 코드 */
    private static final String CTGR_CL_CD = "COMMENT_CL_CD";

    /**
     * 댓글 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_NO")
    @Comment("댓글 번호 (key)")
    private Integer postNo;

    /**
     * 게시판 분류 코드
     */
    @Builder.Default
    @Column(name = "BOARD_CD")
    private String boardCd = BOARD_CD;

    /**
     * 원글 번호
     */
    @Column(name = "REF_POST_NO")
    @Comment("원글 번호")
    private Integer refPostNo;

    /**
     * 원글 게시판 코드
     */
    @Column(name = "REF_BOARD_CD")
    @Comment("원글 게시판 코드")
    private String refBoardCd;
}
