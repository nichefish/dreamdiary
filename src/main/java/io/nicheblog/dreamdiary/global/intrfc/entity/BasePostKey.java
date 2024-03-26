package io.nicheblog.dreamdiary.global.intrfc.entity;

import lombok.*;

import java.io.Serializable;

/**
 * BasePostKey
 * <pre>
 *  (공통/상속) 게시판 복합키 Entity (postNo + boardCd)
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BasePostKey
        implements Serializable {

    /**
     * 글번호
     */
    protected Integer postNo;
    /**
     * 게시판분류코드
     */
    protected String boardCd;
}
