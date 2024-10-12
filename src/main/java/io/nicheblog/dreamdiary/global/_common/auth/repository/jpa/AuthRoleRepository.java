package io.nicheblog.dreamdiary.global._common.auth.repository.jpa;

import io.nicheblog.dreamdiary.global._common.auth.entity.AuthRoleEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.stereotype.Repository;

/**
 * AuthRoleRepository
 * <pre>
 *  권한 정보 repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("authRoleRepository")
public interface AuthRoleRepository
        extends BaseStreamRepository<AuthRoleEntity, String> {
    //
}

