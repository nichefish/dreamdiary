package io.nicheblog.dreamdiary.web.entity.comment;

import io.nicheblog.dreamdiary.global.cmm.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAtchEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditEntity;
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
@Table(name = "BOARD_COMMENT")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE BOARD_COMMENT SET DEL_YN = 'Y' WHERE COMMENT_NO = ?")
public class CommentEntity
        extends BaseAtchEntity {

    /**
     * 댓글 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_NO")
    @Comment("댓글 번호 (key)")
    private Integer commentNo;

    /**
     * 원글 번호
     */
    @Column(name = "POST_NO")
    @Comment("원글 번호")
    private Integer postNo;

    /**
     * 원글 게시판 코드
     */
    @Column(name = "BOARD_CD")
    @Comment("원글 게시판 코드")
    private String boardCd;

    /**
     * 댓글 내용
     */
    @Column(name = "COMMENT_CN")
    @Comment("댓글 내용")
    private String commentCn;

    /**
     * 상단고정여부
     */
    @Builder.Default
    @Column(name = "FXD_YN")
    @Comment("상단고정여부")
    private String fxdYn = "N";

    /**
     * 조치자(작업자) ID
     */
    @Column(name = "MANAGTR_ID", length = 20, insertable = false)
    @Comment("조치자(작업자) ID")
    private String managtrId;

    /**
     * 조치자(작업자) 정보
     */
    @ManyToOne
    @JoinColumn(name = "MANAGTR_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("조치자(작업자) 정보")
    private AuditorInfo managtrInfo;

    /**
     * 조치(작업)일시
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = DateUtils.PTN_DATETIME)
    @Column(name = "MANAGT_DT")
    @Comment("조치(작업)일시")
    private Date managtDt;
}
