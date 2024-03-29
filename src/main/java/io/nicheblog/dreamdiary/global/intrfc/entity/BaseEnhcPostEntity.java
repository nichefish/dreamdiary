package io.nicheblog.dreamdiary.global.intrfc.entity;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * BaseEnhcPostEntity
 * <pre>
 *  (공통/상속) 향상된 게시판 속성 Entity
 *  "All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 *
 * @author nichefish
 * @implements BasePostEntity
 */
@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
public class BaseEnhcPostEntity
        extends BasePostEntity {

    /** 수정권한 */
    @Builder.Default
    @Column(name = "MDFABLE")
    @Comment("수정권한")
    protected String mdfable = Constant.MDFABLE_REGSTR;

    /** 게시물 열람자 목록 */
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

    /** 조치자(작업자)ID */
    @Column(name = "managtr_id", length = 20)
    private String managtrId;

    /** 조치자(작업자) 정보 */
    @ManyToOne
    @JoinColumn(name = "managtr_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    private AuditorInfo managtrInfo;

    /** 조치(작업)일시 */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = DateUtils.PTN_DATETIME)
    @Column(name = "managt_dt")
    private Date managtDt;

    /** 게시물 조치자 목록 */
    // @OneToMany(fetch = FetchType.EAGER)
    // @JoinColumnsOrFormulas({
    //         @JoinColumnOrFormula(column = @JoinColumn(name = "POST_NO", referencedColumnName = "POST_NO", insertable = false, updatable = false)),
    //         @JoinColumnOrFormula(column = @JoinColumn(name = "content_type", referencedColumnName = "content_type", insertable = false, updatable = false))
    // })
    // @Fetch(FetchMode.SELECT)
    // @OrderBy("regDt DESC")
    // @NotFound(action = NotFoundAction.IGNORE)
    // private List<BoardPostManagtrEntity> managtrList;

    /* ----- */

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
