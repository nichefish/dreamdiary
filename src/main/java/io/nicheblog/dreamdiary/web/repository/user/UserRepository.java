package io.nicheblog.dreamdiary.web.repository.user;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository
 * <pre>
 *  사용자 관리 > 사용자(계정) 관리 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("userRepository")
public interface UserRepository
        extends BaseStreamRepository<UserEntity, Integer> {

    /**
     * 사용자 ID로 검색
     */
    Optional<UserEntity> findByUserId(final String userId);
}