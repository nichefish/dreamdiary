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
 * (BaseAtchEntity 상속)
 *
 * @author nichefish
 * @implements BaseAtchEntity
 */
@MappedSuperclass
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
    @Column(name = "POST_NO")
    protected Integer postNo;

    /**
     * 게시판분류코드
     */
    @Column(name = "BOARD_CD")
    protected String boardCd;

    /**
     * 제목
     */
    @Column(name = "TITLE")
    protected String title;

    /**
     * 내용
     */
    @Column(name = "POST_CN")
    protected String postCn;

    /**
     * 조회수
     */
    @Builder.Default
    @Column(name = "HIT_CNT")
    protected Integer hitCnt = 0;


    /* ----- */

    /**
     * 복합키 객체 반환
     */
    protected BasePostKey getPostKey() {
        return new BasePostKey(this.postNo, this.boardCd);
    }



}
