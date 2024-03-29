package io.nicheblog.dreamdiary.global.intrfc.entity;

import lombok.*;

import java.io.Serializable;

/**
 * BaseClsfKey
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
public class BaseClsfKey
        implements Serializable {

    /** 글번호 */
    protected Integer postNo;
    /** 컨텐츠 타입 */
    protected String contentType;
}
