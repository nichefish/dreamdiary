package io.nicheblog.dreamdiary.global.intrfc.entity;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.web.entity.cmm.comment.CommentEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.comment.CommentMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.util.CollectionUtils;

import javax.persistence.OrderBy;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BaseClsfEntity
 * <pre>
 *  (공통/상속) 태그/댓글 속성 Entity
 *  "All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
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
     * !상속받은 클래스에서 실제 매핑 구성 (@Column(name="content_type")
     */
    @Transient
    protected String boardCd;

    /**
     * 제목
     */
    @Column(name = "title")
    protected String title;

    /**
     * 내용
     */
    @Column(name = "cn")
    protected String cn;

    /**
     * 글분류 코드
     */
    @Column(name = "ctgr_cd", length = 20)
    @Comment("글분류 코드")
    protected String ctgrCd;

    /** 글분류 코드 정보 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'"+CTGR_CL_CD+"'", referencedColumnName = "cl_cd")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "ctgr_cd", referencedColumnName = "dtl_cd", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("공지사항 글분류 코드 정보")
    protected DtlCdEntity ctgrCdInfo;

    /**
     * 상단고정여부
     */
    @Builder.Default
    @Column(name = "fxd_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("상단고정여부")
    protected String fxdYn = "N";

    /**
     * 댓글 목록
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "ref_post_no", referencedColumnName = "post_no", insertable = false, updatable = false)),
            @JoinColumnOrFormula(column = @JoinColumn(name = "ref_content_type", referencedColumnName = "content_type", insertable = false, updatable = false)),
    })
    @Fetch(FetchMode.SELECT)
    @OrderBy("regDt ASC")
    @Comment("댓글 목록")
    @NotFound(action = NotFoundAction.IGNORE)
    protected List<CommentEntity> commentList;

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

    /**
     * 댓글 :: List<Entity> -> List<Dto> 반환
     */
    public List<CommentDto> getCommentDtoList() {
        if (CollectionUtils.isEmpty(this.commentList)) return null;
        return this.commentList.stream()
                .map(entity -> {
                    try {
                        return CommentMapstruct.INSTANCE.toDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
