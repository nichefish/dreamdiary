package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

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
    protected String contentType;

    /* ----- */

    /**
     * 복합키 객체 반환
     */
    public BaseClsfKey getClsfKey() {
        return new BaseClsfKey(this.postNo, this.contentType);
    }
}
