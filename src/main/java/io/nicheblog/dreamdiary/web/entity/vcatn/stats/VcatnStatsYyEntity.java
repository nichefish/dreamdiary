package io.nicheblog.dreamdiary.web.entity.vcatn.stats;

import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * VcatnStatsEntity
 * <pre>
 *  휴가계획서 통계 Entity
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "VCATN_STATS_YY")
@SuperBuilder(toBuilder=true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class VcatnStatsYyEntity {

    /**
     * 년도
     */
    @Id
    @Column(name = "STATS_YY")
    @Comment("년도")
    private String statsYy;

    /**
     * 시작일
     */
    @Column(name = "bgn_dt")
    @Comment("시작일")
    private Date bgnDt;

    /**
     * 종료일
     */
    @Column(name = "END_DT")
    @Comment("종료일")
    private Date endDt;

    /* ----- */

    /**
     * 생성자 = 년도 받아서 기본시작일/종료일(1월 1일~12월 31일) 세팅
     */
    public VcatnStatsYyEntity(final String yyStr) throws Exception {
        this.statsYy = yyStr;
        this.bgnDt = DateUtils.asDate(yyStr + "-01-01");
        this.endDt = DateUtils.asDate(yyStr + "-12-31");
    }
}
