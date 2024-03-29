package io.nicheblog.dreamdiary.global.intrfc.entity.embed;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.managt.ManagtrEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.managtr.ManagtrMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.managtr.ManagtrDto;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;

import javax.persistence.OrderBy;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * CommentEmbed
 * <pre>
 *  댓글 관련 정보 위임
 * </pre>
 *
 * @author nichefish
 */
@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagtEmbed {

    @PostLoad
    private void onLoad() {
        this.isManagtr = AuthUtils.isRegstr(this.managtrId);
        if (this.managtrInfo != null) this.managtrNm = this.managtrInfo.getNickNm();
    }

    /** 수정권한 */
    @Builder.Default
    @Column(name = "mdfable")
    @Comment("수정권한")
    private String mdfable = Constant.MDFABLE_REGSTR;

    /** 조치자(작업자)ID */
    @Column(name = "managtr_id", length = 20)
    private String managtrId;

    /** 조치자(작업자) 정보 */
    @ManyToOne
    @JoinColumn(name = "managtr_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    private AuditorInfo managtrInfo;

    @Transient
    private String managtrNm;

    /** 나=조치자 여부 */
    @Transient
    private Boolean isManagtr;

    /** 조치(작업)일시 */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = DateUtils.PTN_DATETIME)
    @Column(name = "managt_dt")
    private Date managtDt;

    /** 게시물 조치자 목록 */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "ref_post_no", referencedColumnName = "post_no", insertable = false, updatable = false)),
            @JoinColumnOrFormula(column = @JoinColumn(name = "ref_content_type", referencedColumnName = "content_type", insertable = false, updatable = false))
    })
    @Fetch(FetchMode.SELECT)
    @OrderBy("regDt DESC")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<ManagtrEntity> managtrList;

    /* ----- */

    /**
     * 조치자 :: List<Entity> -> List<Dto> 반환
     */
    public List<ManagtrDto> getManagtrDtoList() throws Exception {
        if (CollectionUtils.isEmpty(this.managtrList)) return null;
        List<ManagtrDto> dtoList = new ArrayList<>();
        for (ManagtrEntity entity : this.managtrList) {
            dtoList.add(ManagtrMapstruct.INSTANCE.toDto(entity));
        }
        return dtoList;
    }
}