package io.nicheblog.dreamdiary.global.intrfc.entity.embed;

import io.nicheblog.dreamdiary.web.entity.cmm.comment.CommentEntity;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.util.CollectionUtils;

import javax.persistence.OrderBy;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * CommentEmbed
 * <pre>
 *  댓글 관련 정보 위임
 * </pre>
 *
 * @author nichefish
 */
@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEmbed
        implements Serializable {

    @PostLoad
    private void onLoad() {
        this.cnt = (CollectionUtils.isEmpty(this.list)) ? 0 : this.list.size();
        this.hasComment = this.cnt > 0;
    }

    /** 댓글 목록 */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "ref_post_no", referencedColumnName = "post_no", insertable = false, updatable = false)),
            @JoinColumnOrFormula(column = @JoinColumn(name = "ref_content_type", referencedColumnName = "content_type", insertable = false, updatable = false)),
    })
    @Fetch(FetchMode.SELECT)
    @OrderBy("regDt ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("댓글 목록")
    private List<CommentEntity> list;

    /** 댓글 수 */
    @Transient
    private Integer cnt;

    /** 댓글 존재 여부 */
    @Transient
    private Boolean hasComment;
}