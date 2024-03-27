package io.nicheblog.dreamdiary.global.intrfc.entity;

import lombok.*;

import java.io.Serializable;

/**
 * BasePostKey
 * <pre>
 *  (공통/상속) 게시판 복합키 Entity (postNo + contentType)
 * </pre>
 *
 * @author nichefish
 */
@Getter(AccessLevel.PUBLIC)
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
     * 게시판 코드
     */
    protected String contentType;
}
