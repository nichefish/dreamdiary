package io.nicheblog.dreamdiary.web.repository.vcatn;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.vcatn.VcatnStatsEntity;
import io.nicheblog.dreamdiary.web.entity.vcatn.VcatnStatsKey;
import org.springframework.stereotype.Repository;

/**
 * VcatnStatsRepository
 * <pre>
 *  일정관리 > 휴가 통합 관리 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("vcatnStatsRepository")
public interface VcatnStatsRepository
        extends BaseRepository<VcatnStatsEntity, VcatnStatsKey> {
    //
}