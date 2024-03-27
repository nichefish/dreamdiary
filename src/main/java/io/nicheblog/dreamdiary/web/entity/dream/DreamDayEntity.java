package io.nicheblog.dreamdiary.web.entity.dream;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAtchEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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
@SQLDelete(sql = "UPDATE DREAM_DAY SET DEL_YN = 'Y' WHERE DREAM_DAY_NO = ?")
public class DreamDayEntity
        extends BaseClsfEntity {

    private static final String CONTENT_TYPE = ContentType.DREAM_DAY.key;
    private static final String CTGR_CL_CD = "DREAM_DAY_CTGR_CD";

    /**
     * 꿈 일자 고유 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    @Comment("꿈 일자 고유 번호")
    private Integer dreamDayNo;

    /**
     * 게시판 분류 코드
     */
    @Builder.Default
    @Column(name = "content_type")
    private String contentType = CONTENT_TYPE;

    /**
     * 꿈 일자
     */
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

    /**
     * 대략일자 (날짜미상시 해당일자 이후에 표기)
     */
    @Column(name = "aprxmt_dt")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = DateUtils.PTN_DATE)
    @Comment("대략일자 (날짜미상시 해당일자 이후에 표기)")
    private Date aprxmtDt;
}
