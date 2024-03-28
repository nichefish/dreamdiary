package io.nicheblog.dreamdiary.web.repository.vcatn;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.vcatn.VcatnPaprEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * VcatnPaprRepository
 * <pre>
 *  일정관리 > 휴가계획서 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("vcatnPaprRepository")
public interface VcatnPaprRepository
        extends BaseRepository<VcatnPaprEntity, Integer> {

    /**
     * 존재하는 휴가계획서 중 최저년도 조회
     */
    @Query(
            value = "SELECT function('date_format', min(t.regDt), '%Y') FROM VcatnPaprEntity t"
    )
    String selectMinYy();
}