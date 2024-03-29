package io.nicheblog.dreamdiary.web.entity.user;

import io.nicheblog.dreamdiary.web.entity.cmm.comment.CommentEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.comment.CommentMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.util.CollectionUtils;

import javax.persistence.OrderBy;
import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CommentEmbed
 * <pre>
 *  댓글 관련 정보 위임
 * </pre>
 *
 * @author nichefish
 */
@Embeddable
@Getter(AccessLevel.PUBLIC)
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEmbed {

    @PostLoad
    private void onLoad() {
        this.commentCnt = (CollectionUtils.isEmpty(this.commentList)) ? 0 : this.commentList.size();
        this.hasComment = this.commentCnt > 0;
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
    private List<CommentEntity> commentList;

    /** 댓글 수 */
    @Transient
    private Integer commentCnt;

    /** 댓글 존재 여부 */
    @Transient
    private Boolean hasComment;

    /* ----- */

    /**
     * 댓글 :: List<Entity> -> List<Dto> 반환
     */
    public List<CommentDto> getCommentDtoList() {
        if (CollectionUtils.isEmpty(this.commentList)) return null;
        return this.commentList.stream()
                .map(entity -> {
                    try {
                        return CommentMapstruct.INSTANCE.toDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}