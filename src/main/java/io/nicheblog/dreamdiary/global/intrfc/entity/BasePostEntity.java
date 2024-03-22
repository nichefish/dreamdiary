package io.nicheblog.dreamdiary.global.intrfc.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * BasePostEntity
 * <pre>
 *  (공통/상속) 게시판 속성 Entity
 *  "All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 * (BaseAtchEntity 상속)
 *
 * @author nichefish
 * @implements BaseAtchEntity
 */
@MappedSuperclass
@IdClass(BasePostKey.class)      // 분류코드+상세코드 복합키 적용
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
public class BasePostEntity
        extends BaseAtchEntity {

    /**
     * 글 번호
     */
    @Id
    @TableGenerator(name = "board_post", table = "CMM_SEQ", pkColumnName = "SEQ_NM", valueColumnName = "SEQ_VAL", pkColumnValue = "POST_NO", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "board_post")
    @Column(name = "POST_NO")
    protected Integer postNo;

    /**
     * 게시판분류코드
     */
    @Id
    @Column(name = "BOARD_CD")
    protected String boardCd;

    /**
     * 제목
     */
    @Column(name = "POST_SJ")
    protected String postSj;

    /**
     * 내용
     */
    @Column(name = "POST_CN")
    protected String postCn;

    /**
     * 조회수
     */
    @Builder.Default
    @Column(name = "POST_HIT")
    protected Integer postHit = 0;

    /* ----- */

    /**
     * 복합키 객체 반환
     */
    protected BasePostKey getPostKey() {
        return new BasePostKey(this.postNo, this.boardCd);
    }



}
