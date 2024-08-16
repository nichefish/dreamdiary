package io.nicheblog.dreamdiary.web.repository.user;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.user.QUserEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.repository.user.querydsl.QUserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository
 * <pre>
 *  사용자 관리 > 사용자(계정) 관리 Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("userRepository")
public interface UserRepository
    extends BaseStreamRepository<UserEntity, Integer>, QUserRepository {

    /**
     * 사용자 ID로 검색
     */
    Optional<UserEntity> findByUserId(final String userId);
}