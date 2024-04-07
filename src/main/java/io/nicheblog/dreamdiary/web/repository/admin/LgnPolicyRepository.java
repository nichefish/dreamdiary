package io.nicheblog.dreamdiary.web.repository.admin;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.admin.LgnPolicyEntity;
import org.springframework.stereotype.Repository;

/**
 * LgnPolicyRepository
 * <pre>
 *  로그인 정책 정보 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("lgnPolicyRepository")
public interface LgnPolicyRepository
        extends BaseStreamRepository<LgnPolicyEntity, Integer> {
    //
}