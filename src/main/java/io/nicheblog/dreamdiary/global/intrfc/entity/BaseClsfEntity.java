package io.nicheblog.dreamdiary.global.intrfc.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * BaseClsfEntity
 * <pre>
 *  (공통/상속) 태그/댓글 속성 Entity
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
public class BaseClsfEntity
        extends BaseAtchEntity {

    /**
     * 글 번호 (POST_NO, PK)
     * !상속받은 클래스에서 실제 매핑 구성 (auto_increment 또는 테이블 생성 전략(for 복합키))
     */
    @Transient
    protected Integer postNo;

    /**
     * 게시판 분류 코드
     * !상속받은 클래스에서 실제 매핑 구성 (@Column(name="BOARD_CD") 또는 private static final String = BOARD_CD)
     */
    @Transient
    protected String boardCd;

    /**
     * 제목
     */
    @Column(name = "TITLE")
    protected String title;

    /**
     * 내용
     */
    @Column(name = "CN")
    protected String cn;

    /* ----- */

    /**
     * 복합키 객체 반환
     */
    protected BasePostKey getPostKey() {
        return new BasePostKey(this.postNo, this.boardCd);
    }

}
