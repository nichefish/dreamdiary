package io.nicheblog.dreamdiary.cmm.intrfc.repository.impl;

import io.nicheblog.dreamdiary.cmm.intrfc.repository.BaseRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * BaseRepositoryImpl
 * <pre>
 *  (공통/상속) entityManager.refresh 사용 위한 커스텀 Repository 구현체
 * </pre>
 *
 * @author nichefish
 * @implements BaseRepository
 */
public class BaseRepositoryImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID>
        implements BaseRepository<T, ID> {

    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * constructor
     */
    public BaseRepositoryImpl(
            final JpaEntityInformation entityInformation,
            final EntityManager entityManager
    ) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    /**
     * entity refresh
     */
    @Override
    @Transactional
    public void refresh(final T t) {
        entityManager.refresh(t);
    }

    @Override
    public void merge(T t) {
        entityManager.merge(t);
    }
}