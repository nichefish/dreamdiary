package io.nicheblog.dreamdiary.cmm.intrfc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * BaseRepository
 * <pre>
 *  (공통/상속) entityManager.refresh 사용 위한 커스텀 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable>
        extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * entity refresh
     */
    void refresh(final T t);

    /**
     * entity refresh
     */
    void merge(final T t);
}
