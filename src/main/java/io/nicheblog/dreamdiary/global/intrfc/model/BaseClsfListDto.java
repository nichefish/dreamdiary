package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.MappedSuperclass;
import java.util.List;

/**
 * BaseClsfListDto
 * <pre>
 *  (공통/상속) 게시판 목록 조회 Dto
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
public class BaseClsfListDto
        extends BaseAtchDto {

    /** 글 번호 */
    protected Integer postNo;
    /** 컨텐츠 타입 */
    protected String boardCd;

    /** 제목 */
    protected String title;
    /** 내용 */
    protected String cn;

    /** 댓글 목록 */
    protected List<CommentDto> commentList;
    /** 댓글 갯수 */
    @Builder.Default
    protected Integer commentCnt = 0;

    /* ----- */

    /**
     * hasComment
     */
    public Boolean getHasComment() {
        return this.commentCnt > 0;
    }
}
