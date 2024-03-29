package io.nicheblog.dreamdiary.global.intrfc.model.cmpstn;

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
 * CommentCmpstn
 * <pre>
 *  댓글 관련 정보 위임
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCmpstn {

    /** 댓글 목록 */
    private List<CommentDto> commentList;
    /** 댓글 갯수 */
    @Builder.Default
    private Integer commentCnt = 0;
    /** 댓글 존재 여부 */
    @Builder.Default
    private Boolean hasComment = false;

    /* ----- */

    /**
     * 댓글 :: List<Dto> -> List<Entity> 반환
     */
    public List<CommentEntity> getCommentEntityList() {
        if (CollectionUtils.isEmpty(this.commentList)) return null;
        return this.commentList.stream()
                .map(dto -> {
                    try {
                        return CommentMapstruct.INSTANCE.toEntity(dto);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}