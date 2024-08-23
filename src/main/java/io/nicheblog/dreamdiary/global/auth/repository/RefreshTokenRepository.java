package io.nicheblog.dreamdiary.global.auth.repository;

import io.nicheblog.dreamdiary.global.auth.entity.RefreshToken;
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

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByAuthId(String authId);
}

