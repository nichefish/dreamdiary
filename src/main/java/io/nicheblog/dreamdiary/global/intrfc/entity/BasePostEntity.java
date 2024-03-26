package io.nicheblog.dreamdiary.global.intrfc.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

/**
 * BasePostEntity
 * <pre>
 *  (공통/상속) 게시판 속성 Entity
 *  "All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 * (BaseClsfEntity 상속)
 *
 * @author nichefish
 * @implements BaseClsfEntity
 */
@MappedSuperclass
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
public class BasePostEntity
        extends BaseClsfEntity {

    /**
     * 조회수
     */
    @Builder.Default
    @Column(name = "HIT_CNT")
    protected Integer hitCnt = 0;

}
