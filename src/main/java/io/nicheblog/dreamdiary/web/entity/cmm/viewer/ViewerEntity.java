package io.nicheblog.dreamdiary.web.entity.cmm.viewer;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditRegEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * ViewerEntity
 * <pre>
 *  게시판 게시물 열람자 Entity
 *  ※게시판 게시물 열람자(board_post_viewer) = 게시판 게시물(board_post)에 1:N으로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditRegEntity
 */
@Entity
@Table(name = "viewer")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE viewer SET del_yn = 'Y' WHERE viewer_no = ?")
public class ViewerEntity
        extends BaseAuditRegEntity {

    /** 열람자 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "viewer_no")
    @Comment("게시물 열람자 번호 (PK)")
    private Integer postViewerNo;

    /** 참조 글번호 */
    @Column(name = "ref_post_no")
    @Comment("글번호")
    private Integer refPostNo;

    /** 참조 컨텐츠 타입 */
    @Column(name = "ref_content_type")
    @Comment("게시판 분류코드")
    private String refContentType;

    /* ----- */

    /**
     * 생성자
     */
    public ViewerEntity(final BaseClsfKey key) {
        this.refPostNo = key.getPostNo();
        this.refContentType = key.getContentType();
    }
}
