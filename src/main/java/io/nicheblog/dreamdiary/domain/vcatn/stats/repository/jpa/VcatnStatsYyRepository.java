package io.nicheblog.dreamdiary.domain.vcatn.stats.repository.jpa;

import io.nicheblog.dreamdiary.domain.vcatn.stats.entity.VcatnStatsYyEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.stereotype.Repository;

/**
 * VcatnStatsYyRepository
 * <pre>
 *  일정관리 > 휴가 통합 관리 년도 설정 (JPA) Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("vcatnStatsYyRepository")
public interface VcatnStatsYyRepository
        extends BaseStreamRepository<VcatnStatsYyEntity, String> {
    //
}