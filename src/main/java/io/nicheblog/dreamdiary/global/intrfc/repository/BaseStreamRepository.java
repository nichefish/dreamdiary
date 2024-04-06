package io.nicheblog.dreamdiary.global.intrfc.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * CustomStreamRepository
 * <pre>
 *  Stream 조회 처리용 Custom Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("customStreamRepository")
public interface BaseStreamRepository<T, ID extends Serializable> {

    /**
     * stream 조회
     */
    Stream<T> streamAllBy(Specification<T> searchWith);
}