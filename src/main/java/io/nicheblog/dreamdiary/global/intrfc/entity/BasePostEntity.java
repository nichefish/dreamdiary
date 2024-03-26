package io.nicheblog.dreamdiary.global.intrfc.entity;

import io.nicheblog.dreamdiary.global.Constant;
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

    /**
     * 중요여부
     */
    @Builder.Default
    @Column(name = "IMPRTC_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("중요여부")
    private String imprtcYn = "N";

    /**
     * 수정권한
     */
    @Builder.Default
    @Column(name = "MDFABLE")
    @Comment("수정권한")
    private String mdfable = Constant.MDFABLE_REGSTR;
}
