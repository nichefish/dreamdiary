package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@NoArgsConstructor
public class BasePostListDto
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
    protected String postSj;
    /**
     * 조회수
     */
    protected Integer postHit = 0;

}
