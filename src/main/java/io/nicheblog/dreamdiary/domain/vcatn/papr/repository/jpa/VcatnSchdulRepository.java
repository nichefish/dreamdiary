package io.nicheblog.dreamdiary.domain.vcatn.papr.repository.jpa;

import io.nicheblog.dreamdiary.domain.vcatn.papr.entity.VcatnSchdulEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.stereotype.Repository;

/**
 * VcatnSchdulRepository
 * <pre>
 *  일정관리 > 휴가 일정 상세 (JPA) Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("vcatnSchdulRepository")
public interface VcatnSchdulRepository
        extends BaseStreamRepository<VcatnSchdulEntity, Integer> {
    //
}