package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.MappedSuperclass;
import java.util.List;

/**
 * BaseClsfDto
 * <pre>
 *  (공통/상속) 게시판 Dto
 *  "All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 *
 * @author nichefish
 * @extends BaseAtchDto
 */
@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BaseClsfDto
        extends BaseAtchDto {

    /** 글 번호 */
    protected Integer postNo;
    /** 컨텐츠 타입 */
    protected String contentType;

    /** 성공여부 */
    @Builder.Default
    protected Boolean isSuccess = false;

    /** 댓글 목록 */
    protected List<CommentDto> commentList;
    /** 댓글 갯수 */
    @Builder.Default
    protected Integer commentCnt = 0;

    /* ----- */

    /**
     * 복합키 객체 반환
     */
    public BaseClsfKey getClsfKey() {
        return new BaseClsfKey(this.postNo, this.contentType);
    }

    /**
     * hasComment
     */
    public Boolean getHasComment() {
        return this.commentCnt > 0;
    }
}
