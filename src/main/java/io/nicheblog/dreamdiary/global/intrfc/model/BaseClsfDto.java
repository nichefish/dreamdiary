package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostKey;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.MappedSuperclass;
import java.util.List;

/**
 * BasePostDto
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
public class  BaseClsfDto
        extends BaseAtchDto {

    /**
     * 글 번호
     */
    protected Integer postNo;
    /**
     * 게시판 코드
     */
    protected String boardCd;

    /**
     * 제목
     */
    protected String title;
    /**
     * 내용
     */
    protected String cn;

    /**
     * 글분류 코드
     */
    protected String ctgrClCd;
    /**
     * 글분류 코드
     */
    protected String ctgrCd;
    /**
     * 글분류 코드 이름
     */
    protected String ctgrNm;

    /**
     * 상단고정여부
     */
    @Builder.Default
    protected String fxdYn = "N";
    /**
     * 성공여부
     */
    @Builder.Default
    protected Boolean isSuccess = false;

    /**
     * 댓글 목록
     */
    protected List<CommentDto> commentList;
    /**
     * 댓글 갯수
     */
    @Builder.Default
    protected Integer commentCnt = 0;

    /* ----- */

    /**
     * 복합키 객체 반환
     */
    public BasePostKey getPostKey() {
        return new BasePostKey(this.getPostNo(), this.getBoardCd());
    }

    /**
     * hasCtgrNm
     */
    public Boolean getHasCtgrNm() {
        return StringUtils.isNotEmpty(this.ctgrNm);
    }

    /**
     * hasComment
     */
    public Boolean getHasComment() {
        return this.commentCnt > 0;
    }
}
