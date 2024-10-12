package io.nicheblog.dreamdiary.global._common._clsf.viewer.entity;

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
 *  컨탠츠 열람자 Entity.
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "viewer")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
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
    @Comment("열람자 번호 (PK)")
    private Integer viewerNo;

    /** 참조 글번호 */
    @Column(name = "ref_post_no")
    @Comment("글번호")
    private Integer refPostNo;

    /** 참조 컨텐츠 타입 */
    @Column(name = "ref_content_type")
    @Comment("게시판 분류 코드")
    private String refContentType;

    /* ----- */

    /**
     * 생성자.
     * @param refKey 글 번호와 컨텐츠 타입을 포함하는 참조 복합키 객체
     */
    public ViewerEntity(final BaseClsfKey refKey) {
        this.refPostNo = refKey.getPostNo();
        this.refContentType = refKey.getContentType();
    }
}
