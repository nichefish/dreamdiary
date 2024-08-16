package io.nicheblog.dreamdiary.web.repository.user.querydsl.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.nicheblog.dreamdiary.web.entity.user.QUserEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.repository.user.querydsl.QUserRepository;

import javax.annotation.Resource;
import java.util.List;

/**
 * QUserRepositoryImpl
 * <pre>
 *  사용자 관리 > 사용자(계정) 관리 (querydsl) Repository 인터페이스 구현체
 * </pre>
 *
 * @author nichefish
 */
public class QUserRepositoryImpl implements QUserRepository {

    @Resource
    private JPAQueryFactory jpaQueryFactory;

    @Override
    public List<UserEntity> findByUserTest() {
        QUserEntity user = QUserEntity.userEntity;
        return jpaQueryFactory.selectFrom(user)
                .where(user.userId.eq("nichefish"))
                .fetch();
    }
}