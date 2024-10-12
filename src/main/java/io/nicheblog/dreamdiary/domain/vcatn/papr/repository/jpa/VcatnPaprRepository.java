package io.nicheblog.dreamdiary.domain.vcatn.papr.repository.jpa;

import io.nicheblog.dreamdiary.domain.vcatn.papr.entity.VcatnPaprEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.QueryHint;

/**
 * VcatnPaprRepository
 * <pre>
 *  일정관리 > 휴가계획서 (JPA) Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("vcatnPaprRepository")
public interface VcatnPaprRepository
        extends BaseStreamRepository<VcatnPaprEntity, Integer> {

    /**
     * 존재하는 휴가계획서 중 최저년도 조회
     *
     * @return {@link String} -- 가장 오래된 휴가계획서의 등록년도 (문자열 형식)
     */
    @Transactional(readOnly = true)
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    @Query("SELECT function('date_format', min(t.regDt), '%Y') " +
            "FROM VcatnPaprEntity t")
    String selectMinYy();
}