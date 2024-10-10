package io.nicheblog.dreamdiary.domain.vcatn.stats.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

/**
 * VcatnStatsEntity
 * <pre>
 *  휴가계획서 통계 Entity
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "vcatn_stats")
@IdClass(VcatnStatsKey.class)      // 분류 코드+상세 코드 복합키 적용
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString(callSuper = true)
public class VcatnStatsEntity {

    /** 관리년도 */
    @Id
    @Column(name = "stats_yy")
    @Comment("관리년도")
    private String statsYy;

    /** 사용자 ID */
    @Id
    @Column(name = "user_id")
    @Comment("사용자 ID")
    private String userId;

    /** 근속년수 */
    @Column(name = "cnwk_yy")
    @Comment("근속년수")
    private Integer cnwkYy;

    /** 기본연차 */
    @Column(name = "bs_yryc")
    @Comment("기본연차")
    private Integer bsYryc;

    /** 근속 추가연차 */
    @Column(name = "cnwk_yryc")
    @Comment("근속 추가연차")
    private Integer cnwkYryc;

    /** 프로젝트 추가연차 */
    @Column(name = "prjct_yryc")
    @Comment("프로젝트 추가연차")
    private Integer prjctYryc;

    /** 안식년차 */
    @Column(name = "refresh_yryc")
    @Comment("안식년차")
    private Integer refreshYryc;
}
