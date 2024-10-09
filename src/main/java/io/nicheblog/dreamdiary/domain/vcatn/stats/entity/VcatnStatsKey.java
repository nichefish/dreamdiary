package io.nicheblog.dreamdiary.domain.vcatn.stats.entity;

import lombok.*;

import java.io.Serializable;

/**
 * VcatnStatsKey
 * <pre>
 *  휴가 통계 복합키. (statsYy + userId)
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VcatnStatsKey
        implements Serializable {

    /** 관리년도 */
    private String statsYy;

    /** 사용자 ID */
    private String userId;
}
