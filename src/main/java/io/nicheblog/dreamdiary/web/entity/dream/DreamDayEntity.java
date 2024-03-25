package io.nicheblog.dreamdiary.web.entity.dream;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAtchEntity;
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
@Table(name = "DREAM_DAY")
@SuperBuilder(toBuilder=true)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE DREAM_DAY SET DEL_YN = 'Y' WHERE DREAM_DAY_NO = ?")
public class DreamDayEntity
        extends BaseAtchEntity
        implements Serializable {

    /**
     * 꿈 일자 고유 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DREAM_DAY_NO")
    @Comment("꿈 일자 고유 번호")
    private Integer dreamDayNo;

    /**
     * 꿈 일자
     */
    @Column(name = "DREAMT_DT")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = DateUtils.PTN_DATE)
    @Comment("꿈 일자")
    private Date dreamtDt;

    /** 날짜미상여부 */
    @Builder.Default
    @Column(name = "DT_UNKNOWN_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("날짜미상여부")
    private String dtUnknownYn = "N";

    /** 년도 */
    @Column(name = "YY")
    @Comment("년도")
    private Integer yy;

    /** 월 */
    @Column(name = "MNTH")
    @Comment("월")
    private Integer mnth;

    /**
     * 대략일자 (날짜미상시 해당일자 이후에 표기)
     */
    @Column(name = "APRXMT_DT")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = DateUtils.PTN_DATE)
    @Comment("대략일자 (날짜미상시 해당일자 이후에 표기)")
    private Date aprxmtDt;
}
