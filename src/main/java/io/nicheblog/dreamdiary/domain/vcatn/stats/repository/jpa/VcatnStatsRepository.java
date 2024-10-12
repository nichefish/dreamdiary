package io.nicheblog.dreamdiary.domain.vcatn.stats.repository.jpa;

import io.nicheblog.dreamdiary.domain.vcatn.stats.entity.VcatnStatsEntity;
import io.nicheblog.dreamdiary.domain.vcatn.stats.entity.VcatnStatsKey;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.stereotype.Repository;

/**
 * VcatnStatsRepository
 * <pre>
 *  일정관리 > 휴가 통합 관리 (JPA) Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("vcatnStatsRepository")
public interface VcatnStatsRepository
        extends BaseStreamRepository<VcatnStatsEntity, VcatnStatsKey> {
    //
}