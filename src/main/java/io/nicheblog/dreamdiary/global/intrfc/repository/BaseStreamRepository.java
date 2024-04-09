package io.nicheblog.dreamdiary.global.intrfc.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * BaseStreamRepository
 * <pre>
 *  Stream 조회 처리용 Custom Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@NoRepositoryBean
public interface BaseStreamRepository<T, ID extends Serializable>
        extends BaseRepository<T, ID>, JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * stream 조회
     */
    Stream<T> streamAllBy(Specification<T> searchWith);

    /**
     * stream 조회 (+정렬)
     */
    Stream<T> streamAllBy(Specification<T> searchWith, Sort sort);
}