package io.nicheblog.dreamdiary.global.intrfc.entity;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.web.entity.cmm.comment.CommentEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.util.CollectionUtils;

import javax.persistence.OrderBy;
import javax.persistence.*;
import java.util.List;

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

    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = "DEFAULT_CTGR_CL_CD";

    /**
     * 글 번호 (POST_NO, PK)
     * !상속받은 클래스에서 실제 매핑 구성 (auto_increment 또는 테이블 생성 전략(for 복합키))
     */
    @Transient
    protected Integer postNo;

    /**
     * 게시판 분류 코드
     * !상속받은 클래스에서 실제 매핑 구성 (@Column(name="BOARD_CD")
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

    /** 글분류 코드 정보 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'"+CTGR_CL_CD+"'", referencedColumnName = "CL_CD")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "CTGR_CD", referencedColumnName = "DTL_CD", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("공지사항 글분류 코드 정보")
    private DtlCdEntity ctgrCdInfo;

    /**
     * 상단고정여부
     */
    @Builder.Default
    @Column(name = "FXD_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("상단고정여부")
    protected String fxdYn = "N";

    /**
     * 댓글 목록
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "REF_POST_NO", referencedColumnName = "POST_NO", insertable = false, updatable = false)),
            @JoinColumnOrFormula(column = @JoinColumn(name = "REF_BOARD_CD", referencedColumnName = "BOARD_CD", insertable = false, updatable = false)),
    })
    @Fetch(FetchMode.SELECT)
    @OrderBy("regDt ASC")
    @Comment("댓글 목록")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<CommentEntity> commentList;

    /* ----- */

    /**
     * 복합키 객체 반환
     */
    protected BasePostKey getPostKey() {
        return new BasePostKey(this.postNo, this.boardCd);
    }

    /**
     * 댓글 수
     */
    @Transient
    private Integer commentCnt;
    @PostLoad
    private void onLoad() {
        this.commentCnt = (CollectionUtils.isEmpty(this.commentList)) ? 0 : this.commentList.size();
    }
}
