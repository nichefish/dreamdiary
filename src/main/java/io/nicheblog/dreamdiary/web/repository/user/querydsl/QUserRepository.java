package io.nicheblog.dreamdiary.web.repository.user.querydsl;

import io.nicheblog.dreamdiary.web.entity.user.UserEntity;

import java.util.List;

/**
 * QUserRepository
 * <pre>
 *  사용자 관리 > 사용자(계정) 관리 (querydsl) Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
public interface QUserRepository {
    List<UserEntity> findByUserTest();
}