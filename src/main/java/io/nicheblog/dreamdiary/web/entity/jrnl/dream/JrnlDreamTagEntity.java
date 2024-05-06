package io.nicheblog.dreamdiary.web.entity.jrnl.dream;

import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * JrnlDreamTagEntity
 * <pre>
 *  저널 꿈 태그 Entity
 *  (사용 용이성을 위해 엔티티 분리)
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudEntity
 */
@Entity
@Table(name = "content_tag")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "ref_content_type='JRNL_DREAM' AND del_yn='N'")
@SQLDelete(sql = "UPDATE content_tag SET del_yn = 'Y' WHERE content_tag_no = ?")
public class JrnlDreamTagEntity
        extends ContentTagEntity {

    /** 참조 컨텐츠  */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ref_post_no", referencedColumnName = "post_no", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("저널 꿈 정보")
    private JrnlDreamEntity jrnlDream;
}
