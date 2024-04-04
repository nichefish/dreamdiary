package io.nicheblog.dreamdiary.global.auth.repository;

import io.nicheblog.dreamdiary.global.auth.entity.AuthRoleEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * AuthRoleRepository
 * <pre>
 *  권한 정보 repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("authRoleRepository")
public interface AuthRoleRepository
        extends BaseRepository<AuthRoleEntity, String> {
    //
}

