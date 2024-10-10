package io.nicheblog.dreamdiary.domain._core.log.actvty.repository.jpa;

import io.nicheblog.dreamdiary.domain._core.log.actvty.entity.LogActvtyEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.stereotype.Repository;

/**
 * LogActvtyRepository
 * <pre>
 *  활동 로그 관리 (JPA) Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("logActvtyRepository")
public interface LogActvtyRepository
        extends BaseStreamRepository<LogActvtyEntity, Integer> {
    //
}
