package io.nicheblog.dreamdiary.web.entity.board;

import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.Date;

/**
 * BoardPostSmpEntity
 * <pre>
 *  게시판 게시물 간소화 Entity
 *  (BoardPostEntity에서 태그 관련 정보 제거 = 연관관계 순환참조 방지 위함. 나머지는 동일)
 * </pre>
 *
 * @author nichefish
 * @extends BasePostEntity
 */
@Entity
@Table(name = "board_post")
@IdClass(BaseClsfKey.class)      // 분류코드+상세코드 복합키 적용
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE board_post SET del_yn = 'Y' WHERE content_type = ? AND post_no = ?")
public class BoardPostSmpEntity
        extends BasePostEntity {

    /**
     * 글 번호
     * (복합키 사용, 시퀀스 생성 로직을 위해 재정의)
     */
    @Id
    @TableGenerator(name = "board_post", table = "cmm_sequence", pkColumnName = "seq_nm", valueColumnName = "seq_val", pkColumnValue = "POST_NO", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "board_post")
    @Column(name = "post_no")
    @Comment("글번호 (key)")
    private Integer postNo;

    /**
     * 게시판 분류 코드
     */
    @Column(name = "content_type")
    protected String contentType;

    /**
     * 게시판 정의 정보
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "content_type", referencedColumnName = "board_cd", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("게시판 정의 정보")
    private BoardDefEntity boardDefInfo;

    /**
     * 노션 페이지 참조 ID :: UUID
     */
    // @Column(name = "NOTION_PAGE_ID")
    // @Comment("노션 페이지 참조 ID :: UUID")
    // private String notionPageId;

    /**
     * 게시물 열람자 목록
     */
    /*
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "POST_NO", referencedColumnName = "POST_NO", insertable = false, updatable = false)),
            @JoinColumnOrFormula(column = @JoinColumn(name = "content_type", referencedColumnName = "content_type", insertable = false, updatable = false))
    })
    @Fetch(FetchMode.SELECT)
    @OrderBy("regDt DESC")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<BoardPostViewerEntity> viewerList;
    */

    /**
     * 조치자(작업자)ID
     */
    @Column(name = "managtr_id", length = 20)
    private String managtrId;

    /**
     * 조치(작업)일시
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = DateUtils.PTN_DATETIME)
    @Column(name = "managt_dt")
    private Date managtDt;

    /**
     * 조치자(작업자) 정보
     */
    @ManyToOne
    @JoinColumn(name = "managtr_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    private AuditorInfo managtrInfo;

    /**
     * 게시물 조치자 목록
     */
    // @OneToMany(fetch = FetchType.EAGER)
    // @JoinColumnsOrFormulas({
    //         @JoinColumnOrFormula(column = @JoinColumn(name = "POST_NO", referencedColumnName = "POST_NO", insertable = false, updatable = false)),
    //         @JoinColumnOrFormula(column = @JoinColumn(name = "content_type", referencedColumnName = "content_type", insertable = false, updatable = false))
    // })
    // @Fetch(FetchMode.SELECT)
    // @OrderBy("regDt DESC")
    // @NotFound(action = NotFoundAction.IGNORE)
    // private List<BoardPostManagtrEntity> managtrList;

    /**
     * 게시물 태그 목록
     */
    /*
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "POST_NO", referencedColumnName = "POST_NO")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "content_type", referencedColumnName = "content_type"))
    })
    @Fetch(FetchMode.SELECT)
    @OrderBy("boardTag ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("게시물 태그 목록")
    private List<BoardPostTagEntity> tagList;
    */

    /**
     * 파일시스템 참조 목록
     *//*

    // @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    // @JoinColumnsOrFormulas({
    //         @JoinColumnOrFormula(column = @JoinColumn(name = "POST_NO", referencedColumnName = "POST_NO")),
    //         @JoinColumnOrFormula(column = @JoinColumn(name = "content_type", referencedColumnName = "content_type"))
    // })
    // @Fetch(FetchMode.SELECT)
    // @NotFound(action = NotFoundAction.IGNORE)
    // @Comment("파일시스템 참조 목록")
    // private List<FlsysRefEntity> flsysRefList;

    */

    /* ----- */

    /**
     * 서브엔티티 List 처리를 위한 세터
     * -> 한 번 Entity가 생성된 이후부터는 새 List를 할당하면 안 되고 계속 JPA 이력이 추적되어야 한다.
     *//*

    public void setTagList(final List<BoardPostTagEntity> tagList) {
        if (CollectionUtils.isEmpty(tagList)) return;
        if (this.tagList == null) {
            this.tagList = tagList;
        } else {
            this.tagList.clear();
            this.tagList.addAll(tagList);
        }
    }

    */
/**
 * 서브엔티티 List 처리를 위한 Setter (override)
 * 한 번 Entity가 생성된 이후부터는 new List를 할당하면 안 되고 계속 JPA 이력이 추적되어야 한다.
 *//*

    // public void setCommentList(final List<CommentEntity> commentList) {
    //     if (CollectionUtils.isEmpty(commentList)) return;
    //     if (this.commentList == null) {
    //         this.commentList = commentList;
    //     } else {
    //         this.commentList.clear();
    //         this.commentList.addAll(commentList);
    //     }
    // }

    */
}

