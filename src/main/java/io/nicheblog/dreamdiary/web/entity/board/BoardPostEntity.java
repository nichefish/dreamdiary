package io.nicheblog.dreamdiary.web.entity.board;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostKey;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.comment.CommentEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * BoardPostEntity
 * <pre>
 *  게시판 게시물 Entity
 * </pre>
 *
 * @author nichefish
 * @extends BasePostEntity
 */
@Entity
@Table(name = "BOARD_POST")
@IdClass(BasePostKey.class)      // 분류코드+상세코드 복합키 적용
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE BOARD_POST SET DEL_YN = 'Y' WHERE BOARD_CD = ? AND POST_NO = ?")
public class BoardPostEntity
        extends BasePostEntity {

    /**
     * 글 번호
     * (복합키 사용, 시퀀스 생성 로직을 위해 재정의)
     */
    @Id
    @TableGenerator(name = "board_post", table = "CMM_SEQ", pkColumnName = "SEQ_NM", valueColumnName = "SEQ_VAL", pkColumnValue = "POST_NO", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "board_post")
    @Column(name = "POST_NO")
    @Comment("글번호 (key)")
    private Integer postNo;

    /**
     * 게시판분류코드
     */
    @Column(name = "BOARD_CD")
    protected String boardCd;

    /**
     * 게시판 정의 정보
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "BOARD_CD", referencedColumnName = "BOARD_CD", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("게시판 정의 정보")
    private BoardDefEntity boardDefInfo;

    /**
     * 글분류 코드
     */
    @Column(name = "CTGR_CD")
    @Comment("글분류 상세코드")
    private String ctgrCd;

    /**
     * 게시판 글분류 코드 정보 (복합키 조인)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'POST_CTGR_CD\'", referencedColumnName = "CL_CD")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "CTGR_CD", referencedColumnName = "DTL_CD", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("게시판 글분류 코드 정보")
    private DtlCdEntity ctgrCdInfo;

    /**
     * 상단고정여부
     */
    @Builder.Default
    @Column(name = "FXD_YN")
    @Comment("상단고정여부")
    private String fxdYn = "N";

    /**
     * 수정권한
     */
    @Builder.Default
    @Column(name = "MDFABLE")
    @Comment("수정권한")
    private String mdfable = Constant.MDFABLE_REGSTR;

    /**
     * 노션 페이지 참조 ID :: UUID
     */
    // @Column(name = "NOTION_PAGE_ID")
    // @Comment("노션 페이지 참조 ID :: UUID")
    // private String notionPageId;

    /**
     * 게시물 댓글 목록
     */
    // @OneToMany(fetch = FetchType.EAGER)
    // @JoinColumnsOrFormulas({
    //         @JoinColumnOrFormula(column = @JoinColumn(name = "POST_NO", referencedColumnName = "POST_NO", insertable = false, updatable = false)),
    //         @JoinColumnOrFormula(column = @JoinColumn(name = "BOARD_CD", referencedColumnName = "BOARD_CD", insertable = false, updatable = false))
    // })
    // @Fetch(FetchMode.SELECT)
    // @OrderBy("regDt ASC")
    // @NotFound(action = NotFoundAction.IGNORE)
    // private List<CommentEntity> commentList;

    /**
     * 게시물 열람자 목록
     */
    /*
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "POST_NO", referencedColumnName = "POST_NO", insertable = false, updatable = false)),
            @JoinColumnOrFormula(column = @JoinColumn(name = "BOARD_CD", referencedColumnName = "BOARD_CD", insertable = false, updatable = false))
    })
    @Fetch(FetchMode.SELECT)
    @OrderBy("regDt DESC")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<BoardPostViewerEntity> viewerList;
    */

    /**
     * 조치자(작업자)ID
     */
    @Column(name = "MANAGTR_ID", length = 20)
    private String managtrId;

    /**
     * 조치(작업)일시
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = DateUtils.PTN_DATETIME)
    @Column(name = "MANAGT_DT")
    private Date managtDt;

    /**
     * 조치자(작업자) 정보
     */
    @ManyToOne
    @JoinColumn(name = "MANAGTR_ID", referencedColumnName = "USER_ID", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    private AuditorInfo managtrInfo;

    /**
     * 게시물 조치자 목록
     */
    // @OneToMany(fetch = FetchType.EAGER)
    // @JoinColumnsOrFormulas({
    //         @JoinColumnOrFormula(column = @JoinColumn(name = "POST_NO", referencedColumnName = "POST_NO", insertable = false, updatable = false)),
    //         @JoinColumnOrFormula(column = @JoinColumn(name = "BOARD_CD", referencedColumnName = "BOARD_CD", insertable = false, updatable = false))
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
            @JoinColumnOrFormula(column = @JoinColumn(name = "BOARD_CD", referencedColumnName = "BOARD_CD"))
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
    //         @JoinColumnOrFormula(column = @JoinColumn(name = "BOARD_CD", referencedColumnName = "BOARD_CD"))
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
/**
     * 댓글 :: List<Entity> -> List<Dto> 반환
     *//*

    // public List<CommentDto> getCommentDtoList() throws Exception {
    //     if (CollectionUtils.isEmpty(this.commentList)) return null;
    //     List<CommentDto> dtoList = new ArrayList<>();
    //     for (CommentEntity entity : this.commentList) {
    //         dtoList.add(CommentMapstruct.INSTANCE.toDto(entity));
    //     }
    //     return dtoList;
    // }

    */
/**
     * 열람자 :: List<Entity> -> List<Dto> 반환
     *//*

    // public List<BoardPostViewerDto> getViewerDtoList() throws Exception {
    //     if (CollectionUtils.isEmpty(this.viewerList)) return null;
    //     List<BoardPostViewerDto> dtoList = new ArrayList<>();
    //     for (BoardPostViewerEntity entity : this.viewerList) {
    //         dtoList.add(BoardPostViewerMapstruct.INSTANCE.toDto(entity));
    //     }
    //     return dtoList;
    // }

    */
    /**
     * 조치자 :: List<Entity> -> List<Dto> 반환
     */
    // public List<BoardPostManagtrDto> getManagtrDtoList() throws Exception {
    //     if (CollectionUtils.isEmpty(this.managtrList)) return null;
    //     List<BoardPostManagtrDto> dtoList = new ArrayList<>();
    //     for (BoardPostManagtrEntity entity : this.managtrList) {
    //         dtoList.add(BoardPostManagtrMapstruct.INSTANCE.toDto(entity));
    //     }
    //     return dtoList;
    // }
}

