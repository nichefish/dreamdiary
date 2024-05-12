package io.nicheblog.dreamdiary.web.entity.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamTagEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

/**
 * TagEntity
 * <pre>
 *  태그 Entity
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudEntity
 */
@Entity
@Table(name = "tag")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE tag SET del_yn = 'Y' WHERE tag_no = ?")
public class TagEntity
        extends BaseCrudEntity {

    /** 태그 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_no")
    @Comment("태그 번호 (PK)")
    private Integer tagNo;

    /** 태그 */
    @Column(name = "tag_nm")
    @Comment("태그")
    private String tagNm;

    /** 컨텐츠 태그 */
    @OneToMany(mappedBy = "tag", fetch = FetchType.EAGER)
    private List<ContentTagEntity> contentTagList;

    /** 저널 꿈 태그 */
    @OneToMany(mappedBy = "tag", fetch = FetchType.EAGER)
    @Where(clause = "ref_content_type = 'JRNL_DREAM'")
    private List<JrnlDreamTagEntity> jrnlDreamTagList;

    /* ----- */

    /**
     * 생성자
     */
    public TagEntity(final String tagNm) {
        this.tagNm = tagNm;
    }
}
