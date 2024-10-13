package io.nicheblog.dreamdiary.global._common.auth.repository.redis;

import io.nicheblog.dreamdiary.global._common.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RefreshTokenRepository
 * <pre>
 *  RefreshToken redis repository 인터페이스
 * </pre>
z *
 * @author nichefish
 */
@Repository("refreshTokenRepository")
public interface RefreshTokenRepository
        extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(final String token);

    Optional<RefreshToken> findByAuthId(final String authId);
}

