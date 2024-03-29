package io.nicheblog.dreamdiary.web.entity.cmm.managt;

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
 * ManagtrEntity
 * <pre>
 *  작업자 Entity
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditRegEntity
 */
@Entity
@Table(name = "managtr")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE board_post_managtr SET del_yn = 'Y' WHEREmanagtr_no = ?")
public class ManagtrEntity
        extends BaseAuditRegEntity {

    /** 조치자 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "managtr_no")
    @Comment("조치자 번호 (PK)")
    private Integer managtrNo;

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
    public ManagtrEntity(final BaseClsfKey key) {
        this.refPostNo = key.getPostNo();
        this.refContentType = key.getContentType();
    }
}
