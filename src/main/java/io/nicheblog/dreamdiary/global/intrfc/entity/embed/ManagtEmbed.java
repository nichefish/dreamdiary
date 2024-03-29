package io.nicheblog.dreamdiary.global.intrfc.entity.embed;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.comment.CommentEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.comment.CommentMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;

import javax.persistence.OrderBy;
import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CommentEmbed
 * <pre>
 *  댓글 관련 정보 위임
 * </pre>
 *
 * @author nichefish
 */
@Embeddable
@Getter(AccessLevel.PUBLIC)
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagtEmbed {

    @PostLoad
    private void onLoad() {
        //
    }

    /** 수정권한 */
    @Builder.Default
    @Column(name = "mdfable")
    @Comment("수정권한")
    protected String mdfable = Constant.MDFABLE_REGSTR;

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