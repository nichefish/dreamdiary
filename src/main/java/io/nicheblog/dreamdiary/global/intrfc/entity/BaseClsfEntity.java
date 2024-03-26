package io.nicheblog.dreamdiary.global.intrfc.entity;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.web.entity.cmm.comment.CommentEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.persistence.OrderBy;
import java.util.List;
import java.util.TimeZone;

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

    /** 필수: 게시물 코드 */
    private static final String BOARD_CD = "";
    /** 필수: 글분류 코드 */
    private static final String CTGR_CL_CD = "";

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

    /**
     * 글분류 코드
     */
    @Column(name = "CTGR_CD", length = 20)
    @Comment("글분류 코드")
    private String ctgrCd;

    /**
     * 상단고정여부
     */
    @Builder.Default
    @Column(name = "FXD_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("상단고정여부")
    protected String fxdYn = "N";

    /**
     * 게시물 댓글 목록
     */
    // @OneToMany(fetch = FetchType.EAGER)
    // @JoinColumnsOrFormulas({
    //         @JoinColumnOrFormula(column = @JoinColumn(name = "POST_NO", referencedColumnName = "POST_NO", insertable = false, updatable = false)),
    //         @JoinColumnOrFormula(formula = @JoinFormula(value = BOARD_CD, referencedColumnName = "BOARD_CD"))
    // })
    // @Fetch(FetchMode.SELECT)
    // @OrderBy("regDt ASC")
    // @Comment(" 목록")
    // @NotFound(action = NotFoundAction.IGNORE)
    // private List<CommentEntity> commentList;

    /* ----- */

    /**
     * 복합키 객체 반환
     */
    protected BasePostKey getPostKey() {
        return new BasePostKey(this.postNo, this.boardCd);
    }

}
