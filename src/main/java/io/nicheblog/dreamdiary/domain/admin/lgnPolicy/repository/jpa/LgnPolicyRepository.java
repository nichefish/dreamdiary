package io.nicheblog.dreamdiary.domain.admin.lgnPolicy.repository.jpa;

import io.nicheblog.dreamdiary.domain.admin.lgnPolicy.entity.LgnPolicyEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.stereotype.Repository;

/**
 * LgnPolicyRepository
 * <pre>
 *  로그인 정책 정보 (JPA) Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("lgnPolicyRepository")
public interface LgnPolicyRepository
        extends BaseStreamRepository<LgnPolicyEntity, Integer> {
    //
}