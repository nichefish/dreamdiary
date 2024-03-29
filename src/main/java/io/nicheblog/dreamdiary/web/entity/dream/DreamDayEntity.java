package io.nicheblog.dreamdiary.web.entity.dream;

import io.nicheblog.dreamdiary.api.dream.mapstruct.DreamPieceApiMapstruct;
import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfEntity;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.CommentEmbed;
import io.nicheblog.dreamdiary.web.mapstruct.dream.DreamPieceMapstruct;
import io.nicheblog.dreamdiary.web.model.dream.DreamPieceDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;

import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DreamDayEntity
 * <pre>
 *  꿈 일자 Entity.
 *  Entity that contains each distinct dream day.
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudEntity
 */
@Entity
@Table(name = "dream_day")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE dream_day SET del_yn = 'Y' WHERE post_no = ?")
public class DreamDayEntity
        extends BaseClsfEntity {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.DREAM_DAY;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 꿈 일자 고유 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    @Comment("꿈 일자 고유 번호")
    private Integer dreamDayNo;

    /** 컨텐츠 타입 */
    @Builder.Default
    @Column(name = "content_type")
    private String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 꿈 일자 */
    @Column(name = "dreamt_dt")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = DateUtils.PTN_DATE)
    @Comment("꿈 일자")
    private Date dreamtDt;

    /** 날짜미상여부 */
    @Builder.Default
    @Column(name = "dt_unknown_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("날짜미상여부")
    private String dtUnknownYn = "N";

    /** 년도 */
    @Column(name = "yy")
    @Comment("년도")
    private Integer yy;

    /** 월 */
    @Column(name = "mnth")
    @Comment("월")
    private Integer mnth;

    /** 대략일자 (날짜미상시 해당일자 이후에 표기) */
    @Column(name = "aprxmt_dt")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = DateUtils.PTN_DATE)
    @Comment("대략일자 (날짜미상시 해당일자 이후에 표기)")
    private Date aprxmtDt;

    /** 꿈 조각 목록 */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "dream_day_no", referencedColumnName = "post_no", insertable = false, updatable = false)
    @Fetch(FetchMode.SELECT)
    @OrderBy("idx ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("댓글 목록")
    private List<DreamPieceEntity> dreamPieceList;

    /** ----- */

    public List<DreamPieceDto> getDreamPieceDtoList() {
        if (CollectionUtils.isEmpty(this.dreamPieceList)) return null;
        return this.dreamPieceList.stream()
                .map(entity -> {
                    try {
                        return DreamPieceMapstruct.INSTANCE.toDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public List<DreamPieceDto> getDreamPieceApiDtoList() {
        if (CollectionUtils.isEmpty(this.dreamPieceList)) return null;
        return this.dreamPieceList.stream()
                .map(entity -> {
                    try {
                        return DreamPieceApiMapstruct.INSTANCE.toDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentEmbed comment;
}
