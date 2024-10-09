package io.nicheblog.dreamdiary.global.intrfc.entity;

import io.nicheblog.dreamdiary.global.ContentType;
import lombok.*;

import java.io.Serializable;

/**
 * BaseClsfKey
 * <pre>
 *  (공통/상속) 게시판 복합키 Entity. (postNo + contentType)
 * </pre>
 *
 * @author nichefish
 */
@Getter
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

    /* ----- */

    /**
     * 생성자
     * @param postNo - 게시물의 고유 번호
     * @param type - 콘텐츠 유형을 나타내는 ContentType 객체
     */
    public BaseClsfKey(final Integer postNo, final ContentType type) {
        this.postNo = postNo;
        this.contentType = type.key;
    }
}
