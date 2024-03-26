package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostKey;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

/**
 * BasePostDto
 * <pre>
 *  (공통/상속) 게시판 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAtchDto
 */
@MappedSuperclass
@Getter
@Setter
@SuperBuilder
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
     * 게시판분류코드
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
     * 성공여부
     */
    @Builder.Default
    protected Boolean isSuccess = false;

    /**
     * 댓글 갯수
     */
    @Builder.Default
    private Integer commentCnt = 0;

    /* ----- */

    /**
     * 복합키 객체 반환
     */
    public BasePostKey getPostKey() {
        return new BasePostKey(this.getPostNo(), this.getBoardCd());
    }

}
