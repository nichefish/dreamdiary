package io.nicheblog.dreamdiary.web.repository.vcatn.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.vcatn.papr.VcatnSchdulEntity;
import org.springframework.stereotype.Repository;

/**
 * VcatnSchdulRepository
 * <pre>
 *  일정관리 > 휴가 일정 상세 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("vcatnSchdulRepository")
public interface VcatnSchdulRepository
        extends BaseStreamRepository<VcatnSchdulEntity, Integer> {
    //
}