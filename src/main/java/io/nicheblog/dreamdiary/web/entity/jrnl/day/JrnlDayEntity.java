package io.nicheblog.dreamdiary.web.entity.jrnl.day;

import io.nicheblog.dreamdiary.api.jrnl.dream.mapstruct.JrnlDreamApiMapstruct;
import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.CommentEmbed;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.CommentEmbedModule;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.TagEmbed;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.TagEmbedModule;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamEntity;
import io.nicheblog.dreamdiary.web.mapstruct.jrnl.dream.JrnlDreamMapstruct;
import io.nicheblog.dreamdiary.web.model.jrnl.dream.JrnlDreamDto;
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
 * JrnlDayEntity
 * <pre>
 *  저널 일자 Entity.
 *  Single Day that contains each distinct dream/diary.
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudEntity
 */
@Entity
@Table(name = "jrnl_day")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE jrnl_day SET del_yn = 'Y' WHERE post_no = ?")
public class JrnlDayEntity
        extends BaseClsfEntity
        implements CommentEmbedModule, TagEmbedModule {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.JRNL_DAY;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 저널 일자 고유 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    @Comment("저널 일자 고유 번호 (PK)")
    private Integer postNo;

    /** 컨텐츠 타입 */
    @Builder.Default
    @Column(name = "content_type", columnDefinition = "VARCHAR(50) DEFAULT 'JRNL_DAY'")
    @Comment("컨텐츠 타입")
    private String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 저널 일자 */
    @Column(name = "jrnl_dt")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = DateUtils.PTN_DATE)
    @Comment("저널 일자")
    private Date jrnlDt;

    /** 날짜미상 여부 (Y/N) */
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
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = DateUtils.PTN_DATE)
    @Comment("대략일자 (날짜미상시 해당일자 이후에 표기)")
    private Date aprxmtDt;

    /** 날씨 */
    @Column(name = "weather")
    @Comment("날씨")
    private String weather;

    /** 저널 일기 목록 */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "jrnl_day_no", referencedColumnName = "post_no", insertable = false, updatable = false)
    @Fetch(FetchMode.SELECT)
    @OrderBy("idx ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("댓글 목록")
    private List<JrnlDiaryEntity> jrnlDiaryList;

    /** 저널 꿈 목록 */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "jrnl_day_no", referencedColumnName = "post_no", insertable = false, updatable = false)
    @Fetch(FetchMode.SELECT)
    @Where(clause = "else_dream_yn = 'N'")
    @OrderBy("idx ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("댓글 목록")
    private List<JrnlDreamEntity> jrnlDreamList;

    /** 저널 꿈 (타인) 목록 */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "jrnl_day_no", referencedColumnName = "post_no", insertable = false, updatable = false)
    @Fetch(FetchMode.SELECT)
    @Where(clause = "else_dream_yn = 'Y'")
    @OrderBy("idx ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("댓글 목록")
    private List<JrnlDreamEntity> jrnlElseDreamList;


    /* ----- */

    public List<JrnlDreamDto> getJrnlDreamDtoList() {
        if (CollectionUtils.isEmpty(this.jrnlDreamList)) return null;
        return this.jrnlDreamList.stream()
                .map(entity -> {
                    try {
                        return JrnlDreamMapstruct.INSTANCE.toDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException("객체 변환 중 에러가 발생했습니다.", e);
                    }
                })
                .collect(Collectors.toList());
    }

    public List<JrnlDreamDto> getJrnlDreamApiDtoList() {
        if (CollectionUtils.isEmpty(this.jrnlDreamList)) return null;
        return this.jrnlDreamList.stream()
                .map(entity -> {
                    try {
                        return JrnlDreamApiMapstruct.INSTANCE.toDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException("객체 변환 중 에러가 발생했습니다.", e);
                    }
                })
                .collect(Collectors.toList());
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentEmbed comment;

    /** 태그 정보 모듈 (위임) */
    @Embedded
    public TagEmbed tag;
}
