package io.nicheblog.dreamdiary.web.repository.vcatn;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.vcatn.VcatnStatsYyEntity;
import org.springframework.stereotype.Repository;

/**
 * VcatnStatsYyRepository
 * <pre>
 *  일정관리 > 휴가 통합 관리 년도 설정 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("vcatnStatsYyRepository")
public interface VcatnStatsYyRepository
        extends BaseRepository<VcatnStatsYyEntity, String> {

    //
}