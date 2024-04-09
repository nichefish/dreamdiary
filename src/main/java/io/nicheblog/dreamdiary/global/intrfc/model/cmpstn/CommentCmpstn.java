package io.nicheblog.dreamdiary.global.intrfc.model.cmpstn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nicheblog.dreamdiary.web.entity.cmm.comment.CommentEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.comment.CommentMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
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
public class CommentCmpstn
        implements Serializable {

    /** 댓글 목록 */
    private List<CommentDto.LIST> list;
    /** 댓글 갯수 */
    @Builder.Default
    private Integer cnt = 0;
    /** 댓글 존재 여부 */
    @Builder.Default
    private Boolean hasComment = false;

    /* ----- */

    /**
     * 댓글 :: List<Dto> -> List<Entity> 반환
     */
    @JsonIgnore
    public List<CommentEntity> getEntityList() {
        if (CollectionUtils.isEmpty(this.list)) return null;
        return this.list.stream()
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