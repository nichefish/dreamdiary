package io.nicheblog.dreamdiary.global.intrfc.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

/**
 * BasePostListDto
 * <pre>
 *  (공통/상속) 게시판 목록 조회 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAtchDto
 */
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class BaseClsfListDto
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
     * 댓글 갯수
     */
    @Builder.Default
    private Integer commentCnt = 0;
}
